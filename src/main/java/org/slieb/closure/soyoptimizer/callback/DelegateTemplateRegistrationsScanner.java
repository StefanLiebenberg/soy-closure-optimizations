package org.slieb.closure.soyoptimizer.callback;

import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.rhino.Node;
import org.slieb.closure.soyoptimizer.DelegateReference;
import org.slieb.closure.soyoptimizer.DelegateRegistration;
import org.slieb.closure.soyoptimizer.SoyDelegateNodeHelper;
import org.slieb.closure.soyoptimizer.node.DelegateRegistrationCall;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class DelegateTemplateRegistrationsScanner extends NodeTraversal.AbstractShallowCallback {

    private final Set<DelegateRegistrationCall> registrationCalls;

    public DelegateTemplateRegistrationsScanner() {registrationCalls = Collections.newSetFromMap(new ConcurrentHashMap<>());}

    @Override
    public void visit(final NodeTraversal t,
                      final Node n,
                      final Node parent) {
        if (SoyDelegateNodeHelper.isRegisterDelegateFunction(n)) {
            registrationCalls.add(new DelegateRegistrationCall(n));
        }
    }

    public Set<DelegateRegistration> getRegistrations() {
        return registrationCalls.stream().map(DelegateRegistrationCall::toRegistration).collect(Collectors.toSet());
    }

    public Map<DelegateReference, Double> getRegistrationsPriorityMap() {
        final Map<DelegateReference, Double> priorityMap = new ConcurrentHashMap<>();
        getRegistrations().stream().forEach(registration -> {
            final DelegateReference templateReference = registration.getReference();
            final Double priority = registration.getPriority();
            if (!priorityMap.containsKey(templateReference) || priorityMap.get(templateReference) < priority) {
                priorityMap.put(templateReference, priority);
            }
        });
        return priorityMap;
    }

    public Set<DelegateRegistration> getOverriddenRegistrations() {
        final Map<DelegateReference, Double> priorityMap = getRegistrationsPriorityMap();
        return getRegistrations().stream().filter(r -> priorityMap.get(r.getReference()) > r.getPriority()).collect(toSet());
    }
}
