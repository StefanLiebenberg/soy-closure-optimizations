package org.slieb.closure.soyoptimizer;

import java.util.Optional;

public class DelegateRegistration {

    private final DelegateReference reference;

    private final String methodName;

    private final Double priority;

    public DelegateRegistration(final DelegateReference reference,
                                final String methodName,
                                final Double priority) {
        this.reference = reference;
        this.methodName = methodName;
        this.priority = priority;
    }

    public DelegateReference getReference() {
        return reference;
    }

    public DelegateIdentifier getIdentifier() {
        return getReference().getIdentifier().get();
    }

    public Optional<String> getVariant() {
        return getReference().getVariant();
    }

    public String getMethodName() {
        return methodName;
    }

    public Double getPriority() {
        return priority;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof DelegateRegistration)) { return false; }

        final DelegateRegistration that = (DelegateRegistration) o;

        if (reference != null ? !reference.equals(that.reference) : that.reference != null) { return false; }
        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) { return false; }
        return priority != null ? priority.equals(that.priority) : that.priority == null;
    }

    @Override
    public int hashCode() {
        int result = reference != null ? reference.hashCode() : 0;
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DelegateRegistration{" +
                "methodName='" + methodName + '\'' +
                ", reference=" + reference +
                ", priority=" + priority +
                '}';
    }
}
