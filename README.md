# soy-closure-optimizations
A compiler custom pass to remove extra/unused soy delegate templates from the compiled javascript artifact.

## Usage

Usage is simple:

```java
SoyDelegateOptimizationsPass.addToOptions(compiler, compilerOptions);
```
