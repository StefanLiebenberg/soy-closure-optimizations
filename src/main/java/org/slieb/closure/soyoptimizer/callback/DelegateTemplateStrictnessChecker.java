package org.slieb.closure.soyoptimizer.callback;

import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.rhino.Node;
import org.slieb.closure.soyoptimizer.SoyDelegateNodeHelper;
import org.slieb.closure.soyoptimizer.node.DelegateGetIdentifierCall;

import static org.slieb.closure.soyoptimizer.SoyDiagnostics.SOY_NOT_STRING_LITERAL_FOR_ID;

public class DelegateTemplateStrictnessChecker extends NodeTraversal.AbstractPostOrderCallback {

    private boolean hasStrictIdCalls = true;

    @Override
    public void visit(final NodeTraversal t,
                      final Node n,
                      final Node parent) {

        if (SoyDelegateNodeHelper.isGetDelegateIdCall(n)) {
            final Node templateName = new DelegateGetIdentifierCall(n).getTemplateNode().get();
            if (!templateName.isString()) {
                hasStrictIdCalls = false;
                t.report(templateName, SOY_NOT_STRING_LITERAL_FOR_ID);
            }
        }
    }

    public boolean isHasStrictIdCalls() {
        return hasStrictIdCalls;
    }
}
