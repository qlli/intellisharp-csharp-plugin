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

package org.mustbe.consulo.csharp.ide.lineMarkerProvider;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.RequiredReadAction;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 25.03.14
 */
public interface LineMarkerCollector
{
	@RequiredReadAction
	void collect(PsiElement psiElement, @NotNull Collection<LineMarkerInfo> lineMarkerInfos);
}
