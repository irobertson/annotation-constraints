package com.overstock.constraint.processor;

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
  public void processConstraints() {
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
    Answer<List<? extends AnnotationMirror>> notAnnotatedAnswer = new Answer<List<? extends AnnotationMirror>>() {
      @Override
      public List<? extends AnnotationMirror> answer(InvocationOnMock invocation) throws Throwable {
        return Collections.emptyList();
      }
    };
    for (TypeElement element : elements) {
      when(element.getAnnotationMirrors()).thenAnswer(notAnnotatedAnswer);
      when(elementUtils.getAllAnnotationMirrors(element)).thenAnswer(notAnnotatedAnswer);
    }
  }

  private AnnotationMirror mockConstrained(TypeElement... elements) {
    final AnnotationMirror constrained = mockConstrained();
    Answer<List<? extends AnnotationMirror>> constrainedAnswer = new Answer<List<? extends AnnotationMirror>>() {
      @Override
      public List<? extends AnnotationMirror> answer(InvocationOnMock invocation) throws Throwable {
        return Arrays.asList(constrained);
      }
    };
    for (TypeElement element : elements) {
      when(element.getAnnotationMirrors()).thenAnswer(constrainedAnswer);
      when(elementUtils.getAllAnnotationMirrors(element)).thenAnswer(constrainedAnswer);
    }
    return constrained;
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

  private AnnotationMirror mockUnconstrained(TypeElement... elements) {
    final AnnotationMirror unconstrained = mockUnconstrained();
    Answer<List<? extends AnnotationMirror>> unconstrainedAnswer = new Answer<List<? extends AnnotationMirror>>() {
      @Override
      public List<? extends AnnotationMirror> answer(InvocationOnMock invocation) throws Throwable {
        return Arrays.asList(unconstrained);
      }
    };
    for (TypeElement element : elements) {
      when(element.getAnnotationMirrors()).thenAnswer(unconstrainedAnswer);
      when(elementUtils.getAllAnnotationMirrors(element)).thenAnswer(unconstrainedAnswer);
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
