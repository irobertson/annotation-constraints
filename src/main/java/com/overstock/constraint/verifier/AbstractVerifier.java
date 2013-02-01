package com.overstock.constraint.verifier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public abstract class AbstractVerifier implements Verifier {

  protected ProcessingEnvironment processingEnv;

  @Override
  public void init(ProcessingEnvironment environment) {
    this.processingEnv = environment;
  }

  /**
   * Raise an message about a class annotated with an annotation.
   * @param kind the error level
   * @param element the annotated class
   * @param annotationMirror the annotation
   * @param message the rest of the message
   */
  protected void raiseAnnotatedClassMessage(Diagnostic.Kind kind, Element element, AnnotationMirror annotationMirror,
      String message) {
    raiseAnnotatedClassMessage(
      kind, element, annotationMirror, message, annotationMirror.getAnnotationType().asElement());

  }

  /**
   * Raise an message about a class annotated with an annotation.
   * @param kind the error level
   * @param element the annotated class
   * @param annotationMirror the annotation
   * @param message the rest of the message
   * @param annotationLabel a label for the annotation
   */
  protected void raiseAnnotatedClassMessage(Diagnostic.Kind kind, Element element, AnnotationMirror annotationMirror,
      String message, final Object annotationLabel) {
    processingEnv.getMessager().printMessage(
      kind, "Class " + element + " is annotated with @" + annotationLabel + message, element, annotationMirror);
  }
}
