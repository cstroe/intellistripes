package org.intellij.stripes.reference;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;

import java.util.List;
import java.util.Map;

public class SetterMethodReference<T extends PsiElement> extends PsiReferenceBase<T> {
    private PsiClass actionBean;
    private Boolean braces;

    public SetterMethodReference(T element, PsiClass actionBean, Boolean braces) {
        super(element);
        this.actionBean = actionBean;
        this.braces = braces;
    }

    public PsiElement resolve() {
//        MarkupModel mm = FileDocumentManager.getInstance().getDocument(getElement().getContainingFile().getVirtualFile()).getMarkupModel(getElement().getProject());
//        mm.addRangeHighlighter(getElement().getTextOffset() + getRangeInElement().getStartOffset(),
//                getElement().getTextOffset() + getRangeInElement().getEndOffset(),
//                HighlighterLayer.ERROR, EditorColorsManager.getInstance().getGlobalScheme().getAttributes(CodeInsightColors.ERRORS_ATTRIBUTES), HighlighterTargetArea.EXACT_RANGE);
        try {
            List<String> arr = StripesReferenceUtil.splitNestedVar(getCanonicalText().replaceAll("IntellijIdeaRulezzz ", ""));
            if (arr.size() == 0) return null;

            String f = arr.remove(arr.size() - 1);
            if ("".equals(f)) return null;

            PsiClass cls = actionBean;
            for (String field : arr) {
                cls = StripesReferenceUtil.resolveActionBeanSetterReturnType(cls, field);
                if (null == cls) break;
            }

            String propertyName = braces ? f.replaceAll("\\[.*?\\]", "") : f;
            PsiMethod method = cls.findMethodsByName("set" + StringUtil.capitalize(propertyName), true)[0];
            if (null != method && braces && !f.equals(propertyName)) {
                PsiType propertyType = method.getParameterList().getParameters()[0].getType();
                PsiClass propertyClass = PsiUtil.resolveClassInType(propertyType);
                if (!(StripesUtil.isSubclass(List.class.getName(), propertyClass)
                        || propertyType instanceof PsiArrayType
                        || StripesUtil.isSubclass(Map.class.getName(), propertyClass))) {
                    method = null;
                }
            }

            return method;
        } catch (Exception e) {
            return null;
        }
    }

    public Object[] getVariants() {
        PsiClass cls = actionBean;
        String prefix = "";
        String cText = getCanonicalText().replaceAll("IntellijIdeaRulezzz", "");
        int end = cText.indexOf(' ');
        cText = cText.substring(0, end == -1 ? cText.length() : end);

        if (cText.contains(".")) {
            List<String> arr = StripesReferenceUtil.splitNestedVar(cText);
            arr.remove(arr.size() - 1);
            prefix = StringUtil.join(arr, ".") + '.';
            for (String field : arr) {
                cls = StripesReferenceUtil.resolveActionBeanSetterReturnType(cls, field);
                if (cls == null) break;
            }
        }

        return StripesReferenceUtil.getVariants(StripesReferenceUtil.getWritableProperties(cls, true), prefix, StripesConstants.FIELD_ICON);
    }
}
