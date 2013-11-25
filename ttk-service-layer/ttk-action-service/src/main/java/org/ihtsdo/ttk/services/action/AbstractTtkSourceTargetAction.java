package org.ihtsdo.ttk.services.action;

public class AbstractTtkSourceTargetAction extends AbstractTtkAction {
	ActionContextBI sourceContext;
	ActionContextBI targetContext;
	public AbstractTtkSourceTargetAction(String label, String description,
			ActionContextBI sourceContext, ActionContextBI targetContext) {
		super(label, description);
		this.sourceContext = sourceContext;
		this.targetContext = targetContext;
	}

}
