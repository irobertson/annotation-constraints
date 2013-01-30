package com.overstock.constraint.processor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class AbstractCompilingTest {

  private final CompilerProvider provider;

  public AbstractCompilingTest(CompilerProvider provider) {
    this.provider = provider;
  }

  protected Compiler compiler;

  @Before
  public void setUp() throws IOException {
    compiler = provider.provide();
  }

  @After
  public void tearDown() {
    compiler.cleanUp();
  }

  @Parameterized.Parameters
  public static Collection<Object[]> compilers() throws IOException {
    //TODO merge JavacCompiler and EclipseCompiler into one class (Compiler) which takes a JavaCompiler constructor arg
    CompilerProvider javacProvider = new CompilerProvider() {
      public Compiler provide() throws IOException {
        return new JavacCompiler(Compiler.Options());
      }
    };
    CompilerProvider eclipseProvider = new CompilerProvider() {
      public Compiler provide() throws IOException {
        return new EclipseCompiler(Compiler.Options());
      }
    };
    return Arrays.asList(new Object[][]{
      { javacProvider },
      { eclipseProvider }
    });
  }

  protected interface CompilerProvider {
    Compiler provide() throws IOException;
  }
}
