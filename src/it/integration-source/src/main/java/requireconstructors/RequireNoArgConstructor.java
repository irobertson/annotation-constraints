package requireconstructors;

import com.overstock.constraint.RequiredConstructor;
import com.overstock.constraint.TargetRequiresConstructors;

@TargetRequiresConstructors(@RequiredConstructor({}))
public @interface RequireNoArgConstructor {
}
