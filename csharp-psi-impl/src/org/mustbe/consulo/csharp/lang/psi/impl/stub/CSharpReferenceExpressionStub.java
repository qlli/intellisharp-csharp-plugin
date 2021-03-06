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

package org.mustbe.consulo.csharp.lang.psi.impl.stub;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpReferenceExpression;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 05.12.14
 */
public class CSharpReferenceExpressionStub extends StubBase<CSharpReferenceExpression>
{
	private StringRef myReferenceText;
	private int myKindIndex;
	private int myMemberAccessTypeIndex;
	private boolean myGlobal;

	public CSharpReferenceExpressionStub(StubElement parent,
			IStubElementType elementType,
			String referenceText,
			int kindIndex,
			int memberAccessType,
			boolean global)
	{
		super(parent, elementType);
		myGlobal = global;
		myReferenceText = StringRef.fromNullableString(referenceText);
		myKindIndex = kindIndex;
		myMemberAccessTypeIndex = memberAccessType;
	}

	public CSharpReferenceExpressionStub(StubElement parent,
			IStubElementType elementType,
			StringRef referenceText,
			int kindIndex,
			int memberAccessType,
			boolean global)
	{
		super(parent, elementType);
		myReferenceText = referenceText;
		myKindIndex = kindIndex;
		myMemberAccessTypeIndex = memberAccessType;
		myGlobal = global;
	}

	public boolean isGlobal()
	{
		return myGlobal;
	}

	public String getReferenceText()
	{
		return StringRef.toString(myReferenceText);
	}

	public int getKindIndex()
	{
		return myKindIndex;
	}

	public int getMemberAccessTypeIndex()
	{
		return myMemberAccessTypeIndex;
	}

	@NotNull
	public CSharpReferenceExpression.AccessType getMemberAccessType()
	{
		return CSharpReferenceExpression.AccessType.VALUES[myMemberAccessTypeIndex];
	}

	@NotNull
	public CSharpReferenceExpression.ResolveToKind getKind()
	{
		return CSharpReferenceExpression.ResolveToKind.VALUES[myKindIndex];
	}
}
