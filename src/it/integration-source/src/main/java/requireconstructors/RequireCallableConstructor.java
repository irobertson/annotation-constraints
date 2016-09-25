package requireconstructors;

import java.util.concurrent.Callable;

import org.annotationconstraints.Constructor;
import org.annotationconstraints.TargetMustHaveConstructors;

@TargetMustHaveConstructors(@Constructor(Callable.class))
public @interface RequireCallableConstructor {
}
