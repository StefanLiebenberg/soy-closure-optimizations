package org.slieb.closure.soyoptimizer;

import com.google.javascript.rhino.Node;

public class SoyDelegateNodeHelper {

    private SoyDelegateNodeHelper() {}

    public static final String GET_DELEGATE_ID = "soy.$$getDelTemplateId";
    public static final String GET_DELEGATE_FN = "soy.$$getDelegateFn";

    public static boolean isGetDelegateIdCall(final Node n) {
        return n.isCall() && n.getFirstChild().matchesQualifiedName(GET_DELEGATE_ID);
    }

    public static boolean isGetDelegateFnCall(final Node n) {
        return n.isCall() && n.getFirstChild().matchesQualifiedName(GET_DELEGATE_FN);
    }
}
