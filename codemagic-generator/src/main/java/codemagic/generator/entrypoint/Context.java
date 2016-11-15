package codemagic.generator.entrypoint;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.subject.command.CommandContext;
import codemagic.generator.context.subject.datagrid.withasynprovider.DataGridContext;
import codemagic.generator.context.subject.display.DisplayContext;
import codemagic.generator.context.subject.nav.NavContext;
import codemagic.generator.context.subject.orchestrator.OrchestratorContext;
import codemagic.generator.context.subject.type.TypeContext;

public class Context implements IContext {

	private Optional<SharedContext> sharedContext = Optional.absent();
	private Optional<DisplayContext> displayContext = Optional.absent();
	private Optional<DataGridContext> dataGridContext = Optional.absent();
	private Optional<OrchestratorContext> orchestratorContext = Optional.absent();
	private Map<String, CommandContext> commandContextList = Maps.newHashMap();
	private Optional<TypeContext> typeContext = Optional.absent();
	private Optional<NavContext> navContext = Optional.absent();

	@Override
	public SharedContext begin() {
		sharedContext = Optional.of(new SharedContext(this));

		return sharedContext.get();
	}

	@Override
	public DisplayContext beginDisplay() {

		checkSharedContext();

		displayContext = Optional.of(new DisplayContext(sharedContext.get()));

		return displayContext.get();
	}

	@Override
	public DataGridContext beginDataGrid() {

		checkSharedContext();

		dataGridContext = Optional.of(new DataGridContext(sharedContext.get()));

		return dataGridContext.get();
	}

	@Override
	public CommandContext beginCommand() {

		checkSharedContext();

		return new CommandContext(sharedContext.get());

	}

	@Override
	public OrchestratorContext beginOrchestrator() {
		checkSharedContext();

		orchestratorContext = Optional.of(new OrchestratorContext(sharedContext.get()));

		return orchestratorContext.get();
	}

	@Override
	public TypeContext beginType() {
		checkSharedContext();

		typeContext = Optional.of(new TypeContext(sharedContext.get()));

		return typeContext.get();
	}
	
	@Override
	public NavContext beginNav() {
		checkSharedContext();

		navContext = Optional.of(new NavContext(sharedContext.get()));

		return navContext.get();
	}

	@Override
	public Optional<SharedContext> getSharedContext() {
		return sharedContext;
	}

	@Override
	public Optional<DisplayContext> getDisplayContext() {
		return displayContext;
	}

	@Override
	public Optional<DataGridContext> getDataGridContext() {
		return dataGridContext;
	}

	@Override
	public Optional<OrchestratorContext> getOrchestrator() {
		return orchestratorContext;
	}

	@Override
	public Map<String, CommandContext> getCommandContexts() {
		return commandContextList;
	}

	@Override
	public Optional<TypeContext> getTypeContext() {
		return typeContext;
	}

	@Override
	public Optional<NavContext> getNavContext() {
		return navContext;
	}

	//
	// Utilities
	//
	private void checkSharedContext() {
		Preconditions.checkArgument(sharedContext.isPresent(),
				"CommonContext is required. The method begin() should be called first.");
	}

}
