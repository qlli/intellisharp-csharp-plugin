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
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import org.mustbe.consulo.dotnet.psi.DotNetModifierListOwner;

/**
 * @author VISTALL
 * @since 10.06.14
 */
public class RemoveModifierFix extends BaseModifierFix
{
	public RemoveModifierFix(DotNetModifier[] modifiers, DotNetModifierListOwner parent)
	{
		super(modifiers, parent);
	}

	public RemoveModifierFix(DotNetModifier modifier, DotNetModifierListOwner parent)
	{
		super(modifier, parent);
	}

	@Override
	public boolean isValidCondition(@NotNull DotNetModifierList modifierList, @NotNull DotNetModifier modifier)
	{
		return modifierList.hasModifier(modifier);
	}

	@NotNull
	@Override
	public String getActionName()
	{
		return "Remove";
	}

	@Override
	public void doAction(@NotNull DotNetModifierList modifierList, @NotNull DotNetModifier modifier)
	{
		modifierList.removeModifier(modifier);
	}
}
