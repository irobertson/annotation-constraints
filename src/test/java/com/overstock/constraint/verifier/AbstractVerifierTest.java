package com.overstock.constraint.verifier;

import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import org.junit.Test;

import com.overstock.constraint.processor.AbstractConstraintProcessorTest;
import com.overstock.constraint.processor.Constraints;
import com.overstock.constraint.processor.SourceFile;

public class AbstractVerifierTest extends AbstractConstraintProcessorTest {

  public AbstractVerifierTest(CompilerProvider provider) {
    super(provider);
  }

  @Test
  public void testVerifierPass() throws Exception {
    assertCleanCompile(new SourceFile(
      filePath("GoodVerifier.java"),
      PACKAGE_DECLARATION,
      "import " + ProcessingEnvironment.class.getName() + ";",
      "import " + AnnotationMirror.class.getName() + ";",
      "import " + Element.class.getName() + ";",
      "import " + Constraints.class.getName() + ";",
      "import " + AbstractVerifier.class.getName() + ";",
      "public class GoodVerifier extends AbstractVerifier {",
      "  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {}",
      "  public void init(ProcessingEnvironment environment) {}",
      "}"));
  }

  @Test
  public void testVerifierWithoutNoArgConstructor() throws Exception {
    compile(new SourceFile(
      filePath("BadVerifier.java"),
      PACKAGE_DECLARATION,
      "import " + ProcessingEnvironment.class.getName() + ";",
      "import " + AnnotationMirror.class.getName() + ";",
      "import " + Element.class.getName() + ";",
      "import " + Constraints.class.getName() + ";",
      "import " + AbstractVerifier.class.getName() + ";",
      "public class BadVerifier extends AbstractVerifier {",
      "  public BadVerifier(String s) {}",
      "  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {}",
      "  public void init(ProcessingEnvironment environment) {}",
      "}"));

    verifyPrintMessage(
      Diagnostic.Kind.ERROR,
      "Class " + className("BadVerifier") + " is annotated with @" + ServiceProvider.class.getName()
        + " but does not have a constructor with no arguments",
      className("BadVerifier"),
      ServiceProvider.class);
    verifyNoMoreInteractions(messager);
  }

}
