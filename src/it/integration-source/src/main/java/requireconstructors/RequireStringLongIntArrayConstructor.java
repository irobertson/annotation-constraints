package requireconstructors;

import com.overstock.constraint.Constructor;
import com.overstock.constraint.TargetMustHaveConstructors;

@TargetMustHaveConstructors(@Constructor({String.class, long.class, int[].class}))
public @interface RequireStringLongIntArrayConstructor {
}
