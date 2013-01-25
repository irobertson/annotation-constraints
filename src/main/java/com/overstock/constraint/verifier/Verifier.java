package com.overstock.constraint.verifier;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import com.overstock.constraint.Constraint;
import com.overstock.constraint.processor.Constraints;

/**
 * Verifies that elements satisfy certain constraints presented by their annotations.
 */
public interface Verifier {

  /**
   * Verify that an annotated element satisfies contracts imposed by an annotation which has been annotated with
   * one or more {@link Constraint}s.
   *
   * @param element the annotated element
   * @param annotation the annotation on {@code element}
   * @param constraints the {@link Constraint} annotations on the annotation represented by {@code annotation}.
   */
  void verify(Element element, AnnotationMirror annotation, Constraints constraints);
}
