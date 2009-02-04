package org.intellij.stripes.actions;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.JavaPsiClassReferenceElement;
import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.impl.EmptyNode;
import com.intellij.codeInsight.template.impl.MacroCallNode;
import com.intellij.codeInsight.template.macro.SuggestVariableNameMacro;
import com.intellij.ide.util.MemberChooser;
import com.intellij.ide.util.MemberChooserBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.spring.SpringBundle;
import com.intellij.spring.SpringManager;
import com.intellij.spring.SpringModel;
import com.intellij.spring.model.SpringUtils;
import com.intellij.spring.model.actions.generate.SpringBeanClassMember;
import com.intellij.spring.model.xml.beans.SpringBeanPointer;
import org.jetbrains.annotations.NotNull;

import java.beans.Introspector;
import java.util.*;

public class GenerateSpringBeanAction extends StripesBaseGenerateAction {
	public GenerateSpringBeanAction() {
		super(new GenerateSpringBeanHandler());
	}

	private static class GenerateSpringBeanHandler implements CodeInsightActionHandler {

		public void invoke(final Project project, final Editor editor, final PsiFile file) {
			List<SpringModel> models = SpringManager.getInstance(project).getAllModels(ModuleUtil.findModuleForPsiElement(file));

			final SpringBeanPointer beanPointer = GenerateSpringBeanAction.chooseBean(models.get(0), project);
			if (beanPointer != null) {
				final PsiNameHelper psiNameHelper = JavaPsiFacade.getInstance(project).getNameHelper();
				Template t = TemplateManager.getInstance(project).createTemplate("", "",
					"private $TYPE$ $PARAM$;\n@net.sourceforge.stripes.integration.spring.SpringBean("
						+ (psiNameHelper.isIdentifier(beanPointer.getName()) ? "\"" + Introspector.decapitalize(beanPointer.getName()) + "\"" : "")
						+ ")\npublic void inject$TYPE$($TYPE$ $PARAM$) {\nthis.$PARAM$ = $PARAM$;$END$}");

				t.setToReformat(true);
				t.setToShortenLongNames(true);
				t.setToIndent(true);

				t.addVariable("TYPE", getSuperTypesExpression(getSuperTypeVariants(beanPointer.getEffectiveBeanType())), new EmptyNode(), true);
				t.addVariable("PARAM", new MacroCallNode(new SuggestVariableNameMacro()), new EmptyNode(), true);

				TemplateManager.getInstance(project).startTemplate(editor, t);
			}
		}

		public boolean startInWriteAction() {
			return true;
		}
	}

	private static SpringBeanPointer chooseBean(@NotNull final SpringModel model, final Project project) {

		List<SpringBeanClassMember> candidates = new ArrayList<SpringBeanClassMember>();
		final Collection<? extends SpringBeanPointer> allBeans = model.getAllCommonBeans();
		for (final SpringBeanPointer bean : allBeans) {
			if (SpringUtils.getReferencedName(bean, allBeans) != null && bean.getEffectiveBeanType().length > 0) {
				candidates.add(new SpringBeanClassMember(bean));
			}
		}

		MemberChooserBuilder buildr = new MemberChooserBuilder<SpringBeanClassMember>(project);
		buildr.allowEmptySelection(false);
		buildr.allowMultiSelection(false);

		MemberChooser<SpringBeanClassMember> chooser = buildr.createBuilder(candidates.toArray(new ClassMember[candidates.size()]));
		chooser.setTitle(SpringBundle.message("spring.bean.dependencies.chooser.title"));
		chooser.setCopyJavadocVisible(false);
		chooser.show();

		if (chooser.getExitCode() == MemberChooser.OK_EXIT_CODE) {
			List<SpringBeanClassMember> selected = chooser.getSelectedElements();
			if (selected != null && selected.size() > 0) {
				return selected.get(0).getSpringBean();
			}
		}

		return null;
	}

	private static Collection<PsiClass> getSuperTypeVariants(final PsiClass[] psiClasses) {
		Collection<PsiClass> variants = new HashSet<PsiClass>();
		for (PsiClass beanClass : psiClasses) {
			variants.add(beanClass);
			variants.addAll(Arrays.asList(beanClass.getInterfaces()));

			for (PsiClass psiClass : beanClass.getSupers()) {
				if (Object.class.getName().equals(psiClass.getQualifiedName())) continue;
				variants.add(psiClass);
			}
		}
		return variants;
	}

	private static Expression getSuperTypesExpression(final Collection<PsiClass> psiClasses) {
		return new Expression() {
			public Result calculateResult(ExpressionContext context) {
				return new JavaPsiElementResult(psiClasses.iterator().next());
			}

			public Result calculateQuickResult(ExpressionContext context) {
				return calculateResult(context);
			}

			public LookupElement[] calculateLookupItems(ExpressionContext context) {
				List<LookupElement> items = new ArrayList<LookupElement>(psiClasses.size());
				for (PsiClass psiClass : psiClasses) {
					items.add(new JavaPsiClassReferenceElement(psiClass) {
						@Override
						public void handleInsert(InsertionContext insertionContext) {
							super.handleInsert(insertionContext);
							JavaCodeStyleManager.getInstance(insertionContext.getProject()).addImport((PsiJavaFile) insertionContext.getFile(), getObject());
						}
					});
				}
				return items.toArray(new LookupElement[items.size()]);
			}
		};
	}

}
