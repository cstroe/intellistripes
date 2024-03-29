/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.intellij.stripes.reference.contributors;

import com.intellij.javaee.model.common.JavaeeCommonConstants;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.filters.*;
import com.intellij.psi.filters.position.NamespaceFilter;
import com.intellij.psi.filters.position.ParentElementFilter;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.xml.XmlTag;
import org.intellij.stripes.reference.StaticReference;
import org.intellij.stripes.reference.filters.NewStreamingResolutionFilter;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;

public class StaticReferenceContributor {
    private static final String[] STRIPES_FILTER_INIT_PARAMS = {
            StripesConstants.ACTION_RESOLVER_URL_FILTER,
            StripesConstants.ACTION_RESOLVER_PACKAGES,
            "ActionResolver.Class",
            "ActionBeanPropertyBinder.Class",
            "ActionBeanContextFactory.Class",
            "ExceptionHandler.Class",
            "FormatterFactory.Class",
            StripesConstants.INTERCEPTOR_CLASSES,
            "LocalePicker.Class",
            "LocalizationBundleFactory.Class",
            "MultipartWrapperFactory.Class",
            "PopulationStrategy.Class",
            "TagErrorRendererFactory.Class",
            "TypeConverterFactory.Class",
            "ActionBeanContext.Class",
            "DelegatingExceptionHandler.UrlFilters",
            "DelegatingExceptionHandler.PackageFilters",
            "LocalePicker.Locales",
            "LocalizationBundleFactory.ErrorMessageBundle",
            "LocalizationBundleFactory.FieldNameBundle",
            "FileUpload.MaximumPostSize",
            "MultipartWrapper.Class",
            "Validation.InvokeValidateWhenErrorsExist",
            "Extension.Packages"
    };

    private static final String[] MIME_TYPES = {
            "application/envoy",
            "application/fractals",
            "application/futuresplash",
            "application/hta",
            "application/internet-property-stream",
            "application/mac-binhex40",
            "application/msword",
            "application/octet-stream",
            "application/oda",
            "application/olescript",
            "application/pdf",
            "application/pics-rules",
            "application/pkcs10",
            "application/pkix-crl",
            "application/postscript",
            "application/rtf",
            "application/set-payment-initiation",
            "application/set-registration-initiation",
            "application/vnd.ms-excel",
            "application/vnd.ms-outlook",
            "application/vnd.ms-pkicertstore",
            "application/vnd.ms-pkiseccat",
            "application/vnd.ms-pkistl",
            "application/vnd.ms-powerpoint",
            "application/vnd.ms-project",
            "application/vnd.ms-works",
            "application/winhlp",
            "application/x-bcpio",
            "application/x-cdf",
            "application/x-compress",
            "application/x-compressed",
            "application/x-cpio",
            "application/x-csh",
            "application/x-director",
            "application/x-dvi",
            "application/x-gtar",
            "application/x-gzip",
            "application/x-hdf",
            "application/x-internet-signup",
            "application/x-internet-signup",
            "application/x-iphone",
            "application/x-javascript",
            "application/x-latex",
            "application/x-msaccess",
            "application/x-mscardfile",
            "application/x-msclip",
            "application/x-msdownload",
            "application/x-msmediaview",
            "application/x-msmetafile",
            "application/x-msmoney",
            "application/x-mspublisher",
            "application/x-msschedule",
            "application/x-msterminal",
            "application/x-mswrite",
            "application/x-netcdf",
            "application/x-perfmon",
            "application/x-pkcs12",
            "application/x-pkcs7-certificates",
            "application/x-pkcs7-certreqresp",
            "application/x-pkcs7-mime",
            "application/x-pkcs7-signature",
            "application/x-sh",
            "application/x-shar",
            "application/x-shockwave-flash",
            "application/x-stuffit",
            "application/x-sv4cpio",
            "application/x-sv4crc",
            "application/x-tar",
            "application/x-tcl",
            "application/x-tex",
            "application/x-texinfo",
            "application/x-troff",
            "application/x-troff-man",
            "application/x-troff-me",
            "application/x-troff-ms",
            "application/x-ustar",
            "application/x-wais-source",
            "application/x-x509-ca-cert",
            "application/ynd.ms-pkipko",
            "application/zip",
            "audio/basic",
            "audio/mid",
            "audio/mpeg",
            "audio/x-aiff",
            "audio/x-mpegurl",
            "audio/x-pn-realaudio",
            "audio/x-wav",
            "image/bmp",
            "image/cis-cod",
            "image/gif",
            "image/ief",
            "image/jpeg",
            "image/pipeg",
            "image/svg+xml",
            "image/tiff",
            "image/x-cmu-raster",
            "image/x-cmx",
            "image/x-icon",
            "image/x-portable-anymap",
            "image/x-portable-bitmap",
            "image/x-portable-graymap",
            "image/x-portable-pixmap",
            "image/x-rgb",
            "image/x-xbitmap",
            "image/x-xpixmap",
            "image/x-xwindowdump",
            "message/rfc822",
            "text/css",
            "text/h323",
            "text/html",
            "text/iuls",
            "text/plain",
            "text/richtext",
            "text/scriptlet",
            "text/tab-separated-values",
            "text/webviewhtml",
            "text/x-component",
            "text/x-setext",
            "text/x-vcard",
            "text/xml",
            "video/mpeg",
            "video/quicktime",
            "video/x-la-asf",
            "video/x-ms-asf",
            "video/x-msvideo",
            "video/x-sgi-movie",
            "x-world/x-vrml"};

    private static final ElementFilter INIT_PARAM_FILTER = new ScopeFilter(new ParentElementFilter(
            new AndFilter(
                    new NamespaceFilter(JavaeeCommonConstants.JAVAEE_NAMESPACE),
                    new ClassFilter(XmlTag.class),
                    new TextFilter("filter"),
                    new ElementFilter() {
                        public boolean isAcceptable(Object element, PsiElement context) {
                            for (XmlTag xmlTag : ((XmlTag) element).getSubTags()) {
                                if ("filter-class".equals(xmlTag.getName()) && StripesConstants.STRIPES_FILTER_CLASS.equals(xmlTag.getValue().getText())) {
                                    return true;
                                }
                            }
                            return false;
                        }

                        public boolean isClassAcceptable(Class hintClass) {
                            return XmlTag.class.isAssignableFrom(hintClass);
                        }
                    }
            ), 2));

    public void registerReferenceProviders(ReferenceProvidersRegistry registry) {
        registry.registerXmlTagReferenceProvider(new String[]{"param-name"}, INIT_PARAM_FILTER, true, new PsiReferenceProviderBase() {
            @NotNull
            public PsiReference[] getReferencesByElement(PsiElement psiElement) {
                return new PsiReference[]{new StaticReference(psiElement, STRIPES_FILTER_INIT_PARAMS)};
            }
        });

        registry.registerReferenceProvider(
                new ParentElementFilter(new AndFilter(new ClassFilter(PsiExpressionList.class), new NewStreamingResolutionFilter())),
                PsiLiteralExpression.class,
                new PsiReferenceProviderBase() {
                    @NotNull
                    public PsiReference[] getReferencesByElement(PsiElement psiElement) {
                        return new PsiReference[]{new StaticReference(psiElement, MIME_TYPES)};
                    }
                });
    }
}
