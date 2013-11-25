package org.ihtsdo.ttk.services.action;


public abstract class AbstractTtkAction implements ActionBI {
	private String label;
	private String description;
	private ExecutionCriterion executionCriterion = ExecutionCriterion.USER_SELECTION;
	private ThreadForAction threadForAction = ThreadForAction.BACKGROUND_THREAD;
	private ExecutionState executionState = ExecutionState.READY;
	
	public AbstractTtkAction(String label, String description) {
		super();
		this.label = label;
		this.description = description;
	}

        @Override
	public String getLabel() {
		return label;
	}

        @Override
	public String getDescription() {
		return description;
	}

        @Override
	public ExecutionCriterion getExecutionCriterion() {
		return executionCriterion;
	}

	public void setExecutionCriterion(ExecutionCriterion executionCriterion) {
		this.executionCriterion = executionCriterion;
	}

        @Override
	public ThreadForAction getThreadForAction() {
		return threadForAction;
	}

	public void setThreadForAction(ThreadForAction threadForAction) {
		this.threadForAction = threadForAction;
	}

        @Override
	public ExecutionState getExecutionState() {
		return executionState;
	}

	public void setExecutionState(ExecutionState executionState) {
		this.executionState = executionState;
	}

}
