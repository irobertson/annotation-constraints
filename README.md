Annotation Constraints
======================

Annotation Constraints allows you to specify constraints on annotated types which are verified at compile-time. For
example, the following `Model` annotation can only be placed on a class which extends `AbstractModel` and has a no-arg
constructor.

```java
@RequireSupertypes(AbstractModel.class)
@RequireConstructors(@RequiredConstructor({}))
@Target(ElementType.TYPE)
public @interface Model {
}
```

These constraints are violated at compile-time when annotation-constraints is on the compiler's classpath.
(See instructions for Maven below.)
If you violate any of the constraints, you'll receive an error from the compiler.

```java
@Model
public class Person {

  private final String name;

  public Person(String name) {
    this.name = name;
  }
}
```

This results in two compilation errors:
```
Class Person is annotated with @Model but does not have AbstractModel as a supertype

Class Person is annotated with @Model but does not have a constructor with no arguments
```
Out of the box
======================
The following constraints are included in the com.overstock.constraint package. They can be combined with one another
and/or with your own custom constraints. The "target annotation" below refers to the annotation which is being
constrained (i.e. annotated with one or more of these annotation).

* @DisallowAnnotations - issues an error when an element is annotated with both the target annotation and any of the
disallowed annotations.
* @RecommendAnnotations - issues a warning when an element is annotated with the target annotation and not with one of
the recommended annotations.
* @RequireAnnotations - issues an error when an element is annotated with the target annotation and not with one of the
required annotations.
* @RequireAnnotationsOnSupertype - same as @RequireAnnotations except it checks supertypes (for annotations whicha are
not @Inherited). TODO should we merge @RequireAnnotationsOnSupertype behavior into @RequireAnnotations?
* @RequireConstructors - issues an error when an element is annotated with the target annotation and does not have all
of the required constructors with the necessary arguments types.
* @RequireSupertypes - issues an error when an element is annotated witht the target annotation and does not have all of
the required supertypes (classes and/or interfaces).

Writing your own constraint
======================
1. Create an annotation and add @Constraint to it.
1. Implement a Verifier for your new constraint.
1. To register your new Verifier with annotation-constraints, create a text file named
`com.overstock.constraint.verifier.Verifier` under `META-INF/services/` with the fully-qualified binary class name of
your verifier in it. (See the JavaDoc for com.overstock.constraint.verifier.Verifier for more details.)
1. Make sure annotation-constraints and your new Verifier are on the classpath during compilation. (See instructions for
Maven below.)

Maven usage
======================
TODO: Maven pom snippet