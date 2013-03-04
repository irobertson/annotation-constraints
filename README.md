# annotation-constraints

Intended audience: Java (6+) developers who write annotations.

You've probably come across Java annotations which are constrained in some way (e.g. require a no argument constructor
on the annotated class, or are incompatible with one ore more other annotations), but most of the time the constraints
are only mentioned in the annotation's JavaDoc and enforced at runtime. However, most of these constraints could be
verified at compile-time if there was a way to express them, and odds are that you'd prefer compile-time errors to
runtime ones since you are already using Java.

**annotation-constraints** is a library for Java 6 or newer that allows you to specify constraints on annotations which
are verified at compile-time via the included annotation processor. It includes commonly-used constraint
meta-annotations and allows you to create your own. Additionally, it allows you to add constraints to existing (e.g.
third-party) annotations.

For example, suppose you had a `@Model` annotation which should only be placed on a class which extends `AbstractModel`
and has a no-argument constructor. You could add some constraint meta-annotations to it like so:

```java
@TargetRequiresSupertypes(AbstractModel.class) //target must extend AbstractModel
@TargetRequiresConstructors(@RequiredConstructor({})) //target must have a no-arg constructor
@Target(ElementType.TYPE)
public @interface Model {
}
```

These constraints are validated at compile-time when **annotation-constraints** is on the compiler's classpath. No
configuration is necessary because it includes an annotation processor which is picked up automatically by javac
(see below for Eclipse usage). If you violate any of the constraints, you'll receive an error. For example:

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
## Out of the box

The following constraints are included in the `com.overstock.constraint` package. They can be combined with one another
and/or with your own custom constraints. The phrase _target annotation_ below refers to the annotation which is being
constrained (i.e. annotated with one or more of these constraint meta-annotations). In the example above, `@Model` is
the _target annotation_ because it is annotated with `@TargetRequiresSupertypes` and `@TargetRequiresConstructors`. The
phrase _target element_ below refers to the program element which is annotated with the _target annotation_, e.g.
`Person` above.

* **@TargetDisallowsAnnotations(Class<? extends Annotation[])** issues an error when the _target element_ is annotated with both
the _target annotation_ and any of the incompatible annotations. This is a way of specifying that the target annotation is
not compatible with the specified annotations.
* **@TargetRecommendsAnnotations(Class<? extends Annotation[])** issues a warning when the _target element_ is annotated with
the _target annotation_ and not with all of the specified annotations.
* **@TargetRequiresAnnotations(Class<? extends Annotation[])** issues an error when the _target element_ is annotated with the
_target annotation_ and not with all of the specified annotations.
* **@TargetRequiresAnnotationsOnSupertype(Class<? extends Annotation[])** is the same as
`@TargetRequiresAnnotations` except it checks supertypes (i.e. for annotations which are not `@Inherited`).
* **@TargetRequiresConstructors(RequiredConstructor[])** issues an error when the _target element_ is annotated with the
_target annotation_ and does not have all of the required constructors with the necessary arguments types.
* **@TargetRequiresSupertypes(Class<?>[])** issues an error when an _target element_ is annotated with the _target annotation_
and does not have all of the required supertypes (classes and/or interfaces).

There is also one constrained annotation included.

* **@NewInstanceConstructible** requires that the target type have a constructor with no arguments so that it can be
constructed via `Class.newInstance()`.

## Adding constraint meta-annotations to existing annotations

You may want to add a constraint meta-annotation to some annotation for which you don't control the source code.
Here's how to do just that.

1. Create a new annotation and add constraint meta-annotations to it.
1. Annotate your new annotation with `@ProvidesConstraintsFor(ExistingAnnotation.class)`.
1. To register your new annotation with **annotation-constraints**, create a text file named
`com.overstock.constraint.provider.constraint-providers` under `META-INF` with the fully-qualified binary class
name of your new annotation in it. Without this file, the constraints will only be validated in the compilation unit in
which they're defined.
1. Make sure the **annotation-constraints** jar and your new annotation class are on the classpath during compilation.

See the JavaDoc for [com.overstock.constraint.provider.ProvidesConstraintsFor](https://github.com/overstock/annotation-constraints/blob/master/src/main/java/com/overstock/constraint/provider/ProvidesConstraintsFor.java) for more details.

### Example of adding constraints to an existing annotation

For example, JAX-RS (JSR 311) has `@ApplicationPath`, which is required to only be applied to a subclass of
`Application`. To have this validated at compile-time we would do the following.

First, create an annotation on which to put constraints. The name or location of this annotation doesn't really
matter, so let's call it [ApplicationPathConstraints](https://github.com/overstock/annotation-constraints/blob/master/src/it/integration-source/src/main/java/example/ApplicationPathConstraints.java):

```java
package example;

import ...

@Target({}) //this annotation is not intended to be placed on any program element
@Retention(RetentionPolicy.RUNTIME)
@ProvidesConstraintsFor(ApplicationPath.class)
@TargetRequiresSupertypes(Application.class)
public @interface ApplicationPathConstraints {
}
```

Next, create a text file named [META-INF/com.overstock.constraint.provider.constraint-providers](https://github.com/overstock/annotation-constraints/blob/master/src/it/integration-source/src/main/resources/META-INF/com.overstock.constraint.provider.constraint-providers)
with the following line of text:

```
 example.ApplicationPathProvider
```

That's it. As long as these files are in the current compilation unit or on the classpath during compilation, the
validation will occur at compile-time.

## Writing your own constraint meta-annotation

If you need a constraint which is not provided, you can write your own meta-annotation and a `Verifier` for it.
Though there is some overlap, we think writing a `Verifier` is easier than writing an annotation processor from scratch.

1. Create an annotation and add `@Constraint(verifiedBy = ...)` to it.
1. Implement the `Verifier` for your new constraint.
(See the JavaDoc for [com.overstock.constraint.verifier.Verifier](https://github.com/overstock/annotation-constraints/blob/master/src/main/java/com/overstock/constraint/verifier/Verifier.java)
for more details and/or have a look at [an example Verifier](https://github.com/overstock/annotation-constraints/blob/master/src/main/java/com/overstock/constraint/verifier/DisallowAnnotationsVerifier.java).)
1. Make sure both **annotation-constraints** and your new `Verifier` class are on the classpath during compilation.

Note: Custom `Verifier`s cannot be executed in the same compilation unit in which they are declared (which makes sense
because they have yet to be compiled).

### Example of writing your own constraint

For example, JAX-RS (JSR 311) introduced annotations for some common HTTP methods, namely `@GET`, `@PUT`, `@POST`,
`@HEAD` and `@DELETE`, which are all annotated with `@HttpMethod`. The JavaDoc for `@HttpMethod` states, "It is an error
for a method to be annotated with more than one annotation that is annotated with `@HttpMethod`". To have this validated
at compile-time instead of runtime, we could create a new constraint, say
`@TargetHasAtMostOneHttpMethod(verifiedBy = HttpMethodVerifier.class)`, implement `HttpMethodVerifier` which does the
necessary validation and apply this constraint to `@HttpMethod` via `@ProvidesConstraintsFor(HttpMethod.class)`.

TODO write up the example and link to it

## Maven usage

**annotation-constraints** runs as an annotation processor, which happens automatically when it's on the classpath at
compile-time (for Java 6 and greater). No extra configuration is necessary other than declaring a dependency on
**annotation-constraints**.

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

## Eclipse with m2eclipse for Maven integration

If you use Maven, the easiest way to use **annotation-constraints** within Eclipse is using m2eclipse and m2e-apt.

* Install m2eclipse from the Eclipse Marketplace.
* Install m2e-apt (from the Eclipse Marketplace or from the update site listed
[here](https://github.com/jbosstools/m2e-apt).
* Import your project or right-click and under Maven choose Update Project... and the m2e-apt configurator will
configure annotation processors based on the project's Maven classpath.

## Eclipse without Maven

If you're not using Maven you'll have to configure annotation processing in Eclipse by hand.

* Under the project's properties, go to **Java Compiler** -> **Annotation Processing** and check
"Enable project specific settings", "Enable annotation processing" and "Enable processing in editor".
* Under **Annotation Processing**, go to **Factory Path** and add the **annotation-constraints** jar via
**Add JARs...**, **Add External JARs** or **Add Variable...**.
* Also add any jars which contain additional constraints (custom constraints or `@ProvidesConstraintsFor`) along with
any jars which they depend on.