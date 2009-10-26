package org.intellij.stripes.reference;

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

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.ReferenceSetBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class StripesReferenceSetBase<T extends PsiReference> {

    private final String str;
    private final PsiElement element;
    private final int offset;
    private final char separator;
    private final PsiClass actionBeanPsiClass;
    private final Boolean supportBraces;

    private List<T> references;

    public StripesReferenceSetBase(@NotNull final PsiElement element, final int offset, final char separator,
                                   final PsiClass actionBeanPsiClass, final Boolean supportBraces) {
        this.str = ElementManipulators.getValueText(element);
        this.element = element;
        this.offset = offset;
        this.separator = separator;
        this.actionBeanPsiClass = actionBeanPsiClass;
        this.supportBraces = supportBraces;

        this.references = initRefs();
    }

    public StripesReferenceSetBase(final String str, @NotNull final PsiElement element, final int offset, final char separator,
                                   final PsiClass actionBeanPsiClass, final Boolean supportBraces) {
        this.str = str;
        this.element = element;
        this.offset = offset;
        this.separator = separator;
        this.actionBeanPsiClass = actionBeanPsiClass;
        this.supportBraces = supportBraces;

        this.references = initRefs();
    }

    protected List<T> initRefs() {
        ReferenceSetBase<T> referenceSet = new ReferenceSetBase<T>(this.str, element, offset, separator) {
            @NotNull
            @Override
            protected T createReference(TextRange range, int index) {
                return StripesReferenceSetBase.this.createReference(range, index);
            }
        };

        return referenceSet.getReferences();
    }

    public String getStr() {
        return str;
    }

    public PsiElement getElement() {
        return element;
    }

    protected int getOffset() {
        return offset;
    }

    public Boolean isSupportBraces() {
        return supportBraces;
    }

    public PsiClass getActionBeanPsiClass() {
        return actionBeanPsiClass;
    }

    public PsiReference[] getPsiReferences() {
        return references.toArray(new PsiReference[references.size()]);
    }

    public T getReference(int index) {
        return references.get(index);
    }

    protected abstract T createReference(TextRange range, int index);
}
