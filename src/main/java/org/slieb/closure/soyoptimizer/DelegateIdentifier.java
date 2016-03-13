package org.slieb.closure.soyoptimizer;

public class DelegateIdentifier {

    private final String templateName;

    public DelegateIdentifier(final String templateName) {
        this.templateName = templateName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof DelegateIdentifier)) { return false; }
        final DelegateIdentifier that = (DelegateIdentifier) o;
        return templateName != null ? templateName.equals(that.templateName) : that.templateName == null;
    }

    @Override
    public int hashCode() {
        return templateName != null ? templateName.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DelegateIdentifier{" +
                "templateName='" + templateName + '\'' +
                '}';
    }
}
