package codemagic.generator.context.subject.datagrid.withasynprovider.driver;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.shared.util.OtherUtil;
import codemagic.generator.context.subject.AbstractGen.FileNameHolder;
import codemagic.generator.context.subject.command.CommandContext;
import codemagic.generator.context.subject.datagrid.withasynprovider.DataGridContext.SubContext;
import codemagic.generator.context.subject.type.TypeContext;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;
import grain.util.ClassLoadUtil;

public class DataGridDriverGen {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataGridDriverGen.class);
	private static final String KEY_COMMAND_GET = "get";
	private final SubContext driverSubContext;
	private final Map<String, CommandContext> commandContexts;
	private final TypeContext typeContext;
	private final SharedContext sharedContext;
	
	
	public DataGridDriverGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getSharedContext().isPresent(),	"Common context cannot be null");
		
		Preconditions.checkArgument( ctx.getDataGridContext().isPresent(), "Datagrid context cannot be null" );
		
		Preconditions.checkArgument( !ctx.getCommandContexts().isEmpty(), "Datagrid requires a command to fetch and delete items. You called the beginCommand() ?" );
		
		Preconditions.checkArgument( ctx.getCommandContexts().containsKey(KEY_COMMAND_GET) , "Datagrid requires a GetCommand to fetch items. You called the beginCommand().setContextId(\"get\") ?");

		Preconditions.checkArgument( ctx.getTypeContext().isPresent(), "The TypeContext cannot be null. Do you have called the method beginType() ?" );
		
		this.commandContexts = ctx.getCommandContexts();
		
		this.driverSubContext = ctx.getDataGridContext().get().getDriverSubContext();

		this.typeContext = ctx.getTypeContext().get();
		
		sharedContext = ctx.getSharedContext().get();

	}

	public void generate() throws IOException {

		final FileNameHolder fileNameHolder = new FileNameHolder( driverSubContext.buildArtifactFullFileName( ArtefactyType.DRIVER ));
		
		if (! OtherUtil.shouldGenerateFile(fileNameHolder.getFile(), sharedContext.isFileOverwrite())) {

			LOGGER.info("<<< The file '{}' already exists. It is not overwritable, because the flag fileOverwrite is true.", fileNameHolder.getFile().getAbsolutePath());
			
			return;
		}
		
		// input
		final String template = ClassLoadUtil.loadFile(DataGridDriverGen.class, "templates/datagrid/withasynprovider/driver/_EntityName_DataGridDriver.java.template");
		
		// pre-processing
		final Engine engine = new Engine();
		
		final CommandContext commandContext = commandContexts.get(KEY_COMMAND_GET);

		driverSubContext.getModel().put("fullContextClassName", typeContext.buildFullClassName(ArtefactyType.CONTEXT));
		driverSubContext.getModel().put("ContextClassName", typeContext.buildClassName(ArtefactyType.CONTEXT));
		driverSubContext.getModel().put("GetCommandName", commandContext.getCapitalizedCommandName());
		driverSubContext.getModel().put("getCommandName", commandContext.getCommandName());
		driverSubContext.getModel().put("fullGetCommandName", commandContext.getFullCommandName());
		driverSubContext.getModel().put("TargetResultName", commandContext.getTargetResult().get().getSimpleName());
		driverSubContext.getModel().put("fullTargetResultName", commandContext.getTargetResult().get().getName());
		
		
		final String transformedJava = engine.transform(template, driverSubContext.getModel());
		
		final String fullName = driverSubContext.buildArtifactFullFileName( ArtefactyType.DRIVER );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}
}
