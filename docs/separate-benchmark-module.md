# Creating Separate Module for Benchmarks

This guide will walk you through the process of creating a separate module for benchmarks in your Kotlin project.

## Table of Contents

1. [Why Have Separate Source Sets for Benchmarks?](#why-have-a-separate-module-for-benchmarks)
2. [Step-by-step Setup Guide](#step-by-step-setup-guide)
   - [Kotlin Multiplatform Project](#kotlin-multiplatform-project)
   - [Kotlin/JVM Project](#kotlinjvm-project)

## Why Have a Separate Module for Benchmarks?

Creating a separate module for benchmarks is especially beneficial when you are integrating benchmarks into an existing project. Here are several advantages of doing so:

1. **Organization**: It helps maintain a clean and organized project structure. Segregating benchmarks from the main code makes it easier to navigate and locate specific code segments.

2. **Isolation**: Separating benchmarks ensures that the benchmarking code does not interfere with your main code or test code. This isolation guarantees accurate measurements without unintentional side effects.

3. **Flexibility**: Creating a separate module allows you to manage your benchmarking code independently. You can compile, test, and run benchmarks without impacting your main source code.

## Step-by-step Setup Guide

Below are the step-by-step instructions to set up a separate module for benchmarks in both Kotlin Multiplatform and Kotlin/JVM projects:

### Kotlin Multiplatform Project

Follow the these steps to set up a separate compilation for benchmarks:

1. **Define New Compilation**:

    Start by creating a new compilation in your target of choice (e.g. jvm, js, native, wasm etc.). 
    In this example, we're associating the new compilation `benchmark` with the `main` compilation of the `jvm` target to be able to access its internal API.

    ```kotlin
    // build.gradle.kts
    kotlin { 
        jvm { 
            compilations.create('benchmark') {
                associateWith(compilations.main)
            }
        }
    }
    ```

2. **Register Benchmark Compilation**:

    Conclude by registering your new benchmark compilation using its default source set name. In this instance, `jvmBenchmark` is the name of the default source set of the `benchmark` compilation.

    ```kotlin
    // build.gradle.kts
    benchmark {
        targets {
            register("jvmBenchmark")
        }
    }
    ```

    For more information on creating a custom compilation, you can refer to the [Kotlin documentation on creating a custom compilation](https://kotlinlang.org/docs/multiplatform-configure-compilations.html#create-a-custom-compilation).

### Kotlin/JVM Project

Set up a separate benchmark source set by following these simple steps:

1. **Define Source Set**:

    Begin by defining a new source set. We'll use `benchmark` as the name for the source set.

    ```kotlin
    // build.gradle.kts
    sourceSets {
        benchmark
    }
    ```

2. **Propagate Dependencies**:

    Next, propagate dependencies and output from the `main` source set to your `benchmark` source set. This ensures the `benchmark` source set has access to classes and resources from the `main` source set.

    ```kotlin
    // build.gradle.kts
    dependencies {
        add("benchmarkImplementation", sourceSets.main.output + sourceSets.main.runtimeClasspath)
    }
    ```

    You can also add output and `compileClasspath` from `sourceSets.test` in the same way if you wish to reuse some of the test infrastructure.

3. **Register Benchmark Source Set**:

    Finally, register your benchmark source set. This informs the kotlinx-benchmark tool that benchmarks reside within this source set and need to be executed accordingly.

    ```kotlin
    // build.gradle.kts
    benchmark {
        targets { 
            register("benchmark")
        }
    }
    ```
