package requireconstructors;

import java.util.concurrent.Callable;

import com.overstock.constraint.RequiredConstructor;
import com.overstock.constraint.TargetMustHaveConstructors;

@TargetMustHaveConstructors(@RequiredConstructor(Callable.class))
public @interface RequireCallableConstructor {
}
