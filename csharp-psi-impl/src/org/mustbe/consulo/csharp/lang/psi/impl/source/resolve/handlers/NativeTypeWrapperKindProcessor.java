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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.handlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.csharp.lang.psi.CSharpReferenceExpression;
import org.mustbe.consulo.csharp.lang.psi.impl.msil.CSharpTransform;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpNativeTypeImplUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.CSharpResolveOptions;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.util.Processor;

/**
 * @author VISTALL
 * @since 27.07.2015
 */
public class NativeTypeWrapperKindProcessor implements KindProcessor
{
	@RequiredReadAction
	@Override
	public void process(@NotNull CSharpResolveOptions options,
			@NotNull DotNetGenericExtractor defaultExtractor,
			@Nullable PsiElement forceQualifierElement,
			@NotNull Processor<ResolveResult> processor)
	{
		PsiElement element = options.getElement();

		PsiElement nativeElement = ((CSharpReferenceExpression) element).getReferenceElement();
		assert nativeElement != null;
		String nativeRuntimeType = CSharpNativeTypeImplUtil.ourElementToQTypes.get(nativeElement.getNode().getElementType());
		if(nativeRuntimeType == null)
		{
			return;
		}
		PsiElement resolve = DotNetPsiSearcher.getInstance(element.getProject()).findType(nativeRuntimeType, element.getResolveScope(),
				DotNetPsiSearcher.TypeResoleKind.UNKNOWN, CSharpTransform.INSTANCE);
		if(resolve == null)
		{
			return;
		}
		processor.process(new PsiElementResolveResult(resolve, true));
	}
}
