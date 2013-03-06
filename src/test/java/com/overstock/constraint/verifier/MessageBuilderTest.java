package com.overstock.constraint.verifier;

import static org.junit.Assert.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

import org.junit.Test;
import org.mockito.Mockito;

import com.overstock.constraint.processor.ConstraintMirror;

public class MessageBuilderTest {

  @Test
  public void testNotNullConstraints() {
    Kind kind = Diagnostic.Kind.NOTE;
    ProcessingEnvironment processingEnv = Mockito.mock(ProcessingEnvironment.class);
    Element element = Mockito.mock(Element.class);
    AnnotationMirror annotationMirror = Mockito.mock(AnnotationMirror.class);
    ConstraintMirror constraintMirror = new ConstraintMirror(annotationMirror);
    verifyNotNull(null, processingEnv, element, annotationMirror, constraintMirror, "Kind cannot be null");
    verifyNotNull(kind, null, element, annotationMirror, constraintMirror, "ProcessingEnvironment cannot be null");
    verifyNotNull(kind, processingEnv, null, annotationMirror, constraintMirror, "Element cannot be null");
    verifyNotNull(kind, processingEnv, element, null, constraintMirror, "AnnotationMirror cannot be null");
    verifyNotNull(kind, processingEnv, element, annotationMirror, null, "ConstraintMirror cannot be null");
  }

  private void verifyNotNull(
    Diagnostic.Kind kind,
    ProcessingEnvironment processingEnv,
    Element element,
    AnnotationMirror annotationMirror,
    ConstraintMirror constraintMirror,
    String expectedMessage) {
    try {
      new MessageBuilder(kind,processingEnv, element, annotationMirror, constraintMirror);
      fail("NPE expected");
    }
    catch (NullPointerException e) {
      assertEquals(expectedMessage, e.getMessage());
    }
  }
}
