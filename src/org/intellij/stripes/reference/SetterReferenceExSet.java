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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SetterReferenceExSet extends StripesReferenceSetBase<SetterReferenceEx<PsiElement>> {

	public SetterReferenceExSet(@NotNull PsiElement element, int offset, char separator,
	                            PsiClass actionBeanPsiClass, Boolean supportBraces) {
		super(element, offset, separator, actionBeanPsiClass, supportBraces);
	}

	public SetterReferenceExSet(String str, @NotNull PsiElement element, int offset, char separator,
	                            PsiClass actionBeanPsiClass, Boolean supportBraces) {
		super(str, element, offset, separator, actionBeanPsiClass, supportBraces);
	}

	@Override
	protected List<SetterReferenceEx<PsiElement>> initRefs() {
		if (!isSupportBraces()) {
			return super.initRefs();
		}

		List<SetterReferenceEx<PsiElement>> retval = new ArrayList<SetterReferenceEx<PsiElement>>(8);
		for (int i = 0, wStart = 0, lBrace = 0, index = 0, wEnd = 0; i < getStr().length(); i++) {
			if (getStr().charAt(i) == '.' && lBrace == 0) {
				TextRange range = new TextRange(getOffset() + wStart, getOffset() + wEnd + 1);
				String refText = new TextRange(range.getStartOffset() - 1, range.getEndOffset() - 1).substring(getStr());

				if (!Pattern.compile("\\$\\{.*?\\}").matcher(refText).find()
					|| Pattern.compile("\\[.*?\\$\\{.*?\\}\\.*?\\]").matcher(refText).find()) {
					retval.add(createReferenceWithBraces(range, index++, wEnd != (i - 1)));
				} else {
					return retval;
				}

				wStart = i + 1;
			} else if (getStr().charAt(i) == '[') {
				lBrace++;
			} else if (getStr().charAt(i) == ']') {
				lBrace--;
			} else if (lBrace == 0) {
				wEnd = i;
			}

			if (i == (getStr().length() - 1)) {
				TextRange range = new TextRange(getOffset() + wStart, getOffset() + (wStart < wEnd ? wEnd + 1 : i + 1));
				String refText = new TextRange(range.getStartOffset() - 1, range.getEndOffset() - 1).substring(getStr());

				if (!Pattern.compile("\\$\\{.*?\\}").matcher(refText).find()
					|| Pattern.compile("\\[.*?\\$\\{.*?\\}\\.*?\\]").matcher(refText).find()) {
					retval.add(createReferenceWithBraces(range, index++, wEnd != i && lBrace == 0));
				}
			}
		}

		return retval;
	}

	/**
	 * This method should never be directly used.
	 *
	 * @param range
	 * @param index
	 * @return
	 */
	@NotNull
	protected final SetterReferenceEx<PsiElement> createReference(TextRange range, int index) {
		return this.createReferenceWithBraces(range, index, false);
	}

	@NotNull
	protected SetterReferenceEx<PsiElement> createReferenceWithBraces(TextRange range, int index, boolean hasBraces) {
		return new SetterReferenceEx<PsiElement>(range, isSupportBraces(), hasBraces, this, index);
	}
}
