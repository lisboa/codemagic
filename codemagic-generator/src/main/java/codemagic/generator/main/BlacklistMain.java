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
import db.assist.shared.common.domain.entity.DevBlacklistItem;
import db.assist.shared.common.to.blacklist.command.BlacklistDeleteInput;
import db.assist.shared.common.to.blacklist.command.BlacklistGetResult;
import db.assist.shared.common.to.blacklist.command.BlacklistSaveResult;
import grain.util.ClassLoadUtil;

public class BlacklistMain {

	private static final Logger LOGGER = LoggerFactory.getLogger( BlacklistMain.class );
	
	private static final String ORCHESTRATOR_PACKAGE_NAME = "db.assist.view.client.application.adminlayout.blacklist.manager2";
	private static final String ORCHESTRATOR_JAVA_SOURCE_FOLDER = "/root/projects/db-assist/db-assist-client/db-assist-view/src/main/java";
	private static final String HTML_PREAMBLE_HELP = ClassLoadUtil.loadFile(BlacklistMain.class, "help/BlacklistMain/help.html");
	private static final String HTML_INPUT_WILDCHAR_HELP = ClassLoadUtil.loadFile(BlacklistMain.class, "help/BlacklistMain/wildcharInputhelp.html");

	/**
	 * @param args
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static void main (final String[] args) throws JDOMException, IOException {
		
		final ImmutableList<FieldProperty> fields = ImmutableList.of(
				
				new FieldProperty.Builder()
				.setFieldName("id")
				.setRequired(true)
				.setFieldType(FieldType.LONG)
				.setReadOnly(true)
				.build(),

				new FieldProperty.Builder()
				.setFieldName("txtInputWildcharRule")
				.setGetterField("getInputWildcharRule") // Needed only for non standard name
				.setSetterField("setInputWildcharRule") // Needed only for non standard name
				.setTitle("Rule")
				.setFieldType(FieldType.STRING)
				.setMaxLength(80)
				.setRequired(true)
				.setHelpText(HTML_INPUT_WILDCHAR_HELP)
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("txtMessage")
				.setTitle("Message")
				.setRequired(false)
				.setFieldType(FieldType.STRING)
				.setMaxLength(100)
				.setHelpText("A text to show to user when a script content is blocked by this rule.")
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("flgActive")
				.setTitle("Active")
				.setRequired(false)
				.setFieldType(FieldType.INTEGER)
				.setComponentType(ComponentType.CHECKBOX)
				.setHelpText("Uncheck to disable this rule.")
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("environments.flgDev")
				.setTitle("DEV")
				.setRequired(false)
				.setFieldType(FieldType.INTEGER)
				.setComponentType(ComponentType.CHECKBOX)
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("environments.flgInt")
				.setTitle("INT")
				.setRequired(false)
				.setFieldType(FieldType.INTEGER)
				.setComponentType(ComponentType.CHECKBOX)
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("environments.flgTst")
				.setTitle("TST")
				.setRequired(false)
				.setFieldType(FieldType.INTEGER)
				.setComponentType(ComponentType.CHECKBOX)
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("environments.flgHlgc")
				.setTitle("HLG")
				.setRequired(false)
				.setFieldType(FieldType.INTEGER)
				.setComponentType(ComponentType.CHECKBOX)
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("environments.flgPrdc")
				.setTitle("PRD")
				.setRequired(false)
				.setFieldType(FieldType.INTEGER)
				.setComponentType(ComponentType.CHECKBOX)
				.build()
		); 
		
		final IContext ctx = new Context()
				.begin()
					.setBaseNameForGeneratedArtifacts("Blacklist")
					.setFields(fields)
					.setTargetEntity(DevBlacklistItem.class.getName())
					.defineFromRequest("projectName") // Can be referenced: ${projectName}
					.setSingularItemLabel("rule") // The item name used in messages: "A new rule was saved"
					.setPlutalItemLabel("rules")  // "3 rules was deleted"
				.end()
				.beginType()
					.setJavaSourceFolder("/root/projects/db-assist/db-assist-client/db-assist-view-form/project/src/main/java")
					.setPackageName("db.assist.view.form.project.client.blacklist.type")	
				.end()
				.beginDisplay()
					.setJavaSourceFolder("/root/projects/db-assist/db-assist-client/db-assist-view-form/project/src/main/java")
					.setPackageName("db.assist.view.form.project.client.blacklist.display")
					.setEditingModeTitle("Editing a Rule")
					.setNewModeTitle("Creating a new Blacklist Rule")
					.addMainField("txtInputWildcharRule")
					.addMainField("txtMessage")
					.addMoreField("flgActive")
					.addMoreField("environments.flgDev")
					.addMoreField("environments.flgInt")
					.addMoreField("environments.flgTst")
					.addMoreField("environments.flgHlgc")
					.addMoreField("environments.flgPrdc")
				.end()
				.beginCommand()
					.setContextId("get")
					.setJavaSourceFolder("/root/projects/db-assist/db-assist-client/db-assist-view-form/project/src/main/java")
					.setPackageName("db.assist.view.form.project.client.blacklist.command")
					.setRestUrl("/svc/u/blacklists/${projectName}") // references the parameter defined above
					.setRestVerb(Verb.GET)
					.setFullQualifiedResultClassName(BlacklistGetResult.class.getName())
				.end()
				.beginCommand()
					.setContextId("save")
					.setJavaSourceFolder("/root/projects/db-assist/db-assist-client/db-assist-view-form/project/src/main/java")
					.setPackageName("db.assist.view.form.project.client.blacklist.command")
					.setRestUrl("/svc/a/blacklists/${projectName}")
					.setRestVerb(Verb.PUT)
					.setFullQualifiedResultClassName(BlacklistSaveResult.class.getName())
				.end()
				.beginCommand()
					.setContextId("delete")
					.setJavaSourceFolder("/root/projects/db-assist/db-assist-client/db-assist-view-form/project/src/main/java")
					.setPackageName("db.assist.view.form.project.client.blacklist.command")
					.setRestUrl("/svc/a/blacklists/${projectName}/delete") //the put will be used, therefore, need another endpoint
					.setRestVerb(Verb.DELETE) //Using the put verb, because delete does not allow request payload
					.setFullQualifiedInputClassName(BlacklistDeleteInput.class.getName())
				.end()
				.beginDataGrid()
					.setJavaSourceFolder("/root/projects/db-assist/db-assist-client/db-assist-view-form/project/src/main/java")
					.setPackageName("db.assist.view.form.project.client.blacklist.grid")
					.usePagination(true) // TODO This property is ignored - Fix this bug
					.beginCheckColum()
						.setColumnWidth("10px")
					.end()
					.addColumn()
						.setFieldName("id")
						.setColumnWidth("20px")
					.end()
					.addColumn()
						.setFieldName("txtInputWildcharRule")
						.setColumnWidth("50px")
					.end()
					.addColumn()
						.setFieldName("txtMessage")
						.setColumnWidth("70px")
					.end()
					.addColumn()
						.setFieldName("flgActive")
						.setColumnWidth("15px")
					.end()
					.addColumn()
						.setFieldName("environments.flgDev")
						.setColumnWidth("15px")
					.end()
					.addColumn()
						.setFieldName("environments.flgInt")
						.setColumnWidth("15px")
					.end()
					.addColumn()
						.setFieldName("environments.flgTst")
						.setColumnWidth("15px")
					.end()
					.addColumn()
						.setFieldName("environments.flgHlgc")
						.setColumnWidth("15px")
					.end()
					.addColumn()
						.setFieldName("environments.flgPrdc")
						.setColumnWidth("15px")
					.end()
				.end() 
				.beginNav()
					.setJavaSourceFolder("/root/projects/db-assist/db-assist-client/db-assist-view-form/project/src/main/java")
					.setPackageName("db.assist.view.form.project.client.blacklist.nav")
					.beginBreadcrumb()
						.setName("breadcrumb")
						.setGridSystemSize("MD_5")
						.addItem("home", "/u/home")   // The nameToken will be added to UiBinder file. Should be a literal
						.addItem("Blacklist")
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
					.setNameToken("/a/managers/blacklist/${projectName}")
					.setFullNameOfTargetSlot("db.assist.view.client.application.adminlayout.AdminLayoutPresenter.SLOT_AL_MainContent")
				.end()
				;
		
		
		if (! ctx.getSharedContext().get().isFileOverwrite()) {
			LOGGER.info("[Skipped] The code generation was skipped bwecause fileOverwrite is false ");
			return;
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
