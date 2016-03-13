package org.slieb.closure.soyoptimizer;

import com.google.javascript.jscomp.DiagnosticGroup;
import com.google.javascript.jscomp.DiagnosticType;

public class SoyDiagnostics {

    private SoyDiagnostics() {}

    public static final DiagnosticType REQUIREMENT_STRING_LITERAL_FOR_GET_ID =
            DiagnosticType.error("REQUIREMENT_STRING_LITERAL_FOR_GET_ID",
                                 "Not using string literal for " + SoyDelegateNodeHelper.GET_DELEGATE_ID + ".");

    public static final DiagnosticType OPTIMIZATION_STRING_LITERAL_FOR_VARIANT =
            DiagnosticType.warning("OPTIMIZATION_STRING_LITERAL_FOR_VARIANT",
                                   "Not using string literal for variant. Some soy optimizations won't be able to run.");

    public static final DiagnosticType OPTIMIZATION_GET_ID_NOT_USED_FOR_GET_FN =
            DiagnosticType.warning("OPTIMIZATION_GET_ID_NOT_USED_FOR_GET_FN",
                                   SoyDelegateNodeHelper.GET_DELEGATE_FN + "called, but the first argument is not " + SoyDelegateNodeHelper
                                           .GET_DELEGATE_ID);

    /**
     * All SoyDelegateOptimizationPass diagnostics that indicate fatal failures.
     */
    public static final DiagnosticGroup REQUIREMENTS =
            DiagnosticGroup.forType(REQUIREMENT_STRING_LITERAL_FOR_GET_ID);

    /**
     * All SoyDelegateOptimizationPass diagnostics that related to optimization checks.
     */
    public static final DiagnosticGroup OPTIMIZATIONS =
            new DiagnosticGroup(
                    DiagnosticGroup.forType(OPTIMIZATION_STRING_LITERAL_FOR_VARIANT),
                    DiagnosticGroup.forType(OPTIMIZATION_GET_ID_NOT_USED_FOR_GET_FN));

    /**
     * All SoyDelegateOptimizationPass related diagnostics
     */
    public static final DiagnosticGroup ALL =
            new DiagnosticGroup(REQUIREMENTS, OPTIMIZATIONS);
}
