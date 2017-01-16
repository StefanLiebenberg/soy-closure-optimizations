package org.slieb.closure.soyoptimizer;

import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.slieb.closure.soyoptimizer.SoyDelegateOptimizationsPass.addToOptions;

public class StrictUsageRemovalTest extends BaseTemplateTest {

    private Compiler compiler;

    private Result result;

    @Before
    public void setup() throws IOException {
        compiler = new Compiler();
        CompilerOptions compilerOptions = new CompilerOptions();
        final List<SourceFile> inputs = getInputs(packageB());
        final List<SourceFile> externs = CommandLineRunner.getBuiltinExterns(CompilerOptions.Environment.BROWSER);
        addToOptions(compiler, compilerOptions);
        compilerOptions.setPrettyPrint(true);
        result = compiler.compile(externs, inputs, compilerOptions);
    }

    @Test
    public void testTemplateSet001() throws IOException {
        Assert.assertTrue(result.success);
        Assert.assertEquals(0, result.errors.length);
        Assert.assertEquals(0, result.warnings.length);
        Assert.assertTrue(compiler.toSource().contains("Package B - Template 001 - Default"));
        Assert.assertFalse(compiler.toSource().contains("Package B - Template 001 - Variant 1"));
        Assert.assertFalse(compiler.toSource().contains("Package B - Template 001 - Variant 2"));
    }

    @Test
    public void testTemplateSet002() throws IOException {
        Assert.assertTrue(result.success);
        Assert.assertEquals(0, result.errors.length);
        Assert.assertEquals(0, result.warnings.length);
        Assert.assertTrue(compiler.toSource().contains("Package B - Template 002 - Default"));
        Assert.assertTrue(compiler.toSource().contains("Package B - Template 002 - Variant 1"));
        Assert.assertTrue(compiler.toSource().contains("Package B - Template 002 - Variant 2"));
    }

    @Test
    public void testTemplateSet003() throws IOException {
        Assert.assertTrue(result.success);
        Assert.assertEquals(0, result.errors.length);
        Assert.assertEquals(0, result.warnings.length);
        Assert.assertFalse(compiler.toSource().contains("Package B - Template 003 - Default"));
        Assert.assertFalse(compiler.toSource().contains("Package B - Template 003 - Variant 1"));
        Assert.assertFalse(compiler.toSource().contains("Package B - Template 003 - Variant 2"));
    }

    @Test
    public void testTemplateSet004() throws IOException {
        Assert.assertTrue(result.success);
        Assert.assertEquals(0, result.errors.length);
        Assert.assertEquals(0, result.warnings.length);
        Assert.assertFalse(compiler.toSource().contains("Package B - Template 004 - Default"));
        Assert.assertTrue(compiler.toSource().contains("Package B - Template 004 - Variant 1"));
        Assert.assertFalse(compiler.toSource().contains("Package B - Template 004 - Variant 2"));
    }
}
