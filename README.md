# jMIP - Mixed Integer Programming (MIP) for Java

![Maven Central Version](https://img.shields.io/maven-central/v/org.optsol.jmip/jmip-core)
![](https://img.shields.io/badge/java--version-11-blue.svg)

### What is **_jMIP_** ?

**_jMIP_** is a **Dec**larative Java API for **O**perations **R**esearch (OR) software. It evolved from [**_jDecOR_**](https://github.com/OPTIMAL-SOLUTION-org/jdecor-pojo-template/) and is build on [OR-Tools](https://github.com/google/or-tools) from google.

>**_jMIP_**'s original purpose is to get rid of the often hard-to-avoid boilerplate code typically arising in applications that rely on solving OR models using (open-source or commercial) OR software packages. In addition, it follows a more declarative programming paradigm by encouraging you to compose your specific OR model in a structured way.
>
> Long story short, it is designed to help making your code **cleaner** and **less error-prone**!

### How to install **_jMIP_** ?

* You can [download](https://central.sonatype.com/namespace/org.optsol.jmip) **_jMIP_** manually from maven central repository
* Alternatively add the dependency to your project's POM:
```xml
    <dependencies>
        <dependency>
            <groupId>org.optsol.jmip</groupId>
            <artifactId>jmip-ortools-linearsolver</artifactId>
            <version>2.1.0</version>
        </dependency>
    </dependencies>
```

### Get startet...

We are working on providing detailed documentation (_Need more :coffee:_). \
In the meantime you can refer to our template project [jmip-pojo-template](https://github.com/OPTIMAL-SOLUTION-org/jmip-pojo-template/)