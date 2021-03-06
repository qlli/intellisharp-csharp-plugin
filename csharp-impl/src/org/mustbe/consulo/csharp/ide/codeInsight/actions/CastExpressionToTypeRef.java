/*
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

package org.mustbe.consulo.csharp.ide.codeInsight.actions;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.RequiredDispatchThread;
import org.mustbe.consulo.csharp.lang.psi.CSharpFileFactory;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeRefPresentationUtil;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRefUtil;
import com.intellij.BundleBase;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 30.12.14
 */
public class CastExpressionToTypeRef extends BaseIntentionAction
{
	@NotNull
	protected final SmartPsiElementPointer<DotNetExpression> myExpressionPointer;
	@NotNull
	protected final DotNetTypeRef myExpectedTypeRef;

	public CastExpressionToTypeRef(@NotNull DotNetExpression expression, @NotNull DotNetTypeRef expectedTypeRef)
	{
		myExpressionPointer = SmartPointerManager.getInstance(expression.getProject()).createSmartPsiElementPointer(expression);
		myExpectedTypeRef = expectedTypeRef;
	}

	@NotNull
	@Override
	@RequiredDispatchThread
	public String getText()
	{
		DotNetExpression element = myExpressionPointer.getElement();
		if(element == null)
		{
			return "invalid";
		}
		return BundleBase.format("Cast to ''{0}''", CSharpTypeRefPresentationUtil.buildTextWithKeyword(myExpectedTypeRef, element));
	}

	@NotNull
	@Override
	public String getFamilyName()
	{
		return "C#";
	}

	@Override
	@RequiredDispatchThread
	public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file)
	{
		if(myExpectedTypeRef == DotNetTypeRef.UNKNOWN_TYPE)
		{
			return false;
		}
		DotNetExpression element = myExpressionPointer.getElement();
		if(element == null)
		{
			return false;
		}

		if(DotNetTypeRefUtil.isVmQNameEqual(myExpectedTypeRef, element, DotNetTypes.System.Void))
		{
			return false;
		}
		return true;
	}

	@Override
	@RequiredDispatchThread
	public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException
	{
		DotNetExpression element = myExpressionPointer.getElement();
		if(element == null)
		{
			return;
		}

		String typeText = CSharpTypeRefPresentationUtil.buildShortText(myExpectedTypeRef, element);

		DotNetExpression expression = CSharpFileFactory.createExpression(project, "(" + typeText + ") " + element.getText());

		element.replace(expression);
	}
}
