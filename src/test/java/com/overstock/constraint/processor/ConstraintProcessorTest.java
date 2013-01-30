package com.overstock.constraint.processor;

import org.junit.Test;

public class ConstraintProcessorTest extends AbstractConstraintProcessorTest {

  public ConstraintProcessorTest(CompilerProvider provider) {
    super(provider);
  }

  @Test
  public void testNoConstraints() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("SimpleAnnotated.java"),
      PACKAGE_DECLARATION,
      "@Unconstrained(someInt=3)",
      "public class SimpleAnnotated<T> extends java.util.ArrayList<T> { static final long serialVersionUID = 1; }"));
  }

}
