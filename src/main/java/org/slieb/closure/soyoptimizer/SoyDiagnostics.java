package org.slieb.closure.soyoptimizer;

import com.google.javascript.jscomp.DiagnosticType;

public class SoyDiagnostics {

    private SoyDiagnostics() {}

    public static final DiagnosticType SOY_NOT_STRING_LITERAL_FOR_ID =
            DiagnosticType.error("SOY_NOT_STRING_LITERAL_FOR_ID", "Not using string literal for " + SoyDelegateNodeHelper.GET_DELEGATE_ID + ".");
}
