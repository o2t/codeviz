package com.one2team.codeviz;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Predicate;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.one2team.codeviz.config.ConfigLoader;
import com.one2team.codeviz.config.RendererConfig;
import com.one2team.codeviz.utils.PatternFilters;

import static java.util.Optional.ofNullable;

public class Analyzer {

  @Inject
  private ConfigLoader configLoader;

  @Inject
  private JavaParser parser;

  @Inject
  private PluginContext.Factory contextFactory;

  @Inject
  private PatternFilters patternFilters;

  public void run (String... args) throws IOException {
    var config = configLoader.loadConfig (Paths.get (args[0]));
    var src = Paths.get (config.getSrc ());

    PluginContext context = contextFactory.create (config);
    Predicate<Path> includePattern = patternFilters.createInclusionFilter (config.getFileIncludePatterns (), Path::toString);
    Predicate<Path> excludePattern = patternFilters.createExclusionFilter (config.getFileExcludePatterns (), Path::toString);

    @SuppressWarnings ("all")
    Predicate<Path> javaExtensionFilter = path ->
      com.google.common.io.Files.getFileExtension (path.toString ())
        .equalsIgnoreCase ("java");

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
          declaration.getFullyQualifiedName ().ifPresent (name ->
            context.getPluginsManager ().analyze (unit, context.getNode (name)));
        }
      }, null));

    context.getPluginsManager ().analyze (null);
    ofNullable (config.getRenderers ()).ifPresent (rendererConfigs ->
      rendererConfigs.forEach (rendererConfig -> render (context, rendererConfig, context.getAttribute (Graph.class))));
  }

  @SuppressWarnings ({ "unchecked", "rawtypes" })
  private void render (PluginContext context, RendererConfig config, Graph graph) {
    Renderer renderer = context.getRenderer (config.getClass ());
    renderer.render (config, graph);
  }
}
