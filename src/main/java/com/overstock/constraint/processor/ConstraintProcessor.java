package com.overstock.constraint.processor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.overstock.constraint.verifier.CompanionAnnotationsVerifier;
import com.overstock.constraint.verifier.DisallowAnnotationsVerifier;
import com.overstock.constraint.verifier.RequireAnnotationsOnSupertypeVerifier;
import com.overstock.constraint.verifier.RequireConstructorsVerifier;
import com.overstock.constraint.verifier.RequireSupertypesVerifier;
import com.overstock.constraint.verifier.Verifier;

@SupportedAnnotationTypes("*")
public class ConstraintProcessor extends AbstractProcessor {

  private List<? extends Verifier> verifiers;

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    this.verifiers = Arrays.asList(
      new RequireConstructorsVerifier(processingEnv),
      new CompanionAnnotationsVerifier(processingEnv),
      new DisallowAnnotationsVerifier(processingEnv),
      new RequireSupertypesVerifier(processingEnv),
      new RequireAnnotationsOnSupertypeVerifier(processingEnv)
    ); //TODO uniform and extensible way of registering verifiers
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element element : roundEnv.getRootElements()) {
      for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
        Constraints constraints = Constraints.on(annotationMirror);
        if (!constraints.isEmtpy()) {
          for (Verifier verifier : verifiers) {
            verifier.verify(element, annotationMirror, constraints);
          }
        }
      }
    }

    return false;
  }

}
