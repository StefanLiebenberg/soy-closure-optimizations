// This file was automatically generated from delegate_example.soy.
// Please don't edit this file by hand.

/**
 * @fileoverview Templates in namespace example.
 * @modName {X}
 * @hassoydeltemplate {SpecialTemplate}
 * @hassoydelcall {SpecialTemplate}
 * @public
 */

if (typeof example == 'undefined') { var example = {}; }


example.NormalTemplate = function(opt_data, opt_ignored, opt_ijData) {
    return soydata.VERY_UNSAFE.ordainSanitizedHtml(soy.$$escapeHtml(opt_data.NormalFoo));
};
if (goog.DEBUG) {
    example.NormalTemplate.soyTemplateName = 'example.NormalTemplate';
}


example.__deltemplate_s4_e67de936 = function(opt_data, opt_ignored, opt_ijData) {
    return soydata.VERY_UNSAFE.ordainSanitizedHtml(soy.$$escapeHtml(opt_data.SpecialFoo));
};
if (goog.DEBUG) {
    example.__deltemplate_s4_e67de936.soyTemplateName = 'example.__deltemplate_s4_e67de936';
}

soy.$$registerDelegateFn(soy.$$getDelTemplateId('SpecialTemplate'), '', 1, example.__deltemplate_s4_e67de936);
example.__deltemplate_s4_e67de936 = function(opt_data, opt_ignored, opt_ijData) {
    return soydata.VERY_UNSAFE.ordainSanitizedHtml(soy.$$escapeHtml(opt_data.SpecialFoo));
};
if (goog.DEBUG) {
    example.__deltemplate_s4_e67de936.soyTemplateName = 'example.__deltemplate_s4_e67de936';
}
soy.$$registerDelegateFn(soy.$$getDelTemplateId('SpecialTemplate'), '', 1, example.__deltemplate_s4_e67de936);


example.TemplateCall = function(opt_data, opt_ignored, opt_ijData) {
    var specialTemplateId = ;
    var specialTemplateVariant = functionX();
    var specialTempalteFn = soy.$$getDelegateFn(soy.$$getDelTemplateId('SpecialTemplate'), specialTemplateVariant, false);
    var specialTempalteFn = soy.$$getDelegateFn(soy.$$getDelTemplateId('DooDoo'), "asdf", false);
    return soydata.VERY_UNSAFE.ordainSanitizedHtml(example.NormalTemplate(opt_data, null, opt_ijData) + specialTempalteFn(opt_data, null, opt_ijData));



};
if (goog.DEBUG) {
    example.TemplateCall.soyTemplateName = 'example.TemplateCall';
}
