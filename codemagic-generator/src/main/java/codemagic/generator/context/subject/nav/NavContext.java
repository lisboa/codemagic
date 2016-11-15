package codemagic.generator.context.subject.nav;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.shared.component.AbstractWidget;
import codemagic.generator.context.shared.component.Breadcrumb;
import codemagic.generator.context.shared.component.Button;
import codemagic.generator.context.shared.component.ButtonGroup;
import codemagic.generator.context.shared.component.Happened;
import codemagic.generator.context.shared.util.ConstantHolder;
import codemagic.generator.context.shared.util.ContextUtil;
import codemagic.generator.context.subject.AbstractSubjectContext;
import codemagic.generator.context.subject.IsSubjectContext;
import codemagic.generator.context.subject.common.help.HelpContext;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

/**
 * <pre>
 *   Notes: 
 *   
 *   1) The Nav can have only one Breacrumb;
 *   2) Any number of ButtonGroup .. but for now, only one group are supported
 * </pre> 
 *
 */
public class NavContext extends AbstractSubjectContext implements IsSubjectContext<NavContext>{

	private static final Logger LOGGER = LoggerFactory.getLogger( NavContext.class ); 
	
	public static final String GOTO_COMMAND = "PlaceUtil.gotoPage(%s, placeManager);";
	public static final String PLACE_UTIL_IMPORT = "db.assist.view.common.common.util.PlaceUtil";
	public static final String PLACE_MANAGER_DECL = "@Inject private PlaceManager placeManager";
	public static final String PLACE_MANAGER_IMPORT = "com.gwtplatform.mvp.client.proxy.PlaceManager";
	
	private final List<AbstractWidget<NavContext>> widgetsAsIs = Lists.newArrayList();
	private final String handlerMehodName;

	private Optional<HelpContext> helpContext = Optional.absent();
	
	// Projections
	private final List<AbstractWidget<?>> widgets = Lists.newArrayList();
	private final List<Happened<?>> clickables = Lists.newArrayList();
	private final Set<String> widgetFullQualifiedClassName = Sets.newHashSet();
	
	private Optional<Breadcrumb<NavContext>> breadcrumb = Optional.absent();
	
	public NavContext(final SharedContext sharedContext) {
		super("Nav", sharedContext);
		this.handlerMehodName = buildHandleMethodName(sharedContext);
	}

	@Override
	public IContext end() {
		populeClickablesProjection();
		addWidgetsAsIsToWidgetsProjection();
		addWidgetsInModel();
		addClickablesInModel(clickables);
		addHandleMethodNameInModel();
		addWidgetFullQualifiedClassNameInModel();
		

		return super.end();
	}

	public Breadcrumb<NavContext> beginBreadcrumb() {
		
		if (this.breadcrumb.isPresent()) {
			throw new RuntimeException("Thare is already a breadcrumb defined for this navbar");
		}
		
		this.breadcrumb = Optional.of(new Breadcrumb<NavContext>(this));
		
		widgetsAsIs.add(this.breadcrumb.get());
		
		return this.breadcrumb.get();
	}
	
	public ButtonGroup<NavContext> beginButtonGroup() {
		final ButtonGroup<NavContext> buttonGroup = new ButtonGroup<NavContext>(this);
		 
		widgetsAsIs.add(buttonGroup);
		
		return buttonGroup;
	}
	
	/**
	 * The full qualified class name of the widgets defined in this nav.
	 * 
	 * This can be used, for example, to build the import declarations for this
	 * widgets.
	 * 
	 * @return
	 */
	public Set<String> getWidgetFullQualifiedClassName() {
		return widgetFullQualifiedClassName;
	}

	/**
	 * A list of widgets that is clickable. This is a sublist of
	 * {@link #widgetsAsIs}, with some transformations. For example, the
	 * {@link ButtonGroup} items where put directly to this list.
	 * 
	 * @return A List of Happened (events). Each happened has the clicked widget
	 *         and the name of the event fired by this widget. This list is
	 *         used, for example, to build the WhatsHappen enumaration of nav
	 *         events. See the example bellow (BlacklistNavEvent):
	 * 
	 *         <code>
	 *          public static enum WhatsHappen {
	 *	            BTN_BACK_CLICKED,
	 *              BTN_SAVE_CLICKED
	 *          }
	 *         </code>
	 * 
	 *         This list also is used to build the handler for this events
	 *         (BlacklistOrkestratorPresenter):
	 * 
	 *         <code>
	 *          &#64;Override
	 *			public void onBlacklistNav(final BlacklistNavEvent event) {
	 *	
	 *				switch (event.getHappening()) {
	 *				case BTN_BACK_CLICKED:
	 *					 
	 *					 break;
	 *				default:
	 *					break;
	 *				}
	 *			}
	 *         </code>
	 * 
	 */
	public List<Happened<?>> getClickables() {
		return clickables;
	}

	/**
	 * 
	 * @return The name of the EventHandler method for this nav.
	 */
	public String getHandlerMehodName() {
		return handlerMehodName;
	}


	/**
	 * The widgets as is, that is, the widgets as they are add to nav.On the
	 * other hand, {@link #widgets} and {@link #clickables} are projections,
	 * that is, contain only some widgets that shared specific properties. The
	 * projections turn more easy to generate code.
	 * 
	 * @see #clickables
	 * @see #widgets
	 * 
	 * @return
	 */
	public List<AbstractWidget<NavContext>> getWidgetsAsIs() {
		return widgetsAsIs;
	}

	@Override
	public NavContext setContextId(final String id) {
		internalSetContextId(id);
		return this;
	}

	@Override
	public NavContext setJavaSourceFolder(final String javaSourceFolder) {
		internalSetJavaSourceFolder(javaSourceFolder);
		return this;
	}

	@Override
	public NavContext setPackageName(final String packageName) {
		internalSetPackageName(packageName);
		return this;
	}
	
	public Optional<HelpContext> getHelpContext() {
		return helpContext;
	}
	
	  //~~~~~~~~~~~// 
	 // Utilities //
	//~~~~~~~~~~~//
	
	private void addWidgetsInModel() {
		getModel().put("widgets", widgets);
	}

	/**
	 * @See {@link #widgetFullQualifiedClassName}
	 */
	private void addWidgetFullQualifiedClassNameInModel() {
		getModel().put("widgetFullQualifiedClassName", widgetFullQualifiedClassName);
	}
	
	// For now, only the buttons are clickables
	private void populeClickablesProjection() {
		
		ButtonGroup<NavContext> buttonGroup;
		
		for (final AbstractWidget<NavContext> widget : widgetsAsIs) {
			
			if (widget instanceof ButtonGroup) {
				
				buttonGroup = (ButtonGroup<NavContext>) widget;
				// Add buttons to clickable
				for (final Button<ButtonGroup<NavContext>> button : buttonGroup.getButtons()) {
					if (button.isAddInJavaCode()) { 
						clickables.add( new Happened<ButtonGroup<NavContext>>(button, ContextUtil.buildHappenedName(button, "_CLICKED"), buildActionCode(button) ));
						widgets.add(button);
						widgetFullQualifiedClassName.add(button.getQualifiedJavaType());
					}
				}
			} 
		}
	}
	
	private void addWidgetsAsIsToWidgetsProjection() {
		for (final AbstractWidget<NavContext> widget : widgetsAsIs) {
			if (widget.isAddInJavaCode()) {
				widgets.add(widget);
				widgetFullQualifiedClassName.add(widget.getQualifiedJavaType());
			}
		}
	}

	private String buildActionCode(final Button<ButtonGroup<NavContext>> button) {

		if ( !button.getActionType().isPresent() ) {
			return "";
		}
		
		switch ( button.getActionType().get() ) {
		case fullNameToken:
			return new NameTokenAction(getImports(), button, getFieldDecls()).buildActionCode();
			
		case htmlHelp:
			helpContext = buildHelpContext(button);
			return new HelpAction(helpContext.get(), getImports(), getFieldDecls()).buildActionCode();
			
		default:
			LOGGER.warn("The ActionType {} is unkown. Returning an empty action", button.getActionType().get());
			return "";
		}
	}

	private Optional<HelpContext>  buildHelpContext(final Button<ButtonGroup<NavContext>> button) {
		
		Verify.verify( button.getHtmlHelp().isPresent(), "Help action was chose, but the HelpData is empty. Therefore, I cant determine the help text" );
		
		final HelpContext result =  new HelpContext(getSharedContext());
		result.setJavaSourceFolder( button.getHtmlHelp().get().getJavaSourceFolder());
		result.setPackageName(button.getHtmlHelp().get().getPackageName() + ".help");
		result.setContextId(getContextId() + "Help");
		result.getModel().put("htmlHelp", button.getHtmlHelp().get().getHtmlHelp() );
		result.end();
		return Optional.of(result);
	}

	private void addClickablesInModel(final List<Happened<?>> clickables) {
		getModel().put("clickables", clickables);
	}
	
	private void addHandleMethodNameInModel() {
		getModel().put("handlerMethodName", handlerMehodName);
		
	}
	
	/**
	 * 
	 * @param commonContext
	 * @return The method name to be used on Handler interface declaration. Example: "onBacklistNav"
	 * @see "templates/nav/_BaseObjectName_Event.java.template"
	 */
	private String buildHandleMethodName(final SharedContext commonContext) {
		return "on" + commonContext.getBaseNameForGeneratedArtifacts() + getCapitalizedSubject();
	}

	public Optional<Breadcrumb<NavContext>> getBreadcrumb() {
		return breadcrumb;
	}
	
	
	public static class HelpAction {
		private final Set<String> imports;
		private final Set<String> fieldDecls;
		private final HelpContext helpContext;
		private final String capitalizedHelpPresenter;
		
		
		public HelpAction(final HelpContext helpContext, final Set<String> imports, final Set<String> fieldDecls) {
			this.imports = imports;
			this.fieldDecls = fieldDecls;
			this.helpContext = helpContext;
			
			capitalizedHelpPresenter = helpContext.buildClassName(ArtefactyType.PRESENTER);
			
			Verify.verify(helpContext != null , "The helContext used to built the help action cannot be null");
		}

		public String buildActionCode() {
			addImports();
			addFieldDecls();
			return "helpDriver.showHelp();";
		}

		private void addFieldDecls() {
			// fieldDecls.add(String.format("private final %s helpPresenter", capitalizedHelpPresenter ));
			fieldDecls.add(String.format("private final HelpDirver helpDriver", capitalizedHelpPresenter ));
		}

		private void addImports() {
			imports.add(helpContext.buildFullClassName(ArtefactyType.PRESENTER));
			imports.add("db.assist.view.client.application.applayout.driver.HelpDirver");
		}
	}
	
	/**
	 * Manager the Name Token action. That is:
	 * <pre>
	 * 	1. Build the code that should be executed when the action is a name token
	 *  2. Add the NameToken imports 
	 * </pre> 
	 *
	 */
	public static class NameTokenAction {
		
		private final Set<String> imports;
		private final Button<ButtonGroup<NavContext>> button;
		private final Set<String> fieldDecls;
		
		
		public NameTokenAction(final Set<String> imports, final Button<ButtonGroup<NavContext>> button, final Set<String> decls) {
			this.imports = imports;
			this.button = button;
			this.fieldDecls = decls;
		}

		public String buildActionCode() {

			Preconditions.checkArgument( !button.getFullNameToken().isEmpty(), "%s should be called only when the NameToken is not null", NameTokenAction.class.getName() );
			
			addImports();
			addFieldDecls();

			if (isLiteral()) {
				return buildActionFromLiteral();

			} else {
				return buildActionFromConstant();
			}
		}
		
		// All goto action requires theses imports:
		// Remember: "PlaceUtil.gotoPage(%s, placeManager);";  
		private void addImports() {
			imports.add(PLACE_UTIL_IMPORT);
			imports.add(PLACE_MANAGER_IMPORT);
		}

		private void addFieldDecls() {
			fieldDecls.add(PLACE_MANAGER_DECL);
		}
		
		// When built from constant, it is necessary to add the NameTokens class in
		// the import declaration, to prevents the nasty full qualified constant
		// name in the code, that is: "x.y.z.NameToken.myToken". 
		// Because this, the constant name is <ClassName>.<constantName>
		// Example: "NameTokens.myToken",
		private String buildActionFromConstant() {
			
			// x.y.z.NameToken.myToken
			final ConstantHolder constant = new ConstantHolder( button.getFullNameToken() );
			
			// x.y.z.NameToken
			imports.add( constant.getFullQualifiedClassName() );	
			
			// NameToken.myToken
			return String.format( GOTO_COMMAND , constant.getQualifiedConstantName());
		}

		private String buildActionFromLiteral() {
			return String.format( GOTO_COMMAND , button.getFullNameToken());
		}

		// The NameToken can be a literal (starts with quote) or a constant (otherwise)
		private boolean isLiteral() {
			return button.getFullNameToken().startsWith("\"");
		}
		
	}
}
