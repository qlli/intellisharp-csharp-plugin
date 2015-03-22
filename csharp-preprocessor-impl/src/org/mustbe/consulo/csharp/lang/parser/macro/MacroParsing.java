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

package org.mustbe.consulo.csharp.lang.parser.macro;

import org.mustbe.consulo.csharp.lang.psi.CSharpMacroElements;
import org.mustbe.consulo.csharp.lang.psi.CSharpMacroTokens;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderUtil;
import com.intellij.psi.tree.TokenSet;
import lombok.val;

/**
 * @author VISTALL
 * @since 18.12.13.
 */
public class MacroParsing implements CSharpMacroTokens, CSharpMacroElements
{
	private static final TokenSet COND_STOPPERS = TokenSet.create(ENDIF_KEYWORD, ELSE_KEYWORD, ELIF_KEYWORD);

	public static void parse(PsiBuilder builder)
	{
		if(builder.getTokenType() == CSharpMacroTokens.DIRECTIVE_START)
		{
			PsiBuilder.Marker mark = builder.mark();

			builder.advanceLexer();

			val token = builder.getTokenType();

			if(token == DEFINE_KEYWORD || token == UNDEF_KEYWORD)
			{
				builder.advanceLexer();

				if(builder.getTokenType() == VALUE)
				{
					builder.advanceLexer();
				}
				else
				{
					builder.error("Identifier expected");
				}
				skipUntilStop(builder);
				mark.done(token == UNDEF_KEYWORD ? CSharpMacroElements.UNDEF_DIRECTIVE : CSharpMacroElements.DEFINE_DIRECTIVE);
			}
			else if(token == IF_KEYWORD)
			{
				PsiBuilder.Marker condBlock = builder.mark();

				PsiBuilder.Marker startMarker = builder.mark();

				builder.advanceLexer();

				PsiBuilder.Marker parse = MacroExpressionParsing.parse(builder);
				if(parse == null)
				{
					builder.error("Expression expected");
				}

				PsiBuilderUtil.expect(builder, DIRECTIVE_END);
				startMarker.done(CSharpMacroElements.MACRO_BLOCK_START);

				parseAndDoneUntilCondStoppers(builder, condBlock);

				while(!builder.eof())
				{
					if(builder.getTokenType() == ELIF_KEYWORD)
					{
						parseElIf(builder, startMarker);
					}
					else if(builder.getTokenType() == ELSE_KEYWORD)
					{
						parseElse(builder, startMarker);
					}
					else if(builder.getTokenType() == ENDIF_KEYWORD)
					{
						break;
					}
				}

				if(builder.getTokenType() == ENDIF_KEYWORD)
				{
					PsiBuilder.Marker endIfMarker = builder.mark();
					builder.advanceLexer();

					PsiBuilderUtil.expect(builder, DIRECTIVE_END);

					endIfMarker.done(CSharpMacroElements.MACRO_BLOCK_STOP);
				}
				else
				{
					builder.error("'#endif' expected");
				}

				mark.done(CSharpMacroElements.MACRO_IF);
			}
			else if(token == REGION_KEYWORD)
			{
				skipUntilStop(builder);
				mark.done(CSharpMacroElements.ENDREGION_DIRECTIVE);
			}
			else if(token == ENDREGION_KEYWORD)
			{
				skipUntilStop(builder);
				mark.done(CSharpMacroElements.ENDREGION_DIRECTIVE);
			}
			else if(token == ENDIF_KEYWORD)
			{
				builder.advanceLexer();
				builder.error("'#endif' without '#if'");

				skipUntilStop(builder);
				mark.done(CSharpMacroElements.MACRO_BLOCK_STOP);
			}
			else
			{
				builder.error("Directive name expected");
				skipUntilStop(builder);
				mark.done(CSharpMacroElements.UNKNOWN_DIRECTIVE);
			}
		}
		else
		{
			builder.advanceLexer();
		}
	}

	private static void parseElse(PsiBuilder builder, PsiBuilder.Marker parentMarker)
	{
		PsiBuilder.Marker mark = builder.mark();

		PsiBuilder.Marker headerMarker = builder.mark();

		if(parentMarker == null)
		{
			builder.error("#if block not opened");
		}

		builder.advanceLexer();
		PsiBuilderUtil.expect(builder, DIRECTIVE_END);

		headerMarker.done(CSharpMacroElements.MACRO_BLOCK_START);

		parseAndDoneUntilCondStoppers(builder, mark);
	}

	private static void parseAndDoneUntilCondStoppers(PsiBuilder builder, PsiBuilder.Marker marker)
	{
		while(!builder.eof())
		{
			if(COND_STOPPERS.contains(builder.getTokenType()))
			{
				break;
			}

			MacroParsing.parse(builder);
		}

		marker.done(CSharpMacroElements.MACRO_IF_CONDITION_BLOCK);
	}

	private static void parseElIf(PsiBuilder builder, PsiBuilder.Marker parentMarker)
	{
		PsiBuilder.Marker mark = builder.mark();

		PsiBuilder.Marker headerMarker = builder.mark();

		if(parentMarker == null)
		{
			builder.error("#if block not opened");
		}

		builder.advanceLexer();

		PsiBuilder.Marker parse = MacroExpressionParsing.parse(builder);
		if(parse == null)
		{
			builder.error("Expression expected");
		}

		PsiBuilderUtil.expect(builder, DIRECTIVE_END);

		headerMarker.done(CSharpMacroElements.MACRO_BLOCK_START);

		while(!builder.eof())
		{
			if(COND_STOPPERS.contains(builder.getTokenType()))
			{
				break;
			}

			MacroParsing.parse(builder);
		}

		mark.done(CSharpMacroElements.MACRO_IF_CONDITION_BLOCK);
	}

	private static void skipUntilStop(PsiBuilder builder)
	{
		while(!builder.eof())
		{
			if(builder.getTokenType() == DIRECTIVE_END)
			{
				builder.advanceLexer();
				break;
			}
			else
			{
				builder.advanceLexer();
			}
		}
	}
}
