package org.intellij.stripes.reference.providers;

import com.intellij.openapi.paths.PathReference;
import com.intellij.openapi.paths.PathReferenceProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.stripes.reference.UrlBindingReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UrlBindingPathReferenceProvider implements PathReferenceProvider {

    public boolean createReferences(@NotNull PsiElement psiElement, @NotNull List<PsiReference> references, boolean soft) {
        if (psiElement instanceof XmlAttributeValue) {
            references.add(new UrlBindingReference((XmlAttributeValue) psiElement));
        }
        return true;
    }

    @Nullable
    public PathReference getPathReference(@NotNull String path, @NotNull PsiElement element) {
        return null;
    }
}
