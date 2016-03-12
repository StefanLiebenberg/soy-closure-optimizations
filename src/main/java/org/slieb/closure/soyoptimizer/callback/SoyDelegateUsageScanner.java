package org.slieb.closure.soyoptimizer.callback;

import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.rhino.Node;
import org.slieb.closure.soyoptimizer.DelegateIdentifier;
import org.slieb.closure.soyoptimizer.DelegateReference;
import org.slieb.closure.soyoptimizer.DelegateRegistration;
import org.slieb.closure.soyoptimizer.SoyDelegateNodeHelper;
import org.slieb.closure.soyoptimizer.node.DelegateGetFunctionCall;
import org.slieb.closure.soyoptimizer.node.DelegateGetIdentifierCall;
import org.slieb.closure.soyoptimizer.node.DelegateRegistrationCall;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SoyDelegateUsageScanner extends NodeTraversal.AbstractPostOrderCallback {

    private final Set<DelegateIdentifier> templateIds;

    private final Set<DelegateReference> templateCalls;

    private final Set<DelegateRegistration> templateRegistrations;

    public SoyDelegateUsageScanner() {
        this.templateIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.templateCalls = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.templateRegistrations = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public void visit(final NodeTraversal t,
                      final Node node,
                      final Node parent) {
        if (SoyDelegateNodeHelper.isGetDelegateIdCall(node) && !SoyDelegateNodeHelper.isRegisterDelegateFunction(parent)) {
            final DelegateGetIdentifierCall identifierCall = new DelegateGetIdentifierCall(node);
            templateIds.add(identifierCall.toIdentifier());
        } else if (SoyDelegateNodeHelper.isGetDelegateFnCall(node)) {
            final DelegateGetFunctionCall delegateFnCall = new DelegateGetFunctionCall(node);
            templateCalls.add(delegateFnCall.toReference());
        } else if (SoyDelegateNodeHelper.isRegisterDelegateFunction(node)) {
            final DelegateRegistrationCall registrationCall = new DelegateRegistrationCall(node);
            templateRegistrations.add(registrationCall.toRegistration());
        }
    }

}

