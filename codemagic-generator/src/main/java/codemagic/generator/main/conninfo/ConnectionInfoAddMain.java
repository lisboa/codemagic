package codemagic.generator.main.conninfo;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.FieldProperty.FieldType;
import codemagic.generator.context.shared.component.ContextualType;
import codemagic.generator.context.subject.command.CommandContext.Verb;
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
import codemagic.generator.context.subject.type.ContextGen;
import codemagic.generator.context.subject.type.MessageGen;
import codemagic.generator.context.subject.type.StateGen;
import codemagic.generator.entrypoint.Context;
import codemagic.generator.entrypoint.IContext;
import db.assist.shared.common.to.manager.connectioninfo.ConnectionInfoTO;
import db.assist.shared.common.to.manager.connectioninfo.command.ConnInfoOneGetResult;
import db.assist.shared.common.to.manager.connectioninfo.command.ConnInfoSaveResult;
import grain.util.ClassLoadUtil;

/**
 * List and delete connections info of a module.
 *
 */
public class ConnectionInfoAddMain {

	private static final String CORE_JAVA_SOURCE_FOLDER = "/root/projects/db-assist/db-assist-core/src/main/java";

	private static final Logger LOGGER = LoggerFactory.getLogger( ConnectionInfoAddMain.class );
	
	private static final String MANAGER_JAVA_SOURCE_FOLDER = "/root/projects/db-assist/db-assist-client/db-assist-view-form/project/src/main/java";
	private static final String MANAGER_BASE_PACKAGE = "db.assist.view.form.project.client.connectioninfo.add";
	
	private static final String ORCHESTRATOR_PACKAGE_NAME = "db.assist.view.client.application.adminlayout.manager.connectioninfo.add";
	private static final String ORCHESTRATOR_JAVA_SOURCE_FOLDER = "/root/projects/db-assist/db-assist-client/db-assist-view/src/main/java";
	
	private static final String HTML_PREAMBLE_HELP = ClassLoadUtil.loadFile(ConnectionInfoAddMain.class, "help/ConnectionInfoAdd/preemble.html");

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
				.setFieldName("connectionName")
				.setTitle("Connection name")
				.setRequired(true)
				.setFieldType(FieldType.STRING)
				// .setComponentType(ComponentType.TEXTBOX)
				.build(),

				new FieldProperty.Builder()
				.setFieldName("driverClassName")
				.setTitle("Driver")
				.setFieldType(FieldType.STRING)
				.setMaxLength(1000)
				.setRequired(true)
				.setHelpText("The JDBC driver used to connect to database")
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("jdbcUrl")
				.setTitle("URL")
				.setFieldType(FieldType.STRING)
				.setMaxLength(1000)
				.setRequired(true)
				.setHelpText("The database server url")
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("user")
				.setTitle("Username")
				.setFieldType(FieldType.STRING)
				.setMaxLength(80)
				.setRequired(true)
				.setHelpText("The username used to access the database")
				.build(),
				
				new FieldProperty.Builder()
				.setFieldName("password")
				.setTitle("Password")
				.setFieldType(FieldType.STRING)
				.setMaxLength(80)
				.setRequired(true)
				.setHelpText("The password used to access the database")
				.build()
		); 
		
		final IContext ctx = new Context()
				.begin()
					.setBaseNameForGeneratedArtifacts("ConnInfoAdd")
					.setFields(fields)
					.setTargetEntity(ConnectionInfoTO.class.getName())
					.setSingularItemLabel("connection") // "A new 'connection' was saved"
					.setPlutalItemLabel("connections")  // "3 'connections' was deleted"
					.defineFromRequest("id")
				.end()
				.beginType()	
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName( MANAGER_BASE_PACKAGE + ".type")	
				.end()
				.beginCommand()
					.setContextId("get")
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".command")
					.setRestUrl("/svc/u/connectioninfos/{id}") // references the parameter defined above
					.setRestVerb(Verb.GET)
					.setFullQualifiedResultClassName( ConnInfoOneGetResult.class.getName() )
					.beginServerRest()
						.setJavaSourceFolder(CORE_JAVA_SOURCE_FOLDER)
						.setFullQualifiedServiceName("db.assist.core.ws.rest.user.UserConnectionInfoRestService2")
					.end()
				.end()
				.beginCommand()
					.setContextId("save")
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".command")
					.setRestUrl("/svc/a/connectioninfos")
					.setRestVerb(Verb.PUT)
					.setFullQualifiedResultClassName( ConnInfoSaveResult.class.getName() )
					.beginServerRest()
						.setJavaSourceFolder(CORE_JAVA_SOURCE_FOLDER)
						.setFullQualifiedServiceName("db.assist.core.ws.rest.admin.AdminConnectionInfoRestService2")
					.end()
				.end()
				.beginDisplay()
					.setJavaSourceFolder( MANAGER_JAVA_SOURCE_FOLDER )
					.setPackageName( MANAGER_BASE_PACKAGE + ".display" )
					.setEditingModeTitle("Editing a Connections")
					.setNewModeTitle("Creating a new Connection")
					.addMainField("connectionName")
					.addMainField("driverClassName")
					.addMainField("jdbcUrl")
					.addMainField("user")
					.addMainField("password")
				.end()
				.beginNav()
					.setJavaSourceFolder(MANAGER_JAVA_SOURCE_FOLDER)
					.setPackageName(MANAGER_BASE_PACKAGE + ".nav")
					.beginBreadcrumb()
						.setName("breadcrumb")
						.setGridSystemSize("MD_5")
						.setGridSystemOffset("MD_1")
						.addItem("home", "/u/home")   // The nameToken will be added to UiBinder file. Should be a literal
						.addItem("Connection Manager")
						.addItem("{id}") // The connection been edited
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
					.setNameToken("NameTokens.uConnectionInfoAdd") //Should remove quotes on generated code: EmcManagerOrchestratorPresenter
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
		// new CommandGen(ctx).generate();
		
		// Display
		new DisplayViewJavaGen(ctx).generate();
		new DisplayUiHandlerGen(ctx).generate();
		new DisplayPresenterGen(ctx).generate();
		new DisplayViewXmlGen(ctx).generate();
		new DisplayModuleGen(ctx).generate();
		
		
		// nav buttons
		new NavViewXmlGen(ctx).generate();
		new NavViewJavaGen(ctx).generate();
		new NavPresenterGen(ctx).generate();
		new NavUiHandlerGen(ctx).generate();
		new NavModuleGen(ctx).generate();
		new NavEventGen(ctx).generate();
		
	}

}
