package org.mustbe.consulo.csharp.ide.reflactoring.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.lexer.CSharpLexer;
import org.mustbe.consulo.csharp.lang.psi.CSharpReferenceExpression;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpMethodCallExpressionImpl;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author Fedor.Korotkov
 *
 * from google-dart
 */
public class CSharpNameSuggesterUtil
{
	private CSharpNameSuggesterUtil()
	{
	}

	private static String deleteNonLetterFromString(@NotNull final String string)
	{
		Pattern pattern = Pattern.compile("[^a-zA-Z_]+");
		Matcher matcher = pattern.matcher(string);
		return matcher.replaceAll("_");
	}

	public static Collection<String> getSuggestedNames(final DotNetExpression expression)
	{
		return getSuggestedNames(expression, null);
	}

	public static Collection<String> getSuggestedNames(final DotNetExpression expression, @Nullable Collection<String> additionalUsedNames)
	{
		Collection<String> candidates = new LinkedHashSet<String>();

		String text = expression.getText();
		if(expression instanceof CSharpReferenceExpression)
		{
			PsiElement resolvedElement = ((CSharpReferenceExpression) expression).resolve();
			String name = null;
			if(resolvedElement instanceof PsiNamedElement)
			{
				name = ((PsiNamedElement) resolvedElement).getName();
			}

			if(name != null && !name.equals(StringUtil.decapitalize(name)))
			{
				candidates.add(StringUtil.decapitalize(name));
			}
		}

		if(expression instanceof CSharpMethodCallExpressionImpl)
		{
			final PsiElement callee = ((CSharpMethodCallExpressionImpl) expression).getCallExpression();
			text = callee.getText();
		}

		if(text != null)
		{
			candidates.addAll(generateNames(text));
		}

		final Set<String> usedNames = CSharpRefactoringUtil.collectUsedNames(expression);
		if(additionalUsedNames != null && !additionalUsedNames.isEmpty())
		{
			usedNames.addAll(additionalUsedNames);
		}
		final Collection<String> result = new ArrayList<String>();

		for(String candidate : candidates)
		{
			int index = 0;
			String suffix = "";
			while(usedNames.contains(candidate + suffix))
			{
				suffix = Integer.toString(++index);
			}
			result.add(candidate + suffix);
		}

		if(result.isEmpty())
		{
			result.add("o"); // never empty
		}

		for(Iterator<String> iterator = result.iterator(); iterator.hasNext(); )
		{
			String next = iterator.next();

			if(isKeyword(next))
			{
				iterator.remove();
			}
		}
		return result;
	}

	public static boolean isKeyword(String str)
	{
		try
		{
			CSharpLexer cSharpLexer = new CSharpLexer();
			cSharpLexer.start(str);
			return CSharpTokenSets.KEYWORDS.contains(cSharpLexer.getTokenType());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@NotNull
	public static Collection<String> generateNames(@NotNull String name)
	{
		name = StringUtil.decapitalize(deleteNonLetterFromString(StringUtil.unquoteString(name.replace('.', '_'))));
		if(name.startsWith("get"))
		{
			name = name.substring(3);
		}
		else if(name.startsWith("is"))
		{
			name = name.substring(2);
		}
		while(name.startsWith("_"))
		{
			name = name.substring(1);
		}
		while(name.endsWith("_"))
		{
			name = name.substring(0, name.length() - 1);
		}
		final int length = name.length();
		final Collection<String> possibleNames = new LinkedHashSet<String>();
		for(int i = 0; i < length; i++)
		{
			if(Character.isLetter(name.charAt(i)) && (i == 0 || name.charAt(i - 1) == '_' || (Character.isLowerCase(name.charAt(i - 1)) && Character
					.isUpperCase(name.charAt(i)))))
			{
				final String candidate = StringUtil.decapitalize(name.substring(i));
				if(candidate.length() < 25)
				{
					possibleNames.add(candidate);
				}
			}
		}
		// prefer shorter names
		ArrayList<String> reversed = new ArrayList<String>(possibleNames);
		Collections.reverse(reversed);
		return ContainerUtil.map(reversed, new Function<String, String>()
		{
			@Override
			public String fun(String name)
			{
				if(name.indexOf('_') == -1)
				{
					return name;
				}
				name = StringUtil.capitalizeWords(name, "_", true, true);
				return StringUtil.decapitalize(name.replaceAll("_", ""));
			}
		});
	}
}
