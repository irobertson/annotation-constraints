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
import javax.lang.model.util.Elements;

import com.overstock.constraint.provider.ConstraintProvider;
import com.overstock.constraint.verifier.Verifier;

@SupportedAnnotationTypes("*")
public class ConstraintProcessor extends AbstractProcessor {

  private Iterable<Verifier> verifiers;

  private Iterable<ConstraintProvider> constraintProviders;

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    ClassLoader classLoader = getClass().getClassLoader();
    verifiers = VerifierLoader.loadAll(classLoader);
    for (Verifier verifier : verifiers) {
      verifier.init(processingEnv);
    }
    constraintProviders = ConstraintProviderLoader.loadAll(classLoader);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Elements elementUtils = processingEnv.getElementUtils();
    for (Element element : roundEnv.getRootElements()) {
      for (AnnotationMirror annotationMirror : elementUtils.getAllAnnotationMirrors(element)) {
        Constraints constraints = Constraints.on(annotationMirror, constraintProviders, processingEnv);
        if (!constraints.isEmpty()) {
          for (Verifier verifier : verifiers) {
            verifier.verify(element, annotationMirror, constraints);
          }
        }
      }
    }

    return false;
  }

  static class VerifierLoader {
    private static VerifierLoader INSTANCE = new VerifierLoader();

    public Iterable<Verifier> load(ClassLoader classLoader) {
      return ServiceLoader.load(Verifier.class, classLoader);
    }

    public static Iterable<Verifier> loadAll(ClassLoader classLoader) {
      return INSTANCE.load(classLoader);
    }

    static void set(VerifierLoader loader) { //for testing
      INSTANCE = loader;
    }
  }

  static class ConstraintProviderLoader {
    private static ConstraintProviderLoader INSTANCE = new ConstraintProviderLoader();

    public Iterable<ConstraintProvider> load(ClassLoader classLoader) {
      return ServiceLoader.load(ConstraintProvider.class, classLoader);
    }

    public static Iterable<ConstraintProvider> loadAll(ClassLoader classLoader) {
      return INSTANCE.load(classLoader);
    }

    static void set(ConstraintProviderLoader loader) { //for testing
      INSTANCE = loader;
    }
  }

}
