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

package org.intellij.stripes.components.application;

import com.intellij.facet.FacetTypeRegistry;
import com.intellij.ide.IconProvider;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.jsp.JspDirectiveKind;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.xml.XmlTag;
import org.intellij.stripes.facet.StripesFacetType;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 2/07/2007 Time: 01:57:05 AM
 */
public class StripesApplicationComponent implements ApplicationComponent, IconProvider
{

    @NotNull
    public String getComponentName()
    {
        return "Stripes Application Component";
    }


    public void initComponent()
    {
        //Register a new Facet Type
        FacetTypeRegistry.getInstance().registerFacetType(StripesFacetType.INSTANCE);
    }

    public void disposeComponent()
    {

    }

    @Nullable
    public Icon getIcon(@NotNull PsiElement element, int flags)
    {

        //is JSP's?
        if (element instanceof JspFile)
        {
            JspFile jspFile = (JspFile) element;
            //get tags like page, taglib...
            XmlTag[] tags = jspFile.getDirectiveTags(JspDirectiveKind.TAGLIB, true);
            boolean[] isStripesPageList = new boolean[tags.length];
            for (int i = 0; i < tags.length; i++)
            {
                XmlTag tag = tags[i];
                String uri = tag.getAttributeValue("uri");
                try
                {
                    //this tag is a Stripes declaration taglib?
                    isStripesPageList[i] = uri.equals(StripesConstants.STRIPES_DYNAMIC_TLD) || uri.equals(StripesConstants.STRIPES_TLD);
                }
                catch (Exception e)
                {
                    //
                }
            }

            for (boolean isStripes : isStripesPageList)
            {
                //if anyone return Stripes Jsp Icon
                if (isStripes)
                {
                    return StripesConstants.STRIPES_JSP_ICON;
                }
            }
        }
        //is class?
        else if (element instanceof PsiClass)
        {
            PsiClass clazz = (PsiClass) element;

            boolean isActionBean = false;
            try
            {
                //Is an implementation of ActionBean?
                isActionBean = StripesUtil.isSubclass(clazz, StripesConstants.STRIPES_ACTION_BEAN_CLASS);
            }
            catch (Exception e)
            {
                //
            }

            if (isActionBean)
            {
                return StripesConstants.ACTION_BEAN_ICON;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }

        return null;
    }
}