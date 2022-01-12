package com.one2team.codeviz;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.CompilationUnit.Storage;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.one2team.codeviz.config.ConfigLoader;
import com.one2team.codeviz.config.RendererConfig;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

public class Analyzer {

  @Inject
  private ConfigLoader configLoader;

  @Inject
  private JavaParser parser;

  @Inject
  private MetricManagerFactory metricManagerFactory;

  @Inject
  private Map<Class<? extends RendererConfig>, Renderer<?>> renderers;

  public void run (String... args) throws IOException {
    var config = configLoader.loadConfig (Paths.get (args[0]));
    var src = Paths.get (config.getSrc ());
    MetricManager metricManager = metricManagerFactory.create (config);

    List<Pattern> fileIncludePatterns = config.getFileIncludePatterns ().stream ()
      .map (Pattern::compile)
      .toList ();

    Predicate<Path> includePattern = path -> fileIncludePatterns.stream ()
      .map (pattern -> pattern.matcher (path.toString ()))
      .anyMatch (Matcher::find);

    List<Pattern> importIncludePatterns = config.getImportIncludePatterns ().stream ()
      .map (Pattern::compile)
      .toList ();

    Predicate<String> importIncludePattern = importDeclaration -> importIncludePatterns.stream ()
      .map (pattern -> pattern.matcher (importDeclaration))
      .anyMatch (Matcher::find);

    List<Pattern> fileExcludePatterns = config.getFileExcludePatterns ().stream ()
      .map (Pattern::compile)
      .toList ();

    Predicate<Path> excludePattern = path -> fileExcludePatterns.stream ()
      .map (pattern -> pattern.matcher (path.toString ()))
      .allMatch (not (Matcher::find));

    @SuppressWarnings ("all")
    Predicate<Path> javaExtensionFilter = path ->
      com.google.common.io.Files.getFileExtension (path.toString ())
        .equalsIgnoreCase ("java");

    Map<String, Node> nodeMap = new HashMap<> ();
    Files.walk (src)
      .filter (Files::isRegularFile)
      .filter (javaExtensionFilter)
      .filter (excludePattern)
      .filter (includePattern)
      .map (path -> {
        try {
          return parser.parse (path);
        } catch (ParseProblemException e) {
          System.err.printf ("could not parse file %s%n", path);
          return null;
        }
      })
      .filter (Objects::nonNull)
      .forEach (unit -> unit.accept (new VoidVisitorAdapter<Void> () {
        @Override
        public void visit (ClassOrInterfaceDeclaration declaration, Void arg) {
          declaration.getFullyQualifiedName ().ifPresent (name -> {
            var node = nodeMap.computeIfAbsent (name, Node::new);
            node.setDependencies (unit.getImports ().stream ()
              .filter (not (ImportDeclaration::isAsterisk))
              .map (i -> i.isStatic () ?
                i.getName ().getQualifier ().map (Name::asString).orElse (null) :
                i.getNameAsString ())
              .filter (Objects::nonNull)
              .filter (importIncludePattern)
              .collect (Collectors.toSet ()));

            metricManager.collect (unit, node);
          });
        }
      }, null));

    var graph = new Graph (nodeMap.values ().stream ()
      .peek (node -> ofNullable (node.getDependencies ())
        .map (dependencies -> dependencies.stream ()
          .filter (nodeMap::containsKey)
          .collect (Collectors.toSet ()))
        .ifPresent (node::setDependencies))
      .collect (Collectors.toMap (Node::getName, Function.identity ())));

    metricManager.collect (graph);
    System.out.printf ("processed %d units%n", graph.getNodes ().size ());
    ofNullable (config.getRenderers ()).ifPresent (rendererConfigs ->
      rendererConfigs.forEach (rendererConfig -> render (rendererConfig, graph)));
  }

  @SuppressWarnings ({ "unchecked", "rawtypes" })
  private void render (RendererConfig config, Graph graph) {
    Renderer renderer = renderers.get (config.getClass ());
    renderer.render (config, graph);
  }
}
