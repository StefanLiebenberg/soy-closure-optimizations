package org.slieb.closure.soyoptimizer;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.*;
import com.google.javascript.rhino.Node;
import org.slieb.closure.soyoptimizer.callback.DelegateTemplateRegistrationsRemover;
import org.slieb.closure.soyoptimizer.callback.DelegateTemplateRegistrationsScanner;
import org.slieb.closure.soyoptimizer.callback.DelegateTemplateStrictnessChecker;
import org.slieb.closure.soyoptimizer.callback.SoyDelegateUsageScanner;

public class SoyDelegateOptimizationsPass implements CompilerPass {

    private final Compiler compiler;

    public SoyDelegateOptimizationsPass(final Compiler compiler) {
        this.compiler = compiler;
    }

    /**
     * Process the JS with root node root.
     * Can modify the contents of each Node tree
     *
     * @param externs Top of external JS tree
     * @param root    Top of JS tree
     */
    @Override
    public void process(final Node externs,
                        final Node root) {
        final DelegateTemplateStrictnessChecker checker = new DelegateTemplateStrictnessChecker();
        if (checker.isHasStrictIdCalls()) {
            DelegateTemplateRegistrationsScanner registrationsScanner = new DelegateTemplateRegistrationsScanner();
            NodeTraversal.traverseTyped(compiler, root, registrationsScanner);
            NodeTraversal.traverseTyped(compiler, root, new DelegateTemplateRegistrationsRemover(registrationsScanner.getOverriddenRegistrations()));
            NodeTraversal.traverseTyped(compiler, root, new SoyDelegateUsageScanner());
        }
    }

    public static void addToOptions(Compiler compiler,
                                    CompilerOptions compilerOptions) {
        compilerOptions.addCustomPass(CustomPassExecutionTime.BEFORE_CHECKS, new SoyDelegateOptimizationsPass(compiler));
    }
}
