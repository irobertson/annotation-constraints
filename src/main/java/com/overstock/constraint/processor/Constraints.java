package com.overstock.constraint.processor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import com.overstock.constraint.Constraint;

/**
 * The {@link Constraint Constraints} on an annotation.
 */
public class Constraints {

  private final Collection<AnnotationMirror> constraintAnnotations;
  private final ProcessingEnvironment processingEnv;

  /**
   * The constraints on the annotation represented by the {@link AnnotationMirror}.
   *
   * @param annotation the annotation mirror
   * @param processingEnv the processing environment
   * @return the constraints for the annotation represented by the {@link AnnotationMirror}, never {@code null}.
   */
  public static Constraints on(AnnotationMirror annotation, ProcessingEnvironment processingEnv) {
    return on(annotation.getAnnotationType().asElement(), processingEnv);
  }

  /**
   * The constraints on the annotation represented by the {@link Element}.
   *
   * @param annotation the annotation element
   * @param processingEnv the processing environment
   * @return the constraints for the annotation represented by the {@link Element}, never {@code null}.
   */
  private static Constraints on(Element annotation, ProcessingEnvironment processingEnv) {
    Set<AnnotationMirror> constraints = new HashSet<AnnotationMirror>();
    TypeMirror constraintMirror = getTypeMirror(Constraint.class, processingEnv);
    Types types = processingEnv.getTypeUtils();
    for (AnnotationMirror maybeConstraining : annotation.getAnnotationMirrors()) {
      for (AnnotationMirror metaAnnotation : maybeConstraining.getAnnotationType().asElement().getAnnotationMirrors()) {
        if (types.isSameType(constraintMirror, metaAnnotation.getAnnotationType())) {
          constraints.add(maybeConstraining);
        }
      }
    }
    return new Constraints(constraints, processingEnv);
  }

  /**
   * Gets the constraint of the given type, if present.
   *
   * @param constraintType the type of the constraint
   * @return the constraint of the given type or null if it is not present
   */
  public AnnotationMirror get(Class<? extends Annotation> constraintType) {
    final TypeMirror queried = getTypeMirror(constraintType, processingEnv);
    final Types types = processingEnv.getTypeUtils();
    for (AnnotationMirror constraint : constraintAnnotations) {
      if (types.isSameType(queried, constraint.getAnnotationType())) {
        return constraint;
      }
    }
    return null;
  }

  private static TypeMirror getTypeMirror(Class<?> clazz, ProcessingEnvironment processingEnv) {
    return processingEnv.getElementUtils().getTypeElement(clazz.getCanonicalName()).asType();
  }

  public boolean isEmpty() {
    return constraintAnnotations.isEmpty();
  }

  private Constraints(Collection<AnnotationMirror> constraintAnnotations, ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
    this.constraintAnnotations = Collections.unmodifiableCollection(constraintAnnotations);
  }
}
