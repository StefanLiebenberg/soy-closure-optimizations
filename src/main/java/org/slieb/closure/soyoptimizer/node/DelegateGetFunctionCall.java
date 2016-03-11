package org.slieb.closure.soyoptimizer.node;

import com.google.javascript.rhino.Node;
import org.slieb.closure.soyoptimizer.DelegateIdentifier;
import org.slieb.closure.soyoptimizer.DelegateReference;
import org.slieb.closure.soyoptimizer.SoyDelegateNodeHelper;

import java.util.Optional;

public class DelegateGetFunctionCall {

    private final Node node;

    public DelegateGetFunctionCall(final Node node) {
        this.node = node;
    }

    public Optional<Node> getIdentifierNode() {
        if (node.getChildCount() >= 1) {
            return Optional.of(node.getChildAtIndex(1));
        } else {
            return Optional.empty();
        }
    }

    public Optional<DelegateGetIdentifierCall> getIdentifierCall() {
        return getIdentifierNode().filter(SoyDelegateNodeHelper::isGetDelegateIdCall)
                                  .map(DelegateGetIdentifierCall::new);
    }

    public Optional<DelegateIdentifier> getIdentifier() {
        return getIdentifierCall().map(DelegateGetIdentifierCall::toIdentifier);
    }

    public Optional<Node> getVariantNode() {
        return Optional.of(node.getChildAtIndex(2));
    }

    public Optional<String> getVariant() {
        return getVariantNode().filter(Node::isString).map(Node::getString);
    }

    public DelegateReference toReference() {
        return new DelegateReference(getIdentifier().orElse(null), getVariant().orElse(null));
    }
}
