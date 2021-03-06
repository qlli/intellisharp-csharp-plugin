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

package org.mustbe.consulo.csharp.ide.debugger;

import org.jetbrains.annotations.NotNull;
import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;

/**
 * @author VISTALL
 * @since 06.06.14
 */
public class CSharpErrorValue extends XValue
{
	private final String myError;

	public CSharpErrorValue(String error)
	{
		myError = error;
	}

	@Override
	public void computePresentation(
			@NotNull XValueNode node, @NotNull XValuePlace place)
	{
		node.setPresentation(AllIcons.Debugger.Watch, new XValuePresentation()
		{
			@Override
			public void renderValue(@NotNull XValueTextRenderer renderer)
			{
				renderer.renderError(myError);
			}
		}, false);
	}
}
