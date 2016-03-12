package org.slieb.closure.soyoptimizer;

import com.google.javascript.rhino.Node;

public class SoyDelegateNodeHelper {

    private SoyDelegateNodeHelper() {}

    public static final String GET_DELEGATE_ID = "soy.$$getDelTemplateId";
    public static final String GET_DELEGATE_FN = "soy.$$getDelegateFn";
    public static final String REGISTER_DELEGATE_FN = "soy.$$registerDelegateFn";

    public static boolean isRegisterDelegateFunction(Node node) {
        return isMethodCall(node, REGISTER_DELEGATE_FN);
    }

    public static boolean isGetDelegateIdCall(final Node n) {
        return isMethodCall(n, GET_DELEGATE_ID);
    }

    public static boolean isGetDelegateFnCall(final Node n) {
        return isMethodCall(n, GET_DELEGATE_FN);
    }

    private static boolean isMethodCall(final Node n,
                                        final String methodName) {
        return n.isCall() && n.getFirstChild().matchesQualifiedName(methodName);
    }
}
