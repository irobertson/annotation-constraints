package com.overstock.constraint.verifier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import com.overstock.constraint.processor.ConstraintMirror;

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
  protected void printMessage(Diagnostic.Kind kind, Element element, AnnotationMirror constrained,
      String message, ConstraintMirror constraint) {
    processingEnv.getMessager().printMessage(
      kind,
      "Class " + element + " is annotated with @" + getSimpleName(constrained) + message + providerMessage(constraint),
      element,
      constrained);
  }

  private String providerMessage(ConstraintMirror constraint) {
    return constraint.isProvided() ? " as specified by " + constraint.getProvider() : "";
  }

  protected final Name getSimpleName(AnnotationMirror constrained) {
    return constrained.getAnnotationType().asElement().getSimpleName();
  }

  protected final Name getSimpleName(TypeMirror typeMirror) {
    return processingEnv.getTypeUtils().asElement(typeMirror).getSimpleName();
  }
}
