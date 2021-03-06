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

package org.mustbe.consulo.csharp.lang.doc;

import java.util.List;

import org.emonic.base.documentation.IDocumentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.doc.psi.CSharpDocRoot;
import org.mustbe.consulo.csharp.lang.psi.UsefulPsiTreeUtil;
import org.mustbe.consulo.dotnet.documentation.DotNetDocumentationResolver;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 04.03.2015
 */
public class CSharpCommentDocumentationResolver implements DotNetDocumentationResolver
{
	@Nullable
	@Override
	public IDocumentation resolveDocumentation(@NotNull List<VirtualFile> virtualFile, @NotNull PsiElement element)
	{
		if(!(element instanceof DotNetQualifiedElement))
		{
			return null;
		}

		PsiElement prevSibling = UsefulPsiTreeUtil.getPrevSiblingSkipWhiteSpaces(element, true);
		if(prevSibling instanceof CSharpDocRoot)
		{
			return new CSharpDocAsIDocumentation((CSharpDocRoot)prevSibling);
		}
		return null;
	}
}
