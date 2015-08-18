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
import org.mustbe.consulo.csharp.lang.psi.CSharpConversionMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpTypeUtil;
import org.mustbe.consulo.csharp.module.extension.CSharpLanguageVersion;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.util.ArrayUtil2;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 19.08.2015
 */
public class CS0555 extends CompilerCheck<CSharpConversionMethodDeclaration>
{
	@RequiredReadAction
	@Nullable
	@Override
	public HighlightInfoFactory checkImpl(@NotNull CSharpLanguageVersion languageVersion,
			@NotNull CSharpConversionMethodDeclaration element)
	{
		DotNetTypeRef typeRef1 = element.getReturnTypeRef();
		DotNetTypeRef typeRef2 = ArrayUtil2.safeGet(element.getParameterTypeRefs(), 0);
		if(typeRef2 == null)
		{
			return null;
		}

		if(CSharpTypeUtil.isTypeEqual(typeRef1, typeRef2, element))
		{
			PsiElement operatorElement = element.getOperatorElement();
			if(operatorElement == null)
			{
				return null;
			}
			return newBuilder(operatorElement);
		}
		return super.checkImpl(languageVersion, element);
	}
}
