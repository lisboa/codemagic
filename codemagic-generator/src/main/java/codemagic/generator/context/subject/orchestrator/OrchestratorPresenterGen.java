package codemagic.generator.context.subject.orchestrator;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import codemagic.generator.context.shared.component.Breadcrumb;
import codemagic.generator.context.shared.component.Breadcrumb.Item;
import codemagic.generator.context.shared.urlparam.UrlParamProcessor.Result;
import codemagic.generator.context.subject.command.CommandContext;
import codemagic.generator.context.subject.datagrid.withasynprovider.DataGridContext.SubContext;
import codemagic.generator.context.subject.nav.NavContext;
import codemagic.generator.context.subject.type.TypeContext;
import codemagic.generator.context.subject.type.infra.holder.ContextUrlParamList;
import codemagic.generator.context.subject.type.infra.processor.ContextParamProcessor;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;
import grain.util.ClassLoadUtil;

/**
 * Assumes that the save command has the name "save". 
 *
 */
public class OrchestratorPresenterGen {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorPresenterGen.class);
	
	private final OrchestratorContext orchContext;
	// private final DisplayContext displayContext;
	private final SubContext tableSubContext;
	private final TypeContext typeContext;
	private final ContextUrlParamList contextUrlParamList;
	private final Optional<CommandContext> saveCommandContext;
	private final CommandContext deleteCommandContext;
	private final NavContext navContext;
	private final StringBuilder summary = new StringBuilder();
	private final IContext ctx;
	
	public OrchestratorPresenterGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getOrchestrator().isPresent(), "The OrchestratorContext cannot be null. Do you called the method beginOrchestrator() ?" );
		
		this.ctx = ctx;
		
		if (! ctx.getDisplayContext().isPresent()) {
			summary.append("Display was not generated. To gnerate the display, call the method beginDisplay()");
		}
		
		Preconditions.checkArgument( ctx.getDataGridContext().isPresent(), "The DatagridContext cannot be null. Do you have called the method beginDatagrid() ?" );
		
		Preconditions.checkArgument( ctx.getTypeContext().isPresent(), "The TypeContext cannot be null. Do you have called the method beginType() ?" );
		
		Preconditions.checkArgument( ctx.getSharedContext().isPresent(), "The SharedContext cannot be null. Do you have called the method begin() ?" );
		
		// Preconditions.checkArgument( ctx.getCommandContexts().get("save") != null, "The SaveCommandContext cannot be null. Do you have called the method beginCommand().setContextId('save')  ?" );
		
		Preconditions.checkArgument( ctx.getCommandContexts().get("delete") != null, "The DeleteCommandContext cannot be null. Do you have called the method beginCommand().setContextId('delete')  ?" );
		
		Preconditions.checkArgument( ctx.getNavContext().isPresent(), "The Nav context cannot be null. Do you have called the method beginNav() ?" );
		
		this.orchContext = ctx.getOrchestrator().get();
		
		this.tableSubContext = ctx.getDataGridContext().get().getTableSubContext();
		
		this.typeContext = ctx.getTypeContext().get();
		
		this.saveCommandContext = Optional.fromNullable(ctx.getCommandContexts().get("save"));
		
		this.deleteCommandContext = ctx.getCommandContexts().get("delete");
		
		this.navContext = ctx.getNavContext().get();
		
		contextUrlParamList = new ContextUrlParamList( ctx.getSharedContext().get().getUrlParamList(), typeContext);
		
		// summaries
		if (!this.saveCommandContext.isPresent()) {
			summary.append("\nThe SaveCommand will not generated. To generate it, call the method beginCommand().setContextId('save')");
		}
	}

	public void generate() throws IOException {

		// input
		final String template = ClassLoadUtil.loadFile(OrchestratorPresenterGen.class, "templates/orchestrator/_BaseObjectName_Presenter.java.template");
		
		final Engine engine = new Engine();
		
		if (navContext.getHelpContext().isPresent()) {
			orchContext.getModel().put("HelpPresenter", navContext.getHelpContext().get().buildClassName(ArtefactyType.PRESENTER) );
		
			orchContext.getModel().put("hasHelp", true );
		}
		
		orchContext.getModel().put("fullMessagesClassName", typeContext.buildFullClassName(ArtefactyType.MESSAGES));
		
		orchContext.getModel().put("MessagesClassName", typeContext.buildClassName(ArtefactyType.MESSAGES));
		
		if ( ctx.getDisplayContext().isPresent()) {
			orchContext.getModel().put("displayNewModeTitle", ctx.getDisplayContext().get().getNewModeTitle());
			orchContext.getModel().put("displayEditingModeTitle", ctx.getDisplayContext().get().getEditingModeTitle());
			orchContext.getModel().put("fullDisplayPresenter", ctx.getDisplayContext().get().buildFullClassName(ArtefactyType.PRESENTER));
			orchContext.getModel().put("DisplayPresenter", ctx.getDisplayContext().get().buildClassName(ArtefactyType.PRESENTER));
		}
		
		
		orchContext.getModel().put("navMethodDecls", navContext.getMethodDecls());
		
		// for example: 'getBreadcrumb'
		orchContext.getModel().put("breadcrumbGetter", getBreadcrumbGetter());
		
		// Code to get the url parameters and set the breadcrumb accorging
		orchContext.getModel().put("configureBreadcrumbCode", buildConfigureBreadcrumbCode());
		
		//TODO: If the OrchestratorContext have your own import set, it should be merged with this
		orchContext.getModel().put("navImports", navContext.getImports());
		
		//TODO: If the OrchestratorContext have your own additional declarations, it should be merged with this
		orchContext.getModel().put("navDecls", navContext.getFieldDecls());
		
		orchContext.getModel().put("navClickables", navContext.getClickables());
		
		orchContext.getModel().put("fullNavEventClassName", navContext.buildFullClassName(ArtefactyType.EVENT));
		orchContext.getModel().put("NavEventClassName", navContext.buildClassName(ArtefactyType.EVENT));
		orchContext.getModel().put("navEventHandlerMethodName", navContext.getHandlerMehodName());
		
		orchContext.getModel().put("fullNavPresenterClassName", navContext.buildFullClassName(ArtefactyType.PRESENTER));
		orchContext.getModel().put("NavPresenterClassName", navContext.buildClassName(ArtefactyType.PRESENTER));
		
		orchContext.getModel().put("fullInputClassName", deleteCommandContext.getQualifiedInputClassName());
		orchContext.getModel().put("InputClassName", deleteCommandContext.getInputClassName());
		orchContext.getModel().put("fullDeleteCommandName", deleteCommandContext.getFullCommandName());
		orchContext.getModel().put("DeleteCommandName", deleteCommandContext.getCapitalizedCommandName());
		
		if (saveCommandContext.isPresent()) {
			orchContext.getModel().put("fullSaveCommandClass", saveCommandContext.get().buildFullClassName(ArtefactyType.COMMAND));
			orchContext.getModel().put("SaveCommandClass", saveCommandContext.get().buildClassName(ArtefactyType.COMMAND));
		}
		
		// Used to initialize a entity with the values read from the context
		orchContext.getModel().put("contextUrlParamList", contextUrlParamList);
		
		orchContext.getModel().put("fullContextClassName", typeContext.buildFullClassName(ArtefactyType.CONTEXT));
		orchContext.getModel().put("ContextClassName", typeContext.buildClassName(ArtefactyType.CONTEXT));
		
		orchContext.getModel().put("fullStateClassName", typeContext.buildFullClassName(ArtefactyType.STATE));
		orchContext.getModel().put("StateClassName", typeContext.buildClassName(ArtefactyType.STATE));
		
		orchContext.getModel().put("fullTablePresenter", tableSubContext.buildFullClassName(ArtefactyType.PRESENTER));
		orchContext.getModel().put("TablePresenter", tableSubContext.buildClassName(ArtefactyType.PRESENTER));
		
		final String transformedJava = engine.transform(template, orchContext.getModel());
		
		final String fullName = orchContext.buildArtifactFullFileName( ArtefactyType.PRESENTER );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}

	private String getBreadcrumbGetter() {
		if (navContext.getBreadcrumb().isPresent()) {
			return navContext.getBreadcrumb().get().getName().getGetter();
		} else {
			return "";
		}
	}

	private String buildConfigureBreadcrumbCode() {
		final Optional<Breadcrumb<NavContext>> breadcrumb = navContext.getBreadcrumb();
		
		if ( !breadcrumb.isPresent()) {
			return "";
		}
		
		if ( breadcrumb.get().getItems().isEmpty() ) {
			LOGGER.warn("The breadcrumb has none elements. You have called the method beginBreadcrumb().addItem() ?");
			return "";
		}
		
		final ContextParamProcessor processor = new ContextParamProcessor(typeContext);
		
		// Assumes 20 chars per breadcrumb item. For example: "Blacklist manager"
		final int capacity = breadcrumb.get().getItems().size() * 20;
		
		final StringBuilder builder = new StringBuilder( capacity );
		Item item;
		
		for (int i = 0; i < breadcrumb.get().getItems().size(); i++) {
			
			item =  breadcrumb.get().getItems().get(i);
					
			final Result result = processor.process( item.getText() );
			
			if (result.getExtractedVariables().isEmpty() ) {
				continue;
			}
			
			// The getBreadcrumbItem is already in template
			builder.append(
				String.format("WidgetUtil.setText(SupportUtil.getBreacrumbItem(nav.getBreadcrumb(), %d, this.getClass()), StringUtilClient.format( \"%s\", %s )); ", i, result.getInterpolatedUrl(), result.getMethodArguments())
			);
		}
		
		return builder.toString();
	}
}
