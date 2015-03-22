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

package org.mustbe.consulo.csharp.ide.highlight;

import org.mustbe.consulo.csharp.lang.CSharpLanguage;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public interface CSharpHighlightKey
{
	TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE, DefaultLanguageHighlighterColors.STRING);
	TextAttributesKey CLASS_NAME = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE, DefaultLanguageHighlighterColors.CLASS_NAME);
	TextAttributesKey ATTRIBUTE_NAME = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE, DefaultLanguageHighlighterColors.METADATA);
	TextAttributesKey GENERIC_PARAMETER_NAME = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE,
			DefaultLanguageHighlighterColors.TYPE_ALIAS_NAME);
	TextAttributesKey DELEGATE_METHOD_NAME = TextAttributesKey.createTextAttributesKey("CSHARP_DELEGATE_METHOD_NAME",
			DefaultLanguageHighlighterColors.IDENTIFIER);
	TextAttributesKey STATIC_FIELD_OR_PROPERTY = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE,
			DefaultLanguageHighlighterColors.STATIC_FIELD);
	TextAttributesKey STATIC_EVENT = TextAttributesKey.createTextAttributesKey("CSHARP_STATIC_EVENT", STATIC_FIELD_OR_PROPERTY);
	TextAttributesKey INSTANCE_FIELD_OR_PROPERTY = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE,
			DefaultLanguageHighlighterColors.INSTANCE_FIELD);
	TextAttributesKey INSTANCE_EVENT = TextAttributesKey.createTextAttributesKey("CSHARP_INSTANCE_EVENT", INSTANCE_FIELD_OR_PROPERTY);
	TextAttributesKey STATIC_METHOD = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE,
			DefaultLanguageHighlighterColors.STATIC_METHOD);
	TextAttributesKey EXTENSION_METHOD = TextAttributesKey.createTextAttributesKey("CSHARP_EXTENSION_METHOD",
			DefaultLanguageHighlighterColors.LABEL);
	TextAttributesKey METHOD_REF = TextAttributesKey.createTextAttributesKey("CSHARP_METHOD_REF", DefaultLanguageHighlighterColors.IDENTIFIER);
	TextAttributesKey IMPLICIT_OR_EXPLICIT_CAST = TextAttributesKey.createTextAttributesKey("CSHARP_IMPLICIT_OR_EXPLICIT_CAST",
			EditorColors.INJECTED_LANGUAGE_FRAGMENT);
	TextAttributesKey INSTANCE_METHOD = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE,
			DefaultLanguageHighlighterColors.INSTANCE_METHOD);
	TextAttributesKey PARAMETER = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE, DefaultLanguageHighlighterColors.PARAMETER);
	TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE, DefaultLanguageHighlighterColors.KEYWORD);
	TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE, DefaultLanguageHighlighterColors.NUMBER);
	TextAttributesKey SOFT_KEYWORD = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE, KEYWORD);
	TextAttributesKey BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE,
			DefaultLanguageHighlighterColors.BLOCK_COMMENT);
	TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE,
			DefaultLanguageHighlighterColors.LINE_COMMENT);
	TextAttributesKey DISABLED_BLOCK = TextAttributesKey.createTextAttributesKey("CSHARP_DISABLED_BLOCK");
}
