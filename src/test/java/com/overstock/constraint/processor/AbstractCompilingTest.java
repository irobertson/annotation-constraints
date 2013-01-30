package com.overstock.constraint.processor;

import static org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.tools.ToolProvider;

import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
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

  @Parameters
  public static Collection<Object[]> compilers() throws IOException {
    CompilerProvider javacProvider = new CompilerProvider() {
      public Compiler provide() throws IOException {
        return new Compiler(ToolProvider.getSystemJavaCompiler(), Compiler.Options());
      }
    };
    CompilerProvider eclipseProvider = new CompilerProvider() {
      public Compiler provide() throws IOException {
        return new Compiler(new EclipseCompiler(), Compiler.Options());
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
