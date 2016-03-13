package org.slieb.closure.soyoptimizer;

import com.google.javascript.jscomp.DiagnosticGroup;
import com.google.javascript.jscomp.DiagnosticType;

public class SoyDiagnostics {

    private SoyDiagnostics() {}

    public static final DiagnosticType SOY_NOT_STRING_LITERAL_FOR_ID =
            DiagnosticType.error("SOY_NOT_STRING_LITERAL_FOR_ID",
                                 "Not using string literal for " + SoyDelegateNodeHelper.GET_DELEGATE_ID + ".");

    public static final DiagnosticType SOY_NOT_STRING_LITERAL_FOR_VARIANT =
            DiagnosticType.warning("SOY_NOT_STRING_LITERAL",
                                   "Not using string literal for variant. Some features of delegate optimization will be less powerfull.");

    public static final DiagnosticType SOY_NOT_USING_ID =
            DiagnosticType.warning("SOY_NOT_USING_ID", SoyDelegateNodeHelper.GET_DELEGATE_FN + "called, but the first argument is not " + SoyDelegateNodeHelper
                    .GET_DELEGATE_ID);

    public static final DiagnosticGroup SOY_DELEGATE_CRITICAL_GROUP =
            DiagnosticGroup.forType(SOY_NOT_STRING_LITERAL_FOR_ID);

    /**
     *
     */
    public static final DiagnosticGroup SOY_DELEGATE_OPTIMIZATIONS_GROUP =
            new DiagnosticGroup(
                    DiagnosticGroup.forType(SOY_NOT_STRING_LITERAL_FOR_VARIANT),
                    DiagnosticGroup.forType(SOY_NOT_USING_ID));

    /**
     * All related warnings.
     */
    public static final DiagnosticGroup SOY_DELEGATE_GROUP =
            new DiagnosticGroup(SOY_DELEGATE_CRITICAL_GROUP, SOY_DELEGATE_OPTIMIZATIONS_GROUP);
}
