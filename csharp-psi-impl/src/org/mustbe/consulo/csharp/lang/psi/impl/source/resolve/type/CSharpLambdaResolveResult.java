package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpDelegateMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpSimpleLikeMethod;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeResolveResult;

/**
 * @author VISTALL
 * @since 24.10.14
 */
public interface CSharpLambdaResolveResult extends DotNetTypeResolveResult, CSharpSimpleLikeMethod
{
	boolean isInheritParameters();

	@NotNull
	DotNetTypeRef[] getParameterTypeRefs();

	@Nullable
	CSharpDelegateMethodDeclaration getTarget();
}
