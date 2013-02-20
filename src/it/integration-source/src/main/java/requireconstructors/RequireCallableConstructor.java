package requireconstructors;

import java.util.concurrent.Callable;

import com.overstock.constraint.RequiredConstructor;
import com.overstock.constraint.TargetRequiresConstructors;

@TargetRequiresConstructors(@RequiredConstructor(Callable.class))
public @interface RequireCallableConstructor {
}
