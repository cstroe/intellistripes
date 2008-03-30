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

package org.intellij.stripes.util;

import com.intellij.facet.FacetManager;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.jsp.JspDirectiveKind;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.ui.HyperlinkLabel;
import org.intellij.stripes.facet.StripesFacet;
import org.jetbrains.annotations.NonNls;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 2/07/2007 Time: 02:04:03 AM
 */
public final class StripesUtil {
// -------------------------- STATIC METHODS --------------------------

    public static boolean isSubclass(PsiClass clazz, String superClass) {
        if (clazz == null) {
            return false;
        }
        boolean b = false;
        try {
            b = clazz.getQualifiedName().equals(superClass);
        }
        catch (NullPointerException e) {
            //
        }
        if (b) {
            return true;
        } else {
            if (isSubclass(clazz.getSupers(), superClass)) {
                return true;
            } else if (isSubclass(clazz.getInterfaces(), superClass)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean isSubclass(PsiClass[] supers, String superClass) {
        for (PsiClass aSuper : supers) {
            if (isSubclass(aSuper, superClass)) {
                return true;
            }
        }
        return false;
    }

    public static String[] getStringsArray(String... strings) {
        return strings;
    }

    public static <T> T[] makeArray(T... parameters) {
        return parameters;
    }

    /**
     * Is Stripes Facet Configured
     *
     * @param module Module
     * @return true or false (D'oh)
     */
    public static boolean isStripesFacetConfigured(Module module) {
        if (module == null) {
            return false;
        }
        StripesFacet stripesFacet = getStripesFacet(module);
        return stripesFacet != null;
    }

    /**
     * Get Stripes Facet
     *
     * @param module Module
     * @return StripesFacetObject
     */
    public static StripesFacet getStripesFacet(Module module) {
        if (module != null) {
            FacetManager facetManager = FacetManager.getInstance(module);
            return facetManager.findFacet(StripesFacet.FACET_TYPE_ID, "Stripes");
        } else {
            return null;
        }
    }

    /**
     * Get a Module
     *
     * @param psiElement PsiElement
     * @return Module
     */
    public static Module getModule(PsiElement psiElement) {
        try {
            return ModuleUtil.findModuleForPsiElement(psiElement);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Create a HyperLink
     *
     * @param text Text
     * @param url  Url
     * @return a HyperLinkLaber, when the user Click on it IntelliJ Open a Navigator with the URL
     */
    public static HyperlinkLabel createLink(final String text, final @NonNls String url) {
        final HyperlinkLabel link = new HyperlinkLabel(text);
        link.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                BrowserUtil.launchBrowser(url);
            }
        });
        return link;
    }

    /**
     * get the stripes namespace for a JspFile
     *
     * @param jspFile JspFile
     * @return the namespace, null if this page don't have stripes taglib
     */
    public static String getStripesNamespace(JspFile jspFile) {
        if (isStripesPage(jspFile)) {
            XmlTag[] tags = jspFile.getDirectiveTags(JspDirectiveKind.TAGLIB, true);
            for (XmlTag tag : tags) {
                String uri = tag.getAttributeValue("uri");
                assert uri != null;
                if (uri.equals(StripesConstants.STRIPES_DYNAMIC_TLD) || uri.equals(StripesConstants.STRIPES_TLD)) {
                    return tag.getAttributeValue("prefix");
                }
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * This Jsp have a Stripes Taglib declared
     *
     * @param jspFile JspFile
     * @return true or false
     */
    public static boolean isStripesPage(JspFile jspFile) {
        XmlTag[] tags = jspFile.getDirectiveTags(JspDirectiveKind.TAGLIB, true);
        boolean[] isStripesPageList = new boolean[tags.length];
        for (int i = 0; i < tags.length; i++) {
            XmlTag tag = tags[i];
            String uri = tag.getAttributeValue("uri");
            try {
                //this tag is a Stripes declaration taglib?
                isStripesPageList[i] = uri.equals(StripesConstants.STRIPES_DYNAMIC_TLD) || uri.equals(StripesConstants.STRIPES_TLD);
            }
            catch (Exception e) {
                //
            }
        }

        for (boolean isStripes : isStripesPageList) {
            if (isStripes) {
                return true;
            }
        }
        return false;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    private StripesUtil() {
    }
}
