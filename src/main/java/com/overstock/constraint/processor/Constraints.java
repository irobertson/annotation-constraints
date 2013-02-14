package com.overstock.constraint.processor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import com.overstock.constraint.Constraint;

/**
 * The {@link Constraint Constraints} on an annotation.
 *
 * @see Constraint
 */
public class Constraints {
  private static final Map<Element, Constraints> CACHE = new HashMap<Element, Constraints>();

  private final Collection<AnnotationMirror> constraintAnnotations;
  private final ProcessingEnvironment processingEnv;

  /**
   * The constraints on the annotation represented by the {@link AnnotationMirror}.
   *
   *
   * @param annotation the annotation mirror
   * @param externalConstraints the external constraints
   * @param internalConstraints the internal constraints
   * @param processingEnv the processing environment
   * @return the constraints for the annotation represented by the {@link AnnotationMirror}, never {@code null}.
   */
  public static Constraints on(AnnotationMirror annotation, ExternalConstraints externalConstraints,
      InternalConstraints internalConstraints, ProcessingEnvironment processingEnv) {
    Element annotationElement = annotation.getAnnotationType().asElement();
    Constraints cached = CACHE.get(annotationElement);
    if (cached != null) {
      return cached;
    }
    Set<AnnotationMirror> constraints = annotatedConstraints(annotationElement, processingEnv);
    constraints.addAll(externalConstraints.get(annotationElement));
    constraints.addAll(internalConstraints.get(annotationElement));
    Constraints result = new Constraints(constraints, processingEnv);
    CACHE.put(annotationElement, result);
    return result;
  }

  private static Set<AnnotationMirror> annotatedConstraints(Element annotation, ProcessingEnvironment processingEnv) {
    Set<AnnotationMirror> constraints = new HashSet<AnnotationMirror>();
    addConstraints(constraints, annotation, MirrorUtils.getTypeMirror(Constraint.class, processingEnv),
      processingEnv.getTypeUtils());
    return constraints;
  }

  private static void addConstraints(Set<AnnotationMirror> constraints, Element annotation, TypeMirror constraintMirror,
      Types types) {
    for (AnnotationMirror maybeConstraining : annotation.getAnnotationMirrors()) {
      for (AnnotationMirror metaAnnotation : maybeConstraining.getAnnotationType().asElement().getAnnotationMirrors()) {
        if (types.isSameType(constraintMirror, metaAnnotation.getAnnotationType())) {
          constraints.add(maybeConstraining);
        }
      }
    }
  }

  /**
   * Gets the constraint of the given type, if present.
   *
   * @param constraintType the type of the constraint
   * @return the constraint of the given type or null if it is not present
   */
  public AnnotationMirror get(Class<? extends Annotation> constraintType) {
    final TypeMirror queried = MirrorUtils.getTypeMirror(constraintType, processingEnv);
    final Types types = processingEnv.getTypeUtils();
    for (AnnotationMirror constraint : constraintAnnotations) {
      if (types.isSameType(queried, constraint.getAnnotationType())) {
        return constraint;
      }
    }
    return null;
  }

  public boolean isEmpty() {
    return constraintAnnotations.isEmpty();
  }

  private Constraints(Collection<AnnotationMirror> constraintAnnotations, ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
    this.constraintAnnotations = Collections.unmodifiableCollection(constraintAnnotations);
  }
}
