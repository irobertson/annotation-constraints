package com.overstock.constraint.processor;

import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.tools.Diagnostic;

import org.junit.Test;

import com.overstock.constraint.RecommendMultipleAnnotations;
import com.overstock.constraint.RecommendUnconstrained;
import com.overstock.constraint.RequireMultipleAnnotations;
import com.overstock.constraint.RequireUnconstrained;
import com.overstock.constraint.Unconstrained;

public class CompanionAnnotationsVerifierTest extends AbstractConstraintProcessorTest {

  @Test
  public void testRequireUnconstrainedPass() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RequireUnconstrained @Unconstrained public class Annotated {}"));
  }

  @Test
  public void testRequireUnconstrainedFail() throws Exception {
    compile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RequireUnconstrained public class Annotated { }"));

    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" + RequireUnconstrained.class.getName()
        + " but not with @" + Unconstrained.class.getName(),
      new ClassValue(className("Annotated")),
      RequireUnconstrained.class);
    verifyNoMoreInteractions(messager);
  }

  @Test
  public void testRequireMultipleAnnotationsPass() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RequireMultipleAnnotations @Unconstrained @RequireNoArgConstructor public class Annotated {}"));
  }

  @Test
  public void testRequireMultipleAnnotationsFail() throws Exception {
    compile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RequireMultipleAnnotations @RequireNoArgConstructor public class Annotated { }"));

    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" + RequireMultipleAnnotations.class.getName()
        + " but not with @" + Unconstrained.class.getName(),
      new ClassValue(className("Annotated")),
      RequireMultipleAnnotations.class);
    verifyNoMoreInteractions(messager);
  }

  @Test
  public void testRecommendUnconstrainedPass() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RecommendUnconstrained @Unconstrained public class Annotated {}"));
  }

  @Test
  public void testRecommendUnconstrainedFail() throws Exception {
    compile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RecommendUnconstrained public class Annotated { }"));

    verifyPrintMessage(
      Diagnostic.Kind.WARNING,
      "Class " + className("Annotated") + " is annotated with @" + RecommendUnconstrained.class.getName()
        + " but not with @" + Unconstrained.class.getName(),
      new ClassValue(className("Annotated")),
      RecommendUnconstrained.class);
    verifyNoMoreInteractions(messager);
  }

  @Test
  public void testRecommendMultipleAnnotationsPass() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RecommendMultipleAnnotations @Unconstrained @RequireNoArgConstructor public class Annotated {}"));
  }

  @Test
  public void testRecommendMultipleAnnotationsFail() throws Exception {
    compile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "@RecommendMultipleAnnotations @RequireNoArgConstructor public class Annotated { }"));

    verifyPrintMessage(
      Diagnostic.Kind.WARNING,
      "Class " + className("Annotated") + " is annotated with @" + RecommendMultipleAnnotations.class.getName()
        + " but not with @" + Unconstrained.class.getName(),
      new ClassValue(className("Annotated")),
      RecommendMultipleAnnotations.class);
    verifyNoMoreInteractions(messager);
  }

}
