package com.overstock.constraint.verifier;

import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.tools.Diagnostic;

import org.junit.Test;

import com.overstock.constraint.RequireNoArgConstructor;
import com.overstock.constraint.RequireStringLongIntArrayConstructor;
import com.overstock.constraint.processor.AbstractConstraintProcessorTest;
import com.overstock.constraint.processor.SourceFile;

public class RequireConstructorsVerifierTest extends AbstractConstraintProcessorTest {

  @Test
  public void testNoArgConstructorPass() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RequireNoArgConstructor public class Annotated {}"));
  }

  @Test
  public void testNoArgConstructorFail() throws Exception {
    compile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RequireNoArgConstructor public class Annotated { public Annotated(String s) {} }"));

    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" + RequireNoArgConstructor.class.getName()
        + " but does not have a constructor with no arguments",
      className("Annotated"),
      RequireNoArgConstructor.class);
    verifyNoMoreInteractions(messager);
  }

  @Test
  public void testRequireStringLongIntArrayConstructorPass() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RequireStringLongIntArrayConstructor public class Annotated {",
      "  public Annotated(String s, long l, int[] array) {}",
      "}"));
  }

  @Test
  public void testRequireStringLongIntArrayConstructorFail() throws Exception {
    compile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RequireStringLongIntArrayConstructor public class Annotated { ",
      "  public Annotated(String s, long l, long[] longs) {}",
      "}"));

    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" + RequireStringLongIntArrayConstructor.class.getName()
        + " but does not have a constructor with arguments (java.lang.String, long, int[])",
      className("Annotated"),
      RequireStringLongIntArrayConstructor.class);
    verifyNoMoreInteractions(messager);
  }

}
