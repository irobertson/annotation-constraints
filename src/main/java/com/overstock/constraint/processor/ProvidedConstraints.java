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
import javax.tools.Diagnostic;

import com.overstock.constraint.Constraint;
import com.overstock.constraint.provider.ConstraintProvider;
import com.overstock.constraint.provider.ConstraintsFor;

/**
 * Constraints provided by {@link ConstraintProvider}s.
 */
class ProvidedConstraints {

  private final Map<Element, Collection<AnnotationMirror>> constraints;

  private final ProcessingEnvironment processingEnv;

  public static ProvidedConstraints from(Iterable<ConstraintProvider> providers, ProcessingEnvironment processingEnv) {
    final Map<Element, Collection<AnnotationMirror>> constraints = new HashMap<Element, Collection<AnnotationMirror>>();
    final Elements elementUtils = processingEnv.getElementUtils();
    for (ConstraintProvider provider : providers) {
      ConstraintsFor constraintsFor = provider.getClass().getAnnotation(ConstraintsFor.class);
      if (constraintsFor == null) {
        processingEnv.getMessager().printMessage(
          Diagnostic.Kind.WARNING,
          String.format("ConstraintProvider %s is not annotated with %s", provider.getClass().getName(),
            ConstraintsFor.class.getName()));
        continue;
      }
      Element target = elementUtils.getTypeElement(constraintsFor.annotation().getCanonicalName());
      Element providingAnnotation = elementUtils.getTypeElement(
        constraintsFor.canBeFoundOn().getCanonicalName());
      putOrAddAll(constraints, target, getConstraints(providingAnnotation, processingEnv));
    }
    return new ProvidedConstraints(constraints, processingEnv);
  }

  public static ProvidedConstraints from(Set<? extends Element> elements, ProcessingEnvironment processingEnv) {
    final Map<Element, Collection<AnnotationMirror>> constraints = new HashMap<Element, Collection<AnnotationMirror>>();
    final Types typeUtils = processingEnv.getTypeUtils();
    final Elements elementUtils = processingEnv.getElementUtils();
    final TypeMirror constraintsForMirror = MirrorUtils.getTypeMirror(ConstraintsFor.class,
      processingEnv.getElementUtils());
    final TypeMirror constraintProviderMirror = MirrorUtils.getTypeMirror(ConstraintProvider.class,
      processingEnv.getElementUtils());
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
            putOrAddAll(constraints, target, getConstraints(providingAnnotation, processingEnv));
          }
        }
      }
    }
    return new ProvidedConstraints(constraints, processingEnv);
  }

  private static Collection<AnnotationMirror> getConstraints(Element annotation,
      ProcessingEnvironment processingEnv) {
    final Types typeUtils = processingEnv.getTypeUtils();
    final TypeMirror constraintMirror = MirrorUtils.getTypeMirror(Constraint.class, processingEnv.getElementUtils());
    final List<AnnotationMirror> currentConstraints = new ArrayList<AnnotationMirror>();
    for (AnnotationMirror maybeConstraining : annotation.getAnnotationMirrors()) {
      for (AnnotationMirror metaAnnotation :
          maybeConstraining.getAnnotationType().asElement().getAnnotationMirrors()) {
        if (typeUtils.isSameType(constraintMirror, metaAnnotation.getAnnotationType())) {
          currentConstraints.add(maybeConstraining);
        }
      }
    }
    return currentConstraints;
  }

  private static void putOrAddAll(Map<Element, Collection<AnnotationMirror>> constraints, Element key,
      Collection<AnnotationMirror> value) {
    if (constraints.containsKey(key)) {
      constraints.get(key).addAll(value);
    }
    else {
      constraints.put(key, value);
    }
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

  public ProvidedConstraints combineWith(ProvidedConstraints providedConstraints) {
    Map<Element, Collection<AnnotationMirror>> combined =
      new HashMap<Element, Collection<AnnotationMirror>>(constraints);
    combined.putAll(providedConstraints.constraints);
    return new ProvidedConstraints(combined, processingEnv);
  }

  private ProvidedConstraints(Map<Element, Collection<AnnotationMirror>> constraints,
    ProcessingEnvironment processingEnv) {
    this.constraints = constraints;
    this.processingEnv = processingEnv;
  }
}
