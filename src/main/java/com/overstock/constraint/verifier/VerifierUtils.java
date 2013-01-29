package com.overstock.constraint.verifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public class VerifierUtils {

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
        List<AnnotationValue> values = (List<AnnotationValue>) elementValues.get(executableElement).getValue(); //TODO does this work for other compilers besides javac?
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
   * Gets the class names represented by the annotation values.
   *
   * @param annotationValues the annotation values
   * @return the class names represented by the annotation values
   * @see #getClassName(javax.lang.model.element.AnnotationValue)
   */
  public static Set<String> getClassNames(Collection<AnnotationValue> annotationValues) {
    Set<String> classNames = new HashSet<String>(annotationValues.size());
    for (AnnotationValue annotationValue : annotationValues) {
      classNames.add(getClassName(annotationValue));
    }
    return classNames;
  }

  /**
   * Gets the class name represented by the annotation value.
   *
   * @param annotationValue the annotation value
   * @return the class name represented by the annotation value
   */
  public static String getClassName(AnnotationValue annotationValue) {
    return annotationValue.getValue().toString(); //TODO does this work for other compilers besides javac?
  }

  /**
   * Gets the class name of the element's type.
   *
   * @param element the element
   * @return the class name of the element's type
   */
  public static String getClassName(Element element) {
    return element.asType().toString();
  }

  /**
   * Gets the class name of the annotation's type.
   *
   * @param annotation the annotation
   * @return the class name of the annotation's type
   */
  public static String getClassName(AnnotationMirror annotation) {
    return annotation.getAnnotationType().asElement().toString();
  }

  /**
   * Gets the class names represented by the annotation values for the element named "{@code value}" on the annotation.
   *
   * @param annotation the annotation
   * @return the class names represented by the annotation values for the element named "{@code value}" on the
   * annotation
   * @see #getClassNames(java.util.Collection)
   * @see #getArrayValues(javax.lang.model.element.AnnotationMirror)
   */
  public static Set<String> getValuesAsClassNames(AnnotationMirror annotation) {
    return getClassNames(getArrayValues(annotation));
  }

  public static String getClassName(TypeMirror typeMirror) {
    return typeMirror.toString();
  }

  private VerifierUtils() {}
}
