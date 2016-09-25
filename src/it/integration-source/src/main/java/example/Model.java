package example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import org.annotationconstraints.Constructor;
import org.annotationconstraints.TargetMustHaveConstructors;
import org.annotationconstraints.TargetMustHaveSupertypes;

@TargetMustHaveSupertypes(AbstractModel.class)
@TargetMustHaveConstructors(@Constructor({}))
@Target(ElementType.TYPE)
public @interface Model {
}
