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

package org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpNamespaceDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpNamespaceDeclStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 15.12.13.
 */
public class CSharpNamespaceStubElementType extends CSharpAbstractStubElementType<CSharpNamespaceDeclStub, CSharpNamespaceDeclarationImpl>
{
	public CSharpNamespaceStubElementType()
	{
		super("NAMESPACE_DECLARATION");
	}

	@Override
	public CSharpNamespaceDeclarationImpl createPsi(@NotNull CSharpNamespaceDeclStub cSharpNamespaceStub)
	{
		return new CSharpNamespaceDeclarationImpl(cSharpNamespaceStub);
	}

	@NotNull
	@Override
	public CSharpNamespaceDeclarationImpl createElement(@NotNull ASTNode astNode)
	{
		return new CSharpNamespaceDeclarationImpl(astNode);
	}

	@RequiredReadAction
	@Override
	public CSharpNamespaceDeclStub createStub(@NotNull CSharpNamespaceDeclarationImpl declaration, StubElement stubElement)
	{
		String qName = declaration.getQNameFromDecl();
		return new CSharpNamespaceDeclStub(stubElement, this, qName);
	}

	@Override
	public void serialize(@NotNull CSharpNamespaceDeclStub namespaceStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(namespaceStub.getQualifiedName());
	}

	@NotNull
	@Override
	public CSharpNamespaceDeclStub deserialize(@NotNull StubInputStream stubInputStream, StubElement stubElement) throws IOException
	{
		StringRef qname = stubInputStream.readName();
		return new CSharpNamespaceDeclStub(stubElement, this, qname);
	}
}
