package com.overstock.constraint.verifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class VerifierUtils {

  public static List<AnnotationValue> getArrayValues(AnnotationMirror annotation) {
    return getArrayValues(annotation, "value");
  }

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

  public static Set<String> getClassNames(Collection<AnnotationValue> annotationValues) {
    Set<String> classNames = new HashSet<String>(annotationValues.size());
    for (AnnotationValue annotationValue : annotationValues) {
      classNames.add(getClassName(annotationValue));
    }
    return classNames;
  }

  public static String getClassName(AnnotationValue annotationValue) {
    return annotationValue.getValue().toString(); //TODO does this work for other compilers besides javac?
  }

  public static String getClassName(VariableElement variableElement) {
    return variableElement.asType().toString();
  }

  private VerifierUtils() {}

  public static String getClassName(AnnotationMirror annotated) {
    return annotated.getAnnotationType().asElement().toString();
  }
}
