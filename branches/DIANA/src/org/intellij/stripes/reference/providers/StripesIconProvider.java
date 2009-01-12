/*
 * Copyright 2000-2009 JetBrains s.r.o.
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

package org.intellij.stripes.reference.providers;

import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.jsp.JspFile;
import org.intellij.stripes.facet.StripesFacet;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 4/01/2009
 * Time: 09:32:06 PM
 */
public class StripesIconProvider extends IconProvider {
// -------------------------- OTHER METHODS --------------------------

    @Nullable
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        StripesFacet facet = StripesUtil.getStripesFacet(StripesUtil.getModule(element));
        if (facet == null) {
            return null;
        }
        //Change Icons?
        if (facet.getConfiguration().isChangeIcons()) {
            //is JSP's?
            if (element instanceof JspFile && StripesUtil.isStripesPage(element)) {//get tags like page, taglib...
                return StripesConstants.STRIPES_JSP_ICON;
            } else if (element instanceof PsiClass) { //is class?
                PsiClass clazz = (PsiClass) element;
                boolean isActionBean = false;
                try {
                    //is abstract?
                    if (!clazz.getModifierList().hasExplicitModifier(PsiModifier.ABSTRACT)) {
                        //Is an implementation of ActionBean?
                        isActionBean = StripesUtil.isSubclass(StripesConstants.ACTION_BEAN, clazz);
                    }
                } catch (Exception e) {
                    //
                }
                return isActionBean ? StripesConstants.ACTION_BEAN_ICON : null;
            } else {
                return null;
            }
        }

        return null;
    }
}
