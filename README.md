# kotlinx-benchmark

[![Kotlin Alpha](https://kotl.in/badges/alpha.svg)](https://kotlinlang.org/docs/components-stability.html)
[![JetBrains incubator project](https://jb.gg/badges/incubator.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Build status](<https://teamcity.jetbrains.com/guestAuth/app/rest/builds/buildType:(id:KotlinTools_KotlinxCollectionsImmutable_Build_All)/statusIcon.svg>)](https://teamcity.jetbrains.com/viewType.html?buildTypeId=KotlinTools_KotlinxBenchmark_Build_All)
[![Maven Central](https://img.shields.io/maven-central/v/org.jetbrains.kotlinx/kotlinx-benchmark-runtime.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.jetbrains.kotlinx%22%20AND%20a:%22kotlinx-benchmark-runtime%22)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v?label=Gradle%20Plugin&metadataUrl=https://plugins.gradle.org/m2/org/jetbrains/kotlinx/kotlinx-benchmark-plugin/maven-metadata.xml)](https://plugins.gradle.org/plugin/org.jetbrains.kotlinx.benchmark)
[![IR](https://img.shields.io/badge/Kotlin%2FJS-IR%20supported-yellow)](https://kotl.in/jsirsupported)

kotlinx-benchmark is a toolkit for running benchmarks for multiplatform code written in Kotlin.

## Features

- Low noise and reliable results
- Statistical analysis
- Detailed performance reports

## Table of contents

<!--- TOC -->

- [Using in Your Projects](#using-in-your-projects)
  - [Gradle Setup](#gradle-setup)
    - [Kotlin DSL](#kotlin-dsl)
    - [Groovy DSL](#groovy-dsl)
  - [Target-specific configurations](#target-specific-configurations)
    - [Kotlin/JVM](#kotlinjvm)
    - [Kotlin/JS](#kotlinjs)
    - [Multiplatform](#multiplatform)
  - [Benchmark Configuration](#benchmark-configuration)
- [Examples](#examples)
- [Contributing](#contributing)

<!--- END -->

- **Additional links**
  - [Code Benchmarking: A Brief Overview](docs/benchmarking-overview.md)
  - [Understanding Benchmark Runtime](docs/benchmark-runtime.md)
  - [Configuring kotlinx-benchmark](docs/configuration-options.md)
  - [Interpreting and Analyzing Results](docs/interpreting-results.md)
  - [Creating Separate Source Sets](docs/seperate-source-sets.md)
  - [Tasks Overview](docs/tasks-overview.md)
  - [Compatibility Guide](docs/compatibility.md)
  - [Submitting issues and PRs](CONTRIBUTING.md)

## Using in Your Projects

The `kotlinx-benchmark` library is designed to work with Kotlin/JVM, Kotlin/JS, Kotlin/Native, and Kotlin/WASM (experimental) targets. To get started, ensure you're using Kotlin 1.8.20 or newer and Gradle 8.0 or newer.

### Gradle Setup

<details open>
<summary>Kotlin DSL</summary>

1.  **Applying Plugin**: Apply the benchmark plugin.

    ```kotlin
    plugins {
        id("org.jetbrains.kotlinx.benchmark") version "0.4.8"
    }
    ```

2.  **Adding Dependency**: Next, add the `kotlinx-benchmark-runtime` dependency to the common source set in your `build.gradle.kts` file.

    ```kotlin
    kotlin {
        sourceSets {
            commonMain {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.8")
                }
            }
        }
    }
    ```

3.  **Specifying Repository**: Ensure you have `mavenCentral()` for dependencies lookup in the list of repositories:

    ```kotlin
    repositories {
        mavenCentral()
    }
    ```

    </details>

<details>
<summary>Groovy DSL</summary>

1.  **Applying Plugin**: Apply the benchmark plugin.

    ```groovy
    plugins {
        id 'org.jetbrains.kotlinx.benchmark' version '0.4.8'
    }
    ```

2.  **Adding Dependency**: Next, add the `kotlinx-benchmark-runtime` dependency to the common source set in your `build.gradle.kts` file.

    ```groovy
    kotlin {
        sourceSets {
            commonMain {
                dependencies {
                    implementation 'org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.8'
                }
            }
        }
    }
    ```

3.  **Specifying Repository**: Ensure you have `mavenCentral()` for dependencies lookup in the list of repositories:

    ```groovy
    repositories {
        mavenCentral()
    }
    ```

    </details>

Benchmark classes located in the common source set will be run in all platforms, 
while those located in a platform-specific source set will be run in the corresponding platform.

### Target-specific configurations

For different platforms, there may be distinct requirements and settings that need to be configured.

#### Kotlin/JVM

For benchmarking Kotlin/JVM code you can have either a Kotlin/JVM project or a Kotlin Multiplatform project.

In a Kotlin/JVM project:

-   Add the `kotlinx-benchmark-runtime` to project dependencies:

    ```kotlin
    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.8")
    }
    ```

-   Register the source set containing benchmarks as the benchmark target:

    ```kotlin
    benchmark {
        targets {
            register("main")
        }
    }
    ```
  
In a Kotlin Multiplatform project:

-   Create a `JVM` target:

    ```kotlin
    kotlin {
        jvm()
    }
    ```

-   Register `jvm` as a benchmark target:

    ```kotlin
    benchmark {
        targets { 
            register("jvm")
        }
    }
    ```

Apply [allopen plugin](https://kotlinlang.org/docs/all-open-plugin.html) to ensures your benchmark classes and methods are `open`, meeting Java Microbenchmark Harness (JMH)'s requirements. 

```kotlin
plugins {
    kotlin("plugin.allopen") version "1.8.21"
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}
```

<details>
  <summary><b>Illustrative Example</b></summary>

Consider you annotated each of your benchmark classes with `@State(Scope.Benchmark)`:

```kotlin
@State(Scope.Benchmark)
class MyBenchmark {
    // Benchmarking-related methods and variables
    fun benchmarkMethod() {
        // benchmarking logic
    }
}
```

In Kotlin, classes and methods are `final` by default, which means they can't be overridden. This is incompatible with the operation of the Java Microbenchmark Harness (JMH), which needs to generate subclasses for benchmarking.

This is where the `allopen` plugin comes into play. With the plugin applied, any class annotated with `@State` is treated as `open`, which allows JMH to work as intended. Here's the Kotlin DSL configuration for the `allopen` plugin:

```kotlin
plugins {
    kotlin("plugin.allopen") version "1.8.21"
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}
```

This configuration ensures that your `MyBenchmark` class and its `benchmarkMethod` function are treated as `open`, allowing JMH to generate subclasses and conduct the benchmark.

</details>

You can alternatively mark your benchmark classes and methods `open` manually. For a practical example, please refer to [examples](examples/kotlin-kts).

#### Java

Setting up benchmarking in a Java project is the same as in a Kotlin/JVM project.
For a practical example, please refer to [examples](examples/java).

#### Kotlin/JS

For benchmarking Kotlin/JS code you should have a Kotlin Multiplatform project.

-   Create a `JS` target with Node.js execution environment:

    ```kotlin
    kotlin {
        js { 
            nodejs()
        } 
    }
    ```

-   Register `js` as a benchmark target:

    ```kotlin
    benchmark {
        targets { 
            register("js")
        }
    }
    ```

For Kotlin/JS, only IR backend is supported. For more information on the IR compiler, please refer to the [Kotlin/JS IR compiler documentation](https://kotlinlang.org/docs/js-ir-compiler.html)

This setup enables running benchmarks in the main compilation of any registered targets. Another option is to register the compilation you want to run benchmarks from. The platform-specific artifacts will be resolved automatically. For a practical example, please refer to [examples](examples/multiplatform).

### Benchmark Configurations

To further customize your benchmarks, add a `configurations` section within the `benchmark` block. By default, a `main` configuration is generated, but additional configurations can be added as needed:

```kotlin
benchmark {
    configurations {
        main {
            warmups = 20
            iterations = 10
            iterationTime = 3
            iterationTimeUnit = "s"
        }
        smoke {
            warmups = 5
            iterations = 3
            iterationTime = 500
            iterationTimeUnit = "ms"
        }
    }
}
```

For comprehensive guidance on configuring your benchmark setup, please refer to our detailed documentation on [Configuring kotlinx-benchmark](docs/configuration-options.md).

# Examples

To help you better understand how to use the kotlinx-benchmark library, we've provided an [examples](examples) subproject. These examples showcase various use cases and offer practical insights into the library's functionality.

## Contributing

We welcome contributions to kotlinx-benchmark! If you want to contribute, please refer to our Contribution Guidelines.
