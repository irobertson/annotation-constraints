package requireconstructors;

import com.overstock.constraint.RequiredConstructor;
import com.overstock.constraint.TargetMustHaveConstructors;

@TargetMustHaveConstructors(@RequiredConstructor({}))
public @interface RequireNoArgConstructor {
}
