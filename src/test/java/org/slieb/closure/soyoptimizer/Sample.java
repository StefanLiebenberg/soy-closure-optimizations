package org.slieb.closure.soyoptimizer;

import com.google.template.soy.jssrc.SoyJsSrcOptions;

public class Sample {

    public static void main(String[] args) {
        BaseTemplateTest base = new BaseTemplateTest();
        base.example().compileToJsSrc(new SoyJsSrcOptions(), null).stream().forEach(System.out::println);
    }
}
