package com.one2team.codeviz;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

public class Analyzer {

  @Inject
  private ConfigLoader configLoader;

  @Inject
  private JavaParser parser;

  @Inject
  private Set<Renderer> renderers;

  @Inject
  private Set<GraphFilter> graphFilters;

  public void run (String... args) throws IOException {
    var config = configLoader.loadConfig (Paths.get (args[0]));
    var src = Paths.get (config.src ());

    Predicate<Path> includePattern = path ->
      ofNullable (config.fileIncludePatterns ())
        .map (patterns -> patterns.stream ()
          .map (Pattern::compile)
          .map (pattern -> pattern.matcher (path.toString ()))
          .anyMatch (Matcher::find))
        .orElse (true);

    Predicate<String> importIncludePattern = importDeclaration ->
      ofNullable (config.importIncludePatterns ())
        .map (patterns -> patterns.stream ()
          .map (Pattern::compile)
          .map (pattern -> pattern.matcher (importDeclaration))
          .anyMatch (Matcher::find))
        .orElse (true);

    Predicate<Path> excludePattern = path ->
      ofNullable (config.fileExcludePatterns ())
        .map (patterns -> patterns.stream ()
          .map (Pattern::compile)
          .map (pattern -> pattern.matcher (path.toString ()))
          .allMatch (not (Matcher::find)))
        .orElse (true);

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
            node.setSize (Optional.of (unit)
              .flatMap (CompilationUnit::getStorage)
              .map (Storage::getPath)
              .map (path -> {
                try {
                  return Files.size (path);
                } catch (IOException e) {
                  throw new UncheckedIOException (e);
                }
              })
              .orElse (0L));

            node.setDependencies (unit.getImports ().stream ()
              .filter (not (ImportDeclaration::isAsterisk))
              .map (i -> i.isStatic () ?
                i.getName ().getQualifier ().map (Name::asString).orElse (null) :
                i.getNameAsString ())
              .filter (Objects::nonNull)
              .filter (importIncludePattern)
              .collect (Collectors.toSet ()));
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

    for (GraphFilter filter : graphFilters)
      graph = filter.filter (config, graph);

    var fg = graph;
    System.out.printf ("processed %d units%n", graph.getNodes ().size ());
    renderers.forEach (renderer -> renderer.render (config, fg));
  }
}
