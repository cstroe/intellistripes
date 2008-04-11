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

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 21/09/2007 Time: 01:28:16 AM
 */
public class InClassResolutionMethodsReference extends StripesInClassReference {
// ------------------------------ FIELDS ------------------------------

    private PsiClass actionBeanPsiClass;

// --------------------------- CONSTRUCTORS ---------------------------

    public InClassResolutionMethodsReference(PsiLiteralExpression expression, PsiClass actionBeanPsiClass) {
        super(expression);
        this.actionBeanPsiClass = actionBeanPsiClass;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * Get all Posible Resolution Methods for an ActionBean Class
     *
     * @return an Array with References to Resolution Methods
     */
    @Override
    public Object[] getVariants() {
        return StripesPsiReferenceHelper.getVariants(StripesReferenceUtil.getResolutionMethodsNames(actionBeanPsiClass), "", StripesConstants.RESOLUTION_ICON);
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReference ---------------------

    /**
     * Ctrl + Click in the attribute name will be resolved
     *
     * @return Element
     */
    @Nullable
    @Override
    public PsiElement resolve() {
        for (PsiMethod method : StripesReferenceUtil.getResolutionMethods(actionBeanPsiClass).values()) {
            if (getCanonicalText().equals(method.getName())
                    || getCanonicalText().equals(StripesReferenceUtil.resolveHandlesEventAnnotation(method))) {
                return method;
            }
        }
        return null;
    }

    /**
     * When Method will renamed
     *
     * @param newElementName the new methodName
     * @return Element
     * @throws com.intellij.util.IncorrectOperationException
     *
     */
    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return StripesReferenceUtil.getManipulator(expression).handleContentChange(expression, newElementName);
    }


}