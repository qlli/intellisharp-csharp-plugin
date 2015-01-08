/*
 * Copyright 2000-2009 JetBrains s.r.o.
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.csharp.lang.psi.impl.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpRecursiveElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.index.TypeIndex;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.search.searches.TypeSearchParameters;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;

/**
 * @author max
 *         <p/>
 *         Copied from Java plugin by Jetbrains (com.intellij.psi.search.searches.ClassInheritorsSearch)
 */
public class CSharpAllClassesSearchExecutor implements QueryExecutor<DotNetTypeDeclaration, TypeSearchParameters>
{
	@Override
	public boolean execute(@NotNull final TypeSearchParameters queryParameters, @NotNull final Processor<DotNetTypeDeclaration>
			consumer)
	{
		SearchScope scope = queryParameters.getScope();

		if(scope instanceof GlobalSearchScope)
		{
			return processAllClassesInGlobalScope((GlobalSearchScope) scope, consumer, queryParameters);
		}

		PsiElement[] scopeRoots = ((LocalSearchScope) scope).getScope();
		for(final PsiElement scopeRoot : scopeRoots)
		{
			if(!processScopeRootForAllClasses(scopeRoot, consumer))
			{
				return false;
			}
		}
		return true;
	}

	private static boolean processAllClassesInGlobalScope(final GlobalSearchScope scope, final Processor<DotNetTypeDeclaration> processor,
			final TypeSearchParameters parameters)
	{
		final Collection<String> names = ApplicationManager.getApplication().runReadAction(new Computable<Collection<String>>()
		{
			@Override
			public Collection<String> compute()
			{
				return TypeIndex.getInstance().getAllKeys(parameters.getModule().getProject());
			}
		});

		final ProgressIndicator indicator = ProgressIndicatorProvider.getGlobalProgressIndicator();
		if(indicator != null)
		{
			indicator.checkCanceled();
		}

		List<String> sorted = new ArrayList<String>(names.size());
		int i = 0;
		for(String name : names)
		{
			if(parameters.getNameCondition().value(name))
			{
				sorted.add(name);
			}
			if(indicator != null && i % 512 == 0)
			{
				indicator.checkCanceled();
			}

			i++;
		}

		if(indicator != null)
		{
			indicator.checkCanceled();
		}

		Collections.sort(sorted, new Comparator<String>()
		{
			@Override
			public int compare(final String o1, final String o2)
			{
				return o1.compareToIgnoreCase(o2);
			}
		});

		for(final String name : sorted)
		{
			ProgressIndicatorProvider.checkCanceled();
			final Collection<DotNetTypeDeclaration> classes = ApplicationManager.getApplication().runReadAction(new Computable<Collection<DotNetTypeDeclaration>>()
			{
				@Override
				public Collection<DotNetTypeDeclaration> compute()
				{
					return TypeIndex.getInstance().get(name, parameters.getModule().getProject(), scope);
				}
			});
			for(DotNetTypeDeclaration psiClass : classes)
			{
				ProgressIndicatorProvider.checkCanceled();
				if(!processor.process(psiClass))
				{
					return false;
				}
			}
		}
		return true;
	}

	private static boolean processScopeRootForAllClasses(PsiElement scopeRoot, final Processor<DotNetTypeDeclaration> processor)
	{
		if(scopeRoot == null)
		{
			return true;
		}
		final boolean[] stopped = new boolean[]{false};

		scopeRoot.accept(new CSharpRecursiveElementVisitor()
		{
			@Override
			public void visitElement(PsiElement element)
			{
				if(!stopped[0])
				{
					super.visitElement(element);
				}
			}

			@Override
			public void visitTypeDeclaration(CSharpTypeDeclaration aClass)
			{
				stopped[0] = !processor.process(aClass);
				super.visitTypeDeclaration(aClass);
			}
		});

		return !stopped[0];
	}
}
