package com.overstock.constraint.processor;

import java.io.IOException;

import javax.tools.JavaCompiler;

public class EclipseCompiler extends Compiler {

  public EclipseCompiler(Options options) throws IOException {
    super(options);
  }

  @Override
  protected JavaCompiler createCompiler() {
    return new org.eclipse.jdt.internal.compiler.tool.EclipseCompiler();
  }

  @Override
  public String toString() {
    return "EclipseCompiler";
  }
}
