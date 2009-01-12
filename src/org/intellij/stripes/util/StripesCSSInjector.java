package org.intellij.stripes.util;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.css.CssSupportLoader;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.stripes.components.project.StripesReferencesComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class StripesCSSInjector implements MultiHostInjector {
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (StripesReferencesComponent.STRIPES_NAMESPACE_FILTER.isAcceptable(context.getParent().getParent(), null)) {
            final String name = ((XmlAttribute) context.getParent()).getName();
            if (name.startsWith("style")) {
                final TextRange range = new TextRange(1, context.getTextLength() - 1);
                registrar.startInjecting(CssSupportLoader.CSS_FILE_TYPE.getLanguage())
                        .addPlace("inline.style {", "}", (PsiLanguageInjectionHost) context, range)
                        .doneInjecting();
            }
        }
    }

    @NotNull
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return ELEMENTS;
    }

    private static List<Class<XmlAttributeValue>> ELEMENTS = Arrays.asList(XmlAttributeValue.class);
}