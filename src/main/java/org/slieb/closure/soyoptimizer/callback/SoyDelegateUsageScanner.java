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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

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

    public boolean isUsedRegistration(DelegateRegistration registration) {
        final DelegateReference reference = registration.getReference();

        if (templateCalls.contains(reference)) {
            return true;
        }

        final DelegateIdentifier identifier = registration.getIdentifier();
        final boolean useStrict = templateCalls.stream().allMatch(callReference -> isStrictReference(identifier, callReference));

        if (useStrict) {
            return isUsedRegistrationStrictCheck(reference);
        } else {
            return isUsedRegistrationSoftCheck(registration, identifier);
        }
    }

    private boolean isUsedRegistrationSoftCheck(final DelegateRegistration registration,
                                                final DelegateIdentifier identifier) {
        if (templateIds.contains(identifier)) {
            final String variant = registration.getVariant().get();
            return variant.isEmpty() ||
                    isCalledVariantThatEquals(identifier, variant);
        } else {
            return false;
        }
    }

    private boolean isUsedRegistrationStrictCheck(final DelegateReference reference) {
        return templateCalls.stream().anyMatch(callReference -> isDefaultAndOnlyReferenceFor(
                reference, callReference));
    }

    public boolean isCalledVariantThatEquals(final DelegateIdentifier identifier,
                                             final String variant) {
        return getCalledTemplateVariants(identifier).stream().anyMatch(v -> v.map(variant::equals).orElse(true));
    }

    public Set<Optional<String>> getCalledTemplateVariants(DelegateIdentifier identifier) {
        return getCalledTemplates(identifier).map(DelegateReference::getVariant).collect(toSet());
    }

    public Stream<DelegateReference> getCalledTemplates(DelegateIdentifier identifier) {
        return templateCalls.stream().filter(call -> call.getIdentifier().map(identifier::equals).orElse(true));
    }

    private boolean isDefaultAndOnlyReferenceFor(final DelegateReference reference,
                                                 final DelegateReference callReference) {
        return callReference.equals(reference) || (reference.isDefaultReferenceOf(callReference) &&
                templateRegistrations.stream().map(DelegateRegistration::getReference).noneMatch(callReference::equals));
    }

    private boolean isStrictReference(final DelegateIdentifier identifier,
                                      final DelegateReference callReference) {
        final Optional<DelegateIdentifier> optionalIdentifier = callReference.getIdentifier();
        return optionalIdentifier.isPresent() && (callReference.getVariant().isPresent() || !optionalIdentifier.get().equals(identifier));
    }

    public Set<DelegateRegistration> getUsedRegistrations() {
        return templateRegistrations.stream().filter(this::isUsedRegistration).collect(toSet());
    }
}

