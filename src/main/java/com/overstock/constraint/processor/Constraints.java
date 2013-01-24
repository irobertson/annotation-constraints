package com.overstock.constraint.processor;

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
   * The constraints for the annotation represented by the {@link AnnotationMirror}.
   *
   * @param annotation the annotation mirror
   * @return the constraints for the annotation represented by the {@link AnnotationMirror}, never {@code null}.
   */
  public static Constraints from(AnnotationMirror annotation) {
    return from(annotation.getAnnotationType().asElement());
  }

  /**
   * The constraints for the annotation represented by the {@link Element}.
   *
   * @param annotation the annotation element
   * @return the constraints for the annotation represented by the {@link Element}, never {@code null}.
   */
  public static Constraints from(Element annotation) {
    Set<AnnotationMirror> constraints = new HashSet<AnnotationMirror>();
    for (AnnotationMirror maybeConstrained : annotation.getAnnotationMirrors()) {
      for (AnnotationMirror maybeConstraint : maybeConstrained.getAnnotationType().asElement()
        .getAnnotationMirrors()) {
        if (isConstraint(maybeConstraint)) {
          constraints.add(maybeConstraint);
        }
      }
    }
    return new Constraints(constraints);
  }

  public Collection<AnnotationMirror> getConstraintAnnotations() {
    return constraintAnnotations;
  }

  private Constraints(Collection<AnnotationMirror> constraintAnnotations) {
    this.constraintAnnotations = Collections.unmodifiableCollection(constraintAnnotations);
  }

  private static boolean isConstraint(AnnotationMirror maybeConstraint) {
    return Constraint.class.getName().equals(annotationName(maybeConstraint));
  }

  private static String annotationName(AnnotationMirror manifestableCandidate) {
    return ((TypeElement) manifestableCandidate.getAnnotationType().asElement()).getQualifiedName().toString();
  }
}
