package org.slieb.closure.soyoptimizer.callback;

import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.rhino.Node;
import org.slieb.closure.soyoptimizer.DelegateRegistration;
import org.slieb.closure.soyoptimizer.node.DelegateRegistrationCall;

import java.util.Set;

import static org.slieb.closure.soyoptimizer.SoyDelegateNodeHelper.isRegisterDelegateFunction;
import static org.slieb.closure.soyoptimizer.SoyDelegateNodeHelper.isSoyTemplateNameProperty;

/**
 * A soy visitor that removes all soy registrations that match any element in a given set of DelegateRegistrations.
 */
public class DelegateTemplateRegistrationsRemover extends NodeTraversal.AbstractShallowCallback {

    private final Set<DelegateRegistration> registrations;

    public DelegateTemplateRegistrationsRemover(final Set<DelegateRegistration> registrations) {
        this.registrations = registrations;
    }

    @Override
    public void visit(final NodeTraversal t,
                      final Node n,
                      final Node parent) {
        if (nodeIsRemovableRegistration(n) || nodeIsRemovableReference(n)) {
            detachAndClean(n, parent);
            t.getCompiler().reportCodeChange();
        }
    }

    private boolean nodeIsRemovableRegistration(final Node n) {
        return isRegisterDelegateFunction(n) &&
                registrations.stream().anyMatch(new DelegateRegistrationCall(n).toRegistration()::equals);
    }

    private boolean nodeIsRemovableReference(final Node n) {
        return n.isAssign() && (assignNodeIsRemovableTemplateMethodAssign(n) || assignNodeIsRemovableDelegateTemplateNameSetter(n));
    }

    private boolean assignNodeIsRemovableDelegateTemplateNameSetter(final Node n) {
        return n.getLastChild().isString() && isSoyTemplateNameProperty(n.getFirstChild()) && isRemovableMethodName(n.getLastChild().getString());
    }

    private boolean assignNodeIsRemovableTemplateMethodAssign(final Node n) {
        return n.getLastChild().isFunction() && n.getFirstChild().isGetProp() && isRemovableMethodName(n.getFirstChild().getQualifiedName());
    }

    private void detachAndClean(Node n,
                                Node parent) {
        n.detachFromParent();
        if (shouldDetachParent(parent)) {
            detachAndClean(parent, parent.getParent());
        }
    }

    private boolean shouldDetachParent(Node parent) {
        switch (parent.getToken()) {
            case BLOCK:
            case EXPR_RESULT:
                return !parent.hasChildren();
            case IF:
                return (parent.getChildCount() <= 1);
            default:
                return false;
        }
    }

    private boolean isRemovableMethodName(final String methodName) {
        return registrations.stream().map(DelegateRegistration::getMethodName).anyMatch(methodName::equals);
    }
}
