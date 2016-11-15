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
import db.assist.shared.common.to.manager.build.BuildHistoryInfoTO;
import db.assist.shared.common.to.manager.build.command.BuildDeleteInput;
import db.assist.shared.common.to.manager.build.command.BuildsGetResult;
import grain.util.ClassLoadUtil;

public class BuildManagerMain {

	private static final Logger LOGGER = LoggerFactory.getLogger( BuildManagerMain.class );
	private static final String MANAGER_JAVA_SOURCE_FOLDER = "/root/projects/db-assist/db-assist-client/db-assist-view-form/project/src/main/java";
	private static final String MANAGER_BASE_PACKAGE = "db.assist.view.form.project.client.build";
	
	private static final String ORCHESTRATOR_PACKAGE_NAME = "db.assist.view.client.application.adminlayout.manager.build";
	private static final String ORCHESTRATOR_JAVA_SOURCE_FOLDER = "/root/projects/db-assist/db-assist-client/db-assist-view/src/main/java";
	
	private static final String HTML_PREAMBLE_HELP = ClassLoadUtil.loadFile(BuildManagerMain.class, "help/BuildManager/preemble.html");

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
				.setFieldName("buildNumber")
				.setTitle("build number")
				.setRequired(true)
				.setFieldType(FieldType.LONG)
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("buildStatus")
				.setTitle("Status")
				.setRequired(true)
				.setFieldType(FieldType.CUSTOM)
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("startDate")
				.setTitle("Start date")
				.setRequired(true)
				.setFieldType(FieldType.Date)
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("endDate")
				.setTitle("End date")
				.setRequired(true)
				.setFieldType(FieldType.Date)
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("moduleId")
				.setTitle("Module ID")
				.setRequired(true)
				.setFieldType(FieldType.LONG)
				.build()
		); 
		
		final IContext ctx = new Context()
				.begin()
					.setBaseNameForGeneratedArtifacts("BuildManager")
					.setFields(fields)
					.setTargetEntity(BuildHistoryInfoTO.class.getName())
					.setSingularItemLabel("build") // The item name used in messages: "A new rule was saved"
					.setPlutalItemLabel("builds")  // "3 versions was deleted"
					.defineFromRequest("moduleName") // Manager builds for thios module
					.defineFromRequest("moduleId") // Manager builds for thios module
				.end()
				.beginType()	
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName( MANAGER_BASE_PACKAGE + ".type")	
				.end()
				.beginCommand()
					.setContextId("get")
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".command")
					.setRestUrl("/svc/u/builds/modules/${moduleId}") // references the parameter defined above
					.setRestVerb(Verb.GET)
					.setFullQualifiedResultClassName(BuildsGetResult.class.getName())
					.beginServerRest()
						.setJavaSourceFolder("/root/projects/db-assist/db-assist-core/src/main/java")
						.setFullQualifiedServiceName("db.assist.core.ws.rest.user.UserBuildRestService")
					.end()
				.end()
				.beginCommand()
					.setContextId("delete")
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".command")
					.setRestUrl("/svc/a/builds/modules/${moduleId}/delete") //the put will be used, therefore, need another endpoint
					.setRestVerb(Verb.DELETE) //Internally, using the put verb, because delete does not allow request payload
					.setFullQualifiedInputClassName(BuildDeleteInput.class.getName())
					.beginServerRest()
						.setJavaSourceFolder("/root/projects/db-assist/db-assist-core/src/main/java")
						.setFullQualifiedServiceName("db.assist.core.ws.rest.admin.AdminBuildRestService")
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
						.setFieldName("buildNumber")
						.setColumnWidth("20px")
					.end()
					.addColumn()
						.setFieldName("buildStatus")
						.setColumnWidth("20px")
					.end()
					.addColumn()
						.setFieldName("startDate")
						.setColumnWidth("10px")
						.end()
					.addColumn()
						.setFieldName("endDate")
						.setColumnWidth("10px")
					.end()
				.end() 
				.beginNav()
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".nav")
					.beginBreadcrumb()
						.setName("breadcrumb")
						.setGridSystemSize("MD_5")
						.addItem("home", "/u/home")   // The nameToken will be added to UiBinder file. Should be a literal
						.addItem("Builds")
						.addItem("${moduleName}")
					.end()
					.beginButtonGroup()
						.setAddInJavaCode(false) // Do not generate the java code, only the uibinder code
					    .setGridSystemSize("MD_2")
					    .setGridSystemOffset("MD_5") 
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
					.setNameToken("/a/managers/modules/{moduleId}/builds")
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
		
		// display
		/*
		new DisplayViewJavaGen(ctx).generate();
		new DisplayUiHandlerGen(ctx).generate();
		new DisplayPresenterGen(ctx).generate();
		new DisplayViewXmlGen(ctx).generate();
		new DisplayModuleGen(ctx).generate();
		*/
		
		// command
		// new CommandGen(ctx).generate();
		
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
