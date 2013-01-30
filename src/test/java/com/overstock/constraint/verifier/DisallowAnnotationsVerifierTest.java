package com.overstock.constraint.verifier;

import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.tools.Diagnostic;

import org.junit.Test;

import com.overstock.constraint.DisallowMultipleAnnotations;
import com.overstock.constraint.DisallowUnconstrained;
import com.overstock.constraint.RequireNoArgConstructor;
import com.overstock.constraint.Unconstrained;
import com.overstock.constraint.processor.*;

public class DisallowAnnotationsVerifierTest extends AbstractConstraintProcessorTest {

  public DisallowAnnotationsVerifierTest(CompilerProvider provider) {
    super(provider);
  }

  @Test
  public void testDisallowUnconstrainedPass() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@DisallowUnconstrained public class Annotated {}"));
  }

  @Test
  public void testDisallowUnconstrainedFail() throws Exception {
    compile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@DisallowUnconstrained @Unconstrained public class Annotated { }"));

    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" + DisallowUnconstrained.class.getName()
        + " which is not allowed with @" + Unconstrained.class.getName(),
      className("Annotated"),
      DisallowUnconstrained.class);
    verifyNoMoreInteractions(messager);
  }

  @Test
  public void testDisallowMultipleAnnotationsPass() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@DisallowMultipleAnnotations public class Annotated {}"));
  }

  @Test
  public void testDisallowMultipleAnnotationsFail() throws Exception {
    compile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@DisallowMultipleAnnotations @Unconstrained @RequireNoArgConstructor public class Annotated { }"));

    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" + DisallowMultipleAnnotations.class.getName()
        + " which is not allowed with @" + Unconstrained.class.getName(),
      className("Annotated"),
      DisallowMultipleAnnotations.class);
    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" + DisallowMultipleAnnotations.class.getName()
        + " which is not allowed with @" + RequireNoArgConstructor.class.getName(),
      className("Annotated"),
      DisallowMultipleAnnotations.class);
    verifyNoMoreInteractions(messager);
  }

}
