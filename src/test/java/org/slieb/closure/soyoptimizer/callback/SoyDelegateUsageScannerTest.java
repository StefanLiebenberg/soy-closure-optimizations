package org.slieb.closure.soyoptimizer.callback;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import org.junit.Before;
import org.junit.Test;
import org.slieb.closure.soyoptimizer.BaseTemplateTest;
import org.slieb.closure.soyoptimizer.DelegateIdentifier;
import org.slieb.closure.soyoptimizer.DelegateReference;
import org.slieb.closure.soyoptimizer.DelegateRegistration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.javascript.jscomp.SourceFile.fromCode;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.slieb.closure.soyoptimizer.SoyDelegateNodeHelper.GET_DELEGATE_FN;
import static org.slieb.closure.soyoptimizer.SoyDelegateNodeHelper.GET_DELEGATE_ID;

public class SoyDelegateUsageScannerTest extends BaseTemplateTest {

    SoyDelegateUsageScanner scanner;

    @Before
    public void setUp() throws Exception {
        scanner = new SoyDelegateUsageScanner();
    }

    private void parseAndVisit(final SourceFile file) {
        Compiler compiler = new Compiler();
        Node node = compiler.parse(file);
        NodeTraversal.traverseTyped(compiler, node, scanner);
    }

    @Test
    public void testGetUsedTemplatesVariant1() {
        parseAndVisit(fromCode("template.js", toCode(packageC())));
        final Set<DelegateReference> userReferences = getUsedRegistrations();
        assertTrue(userReferences.contains(toReference("templateName_001", "")));
        assertTrue(userReferences.contains(toReference("templateName_001", "variant_1")));
        assertTrue(userReferences.contains(toReference("templateName_001", "variant_2")));
    }

    @Test
    public void testGetUsedTemplatesVariant2() {
        parseAndVisit(fromCode("template.js", toCode(packageC())));
        final Set<DelegateReference> userReferences = getUsedRegistrations();
        assertTrue(userReferences.contains(toReference("templateName_002", "")));
        assertTrue(userReferences.contains(toReference("templateName_002", "variant_1")));
        assertTrue(userReferences.contains(toReference("templateName_002", "variant_2")));
    }

    @Test
    public void testGetUsedTemplatesVariant3() {
        parseAndVisit(fromCode("template.js", toCode(packageC())));
        final Set<DelegateReference> userReferences = getUsedRegistrations();
        assertTrue(userReferences.contains(toReference("templateName_003", "")));
        assertTrue(userReferences.contains(toReference("templateName_003", "variant_1")));
        assertFalse(userReferences.contains(toReference("templateName_003", "variant_2")));
    }

    @Test
    public void testGetUsedTemplatesVariant4() {
        parseAndVisit(fromCode("template.js", toCode(packageC())));
        final Set<DelegateReference> userReferences = getUsedRegistrations();
        assertTrue(userReferences.contains(toReference("templateName_004", "")));
        assertFalse(userReferences.contains(toReference("templateName_004", "variant_1")));
        assertFalse(userReferences.contains(toReference("templateName_004", "variant_2")));
    }

    private Set<DelegateReference> getUsedRegistrations() {
        return scanner.getUsedRegistrations().stream()
                      .map(DelegateRegistration::getReference)
                      .collect(toSet());
    }

    @Test
    public void testGetUsedTemplatesVariant4_WithCustom() {
        testGetUsedTemplatesVariant4();

        parseAndVisit(fromCode("id.js", String.format("var id = %s('templateName_004');", GET_DELEGATE_ID)));
        parseAndVisit(fromCode("useVariant1.js", String.format("var template1 = %s(id, '', true);", GET_DELEGATE_FN)));
        parseAndVisit(fromCode("useVariant2.js", String.format("var template2 = %s(id, 'variant_2', true);", GET_DELEGATE_FN)));

        final Set<DelegateReference> userReferences = getUsedRegistrations();

        assertTrue(userReferences.contains(toReference("templateName_004", "")));
        assertFalse(userReferences.contains(toReference("templateName_004", "variant_1")));
        assertTrue(userReferences.contains(toReference("templateName_004", "variant_2")));
    }

    private DelegateReference toReference(final String templateName,
                                          final String templateVariant) {return new DelegateReference(new DelegateIdentifier(templateName), templateVariant);}

    private String toCode(final SoyFileSet soyFileSet) {
        SoyJsSrcOptions options = new SoyJsSrcOptions();
        List<String> result = soyFileSet.compileToJsSrc(options, null);
        return result.stream().collect(Collectors.joining());
    }
}