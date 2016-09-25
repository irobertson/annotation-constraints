package requireconstructors;

import org.annotationconstraints.Constructor;
import org.annotationconstraints.TargetMustHaveConstructors;

@TargetMustHaveConstructors(@Constructor({String.class, long.class, int[].class}))
public @interface RequireStringLongIntArrayConstructor {
}
