package com.overstock.constraint.verifier;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * A convenience builder for verification messages. Verifiers are recommended (but not required) to use this class so
 * that messages are formatted in a similar fashion.
 */
public final class MessageBuilder {

  private Diagnostic.Kind kind;

  private VerificationContext context;

  private AnnotationValue value;

  private StringBuilder text = new StringBuilder();

  public MessageBuilder(Diagnostic.Kind kind, VerificationContext context) {
    if (kind == null) {
      throw new NullPointerException("Kind cannot be null");
    }
    this.kind = kind;
    if (context == null) {
      throw new NullPointerException("Context cannot be null");
    }
    this.context = context;
  }

  /**
   * Creates a {@link MessageBuilder} with common formatting based on the context.
   *
   * @param kind the {@link Diagnostic.Kind}, which cannot be {@code null}
   * @param context the context for verification
   */
  public static MessageBuilder format(Diagnostic.Kind kind, VerificationContext context) {
    return new MessageBuilder(kind, context).appendText(context.getElement())
      .appendText(" is annotated with @")
      .appendSimpleName(context.getAnnotation());
  }

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
    appendText(context.getTypeUtils().asElement(typeMirror).getSimpleName());
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
    if (context.getConstraint().isProvided()) {
      appendText(" as specified by ").appendText(context.getConstraint().getProvider());
    }
    context.getMessager().printMessage(kind, text, context.getElement(), context.getAnnotation(), value);
  }
}
