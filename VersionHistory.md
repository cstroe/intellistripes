# Note #
Unfortunately we do no have much spare time to maintain both branches stimultaneuosly.
Therefore although there's still many users that use IDEA 7.0 and still download 1.0 version we're announcing that support for 1.x branch of IntelliStripes is closed.
But this project is opened for volunteers who wanna join our team and apply decelopment skills to support 1.x branch.

# History #

### Version 0.1.0.097 (2007-07-29) ###

Initial Version
  * Basic web.xml configuration
    * StripesDispatcher
    * StripesFilter
    * Stripes - Spring Integration
  * Auto completion in JSP Stripes tags
    * beanclass attribute on:
      * form
      * errors
      * link
      * url
      * useActionBean
    * name attribute (**setter** methods) on :
      * checkox
      * file
      * hidden
      * password
      * radio
      * select
      * text
      * textarea
    * name attribute (**event** methods) on
      * button
      * image
      * submit
  * Icons for
    * Action Bean Classes
    * JSP pages with Stripes taglib

### Version 0.1.1.111 (2007-08-04) ###

  * File Templates
    * commons-logging.properties
    * log4j.properties
    * StripesResources.properties
    * Add Configuration options to Logging and Stripes Resource

### Version 0.1.2.241 (2007-08-27) ###

_Deleted from repository_
  * File Templates
  * log4j.xml
  * Add Configuration option to chose between log4j.properties and log4j.xml (**Thanks to Evgeny Shepelyuk for the idea**)

### Version 0.1.3.241 (2007-08-28) ###

  * Bug Fix NPE When Stripes Facet is Added to a module

### Version 0.3.1.294 (2007-09-23) ###

  * Open Source with Apache license
  * Auto Completion in Stripes JSP tags
    * field attribute on
      * errors
    * name attribute (**setter** methods) on
      * link-param
    * event attribute on
      * link
      * url
      * useActionBean

### Version 0.4.1.294 (2007-09-26) ###

  * Auto Completion in ActionBean Class on
    * `@SpringBean` Annotation (Only works when the module have a Spring Facet configured)
  * IntelliStripes code available on http://intellistripes.googlecode.com

### Version 0.4.3.364 (2007-11-01) ###
  * Auto Completion in Stripes JSP tags
    * name attribute on (JSP)
    * layout-render
  * Remove Icon for ActionBean Abstract Classes
  * Add Configuration Option to Configure ActionResolver.UrlFilters
  * Fixed Auto Completion for Setter methods inherited from Super Class

### Version 0.4.4.364 (2007-11-08) ###
  * Bug Fix NPE In Event (Resolution Methods) Auto Completion When the ActionBean Class have Explicit Constructors (**Thanks to Marijan J Milicevic for the feed back**)
  * Fixed Auto Completion for Event (Resolution Methods) inherited from Super Class

### Version 0.6.1.364 (2007-11-12) ###
> _Deleted From Repository_
  * File Templates
  * ActionBean.java
  * Add Action for create New ActionBean Class
  * Auto Completion in Stripes JSP tags
    * name attribute on (layout-component name defined inside layout-definition)
    * layout-component
  * Add Option to Change Icons on Facet Configuration (**Thanks to Evgeny Shepelyuk for the idea**)

### Version 0.6.2.364 (2007-11-18) ###
  * Bug Fix Not-Deterministic Assertion Failed Related with New ActionBean Action

### Version 0.7.1.576 (2007-11-29) ###
  * Stripes Libraries Configured through Facet
  * Correct misspelling in "Plugin Configuration" Tab (**Thanks to Evgeny Shepelyuk for the feed back**)

### Version 0.7.5.584 (2007-12-08) ###
  * Auto Completion in Stripes JSP tags
    * name attribute on (setter method)
    * param

### Version 0.7.6.584 (2007-12-11) ###
  * Bug Fix Not-Deterministic Exception when editing ANT files (**Thanks to David Castañeda for the feed back**)

### Version 0.7.7.626 (2008-01-08) ###
  * Auto Completion in Stripes JSP tags
    * class attribute on (CSS)
      * button
      * checkbox
      * file
      * form
      * image
      * label
      * link
      * hidden
      * option
      * options-collection
      * options-enumeration
      * password
      * radio
      * reset
      * select
      * submit
      * text
      * textarea

### Version 1.0.beta.757 (2008-01-08) ###
  * CSS and JavaScript support for all tags
  * `@HandlesEvent` support
  * Auto completion on `new ForwardResolution(FooActionBean.class,"event")` (and `RedirectResolution` too) on the event parameter
  * Nested Properties support
  * Bug-Fix on stripes:layout-component

### Version 1.0.RC1.860 (2008-06-10) ###
  * Full support for indexed properties in Stripes tag attributes
  * Full support for nested properties in Stripes tag attributes
  * Functional test project
  * Apache License for source files
  * Path support for src attribute in stripes:image tag
  * Inspections for correct stripes annotatin oplacement in Java classes with few basic intention action
  * Inspections for `Resolution` methods in Java classes with few basic intention actions
  * Support for enum attribute in stripes:options-enumeration
  * Regexp language in mask attribute of `@Validate` annotation
  * Support for resolution method names in `on` attribute of `@ValidationMethod` and `@Validate` annotations
  * Full property names support for `field` attribute in `@Validate` annotation within `@ValidateNestedProperties`
  * Basic providing of JSP pathes for one-argement `Resolution` constructors
  * Registration of `actionBean` valiable as pre-defined EL variable in JSPs
  * `FileBean` properties support in stripes:file tag
  * Gutter icon in ActionBean classes allowing navigation to class usages in JSPs
  * Registration of pathes annotated with `@UrlBinding` to be used for common HTML tags
  * Bugfixes and improvements for more reliable processing of JSP files
  * Introduced dependencies from JavaScript and IntelliLang plugins

### Version 1.0.Final.941 (2008-09-01) ###
  * Auto Complete for `new StreamingResultion()` on the `contentType` parameter
  * Support for EL on tags atributes
  * Bugfixes
  * Last Version with Stripes 1.4.3 support

### Version 1.0.1.860 (2008-10-12) ###
  * Major Refactoring dedicated to moving towards Diana (mmmmm who cares if all works :))
  * bugfixes ([Issue #38](https://code.google.com/p/intellistripes/issues/detail?id=#38), [Issue #40](https://code.google.com/p/intellistripes/issues/detail?id=#40))
  * [Issue #35](https://code.google.com/p/intellistripes/issues/detail?id=#35)
  * [Issue #41](https://code.google.com/p/intellistripes/issues/detail?id=#41)
  * bug fix related to renaming of nested property references from JSP

### Version 1.0.3.860 (2008-10-19) ###
  * bugfixes in Stripes installation into web.xml
  * Stripes facet auto-detection
  * When Stripes facet is created the parameter `ActionResolver.Packages` `init-param` is added to Stripes filter in `web.xml`
  * GUI for configuration of `ActionResolver.Packages` filter parameter
  * Auto-complete of `init-param` names within Stripes Filter in `web.xml`
  * Upgrade of plugin library requirements to:
    * Stripes 1.5
    * commons-logging to 1.1.1