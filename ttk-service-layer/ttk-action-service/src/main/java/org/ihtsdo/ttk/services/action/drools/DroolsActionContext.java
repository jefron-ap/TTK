package org.ihtsdo.ttk.services.action.drools;

//~--- non-JDK imports --------------------------------------------------------

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.ihtsdo.ttk.services.action.ActionBI;
import org.ihtsdo.ttk.services.action.ActionContextBI;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;

/**
 * Class description
 *
 *
 * @version        Enter version here..., 13/06/13
 * @author         Enter your name here...    
 */
public class DroolsActionContext {

   /** Field description */
   protected ObservableList<ActionBI> contextualActions =
      FXCollections.observableList(new ArrayList<ActionBI>());

   /** Field description */
   protected ActionContextBI actionContext;

   /**
    * Constructs ...
    *
    *
    * @param actionContext
    */
   public DroolsActionContext(ActionContextBI actionContext) {
      super();
      this.actionContext = actionContext;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public ActionContextBI getActionContext() {
      return actionContext;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public ObservableList<ActionBI> getContextualActions() {
      return contextualActions;
   }
}
