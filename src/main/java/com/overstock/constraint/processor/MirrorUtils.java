package com.overstock.constraint.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class MirrorUtils {
  private static final Map<Class<?>, TypeMirror> TYPE_MIRRORS = new HashMap<Class<?>, TypeMirror>();

  /**
   * Gets the TypeMirror representing the class.
   *
   * @param clazz the class
   * @param processingEnv the ProcessingEnvironment
   * @return the TypeMirror representing the class
   */
  public static TypeMirror getTypeMirror(Class<?> clazz, ProcessingEnvironment processingEnv) {
    TypeMirror typeMirror = TYPE_MIRRORS.get(clazz);
    if (typeMirror == null) {
      typeMirror = processingEnv.getElementUtils().getTypeElement(clazz.getCanonicalName()).asType();
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
    if (TypeKind.DECLARED != type.getKind()) {
      return Collections.emptySet();
    }
    Set<TypeMirror> supertypes = new HashSet<TypeMirror>(); //TODO investigate caching
    supertypes.add(type); //include the type itself
    for (TypeMirror supertype : typeUtils.directSupertypes(type)) {
      supertypes.addAll(getSupertypes(supertype, typeUtils));
    }
    return supertypes;
  }
}
