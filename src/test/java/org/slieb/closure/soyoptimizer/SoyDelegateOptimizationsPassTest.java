package org.slieb.closure.soyoptimizer;

import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class SoyDelegateOptimizationsPassTest extends BaseTemplateTest {

    @Test
    public void testOverridesAreRemoved() throws IOException {
        Compiler compiler = new Compiler();
        CompilerOptions compilerOptions = new CompilerOptions();
        final List<SourceFile> inputs = getInputs(packageA());
        SoyDelegateOptimizationsPass.addToOptions(compiler, compilerOptions);
        final List<SourceFile> externs = CommandLineRunner.getBuiltinExterns(compilerOptions);
        Result result = compiler.compile(externs, inputs, compilerOptions);
        Assert.assertTrue(result.success);
        Assert.assertTrue(result.errors.length == 0);
        Assert.assertFalse(compiler.toSource().contains("deltemplateA - default"));
        Assert.assertFalse(compiler.toSource().contains("deltemplateB - default"));
        Assert.assertTrue(compiler.toSource().contains("deltemplateA - override"));
    }

    @Test
    public void testOverridesAreRemovedInAdvancedMode() throws IOException {
        Compiler compiler = new Compiler();
        CompilerOptions compilerOptions = new CompilerOptions();
        final List<SourceFile> inputs = getInputs(packageA());

        CompilationLevel.ADVANCED_OPTIMIZATIONS.setTypeBasedOptimizationOptions(compilerOptions);
        CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(compilerOptions);
        CompilationLevel.ADVANCED_OPTIMIZATIONS.setDebugOptionsForCompilationLevel(compilerOptions);

        SoyDelegateOptimizationsPass.addToOptions(compiler, compilerOptions);

        final List<SourceFile> externs = CommandLineRunner.getBuiltinExterns(compilerOptions);
        Result result = compiler.compile(externs, inputs, compilerOptions);

        Assert.assertTrue(result.success);
        Assert.assertTrue(result.errors.length == 0);
        Assert.assertFalse(compiler.toSource().contains("deltemplateA - default"));
        Assert.assertFalse(compiler.toSource().contains("deltemplateB - default"));
        Assert.assertTrue(compiler.toSource().contains("deltemplateA - override"));
    }

    @Test
    public void testErrorIsCreatedWithInvalidGetIdCall() throws IOException {
        Compiler compiler = new Compiler();
        CompilerOptions compilerOptions = new CompilerOptions();
        final List<SourceFile> inputs = getInputs(packageA());
        inputs.add(SourceFile.fromCode("/bad.js", String.format("var y = 'x', x = %s(y);", SoyDelegateNodeHelper.GET_DELEGATE_ID)));
        SoyDelegateOptimizationsPass.addToOptions(compiler, compilerOptions);
        final List<SourceFile> externs = CommandLineRunner.getBuiltinExterns(compilerOptions);
        Result result = compiler.compile(externs, inputs, compilerOptions);
        Assert.assertFalse(result.success);
        Assert.assertEquals(1, result.errors.length);
        Assert.assertEquals(0, result.warnings.length);
    }

    @Test
    public void testOverridesAreStillRemovedWithInvalidGetFnCall() throws IOException {
        Compiler compiler = new Compiler();
        CompilerOptions compilerOptions = new CompilerOptions();
        final List<SourceFile> inputs = getInputs(packageA());
        inputs.add(SourceFile.fromCode("/bad.js", String.format("var y = 'x', x = %s(y, '', {});", SoyDelegateNodeHelper.GET_DELEGATE_FN)));
        SoyDelegateOptimizationsPass.addToOptions(compiler, compilerOptions);
        final List<SourceFile> externs = CommandLineRunner.getBuiltinExterns(compilerOptions);
        Result result = compiler.compile(externs, inputs, compilerOptions);
        Assert.assertTrue(result.success);
        Assert.assertEquals(0, result.errors.length);
        Assert.assertEquals(1, result.warnings.length);
    }

    @Test
    public void testWarningsIsThrownForVariableVariants() throws IOException {
        Compiler compiler = new Compiler();
        CompilerOptions compilerOptions = new CompilerOptions();
        final List<SourceFile> inputs = getInputs(packageA());
        inputs.add(SourceFile.fromCode("/bad3.js", String.format("var y3 = 'x', x2 = %s(%s('template'), y3, true);", SoyDelegateNodeHelper.GET_DELEGATE_FN,
                                                                 SoyDelegateNodeHelper.GET_DELEGATE_ID)));
        SoyDelegateOptimizationsPass.addToOptions(compiler, compilerOptions);
        final List<SourceFile> externs = CommandLineRunner.getBuiltinExterns(compilerOptions);
        //        compilerOptions.setWarningLevel(SoyDiagnostics.ALL, CheckLevel.OFF);

        compilerOptions.setPrettyPrint(true);
        Result result = compiler.compile(externs, inputs, compilerOptions);
        Assert.assertTrue(result.success);
        Assert.assertEquals(0, result.errors.length);
        Assert.assertEquals(1, result.warnings.length);
    }

    @Test
    public void testWarningsCanBeSuppressed() throws IOException {
        Compiler compiler = new Compiler();
        CompilerOptions compilerOptions = new CompilerOptions();
        final List<SourceFile> inputs = getInputs(packageA());
        inputs.add(SourceFile.fromCode("/bad1.js", String.format("var y1 = 'x', x1 = %s(y1);", SoyDelegateNodeHelper.GET_DELEGATE_ID)));
        inputs.add(SourceFile.fromCode("/bad2.js", String.format("var y2 = 'x', x2 = %s(y2, \"\", true);", SoyDelegateNodeHelper.GET_DELEGATE_FN)));
        inputs.add(SourceFile.fromCode("/bad3.js", String.format("var y3 = 'x', x2 = %s(%s(y1), y3, true);", SoyDelegateNodeHelper.GET_DELEGATE_FN,
                                                                 SoyDelegateNodeHelper.GET_DELEGATE_ID)));
        SoyDelegateOptimizationsPass.addToOptions(compiler, compilerOptions);
        final List<SourceFile> externs = CommandLineRunner.getBuiltinExterns(compilerOptions);
        compilerOptions.setWarningLevel(SoyDiagnostics.ALL, CheckLevel.OFF);
        compilerOptions.setPrettyPrint(true);
        Result result = compiler.compile(externs, inputs, compilerOptions);
        Assert.assertTrue(result.success);
        Assert.assertEquals(0, result.errors.length);
        Assert.assertEquals(0, result.warnings.length);
    }
}
