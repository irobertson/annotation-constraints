package requireconstructors;

import com.overstock.constraint.RequiredConstructor;
import com.overstock.constraint.TargetMustHaveConstructors;

@TargetMustHaveConstructors(@RequiredConstructor({String.class, long.class, int[].class}))
public @interface RequireStringLongIntArrayConstructor {
}
