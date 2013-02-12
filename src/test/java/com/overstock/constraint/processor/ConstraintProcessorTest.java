package com.overstock.constraint.processor;

import static com.overstock.constraint.processor.ConstraintProcessor.VerifierLoader;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableSet;
import com.overstock.constraint.Constraint;
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
  public void processConstraint() {
    final AnnotationMirror constraint = mockConstrained();

    TypeElement first = mock(TypeElement.class);
    TypeElement second = mock(TypeElement.class);

    Answer<List<? extends AnnotationMirror>> constraintAnswer = new Answer<List<? extends AnnotationMirror>>() {
      @Override
      public List<? extends AnnotationMirror> answer(InvocationOnMock invocation) throws Throwable {
        return Arrays.asList(constraint);
      }
    };
    when(elementUtils.getAllAnnotationMirrors(first)).thenAnswer(constraintAnswer);
    when(elementUtils.getAllAnnotationMirrors(second)).thenAnswer(constraintAnswer);

    final Set<TypeElement> typeElements = ImmutableSet.of(first, second);
    when(roundEnvironment.getRootElements()).thenAnswer(new Answer<Set<? extends Element>>() {
      @Override
      public Set<? extends Element> answer(InvocationOnMock invocation) throws Throwable {
        return typeElements;
      }
    });

    assertFalse(processor.process(Collections.<TypeElement>emptySet(), roundEnvironment));
    verify(verifier).init(processingEnvironment);
    verify(verifier).verify(same(first), same(constraint), any(Constraints.class));
    verify(verifier).verify(same(second), same(constraint), any(Constraints.class));
  }

  @Test
  public void processNoConstraints() {
    final AnnotationMirror nonConstraint = mockUnconstrained();

    TypeElement first = mock(TypeElement.class);
    TypeElement second = mock(TypeElement.class);

    Answer<List<? extends AnnotationMirror>> nonConstraintAnswer = new Answer<List<? extends AnnotationMirror>>() {
      @Override
      public List<? extends AnnotationMirror> answer(InvocationOnMock invocation) throws Throwable {
        return Arrays.asList(nonConstraint);
      }
    };
    when(elementUtils.getAllAnnotationMirrors(first)).thenAnswer(nonConstraintAnswer);
    when(elementUtils.getAllAnnotationMirrors(second)).thenAnswer(nonConstraintAnswer);

    final Set<TypeElement> typeElements = ImmutableSet.of(first, second);
    when(roundEnvironment.getRootElements()).thenAnswer(new Answer<Set<? extends Element>>() {
      @Override
      public Set<? extends Element> answer(InvocationOnMock invocation) throws Throwable {
        return typeElements;
      }
    });

    assertFalse(processor.process(typeElements, roundEnvironment));
    verify(verifier).init(processingEnvironment);
    verifyNoMoreInteractions(verifier);
  }

  private AnnotationMirror mockConstrained() {
    final AnnotationMirror constrained = mock(AnnotationMirror.class);
    DeclaredType declared = mock(DeclaredType.class);
    when(constrained.getAnnotationType()).thenReturn(declared);
    Element element = mock(Element.class);
    when(declared.asElement()).thenReturn(element);

    final AnnotationMirror constraining = mock(AnnotationMirror.class);
    when(element.getAnnotationMirrors()).thenAnswer(new Answer<List<? extends AnnotationMirror>>() {
      @Override
      public List<? extends AnnotationMirror> answer(InvocationOnMock invocation) throws Throwable {
        return Arrays.asList(constraining);
      }
    });
    DeclaredType constrainingType = mock(DeclaredType.class);
    when(constraining.getAnnotationType()).thenReturn(constrainingType);
    Element constrainingElement = mock(Element.class);
    when(constrainingType.asElement()).thenReturn(constrainingElement);

    final AnnotationMirror constraint = mock(AnnotationMirror.class);
    when(constrainingElement.getAnnotationMirrors()).thenAnswer(new Answer<List<? extends AnnotationMirror>>() {
      @Override
      public List<? extends AnnotationMirror> answer(InvocationOnMock invocation) throws Throwable {
        return Arrays.asList(constraint);
      }
    });
    DeclaredType constraintDeclaredType = mock(DeclaredType.class);
    when(constraint.getAnnotationType()).thenReturn(constraintDeclaredType);
    when(typeUtils.isSameType(constraintType, constraintDeclaredType)).thenReturn(true);

    return constrained;
  }

  private AnnotationMirror mockUnconstrained() {
    AnnotationMirror unconstrained = mock(AnnotationMirror.class);
    DeclaredType declared = mock(DeclaredType.class);
    when(unconstrained.getAnnotationType()).thenReturn(declared);
    Element element = mock(Element.class);
    when(declared.asElement()).thenReturn(element);
    return unconstrained;
  }

  @Test
  public void supportedSourceVersion() {
    assertSame(SourceVersion.latestSupported(), processor.getSupportedSourceVersion());
  }

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(processingEnvironment.getElementUtils()).thenReturn(elementUtils);
    when(processingEnvironment.getTypeUtils()).thenReturn(typeUtils);

    VerifierLoader loader = mock(VerifierLoader.class);
    when(loader.load(any(ClassLoader.class))).thenReturn(Arrays.asList(verifier));
    VerifierLoader.set(loader);

    TypeElement constraintsElement = mock(TypeElement.class);
    when(elementUtils.getTypeElement(Constraint.class.getCanonicalName())).thenReturn(constraintsElement);
    when(constraintsElement.asType()).thenReturn(constraintType);

    when(typeUtils.isSameType(constraintType, constraintType)).thenReturn(true);

    processor = new ConstraintProcessor();
    processor.init(processingEnvironment);
  }
}
