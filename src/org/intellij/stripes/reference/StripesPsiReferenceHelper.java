package org.intellij.stripes.reference;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class StripesPsiReferenceHelper {
    public static Object[] getVariants(@NotNull List<String> list, String prefix, @NotNull Icon icon) {
        if (list.size() == 0) return PsiReferenceBase.EMPTY_ARRAY;

        Object[] retval = new Object[list.size()];
        for (int i = 0; i < list.size(); i++) {
            retval[i] = LookupValueFactory.createLookupValue(prefix + list.get(i), icon);
        }
        return retval;
    }
}
