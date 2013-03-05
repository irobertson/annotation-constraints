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
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.overstock.constraint.Constraint;
import com.overstock.constraint.provider.ProvidesConstraintsFor;

/**
 * Constraints provided by proxy via {@link com.overstock.constraint.provider.ProvidesConstraintsFor}.
 */
class ProvidedConstraints {

  private final Map<Element, Collection<ConstraintMirror>> constraints;

  private final ProcessingEnvironment processingEnv;

  public static ProvidedConstraints from(Set<? extends Element> elements, ProcessingEnvironment processingEnv) {
    Map<Element, Collection<ConstraintMirror>> constraints = new HashMap<Element, Collection<ConstraintMirror>>();
    Types typeUtils = processingEnv.getTypeUtils();
    Elements elementUtils = processingEnv.getElementUtils();
    TypeMirror constraintsForMirror = MirrorUtils.getTypeMirror(ProvidesConstraintsFor.class,
      processingEnv.getElementUtils());
    for (Element element : elements) {
      putOrAddAll(constraints, element, constraintsForMirror, typeUtils, elementUtils, processingEnv);
    }
    return new ProvidedConstraints(constraints, processingEnv);
  }

  private static void putOrAddAll(Map<Element, Collection<ConstraintMirror>> constraints, Element provider,
      TypeMirror constraintsForMirror, Types typeUtils, Elements elementUtils, ProcessingEnvironment processingEnv) {
    for (AnnotationMirror annotationMirror : elementUtils.getAllAnnotationMirrors(provider)) {
      if (typeUtils.isSameType(constraintsForMirror, annotationMirror.getAnnotationType())) {
        AnnotationValue value = MirrorUtils.getAnnotationValue(annotationMirror, "value");
        if (value == null) {
          continue; //apparently isSameType can return true for javac when one is a missing type!!!
        }
        putOrAddAll(constraints, typeUtils.asElement((TypeMirror) value.getValue()), getConstraints(provider,
          processingEnv, provider.asType()));
      }
    }
  }

  private static Collection<ConstraintMirror> getConstraints(Element annotation,
      ProcessingEnvironment processingEnv, TypeMirror provider) {
    final Types typeUtils = processingEnv.getTypeUtils();
    final TypeMirror constraintMirror = MirrorUtils.getTypeMirror(Constraint.class, processingEnv.getElementUtils());
    final List<ConstraintMirror> currentConstraints = new ArrayList<ConstraintMirror>();
    for (AnnotationMirror maybeConstraining : annotation.getAnnotationMirrors()) {
      for (AnnotationMirror metaAnnotation :
          maybeConstraining.getAnnotationType().asElement().getAnnotationMirrors()) {
        if (typeUtils.isSameType(constraintMirror, metaAnnotation.getAnnotationType())) {
          currentConstraints.add(new ConstraintMirror(maybeConstraining, provider));
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
