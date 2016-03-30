# History #
<a href='Hidden comment: 
===Version 2.0.20 (2009-xx-xx)===
* On project/facet addition following directories are created under web root
** /js
** /css
** /WEB-INF/views
* default layout is created on project/facet addtion in /WEB/INF/views/layout directory
* commons-fileupload is mandatory dependency now
'></a>

### Version 2.0.8 (2009-10-22) ###
  * Inspection for correct values in `minvalue`/'maxvalue', `minlength`/`maxlength` attributes of `@Validate` annotation
  * [Issue #65](https://code.google.com/p/intellistripes/issues/detail?id=#65)

### Version 2.0.7 (2009-10-18) ###
  * [Issue #64](https://code.google.com/p/intellistripes/issues/detail?id=#64)

### Version 2.0.6 (2009-04-21) ###
  * Improve declaration of implicit actionBean variable basing on useActionBean/form tags
  * Full support for EL in `expression` in `@Validate` annotation.
  * Stripes-Reload extension support
  * File upload library only supports commons-fileupload. COS is not used anymore

### Version 2.0.3 (2009-04-14) ###
  * [Issue #63](https://code.google.com/p/intellistripes/issues/detail?id=#63)
  * Initial support for EL in `expression` in `@Validate` annotation. `this` is added as auto-complete variant

### Version 2.0.2 (2009-03-21) ###
  * [Issue #62](https://code.google.com/p/intellistripes/issues/detail?id=#62)
  * [Issue #52](https://code.google.com/p/intellistripes/issues/detail?id=#52)

### Version 2.0 (2009-03-15) ###
  * Autocomplete on `<s:param name="">` inside `<s:form>`
  * Drop support for `<s:link-param>`
  * Provide option of adding `context-param` to `web.xml` indicating usage of Stripes resource as default bundle for web application
  * Quick fix for inspection "`@SpringBean` applied to public setter"
  * Actions for generating elements within `ActionBean` classes
    * correct `@SpringBean` injection method (prefixed with inject)
  * Correct handling of search and rename of references to `Action Bean` setters
  * Optionally select file upload support library from plugin facet setting
  * [Issue #48](https://code.google.com/p/intellistripes/issues/detail?id=#48), [Issue #51](https://code.google.com/p/intellistripes/issues/detail?id=#51), [Issue #46](https://code.google.com/p/intellistripes/issues/detail?id=#46), [Issue #47](https://code.google.com/p/intellistripes/issues/detail?id=#47), [Issue #49](https://code.google.com/p/intellistripes/issues/detail?id=#49), [Issue #54](https://code.google.com/p/intellistripes/issues/detail?id=#54), [Issue #56](https://code.google.com/p/intellistripes/issues/detail?id=#56), [Issue #57](https://code.google.com/p/intellistripes/issues/detail?id=#57), [Issue #50](https://code.google.com/p/intellistripes/issues/detail?id=#50), [Issue #55](https://code.google.com/p/intellistripes/issues/detail?id=#55), [Issue #58](https://code.google.com/p/intellistripes/issues/detail?id=#58), [Issue #60](https://code.google.com/p/intellistripes/issues/detail?id=#60), [Issue #61](https://code.google.com/p/intellistripes/issues/detail?id=#61), [Issue #59](https://code.google.com/p/intellistripes/issues/detail?id=#59)
  * Plugin code dependency on Apache Commons Lang is introduced

### Version 2.0.RC3.164 (2009-01-12) ###
  * Actions to Create Stripes Classes:
    * `ActionBeanContext`
    * `ActionBeanContextFactory`
    * `Interceptor`
  * Complete Support for `@Wizard`
  * Complete Support for `@StrictBinding`
  * Clean URL in `@UrlBinding` now support nested properties
  * Auto-complete of `init-param` names within Stripes Filter in `web.xml` updated to Stripes 1.5
  * Stripes Icons return
  * Minor Bug Fixes

### Version 2.0.RC2.013 (2008-11-20) ###
  * Actions for generating elements within `ActionBean` classes
    * `Resolution`
    * `@ValidationMethod`
    * `@Before`/`@After` methods
  * removed creating of `context-param` and Spring Context Listener when adding Spring Support
  * `mime-type` autocomplete in `StreamingResolution` constructor when its is serves as anonymous class constructor
  * initial support for Clean URL in `@UrlBinding`. Auto complete works but URL are not correctly resolved from JSP
  * fixed registration of `actionBean` as implicit EL variable
  * More valid `actionBean` EL implicit variable proessing
  * fixed duplicate property names in auto-complete for `ActionBean` setters

### Version 2.0.RC2.013 (2008-11-20) ###
  * Actions for generating elements within `ActionBean` classes
    * `Resolution`
    * `@ValidationMethod`
    * `@Before`/`@After` methods
  * removed creating of `context-param` and Spring Context Listener when adding Spring Support
  * `mime-type` autocomplete in `StreamingResolution` constructor when its is serves as anonymous class constructor
  * initial support for Clean URL in `@UrlBinding`. Auto complete works but URL are not correctly resolved from JSP
  * fixed registration of `actionBean` as implicit EL variable
  * More valid `actionBean` EL implicit variable proessing
  * fixed duplicate property names in auto-complete for `ActionBean` setters

### Version 2.0.beta2.664 (2008-10-14) ###
  * Major Refactoring dedicated to moving towards Diana (mmmmm who cares if all works :))
  * bugfixes ([Issue #38](https://code.google.com/p/intellistripes/issues/detail?id=#38), [Issue #39](https://code.google.com/p/intellistripes/issues/detail?id=#39), [Issue #40](https://code.google.com/p/intellistripes/issues/detail?id=#40))
  * [Issue #35](https://code.google.com/p/intellistripes/issues/detail?id=#35)
  * [Issue #41](https://code.google.com/p/intellistripes/issues/detail?id=#41)
  * bug fix related to renaming of nested property references from JSP
  * new settings persistence

### Version 2.0.beta1.664 (2008-09-27) ###

Initial version. Port of version 1.0.Final to Diana