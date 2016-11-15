package codemagic.generator.context.shared.component;

public enum ContextualType {
	PRIMARY ("PRIMARY"), 
	DEFAULT ("DEFAULT"), 
	SUCCESS ("SUCCESS"), 
	INFO ("INFO"), 
	WARNING ("WARNING"), 
	DANGER ("DANGER");
	
	/**
	 * The name that can be used to set the type attribute of the gwtbootstrap3
	 * widgets
	 */
	private final String gwtBootstrap3Type;

	private ContextualType(final String str) {
		this.gwtBootstrap3Type = str;
	}

	public String getGwtBootstrap3Type() {
		return gwtBootstrap3Type;
	}
}