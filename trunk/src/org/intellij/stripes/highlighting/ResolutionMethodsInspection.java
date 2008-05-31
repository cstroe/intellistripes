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
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolutionMethodsInspection extends LocalInspectionTool {

    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return StripesUtil.message("inspection.groupDisplayName");
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return StripesUtil.message("inspection.resolutionMethods");
    }

    @NonNls
    @NotNull
    public String getShortName() {
        return StripesUtil.message("inspection.resolutionMethods.shortName");
    }

    public boolean isEnabledByDefault() {
        return true;
    }


    @NotNull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        List<ProblemDescriptor> retval = new ArrayList<ProblemDescriptor>();

        Map<String, Integer> handlesEventMap = new HashMap<String, Integer>();
        Map<String, PsiMethod> resolutionMethods = StripesReferenceUtil.getResolutionMethods(aClass);
        Integer defHndlCnt = 0;

        for (PsiMethod method : resolutionMethods.values()) {
            PsiModifierList mList = method.getModifierList();
            if (mList.findAnnotation(StripesConstants.DEFAULT_HANDLER_ANNOTATION) != null) {
                defHndlCnt++;
            } else if (mList.findAnnotation(StripesConstants.HANDLES_EVENT_ANNOTATION) != null) {
                String s = StripesReferenceUtil.resolveHandlesEventAnnotation(method);
                Integer cnt = handlesEventMap.get(s);
                handlesEventMap.put(s, cnt == null ? 1 : ++cnt);
            }
        }

        if (resolutionMethods.size() > 1 && defHndlCnt == 0) {
            retval.add(
                    manager.createProblemDescriptor(aClass, StripesUtil.message("inspection.noDefaultHandler"),
                            LocalQuickFix.EMPTY_ARRAY, ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
            );
        }

        if (resolutionMethods.size() > 1 && defHndlCnt > 1) {
            retval.add(
                    manager.createProblemDescriptor(aClass, StripesUtil.message("inspection.moreThanOneDefaultHandler"),
                            LocalQuickFix.EMPTY_ARRAY, ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
            );
        }

        if (resolutionMethods.size() == 1 && defHndlCnt == 1) {
            retval.add(
                    manager.createProblemDescriptor(aClass, StripesUtil.message("inspection.unnecessaryDefaultHandler"),
                            LocalQuickFix.EMPTY_ARRAY, ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
            );
        }

        for (String method : handlesEventMap.keySet()) {
            if (handlesEventMap.get(method) > 1) {
                retval.add(manager.createProblemDescriptor(aClass, StripesUtil.message("inspection.duplicatedHandlesEvent", method),
                        LocalQuickFix.EMPTY_ARRAY, ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
            }
        }

        return retval.toArray(new ProblemDescriptor[retval.size()]);
    }
}