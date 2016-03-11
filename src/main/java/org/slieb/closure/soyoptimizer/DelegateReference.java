package org.slieb.closure.soyoptimizer;

import java.util.Objects;
import java.util.Optional;

public class DelegateReference {

    private final DelegateIdentifier identifier;

    private final String variant;

    public DelegateReference(final DelegateIdentifier identifier,
                             final String variant) {
        this.identifier = identifier;
        this.variant = variant;
    }

    public Optional<DelegateIdentifier> getIdentifier() {
        return Optional.ofNullable(identifier);
    }

    public Optional<String> getVariant() {
        return Optional.ofNullable(variant);
    }

    public boolean isDefaultReferenceOf(DelegateReference reference) {
        return Objects.equals(identifier, reference.identifier) && this.variant.isEmpty();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof DelegateReference)) { return false; }

        final DelegateReference that = (DelegateReference) o;

        if (identifier != null ? !identifier.equals(that.identifier) : that.identifier != null) { return false; }
        return variant != null ? variant.equals(that.variant) : that.variant == null;
    }

    @Override
    public int hashCode() {
        int result = identifier != null ? identifier.hashCode() : 0;
        result = 31 * result + (variant != null ? variant.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DelegateReference{" +
                "identifier=" + identifier +
                ", variant='" + variant + '\'' +
                '}';
    }
}
