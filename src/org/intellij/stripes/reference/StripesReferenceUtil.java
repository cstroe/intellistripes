/*
 * Copyright 2000-2009 JetBrains s.r.o.
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

package org.intellij.stripes.reference;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlTag;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesTagFilter;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;

public final class StripesReferenceUtil {

	private static List<String> EMPTY_STRING_LIST = new ArrayList<String>(8);

	public static PsiElementFilter NAME_ATTR_FILTER = new StripesTagFilter() {
		protected boolean isDetailsAccepted(XmlTag tag) {
			return tag.getAttributeValue(StripesConstants.NAME_ATTR) != null;
		}
	};

	/**
	 * Get the Event methods (Resolution methods) for an ActionBean Class
	 *
	 * @param psiClass an ActionBean PsiClass
	 * @return List with all Resolution Methods names
	 */
	public static List<String> getResolutionMethodsNames(PsiClass psiClass) {
		List<String> methodNames = new ArrayList<String>(16);
		for (PsiMethod method : getResolutionMethods(psiClass).values()) {
			String s = resolveHandlesEventAnnotation(method);
			//add the @HandlesEvent value
			methodNames.add(null == s ? method.getName() : s);
		}
		return methodNames;
	}

	/**
	 * Get the Event methods (Resolution methods) for an ActionBean Class.
	 * <p/>
	 * Methods are sorted by inheritance. Iitems with lower indexes correspond to closest supercalss of current {@link PsiClass}.
	 *
	 * @param psiClass an ActionBean PsiClass
	 * @return List with all Resolution Methods
	 */
	public static Collection<PsiMethod> getResolutionMethodsAsList(PsiClass psiClass) {
		Map<String, PsiMethod> retval = new LinkedHashMap<String, PsiMethod>();
		for (PsiMethod method : psiClass.getAllMethods()) {
			PsiType returnType = method.getReturnType();
			if (returnType != null
				&& returnType.equalsToText(StripesConstants.STRIPES_RESOLUTION_CLASS)
				&& method.getParameterList().getParametersCount() == 0
				&& !retval.containsKey(method.getName())) {
				retval.put(method.getName(), method);
			}
		}
		return retval.values();
	}

	/**
	 * Get the Event methods (Resolution methods) for an ActionBean Class
	 *
	 * @param psiClass an ActionBean PsiClass
	 * @return List with all Resolution Methods
	 */
	public static Map<String, PsiMethod> getResolutionMethods(PsiClass psiClass) {
		Map<String, PsiMethod> psiMethods = new HashMap<String, PsiMethod>(8);

//Add Resultion methods for super classes
		PsiClass superClass;
		try {
			superClass = psiClass.getSuperClass();
		} catch (Exception e) {
			return psiMethods;
		}

		if (null == superClass) return new HashMap<String, PsiMethod>(0);

		if (!(Object.class.getName().equals(superClass.getQualifiedName()))) {
			psiMethods.putAll(getResolutionMethods(superClass));
		}

		for (PsiMethod method : psiClass.getMethods()) {
//method return Resolution and have 0 parameters
			PsiType returnType = method.getReturnType();
			if (returnType != null) {
				if (returnType.equalsToText(StripesConstants.STRIPES_RESOLUTION_CLASS) && method.getParameterList().getParametersCount() == 0) {
					psiMethods.put(method.getName(), method);
				}
			}
		}

		return psiMethods;
	}

	public static String resolveHandlesEventAnnotation(PsiMethod method) {
		String retval = null;
		PsiAnnotation a = method.getModifierList().findAnnotation(StripesConstants.HANDLES_EVENT_ANNOTATION);
		try {
			if (a != null) {
				PsiAnnotationMemberValue psvm = a.findAttributeValue("value");
				if (psvm instanceof PsiLiteralExpression) {
					retval = StringUtil.stripQuotesAroundValue(psvm.getText());
				} else if (psvm instanceof PsiReferenceExpression) {
					PsiElement el = psvm.getReference().resolve();
					if (el instanceof PsiField) {
						retval = StringUtil.stripQuotesAroundValue(((PsiField) el).getInitializer().getText());
					}
				}
			}
		} catch (ProcessCanceledException e) {
			//Do nothig, this exception is very common and can be throw for intellij
			//Logger don't be reported or just raise an ugly error
		} catch (Exception e) {
			Logger.getInstance("IntelliStripes").error("Error resolving annotation", e);
		}
		return retval;
	}

	/**
	 * Returns list of properties for certain class and its superclasses that have setter method
	 *
	 * @param psiClass class to examine
	 * @param braces   append square braces to the property name or not
	 * @return {@link java.util.List java.util.List} of property names
	 */
	public static List<String> getWritableProperties(PsiClass psiClass, Boolean braces) {
		if (null == psiClass) return EMPTY_STRING_LIST;

		List<String> methodNames = new ArrayList<String>(16);
		for (PsiMethod psiMethod : psiClass.getAllMethods()) {
			String name = PropertyUtil.getPropertyNameBySetter(psiMethod);
			if (PropertyUtil.isSimplePropertySetter(psiMethod) && !methodNames.contains(name)) {
				PsiType propertyType = psiMethod.getParameterList().getParameters()[0].getType();
				PsiClass propertyClass = PsiUtil.resolveClassInType(propertyType);

				if (StripesUtil.isSubclass(StripesConstants.ACTION_BEAN_CONTEXT, propertyClass)
					|| StripesUtil.isSubclass(StripesConstants.FILE_BEAN, propertyClass)) continue;

				if (propertyType instanceof PsiArrayType
					|| StripesUtil.isSubclass(List.class.getName(), propertyClass)
					|| StripesUtil.isSubclass(Map.class.getName(), propertyClass)) {
					name += (braces ? "[]" : "");
				}
				methodNames.add(name);
			}
		}

		return methodNames;
	}

	/**
	 * Returns properties of FileBean of certain PsiClass that can be set by Stripes.
	 *
	 * @param psiClass
	 * @return {@link java.util.List list} of property names
	 */
	public static List<String> getFileBeanProperties(PsiClass psiClass) {
		if (null == psiClass) return EMPTY_STRING_LIST;

		List<String> methodNames = new ArrayList<String>(4);
		for (PsiMethod psiMethod : psiClass.getAllMethods()) {
			if (PropertyUtil.isSimplePropertySetter(psiMethod)) {
				PsiType propertyType = psiMethod.getParameterList().getParameters()[0].getType();
				PsiClass propertyClass = StripesReferenceUtil.resolveClassInType(propertyType, psiClass.getProject());

				if (StripesUtil.isSubclass(StripesConstants.FILE_BEAN, propertyClass)) {
					String methodName = PropertyUtil.getPropertyNameBySetter(psiMethod);

					propertyClass = PsiUtil.resolveClassInType(propertyType);
					if (StripesUtil.isSubclass(List.class.getName(), propertyClass)
						|| propertyType instanceof PsiArrayType
						|| StripesUtil.isSubclass(Map.class.getName(), propertyClass)) {
						methodName += "[]";
					}
					methodNames.add(methodName);
				}
			}
		}

		return methodNames;
	}

	public static PsiClass resolveClassInType(PsiType propertyType, Project project) {
		PsiClass cls;
		try {
			PsiClass propertyClass = PsiUtil.resolveClassInType(propertyType);
			if (StripesUtil.isSubclass(List.class.getName(), propertyClass)) {
				if (((PsiClassReferenceType) propertyType).getParameters().length == 1) {
					cls = PsiUtil.resolveClassInType(((PsiClassReferenceType) propertyType).getParameters()[0]);
				} else {
					cls = StripesUtil.findPsiClassByName(Object.class.getName(), project);
				}
			} else if (StripesUtil.isSubclass(Map.class.getName(), propertyClass)) {
				if (((PsiClassReferenceType) propertyType).getParameters().length == 2) {
					cls = PsiUtil.resolveClassInType(((PsiClassReferenceType) propertyType).getParameters()[1]);
				} else {
					cls = StripesUtil.findPsiClassByName(Object.class.getName(), project);
				}
			} else {
				cls = propertyClass;
			}
		} catch (Exception e) {
			cls = null;
		}

		return cls;
	}

	/**
	 * Gets s PsiClass from a ExpressionList of type com.foo.MyClass.class
	 *
	 * @param list
	 * @return
	 */
	public static PsiClass getPsiClassFromExpressionList(PsiExpressionList list) {
		String className = list.getExpressions()[0] instanceof PsiClassObjectAccessExpression
			? ((PsiClassObjectAccessExpression) list.getExpressions()[0]).getOperand().getType().getCanonicalText()
			: null;

		return StripesUtil.findPsiClassByName(className, list.getProject());
	}

	/**
	 * Get an ActionBean PsiClass from a given tag parent
	 *
	 * @param xmlTag xml tag
	 * @param parent parent's name
	 * @return An ActionBean PsiClass
	 */
	public static PsiClass getBeanClassFromParentTag(@NotNull XmlTag xmlTag, @NotNull String parent) {
		for (XmlTag tag = xmlTag.getParentTag(); tag != null; tag = tag.getParentTag()) {
			if (tag.getNamespace().startsWith(StripesConstants.TAGLIB_PREFIX)
				&& parent.equals(tag.getLocalName())) {
				PsiClass cls = StripesUtil.findPsiClassByName(tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR), tag.getProject());
				return StripesUtil.isSubclass(StripesConstants.ACTION_BEAN, cls) ? cls : null;
			}
		}

		return null;
	}

	public static Object[] getVariants(@NotNull Collection<String> list, @NotNull Icon icon) {
		if (list.isEmpty()) return PsiReferenceBase.EMPTY_ARRAY;

		Object[] retval = new Object[list.size()];
		Integer i = 0;
		for (String s : list) {
			retval[i++] = LookupValueFactory.createLookupValue(s, icon);
		}
		return retval;
	}

	public static UrlBindingSearcher URL_BINDING_SEARCHER;
	private static Map<String, PsiClass> EMPTY_URL_BINDING_MAP = new HashMap<String, PsiClass>(0);

	public static Map<String, PsiClass> getUrlBindings(Project project) {
		if (URL_BINDING_SEARCHER == null) {
			PsiClass urlBindingCls = StripesUtil.findPsiClassByName(StripesConstants.URL_BINDING_ANNOTATION, project);
			if (null != urlBindingCls) URL_BINDING_SEARCHER = new UrlBindingSearcher(urlBindingCls);
		}
		return URL_BINDING_SEARCHER == null ? EMPTY_URL_BINDING_MAP : URL_BINDING_SEARCHER.execute();
	}
}
