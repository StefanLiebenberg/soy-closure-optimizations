package org.slieb.closure.soyoptimizer.node;

import com.google.javascript.rhino.Node;
import org.slieb.closure.soyoptimizer.DelegateIdentifier;

import java.util.Optional;

public class DelegateGetIdentifierCall {

    private final Node node;

    public DelegateGetIdentifierCall(final Node node) {
        this.node = node;
    }

    public Optional<String> getTemplateName() {
        return getTemplateNode().map(Node::getString);
    }

    public Optional<Node> getTemplateNode() {

        if (node.getChildCount() > 1) {
            return Optional.of(node.getChildAtIndex(1));
        } else {
            return Optional.empty();
        }
    }

    public DelegateIdentifier toIdentifier() {
        return new DelegateIdentifier(getTemplateName().orElse(null));
    }
}
