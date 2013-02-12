package com.overstock.constraint.verifier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

@ServiceProvider
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
   * @param constrained the constrained annotation
   * @param message the rest of the message
   */
  protected void raiseAnnotatedClassMessage(Diagnostic.Kind kind, Element element, AnnotationMirror constrained,
      String message) {
    processingEnv.getMessager().printMessage(
      kind,
      "Class " + element + " is annotated with @" + constrained.getAnnotationType().asElement() + message,
      element,
      constrained);
  }
}
