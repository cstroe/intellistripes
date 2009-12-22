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

package org.intellij.stripes.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PropertyUtil;
import org.apache.commons.lang.ArrayUtils;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class GetterReference extends PsiReferenceBase<PsiElement> {

	protected PsiClass actionBeanPsiClass;

	private String[] variants;

	public GetterReference(PsiElement psiElement, TextRange range, PsiClass actionBeanPsiClass, String... variants) {
		super(psiElement, range);
		this.actionBeanPsiClass = actionBeanPsiClass;
		this.variants = variants;
	}

	public GetterReference(PsiElement psiElement, PsiClass actionBeanPsiClass) {
		super(psiElement);
		this.actionBeanPsiClass = actionBeanPsiClass;
	}

	@Nullable
	public PsiElement resolve() {
		String value = getValue();
		if (!ArrayUtils.contains(variants, value)) {
			return PropertyUtil.findPropertyGetter(actionBeanPsiClass, value, false, true);
		}
		return myElement;
	}

	public Object[] getVariants() {
		String[] retval = PropertyUtil.getReadableProperties(actionBeanPsiClass, true);
		if (variants != null) retval = (String[]) ArrayUtils.addAll(retval, variants);

		return StripesReferenceUtil.getVariants(Arrays.asList(retval), StripesConstants.FIELD_ICON);
	}
}