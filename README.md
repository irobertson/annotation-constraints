Annotation Constraints
======================
Requires Java 6 or greater.

Annotation Constraints allows you to specify constraints on annotated types which are verified at compile-time. For
example, the following `@Model` annotation can only be placed on a class which extends `AbstractModel` and has a
no-argument constructor.

```java
@TargetRequiresSupertypes(AbstractModel.class) //must extend AbstractModel
@TargetRequiresConstructors(@RequiredConstructor({})) //must have a no-arg constructor
@Target(ElementType.TYPE)
public @interface Model {
}
```

These constraints are violated at compile-time when `annotation-constraints` is on the compiler's classpath.
If you violate any of the constraints, you'll receive an error from the compiler. For example:

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
The following constraints are included in the `com.overstock.constraint` package. They can be combined with one another
and/or with your own custom constraints. The "target annotation" below refers to the annotation which is being
constrained (i.e. annotated with one or more of these constraint annotations).

* **@TargetDisallowsAnnotations** issues an error when an element is annotated with both the target annotation and any
of the disallowed annotations. This is a way of specifying that the target annotation is not compatible with one or more
other annotations.
* **@TargetRecommendsAnnotations** issues a warning when an element is annotated with the target annotation and not with
one of the recommended annotations.
* **@TargetRequiresAnnotations** issues an error when an element is annotated with the target annotation and not with
one of the required annotations.
* **@TargetRequiresAnnotationsOnSupertype** same as `@TargetRequiresAnnotations` except it checks supertypes (i.e. for
annotations which are not @Inherited).
* **@TargetRequiresConstructors** issues an error when an element is annotated with the target annotation and does not
have all of the required constructors with the necessary arguments types.
* **@TargetRequiresSupertypes** issues an error when an element is annotated with the target annotation and does not
have all of the required supertypes (classes and/or interfaces).

Adding constraints to existing annotations
======================
You may want to add a constraint to an annotation for which you don't control the source code. Here's how to do that.

1. Create a new annotation and add constraints to it.
1. Annotate your new annotation with `@ProvidesConstraintsFor([existing annotation].class)`.
1. To register your new annotation with annotation-constraints, create a text file named
`com.overstock.constraint.provider.constraint-providers` under `META-INF` with the fully-qualified binary class
name of your new annotation in it. (See the JavaDoc for `com.overstock.constraint.provider.ProvidesConstraintsFor` for
more details.)
1. Make sure the `annotation-constraints` jar and your new annotation class are on the classpath during compilation.

TODO real-world example

Writing your own constraint
======================
1. Create an annotation and add `@Constraint` to it.
1. Implement a `Verifier` for your new constraint.
1. To register your new `Verifier` with annotation-constraints, create a text file named
`com.overstock.constraint.verifier.Verifier` under `META-INF/services/` with the fully-qualified binary class name of
your verifier in it. (See the JavaDoc for `com.overstock.constraint.verifier.Verifier` for more details.)
1. Make sure `annotation-constraints` and your new `Verifier` class are on the classpath during compilation.

TODO real-world example

Maven usage
======================
`annotation-constraints` runs as an annotation processor, which happens automatically when it's on the classpath at
compile-time (for Java 6 and greater). No extra configuration is necessary other than declaring a dependency on
`annotation-constraints`.

```xml
  <dependencies>
    ...
    <dependency>
      <groupId>com.overstock</groupId>
      <artifactId>annotation-constraints</artifactId>
      <version>${annotation-constraints.version}</version>
    </dependency>
    ...
  </dependencies>
```

TODO Eclipse usage, Eclipse and Maven usage with m2e-apt