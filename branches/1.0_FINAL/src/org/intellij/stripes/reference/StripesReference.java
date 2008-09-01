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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 4/07/2007 Time: 12:57:24 AM
 */
public abstract class StripesReference implements PsiReference {
// ------------------------------ FIELDS ------------------------------

    private static final Object[] EMPTY_OBJECT = new Object[0];

// -------------------------- STATIC METHODS --------------------------

    // ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReference ---------------------

    @Nullable
    public PsiElement resolve() {
        return null;
    }

    public String getCanonicalText() {
        return null;
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return null;
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;
    }

    public boolean isReferenceTo(PsiElement element) {
        return element == resolve();
    }

    public Object[] getVariants() {
        return EMPTY_OBJECT;
    }

    public boolean isSoft() {
        return false;
    }
}
