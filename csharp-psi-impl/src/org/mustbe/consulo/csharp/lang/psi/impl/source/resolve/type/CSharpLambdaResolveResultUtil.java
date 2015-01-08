package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpDelegateMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.light.builder.CSharpLightTypeDeclarationBuilder;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.util.CSharpResolveUtil;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import com.intellij.openapi.project.Project;

/**
 * @author VISTALL
 * @since 26.10.14
 */
public class CSharpLambdaResolveResultUtil
{
	@NotNull
	public static CSharpTypeDeclaration createTypeFromDelegate(@NotNull CSharpDelegateMethodDeclaration declaration)
	{
		Project project = declaration.getProject();

		CSharpLightTypeDeclarationBuilder builder = new CSharpLightTypeDeclarationBuilder(project);
		builder.withParentQName(declaration.getPresentableParentQName());
		builder.withName(declaration.getName());
		builder.setNavigationElement(declaration);

		builder.putUserData(CSharpResolveUtil.DELEGATE_METHOD_TYPE, declaration);

		builder.addExtendType(new CSharpTypeRefByQName(DotNetTypes.System.MulticastDelegate));

		for(DotNetGenericParameter parameter : declaration.getGenericParameters())
		{
			builder.addGenericParameter(parameter);
		}
		return builder;
	}
}
