package requireconstructors;

import org.annotationconstraints.Constructor;
import org.annotationconstraints.TargetMustHaveConstructors;

@TargetMustHaveConstructors(@Constructor({}))
public @interface RequireNoArgConstructor {
}
