# What is planning to do in release before Stripes 1.5 goes final #

## Intentions/Inspections ##

  * ~~`ActionBean` with more than one Resolution method not define `@DefaultHandler`~~
  * ~~`ActionBean` with more than one `@DefaultHandler`~~
  * ~~`ActionBean` with only one Resolution Method define `@DefaultHandler`~~
  * ~~Duplicated definition on `@HandlesEvent`~~
  * ~~`@SpringBean` Annotated method start with "set" preffix~~
  * ~~`@Validate` annotation having field attribute when not inside `@ValidateNestedProperties`~~
  * ~~`@Validate`, `@DefaultHandler`, `@HandlesEvent` annotations applied to invalid class member~~
  * ~~`@HandlesEvent` declared with same name as Resolution method~~

## Auto complete ##

  * ~~Method names in on attribute of `@ValidationMethod` annotation~~
  * ~~Method names in on attribute of `@Validate` annotation~~
  * ~~Auto complete of field attribute in `@Validate` annotation within `@ValidateNestedProperties`~~
  * ~~JSP Path references in Resolution one argument constructors~~ (basic support, having some usability issues)
  * ~~Enums in stripes:options-enumeration tag~~
  * ~~CSS language injection on all stripes:tags in style attribute when is defined~~
  * ~~JS language injection on all stripes:tags in onXXX attribute~~
  * ~~Path auto-complete/resolve in src attribute of stripes:image tag~~

## Editor ##

  * ~~Gutters for `ActionBean` classes allowing navigation to form, link, useActionBean or URL that use certain `ActionBean`~~
  * ~~Register `@UrlBinding` path on starup so they can be used from ordinary HTML~~
  * ~~Add regexp validation into mask attribute of `@Validate` annotation~~