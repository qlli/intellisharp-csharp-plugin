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

package org.mustbe.consulo.csharp.ide.highlight.check.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.csharp.ide.CSharpErrorBundle;
import org.mustbe.consulo.csharp.ide.codeInsight.actions.CastExpressionToTypeRef;
import org.mustbe.consulo.csharp.ide.codeInsight.actions.ChangeReturnToTypeRefFix;
import org.mustbe.consulo.csharp.ide.codeInsight.actions.ChangeVariableToTypeRefFix;
import org.mustbe.consulo.csharp.ide.highlight.CSharpHighlightKey;
import org.mustbe.consulo.csharp.ide.highlight.check.CompilerCheck;
import org.mustbe.consulo.csharp.lang.psi.CSharpConversionMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpModifier;
import org.mustbe.consulo.csharp.lang.psi.CSharpSimpleLikeMethodAsElement;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeRefPresentationUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpImplicitReturnModel;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpTypeUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpAssignmentExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpDoWhileStatementImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpIfStatementImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpLambdaExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpOperatorReferenceImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpRefTypeExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpRefValueExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpReturnStatementImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpWhileStatementImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpLambdaResolveResult;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpStaticTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpTypeRefByQName;
import org.mustbe.consulo.csharp.module.extension.CSharpLanguageVersion;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRefUtil;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeResolveResult;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.openapi.util.Trinity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 15.05.14
 */
public class CS0029 extends CompilerCheck<PsiElement>
{
	@RequiredReadAction
	@Nullable
	@Override
	public CompilerCheckBuilder checkImpl(@NotNull CSharpLanguageVersion languageVersion, @NotNull PsiElement element)
	{
		val resolve = resolve(element);
		if(resolve == null)
		{
			return null;
		}

		DotNetTypeRef firstTypeRef = resolve.getFirst();


		DotNetTypeRef secondTypeRef = resolve.getSecond();
		PsiElement elementToHighlight = resolve.getThird();

		CSharpTypeUtil.InheritResult inheritResult = CSharpTypeUtil.isInheritable(firstTypeRef, secondTypeRef, element,
				CSharpStaticTypeRef.IMPLICIT);
		if(!inheritResult.isSuccess())
		{
			CompilerCheckBuilder builder = newBuilder(elementToHighlight, CSharpTypeRefPresentationUtil.buildTextWithKeyword(secondTypeRef,
					element), CSharpTypeRefPresentationUtil.buildTextWithKeyword(firstTypeRef, element));

			if(elementToHighlight instanceof DotNetExpression)
			{
				builder.addQuickFix(new CastExpressionToTypeRef((DotNetExpression) elementToHighlight, firstTypeRef));
			}

			if(element instanceof DotNetVariable)
			{
				builder.addQuickFix(new ChangeVariableToTypeRefFix((DotNetVariable) element, secondTypeRef));
			}

			if(element instanceof CSharpReturnStatementImpl)
			{
				CSharpSimpleLikeMethodAsElement methodElement = PsiTreeUtil.getParentOfType(element, CSharpSimpleLikeMethodAsElement.class);
				if(methodElement instanceof CSharpConversionMethodDeclaration || methodElement instanceof CSharpMethodDeclaration)
				{
					builder.addQuickFix(new ChangeReturnToTypeRefFix((DotNetLikeMethodDeclaration) methodElement, secondTypeRef));
				}
			}
			return builder;
		}
		else if(inheritResult.isConversion())
		{
			String text = CSharpErrorBundle.message("impicit.cast.from.0.to.1", CSharpTypeRefPresentationUtil.buildTextWithKeyword(secondTypeRef,
					element), CSharpTypeRefPresentationUtil.buildTextWithKeyword(firstTypeRef, element));
			return newBuilder(elementToHighlight).setText(text).setHighlightInfoType(HighlightInfoType.INFORMATION).setTextAttributesKey
					(CSharpHighlightKey.IMPLICIT_OR_EXPLICIT_CAST);
		}

		return null;
	}

	@RequiredReadAction
	private Trinity<? extends DotNetTypeRef, ? extends DotNetTypeRef, ? extends PsiElement> resolve(PsiElement element)
	{
		if(element instanceof DotNetVariable)
		{
			DotNetExpression initializer = ((DotNetVariable) element).getInitializer();
			if(initializer == null)
			{
				return null;
			}
			DotNetTypeRef variableTypRef = ((DotNetVariable) element).toTypeRef(false);
			if(variableTypRef == DotNetTypeRef.AUTO_TYPE)
			{
				return null;
			}
			return Trinity.create(variableTypRef, initializer.toTypeRef(true), initializer);
		}
		else if(element instanceof DotNetExpression && element.getParent() instanceof CSharpMethodDeclaration)
		{
			CSharpMethodDeclaration parent = (CSharpMethodDeclaration) element.getParent();
			assert parent.getCodeBlock() == element;

			CSharpImplicitReturnModel model = CSharpImplicitReturnModel.None;
			if(parent.hasModifier(CSharpModifier.ASYNC))
			{
				model = CSharpImplicitReturnModel.Async;
			}

			DotNetTypeRef returnTypeRef = model.extractTypeRef(parent.getReturnTypeRef(), element);
			return Trinity.create(returnTypeRef, ((DotNetExpression) element).toTypeRef(true), element);
		}
		else if(element instanceof CSharpAssignmentExpressionImpl)
		{
			CSharpOperatorReferenceImpl operatorElement = ((CSharpAssignmentExpressionImpl) element).getOperatorElement();
			if(operatorElement.getOperatorElementType() != CSharpTokens.EQ)
			{
				return null;
			}
			DotNetExpression[] expressions = ((CSharpAssignmentExpressionImpl) element).getParameterExpressions();
			if(expressions.length != 2)
			{
				return null;
			}
			return Trinity.create(expressions[0].toTypeRef(true), expressions[1].toTypeRef(true), expressions[1]);
		}
		else if(element instanceof CSharpWhileStatementImpl)
		{
			DotNetExpression conditionExpression = ((CSharpWhileStatementImpl) element).getConditionExpression();
			if(conditionExpression == null)
			{
				return null;
			}
			return Trinity.create(new CSharpTypeRefByQName(DotNetTypes.System.Boolean), conditionExpression.toTypeRef(true), conditionExpression);
		}
		else if(element instanceof CSharpDoWhileStatementImpl)
		{
			DotNetExpression conditionExpression = ((CSharpDoWhileStatementImpl) element).getConditionExpression();
			if(conditionExpression == null)
			{
				return null;
			}
			return Trinity.create(new CSharpTypeRefByQName(DotNetTypes.System.Boolean), conditionExpression.toTypeRef(true), conditionExpression);
		}
		else if(element instanceof CSharpIfStatementImpl)
		{
			DotNetExpression conditionExpression = ((CSharpIfStatementImpl) element).getConditionExpression();
			if(conditionExpression == null)
			{
				return null;
			}
			return Trinity.create(new CSharpTypeRefByQName(DotNetTypes.System.Boolean), conditionExpression.toTypeRef(true), conditionExpression);
		}
		else if(element instanceof CSharpRefTypeExpressionImpl)
		{
			DotNetExpression expression = ((CSharpRefTypeExpressionImpl) element).getExpression();
			if(expression == null)
			{
				return null;
			}
			return Trinity.create(new CSharpTypeRefByQName(DotNetTypes.System.TypedReference), expression.toTypeRef(true), expression);
		}
		else if(element instanceof CSharpRefValueExpressionImpl)
		{
			DotNetExpression expression = ((CSharpRefValueExpressionImpl) element).getExpression();
			if(expression == null)
			{
				return null;
			}
			return Trinity.create(new CSharpTypeRefByQName(DotNetTypes.System.TypedReference), expression.toTypeRef(true), expression);
		}
		else if(element instanceof CSharpLambdaExpressionImpl)
		{
			CSharpLambdaExpressionImpl lambdaExpression = (CSharpLambdaExpressionImpl) element;
			DotNetTypeRef typeRefOfLambda = lambdaExpression.toTypeRef(true);
			if(typeRefOfLambda == DotNetTypeRef.ERROR_TYPE)
			{
				return null;
			}

			DotNetTypeResolveResult typeResolveResult = typeRefOfLambda.resolve(element);
			if(!(typeResolveResult instanceof CSharpLambdaResolveResult))
			{
				return null;
			}
			PsiElement singleExpression = lambdaExpression.getCodeBlock();
			if(singleExpression instanceof DotNetExpression)
			{
				DotNetTypeRef returnTypeRef = ((CSharpLambdaResolveResult) typeResolveResult).getReturnTypeRef();

				CSharpImplicitReturnModel model = CSharpImplicitReturnModel.None;
				if(lambdaExpression.hasModifier(CSharpModifier.ASYNC))
				{
					model = CSharpImplicitReturnModel.Async;
				}

				returnTypeRef = model.extractTypeRef(returnTypeRef, element);

				// void type allow any type if used expression body
				if(DotNetTypeRefUtil.isVmQNameEqual(returnTypeRef, element, DotNetTypes.System.Void))
				{
					return null;
				}
				return Trinity.create(returnTypeRef, ((DotNetExpression) singleExpression).toTypeRef(true), singleExpression);
			}
		}
		else if(element instanceof CSharpReturnStatementImpl)
		{
			CSharpSimpleLikeMethodAsElement pseudoMethod = PsiTreeUtil.getParentOfType(element, CSharpSimpleLikeMethodAsElement.class);
			if(pseudoMethod == null)
			{
				return null;
			}

			DotNetTypeRef expected = pseudoMethod.getReturnTypeRef();
			if(expected == DotNetTypeRef.UNKNOWN_TYPE)
			{
				return null;
			}

			DotNetTypeRef actual = null;
			DotNetExpression expression = ((CSharpReturnStatementImpl) element).getExpression();
			if(expression == null)
			{
				actual = new CSharpTypeRefByQName(DotNetTypes.System.Void);
			}
			else
			{
				actual = expression.toTypeRef(false);
			}

			CSharpImplicitReturnModel implicitReturnModel = CSharpImplicitReturnModel.getImplicitReturnModel((CSharpReturnStatementImpl) element,
					pseudoMethod);
			DotNetTypeRef typeRef = implicitReturnModel.extractTypeRef(expected, element);
			if(typeRef != DotNetTypeRef.ERROR_TYPE)
			{
				expected = typeRef;
			}
			return Trinity.create(expected, actual, ObjectUtil.notNull(expression, element));
		}
		return null;
	}
}
