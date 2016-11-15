package codemagic.generator.context.subject.display;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.shared.parser.JDomWrapper;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.context.types.TabdIndexHolder;
import codemagic.generator.entrypoint.IContext;
import grain.util.ClassLoadUtil;

/**
 * 
 *
 */
public class DisplayViewXmlGen {

	private final SharedContext commonContext;
	private final DisplayContext displayContext;
	
	private static final ImmutableMap<FieldProperty.ComponentType, String> TEMPLATE_NAME = ImmutableMap.of(
		FieldProperty.ComponentType.TEXTBOX, "templates/display/formGroup_textbox_snippet.xml",
		FieldProperty.ComponentType.CHOSEN, "templates/display/formGroup_textbox_snippet.xml",
		FieldProperty.ComponentType.CHECKBOX, "templates/display/formGroup_checkbox_snippet.xml"
	);
	
	private static final ImmutableMap<FieldProperty.ComponentType, String> TEMPLATE_NAME_WITH_HELP = ImmutableMap.of(
			FieldProperty.ComponentType.TEXTBOX, "templates/display/formGroup_textboxWithHelp_snippet.xml",
			FieldProperty.ComponentType.CHOSEN, "templates/display/formGroup_textboxWithHelp_snippet.xml",
			FieldProperty.ComponentType.CHECKBOX, "templates/display/formGroup_checkboxWithHelp_snippet.xml"
		); 
	
	private static final Namespace NAMESPACE_b = Namespace.getNamespace("b", "urn:import:org.gwtbootstrap3.client.ui");
	private static final Namespace NAMESPACE_ui = Namespace.getNamespace("ui", "urn:ui:com.google.gwt.uibinder");

	private final String fullName;
	
	public DisplayViewXmlGen(final IContext ctx) {
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getSharedContext().isPresent(), "Common context cannot be null" );
		
		Preconditions.checkArgument( ctx.getDisplayContext().isPresent(), "Display context cannot be null" );
		
		this.commonContext = ctx.getSharedContext().get();
		
		this.displayContext = ctx.getDisplayContext().get();
		
		this.fullName = displayContext.buildArtifactFullFileName( ArtefactyType.VIEW_XML );

	}

	public void generate() throws JDOMException, IOException {

		// The step 1 (input) use the tabIndex values in advance, but 2 (processing) needs 
		// the initial tabIndex for this display
		final int beginTabIndex = TabdIndexHolder.get(); 
		
		// 1. input
		final Engine engine = new Engine();
		final Document doc = buildDoc( "templates/display/_EntityName_View.ui.xml" , engine);
		Element formEl = getForm(doc);
				
		// 2. processing
		int i = beginTabIndex;
		
		final List<FieldProperty> fields = displayContext.getMainFields().isEmpty() ? commonContext.getFields() : displayContext.getMainFields();  
		
		for (final FieldProperty p : fields ) {
			addFormGroup(p, formEl, engine, ++i);
		}
		
		if (!displayContext.getMoreFields().isEmpty()) {

			formEl = addPanelGroup(doc, engine, ++i);

			for (final FieldProperty p : displayContext.getMoreFields()) {
				addFormGroup(p, formEl, engine, ++i);
			}
		}
		
		// 3. output
		writeOutputToFile(doc);
	}


	private Element addPanelGroup(final Document doc, final Engine engine, final int tabIndex) {
		final JDomWrapper jdom = new JDomWrapper(doc);
		final Element columnEl = jdom.findByAttribute("id", "formContainer", NAMESPACE_b);
		final Map<String, Object> model = ImmutableMap.<String, Object>of("tabIndex", tabIndex);
		final JDomWrapper panelGroupDom = new JDomWrapper("templates/display/snippet/panelGroup_snippet.xml", engine, model, this.getClass());
		final Element panelGroupdEl = panelGroupDom.findByAttribute("id", "accordion", NAMESPACE_b);
		final Element formEl = panelGroupDom.findByAttribute("field", "moreFieldsForm", NAMESPACE_ui); 
		
		columnEl.addContent(panelGroupdEl.detach());
		
		return formEl;
	}

	private void addFormGroup(final FieldProperty p, final Element formEl, final Engine engine, final int i) throws JDOMException, IOException,	FileNotFoundException {
		
		// 1. template
		final String templateName = getTemplateName(p);
		final String formGroutTextBoxSnippet = ClassLoadUtil.loadFile(DisplayViewXmlGen.class,	templateName);
		
		// 2. inject field properties
		displayContext.getModel().put("FieldName", p.getCapitalizedFieldName());
		displayContext.getModel().put("fieldName", p.getFieldName());
		displayContext.getModel().put("title", p.getTitle());
		displayContext.getModel().put("required", p.isRequired());
		displayContext.getModel().put("allowBlank", !p.isRequired());
		displayContext.getModel().put("componentName", p.getComponentName());
		displayContext.getModel().put("tabIndex", i);
		displayContext.getModel().put("readOnly", p.isReadOnly());
		displayContext.getModel().put("maxLength", p.getMaxLength());
		displayContext.getModel().put("enabled", p.isEnabled());
		displayContext.getModel().put("helpContent", p.getHelpText());
		
		final String transformedFormGroup = engine.transform(formGroutTextBoxSnippet, displayContext.getModel());
		
		// 3. build form group
		final SAXBuilder sax = new SAXBuilder();
		
		// snippet
		sax.setXMLReaderFactory(XMLReaders.NONVALIDATING);
		final Document formGroupDoc = sax.build(new StringReader(transformedFormGroup));
		final Element formGroupRoot = formGroupDoc.getRootElement();
		final Element formGroupEl = formGroupRoot.getChildren().get(0);
		
		// 4. Add to form
		formEl.addContent(formGroupEl.detach());
	}

	private String getTemplateName(final FieldProperty p) {
		if (p.getHelpText().isEmpty()) {
			return TEMPLATE_NAME.get( p.getComponentType() );
		} else {
			return TEMPLATE_NAME_WITH_HELP.get( p.getComponentType() );
		}
	}

	  //~~~~~~~~~~
	 // Utilities
	//~~~~~~~~~~
	private void writeOutputToFile(final Document doc)	throws FileNotFoundException, IOException {
		System.out.println( new File(fullName).getAbsolutePath() );
		
		final OutputStream os = new FileOutputStream(fullName);
		final XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		xout.output(doc, os);
	}

	private Element getForm(final Document doc) {
		final Element root = doc.getRootElement();
		final IteratorIterable<Element> formIt = root.getDescendants(new ElementFilter("Form", NAMESPACE_b));
	    final Element formEl = formIt.next();
	    Preconditions.checkArgument( formEl != null, "Form should be present in the template");
	    
	    return formEl;
	}
	
	private Document buildDoc(final String xml, final Engine engine) throws JDOMException, IOException {
		final String viewxml = ClassLoadUtil.loadFile(DisplayViewXmlGen.class,	xml);

		// The buttuns are the last components in the display. Therefore, they tabIndexes
		// are shift by the number of fields in this display
		TabdIndexHolder.addAndGet( commonContext.getFields().size() + 2);
		
		// Set the tabindex of buttons
		displayContext.getModel().put("tabIndexApply", TabdIndexHolder.next() );
		displayContext.getModel().put("tabIndexSave", TabdIndexHolder.next() );
		displayContext.getModel().put("tabIndexCancel", TabdIndexHolder.next() );
		
		final String transformedXml = engine.transform(viewxml, displayContext.getModel());
		final SAXBuilder sax = new SAXBuilder();
		final Document doc = sax.build(new StringReader(transformedXml));
		return doc;
	}


}
