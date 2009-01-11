package org.intellij.stripes.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.Nullable;

public class StaticReference extends PsiReferenceBase<PsiElement> {

	private String[] array;

    public StaticReference(PsiElement element, String... array) {
        super(element);
        this.array = array;
    }

	public StaticReference(PsiElement element, TextRange range, String... array) {
        super(element, range);
        this.array = array;
    }

    @Nullable
    public PsiElement resolve() {
        return getElement();
    }

    public Object[] getVariants() {
        return array;
    }


}