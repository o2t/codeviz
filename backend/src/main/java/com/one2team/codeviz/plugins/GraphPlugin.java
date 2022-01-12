package com.one2team.codeviz.plugins;

import javax.annotation.Priority;
import javax.inject.Inject;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.inject.assistedinject.Assisted;
import com.one2team.codeviz.Graph;
import com.one2team.codeviz.Node;
import com.one2team.codeviz.Plugin;
import com.one2team.codeviz.PluginContext;
import com.one2team.codeviz.config.PluginConfig;
import com.one2team.codeviz.utils.PatternFilters;

import static com.one2team.codeviz.plugins.Priorities.HIGHEST;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;

@Priority (HIGHEST)
public class GraphPlugin extends Plugin<GraphPlugin.Config> {

  public static class Config extends PluginConfig {

  }

  private final Predicate<String> importIncludePatterns;

  @Inject
  GraphPlugin (PatternFilters patternFilters, @Assisted PluginContext context) {
    importIncludePatterns = patternFilters.createInclusionFilter (
      context.getConfig ().getImportIncludePatterns (), identity ());
  }

  @Override
  public void analyze (Config config, PluginContext context, CompilationUnit unit, Node node) {
    unit.accept (new VoidVisitorAdapter<Void> () {
      @Override
      public void visit (ClassOrInterfaceDeclaration declaration, Void arg) {
        declaration.getFullyQualifiedName ().ifPresent (name -> {
          var node = context.getNode (name);
          node.getDependencies ().set (unit.getImports ().stream ()
            .filter (not (ImportDeclaration::isAsterisk))
            .map (i -> i.isStatic () ?
              i.getName ().getQualifier ().map (Name::asString).orElse (null) :
              i.getNameAsString ())
            .filter (Objects::nonNull)
            .filter (importIncludePatterns)
            .collect (Collectors.toSet ()));
        });
      }
    }, null);
  }

  @Override
  public void analyze (Config config, PluginContext context) {
    Graph graph = new Graph (context.getNodes ().stream ()
      .peek (node -> ofNullable (node.getDependencies ().get ())
        .map (dependencies -> dependencies.stream ()
          .filter (context::hasNode)
          .collect (Collectors.toSet ()))
        .ifPresent (node.getDependencies ()::set))
      .collect (Collectors.toMap (Node::getName, identity ())));
    context.setAttribute (Graph.class, graph);

    System.out.printf ("processed %d units%n", graph.getNodes ().size ());
  }
}
