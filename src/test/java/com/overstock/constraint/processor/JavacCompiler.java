package com.overstock.constraint.processor;

import java.io.IOException;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavacCompiler extends Compiler {

  public JavacCompiler(Options options) throws IOException {
    super(options);
  }

  @Override
  protected JavaCompiler createCompiler() {
    return ToolProvider.getSystemJavaCompiler();
  }

  @Override
  public String toString() {
    return "javac";
  }
}
