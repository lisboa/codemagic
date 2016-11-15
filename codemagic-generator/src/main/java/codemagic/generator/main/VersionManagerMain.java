package codemagic.generator.main;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.FieldProperty.ComponentType;
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
import codemagic.generator.context.subject.display.DisplayModuleGen;
import codemagic.generator.context.subject.display.DisplayPresenterGen;
import codemagic.generator.context.subject.display.DisplayUiHandlerGen;
import codemagic.generator.context.subject.display.DisplayViewJavaGen;
import codemagic.generator.context.subject.display.DisplayViewXmlGen;
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
import db.assist.shared.common.domain.entity.DevVersion;
import db.assist.shared.common.to.manager.versions.command.VersionDeleteInput;
import db.assist.shared.common.to.manager.versions.command.VersionSaveResult;
import db.assist.shared.common.to.manager.versions.command.VersionsGetResult;
import grain.util.ClassLoadUtil;

public class VersionManagerMain {

	private static final Logger LOGGER = LoggerFactory.getLogger( VersionManagerMain.class );
	
	private static final String MANAGER_JAVA_SOURCE_FOLDER = "/root/projects/db-assist/db-assist-client/db-assist-view-form/project/src/main/java";
	private static final String MANAGER_BASE_PACKAGE = "db.assist.view.form.project.client.version";
	
	private static final String ORCHESTRATOR_PACKAGE_NAME = "db.assist.view.client.application.adminlayout.manager.version";
	private static final String ORCHESTRATOR_JAVA_SOURCE_FOLDER = "/root/projects/db-assist/db-assist-client/db-assist-view/src/main/java";
	
	private static final String HTML_PREAMBLE_HELP = ClassLoadUtil.loadFile(VersionManagerMain.class, "help/VersionManager/preemble.html");

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
				.setFieldName("projectName")
				.setTitle("Project")
				.setFieldType(FieldType.STRING)
				.setMaxLength(80)
				.setRequired(true)
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("versionName")
				.setTitle("Version")
				.setFieldType(FieldType.STRING)
				.setMaxLength(80)
				.setRequired(true)
				.setHelpText("The project version")
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("flgOpen")
				.setTitle("Open")
				.setFieldType(FieldType.INTEGER)
				.setMaxLength(80)
				.setRequired(true)
				.setHelpText("If flagged (default), you can add scripts to this versions.")
				.setComponentType(ComponentType.CHECKBOX)
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("numOrder")
				.setTitle("NumOrder")
				.setFieldType(FieldType.Double)
				.setMaxLength(80)
				.setRequired(true)
				.setHelpText("Control the appearance order of the version in the home page. If a Version A has\n" + 
						"	numOrder 1 and a Version B has numOrder 2, then B appear before A. The\n" + 
						"	default value is the creation order, that is, younger version appear\n" + 
						"	before older")
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("txtVersion")
				.setTitle("Description")
				.setFieldType(FieldType.STRING)
				.setMaxLength(80)
				.setRequired(true)
				.setHelpText("An infromative description for this versions.")
				.build()
				
		); 
		
		final IContext ctx = new Context()
				.begin()
					.setBaseNameForGeneratedArtifacts("VersionManager")
					.setFields(fields)
					.setTargetEntity(DevVersion.class.getName())
					.setSingularItemLabel("version") // The item name used in messages: "A new rule was saved"
					.setPlutalItemLabel("versions")  // "3 versions was deleted"
					.defineFromRequest("projectName")
				.end()
				.beginType()	
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName( MANAGER_BASE_PACKAGE + ".type")	
				.end()
				.beginDisplay()
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".display")
					.setEditingModeTitle("Editing a Version")
					.setNewModeTitle("Creating a new Version")
					.addMainField("versionName")
					.addMoreField("flgOpen")
					.addMoreField("numOrder")
					.addMoreField("txtVersion")
				.end()
				.beginCommand()
					.setContextId("get")
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".command")
					.setRestUrl("/svc/u/versions/${projectName}") // references the parameter defined above
					.setRestVerb(Verb.GET)
					.setFullQualifiedResultClassName(VersionsGetResult.class.getName())
					/*
					.beginServerRest()
						.setJavaSourceFolder("/root/projects/db-assist/db-assist-core/src/main/java")
						.setFullQualifiedServiceName("db.assist.core.ws.rest.user.UserVersionRestService")
					.end()
					*/
				.end()
				.beginCommand()
					.setContextId("save")
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".command")
					.setRestUrl("/svc/a/versions/${projectName}")
					.setRestVerb(Verb.PUT)
					.setFullQualifiedResultClassName(VersionSaveResult.class.getName())
					/*
					.beginServerRest()
						.setJavaSourceFolder("/root/projects/db-assist/db-assist-core/src/main/java")
						.setFullQualifiedServiceName("db.assist.core.ws.rest.admin.AdminVersionRestService")
					.end()
					*/
				.end()
				.beginCommand()
					.setContextId("delete")
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".command")
					.setRestUrl("/svc/a/versions/${projectName}/delete") //the put will be used, therefore, need another endpoint
					.setRestVerb(Verb.DELETE) //Internally, using the put verb, because delete does not allow request payload
					.setFullQualifiedInputClassName(VersionDeleteInput.class.getName())
					/*
					.beginServerRest()
						.setJavaSourceFolder("/root/projects/db-assist/db-assist-core/src/main/java")
						.setFullQualifiedServiceName("db.assist.core.ws.rest.admin.AdminVersionRestService")
					.end()
					 */
				.end()
				.beginDataGrid()
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".grid")
					.usePagination(true) // TODO This property is ignored - Fix this bug
					.beginCheckColum()
						.setColumnWidth("10px")
					.end()
					.addColumn()
						.setFieldName("versionName")
						.setColumnWidth("20px")
					.end()
					.addColumn()
						.setFieldName("flgOpen")
						.setColumnWidth("10px")
						.end()
					.addColumn()
						.setFieldName("numOrder")
						.setColumnWidth("10px")
					.end()
					.addColumn()
						.setFieldName("txtVersion")
						.setColumnWidth("80px")
					.end()
				.end() 
				.beginNav()
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".nav")
					.beginBreadcrumb()
						.setName("breadcrumb")
						.setGridSystemSize("MD_5")
						.addItem("home", "/u/home")   // The nameToken will be added to UiBinder file. Should be a literal
						.addItem("Versions")
						.addItem("${projectName}")
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
					.setNameToken("/a/managers/versions/${projectName}")
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
		new DisplayViewJavaGen(ctx).generate();
		new DisplayUiHandlerGen(ctx).generate();
		new DisplayPresenterGen(ctx).generate();
		new DisplayViewXmlGen(ctx).generate();
		new DisplayModuleGen(ctx).generate();
		
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
