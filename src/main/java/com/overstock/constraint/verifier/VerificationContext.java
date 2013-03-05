package com.overstock.constraint.verifier;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.overstock.constraint.processor.ConstraintMirror;

public class VerificationContext {

  private final ProcessingEnvironment processingEnvironment;

  private final Element element;

  private final AnnotationMirror annotation;

  private final ConstraintMirror constraint;

  public VerificationContext(ProcessingEnvironment processingEnvironment, Element element, AnnotationMirror annotation,
      ConstraintMirror constraint) {
    this.processingEnvironment = processingEnvironment;
    this.element = element;
    this.annotation = annotation;
    this.constraint = constraint;
  }

  public ProcessingEnvironment getProcessingEnvironment() {
    return processingEnvironment;
  }

  public Element getElement() {
    return element;
  }

  public AnnotationMirror getAnnotation() {
    return annotation;
  }

  public ConstraintMirror getConstraint() {
    return constraint;
  }

  public Messager getMessager() {
    return getProcessingEnvironment().getMessager();
  }

  public Types getTypeUtils() {
    return getProcessingEnvironment().getTypeUtils();
  }

  public Elements getElementUtils() {
    return getProcessingEnvironment().getElementUtils();
  }

  @Override
  public String toString() {
    return "VerificationContext{" +
      "element=" + element +
      ", annotation=" + annotation +
      ", constraint=" + constraint +
      '}';
  }
}
