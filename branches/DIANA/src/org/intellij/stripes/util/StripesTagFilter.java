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

package org.intellij.stripes.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.xml.XmlTag;

/**
 * Base class for filters that can process stripes tags.
 *
 * <br>Implements filtering by taglib declaration uri.
 * Checks if taglib uri starts with valid prefix.<br>
 *
 * To implement custom filtering one need overriding {@link #isDetailsAccepted(com.intellij.psi.xml.XmlTag)} method.
 */
public abstract class StripesTagFilter implements PsiElementFilter {
    public boolean isAccepted(PsiElement element) {
        return element instanceof XmlTag
                && ((XmlTag) element).getNamespace().startsWith(StripesConstants.TAGLIB_PREFIX)
                && isDetailsAccepted((XmlTag) element);
    }

    /**
     * Implement this method to extend filter functionality.
     *
     * @param tag tag to be checked
     * @return true if tag match conditions, false otherwise.
     */
    protected abstract boolean isDetailsAccepted(XmlTag tag);
}
