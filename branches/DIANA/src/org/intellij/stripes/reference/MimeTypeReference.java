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

package org.intellij.stripes.reference;

import com.intellij.psi.PsiLiteralExpression;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 23/08/2008
 * Time: 11:58:59 PM
 */
public class MimeTypeReference extends JavaStringReference {
    public MimeTypeReference(PsiLiteralExpression expression) {
        super(expression);
    }

    @Override
    public Object[] getVariants() {
        return new Object[]{"application/envoy",
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
    }
}