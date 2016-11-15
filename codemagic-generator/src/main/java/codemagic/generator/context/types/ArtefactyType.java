package codemagic.generator.context.types;

public enum ArtefactyType {
	VIEW_JAVA ("View", ".java"),
	
	VIEW_XML ("View", ".ui.xml"),
	
	UI_HANDLER ("UiHandlers", ".java"), 
	
	PRESENTER ("Presenter", ".java"), 
	
	MODULE ("Module", ".java"),
	
	DRIVER ("Driver", ".java"), 
	
	COMMAND ("", ".java"),
	
	STATE ("State", ".java"), 
	
	CONTEXT ("Context", ".java"), 
	
	EVENT ("Event", ".java"), 
	
	MESSAGES ("Messages", ".java")
	
	;
	
	private final String classNameSuffix;
	private final String fileSuffix;
	

	private ArtefactyType(final String classNameSuffix, final String fileSuffix) {
		this.fileSuffix = fileSuffix;
		this.classNameSuffix = classNameSuffix;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	public String getClassNameSuffix() {
		return classNameSuffix;
	}
}