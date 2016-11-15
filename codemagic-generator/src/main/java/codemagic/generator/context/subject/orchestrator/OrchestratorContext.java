package codemagic.generator.context.subject.orchestrator;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import com.google.common.base.Verify;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.subject.AbstractSubjectContext;
import codemagic.generator.context.subject.IsSubjectContext;
import codemagic.generator.context.subject.orchestrator.subcontext.OrchestratorSubContext;
import codemagic.generator.entrypoint.IContext;

public class OrchestratorContext extends AbstractSubjectContext implements IsSubjectContext<OrchestratorContext> {

	// Used only to generate class and artifact names for Grid toolbar driver
	// It is necessary because the drivers were created in a subfolder of the orckestrator 
	private final OrchestratorSubContext toolbarDriverCtx;
	
	private String nameToken = "";
	private String fullNameOfTargetSlot = "";
	
	public OrchestratorContext(final SharedContext commonContext) {
		super( "Orchestrator", commonContext);
		this.toolbarDriverCtx = new OrchestratorSubContext("", commonContext);
	}

	
	public OrchestratorSubContext getToolbarDriverCtx() {
		return toolbarDriverCtx;
	}

	@Override
	public OrchestratorContext setJavaSourceFolder(final String javaSourceFolder) {
		internalSetJavaSourceFolder(javaSourceFolder);
		toolbarDriverCtx.setJavaSourceFolder( javaSourceFolder );
		return this;
	}

	@Override
	public OrchestratorContext setPackageName(final String packageName) {
		internalSetPackageName(packageName);
		toolbarDriverCtx.setPackageName(packageName + ".driver");
		return this;
	}

	@Override
	public OrchestratorContext setContextId(final String id) {
		internalSetContextId(id);
		toolbarDriverCtx.setContextId(id + "Driver");
		return this;
	}

	public String getNameToken() {
		return nameToken;
	}

	public OrchestratorContext setNameToken(final String nameToken) {
		this.nameToken = sanitize(nameToken).trim();
		
		return this;
	}

	public String getFullNameOfTargetSlot() {
		return fullNameOfTargetSlot;
	}

	/**
	 * The full name (qualified name) Slot where the glue will be added.
	 * Ex.: db.assist.view.client.application.adminlayout.AdminLayoutPresenter.SLOT_AL_MainContent
	 * 
	 * Note that this field is fullNameOfPresenter + "." + slotName 
	 * 
	 * @param fullNameOfTargetSlot
	 * @return
	 */
	public OrchestratorContext setFullNameOfTargetSlot(final String fullNameOfTargetSlot) {
		this.fullNameOfTargetSlot = sanitize( fullNameOfTargetSlot ).trim();
		
		return this;
	}

	@Override
	public IContext end() {
		getModel().put("nameToken", nameToken);
		
		if ( !fullNameOfTargetSlot.isEmpty()) {
			final String[] parts = fullNameOfTargetSlot.split("\\.");
			
			Verify.verify( parts.length > 1, "The slot name must have at least two parts: <PresenterClass>.<slotName>. But instead it has %s parts: %s. Note: A correct slot name is 'db.assist.view.client.application.adminlayout.AdminLayoutPresenter.SLOT_AL_MainContent'", parts.length, fullNameOfTargetSlot );
			
			getModel().put("fullNameOfTargetPresenter",  getFullPresenterName( parts, fullNameOfTargetSlot.length()));
			
			getModel().put("targetSlotName",  getTargetSlotName( parts, fullNameOfTargetSlot.length()));
		}
		
		// Do many initialization stuffs
		toolbarDriverCtx.end();
		
		return super.end();
	}

	private String getTargetSlotName(final String[] parts, final int capacity) {
		final StringBuilder builder = new StringBuilder( capacity );
		
		final int last = parts.length - 1;
		final int beforeLast = parts.length - 2;
		
		builder.append(parts[beforeLast]).append(".").append(parts[last]);
		
		return builder.toString();
	}

	private String getFullPresenterName(final String[] parts, final int capacity) {
		
		final StringBuilder builder = new StringBuilder( capacity );
		
		builder.append(parts[0]);
		for (int i = 1; i < parts.length - 1; i++) {
			 builder.append(".").append(parts[i]);
		}
		return builder.toString();
	}

}
