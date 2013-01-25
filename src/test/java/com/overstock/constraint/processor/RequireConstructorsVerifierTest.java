package com.overstock.constraint.processor;

import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.tools.Diagnostic;

import org.junit.Test;

import com.overstock.constraint.RequireNoArgConstructor;
import com.overstock.constraint.RequireStringLongIntArrayConstructor;

public class RequireConstructorsVerifierTest extends AbstractConstraintProcessorTest {

  @Test
  public void testNoArgConstructorPass() throws Exception {
    SourceFile[] sourceFiles = { new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RequireNoArgConstructor public class Annotated {}") };

    assertCleanCompile(sourceFiles);
  }

  @Test
  public void testNoArgConstructorFail() throws Exception {
    SourceFile[] sourceFiles = { new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RequireNoArgConstructor public class Annotated { public Annotated(String s) {} }") };

    compile(sourceFiles);
    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" + RequireNoArgConstructor.class.getName()
        + " but does not have a constructor with no arguments",
      new ClassValue(className("Annotated")),
      RequireNoArgConstructor.class);
    verifyNoMoreInteractions(messager);
  }

  @Test
  public void testRequireStringLongIntArrayConstructorPass() throws Exception {
    SourceFile[] sourceFiles = { new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RequireStringLongIntArrayConstructor public class Annotated {",
      "  public Annotated(String s, long l, int[] array) {}",
      "}") };

    assertCleanCompile(sourceFiles);
  }

  @Test
  public void testRequireStringLongIntArrayConstructorFail() throws Exception {
    SourceFile[] sourceFiles = { new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RequireStringLongIntArrayConstructor public class Annotated { ",
      "  public Annotated(String s, long l, long[] longs) {}",
      "}") };

    compile(sourceFiles);
    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" + RequireStringLongIntArrayConstructor.class.getName()
        + " but does not have a constructor with arguments (java.lang.String, long, int[])",
      new ClassValue(className("Annotated")),
      RequireStringLongIntArrayConstructor.class);
    verifyNoMoreInteractions(messager);
  }

}
