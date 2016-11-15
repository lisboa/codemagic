package codemagic.generator.context.subject.datagrid.withasynprovider.table;

import grain.util.ClassLoadUtil;

import java.io.File;
import java.io.IOException;

import codemagic.generator.context.subject.datagrid.withasynprovider.DataGridContext.SubContext;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

public class DataGridTablePresenterGen {

	private final SubContext tableSubContext;
	private final SubContext driverSubContext;
	
	public DataGridTablePresenterGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getSharedContext().isPresent(), "Common context cannot be null" );
	
		Preconditions.checkArgument( ctx.getDataGridContext().isPresent(), "Datagrid context cannot be null" );
		
		tableSubContext = ctx.getDataGridContext().get().getTableSubContext();

		driverSubContext = ctx.getDataGridContext().get().getDriverSubContext();
		
		
	}

	public void generate() throws IOException {

		// input
		final String template = ClassLoadUtil.loadFile(DataGridTablePresenterGen.class, "templates/datagrid/withasynprovider/table/_BaseObjectName_Presenter.java.template");
		
		final Engine engine = new Engine();
		
		tableSubContext.getModel().put("fullDriverClassName", driverSubContext.buildFullClassName( ArtefactyType.DRIVER ));
		tableSubContext.getModel().put("DriverClassName", driverSubContext.buildClassName( ArtefactyType.DRIVER ));
		
		final String transformedJava = engine.transform(template, tableSubContext.getModel());
		
		final String fullName = tableSubContext.buildArtifactFullFileName( ArtefactyType.PRESENTER );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}
}
