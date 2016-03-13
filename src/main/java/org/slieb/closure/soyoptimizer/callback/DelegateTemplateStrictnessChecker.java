package org.slieb.closure.soyoptimizer.callback;

import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.rhino.Node;
import org.slieb.closure.soyoptimizer.SoyDelegateNodeHelper;
import org.slieb.closure.soyoptimizer.node.DelegateGetFunctionCall;
import org.slieb.closure.soyoptimizer.node.DelegateGetIdentifierCall;

import static org.slieb.closure.soyoptimizer.SoyDelegateNodeHelper.isGetDelegateFnCall;
import static org.slieb.closure.soyoptimizer.SoyDiagnostics.*;

public class DelegateTemplateStrictnessChecker extends NodeTraversal.AbstractPostOrderCallback {

    private boolean hasStrictIdCalls = true;
    private boolean hasStrictGetFnCalls = true;
    private boolean isHasStrictVariantUsages = true;

    @Override
    public void visit(final NodeTraversal t,
                      final Node n,
                      final Node parent) {

        if (SoyDelegateNodeHelper.isGetDelegateIdCall(n)) {
            final Node templateName = new DelegateGetIdentifierCall(n).getTemplateNode().get();
            if (!templateName.isString()) {
                hasStrictIdCalls = false;
                t.report(templateName, REQUIREMENT_STRING_LITERAL_FOR_GET_ID);
            }
        }
        if (isGetDelegateFnCall(n)) {
            final DelegateGetFunctionCall delegateGetFunctionCall = new DelegateGetFunctionCall(n);
            final Node identifierNode = delegateGetFunctionCall.getIdentifierNode().get();
            if (!SoyDelegateNodeHelper.isGetDelegateIdCall(identifierNode)) {
                hasStrictGetFnCalls = false;
                t.report(identifierNode, OPTIMIZATION_GET_ID_NOT_USED_FOR_GET_FN);
            }

            final Node variantNode = delegateGetFunctionCall.getVariantNode().get();
            if (!variantNode.isString()) {
                isHasStrictVariantUsages = false;
                t.report(variantNode, OPTIMIZATION_STRING_LITERAL_FOR_VARIANT);
            }
        }
    }

    public boolean isHasStrictGetFnCalls() {
        return hasStrictGetFnCalls;
    }

    public boolean isHasStrictIdCalls() {
        return hasStrictIdCalls;
    }

    public boolean isHasStrictVariantUsages() {
        return isHasStrictVariantUsages;
    }
}
