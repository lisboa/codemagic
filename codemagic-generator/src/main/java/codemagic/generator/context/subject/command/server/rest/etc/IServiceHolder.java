package codemagic.generator.context.subject.command.server.rest.etc;

public interface IServiceHolder<P> {

	P end();

	IServiceHolder<P> setJavaSourceFolder(String javaSourceFolder);

	IServiceHolder<P> setFullQualifiedServiceName(String fullQualifiedServiceName);
}