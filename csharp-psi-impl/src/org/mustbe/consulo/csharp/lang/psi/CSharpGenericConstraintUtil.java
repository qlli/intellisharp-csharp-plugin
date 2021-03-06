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

package org.mustbe.consulo.csharp.lang.psi;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpTypeRefByQName;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.psi.PsiElement;
import com.intellij.util.SmartList;
import lombok.val;

/**
 * @author VISTALL
 * @since 18.05.14
 */
public class CSharpGenericConstraintUtil
{
	public static List<DotNetTypeRef> getExtendTypes(@NotNull DotNetGenericParameter parameter)
	{
		CSharpGenericConstraint genericConstraint = findGenericConstraint(parameter);
		if(genericConstraint == null)
		{
			return Collections.<DotNetTypeRef>singletonList(new CSharpTypeRefByQName(DotNetTypes.System.Object));
		}

		val superTypes = new SmartList<DotNetTypeRef>();
		for(CSharpGenericConstraintValue value : genericConstraint.getGenericConstraintValues())
		{
			if(value instanceof CSharpGenericConstraintTypeValue)
			{
				DotNetTypeRef typeRef = ((CSharpGenericConstraintTypeValue) value).toTypeRef();
				superTypes.add(typeRef);
			}
			else if(value instanceof CSharpGenericConstraintKeywordValue)
			{
				if(((CSharpGenericConstraintKeywordValue) value).getKeywordElementType() == CSharpTokens.STRUCT_KEYWORD)
				{
					superTypes.add(new CSharpTypeRefByQName(DotNetTypes.System.ValueType));
				}
				else if(((CSharpGenericConstraintKeywordValue) value).getKeywordElementType() == CSharpTokens.CLASS_KEYWORD)
				{
					superTypes.add(new CSharpTypeRefByQName(DotNetTypes.System.Object));
				}
			}
		}

		if(superTypes.isEmpty())
		{
			superTypes.add(new CSharpTypeRefByQName(DotNetTypes.System.Object));
		}
		return superTypes;
	}

	@Nullable
	public static CSharpGenericConstraint findGenericConstraint(@NotNull DotNetGenericParameter element)
	{
		PsiElement firstParent = element.getParent();
		if(firstParent == null)
		{
			return null;
		}
		PsiElement parent = firstParent.getParent();
		if(parent instanceof CSharpGenericConstraintOwner)
		{
			return CSharpGenericConstraintUtil.forParameter((CSharpGenericConstraintOwner) parent, element);
		}
		return null;
	}

	@Nullable
	public static CSharpGenericConstraint forParameter(@NotNull CSharpGenericConstraintOwner owner, @NotNull DotNetGenericParameter parameter)
	{
		for(CSharpGenericConstraint constraint : owner.getGenericConstraints())
		{
			if(constraint.resolve() == parameter)
			{
				return constraint;
			}
		}
		return null;
	}
}
