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

package org.intellij.stripes.reference.filters;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationParameterList;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.filters.ElementFilter;
import org.intellij.stripes.util.StripesConstants;

/**
 * This Class Filter AutoCompletion For @SpringBean
 * <p/>
 * Created by IntelliJ IDEA. User: Mario Arias Date: 24/09/2007 Time: 10:34:19 PM
 */
public class SpringBeanAnnotationFilter implements ElementFilter {
// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ElementFilter ---------------------

    public boolean isAcceptable(final Object element, final PsiElement context) {
        if (element instanceof PsiNameValuePair) {
            PsiNameValuePair value = (PsiNameValuePair) element;
            //Ia the element An Annotation Parameter List
            if (value.getParent() instanceof PsiAnnotationParameterList) {
                PsiAnnotationParameterList annotationParameterList = (PsiAnnotationParameterList) value.getParent();
                //get the Annotation Object
                PsiAnnotation annotation = (PsiAnnotation) annotationParameterList.getParent();
                try {
                    //Is @SpringBean Annotation??
                    return annotation.getQualifiedName().equals(StripesConstants.SPRING_BEAN_ANNOTATION);
                }
                catch (NullPointerException e) {
                    //OOPS
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isClassAcceptable(final Class hintClass) {
        return true;
    }
}
