![example workflow](https://github.com/petros-team/bring/actions/workflows/maven.yml/badge.svg)
## <span style="color:green">Petros Bring Project</span>
Bring project is an implementation of the Dependency Injection pattern that is a more specific
version of the Inversion of Control pattern. Dependency Injection is a technique in which an object
receives other objects that it depends on, called dependencies.

In Bring we have four main players:
1. <span style="color:green">_**Dependency**_</span> - classes-components that might be bound between each other inside the **Container**
2. <span style="color:green">_**Dependency Scanner**_</span> - classes that are responsible for scanning packages to find **Dependencies**
3. <span style="color:green">_**Injector**_</span> -  internal classes that are responsible for creation and binding **Dependencies** between each other
4. <span style="color:green">_**Container**_</span> - main class that is used by clients, where all **Dependencies** are stored and might be retrieved by their names or types

To start using Bring framework it's enough to add it as a dependency to your Maven or Gradle project.
For that you can put the built jar file into the .m2 folder in proper directory(package): _**com/bobocode/petros/bring**_
or simply download the sources ane execute _**mvn install**_ command in terminal, so the project will be added to proper destination

To mark your class as a dependency you should use <span style="color:yellow">@Dependency</span> annotation. 
Thus, your class will be added to the Application Container.
Pay attention that your class must have a default constructor explicitly or implicitly.

```java
import com.bobocode.petros.annotation.Dependency;

@Dependency
public class MyBeautifulDependency{
    
}
```
In case if your class has other classes as its Dependencies then you should use <span style="color:yellow">@Injected</span> annotation
on its constructor where these dependencies are passed as parameters (these parameters also must be marked with <span style="color:yellow">@Dependency</span> annotation)

```java
import com.bobocode.petros.annotation.Dependency;
import com.bobocode.petros.annotation.Injected;

@Dependency
public class MyBeautifulDependency {
    private DependencyOne dependencyOne;
    private DependencyTwo dependencyTwo;
    
    public MyBeautifulDependency() {
    }

    @Injected
    public MyBeautifulDependency(DependencyOne dependencyOne, DependencyTwo dependencyTwo) {
        this.dependencyOne = dependencyOne;
        this.dependencyTwo = dependencyTwo;
    }
}
```
In cases when you want to use classes that you don't own in your container and be able to inject them as Dependencies into your classes
then you should provide a proper configuration using <span style="color:yellow">@ConfigClass</span> annotation

```java
import com.bobocode.petros.annotation.ConfigClass;
import com.bobocode.petros.annotation.Dependency;

@ConfigClass
public class MyBeautifulConfiguration {
    @Dependency
    public ForeignDependencyOne foreignDependencyOne(){
        return new ForeignDependencyOne();
    }

    @Dependency(name = "myForeignDependencyTwo")
    public ForeignDependencyTwo foreignDependencyOne(ForeignDependencyOne foreignDependencyOne){
        return new ForeignDependencyTwo(foreignDependencyOne);
    }
}
```
After this configuration provided _ForeignDependencyOne_ and _ForeignDependencyTwo_ classes from any other libraries you don't own might be used
in your project as **Dependencies** and can be inserted into your own Dependency classes

After all configurations is done - the time for creating _**Application Container**_ has come. You just have to create new instance of _**ApplicationAnnotationContainer**_
and pass a string with package name as a parameter. This package and all its sub packages will be scanned and in case of proper configuration
will be added to the container

```java
import com.bobocode.petros.container.ApplicationAnnotationContainer;
import com.bobocode.petros.container.ApplicationContainer;

public class MyBeautifulRunner {
    public static void main(String[] args) {
        ApplicationContainer container = new ApplicationAnnotationContainer("com.my.beautiful.package");
        MyBeautifulDependency dependencyByClass = container.getDependency(MyBeautifulDependency.class);
        MyBeautifulDependency dependencyByDependencyName = container.getDependency("myForeignDependencyTwo", MyBeautifulDependency.class);
    }
}
```
<span style="color:green">**AND YOU'RE READY TO GO!!!**</span>

Current implementation has some restrictions that you should take into account:
- you can't inject your **Dependencies** into configuration dependencies
- in case of creating configuration dependencies pay attention on parameters naming. Parameter names must correspond to its Dependency name
- and much more hidden constraints... try to find em 😁

Don't hesitate to help us with improving of our project. It's opensource and free to use. We're waiting for your contribution 😜

https://github.com/petros-team/bring
