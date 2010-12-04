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

package org.intellij.stripes.highlighting;

import com.intellij.codeInsight.intention.AddAnnotationFix;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.css.impl.util.RemoveElementAction;
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

/**
 * Inspection tool checking for correct usage and annotations applied to event methods (ones returnig Resolutions).
 */
public class ResolutionMethodsInspection extends BaseJavaLocalInspectionTool {

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

    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        List<ProblemDescriptor> retval = new ArrayList<ProblemDescriptor>(4);

        Map<String, List<PsiAnnotation>> handlesEvents = new HashMap<String, List<PsiAnnotation>>(4);
        Map<String, PsiMethod> resolutionMethods = StripesReferenceUtil.getResolutionMethods(aClass);
        List<PsiAnnotation> defaultHandlers = new ArrayList<PsiAnnotation>(4);

// iterates over all events of ActionBean and collects ones annotated with @DefaultHandler and @HandlesEvent
        for (PsiMethod method : resolutionMethods.values()) {
            PsiModifierList mList = method.getModifierList();

            PsiAnnotation ann = mList.findAnnotation(StripesConstants.DEFAULT_HANDLER_ANNOTATION);
            if (ann != null) defaultHandlers.add(ann);

            ann = mList.findAnnotation(StripesConstants.HANDLES_EVENT_ANNOTATION);
            if (ann != null) {
                String eventName = StripesReferenceUtil.resolveHandlesEventAnnotation(method);

                if (method.getName().equals(eventName)) {
                    retval.add(manager.createProblemDescriptor(ann, StripesUtil.message("inspection.duplicatesMethodName"),
                            new RemoveAnnotationQuickFix(ann, method), ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
                }

                List<PsiAnnotation> methods = handlesEvents.get(eventName);
                if (null == methods) methods = new ArrayList<PsiAnnotation>(8);
                methods.add(ann);
                handlesEvents.put(eventName, methods);
            }
        }
//check if there's more than one event and no default is declared
        if (resolutionMethods.size() > 1 && defaultHandlers.size() == 0) {
            LocalQuickFix[] fixes = new LocalQuickFix[resolutionMethods.size()];
            int i = 0;
            for (PsiMethod method : resolutionMethods.values()) {
                fixes[i++] = new AddAnnotationFix(StripesConstants.DEFAULT_HANDLER_ANNOTATION, method);
            }
            retval.add(manager.createProblemDescriptor(aClass.getNameIdentifier(), StripesUtil.message("inspection.noDefaultHandler"),
                    fixes, ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        }

//check if few events are annotated as defautl handler
        if (resolutionMethods.size() > 1 && defaultHandlers.size() > 1) {
            LocalQuickFix[] fixes = new LocalQuickFix[defaultHandlers.size()];
            int i = 0;
            for (PsiAnnotation annotation : defaultHandlers) {
                fixes[i++] = new RemoveElementAction(annotation);
            }
            retval.add(manager.createProblemDescriptor(aClass.getNameIdentifier(), StripesUtil.message("inspection.moreThanOneDefaultHandler"),
                    fixes, ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        }

//if only one event exists and its declared as default handler
        if (resolutionMethods.size() == 1 && defaultHandlers.size() == 1) {
            retval.add(manager.createProblemDescriptor(defaultHandlers.get(0), StripesUtil.message("inspection.unnecessaryDefaultHandler"),
                    new RemoveElementAction(defaultHandlers.get(0)), ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
            );
        }

//checks duplicated @HandlesEvent
        List<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>(4);
        for (List<PsiAnnotation> annotations : handlesEvents.values()) {
            if (annotations.size() > 1) {
                for (PsiAnnotation annotation : annotations) {
                    fixes.add(new RemoveElementAction(annotation));
                }
            }
        }

        if (!fixes.isEmpty()) {
            retval.add(manager.createProblemDescriptor(aClass.getNameIdentifier(), StripesUtil.message("inspection.duplicatedHandlesEvent"),
                    fixes.toArray(new LocalQuickFix[fixes.size()]), ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        }

        return retval.toArray(new ProblemDescriptor[retval.size()]);
    }
}