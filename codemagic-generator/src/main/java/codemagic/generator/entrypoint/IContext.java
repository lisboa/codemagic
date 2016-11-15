package codemagic.generator.entrypoint;

import java.util.Map;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.subject.command.CommandContext;
import codemagic.generator.context.subject.datagrid.withasynprovider.DataGridContext;
import codemagic.generator.context.subject.display.DisplayContext;
import codemagic.generator.context.subject.nav.NavContext;
import codemagic.generator.context.subject.orchestrator.OrchestratorContext;
import codemagic.generator.context.subject.type.TypeContext;

import com.google.common.base.Optional;

public interface IContext {

	SharedContext begin();

	DisplayContext beginDisplay();
	
	DataGridContext beginDataGrid();
	
	CommandContext beginCommand();
	
	TypeContext beginType();
	
	NavContext beginNav();

	OrchestratorContext beginOrchestrator();

	Optional<SharedContext> getSharedContext();

	Optional<DisplayContext> getDisplayContext();
	
	Optional<DataGridContext> getDataGridContext();
	
	Map<String, CommandContext> getCommandContexts();
	
	Optional<OrchestratorContext> getOrchestrator();

	Optional<TypeContext> getTypeContext();
	
	Optional<NavContext> getNavContext();
}