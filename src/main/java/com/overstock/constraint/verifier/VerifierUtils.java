package com.overstock.constraint.verifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

class VerifierUtils {

  /**
   * Gets the array values for the element on the annotation with the specified name.
   *
   * @param annotation the annotation
   * @param  elementName the name of the element on the annotation
   * @return the array values for the element on the annotation with the specified name
   */
  public static List<AnnotationValue> getArrayValues(AnnotationMirror annotation, String elementName) {
    Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotation.getElementValues();
    for (ExecutableElement executableElement : elementValues.keySet()) {
      if (executableElement.getSimpleName().contentEquals(elementName)) {
        @SuppressWarnings("unchecked")
        List<AnnotationValue> values = (List<AnnotationValue>) elementValues.get(executableElement).getValue();
        return values;
      }
    }
    throw new IllegalStateException(annotation + "." + elementName + " was not found");
  }

  /**
   * Gets the array values for the element named "{@code value}" on the annotation.
   *
   * @param annotation the annotation
   * @return the array values for the element named "{@code value}" on the annotation
   */
  public static List<AnnotationValue> getArrayValues(AnnotationMirror annotation) {
    return getArrayValues(annotation, "value");
  }

  /**
   * Gets the types represented by the annotation values.
   *
   * @param annotationValues the annotation values
   * @return the types represented by the annotation values
   * @see #asType(javax.lang.model.element.AnnotationValue)
   */
  public static List<TypeMirror> asTypes(Collection<AnnotationValue> annotationValues) {
    List<TypeMirror> types = new ArrayList<TypeMirror>(annotationValues.size());
    for (AnnotationValue annotationValue : annotationValues) {
      types.add(asType(annotationValue));
    }
    return types;
  }

  /**
   * Gets the type represented by the annotation value. This only works for annotation values which are known to be
   * classes.
   *
   * @param annotationValue the annotation value
   * @return the type represented by the annotation value
   */
  public static TypeMirror asType(AnnotationValue annotationValue) {
    return (TypeMirror) annotationValue.getValue();
  }

  /**
   * Gets the type represented by the element.
   *
   *
   * @param element the element
   * @return the type represented by the element
   */
  public static TypeMirror asType(Element element) {
    return element.asType();
  }

  /**
   * Gets the annotation's type.
   *
   *
   * @param annotation the annotation
   * @return the annotation's type
   */
  public static TypeMirror asType(AnnotationMirror annotation) {
    return asType(annotation.getAnnotationType().asElement());
  }

  /**
   * Gets the types represented by the annotation values for the element named "{@code value}" on the annotation.
   *
   * @param annotation the annotation
   * @return the types represented by the annotation values for the element named "{@code value}" on the
   * annotation
   * @see #asTypes(java.util.Collection)
   * @see #getArrayValues(javax.lang.model.element.AnnotationMirror)
   */
  public static List<TypeMirror> getValuesAsTypes(AnnotationMirror annotation) {
    return asTypes(getArrayValues(annotation));
  }

  /**
   * Converts a {@link TypeMirror} to a {@link TypeElement} if the mirror represents a declared type.
   *
   * @param typeMirror the {@link TypeMirror}
   * @return the {@link TypeElement} represented by the {@link TypeMirror} or null
   */
  public static TypeElement asTypeElement(TypeMirror typeMirror) {
    return (typeMirror.getKind() == TypeKind.DECLARED)
      ? (TypeElement) ((DeclaredType) typeMirror).asElement()
      : null;
  }

  public static void removeType(Iterable<TypeMirror> types, TypeMirror t, Types typeUtils) {
    Iterator<TypeMirror> iterator = types.iterator();
    while (iterator.hasNext()) {
      if (typeUtils.isSameType(t, iterator.next())) {
        iterator.remove();
      }
    }
  }

  private VerifierUtils() {}

  public static List<TypeMirror> eraseGenerics(List<TypeMirror> types, Types typeUtils) {
    ArrayList<TypeMirror> result = new ArrayList<TypeMirror>(types.size());
    for (TypeMirror type : types) {
      result.add(typeUtils.erasure(type));
    }
    return result;
  }
}
