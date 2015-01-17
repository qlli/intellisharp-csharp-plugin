/*
 * Copyright 2013-2015 must-be.org
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

package org.mustbe.consulo.csharp.ide.refactoring.rename;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpConstructorDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.resolve.CSharpCompositeResolveContext;
import org.mustbe.consulo.csharp.lang.psi.impl.resolve.CSharpResolveContextUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.resolve.CSharpTypeResolveContext;
import org.mustbe.consulo.csharp.lang.psi.resolve.CSharpElementGroup;
import org.mustbe.consulo.csharp.lang.psi.resolve.CSharpResolveContext;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;

/**
 * @author VISTALL
 * @since 17.01.15
 */
public class CSharpTypeRenamePsiElementProcessor extends RenamePsiElementProcessor
{
	@Nullable
	@Override
	public PsiElement substituteElementToRename(PsiElement element, @Nullable Editor editor)
	{
		if(element instanceof CSharpConstructorDeclaration)
		{
			return PsiTreeUtil.getParentOfType(element, CSharpTypeDeclaration.class);
		}
		return super.substituteElementToRename(element, editor);
	}

	@Override
	public boolean canProcessElement(@NotNull PsiElement element)
	{
		return element instanceof CSharpTypeDeclaration || element instanceof CSharpConstructorDeclaration;
	}

	@Override
	public void prepareRenaming(PsiElement element, String newName, Map<PsiElement, String> allRenames, SearchScope scope)
	{
		CSharpResolveContext context = CSharpResolveContextUtil.createContext(DotNetGenericExtractor.EMPTY,
				(GlobalSearchScope) element.getUseScope(), element);

		CSharpElementGroup<CSharpConstructorDeclaration> constructors = context.constructorGroup();
		if(constructors != null)
		{
			for(CSharpConstructorDeclaration declaration : constructors.getElements())
			{
				allRenames.put(declaration, newName);
			}
		}
		constructors = context.deConstructorGroup();
		if(constructors != null)
		{
			for(CSharpConstructorDeclaration declaration : constructors.getElements())
			{
				allRenames.put(declaration, newName);
			}
		}

		// if we have composite - that partial type, need append other types
		if(context instanceof CSharpCompositeResolveContext)
		{
			for(CSharpResolveContext resolveContext : ((CSharpCompositeResolveContext) context).getContexts())
			{
				if(resolveContext instanceof CSharpTypeResolveContext)
				{
					allRenames.put(((CSharpTypeResolveContext) resolveContext).getElement(), newName);
				}
			}
		}
	}
}
