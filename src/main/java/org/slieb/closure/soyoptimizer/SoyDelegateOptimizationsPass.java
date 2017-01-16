package org.slieb.closure.soyoptimizer;

import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.rhino.Node;
import org.slieb.closure.soyoptimizer.callback.DelegateTemplateRegistrationsRemover;
import org.slieb.closure.soyoptimizer.callback.DelegateTemplateRegistrationsScanner;
import org.slieb.closure.soyoptimizer.callback.DelegateTemplateStrictnessChecker;
import org.slieb.closure.soyoptimizer.callback.SoyDelegateUsageScanner;

import java.util.Set;

/**
 * A custom compiler pass to scan for unused templates or overridden soy templates.
 */
public class SoyDelegateOptimizationsPass implements CompilerPass {

    /**
     * todo(talk) #1
     * <p>
     * - Adding pass to the compiler
     * - BEFORE_CHECKS vs other options
     */
    @SuppressWarnings("WeakerAccess")
    public static void addToOptions(Compiler compiler,
                                    CompilerOptions compilerOptions) {
        compilerOptions.addCustomPass(CustomPassExecutionTime.BEFORE_CHECKS, new SoyDelegateOptimizationsPass(compiler));
    }

    private final AbstractCompiler compiler;

    /**
     * @param compiler The Abstract compiler object.
     */
    public SoyDelegateOptimizationsPass(final AbstractCompiler compiler) {
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
        doTraverse(root, checker);
        if (checker.isHasStrictIdCalls()) {
            DelegateTemplateRegistrationsScanner registrationsScanner = new DelegateTemplateRegistrationsScanner();
            doTraverse(root, registrationsScanner);
            doTraverse(root, new DelegateTemplateRegistrationsRemover(registrationsScanner.getOverriddenRegistrations()));

            final boolean assumeStrict = checker.isHasStrictGetFnCalls() && checker.isHasStrictVariantUsages();
            SoyDelegateUsageScanner usageScanner = new SoyDelegateUsageScanner(assumeStrict);
            doTraverse(root, usageScanner);
            Set<DelegateRegistration> unusedTemplates = usageScanner.getUnusedTemplates();
            while (!unusedTemplates.isEmpty()) {
                doTraverse(root, new DelegateTemplateRegistrationsRemover(unusedTemplates));
                usageScanner.reset();
                doTraverse(root, usageScanner);
                unusedTemplates = usageScanner.getUnusedTemplates();
            }
        }
    }

    private void doTraverse(final Node root,
                            final NodeTraversal.Callback callback) {
        NodeTraversal.traverseTyped(compiler, root, callback);
    }
}
