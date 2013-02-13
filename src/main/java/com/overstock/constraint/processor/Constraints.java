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
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.overstock.constraint.Constraint;
import com.overstock.constraint.provider.ConstraintProvider;
import com.overstock.constraint.provider.ConstraintsFor;

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
   *
   * @param annotation the annotation mirror
   * @param constraintProviders the providers of external constraints
   * @param processingEnv the processing environment
   * @return the constraints for the annotation represented by the {@link AnnotationMirror}, never {@code null}.
   */
  public static Constraints on(AnnotationMirror annotation, Iterable<ConstraintProvider> constraintProviders,
      ProcessingEnvironment processingEnv) {
    Element annotationElement = annotation.getAnnotationType().asElement();
    Constraints cached = CACHE.get(annotationElement);
    if (cached != null) {
      return cached;
    }
    Set<AnnotationMirror> constraints = annotatedConstraints(annotationElement, processingEnv);
    constraints.addAll(externalConstraints(annotationElement, constraintProviders, processingEnv));
    Constraints result = new Constraints(constraints, processingEnv);
    CACHE.put(annotationElement, result);
    return result;
  }

  private static Set<AnnotationMirror> externalConstraints(Element annotation,
      Iterable<ConstraintProvider> constraintProviders, ProcessingEnvironment processingEnv) {
    Set<AnnotationMirror> constraints = new HashSet<AnnotationMirror>();
    Elements elementUtils = processingEnv.getElementUtils();
    Types typeUtils = processingEnv.getTypeUtils();
    for (ConstraintProvider constraintProvider : constraintProviders) {
      ConstraintsFor constraintsFor = constraintProvider.getClass().getAnnotation(ConstraintsFor.class); //TODO cache this
      if (typeUtils.isSameType(annotation.asType(), getTypeMirror(constraintsFor.annotation(), processingEnv))) {
        TypeElement providerElement = elementUtils.getTypeElement(constraintsFor.canBeFoundOn().getCanonicalName());
        TypeMirror constraintMirror = getTypeMirror(Constraint.class, processingEnv);
        for (AnnotationMirror maybeConstrained : elementUtils.getAllAnnotationMirrors(providerElement)) {
          addConstraints(constraints, maybeConstrained.getAnnotationType().asElement(), constraintMirror, typeUtils);
        }
      }

    }
    return constraints;
  }

  private static Set<AnnotationMirror> annotatedConstraints(Element annotation, ProcessingEnvironment processingEnv) {
    Set<AnnotationMirror> constraints = new HashSet<AnnotationMirror>();
    addConstraints(constraints, annotation, getTypeMirror(Constraint.class, processingEnv),
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
