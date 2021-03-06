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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpGenericConstraint;
import org.mustbe.consulo.csharp.lang.psi.CSharpGenericConstraintList;
import org.mustbe.consulo.csharp.lang.psi.CSharpStubElements;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.ExecuteTarget;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.ExecuteTargetUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpTypeRefByQName;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpTypeDeclStub;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetInheritUtil;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclarationUtil;
import org.mustbe.consulo.dotnet.psi.DotNetTypeList;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpTypeDeclarationImpl extends CSharpStubMemberImpl<CSharpTypeDeclStub> implements CSharpTypeDeclaration
{
	public CSharpTypeDeclarationImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public CSharpTypeDeclarationImpl(@NotNull CSharpTypeDeclStub stub)
	{
		super(stub, CSharpStubElements.TYPE_DECLARATION);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitTypeDeclaration(this);
	}

	@RequiredReadAction
	@Override
	public PsiElement getLeftBrace()
	{
		return findChildByType(CSharpTokens.LBRACE);
	}

	@RequiredReadAction
	@Override
	public PsiElement getRightBrace()
	{
		return findChildByType(CSharpTokens.RBRACE);
	}

	@Nullable
	@Override
	public DotNetGenericParameterList getGenericParameterList()
	{
		return getStubOrPsiChild(CSharpStubElements.GENERIC_PARAMETER_LIST);
	}

	@NotNull
	@Override
	public DotNetGenericParameter[] getGenericParameters()
	{
		DotNetGenericParameterList genericParameterList = getGenericParameterList();
		return genericParameterList == null ? DotNetGenericParameter.EMPTY_ARRAY : genericParameterList.getParameters();
	}

	@Override
	public int getGenericParametersCount()
	{
		DotNetGenericParameterList genericParameterList = getGenericParameterList();
		return genericParameterList == null ? 0 : genericParameterList.getGenericParametersCount();
	}

	@Override
	public String getVmQName()
	{
		return DotNetTypeDeclarationUtil.getVmQName(this);
	}

	@Nullable
	@Override
	public String getVmName()
	{
		return DotNetTypeDeclarationUtil.getVmName(this);
	}

	@NotNull
	@Override
	public DotNetQualifiedElement[] getMembers()
	{
		return CachedValuesManager.getCachedValue(this, new CachedValueProvider<DotNetQualifiedElement[]>()
		{
			@Nullable
			@Override
			public Result<DotNetQualifiedElement[]> compute()
			{
				return Result.create(getStubOrPsiChildren(CSharpStubElements.QUALIFIED_MEMBERS, DotNetQualifiedElement.ARRAY_FACTORY),
						PsiModificationTracker.OUT_OF_CODE_BLOCK_MODIFICATION_COUNT);
			}
		});
	}

	@Override
	public boolean isInterface()
	{
		CSharpTypeDeclStub stub = getStub();
		if(stub != null)
		{
			return stub.isInterface();
		}
		return findChildByType(CSharpTokens.INTERFACE_KEYWORD) != null;
	}

	@Override
	public boolean isStruct()
	{
		CSharpTypeDeclStub stub = getStub();
		if(stub != null)
		{
			return stub.isStruct();
		}
		return findChildByType(CSharpTokens.STRUCT_KEYWORD) != null;
	}

	@Override
	public boolean isEnum()
	{
		CSharpTypeDeclStub stub = getStub();
		if(stub != null)
		{
			return stub.isEnum();
		}
		return findChildByType(CSharpTokens.ENUM_KEYWORD) != null;
	}

	@Override
	public boolean isNested()
	{
		PsiElement parentByStub = getParentByStub();
		return parentByStub instanceof DotNetTypeDeclaration;
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent,
			@NotNull PsiElement place)
	{
		if(ExecuteTargetUtil.canProcess(processor, ExecuteTarget.GENERIC_PARAMETER))
		{
			for(DotNetGenericParameter dotNetGenericParameter : getGenericParameters())
			{
				if(!processor.execute(dotNetGenericParameter, state))
				{
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean isEquivalentTo(PsiElement another)
	{
		return CSharpTypeDeclarationImplUtil.isEquivalentTo(this, another);
	}

	@Override
	public DotNetTypeList getExtendList()
	{
		return getStubOrPsiChild(CSharpStubElements.EXTENDS_LIST);
	}

	@NotNull
	@Override
	public DotNetTypeRef[] getExtendTypeRefs()
	{
		return CSharpTypeDeclarationImplUtil.getExtendTypeRefs(this);
	}

	@RequiredReadAction
	@Override
	public boolean isInheritor(@NotNull String other, boolean deep)
	{
		return DotNetInheritUtil.isInheritor(this, other, deep);
	}

	@Override
	public DotNetTypeRef getTypeRefForEnumConstants()
	{
		DotNetTypeList extendList = getExtendList();
		if(extendList == null)
		{
			return new CSharpTypeRefByQName(DotNetTypes.System.Int32);
		}
		DotNetTypeRef[] typeRefs = extendList.getTypeRefs();
		return typeRefs.length == 0 ? new CSharpTypeRefByQName(DotNetTypes.System.Int32) : typeRefs[0];
	}

	@Nullable
	@Override
	public CSharpGenericConstraintList getGenericConstraintList()
	{
		return getStubOrPsiChild(CSharpStubElements.GENERIC_CONSTRAINT_LIST);
	}

	@NotNull
	@Override
	public CSharpGenericConstraint[] getGenericConstraints()
	{
		CSharpGenericConstraintList genericConstraintList = getGenericConstraintList();
		return genericConstraintList == null ? CSharpGenericConstraint.EMPTY_ARRAY : genericConstraintList.getGenericConstraints();
	}
}
