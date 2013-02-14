package com.overstock.constraint.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.overstock.constraint.provider.ConstraintProvider;
import com.overstock.constraint.provider.ConstraintsFor;

/**
 * Constraints defined externally via one or more {@link ConstraintProvider}s.
 */
class ExternalConstraints {

  private final Map<TypeElement, Collection<AnnotationMirror>> constraints;

  private final ProcessingEnvironment processingEnv;

  public static ExternalConstraints from(Iterable<ConstraintProvider> providers, ProcessingEnvironment processingEnv) {
    final Map<TypeElement, Collection<AnnotationMirror>> constraints =
      new HashMap<TypeElement, Collection<AnnotationMirror>>();
    final Types typeUtils = processingEnv.getTypeUtils();
    final Elements elementUtils = processingEnv.getElementUtils();
    final TypeMirror constraintMirror = MirrorUtils.getConstraintMirror(processingEnv);
    for (ConstraintProvider provider : providers) {
      ConstraintsFor constraintsFor = provider.getClass().getAnnotation(ConstraintsFor.class);
      if (constraintsFor == null) {
        processingEnv.getMessager().printMessage(
          Diagnostic.Kind.WARNING,
          String.format("ConstraintProvider %s is not annotated with %s", provider.getClass().getName(),
            ConstraintsFor.class.getName()));
        continue;
      }
      final TypeElement target = elementUtils.getTypeElement(constraintsFor.annotation().getCanonicalName());
      final TypeElement providingAnnotation = elementUtils.getTypeElement(
        constraintsFor.canBeFoundOn().getCanonicalName());
      final List<AnnotationMirror> currentConstraints = new ArrayList<AnnotationMirror>();
      for (AnnotationMirror maybeConstraining : providingAnnotation.getAnnotationMirrors()) {
        for (AnnotationMirror metaAnnotation :
            maybeConstraining.getAnnotationType().asElement().getAnnotationMirrors()) {
          if (typeUtils.isSameType(constraintMirror, metaAnnotation.getAnnotationType())) {
            currentConstraints.add(maybeConstraining);
          }
        }
      }
      if (constraints.containsKey(target)) {
        constraints.get(target).addAll(currentConstraints);
      }
      else {
        constraints.put(target, currentConstraints);
      }
    }
    return new ExternalConstraints(constraints, processingEnv);
  }

  public Collection<AnnotationMirror> get(AnnotationMirror annotation) {
    return get(annotation.getAnnotationType().asElement());
  }

  public Collection<AnnotationMirror> get(Element annotation) {
    Types typeUtils = processingEnv.getTypeUtils();
    for (TypeElement typeElement : constraints.keySet()) {
      if (typeUtils.isSameType(annotation.asType(), typeElement.asType())) {
        return constraints.get(typeElement);
      }
    }
    return Collections.emptyList();
  }

  private ExternalConstraints(Map<TypeElement, Collection<AnnotationMirror>> constraints,
      ProcessingEnvironment processingEnv) {
    this.constraints = constraints;
    this.processingEnv = processingEnv;
  }
}
