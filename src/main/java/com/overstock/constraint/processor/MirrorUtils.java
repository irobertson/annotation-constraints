package com.overstock.constraint.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

import com.overstock.constraint.Constraint;

class MirrorUtils {
  private static volatile TypeMirror constraintMirror;

  public static TypeMirror getTypeMirror(Class<?> clazz, ProcessingEnvironment processingEnv) {
    return processingEnv.getElementUtils().getTypeElement(clazz.getCanonicalName()).asType();
  }

  public static TypeMirror getConstraintMirror(ProcessingEnvironment processingEnv) {
    if (constraintMirror == null) {
      constraintMirror = getTypeMirror(Constraint.class, processingEnv);
    }
    return constraintMirror;
  }
}
