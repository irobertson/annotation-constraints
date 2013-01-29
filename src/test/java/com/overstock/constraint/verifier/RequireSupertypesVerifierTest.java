package com.overstock.constraint.verifier;

import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.Serializable;
import java.lang.annotation.Inherited;

import javax.tools.Diagnostic;

import org.junit.Test;

import com.overstock.constraint.RequireSupertypes;
import com.overstock.constraint.processor.AbstractConstraintProcessorTest;
import com.overstock.constraint.processor.SourceFile;

public class RequireSupertypesVerifierTest extends AbstractConstraintProcessorTest {

  @Test
  public void testRequireSupertypesPass() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "import " + qualifiedNestedClassName(RequireBaseClassAndSerializable.class) + ";",
      "import " + qualifiedNestedClassName(BaseClass.class) + ";",
      "import " + Serializable.class.getName() + ";",
      "@RequireBaseClassAndSerializable public class Annotated extends BaseClass implements Serializable {}"));
  }

  @Test
  public void testRequireSupertypesFail() throws Exception {
    compile(new SourceFile(
      filePath("Annotated.java"),
      PACKAGE_DECLARATION,
      "import " + qualifiedNestedClassName(RequireBaseClassAndSerializable.class) + ";",
      "@RequireBaseClassAndSerializable public class Annotated {}"));

    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" + qualifiedNestedClassName(RequireBaseClassAndSerializable.class)
        + " but does not have " + qualifiedNestedClassName(BaseClass.class) + " as a supertype",
      className("Annotated"),
      qualifiedNestedClassName(RequireBaseClassAndSerializable.class));
    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("Annotated") + " is annotated with @" + qualifiedNestedClassName(RequireBaseClassAndSerializable.class)
        + " but does not have " + Serializable.class.getName() + " as a supertype",
      className("Annotated"),
      qualifiedNestedClassName(RequireBaseClassAndSerializable.class));
    verifyNoMoreInteractions(messager);
  }

  @Test
  public void testRequireSupertypesIndirectPass() throws Exception {
    assertCleanCompile(
      new SourceFile(
        filePath("Extending.java"),
        PACKAGE_DECLARATION,
        "import " + qualifiedNestedClassName(BaseClass.class) + ";",
        "import " + Serializable.class.getName() + ";",
        "public class Extending extends BaseClass implements Serializable {}"),
      new SourceFile(
        filePath("Annotated.java"),
        PACKAGE_DECLARATION,
        "import " + qualifiedNestedClassName(RequireBaseClassAndSerializable.class) + ";",
        "import " + Serializable.class.getName() + ";",
        "@RequireBaseClassAndSerializable public class Annotated extends Extending {}"));
  }

  private static String qualifiedNestedClassName(Class<?> clazz) {
    return RequireSupertypesVerifierTest.class.getName() + "." + clazz.getSimpleName();
  }

  @Inherited
  @RequireSupertypes({BaseClass.class, Serializable.class})
  public static @interface RequireBaseClassAndSerializable {
  }

  public static class BaseClass {}

}
