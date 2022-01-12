package com.one2team.codeviz.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.one2team.codeviz.AnalyzerPlugin;
import com.one2team.codeviz.Graph;
import com.one2team.codeviz.Node;
import com.one2team.codeviz.plugins.InheritanceAnalyzerPlugin.Context;
import com.one2team.codeviz.config.InheritanceAnalyzerPluginConfig;
import lombok.Getter;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Stream.concat;

public class InheritanceAnalyzerPlugin extends AnalyzerPlugin<InheritanceAnalyzerPluginConfig, Context> {

  static class Context {

    @Getter
    private final Function<CompilationUnit, Predicate<ClassOrInterfaceType>> includePatterns;

    private final Map<String, TypeNode> typeNodes;

    private Context (InheritanceAnalyzerPluginConfig config) {
      List<Pattern> includePatterns = config.getIncludePatterns ().stream ()
        .map (Pattern::compile)
        .toList ();

      this.includePatterns = compilationUnit ->
        type -> getFullyQualifiedTypeName (compilationUnit, type)
          .map (typeName -> includePatterns.stream ()
            .map (pattern -> pattern.matcher (typeName))
            .anyMatch (Matcher::find))
          .orElse (false);

      this.typeNodes = new HashMap<> ();
    }

    private TypeNode getTypeNode (String typeName) {
      return typeNodes.computeIfAbsent (typeName, n -> new TypeNode ());
    }

    private void addSubType (String superType, Node subType) {
      getTypeNode (superType).addSubType (getTypeNode (subType.getName ()));
    }
  }

  private static class TypeNode {

    private final List<TypeNode> subTypes;

    private TypeNode () {
      subTypes = new ArrayList<> ();
    }

    public void addSubType (TypeNode subType) {
      subTypes.add (subType);
    }

    public int getRecursiveSubTypes () {
      return subTypes.size () + subTypes.stream ()
        .mapToInt (TypeNode::getRecursiveSubTypes)
        .sum ();
    }
  }

  @Override
  public Context newContext (InheritanceAnalyzerPluginConfig config) {
    return new Context (config);
  }

  @Override
  public void collect (InheritanceAnalyzerPluginConfig config, Context context, CompilationUnit unit, Node node) {
    Predicate<ClassOrInterfaceType> includeFilter = context.getIncludePatterns ().apply (unit);
    unit.accept (new VoidVisitorAdapter<Void> () {
      @Override
      public void visit (ClassOrInterfaceDeclaration declaration, Void arg) {
        super.visit (declaration, arg);
        concat (declaration.getExtendedTypes ().stream (), declaration.getImplementedTypes ().stream ())
          .filter (includeFilter)
          .flatMap (type -> getFullyQualifiedTypeName (unit, type).stream ())
          .forEach (superType -> context.addSubType (superType, node));
      }
    }, null);
  }

  @Override
  public void collect (InheritanceAnalyzerPluginConfig config, Context context, Graph graph) {
    graph.getNodes ().values ().forEach (node ->
      node.getMetrics ().put ("subtypes", new AtomicLong (ofNullable (context.getTypeNode (node.getName ()))
        .map (TypeNode::getRecursiveSubTypes)
        .map (Long::valueOf)
        .orElse (0L))));
  }

  private static Optional<String> getFullyQualifiedTypeName (CompilationUnit unit, ClassOrInterfaceType type) {
    String suffix = "." + type.getNameWithScope ();
    return unit.getImports ().stream ()
      .filter (not (ImportDeclaration::isStatic))
      .filter (not (ImportDeclaration::isAsterisk))
      .map (NodeWithName::getNameAsString)
      .filter (i -> i.endsWith (suffix))
      .findFirst ();
  }
}
