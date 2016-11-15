package codemagic.generator.context.subject.command.server.rest.etc;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.shared.util.ClassNameHolder;

public class ServiceHolder<P> implements IServiceHolder<P> {
	
	private final Subcontext subcontext;
	private final P parent;
	private Optional<ClassNameHolder> serviceClassName = Optional.absent();

	public ServiceHolder(final P parent, final String subject, final SharedContext sharedContext) {
		
		Preconditions.checkArgument( sharedContext != null, "SharedContext cannot be null");
		
		Preconditions.checkArgument( parent != null, "The parent cannot be null");
		
		this.subcontext = new Subcontext(subject, sharedContext);
		this.parent = parent;
	}

	public Subcontext getSubcontext() {
		return subcontext;
	}

	/* (non-Javadoc)
	 * @see codemagic.generator.context.subject.command.aux.IserviceHolder#end()
	 */
	@Override
	public P end() {
		checkServiceClassName();
		putServiceClassNameInModel();
		subcontext.end();
		return parent;
	}

	/* (non-Javadoc)
	 * @see codemagic.generator.context.subject.command.aux.IserviceHolder#setJavaSourceFolder(java.lang.String)
	 */
	@Override
	public IServiceHolder<P> setJavaSourceFolder(final String javaSourceFolder) {
		subcontext.setJavaSourceFolder(javaSourceFolder);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see codemagic.generator.context.subject.command.aux.IserviceHolder#setFullQualifiedServiceName(java.lang.String)
	 */
	@Override
	public IServiceHolder<P> setFullQualifiedServiceName(final String fullQualifiedServiceName) {
		
		serviceClassName = Optional.of(new ClassNameHolder(fullQualifiedServiceName));
		
		subcontext.setPackageName( serviceClassName.get().getPackageName() );
		
		return this;
	}

	public String getFullArtifactFileName() {
		
		return  subcontext.buildFullFileName( serviceClassName.get().getSimpleName() , ".java") ;
	}

	
	  //~~~~~~~~~~~~//
	 // Utilities  //
	//~~~~~~~~~~~~//
	
	private void checkServiceClassName() {
		Preconditions.checkArgument( serviceClassName.isPresent() , "ServiceClassName cannot bem empty. You called the method setFullQualifiedServiceName() to specify the full class name of the generated service ?");
	}

	private void putServiceClassNameInModel() {
		subcontext.getModel().put("ServiceClassName", serviceClassName.get().getSimpleName());
	}
}
