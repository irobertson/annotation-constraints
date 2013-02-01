package com.overstock.constraint.processor;

import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.overstock.constraint.verifier.Verifier;

@SupportedAnnotationTypes("*")
public class ConstraintProcessor extends AbstractProcessor {

  private Iterable<Verifier> verifiers;

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    verifiers = ServiceLoader.load(Verifier.class);
    for (Verifier verifier : verifiers) {
      verifier.init(processingEnv);
    }
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element element : roundEnv.getRootElements()) {
      for (AnnotationMirror annotationMirror : processingEnv.getElementUtils().getAllAnnotationMirrors(element)) {
        Constraints constraints = Constraints.on(annotationMirror);
        if (!constraints.isEmpty()) {
          for (Verifier verifier : verifiers) {
            verifier.verify(element, annotationMirror, constraints);
          }
        }
      }
    }

    return false;
  }

}
