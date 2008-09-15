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

/**
 * Class providing JS or CSS injection into attributes of stripes tags.
 */
public class StripesMultiHostInjector implements com.intellij.lang.injection.MultiHostInjector {

    private static StripesMultiHostInjector CSS_INSTANCE = new StripesMultiHostInjector("inline.style {", "}", CssSupportLoader.CSS_FILE_TYPE.getLanguage(), "style");
    //You must need to add <IDEA_HOME>/plugins/JavaScriptLanguage/lib/JavaScriptLanguage.jar to your IDEA JDK
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

    /**
     * Injects certain language into attribute starting with specified prefix of tag matchhing Stripes Tag Filter.
     * @param registrar
     * @param context
     */
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

    /**
     * Only attributes of tags are allowed for language injection by this injector.
     * @return {@link java.util.List} containing PsiElement corresponding to HTML tag attributes
     */
    @NotNull
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Arrays.asList(XmlAttributeValue.class);
    }
}
