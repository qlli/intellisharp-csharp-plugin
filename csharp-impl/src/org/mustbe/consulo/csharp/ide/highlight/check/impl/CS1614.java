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

package org.mustbe.consulo.csharp.ide.highlight.check.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.csharp.ide.highlight.check.CompilerCheck;
import org.mustbe.consulo.csharp.lang.psi.CSharpAttribute;
import org.mustbe.consulo.csharp.lang.psi.CSharpFileFactory;
import org.mustbe.consulo.csharp.lang.psi.CSharpReferenceExpression;
import org.mustbe.consulo.csharp.lang.psi.CSharpReferenceExpressionEx;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.csharp.lang.psi.resolve.AttributeByNameSelector;
import org.mustbe.consulo.csharp.module.extension.CSharpLanguageVersion;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 18.01.15
 */
public class CS1614 extends CompilerCheck<CSharpAttribute>
{
	public static abstract class BaseUseTypeFix extends BaseIntentionAction
	{
		private SmartPsiElementPointer<CSharpReferenceExpressionEx> myPointer;

		public BaseUseTypeFix(CSharpReferenceExpressionEx referenceExpression, CSharpTypeDeclaration typeDeclaration)
		{
			myPointer = SmartPointerManager.getInstance(referenceExpression.getProject()).createSmartPsiElementPointer(referenceExpression);

			setText("Use '" + typeDeclaration.getPresentableQName() + "'");
		}

		@NotNull
		@Override
		public String getFamilyName()
		{
			return "C#";
		}

		@Override
		public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file)
		{
			return myPointer.getElement() != null;
		}

		@Override
		public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException
		{
			CSharpReferenceExpressionEx element = myPointer.getElement();
			if(element == null)
			{
				return;
			}

			PsiElement referenceElement = element.getReferenceElement();

			assert referenceElement != null;

			PsiElement identifier = CSharpFileFactory.createIdentifier(project,buildReferenceText(element.getReferenceName()));

			referenceElement.replace(identifier);
		}

		public abstract String buildReferenceText(String baseText);
	}

	public static class UseTypeWithAtFix extends BaseUseTypeFix
	{
		public UseTypeWithAtFix(CSharpReferenceExpressionEx referenceExpression, CSharpTypeDeclaration typeDeclaration)
		{
			super(referenceExpression, typeDeclaration);
		}

		@Override
		public String buildReferenceText(String baseText)
		{
			return "@" + baseText;
		}
	}

	public static class UseTypeWithSuffixFix extends BaseUseTypeFix
	{
		public UseTypeWithSuffixFix(CSharpReferenceExpressionEx referenceExpression, CSharpTypeDeclaration typeDeclaration)
		{
			super(referenceExpression, typeDeclaration);
		}

		@Override
		public String buildReferenceText(String baseText)
		{
			return baseText + AttributeByNameSelector.AttributeSuffix;
		}
	}

	@RequiredReadAction
	@Nullable
	@Override
	public HighlightInfoFactory checkImpl(@NotNull CSharpLanguageVersion languageVersion, @NotNull CSharpAttribute element)
	{
		CSharpReferenceExpressionEx referenceExpression = (CSharpReferenceExpressionEx) element.getReferenceExpression();
		if(referenceExpression == null)
		{
			return null;
		}

		String referenceNameWithAt = referenceExpression.getReferenceNameWithAt();
		if(StringUtil.isEmpty(referenceNameWithAt) || referenceNameWithAt.charAt(0) == '@' || referenceNameWithAt.endsWith(AttributeByNameSelector
				.AttributeSuffix))
		{
			return null;
		}

		ResolveResult[] resolveResults = referenceExpression.multiResolveImpl(CSharpReferenceExpression.ResolveToKind.ATTRIBUTE, false);

		CSharpTypeDeclaration atType = null;
		CSharpTypeDeclaration suffixType = null;
		if((atType = hasElementWithName(resolveResults, referenceNameWithAt)) != null && (suffixType = hasElementWithName(resolveResults,
				referenceNameWithAt + AttributeByNameSelector.AttributeSuffix)) != null)
		{
			CompilerCheckBuilder compilerCheckBuilder = newBuilder(referenceExpression, referenceNameWithAt);
			compilerCheckBuilder.addQuickFix(new UseTypeWithAtFix(referenceExpression, atType));
			compilerCheckBuilder.addQuickFix(new UseTypeWithSuffixFix(referenceExpression, suffixType));
			return compilerCheckBuilder;
		}
		return super.checkImpl(languageVersion, element);
	}

	@Nullable
	private static CSharpTypeDeclaration hasElementWithName(ResolveResult[] resolveResults, String ref)
	{
		for(ResolveResult resolveResult : resolveResults)
		{
			if(!resolveResult.isValidResult())
			{
				continue;
			}
			PsiElement resolveResultElement = resolveResult.getElement();
			if(resolveResultElement instanceof CSharpTypeDeclaration && Comparing.equal(((CSharpTypeDeclaration) resolveResultElement).getName(),
					ref))
			{
				return (CSharpTypeDeclaration) resolveResultElement;
			}
		}
		return null;
	}
}
