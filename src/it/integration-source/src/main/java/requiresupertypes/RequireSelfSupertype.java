package requiresupertypes;

import org.annotationconstraints.TargetMustHaveSupertypes;

@TargetMustHaveSupertypes(RequireSelfSupertypePass.class)
public @interface RequireSelfSupertype {
}
