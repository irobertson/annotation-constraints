package requireconstructors;

import java.util.concurrent.Callable;

import com.overstock.constraint.Constructor;
import com.overstock.constraint.TargetMustHaveConstructors;

@TargetMustHaveConstructors(@Constructor(Callable.class))
public @interface RequireCallableConstructor {
}
