package provider;

import javax.persistence.Table;

import com.overstock.constraint.provider.ConstraintProvider;
import com.overstock.constraint.provider.ConstraintsFor;

@ConstraintsFor(annotation = Table.class, canBeFoundOn = TableProxy.class)
public class InternalProvider implements ConstraintProvider {
}
