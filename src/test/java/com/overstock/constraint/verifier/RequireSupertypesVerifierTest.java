package com.overstock.constraint.verifier;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.Serializable;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.overstock.constraint.RequireSupertypes;
import com.overstock.constraint.processor.*;

public class RequireSupertypesVerifierTest extends AbstractConstraintProcessorTest {

  public RequireSupertypesVerifierTest(CompilerProvider provider) {
    super(provider);
  }

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
      "Class " + className("Annotated") + " is annotated with @" + qualifiedNestedClassName(
        RequireBaseClassAndSerializable.class) + " but does not have " + Serializable.class.getName()
        + " as a supertype",
      className("Annotated"),
      qualifiedNestedClassName(RequireBaseClassAndSerializable.class));
    verify(messager).printMessage(
      eq(Diagnostic.Kind.ERROR),
      argThat(
        RegexMatcher.matches("Class " + className("Annotated") + " is annotated with @" + qualifiedNestedClassName(
          RequireBaseClassAndSerializable.class) + " but does not have " + qualifiedNestedClassName(BaseClass.class)
          + " as a supertype")),
      argThat(Matchers.<Element>hasToString(className("Annotated"))),
      argThat(
        Matchers.<AnnotationMirror>hasToString("@" + qualifiedNestedClassName(RequireBaseClassAndSerializable.class))));
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
        "@RequireBaseClassAndSerializable public class Annotated extends Extending {}"));
  }

  @Test
  public void testRequireSelfSupertypePass() throws Exception {
    assertCleanCompile(
      new SourceFile(
        filePath("Annotated.java"),
        PACKAGE_DECLARATION,
        "@SuperclassRequired public class Annotated {}"),
      new SourceFile(
        filePath("SuperclassRequired.java"),
        PACKAGE_DECLARATION,
        "import com.overstock.constraint.RequireSupertypes;",
        "@RequireSupertypes(Annotated.class)",
        "public @interface SuperclassRequired {}"));
  }

  private static String qualifiedNestedClassName(Class<?> clazz) {
    return RequireSupertypesVerifierTest.class.getName() + "." + clazz.getSimpleName();
  }

  @RequireSupertypes({BaseClass.class, Serializable.class})
  public static @interface RequireBaseClassAndSerializable {
  }

  public static class BaseClass {}

}
