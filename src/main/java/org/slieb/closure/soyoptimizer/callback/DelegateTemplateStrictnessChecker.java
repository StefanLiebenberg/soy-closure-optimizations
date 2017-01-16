package org.slieb.closure.soyoptimizer.callback;

import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.rhino.Node;
import org.slieb.closure.soyoptimizer.SoyDelegateNodeHelper;
import org.slieb.closure.soyoptimizer.node.DelegateGetFunctionCall;
import org.slieb.closure.soyoptimizer.node.DelegateGetIdentifierCall;

import static org.slieb.closure.soyoptimizer.SoyDelegateNodeHelper.isGetDelegateFnCall;
import static org.slieb.closure.soyoptimizer.SoyDiagnostics.*;

/**
 * 
 * - soy.getDelegateFn(soy.getDelegateId('some-template'), 'some-variant', false);
 *
 * - var idValue = 'some-template';
 * - var id = soy.getDelegateId(idValue);
 * - var variantValue = 'some-variant';
 * - var template = soy.getDelegateFn(id, variantValue, false);
 *
 */
public class DelegateTemplateStrictnessChecker extends NodeTraversal.AbstractPostOrderCallback {

    /**
     * The argument passed to soy.getDelegateId('some-template') calls are always string primitives instead of some variable.
     */
    private boolean hasStrictIdCalls = true;

    /**
     * The first argument to soy.getDelegateId(soy.getDelegateId('some-template'), 'some-variant', false) calls are
     * always soy.getDelegateId calls instead of some variable.
     */
    private boolean hasStrictGetFnCalls = true;

    /**
     * The second argument to soy.getDelegateId(soy.getDelegateId('some-template'), 'some-variant', false) calls are
     * always string primitives instead of some variable.
     *
     */
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
