package requireconstructors;

import com.overstock.constraint.Constructor;
import com.overstock.constraint.TargetMustHaveConstructors;

@TargetMustHaveConstructors(@Constructor({}))
public @interface RequireNoArgConstructor {
}
