package org.intellij.stripes.util;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.javascript.JSLanguageInjector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.stripes.components.project.StripesReferencesComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class StripesJSInjector implements MultiHostInjector {
	public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement host) {
		if (StripesReferencesComponent.STRIPES_NAMESPACE_FILTER.isAcceptable(host.getParent().getParent(), null)) {
			final String name = ((XmlAttribute) host.getParent()).getName();
			if (name.startsWith("on")) {
				JSLanguageInjector.injectJSIntoAttributeValue(registrar, (XmlAttributeValue) host, false);
			}
		}
	}

	@NotNull
	public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
		return ELEMENTS;
	}

	private static List<Class<XmlAttributeValue>> ELEMENTS = Arrays.asList(XmlAttributeValue.class);
}
