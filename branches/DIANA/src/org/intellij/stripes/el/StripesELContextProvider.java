package org.intellij.stripes.el;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.jsp.JspImplicitVariableImpl;
import com.intellij.psi.impl.source.jsp.el.ELContextProvider;
import com.intellij.psi.jsp.JspImplicitVariable;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StripesELContextProvider implements ELContextProvider {

	private final PsiElement host;

	public StripesELContextProvider(PsiElement host) {
		this.host = host;
	}

	public Iterator<? extends PsiVariable> getTopLevelElVariables(@Nullable String s) {
		List<JspImplicitVariable> retval = new ArrayList<JspImplicitVariable>();

		PsiElement fieldTypeHolder = host.getParent().getParent();
		if (!(fieldTypeHolder instanceof PsiMethod) && !(fieldTypeHolder instanceof PsiField)) {
			PsiAnnotation p = PsiTreeUtil.getParentOfType(fieldTypeHolder, PsiAnnotation.class);
			if (p != null && StripesConstants.VALIDATE_NESTED_PROPERTIES_ANNOTATION.equals(p.getQualifiedName())) {
				fieldTypeHolder = p.getParent().getParent();
			} else {
				fieldTypeHolder = null;
			}
		}

		PsiType type = PropertyUtil.getPropertyType((PsiMember) fieldTypeHolder);
		if (type != null) {
			retval.add(new JspImplicitVariableImpl(fieldTypeHolder, "this", type,
				fieldTypeHolder, JspImplicitVariableImpl.NESTED_RANGE) {
				public PsiElement getDeclaration() {
					return null;
				}
			});
		}

		return retval.iterator();
	}

	public boolean acceptsGetMethodForLastReference(PsiMethod psiMethod) {
		return PropertyUtil.isSimplePropertyGetter(psiMethod);
	}

	public boolean acceptsSetMethodForLastReference(PsiMethod psiMethod) {
		return false;
	}

	public boolean acceptsNonPropertyMethodForLastReference(PsiMethod psiMethod) {
		return false;
	}
}
