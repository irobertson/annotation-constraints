Annotation Constraints
======================
Requires Java 6 or greater.

Annotation Constraints allows you to specify constraints on annotated types which are verified at compile-time. For
example, the following `@Model` annotation can only be placed on a class which extends `AbstractModel` and has a
no-argument constructor.

```java
@TargetRequiresSupertypes(AbstractModel.class) //target must extend AbstractModel
@TargetRequiresConstructors(@RequiredConstructor({})) //target must have a no-arg constructor
@Target(ElementType.TYPE)
public @interface Model {
}
```

These constraints are validated at compile-time when `annotation-constraints` is on the compiler's classpath.
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

Compiling the `Person` class results in two compilation errors:
```
Class Person is annotated with @Model but does not have AbstractModel as a supertype
```
```
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
annotations which are not `@Inherited`).
* **@TargetRequiresConstructors** issues an error when an element is annotated with the target annotation and does not
have all of the required constructors with the necessary arguments types.
* **@TargetRequiresSupertypes** issues an error when an element is annotated with the target annotation and does not
have all of the required supertypes (classes and/or interfaces).

There is also one constrained annotation included.

* **@ServiceProvider** requires that the target type have a constructor with no arguments per `java.util.ServiceLoader`.

Adding constraints to existing annotations
======================
You may want to add a constraint to an annotation for which you don't control the source code. Here's how to do that.

1. Create a new annotation and add constraints to it.
1. Annotate your new annotation with `@ProvidesConstraintsFor(ExistingAnnotation.class)`.
1. To register your new annotation with annotation-constraints, create a text file named
`com.overstock.constraint.provider.constraint-providers` under `META-INF` with the fully-qualified binary class
name of your new annotation in it.
1. Make sure the `annotation-constraints` jar and your new annotation class are on the classpath during compilation.

TODO real-world example

See the JavaDoc for `com.overstock.constraint.provider.ProvidesConstraintsFor` for more details.

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

Eclipse with m2eclipse for Maven integration
======================
If you use Maven, the easiest way to use annotation-constraints within Eclipse is using m2eclipse and m2e-apt.

* Install m2eclipse from the Eclipse Marketplace.
* Install m2e-apt (from the Eclipse Marketplace or from the update site listed
[here](https://github.com/jbosstools/m2e-apt).
* Import your project or right-click and under Maven choose Update Project... and the m2e-apt configurator will
configure annotation processors based on the project's Maven classpath.

Eclipse usage
======================
If you're not using Maven you'll have to configure annotation processing in Eclipse by hand.

* Under the project's properties, go to **Java Compiler** -> **Annotation Processing** and check
"Enable project specific settings", "Enable annotation processing" and "Enable processing in editor".
* Under **Annotation Processing**, go to **Factory Path** and add the annotation-constraints jar via **Add JARs...**,
**Add External JARs** or **Add Variable...**.
* Also add any jars which contain additional constraints (custom constraints or `@ProvidesConstraintsFor`) along with
any jars which they depend on.