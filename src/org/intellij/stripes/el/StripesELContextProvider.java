package org.intellij.stripes.el;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.jsp.JspImplicitVariableImpl;
import com.intellij.psi.impl.source.jsp.el.ELContextProvider;
import com.intellij.psi.jsp.JspImplicitVariable;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
			addElVariable(retval, "this", fieldTypeHolder, type);
		}

		PsiClass actionBeanPsiClass = PsiTreeUtil.getParentOfType(host, PsiClass.class);
		if (null != actionBeanPsiClass && StripesUtil.isSubclass(host.getProject(), StripesConstants.ACTION_BEAN, actionBeanPsiClass)) {
			for (Map.Entry<String, PsiMethod> entry : PropertyUtil.getAllProperties(actionBeanPsiClass, true, false, true).entrySet()) {
				addElVariable(retval, entry.getKey(), entry.getValue(), PropertyUtil.getPropertyType(entry.getValue()));
			}
		}
		return retval.iterator();
	}

	private void addElVariable(List<JspImplicitVariable> retval, final String name, final PsiElement fieldTypeHolder, final PsiType type) {
		retval.add(new JspImplicitVariableImpl(fieldTypeHolder, name, type,
			fieldTypeHolder, JspImplicitVariableImpl.NESTED_RANGE) {
			public PsiElement getDeclaration() {
				return null;
			}
		});
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
