package com.one2team.codeviz.plugins;

import javax.annotation.Priority;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.inject.assistedinject.Assisted;
import com.one2team.codeviz.Graph;
import com.one2team.codeviz.Node;
import com.one2team.codeviz.Plugin;
import com.one2team.codeviz.PluginContext;
import com.one2team.codeviz.config.PluginConfig;
import com.one2team.codeviz.plugins.InheritancePlugin.Config;
import lombok.Getter;
import lombok.Setter;

import static com.one2team.codeviz.plugins.Priorities.NORMAL;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Stream.concat;

@Priority (NORMAL)
public class InheritancePlugin extends Plugin<Config> {

  public static class Config extends PluginConfig {

    @Getter
    @Setter
    @JsonProperty ("include-patterns")
    private List<String> includePatterns = new ArrayList<> ();

  }

  @Getter
  private final Function<CompilationUnit, Predicate<ClassOrInterfaceType>> includePatterns;

  private final Map<String, TypeNode> typeNodes;

  @Inject
  InheritancePlugin (@Assisted Config config) {
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
  public void analyze1 (PluginContext context, Config config, CompilationUnit unit, Node node) {
    Predicate<ClassOrInterfaceType> includeFilter = includePatterns.apply (unit);
    unit.accept (new VoidVisitorAdapter<Void> () {
      @Override
      public void visit (ClassOrInterfaceDeclaration declaration, Void arg) {
        super.visit (declaration, arg);
        concat (declaration.getExtendedTypes ().stream (), declaration.getImplementedTypes ().stream ())
          .filter (includeFilter)
          .flatMap (type -> getFullyQualifiedTypeName (unit, type).stream ())
          .forEach (superType -> addSubType (superType, node));
      }
    }, null);
  }

  @Override
  public void analyze (PluginContext context, Config config) {
    context.getAttribute (Graph.class).getNodes ().values ().forEach (node ->
      node.getMetrics ().set ("subtypes", ofNullable (getTypeNode (node.getName ()))
        .map (TypeNode::getRecursiveSubTypes)
        .map (Long::valueOf)
        .orElse (0L)));
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
