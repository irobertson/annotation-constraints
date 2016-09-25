package org.annotationconstraints.verifier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import org.annotationconstraints.processor.ConstraintMirror;

/**
 * A convenience builder for verification messages. Verifiers are recommended (but not required) to use this class so
 * that messages are formatted in a similar fashion.
 */
public final class MessageBuilder {

  private final Diagnostic.Kind kind;

  private final ProcessingEnvironment processingEnv;

  private AnnotationValue value;

  private final Element element;

  private final AnnotationMirror annotationMirror;

  private final ConstraintMirror constraintMirror;

  private final StringBuilder text = new StringBuilder();


  /**
   * Constructs a {@link MessageBuilder}.
   *
   * @param kind the {@link Diagnostic.Kind}, which cannot be {@code null}
   * @param processingEnv the current ProcessingEnvironment
   * @param element the annotated element
   * @param annotationMirror a mirror for the annotation on {@code element}
   * @param constraintMirror a mirror for the constraint annotation on the annotation mirrored by
   *   {@code AnnotationMirror}
   */
  public MessageBuilder(
      Diagnostic.Kind kind,
      ProcessingEnvironment processingEnv,
      Element element,
      AnnotationMirror annotationMirror,
      ConstraintMirror constraintMirror) {
    this.kind = verifyNotNull(kind, Diagnostic.Kind.class);
    this.processingEnv = verifyNotNull(processingEnv, ProcessingEnvironment.class);
    this.element = verifyNotNull(element, Element.class);
    this.annotationMirror = verifyNotNull(annotationMirror, AnnotationMirror.class);
    this.constraintMirror = verifyNotNull(constraintMirror, ConstraintMirror.class);
  }

  private static <T> T verifyNotNull(T value, Class<T> clazz) {
    if (value == null) {
      throw new NullPointerException(clazz.getSimpleName() + " cannot be null");
    }
    else {
      return value;
    }
  }

  /**
   * Creates a {@link MessageBuilder} with common formatting based on the context.
   *
   * @param kind the {@link Diagnostic.Kind}, which cannot be {@code null}
   * @param processingEnv the current ProcessingEnvironment
   * @param element the annotated element
   * @param annotationMirror a mirror for the annotation on {@code element}
   * @param constraintMirror a mirror for the constraint annotation on the annotation mirrored by
   *   {@code AnnotationMirror}
   * @return a new MessageBuilder instance.
   */
  public static MessageBuilder format(
      Diagnostic.Kind kind,
      ProcessingEnvironment processingEnv,
      Element element,
      AnnotationMirror annotationMirror,
      ConstraintMirror constraintMirror) {
    return new MessageBuilder(kind, processingEnv, element, annotationMirror, constraintMirror)
      .appendText(element)
      .appendText(" is annotated with @")
      .appendSimpleName(annotationMirror);
  }

  /**
   * Set the particular field of the annotation which this message should be applied to. By default, messages
   * only apply to the annotation as a whole.
   * @param value the particular field of the annotation which this message should be applied to
   * @return {@code this}
   */
  public MessageBuilder setValue(AnnotationValue value) {
    this.value = value;
    return this;
  }

  public MessageBuilder appendText(String s) {
    text.append(s);
    return this;
  }

  public MessageBuilder appendText(Object object) {
    text.append(object);
    return this;
  }

  public MessageBuilder appendSimpleName(AnnotationMirror annotationMirror) {
    text.append(annotationMirror.getAnnotationType().asElement().getSimpleName());
    return this;
  }

  public MessageBuilder appendSimpleName(TypeMirror typeMirror) {
    appendText(processingEnv.getTypeUtils().asElement(typeMirror).getSimpleName());
    return this;
  }

  public MessageBuilder appendAnnotations(Iterable<TypeMirror> annotations, String delimiter) {
    return appendTypes(annotations, "@", delimiter);
  }

  public MessageBuilder appendTypes(Iterable<TypeMirror> annotations, String typePrefix, String delimiter) {
    boolean first = true;
    for (TypeMirror annotation : annotations) {
      if (!first) {
        appendText(delimiter);
      }
      appendText(typePrefix).appendSimpleName(annotation);
      first = false;
    }
    return this;
  }

  public void print() {
    if (constraintMirror.isProvided()) {
      appendText(" as specified by ").appendText(constraintMirror.getProvider());
    }
    processingEnv.getMessager().printMessage(kind, text, element, annotationMirror, value);
  }
}
