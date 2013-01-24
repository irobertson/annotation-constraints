package com.overstock.constraint.processor;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import com.google.common.collect.Lists;

public abstract class AbstractCompilingTest {

  private List<Compiler> compilers = Lists.newArrayList();

  protected Compiler makeCompiler(Compiler.Options options) throws IOException {
    Compiler compiler = new Compiler(options);
    compilers.add(compiler);
    return compiler;
  }

  protected Compiler compiler;

  @Before
  public void setUp() throws IOException {
    compiler = makeCompiler(Compiler.Options());
  }

  @After
  public void tearDown() {
    for(Compiler compiler: compilers) {
      compiler.cleanUp();
    }
  }
}
