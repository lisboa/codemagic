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

public class DataGridTableViewJavaGen {

	private final SubContext tableSubContext;
	
	public DataGridTableViewJavaGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getSharedContext().isPresent(), "Common context cannot be null" );
	
		Preconditions.checkArgument( ctx.getDataGridContext().isPresent(), "Datagrid context cannot be null" );
		
		tableSubContext = ctx.getDataGridContext().get().getTableSubContext();
		
	}

	public void generate() throws IOException {

		// input
		final String template = ClassLoadUtil.loadFile(DataGridTableViewJavaGen.class, "templates/datagrid/withasynprovider/table/_BaseObjectName_View.java.template");
		
		final Engine engine = new Engine();
		
		tableSubContext.getModel().put("usePagination", tableSubContext.isUsePagination());
		// tableSubContext.getModel().put("fullUiHandlersName", tableSubContext.buildFullClassName( ArtefactyType.UI_HANDLER ));
		
		final String transformedJava = engine.transform(template, tableSubContext.getModel());
		
		final String fullName = tableSubContext.buildArtifactFullFileName( ArtefactyType.VIEW_JAVA );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}
}
