package codemagic.generator.context.subject.nav;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import codemagic.generator.context.shared.component.AbstractWidget;
import codemagic.generator.context.shared.component.Breadcrumb;
import codemagic.generator.context.shared.component.Breadcrumb.Item;
import codemagic.generator.context.shared.parser.JDomWrapper;
import codemagic.generator.context.shared.component.Button;
import codemagic.generator.context.shared.component.ButtonGroup;
import codemagic.generator.context.shared.util.Name.EmptyName;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.context.types.TabdIndexHolder;
import codemagic.generator.entrypoint.IContext;
import grain.util.ClassLoadUtil;

public class NavViewXmlGen {

	private static final Logger LOGGER = LoggerFactory.getLogger(NavViewXmlGen.class); 
	
	private final NavContext navContext;
	
	private static final Namespace NAMESPACE_b = Namespace.getNamespace("b", "urn:import:org.gwtbootstrap3.client.ui");

	private final String fullName;
	
	public NavViewXmlGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getNavContext().isPresent(), "Nav context cannot be null. You called the benginNav() ?" );
		
		this.navContext = ctx.getNavContext().get();
		
		this.fullName = this.navContext.buildArtifactFullFileName( ArtefactyType.VIEW_XML );

	}

	// TODO: generalize to allow more types of widgets. Some changes needed:
	//  1) The template "NavView" assumes a ButtonGroup. This should be removed from root template.
	//  2) Add the NavContext.addGroup() to allow groups buttons
	public void generate() throws JDOMException, IOException {

		// 1. input
		final Engine engine = new Engine();
		final JDomWrapper baseObjectDom = new JDomWrapper("templates/nav/_BaseObjectName_NavView.ui.xml.template" , engine, navContext.getModel(), this.getClass());
		
		// The element where the buttons will be added
		Optional<Element> buttonContainerEl = Optional.absent();
		
		// The element where the breadcrumb will be added
		Optional<Element> breadContainerEl = Optional.absent();
				
		// 2. processing
		for (final AbstractWidget<NavContext> widget : navContext.getWidgetsAsIs()) {
			if (widget instanceof ButtonGroup) {
				
				addButtonGroupToNav(engine, baseObjectDom, buttonContainerEl, (ButtonGroup<NavContext>) widget);
				
			} else if ( widget instanceof Breadcrumb ) {
				addBreadcrumbToNav(engine, baseObjectDom, breadContainerEl, (Breadcrumb<NavContext>) widget);
			}
		}
		
		// 3. output
		baseObjectDom.writeToFile(fullName);
	}

	private void addBreadcrumbToNav(final Engine engine, final JDomWrapper baseObjectDom,
			Optional<Element> breadContainerEl, final Breadcrumb<NavContext> widget) {
		
		if ( !breadContainerEl.isPresent() ) { breadContainerEl = Optional.of(initiBreadContainer(baseObjectDom, engine, widget));  }
		
		addBreadcrumbItensToNav(engine, baseObjectDom, breadContainerEl.get(), widget);
	}

	private Element initiBreadContainer(final JDomWrapper baseObjectDom, final Engine engine, final Breadcrumb<NavContext> breadcrumb) {
		// BreadcrumContainer
		final Map<String, Object> model = Maps.newHashMap(); 
		addBreadcrumbGridSize(breadcrumb, model);
		addBreadcrumbGridOffset(breadcrumb, model);
		addBreadcrumbUifieldAttr(breadcrumb, model);
		
		final JDomWrapper dom = new JDomWrapper("templates/component/breadcrumb/breadcrumb_snippet.template", engine, model, this.getClass());
		final Element toBeIncluidedEl = dom.getFirstElement("Column", NAMESPACE_b);
		
		// Add to BreadcrumContainer to doc
		final Element containerEl = baseObjectDom.getFirstElement("Row", NAMESPACE_b);
		containerEl.addContent(toBeIncluidedEl.detach());
		return baseObjectDom.getFirstElement("Breadcrumbs", NAMESPACE_b);
	}


	private void addBreadcrumbUifieldAttr(final Breadcrumb<NavContext> breadcrumb, final Map<String, Object> model) {
		if ( !(breadcrumb.getName() instanceof EmptyName)) {
			model.put ("uifieldAttr",  String.format("ui:field=\"%s\"", breadcrumb.getName().getUncapitalizedName())) ;
		}               
	}

	private void addBreadcrumbGridSize(final Breadcrumb<NavContext> breadcrumb, final Map<String, Object> model) {
		model.put ("size", breadcrumb.getGridSystemSize());
	}

	private void addBreadcrumbItensToNav(final Engine engine, final JDomWrapper baseObjectDom, final Element breadcrumbEl,
			final Breadcrumb<NavContext> widget) {
		
		final List<Item> items = widget.getItems();
		for (final Item item : items) {
			
			final Map<String, Object> model = Maps.newHashMap(); 
			model.put("text", item.getEscapedText());
			model.put("targetHistoryToken", item.getTargetHistoryTokenLiiteral());
			
			// Default => Use an AnchorListItem
			String templateName = "/templates/component/breadcrumb/breadcrumb_AnchorListItem_snippet.template";
			String itemTag = "AnchorListItem"; 
			
			// Use a ListItem, because has no link
			if (item.getTargetHistoryTokenLiiteral().isEmpty()) {
				templateName = "/templates/component/breadcrumb/breadcrumb_ListItem_snippet.template";
				itemTag = "ListItem";
			}
			
			addItemToBreadcrumb(engine, breadcrumbEl, model, templateName, itemTag);
		}
		
	}

	private void addItemToBreadcrumb(final Engine engine, final Element breadcrumbEl, final Map<String, Object> model,
			String templateName, String itemTag) {
		final JDomWrapper itemDom = new JDomWrapper(templateName, engine, model, this.getClass());
		final Element itemEl = itemDom.getFirstElement(itemTag, NAMESPACE_b);
		breadcrumbEl.addContent(itemEl.detach());
	}

	private void addButtonGroupToNav(
			final Engine engine, final JDomWrapper baseObjectDom,
			Optional<Element> buttonContainerEl, final ButtonGroup<NavContext> buttonGroup)
					throws JDOMException, IOException, FileNotFoundException {
		
		if ( !buttonContainerEl.isPresent() ) { buttonContainerEl = Optional.of(initiButtonGroupContainer(baseObjectDom, engine, buttonGroup));  }
		
		addButtonGroupItemsToNav( buttonGroup, buttonContainerEl.get(), engine);
	}

	private Element initiButtonGroupContainer(final JDomWrapper baseObjectDom, final Engine engine, ButtonGroup<NavContext> buttonGroup) {
		// ButtonGroup
		final Map<String, Object> model = Maps.newHashMap();
		addButtonGroupGridSize(buttonGroup, model);
		addButtonGroupGridOffset(buttonGroup, model);
		addButtonGroupUifieldAttr(buttonGroup, model);
		
		final JDomWrapper dom = new JDomWrapper("templates/component/buttonGroup/_ButtonGroup_View.ui.xml.template", engine, model, this.getClass());
		final Element toBeIncluidedEl = dom.getFirstElement("Column", NAMESPACE_b);
		
		// Add Button group
		final Element containerEl = baseObjectDom.getFirstElement("Row", NAMESPACE_b);
		containerEl.addContent(toBeIncluidedEl.detach());
		return baseObjectDom.getFirstElement("ButtonGroup", NAMESPACE_b);
	}

	private void addButtonGroupUifieldAttr(final ButtonGroup<NavContext> buttonGroup, final Map<String, Object> model) {
		if ( !(buttonGroup.getName() instanceof EmptyName)) {
			model.put ("uifieldAttr", String.format("ui:field=\"%s\"", buttonGroup.getName().getUncapitalizedName()) );
		}
		
	}

	private void addButtonGroupGridOffset(ButtonGroup<NavContext> buttonGroup, final Map<String, Object> model) {
		if (!buttonGroup.getGridSystemOffset().isEmpty()) {
			model.put("offsetAttr", String.format("offset=\"%s\"", buttonGroup.getGridSystemOffset())  );
		}
	}
	
	private void addBreadcrumbGridOffset(Breadcrumb<NavContext> breadcrumb, Map<String, Object> model) {
		if (!breadcrumb.getGridSystemOffset().isEmpty()) {
			model.put("offsetAttr", String.format("offset=\"%s\"", breadcrumb.getGridSystemOffset())  );
		} 
	}


	private void addButtonGroupGridSize(ButtonGroup<NavContext> buttonGroup, final Map<String, Object> model) {
		model.put("size", buttonGroup.getGridSystemSize());
	}

	private void addButtonGroupItemsToNav(final ButtonGroup<NavContext> widget, final Element formEl, final Engine engine) throws JDOMException, IOException,	FileNotFoundException {
		
		// 1. template
		final String buttonSnippet = ClassLoadUtil.loadFile(NavViewXmlGen.class, "templates/nav/button_snippet.template");
				
		for (final Button<ButtonGroup<NavContext>> button : widget.getButtons()) {
			
			// 2. inject field properties
			navContext.getModel().put("enable", button.isEnable());
			navContext.getModel().put("tabIndex", computeTabIndex(button));
			navContext.getModel().put("name", button.getName().getUncapitalizedName());
			navContext.getModel().put("text", button.getText());
			navContext.getModel().put("typeAtt", buildTypeAttr(button));
			navContext.getModel().put("iconAttr", buildIconAttr(button));
			
			final String transformedFormGroup = engine.transform(buttonSnippet, navContext.getModel());
			
			// 3. build form group
			final SAXBuilder sax = new SAXBuilder();
			
			// snippet
			sax.setXMLReaderFactory(XMLReaders.NONVALIDATING);
			final Document buttonDoc = sax.build(new StringReader(transformedFormGroup));
			final Element buttonRoot = buttonDoc.getRootElement();
			final Element buttonEl = buttonRoot.getChildren().get(0);
			
			// 4. Add to form
			formEl.addContent(buttonEl.detach());
		}
	}

	private String buildIconAttr(Button<ButtonGroup<NavContext>> button) {
		if (button.getIcon().isEmpty()) {
			return "";
		} else {
			return "icon=\"" + button.getIcon() + "\"";
			
		}
	}

	private String buildTypeAttr(final Button<ButtonGroup<NavContext>> button) {
		if ( button.getType().isPresent() )  {
			return "type=\"" + button.getType().get().getGwtBootstrap3Type() + "\"";
		} else {
			return "";
		}
	}

	private Object computeTabIndex(final Button<ButtonGroup<NavContext>> button) {
		  
		  if (button.getTabIndex() == Button.EMPTY_TABINDEX) {//The user did not set the tabindex
			  return TabdIndexHolder.next();
			  
		  } else if (button.getTabIndex() < 0 ) {// The user stop tabIndex for this button
			  return button.getTabIndex();
			  
		  } else if (button.getTabIndex() > TabdIndexHolder.get()) {//			  
			  adjustTheTabIndexHolder(button);
			  return button.getTabIndex();
			  
		  } else { // The tabIndex setted is invalid
			  
			  LOGGER.warn("The tabIndex specified {} for button {} is less than the current tabIndex {}. This value is ignored.", new Object[] { button.getTabIndex(), button.getText(), TabdIndexHolder.get()}  );
			  
			  return TabdIndexHolder.next();
		  }
		  
	}


	// This adjustment can be needed if the user set a value for 
	// tabIndex greater than the current value held by TabIndexHolder
	private void adjustTheTabIndexHolder(final Button<ButtonGroup<NavContext>> button) {
		TabdIndexHolder.set( button.getTabIndex() );  
	}
}
