package requireconstructors;

import com.overstock.constraint.RequiredConstructor;
import com.overstock.constraint.TargetRequiresConstructors;

@TargetRequiresConstructors(@RequiredConstructor({String.class, long.class, int[].class}))
public @interface RequireStringLongIntArrayConstructor {
}
