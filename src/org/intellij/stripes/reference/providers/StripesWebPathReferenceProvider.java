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

package org.intellij.stripes.reference.providers;

import com.intellij.facet.FacetManager;
import com.intellij.javaee.web.WebRoot;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.paths.PathReferenceManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.WebPathReferenceProvider;
import org.intellij.stripes.facet.StripesFacet;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 7/10/2007 Time: 12:44:49 AM
 */
@Deprecated
public class StripesWebPathReferenceProvider extends WebPathReferenceProvider
{
    @Override
    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement)
    {
        Module module = ModuleUtil.findModuleForPsiElement(psiElement);
        if(module != null)
        {
            FacetManager facetManager = FacetManager.getInstance(module);
            StripesFacet stripesFacet = facetManager.findFacet(StripesFacet.FACET_TYPE_ID, "Stripes");
            WebFacet webFacet = stripesFacet.getWebFacet();
            List<WebRoot> list = webFacet.getWebRoots(true);
            WebRoot webRoot = list.get(0);
            PsiDirectory directory = PsiManager.getInstance(module.getProject()).findDirectory(webRoot.getFile());           
            //return PathReferenceManager.getReferencesFromProvider(new GlobalPathReferenceProvider(), psiElement,true);            
            return PathReferenceManager.getInstance().createReferences(directory.getChildren()[0], true);
        }
        return PathReferenceManager.getInstance().createReferences(psiElement, true);
    }
}