package com.overstock.constraint.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class MirrorUtils {
  private static final Map<Class<?>, TypeMirror> TYPE_MIRRORS = new HashMap<Class<?>, TypeMirror>();

  private static final Map<TypeMirror, Set<TypeMirror>> SUPERTYPES = new HashMap<TypeMirror, Set<TypeMirror>>();

  /**
   * Gets the value of an annotation's element.
   *
   * @param annotationMirror the annotation mirror
   * @param elementName the name of the element
   * @return the value of the annotation's element with the specified name or {@code null} if it doesn't exist
   */
  public static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String elementName) {
    for (ExecutableElement executableElement : annotationMirror.getElementValues().keySet()) {
      if (executableElement.getSimpleName().contentEquals(elementName)) {
        return annotationMirror.getElementValues().get(executableElement);
      }
    }
    return null;
  }

  /**
   * Gets the TypeMirror representing the class.
   *
   * @param clazz the class
   * @param elementUtils the element utils
   * @return the TypeMirror representing the class
   */
  public static TypeMirror getTypeMirror(Class<?> clazz, Elements elementUtils) {
    TypeMirror typeMirror = TYPE_MIRRORS.get(clazz);
    if (typeMirror == null) {
      typeMirror = elementUtils.getTypeElement(clazz.getCanonicalName()).asType();
      TYPE_MIRRORS.put(clazz, typeMirror);
    }
    return typeMirror;
  }

  /**
   * Gets the supertypes of the type, including the type itself.
   *
   * @param type the type
   * @param typeUtils the type utils
   * @return the supertypes of the type
   */
  public static Set<TypeMirror> getSupertypes(TypeMirror type, Types typeUtils) {
    Set<TypeMirror> supertypes = SUPERTYPES.get(type);
    if (supertypes == null) {
      supertypes = calculateSupertypes(type, typeUtils);
      SUPERTYPES.put(type, supertypes);
    }
    return supertypes;
  }

  private static Set<TypeMirror> calculateSupertypes(TypeMirror type, Types typeUtils) {
    Set<TypeMirror> supertypes = new HashSet<TypeMirror>();
    supertypes.add(type); //include the type itself
    for (TypeMirror supertype : typeUtils.directSupertypes(type)) {
      supertypes.addAll(getSupertypes(supertype, typeUtils));
    }
    return supertypes;
  }

  public static boolean isSameType(Class<?> clazz, TypeMirror type, Types typeUtils, Elements elementUtils) {
    return typeUtils.isSameType(getTypeMirror(clazz, elementUtils), type);
  }

  static void clearCaches() {
    TYPE_MIRRORS.clear();
    SUPERTYPES.clear();
  }

  private MirrorUtils() {}
}
