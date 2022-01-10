package com.one2team.codeviz;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class JavaParser {

  public CompilationUnit parse (Path path) {
    try {
      return StaticJavaParser.parse (path);
    } catch (IOException e) {
      throw new UncheckedIOException (e);
    }
  }
}
