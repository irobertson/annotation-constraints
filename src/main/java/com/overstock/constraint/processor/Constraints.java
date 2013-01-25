package com.overstock.constraint.processor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.overstock.constraint.Constraint;

/**
 * The {@link Constraint Constraints} on an annotation.
 */
public class Constraints {

  private final Collection<AnnotationMirror> constraintAnnotations;

  /**
   * The constraints on the annotation represented by the {@link AnnotationMirror}.
   *
   * @param annotation the annotation mirror
   * @return the constraints for the annotation represented by the {@link AnnotationMirror}, never {@code null}.
   */
  public static Constraints on(AnnotationMirror annotation) {
    return on(annotation.getAnnotationType().asElement());
  }

  /**
   * The constraints on the annotation represented by the {@link Element}.
   *
   * @param annotation the annotation element
   * @return the constraints for the annotation represented by the {@link Element}, never {@code null}.
   */
  public static Constraints on(Element annotation) {
    Set<AnnotationMirror> constraints = new HashSet<AnnotationMirror>();
    for (AnnotationMirror maybeConstraining : annotation.getAnnotationMirrors()) {
      for (AnnotationMirror maybeConstraint : maybeConstraining.getAnnotationType().asElement()
        .getAnnotationMirrors()) {
        if (isOfType(Constraint.class, maybeConstraint)) {
          constraints.add(maybeConstraining);
        }
      }
    }
    return new Constraints(constraints);
  }

  /**
   * Gets the constraint of the given type, if present.
   *
   * @param constraintType the type of the constraint
   * @return the constraint of the given type or null if it is not present
   */
  public AnnotationMirror get(Class<? extends Annotation> constraintType) {
    for (AnnotationMirror constraint : constraintAnnotations) {
      if (isOfType(constraintType, constraint)) {
        return constraint;
      }
    }
    return null;
  }

  public boolean isEmtpy() {
    return constraintAnnotations.isEmpty();
  }

  private Constraints(Collection<AnnotationMirror> constraintAnnotations) {
    this.constraintAnnotations = Collections.unmodifiableCollection(constraintAnnotations);
  }

  private static boolean isOfType(Class<? extends Annotation> annotationType, AnnotationMirror constraint) {
    return annotationType.getName().equals(qualifiedName(constraint));
  }

  private static String qualifiedName(AnnotationMirror annotationMirror) {
    return ((TypeElement) annotationMirror.getAnnotationType().asElement()).getQualifiedName().toString();
  }
}
