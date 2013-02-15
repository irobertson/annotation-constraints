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

  private final Map<Element, Collection<ConstraintMirror>> constraints;

  private final ProcessingEnvironment processingEnv;

  public static ProvidedConstraints from(Iterable<ConstraintProvider> providers, ProcessingEnvironment processingEnv) {
    final Map<Element, Collection<ConstraintMirror>> constraints = new HashMap<Element, Collection<ConstraintMirror>>();
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
      putOrAddAll(constraints, target, getConstraints(providingAnnotation, processingEnv,
        provider.getClass().getCanonicalName()));
    }
    return new ProvidedConstraints(constraints, processingEnv);
  }

  public static ProvidedConstraints from(Set<? extends Element> elements, ProcessingEnvironment processingEnv) {
    Map<Element, Collection<ConstraintMirror>> constraints = new HashMap<Element, Collection<ConstraintMirror>>();
    Types typeUtils = processingEnv.getTypeUtils();
    Elements elementUtils = processingEnv.getElementUtils();
    TypeMirror constraintsForMirror = MirrorUtils.getTypeMirror(ConstraintsFor.class, processingEnv.getElementUtils());
    for (Element element : elements) {
      putOrAddAll(constraints, element, constraintsForMirror, typeUtils, elementUtils, processingEnv);
    }
    return new ProvidedConstraints(constraints, processingEnv);
  }

  private static void putOrAddAll(Map<Element, Collection<ConstraintMirror>> constraints, Element provider,
      TypeMirror constraintsForMirror, Types typeUtils, Elements elementUtils, ProcessingEnvironment processingEnv) {
    for (AnnotationMirror annotationMirror : elementUtils.getAllAnnotationMirrors(provider)) {
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
            provider.asType());
        }
        Element target = typeUtils.asElement(targetMirror);
        Element providingAnnotation = typeUtils.asElement(providingMirror);
        putOrAddAll(constraints, target, getConstraints(providingAnnotation, processingEnv,
          provider.asType().toString()));
        break; //found the constraints
      }
    }
  }

  private static Collection<ConstraintMirror> getConstraints(Element annotation,
      ProcessingEnvironment processingEnv, String providerName) {
    final Types typeUtils = processingEnv.getTypeUtils();
    final TypeMirror constraintMirror = MirrorUtils.getTypeMirror(Constraint.class, processingEnv.getElementUtils());
    final List<ConstraintMirror> currentConstraints = new ArrayList<ConstraintMirror>();
    for (AnnotationMirror maybeConstraining : annotation.getAnnotationMirrors()) {
      for (AnnotationMirror metaAnnotation :
          maybeConstraining.getAnnotationType().asElement().getAnnotationMirrors()) {
        if (typeUtils.isSameType(constraintMirror, metaAnnotation.getAnnotationType())) {
          currentConstraints.add(new ConstraintMirror(maybeConstraining, providerName));
        }
      }
    }
    return currentConstraints;
  }

  private static void putOrAddAll(Map<Element, Collection<ConstraintMirror>> constraints, Element key,
      Collection<ConstraintMirror> value) {
    if (constraints.containsKey(key)) {
      constraints.get(key).addAll(value);
    }
    else {
      constraints.put(key, value);
    }
  }

  public Collection<ConstraintMirror> get(Element annotation) {
    Types typeUtils = processingEnv.getTypeUtils();
    for (Element element : constraints.keySet()) {
      if (typeUtils.isSameType(annotation.asType(), element.asType())) {
        return constraints.get(element);
      }
    }
    return Collections.emptyList();
  }

  public ProvidedConstraints combineWith(ProvidedConstraints providedConstraints) {
    Map<Element, Collection<ConstraintMirror>> combined =
      new HashMap<Element, Collection<ConstraintMirror>>(constraints);
    combined.putAll(providedConstraints.constraints);
    return new ProvidedConstraints(combined, processingEnv);
  }

  private ProvidedConstraints(Map<Element, Collection<ConstraintMirror>> constraints,
      ProcessingEnvironment processingEnv) {
    this.constraints = constraints;
    this.processingEnv = processingEnv;
  }
}
