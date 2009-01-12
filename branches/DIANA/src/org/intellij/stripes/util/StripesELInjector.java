package org.intellij.stripes.util;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.filters.AnnotationParameterFilter;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.impl.source.jsp.el.ELLanguage;
import org.intellij.lang.regexp.RegExpLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class StripesELInjector implements MultiHostInjector {
    final private static ElementFilter VALIDATE_ANNOTATION_EXPRESSION_ATTR = new AnnotationParameterFilter(PsiLiteralExpression.class, StripesConstants.VALIDATE_ANNOTATION, StripesConstants.EXPRESSION_ATTR);
    final private static ElementFilter VALIDATE_ANNOTATION_MASK_ATTR = new AnnotationParameterFilter(PsiLiteralExpression.class, StripesConstants.VALIDATE_ANNOTATION, StripesConstants.MASK_ATTR);

    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (VALIDATE_ANNOTATION_EXPRESSION_ATTR.isAcceptable(context, context)) {
            final TextRange range = new TextRange(1, context.getTextLength() - 1);
            registrar.startInjecting(ELLanguage.INSTANCE)
                    .addPlace(null, null, (PsiLanguageInjectionHost) context, range)
                    .doneInjecting();
        } else if (VALIDATE_ANNOTATION_MASK_ATTR.isAcceptable(context, context)) {
            final TextRange range = new TextRange(1, context.getTextLength() - 1);
            registrar.startInjecting(RegExpLanguage.INSTANCE)
                    .addPlace(null, null, (PsiLanguageInjectionHost) context, range)
                    .doneInjecting();

        }
    }

    @NotNull
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Arrays.asList(PsiLiteralExpression.class);
    }
}