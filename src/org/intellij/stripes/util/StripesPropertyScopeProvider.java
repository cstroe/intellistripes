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

package org.intellij.stripes.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataCache;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.search.CustomPropertyScopeProvider;
import com.intellij.psi.impl.search.JavaSourceFilterScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.PsiModificationTracker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StripesPropertyScopeProvider implements CustomPropertyScopeProvider {
	private static Key<CachedValue<Set<VirtualFile>>> CACHED_FILES_KEY = Key.create("stripesCachedActionBeans");

	private static final UserDataCache<CachedValue<Set<VirtualFile>>, Project, Object> myCachedActionBeansFiles =
		new UserDataCache<CachedValue<Set<VirtualFile>>, Project, Object>() {
			protected CachedValue<Set<VirtualFile>> compute(final Project project, final Object p) {
				return PsiManager.getInstance(project).getCachedValuesManager().createCachedValue(new CachedValueProvider<Set<VirtualFile>>() {
					public Result<Set<VirtualFile>> compute() {
						return Result.createSingleDependency(getActionBeanVirtualFiles(project), PsiModificationTracker.JAVA_STRUCTURE_MODIFICATION_COUNT);
					}
				}, false);
			}
		};

	public SearchScope getScope(final Project project) {
		final Set<VirtualFile> files = myCachedActionBeansFiles.get(CACHED_FILES_KEY, project, null).getValue();

		return new JavaSourceFilterScope(GlobalSearchScope.projectProductionScope(project), project) {
			@Override
			public boolean contains(VirtualFile virtualFile) {
				return super.contains(virtualFile) && files.contains(virtualFile);
			}

			@Override
			public boolean isSearchInLibraries() {
				return false;
			}
		};
	}

	private static Set<VirtualFile> getActionBeanVirtualFiles(Project project) {
		Set<VirtualFile> retval = new HashSet<VirtualFile>();
		PsiClass actionBeanClass = StripesUtil.findPsiClassByName(StripesConstants.ACTION_BEAN, project);
		if (null != actionBeanClass) {
			Collection<PsiClass> actionBeans = ClassInheritorsSearch.search(
				actionBeanClass, GlobalSearchScope.projectProductionScope(project), true).findAll();

			for (PsiClass actionBean : actionBeans) {
				retval.add(actionBean.getContainingFile().getVirtualFile());
			}
		}
		return retval;
	}
}
