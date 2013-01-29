package com.overstock.constraint.processor;

import org.junit.Test;

public class ConstraintProcessorTest extends AbstractConstraintProcessorTest {

  @Test
  public void testNoConstraints() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("SimpleAnnotated.java"),
      PACKAGE_DECLARATION,
      "@Unconstrained(someInt=3)",
      "public class SimpleAnnotated extends java.util.ArrayList {}"));
  }

}
