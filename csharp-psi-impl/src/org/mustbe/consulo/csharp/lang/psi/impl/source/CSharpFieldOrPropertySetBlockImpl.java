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
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpFieldOrPropertySet;
import org.mustbe.consulo.csharp.lang.psi.CSharpFieldOrPropertySetBlock;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class CSharpFieldOrPropertySetBlockImpl extends CSharpElementImpl implements CSharpFieldOrPropertySetBlock
{
	public CSharpFieldOrPropertySetBlockImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitFieldOrPropertySetBlock(this);
	}

	@NotNull
	@Override
	public CSharpFieldOrPropertySet[] getSets()
	{
		return findChildrenByClass(CSharpFieldOrPropertySet.class);
	}
}
