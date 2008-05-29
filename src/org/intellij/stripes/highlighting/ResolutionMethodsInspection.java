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

package org.intellij.stripes.highlighting;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ResolutionMethodsInspection extends LocalInspectionTool {
    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return "Stripes Inspection";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Resolution Methods";
    }

    @NonNls
    @NotNull
    public String getShortName() {
        return "StripesInspections";
    }

    public boolean isEnabledByDefault() {
        return true;
    }


    @NotNull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.DO_NOT_SHOW;
    }

    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        Map<String, PsiMethod> methods = StripesReferenceUtil.getResolutionMethods(aClass);
        Integer defHndlCnt = 0;
        Integer resHndlCnt = 0;
        for (PsiMethod method : methods.values()) {
            if (method.getModifierList().findAnnotation(StripesConstants.DEFAULT_HANDLER_ANNOTATION) != null) {
                defHndlCnt++;
            }

        }
        return super.checkClass(aClass, manager, isOnTheFly);
    }
}