package codemagic.generator.main;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.FieldProperty.FieldType;
import codemagic.generator.context.shared.component.ContextualType;
import codemagic.generator.context.subject.command.CommandContext.Verb;
import codemagic.generator.context.subject.command.CommandGen;
import codemagic.generator.context.subject.datagrid.withasynprovider.driver.DataGridDriverGen;
import codemagic.generator.context.subject.datagrid.withasynprovider.table.DataGridTableModuleGen;
import codemagic.generator.context.subject.datagrid.withasynprovider.table.DataGridTablePresenterGen;
import codemagic.generator.context.subject.datagrid.withasynprovider.table.DataGridTableUiHandlerGen;
import codemagic.generator.context.subject.datagrid.withasynprovider.table.DataGridTableViewJavaGen;
import codemagic.generator.context.subject.datagrid.withasynprovider.table.DataGridTableViewXmlGen;
import codemagic.generator.context.subject.nav.NavEventGen;
import codemagic.generator.context.subject.nav.NavModuleGen;
import codemagic.generator.context.subject.nav.NavPresenterGen;
import codemagic.generator.context.subject.nav.NavUiHandlerGen;
import codemagic.generator.context.subject.nav.NavViewJavaGen;
import codemagic.generator.context.subject.nav.NavViewXmlGen;
import codemagic.generator.context.subject.orchestrator.OrchestratorHelpGen;
import codemagic.generator.context.subject.orchestrator.OrchestratorModuleGen;
import codemagic.generator.context.subject.orchestrator.OrchestratorPresenterGen;
import codemagic.generator.context.subject.orchestrator.OrchestratorUiHandlersGen;
import codemagic.generator.context.subject.orchestrator.OrchestratorViewJavaGen;
import codemagic.generator.context.subject.orchestrator.OrchestratorViewXmlGen;
import codemagic.generator.context.subject.type.ContextGen;
import codemagic.generator.context.subject.type.MessageGen;
import codemagic.generator.context.subject.type.StateGen;
import codemagic.generator.entrypoint.Context;
import codemagic.generator.entrypoint.IContext;
import db.assist.shared.common.to.manager.emc.EmcToWithCn;
import db.assist.shared.common.to.manager.emc.command.EmcDeleteInput;
import db.assist.shared.common.to.manager.emc.command.EmcListGetResult;
import db.assist.shared.common.to.manager.emc.command.EmcSaveResult;
import grain.util.ClassLoadUtil;

/**
 * List the EMC (link between Environment, Module and ConnectionInfo) of a module.
 *
 */
public class EmcListManagerMain {

	private static final String CORE_JAVA_SOURCE_FOLDER = "/root/projects/db-assist/db-assist-core/src/main/java";

	private static final Logger LOGGER = LoggerFactory.getLogger( EmcListManagerMain.class );
	
	private static final String MANAGER_JAVA_SOURCE_FOLDER = "/root/projects/db-assist/db-assist-client/db-assist-view-form/project/src/main/java";
	private static final String MANAGER_BASE_PACKAGE = "db.assist.view.form.project.client.emc.list";
	
	private static final String ORCHESTRATOR_PACKAGE_NAME = "db.assist.view.client.application.adminlayout.manager.emc.list";
	private static final String ORCHESTRATOR_JAVA_SOURCE_FOLDER = "/root/projects/db-assist/db-assist-client/db-assist-view/src/main/java";
	
	private static final String HTML_PREAMBLE_HELP = ClassLoadUtil.loadFile(EmcListManagerMain.class, "help/EmcListManager/preemble.html");

	/**
	 * @param args
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static void main (final String[] args) throws JDOMException, IOException {
		
		final ImmutableList<FieldProperty> fields = ImmutableList.of(

				new FieldProperty.Builder()
				.setFieldName("id")
				.setTitle("id")
				.setRequired(true)
				.setFieldType(FieldType.LONG)
				.build(),

				new FieldProperty.Builder()
				.setFieldName("env")
				.setTitle("Environment")
				.setFieldType(FieldType.STRING)
				.setMaxLength(80)
				.setRequired(true)
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("jdbcUrl")
				.setTitle("Connection string")
				.setFieldType(FieldType.STRING)
				.setMaxLength(80)
				.setRequired(true)
				.setHelpText("The server database url")
				.build()
		); 
		
		final IContext ctx = new Context()
				.begin()
					.setBaseNameForGeneratedArtifacts("EmcManager")
					.setFields(fields)
					.setTargetEntity(EmcToWithCn.class.getName())
					.setSingularItemLabel("link between Module and database Conenction") // "A new 'server target' was saved"
					.setPlutalItemLabel("links between Module and database Conenction")  // "3 'server targets' was deleted"
					.defineFromRequest("moduleId")
				.end()
				.beginType()	
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName( MANAGER_BASE_PACKAGE + ".type")	
				.end()
				.beginCommand()
					.setContextId("get")
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".command")
					.setRestUrl("/u/emces/modules/${moduleId}") // references the parameter defined above
					.setRestVerb(Verb.GET)
					.setFullQualifiedResultClassName(EmcListGetResult.class.getName())
					.beginServerRest()
						.setJavaSourceFolder(CORE_JAVA_SOURCE_FOLDER)
						.setFullQualifiedServiceName("db.assist.core.ws.rest.user.UserEmcRestService")
					.end()
				.end()
				.beginCommand()
					.setContextId("save")
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".command")
					.setRestUrl("/a/emces/modules/${moduleId}")
					.setRestVerb(Verb.PUT)
					.setFullQualifiedResultClassName(EmcSaveResult.class.getName())
					.beginServerRest()
						.setJavaSourceFolder(CORE_JAVA_SOURCE_FOLDER)
						.setFullQualifiedServiceName("db.assist.core.ws.rest.admin.AdminEmcRestService")
					.end()
				.end()
				.beginCommand()
					.setContextId("delete")
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".command")
					.setRestUrl("/a/emces/delete") //the put will be used, therefore, need another endpoint
					.setRestVerb(Verb.DELETE) //Internally, using the put verb, because delete does not allow request payload
					.setFullQualifiedInputClassName(EmcDeleteInput.class.getName())
					.beginServerRest()
						.setJavaSourceFolder(CORE_JAVA_SOURCE_FOLDER)
						.setFullQualifiedServiceName("db.assist.core.ws.rest.admin.AdminEmcRestService")
					.end()
				.end()
				.beginDataGrid()
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".grid")
					.usePagination(true) // TODO This property is ignored - Fix this bug
					.beginCheckColum()
						.setColumnWidth("10px")
					.end()
					.addColumn()
						.setFieldName("env")
						.setColumnWidth("20px")
					.end()
					.addColumn()
						.setFieldName("jdbcUrl")
						.setColumnWidth("10px")
						.end()
				.end() 
				.beginNav()
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".nav")
					.beginBreadcrumb()
						.setName("breadcrumb")
						.setGridSystemSize("MD_5")
						.setGridSystemOffset("MD_1")
						.addItem("home", "/u/home")   // The nameToken will be added to UiBinder file. Should be a literal
						.addItem("Database connections")
						.addItem("${moduleId}")
					.end()
					.beginButtonGroup()
						.setAddInJavaCode(false) // Do not generate the java code, only the uibinder code
					    .setGridSystemSize("MD_2")
					    .setGridSystemOffset("MD_2") 
						.addButton()
							.setText("Back")
							.setType(ContextualType.DEFAULT)
							.whenClickedNavegateTo("db.assist.view.client.place.NameTokens.uHome")
						.end()
						.addButton()
							.setText("Help")
							.setType(ContextualType.DEFAULT).setIcon("QUESTION_CIRCLE")
							.whenClickedShowHelp(HTML_PREAMBLE_HELP, ORCHESTRATOR_JAVA_SOURCE_FOLDER, ORCHESTRATOR_PACKAGE_NAME  )
						.end()
					.end()
				.end()
				.beginOrchestrator()
					.setJavaSourceFolder(ORCHESTRATOR_JAVA_SOURCE_FOLDER)
					.setPackageName(ORCHESTRATOR_PACKAGE_NAME)
					.setNameToken("NameTokens.uEmcManager") //Should remove quotes on generated code: EmcManagerOrchestratorPresenter
					.setFullNameOfTargetSlot("db.assist.view.client.application.adminlayout.AdminLayoutPresenter.SLOT_AL_MainContent")
				.end()
				;
		
		if (! ctx.getSharedContext().get().isFileOverwrite()) {
			LOGGER.info("[Skipped] The code generation was skipped bwecause fileOverwrite is false ");
			// return;
		}
			
		// commom type
		new StateGen(ctx).generate();
		new ContextGen(ctx).generate();
		new MessageGen(ctx).generate();
		
		// command
		new CommandGen(ctx).generate();
		
		// datagrid
		new DataGridDriverGen(ctx).generate();
		new DataGridTableViewXmlGen(ctx).generate();
		new DataGridTableViewJavaGen(ctx).generate();
		new DataGridTableUiHandlerGen(ctx).generate();
		new DataGridTablePresenterGen(ctx).generate();
		new DataGridTableModuleGen(ctx).generate();
		
		// nav buttons
		new NavViewXmlGen(ctx).generate();
		new NavViewJavaGen(ctx).generate();
		new NavPresenterGen(ctx).generate();
		new NavUiHandlerGen(ctx).generate();
		new NavModuleGen(ctx).generate();
		new NavEventGen(ctx).generate();
		
		//orchestrator
		new OrchestratorViewXmlGen(ctx).generate();
		new OrchestratorViewJavaGen(ctx).generate();
		new OrchestratorUiHandlersGen(ctx).generate();
		new OrchestratorPresenterGen(ctx).generate();
		new OrchestratorModuleGen(ctx).generate();
		new OrchestratorHelpGen(ctx).generate();
	}

}
