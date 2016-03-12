package org.slieb.closure.soyoptimizer.node;

import com.google.javascript.rhino.Node;
import org.slieb.closure.soyoptimizer.DelegateReference;
import org.slieb.closure.soyoptimizer.DelegateRegistration;

public class DelegateRegistrationCall {

    protected final Node node;

    public DelegateRegistrationCall(final Node node) {this.node = node;}

    public DelegateGetIdentifierCall getIdentifierCall() {
        return new DelegateGetIdentifierCall(node.getChildAtIndex(1));
    }

    public String getVariant() {
        return node.getChildAtIndex(2).getString();
    }

    public String getMethodName() {
        return node.getChildAtIndex(4).getQualifiedName();
    }

    public DelegateReference getTemplateReference() {
        return new DelegateReference(getIdentifierCall().toIdentifier(), getVariant());
    }

    public double getPriority() {
        return node.getChildAtIndex(3).getDouble();
    }

    public DelegateRegistration toRegistration() {
        return new DelegateRegistration(getTemplateReference(), getMethodName(), getPriority());
    }
}
