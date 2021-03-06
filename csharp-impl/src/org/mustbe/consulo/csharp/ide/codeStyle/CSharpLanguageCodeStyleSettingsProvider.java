package org.mustbe.consulo.csharp.ide.codeStyle;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.CSharpLanguage;
import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;

/**
 * @author VISTALL
 * @since 13.09.14
 */
public class CSharpLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider
{
	@NotNull
	@Override
	public Language getLanguage()
	{
		return CSharpLanguage.INSTANCE;
	}

	@Override
	public IndentOptionsEditor getIndentOptionsEditor()
	{
		return new SmartIndentOptionsEditor();
	}

	@Override
	public CommonCodeStyleSettings getDefaultCommonSettings()
	{
		CommonCodeStyleSettings settings = new CommonCodeStyleSettings(CSharpLanguage.INSTANCE);
		settings.initIndentOptions();
		return settings;
	}

	@Override
	public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType)
	{
		if(settingsType == SettingsType.SPACING_SETTINGS)
		{
			consumer.showStandardOptions("SPACE_BEFORE_METHOD_CALL_PARENTHESES", "SPACE_BEFORE_METHOD_PARENTHESES", "SPACE_BEFORE_IF_PARENTHESES",
					"SPACE_BEFORE_WHILE_PARENTHESES", "SPACE_BEFORE_FOR_PARENTHESES", "SPACE_BEFORE_CATCH_PARENTHESES",
					"SPACE_BEFORE_SWITCH_PARENTHESES", "SPACE_AROUND_ASSIGNMENT_OPERATORS", "SPACE_AROUND_LOGICAL_OPERATORS",
					"SPACE_AROUND_EQUALITY_OPERATORS", "SPACE_AROUND_RELATIONAL_OPERATORS", "SPACE_AROUND_ADDITIVE_OPERATORS",
					"SPACE_AROUND_MULTIPLICATIVE_OPERATORS", "SPACE_AROUND_SHIFT_OPERATORS", "SPACE_BEFORE_CLASS_LBRACE",
					"SPACE_BEFORE_METHOD_LBRACE", "SPACE_BEFORE_IF_LBRACE", "SPACE_BEFORE_ELSE_LBRACE", "SPACE_BEFORE_WHILE_LBRACE",
					"SPACE_BEFORE_FOR_LBRACE", "SPACE_BEFORE_SWITCH_LBRACE", "SPACE_BEFORE_TRY_LBRACE", "SPACE_BEFORE_CATCH_LBRACE",
					"SPACE_BEFORE_WHILE_KEYWORD", "SPACE_BEFORE_ELSE_KEYWORD", "SPACE_BEFORE_CATCH_KEYWORD", "SPACE_WITHIN_METHOD_CALL_PARENTHESES",
					"SPACE_WITHIN_METHOD_PARENTHESES", "SPACE_WITHIN_IF_PARENTHESES", "SPACE_WITHIN_WHILE_PARENTHESES",
					"SPACE_WITHIN_FOR_PARENTHESES", "SPACE_WITHIN_CATCH_PARENTHESES", "SPACE_WITHIN_SWITCH_PARENTHESES", "SPACE_BEFORE_QUEST",
					"SPACE_AFTER_QUEST", "SPACE_BEFORE_COLON", "SPACE_AFTER_COLON", "SPACE_AFTER_COMMA", "SPACE_AFTER_COMMA_IN_TYPE_ARGUMENTS",
					"SPACE_BEFORE_COMMA", "SPACE_AROUND_UNARY_OPERATOR", "SPACE_WITHIN_BRACKETS", "SPACE_BEFORE_METHOD_PARENTHESES",
					"SPACE_AROUND_LAMBDA_ARROW");

			// parentheses settings
			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_FOREACH_PARENTHESES", "'foreach' parentheses",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_PARENTHESES, CodeStyleSettingsCustomizable.OptionAnchor.BEFORE,
					"SPACE_BEFORE_FOR_PARENTHESES");

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_USING_PARENTHESES", "'using' parentheses",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_PARENTHESES);

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_LOCK_PARENTHESES", "'lock' parentheses",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_PARENTHESES);

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_FIXED_PARENTHESES", "'fixed' parentheses",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_PARENTHESES);

			// left brace settings
			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_NAMESPACE_LBRACE", "Namespace left brace",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_LEFT_BRACE, CodeStyleSettingsCustomizable.OptionAnchor.BEFORE,
					"SPACE_BEFORE_CLASS_LBRACE");

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_EVENT_LBRACE", "Event left brace",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_LEFT_BRACE, CodeStyleSettingsCustomizable.OptionAnchor.AFTER,
					"SPACE_BEFORE_METHOD_LBRACE");

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_PROPERTY_LBRACE", "Property left brace",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_LEFT_BRACE, CodeStyleSettingsCustomizable.OptionAnchor.AFTER,
					"SPACE_BEFORE_METHOD_LBRACE");

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_INDEX_METHOD_LBRACE", "Index methd left brace",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_LEFT_BRACE, CodeStyleSettingsCustomizable.OptionAnchor.AFTER,
					"SPACE_BEFORE_METHOD_LBRACE");

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_FOREACH_LBRACE", "'foreach' left brace",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_LEFT_BRACE, CodeStyleSettingsCustomizable.OptionAnchor.BEFORE,
					"SPACE_BEFORE_FOR_LBRACE");

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_UNSAFE_LBRACE", "'unsafe' left brace",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_LEFT_BRACE);

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_USING_LBRACE", "'using' left brace",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_LEFT_BRACE);

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_LOCK_LBRACE", "'lock' left brace",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_LEFT_BRACE);

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "SPACE_BEFORE_FIXED_LBRACE", "'fixed' left brace",
					CodeStyleSettingsCustomizable.SPACES_BEFORE_LEFT_BRACE);
		}
		else if(settingsType == SettingsType.BLANK_LINES_SETTINGS)
		{
			consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE");
		}
		else if(settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS)
		{
			consumer.showStandardOptions("KEEP_LINE_BREAKS", "KEEP_FIRST_COLUMN_COMMENT", "BRACE_STYLE", "CLASS_BRACE_STYLE", "METHOD_BRACE_STYLE",
					"CALL_PARAMETERS_WRAP", "CALL_PARAMETERS_LPAREN_ON_NEXT_LINE", "CALL_PARAMETERS_RPAREN_ON_NEXT_LINE", "METHOD_PARAMETERS_WRAP",
					"METHOD_PARAMETERS_LPAREN_ON_NEXT_LINE", "METHOD_PARAMETERS_RPAREN_ON_NEXT_LINE", "ELSE_ON_NEW_LINE", "WHILE_ON_NEW_LINE",
					"CATCH_ON_NEW_LINE", "ALIGN_MULTILINE_PARAMETERS", "ALIGN_MULTILINE_PARAMETERS_IN_CALLS", "ALIGN_MULTILINE_BINARY_OPERATION",
					"BINARY_OPERATION_WRAP", "BINARY_OPERATION_SIGN_ON_NEXT_LINE", "TERNARY_OPERATION_WRAP", "TERNARY_OPERATION_SIGNS_ON_NEXT_LINE",
					"PARENTHESES_EXPRESSION_LPAREN_WRAP", "PARENTHESES_EXPRESSION_RPAREN_WRAP", "ALIGN_MULTILINE_TERNARY_OPERATION",
					"ARRAY_INITIALIZER_WRAP", "ARRAY_INITIALIZER_LBRACE_ON_NEXT_LINE", "ARRAY_INITIALIZER_RBRACE_ON_NEXT_LINE");

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "KEEP_AUTO_PROPERTY_IN_ONE_LINE", "Simple property(event, " +
					"" + "index methods) in single line", CodeStyleSettingsCustomizable.WRAPPING_KEEP);

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "NAMESPACE_BRACE_STYLE", "In namespace declaration",
					CodeStyleSettingsCustomizable.WRAPPING_BRACES, CodeStyleSettingsCustomizable.OptionAnchor.BEFORE, "CLASS_BRACE_STYLE",
					CodeStyleSettingsCustomizable.BRACE_PLACEMENT_OPTIONS, CodeStyleSettingsCustomizable.BRACE_PLACEMENT_VALUES);

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "EVENT_BRACE_STYLE", "In event declaration",
					CodeStyleSettingsCustomizable.WRAPPING_BRACES, CodeStyleSettingsCustomizable.OptionAnchor.AFTER, "METHOD_BRACE_STYLE",
					CodeStyleSettingsCustomizable.BRACE_PLACEMENT_OPTIONS, CodeStyleSettingsCustomizable.BRACE_PLACEMENT_VALUES);

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "PROPERTY_BRACE_STYLE", "In property declaration",
					CodeStyleSettingsCustomizable.WRAPPING_BRACES, CodeStyleSettingsCustomizable.OptionAnchor.AFTER, "METHOD_BRACE_STYLE",
					CodeStyleSettingsCustomizable.BRACE_PLACEMENT_OPTIONS, CodeStyleSettingsCustomizable.BRACE_PLACEMENT_VALUES);

			consumer.showCustomOption(CSharpCodeStyleSettings.class, "INDEX_METHOD_BRACE_STYLE", "In index method declaration",
					CodeStyleSettingsCustomizable.WRAPPING_BRACES, CodeStyleSettingsCustomizable.OptionAnchor.AFTER, "METHOD_BRACE_STYLE",
					CodeStyleSettingsCustomizable.BRACE_PLACEMENT_OPTIONS, CodeStyleSettingsCustomizable.BRACE_PLACEMENT_VALUES);
		}
	}

	@Override
	public String getCodeSample(@NotNull SettingsType settingsType)
	{
		if(settingsType == SettingsType.SPACING_SETTINGS)
		{
			return loadPreview("spacing.txt");
		}
		if(settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS)
		{
			return loadPreview("wrapping.txt");
		}
		if(settingsType == SettingsType.INDENT_SETTINGS)
		{
			return loadPreview("indent.txt");
		}
		return loadPreview("blankLines.txt");
	}

	private static String loadPreview(String file)
	{
		try
		{
			return FileUtil.loadTextAndClose(CSharpLanguageCodeStyleSettingsProvider.class.getResourceAsStream("/codeStyle/" + file));
		}
		catch(IOException e)
		{
			throw new Error(e);
		}
	}
}
