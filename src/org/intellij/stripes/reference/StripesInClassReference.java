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

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 4/04/2008
 * Time: 04:43:28 AM
 */
public class StripesInClassReference extends StripesReference {

    protected PsiLiteralExpression expression;

    public StripesInClassReference(PsiLiteralExpression expression) {
        this.expression = expression;
    }

    public PsiElement getElement() {
        return expression;
    }

    public TextRange getRangeInElement() {
        return new TextRange(1, expression.getText().length() - 1);
    }

    @Override
    public String getCanonicalText() {
        return expression.getText();
    }
}
