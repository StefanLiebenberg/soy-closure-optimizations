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
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class SoyDelegateUsageScanner extends NodeTraversal.AbstractPostOrderCallback {

    private final boolean assumeStrict;

    private final Set<DelegateIdentifier> templateIds;

    private final Set<DelegateReference> templateCalls;

    private final Set<DelegateRegistration> templateRegistrations;

    public SoyDelegateUsageScanner(final boolean assumeStrict) {
        this.assumeStrict = assumeStrict;
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

    public void reset() {
        templateIds.clear();
        templateCalls.clear();
        templateRegistrations.clear();
    }

    public Set<DelegateRegistration> getUnusedTemplates() {
        final Set<DelegateRegistration> usedRegistrations = getUsedRegistrations();
        final Predicate<DelegateRegistration> contains = usedRegistrations::contains;
        return templateRegistrations.stream().filter(contains.negate()).collect(toSet());
    }

    public Set<DelegateRegistration> getUsedRegistrations() {
        return templateRegistrations.stream().filter(this::isUsedRegistration).collect(toSet());
    }

    public boolean isUsedRegistration(DelegateRegistration registration) {
        final DelegateReference registrationReference = registration.getReference();

        if (templateCalls.contains(registrationReference)) {
            return true;
        }

        final DelegateIdentifier identifier = registration.getIdentifier();
        final boolean useStrict = assumeStrict || templateCalls.stream().allMatch(callReference -> isStrictReference(identifier, callReference));

        if (useStrict) {
            return isUsedRegistrationStrictCheck(registrationReference);
        } else {
            return isUsedRegistrationSoftCheck(registration, identifier);
        }
    }

    private boolean isStrictReference(final DelegateIdentifier identifier,
                                      final DelegateReference callReference) {
        final Optional<DelegateIdentifier> optionalIdentifier = callReference.getIdentifier();
        return optionalIdentifier.isPresent() && (callReference.getVariant().isPresent() || !optionalIdentifier.get().equals(identifier));
    }

    private boolean isUsedRegistrationStrictCheck(final DelegateReference registrationReference) {
        return templateCalls.stream().anyMatch(callReference -> isDefaultAndOnlyReferenceFor(registrationReference, callReference));
    }

    private boolean isDefaultAndOnlyReferenceFor(final DelegateReference registrationReference,
                                                 final DelegateReference callReference) {

        // they're both fully specified.
        if (callReference.equals(registrationReference)) {
            return true;
        }

        if ((registrationReference.isDefaultReferenceOf(callReference) &&
                templateRegistrations.stream().map(DelegateRegistration::getReference).noneMatch(callReference::equals))) {
            return true;
        }

        return false;
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

    private boolean isCalledVariantThatEquals(final DelegateIdentifier identifier,
                                              final String variant) {
        return getCalledTemplates(identifier).map(DelegateReference::getVariant).anyMatch(v -> v.map(variant::equals).orElse(true));
    }

    private Stream<DelegateReference> getCalledTemplates(DelegateIdentifier identifier) {
        return templateCalls.stream().filter(call -> call.getIdentifier().map(identifier::equals).orElse(true));
    }
}

