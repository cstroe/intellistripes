package org.intellij.stripes.util;

import com.intellij.lang.Language;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.javascript.JavaScriptSupportLoader;
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

public class StripesMultiHostInjector implements com.intellij.lang.injection.MultiHostInjector {
    private static StripesMultiHostInjector CSS_INSTANCE = new StripesMultiHostInjector("inline.style {", "}", CssSupportLoader.CSS_FILE_TYPE.getLanguage(), "style");
    private static StripesMultiHostInjector JS_INSTANCE = new StripesMultiHostInjector(null, null, JavaScriptSupportLoader.JAVASCRIPT.getLanguage(), "on");

    public static MultiHostInjector getCSSInstance() {
        return CSS_INSTANCE;
    }

    public static MultiHostInjector getJSInstance() {
        return JS_INSTANCE;
    }

    private String prefix;
    private String postfix;
    private Language language;
    private String attribute;

    public StripesMultiHostInjector(String prefix, String postfix, Language language, String attribute) {
        this.prefix = prefix;
        this.postfix = postfix;
        this.language = language;
        this.attribute = attribute;
    }

    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (StripesReferencesComponent.STRIPES_NAMESPACE_FILTER.isAcceptable(context.getParent().getParent(), null)) {
            final String name = ((XmlAttribute) context.getParent()).getName();
            if (name.startsWith(this.attribute)) {
                final TextRange range = new TextRange(1, context.getTextLength() - 1);
                registrar.startInjecting(this.language)
                        .addPlace(this.prefix, this.postfix, (PsiLanguageInjectionHost) context, range)
                        .doneInjecting();
            }
        }
    }

    @NotNull
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Arrays.asList(XmlAttributeValue.class);
    }
}
