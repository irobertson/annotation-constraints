package com.overstock.constraint.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.overstock.constraint.Constraint;
import com.overstock.constraint.provider.ConstraintProvider;
import com.overstock.constraint.provider.ConstraintsFor;

/**
 * Constraints defined in the current compilation unit via one or more {@link ConstraintProvider}s.
 */
class InternalConstraints { //TODO combine with ExternalConstraints

  private final Map<Element, Collection<AnnotationMirror>> constraints;

  private final ProcessingEnvironment processingEnv;

  public static InternalConstraints from(Set<? extends Element> elements, ProcessingEnvironment processingEnv) {
    final Map<Element, Collection<AnnotationMirror>> constraints = new HashMap<Element, Collection<AnnotationMirror>>();
    final Types typeUtils = processingEnv.getTypeUtils();
    final Elements elementUtils = processingEnv.getElementUtils();
    final TypeMirror constraintMirror = MirrorUtils.getTypeMirror(Constraint.class, processingEnv);
    final TypeMirror constraintsForMirror = MirrorUtils.getTypeMirror(ConstraintsFor.class, processingEnv);
    final TypeMirror constraintProviderMirror = MirrorUtils.getTypeMirror(ConstraintProvider.class, processingEnv);
    for (Element element : elements) {
      if (MirrorUtils.getSupertypes(element.asType(), typeUtils).contains(constraintProviderMirror)) {
        for (AnnotationMirror annotationMirror : elementUtils.getAllAnnotationMirrors(element)) {
          if (typeUtils.isSameType(constraintsForMirror, annotationMirror.getAnnotationType())) {
            TypeMirror targetMirror = null;
            TypeMirror providingMirror = null;
            for (ExecutableElement executableElement : annotationMirror.getElementValues().keySet()) {
              if (executableElement.getSimpleName().contentEquals("annotation")) {
                targetMirror = (TypeMirror) annotationMirror.getElementValues().get(executableElement).getValue();
              }
              else if (executableElement.getSimpleName().contentEquals("canBeFoundOn")) {
                providingMirror = (TypeMirror) annotationMirror.getElementValues().get(executableElement).getValue();
              }
            }
            if (targetMirror == null || providingMirror == null) {
              throw new IllegalStateException("Invalid " + ConstraintsFor.class.getSimpleName() + " annotation on " +
                element.asType());
            }
            Element target = typeUtils.asElement(targetMirror);
            Element providingAnnotation = typeUtils.asElement(providingMirror);
            final List<AnnotationMirror> currentConstraints = new ArrayList<AnnotationMirror>();
            for (AnnotationMirror maybeConstraining : providingAnnotation.getAnnotationMirrors()) {
              Element maybeConstrainingElement = maybeConstraining.getAnnotationType().asElement();
              for (AnnotationMirror metaAnnotation : maybeConstrainingElement.getAnnotationMirrors()) {
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
        }
      }
    }
    return new InternalConstraints(constraints, processingEnv);
  }

  public Collection<AnnotationMirror> get(Element annotation) {
    Types typeUtils = processingEnv.getTypeUtils();
    for (Element element : constraints.keySet()) {
      if (typeUtils.isSameType(annotation.asType(), element.asType())) {
        return constraints.get(element);
      }
    }
    return Collections.emptyList();
  }

  private InternalConstraints(Map<Element, Collection<AnnotationMirror>> constraints,
      ProcessingEnvironment processingEnv) {
    this.constraints = constraints;
    this.processingEnv = processingEnv;
  }
}
