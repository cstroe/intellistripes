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

import com.intellij.openapi.paths.PathReference;
import com.intellij.openapi.paths.PathReferenceProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.stripes.reference.UrlBindingReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Path Reference extention point for providing additional pathes in corresponding HTML tags attributes.
 * <p/>
 * Provider adds the reference to the list of references for HTML tags attribute (for example href attribute of &lt;a&gt;&lt;/a&gt; tag).
 * The reference provides auto-complete and resolving using values from ActionBean classes annotated with @UrlBinding.
 */
public class UrlBindingPathReferenceProvider implements PathReferenceProvider {

    /**
     * Adds reference only if element is instance of XmlAttributeValue.
     *
     * @param psiElement
     * @param references
     * @param soft
     * @return always true for other extention points to be processed.
     */
    public boolean createReferences(@NotNull PsiElement psiElement, @NotNull List<PsiReference> references, boolean soft) {
        if (psiElement instanceof XmlAttributeValue) {
            references.add(new UrlBindingReference((XmlAttributeValue) psiElement));
        }
        return true;
    }

    @Nullable
    public PathReference getPathReference(@NotNull String path, @NotNull PsiElement element) {
        return null;
    }
}
