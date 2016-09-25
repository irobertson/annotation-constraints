package org.annotationconstraints.verifier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import org.annotationconstraints.processor.ConstraintMirror;

public abstract class AbstractVerifier implements Verifier {
  /**
   * The processingEnvironment passed in to {#link {@link #init(ProcessingEnvironment)}.
   */
  protected ProcessingEnvironment processingEnv;

  /**
   * @return convenience method returning {@link #processingEnv}.getElementUtils();
   */
  public Elements getElementUtils() {
    return processingEnv.getElementUtils();
  }

  /**
   * @return convenience method returning {@link #processingEnv}.getTypeUtils();
   */
  public Types getTypeUtils() {
    return processingEnv.getTypeUtils();
  }


  public MessageBuilder messageBuilder(
    Diagnostic.Kind kind, Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint) {
    return MessageBuilder.format(kind, processingEnv, element, annotationMirror, constraint);
  }

  @Override
  public void init(ProcessingEnvironment environment) {
    this.processingEnv = environment;
  }
}
