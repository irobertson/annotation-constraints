package com.overstock.constraint.verifier;

import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.Serializable;
import java.lang.annotation.Inherited;

import javax.tools.Diagnostic;

import org.junit.Test;

import com.overstock.constraint.RequireAnnotationsOnSupertype;
import com.overstock.constraint.RequireUnconstrained;
import com.overstock.constraint.Unconstrained;
import com.overstock.constraint.processor.AbstractConstraintProcessorTest;
import com.overstock.constraint.processor.SourceFile;

public class RequireAnnotationsOnSupertypeVerifierTest extends AbstractConstraintProcessorTest {

  @Test
  public void testRequireAnnotationsOnSupertypePass() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "import " + qualifiedNestedClassName(RequireUnconstrainedSupertype.class) + ";",
      "import " + qualifiedNestedClassName(UnconstrainedClass.class) + ";",
      "import " + qualifiedNestedClassName(RequireUnconstrainedInterface.class) + ";",
      "import " + Serializable.class.getName() + ";",
      "@RequireUnconstrainedSupertype public class Annotated extends UnconstrainedClass implements "
        + "RequireUnconstrainedInterface {}"));
  }

  @Test
  public void testRequireAnnotationsOnSupertypeFail() throws Exception {
    compile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "import " + qualifiedNestedClassName(RequireUnconstrainedSupertype.class) + ";",
      "@RequireUnconstrainedSupertype public class Annotated {}"));

    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" +
        qualifiedNestedClassName(RequireUnconstrainedSupertype.class) + " but does not have a supertype annotated with "
        + "@" + Unconstrained.class.getName(),
      className("Annotated"),
      qualifiedNestedClassName(RequireUnconstrainedSupertype.class));
    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" +
        qualifiedNestedClassName(RequireUnconstrainedSupertype.class) + " but does not have a supertype annotated with "
        + "@" + RequireUnconstrained.class.getName(),
      className("Annotated"),
      qualifiedNestedClassName(RequireUnconstrainedSupertype.class));
    verifyNoMoreInteractions(messager);
  }

  @Test
  public void testRequireAnnotationsOnSupertypeIndirectPass() throws Exception {
    assertCleanCompile(
      new SourceFile(
        filePath("Extending.java"),
        PACKAGE_DECLARATION,
        "import " + qualifiedNestedClassName(UnconstrainedClass.class) + ";",
        "import " + qualifiedNestedClassName(RequireUnconstrainedInterface.class) + ";",
        "public class Extending extends UnconstrainedClass implements RequireUnconstrainedInterface {}"),
      new SourceFile(
        filePath("Annotated.java"),
        PACKAGE_DECLARATION,
        "import " + qualifiedNestedClassName(RequireUnconstrainedSupertype.class) + ";",
        "@RequireUnconstrainedSupertype public class Annotated extends Extending {}"));
  }

  @Test
  public void testRequireAnnotationsOnSelfPass() throws Exception {
    assertCleanCompile(
      new SourceFile(
        filePath("Annotated.java"),
        PACKAGE_DECLARATION,
        "import " + qualifiedNestedClassName(RequireUnconstrainedSupertype.class) + ";",
        "import " + Unconstrained.class.getName() + ";",
        "import " + RequireUnconstrained.class.getName() + ";",
        "@RequireUnconstrainedSupertype @Unconstrained @RequireUnconstrained public class Annotated {}"));
  }

  private static String qualifiedNestedClassName(Class<?> clazz) {
    return RequireAnnotationsOnSupertypeVerifierTest.class.getName() + "." + clazz.getSimpleName();
  }

  @Inherited
  @RequireAnnotationsOnSupertype({Unconstrained.class, RequireUnconstrained.class})
  public static @interface RequireUnconstrainedSupertype {
  }

  @Unconstrained
  public static class UnconstrainedClass {}

  @RequireUnconstrained
  public static interface RequireUnconstrainedInterface {}

}
