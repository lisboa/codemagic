package codemagic.generator.context.subject;

public interface IsSubjectContext<T> {

	/**
	 * 
	 * @param id
	 *            The context id. Should be unique for each context instance,
	 *            because in some cases is used as key in a Map of contexts. It
	 *            is used to generate the class names of generated code classes.
	 *            This key allows another context to get access to specific
	 *            contexts and allows the creation of many contexts of the same
	 *            subject too. For example. It is possible to have two
	 *            {@link codemagic.generator.context.subject.command.CommandContext}
	 *            s. One named "save" and other named "get". Then the
	 *            {@link codemagic.generator.context.subject.orchestrator.OrchestratorContext}
	 *            could get access for both: the "get" and "save" command
	 *            contexts. The "get" could be used to fetch the datagrid items.
	 *            While the "save" could be used to save or edit an item.
	 * 
	 *            <b>Note</b><br />
	 *            Only the
	 *            {@link codemagic.generator.context.subject.command.CommandContext}
	 *            use this engine for now. The others contexts do nothing with
	 *            this id.
	 * @return
	 */
	T setContextId(String id);
	
	T setJavaSourceFolder(String javaSourceFolder);

	T setPackageName(String packageName);

}