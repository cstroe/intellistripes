# Features #

  * ~~Provide option to allow installation of `Stripes Reload` extension.~~
  * Provide auto-complete of proper class names within `param-values` sub tags of Stripes filter configuraiton parameters in `web.xml` _is is really important ? :)_
  * If `ActionResolver.Packages` is configured auto-complete variants of `beanclass` attributes in stripes custom tags should be limited only by those packages. _kinda important feature but supposedly needs a lot of work_
  * detect if spring support is configured from web.xml not from facet setting. _i suppose this should be removed as useless_
  * auto-complete of `label`, `value` and `sort` attributes in `options-collection` tags. _this need resolve of EL variable class to get its methods_
  * auto-complete of `label`, `value` and `sort` attributes in `options-map` tags. _this need resolve of EL variable class to get its methods_
  * improve support for `sort`, `label` attributes in `stripes:options-enumeration` by nested properties support
  * improve support of setter references. now only setter methods are resolved in HTML tags etc, but Stripes actually allows getters and even properties.
  * ~~Better support of JSTL in `expression` attribute of `@Validate` annotation. This means that this should be available and all auto-completion should work.~~
  * `[STS-629]` - The "field-metadata" tag should provide the "on" property value