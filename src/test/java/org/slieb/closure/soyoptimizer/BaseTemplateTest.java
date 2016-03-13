package org.slieb.closure.soyoptimizer;

import com.google.javascript.jscomp.SourceFile;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class BaseTemplateTest {

    public SoyFileSet packageA() {
        SoyFileSet.Builder builder = SoyFileSet.builder();
        Stream.of("/templates/packageA/default.soy", "/templates/packageA/delpackage_override.soy")
              .map(s -> getClass().getResource(s))
              .forEach(builder::add);
        return builder.build();
    }

    public SoyFileSet packageB() {
        SoyFileSet.Builder builder = SoyFileSet.builder();
        Stream.of("/templates/packageB/default.soy")
              .map(s -> getClass().getResource(s))
              .forEach(builder::add);
        return builder.build();
    }

    public SoyFileSet packageC() {
        SoyFileSet.Builder builder = SoyFileSet.builder();
        Stream.of("/templates/packageC/default.soy")
              .map(s -> getClass().getResource(s))
              .forEach(builder::add);
        return builder.build();
    }

    public List<SourceFile> getInputs(SoyFileSet fileSet) {
        final List<SourceFile> inputs = new ArrayList<>();

        final SoyJsSrcOptions options = new SoyJsSrcOptions();
        fileSet.compileToJsSrc(options, null).forEach(string -> {
            inputs.add(SourceFile.fromCode(Objects.hash(string) + ".js", string));
        });
        return inputs;
    }
}
