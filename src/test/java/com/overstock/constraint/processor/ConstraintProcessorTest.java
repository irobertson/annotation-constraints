package com.overstock.constraint.processor;

import static com.overstock.constraint.processor.ConstraintProcessor.ConstraintProviderLoader;
import static com.overstock.constraint.processor.ConstraintProcessor.VerifierLoader;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableSet;
import com.overstock.constraint.Constraint;
import com.overstock.constraint.provider.ConstraintProvider;
import com.overstock.constraint.provider.ConstraintsFor;
import com.overstock.constraint.TargetRequiresAnnotations;
import com.overstock.constraint.verifier.Verifier;

public class ConstraintProcessorTest {

  @Mock
  private ProcessingEnvironment processingEnvironment;

  @Mock
  private RoundEnvironment roundEnvironment;

  @Mock
  private Elements elementUtils;

  @Mock
  private Types typeUtils;

  @Mock
  private TypeMirror constraintType;

  @Mock
  private Verifier verifier;

  private ConstraintProcessor processor;

  @Test
  public void processAdditionalConstraints() {
    //TODO test constraints from same compilation unit as well
    ConstraintProvider externalConstraintProvider = new ExternalConstraintProvider();
    ConstraintProviderLoader constraintsLoader = mock(ConstraintProviderLoader.class);
    when(constraintsLoader.load(any(ClassLoader.class))).thenReturn(Arrays.asList(externalConstraintProvider));
    ConstraintProviderLoader.set(constraintsLoader);

    processor.init(processingEnvironment);

    AnnotationMirror externallyConstrained = mock(AnnotationMirror.class, "externallyConstrained");
    DeclaredType externallyConstrainedType = mock(DeclaredType.class, "externallyConstrainedType");
    when(externallyConstrained.getAnnotationType()).thenReturn(externallyConstrainedType);
    Element externallyConstrainedElement = mock(Element.class, "externallyConstrainedElement");
    when(externallyConstrainedType.asElement()).thenReturn(externallyConstrainedElement);
    when(externallyConstrainedElement.asType()).thenReturn(externallyConstrainedType);

    TypeElement entityElement = mock(TypeElement.class, "entityElement");
    when(elementUtils.getTypeElement(Entity.class.getCanonicalName())).thenReturn(entityElement);
    TypeMirror entityType = mock(TypeMirror.class, "entityType");
    when(entityElement.asType()).thenReturn(entityType);

    when(typeUtils.isSameType(entityType, externallyConstrainedType)).thenReturn(true);
    when(typeUtils.isSameType(externallyConstrainedType, entityType)).thenReturn(true);

    TypeElement providerElement = mock(TypeElement.class, "providerElement");
    when(elementUtils.getTypeElement(ExternalConstraintProvider.class.getCanonicalName())).thenReturn(providerElement);
    mockUnconstrained(providerElement);

    TypeElement proxyElement = mock(TypeElement.class, "proxyElement");
    when(elementUtils.getTypeElement(EntityProxy.class.getCanonicalName())).thenReturn(proxyElement);
    mockConstrained(proxyElement);

    TypeElement annotated = mock(TypeElement.class, "annotated");
    mockGetAnnotationMirrors(annotated, externallyConstrained);

    mockRootElements(annotated);

    assertFalse(processor.process(Collections.<TypeElement>emptySet(), roundEnvironment));
    verify(verifier).init(processingEnvironment);
    verify(verifier).verify(same(annotated), same(externallyConstrained), any(Constraints.class));
  }

  @Test
  public void processConstraints() {
    processor.init(processingEnvironment);

    TypeElement first = mock(TypeElement.class);
    TypeElement second = mock(TypeElement.class);
    AnnotationMirror constrained = mockConstrained(first, second);

    TypeElement notAnnotated = mock(TypeElement.class);
    mockNotAnnotated(notAnnotated);
    TypeElement unconstrained = mock(TypeElement.class);
    mockUnconstrained(unconstrained);

    mockRootElements(first, second, notAnnotated, unconstrained);

    assertFalse(processor.process(Collections.<TypeElement>emptySet(), roundEnvironment));
    verify(verifier).init(processingEnvironment);
    verify(verifier).verify(same(first), same(constrained), any(Constraints.class));
    verify(verifier).verify(same(second), same(constrained), any(Constraints.class));
    verify(verifier, never()).verify(same(notAnnotated), any(AnnotationMirror.class), any(Constraints.class));
    verify(verifier, never()).verify(same(unconstrained), any(AnnotationMirror.class), any(Constraints.class));
  }

  @Test
  public void processNoConstraints() {
    processor.init(processingEnvironment);

    TypeElement first = mock(TypeElement.class);
    TypeElement second = mock(TypeElement.class);
    mockUnconstrained(first, second);

    mockRootElements(first, second);

    assertFalse(processor.process(Collections.<TypeElement>emptySet(), roundEnvironment));
    verify(verifier).init(processingEnvironment);
    verifyNoMoreInteractions(verifier);
  }

  @Test
  public void supportedSourceVersion() {
    processor.init(processingEnvironment);
    assertSame(SourceVersion.latestSupported(), processor.getSupportedSourceVersion());
  }

  private void mockRootElements(final TypeElement... elements) {
    when(roundEnvironment.getRootElements()).thenAnswer(new Answer<Set<? extends Element>>() {
      @Override
      public Set<? extends Element> answer(InvocationOnMock invocation) throws Throwable {
        return ImmutableSet.<TypeElement>builder().add(elements).build();
      }
    });
  }

  private void mockNotAnnotated(TypeElement... elements) {
    for (TypeElement element : elements) {
      mockGetAnnotationMirrors(element);
    }
  }

  private AnnotationMirror mockConstrained(TypeElement... elements) {
    final AnnotationMirror constrained = mockConstrained();
    for (TypeElement element : elements) {
      mockGetAnnotationMirrors(element, constrained);
    }
    return constrained;
  }

  private void mockGetAnnotationMirrors(Element element, final AnnotationMirror... annotationMirrors) {
    Answer<List<? extends AnnotationMirror>> constrainedAnswer = new Answer<List<? extends AnnotationMirror>>() {
      @Override
      public List<? extends AnnotationMirror> answer(InvocationOnMock invocation) throws Throwable {
        return Arrays.asList(annotationMirrors);
      }
    };
    when(element.getAnnotationMirrors()).thenAnswer(constrainedAnswer);
    when(elementUtils.getAllAnnotationMirrors(element)).thenAnswer(constrainedAnswer);
  }

  private AnnotationMirror mockConstrained() {
    final AnnotationMirror constrained = mock(AnnotationMirror.class, "constrained");
    DeclaredType declared = mock(DeclaredType.class, "constrainedDeclared");
    when(constrained.getAnnotationType()).thenReturn(declared);
    Element element = mock(Element.class, "constrainedElement");
    when(declared.asElement()).thenReturn(element);

    final AnnotationMirror constraining = mock(AnnotationMirror.class, "constraining");
    mockGetAnnotationMirrors(element, constraining);
    DeclaredType constrainingType = mock(DeclaredType.class, "constrainingType");
    when(constraining.getAnnotationType()).thenReturn(constrainingType);
    Element constrainingElement = mock(Element.class, "constrainingElement");
    when(constrainingType.asElement()).thenReturn(constrainingElement);

    final AnnotationMirror constraint = mock(AnnotationMirror.class, "constraint");
    mockGetAnnotationMirrors(constrainingElement, constraint);
    DeclaredType constraintDeclaredType = mock(DeclaredType.class, "constraintDeclared");
    when(constraint.getAnnotationType()).thenReturn(constraintDeclaredType);
    when(typeUtils.isSameType(constraintType, constraintDeclaredType)).thenReturn(true);
    when(typeUtils.isSameType(constraintDeclaredType, constraintType)).thenReturn(true);

    return constrained;
  }

  private AnnotationMirror mockUnconstrained(TypeElement... elements) {
    final AnnotationMirror unconstrained = mockUnconstrained();
    for (TypeElement element : elements) {
      mockGetAnnotationMirrors(element, unconstrained);
    }
    return unconstrained;
  }

  private AnnotationMirror mockUnconstrained() {
    AnnotationMirror unconstrained = mock(AnnotationMirror.class);
    DeclaredType declared = mock(DeclaredType.class);
    when(unconstrained.getAnnotationType()).thenReturn(declared);
    Element element = mock(Element.class);
    when(declared.asElement()).thenReturn(element);
    return unconstrained;
  }

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(processingEnvironment.getElementUtils()).thenReturn(elementUtils);
    when(processingEnvironment.getTypeUtils()).thenReturn(typeUtils);

    ConstraintProviderLoader constraintsLoader = mock(ConstraintProviderLoader.class);
    when(constraintsLoader.load(any(ClassLoader.class))).thenReturn(Collections.<ConstraintProvider>emptyList());
    ConstraintProviderLoader.set(constraintsLoader);

    VerifierLoader verifierLoader = mock(VerifierLoader.class);
    when(verifierLoader.load(any(ClassLoader.class))).thenReturn(Arrays.asList(verifier));
    VerifierLoader.set(verifierLoader);

    TypeElement constraintsElement = mock(TypeElement.class, "constraintsElement");
    when(elementUtils.getTypeElement(Constraint.class.getCanonicalName())).thenReturn(constraintsElement);
    when(constraintsElement.asType()).thenReturn(constraintType);

    when(typeUtils.isSameType(constraintType, constraintType)).thenReturn(true);

    processor = new ConstraintProcessor();
  }

  @ConstraintsFor(annotation = Entity.class, canBeFoundOn = EntityProxy.class)
  public static class ExternalConstraintProvider implements ConstraintProvider {
  }

  @TargetRequiresAnnotations(Table.class)
  public static @interface EntityProxy {}
}
