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

package org.mustbe.consulo.csharp.ide.highlight.check;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.csharp.ide.CSharpElementPresentationUtil;
import org.mustbe.consulo.csharp.ide.CSharpErrorBundle;
import org.mustbe.consulo.csharp.lang.psi.CSharpLocalVariable;
import org.mustbe.consulo.csharp.module.extension.CSharpLanguageVersion;
import org.mustbe.consulo.dotnet.ide.DotNetElementPresentationUtil;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import org.mustbe.consulo.dotnet.psi.DotNetXXXAccessor;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.NullableFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 09.03.14
 */
public abstract class CompilerCheck<T extends PsiElement>
{
	public static abstract class HighlightInfoFactory implements NullableFactory<HighlightInfo>
	{
		@NotNull
		public List<IntentionAction> getQuickFixes()
		{
			return Collections.emptyList();
		}
	}

	public static class CompilerCheckBuilder extends HighlightInfoFactory
	{
		private String myText;
		private TextRange myTextRange;
		private HighlightInfoType myHighlightInfoType;
		private TextAttributesKey myTextAttributesKey;

		private List<IntentionAction> myQuickFixes = Collections.emptyList();

		public TextRange getTextRange()
		{
			return myTextRange;
		}

		public CompilerCheckBuilder setTextRange(TextRange textRange)
		{
			myTextRange = textRange;
			return this;
		}

		public String getText()
		{
			return myText;
		}

		public CompilerCheckBuilder setText(String text)
		{
			myText = text;
			return this;
		}

		public HighlightInfoType getHighlightInfoType()
		{
			return myHighlightInfoType;
		}

		public TextAttributesKey getTextAttributesKey()
		{
			return myTextAttributesKey;
		}

		public CompilerCheckBuilder setTextAttributesKey(TextAttributesKey textAttributesKey)
		{
			myTextAttributesKey = textAttributesKey;
			return this;
		}

		public CompilerCheckBuilder setHighlightInfoType(HighlightInfoType highlightInfoType)
		{
			myHighlightInfoType = highlightInfoType;
			return this;
		}

		public CompilerCheckBuilder addQuickFix(IntentionAction a)
		{
			if(myQuickFixes.isEmpty())
			{
				myQuickFixes = new ArrayList<IntentionAction>(3);
			}
			myQuickFixes.add(a);
			return this;
		}

		@Override
		@NotNull
		public List<IntentionAction> getQuickFixes()
		{
			return myQuickFixes;
		}

		@Nullable
		@Override
		public HighlightInfo create()
		{
			HighlightInfo.Builder builder = HighlightInfo.newHighlightInfo(getHighlightInfoType());
			builder = builder.descriptionAndTooltip(getText());
			builder = builder.range(getTextRange());

			TextAttributesKey textAttributesKey = getTextAttributesKey();
			if(textAttributesKey != null)
			{
				builder = builder.textAttributes(textAttributesKey);
			}
			return builder.create();
		}
	}

	@NotNull
	@RequiredReadAction
	public List<? extends HighlightInfoFactory> check(@NotNull CSharpLanguageVersion languageVersion, @NotNull T element)
	{
		HighlightInfoFactory check = checkImpl(languageVersion, element);
		if(check == null)
		{
			return Collections.emptyList();
		}
		return Collections.singletonList(check);
	}

	@Nullable
	@RequiredReadAction
	public HighlightInfoFactory checkImpl(@NotNull CSharpLanguageVersion languageVersion, @NotNull T element)
	{
		return null;
	}

	@NotNull
	@RequiredReadAction
	public CompilerCheckBuilder newBuilder(@NotNull PsiElement range, String... args)
	{
		return newBuilderImpl(getClass(), range, args);
	}

	@NotNull
	@RequiredReadAction
	public CompilerCheckBuilder newBuilder(@NotNull TextRange range, String... args)
	{
		return newBuilderImpl(getClass(), range, args);
	}

	@NotNull
	@RequiredReadAction
	public static CompilerCheckBuilder newBuilderImpl(@NotNull Class<?> clazz, @NotNull PsiElement range, String... args)
	{
		return newBuilderImpl(clazz, range.getTextRange(), args);
	}

	@NotNull
	@RequiredReadAction
	public static CompilerCheckBuilder newBuilderImpl(@NotNull Class<?> clazz, @NotNull TextRange range, String... args)
	{
		CompilerCheckBuilder result = new CompilerCheckBuilder();
		result.setText(message(clazz, args));
		result.setTextRange(range);
		return result;
	}

	@NotNull
	public static String message(@NotNull Class<?> aClass, String... args)
	{
		String id = aClass.getSimpleName();
		String message = CSharpErrorBundle.message(id, args);
		if(ApplicationManager.getApplication().isInternal())
		{
			message = id + ": " + message;
		}
		return message;
	}

	@RequiredReadAction
	@Nullable
	public static String formatElement(PsiElement e)
	{
		if(e instanceof DotNetParameter)
		{
			return ((DotNetParameter) e).getName();
		}
		else if(e instanceof DotNetGenericParameter)
		{
			return ((DotNetGenericParameter) e).getName();
		}
		else if(e instanceof CSharpLocalVariable)
		{
			return ((CSharpLocalVariable) e).getName();
		}
		else if(e instanceof DotNetXXXAccessor)
		{
			PsiElement parent = e.getParent();
			return formatElement(parent) + "." + ((DotNetXXXAccessor) e).getAccessorKind().name().toLowerCase();
		}

		String parentName = null;
		PsiElement parent = e.getParent();
		if(parent instanceof DotNetNamespaceDeclaration)
		{
			parentName = ((DotNetNamespaceDeclaration) parent).getPresentableQName();
		}
		else if(parent instanceof DotNetTypeDeclaration)
		{
			parentName = DotNetElementPresentationUtil.formatTypeWithGenericParameters((DotNetTypeDeclaration) parent);
		}

		String currentText = "Unknown element : " + e.getClass().getSimpleName();
		if(e instanceof DotNetLikeMethodDeclaration)
		{
			currentText = CSharpElementPresentationUtil.formatMethod((DotNetLikeMethodDeclaration) e, 0);
		}
		else if(e instanceof DotNetTypeDeclaration)
		{
			currentText = DotNetElementPresentationUtil.formatTypeWithGenericParameters((DotNetTypeDeclaration) e);
		}
		else if(e instanceof DotNetVariable && e instanceof DotNetQualifiedElement)
		{
			currentText = ((DotNetQualifiedElement) e).getName();
		}

		if(StringUtil.isEmpty(parentName))
		{
			return currentText;
		}
		else
		{
			return parentName + "." + currentText;
		}
	}
}
