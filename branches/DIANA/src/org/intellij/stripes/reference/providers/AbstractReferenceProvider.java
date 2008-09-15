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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.ReferenceType;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 4/07/2007 Time: 12:15:26 AM
 */
public abstract class AbstractReferenceProvider implements PsiReferenceProvider {

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReferenceProvider ---------------------

    @Deprecated
    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement, ReferenceType referenceType) {
        return PsiReference.EMPTY_ARRAY;
    }

    @Deprecated
    @NotNull
    public PsiReference[] getReferencesByString(String s, PsiElement psiElement, ReferenceType referenceType, int i) {
        return PsiReference.EMPTY_ARRAY;
    }

    public void handleEmptyContext(PsiScopeProcessor psiScopeProcessor, PsiElement psiElement) {

    }
}
