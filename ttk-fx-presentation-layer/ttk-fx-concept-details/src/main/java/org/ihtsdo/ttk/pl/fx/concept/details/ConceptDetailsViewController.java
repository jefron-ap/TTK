/*
 * Copyright 2013 International Health Terminology Standards Development Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.ihtsdo.ttk.pl.fx.concept.details;

//~--- non-JDK imports --------------------------------------------------------

import javafx.application.Platform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javafx.util.Callback;

import org.ihtsdo.otf.tcc.api.store.Ts;
import org.ihtsdo.otf.tcc.ddo.concept.ConceptChronicleDdo;
import org.ihtsdo.otf.tcc.ddo.context.TerminologyContext;
import org.ihtsdo.otf.tcc.lookup.Looker;

import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;
import org.ihtsdo.otf.tcc.api.nid.NidSetBI;
import org.ihtsdo.otf.tcc.api.concept.ConceptVersionBI;
import org.ihtsdo.otf.tcc.api.coordinate.EditCoordinate;
import org.ihtsdo.otf.tcc.api.nid.NidSet;
import org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding;
import org.ihtsdo.ttk.logic.DefinitionTree;
import org.ihtsdo.ttk.services.aa.SessionAttributeKeys;
import org.ihtsdo.ttk.services.aa.SessionAttributes;

/**
 *
 * @author kec
 */
public class ConceptDetailsViewController
        implements LookupListener, Initializable, ChangeListener<ConceptChronicleDdo> {

   /** Field description */
   @FXML                                        // fx:id="contextComboBox"
   private ComboBox<Object> contextComboBox;    // Value injected by FXMLLoader

   /** Field description */
   @FXML                           // fx:id="textField"
   private TextField textField;    // Value injected by FXMLLoader

   @FXML                         // fx:id="definitionPane"
   private DefinitionPane definitionPane;    // Value injected by FXMLLoader

   /** Field description */
   TerminologyContext currentContext;

   /** Field description */
   Lookup.Result<TerminologyContext> contextLookupResult;

   /**
    * Method description
    *
    *
    * @param contextConcept
    */
   private void changeConcept(ConceptChronicleDdo contextConcept) {
      if (contextConcept != null) {
          try {
              
              UUID[] userUuidArray = (UUID[]) Looker.lookup(SessionAttributes.class).get(SessionAttributeKeys.USER_UUID_ARRAY);
              int authorNid = Ts.get().getNidForUuids(userUuidArray);
              UUID[] moduleUuids = (UUID[]) Looker.lookup(SessionAttributes.class).get(SessionAttributeKeys.EDIT_MODULE_UUID_ARRAY);
              int moduleNid = Ts.get().getNidForUuids(moduleUuids);
              NidSetBI editPathNidSet = new NidSet();
              editPathNidSet.add(Ts.getGlobalSnapshot().getViewCoordinate().getViewPosition().getPath().getConceptNid());
              // TODO how to specify the module nid? Should it be dynamic? Drools should specify the module nid in the action?
              
              EditCoordinate editCoordinate = new EditCoordinate(authorNid, moduleNid, editPathNidSet);
              //System.out.println(contextConcept.toXml());
              //System.out.println(Ts.get().getConcept(contextConcept.getPrimordialUuid()).toLongString());
              ConceptVersionBI concept = Ts.getGlobalSnapshot().getConceptVersion(contextConcept.getConceptReference().getNid());
              DefinitionTree dt = new DefinitionTree(concept, DescriptionLogicBinding.EL_PLUS_PLUS.getNid());
              //System.out.println(dt.dfsPrint());
             textField.setText(contextConcept.toString());
             definitionPane.setDefinitionTree(dt, editCoordinate);
          } catch (IOException | ContradictionException ex) {
              Logger.getLogger(ConceptDetailsViewController.class.getName()).log(Level.SEVERE, null, ex);
          }
      } else {
         textField.setText("No selection");
      }
   }

   /**
    * Method description
    *
    *
    * @param ov
    * @param oldValue
    * @param newValue
    */
   @Override
   public void changed(ObservableValue<? extends ConceptChronicleDdo> ov, ConceptChronicleDdo oldValue,
                       final ConceptChronicleDdo newValue) {
      Platform.runLater(new Runnable() {
         @Override
         public void run() {
            changeConcept(newValue);
         }
      });
   }

   /**
    * Method description
    *
    *
    * @param newValue
    */
   private void contextChanged(Object newValue) {
      if (currentContext != null) {
         currentContext.getContextProperty().removeListener(this);
         currentContext = null;
      }

      if (newValue instanceof TerminologyContext) {
         currentContext = (TerminologyContext) newValue;
         currentContext.getContextProperty().addListener(this);
         changeConcept(currentContext.getContext());
      }
   }

   /**
    * Method description
    *
    *
    * @param fxmlFileLocation
    * @param resources
    */
   @Override    // This method is called by the FXMLLoader when initialization is complete
   public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
      assert contextComboBox != null :
             "fx:id=\"contextChoiceBox\" was not injected: check your FXML file 'ConceptDetailsView.fxml'.";
      assert textField != null :
             "fx:id=\"textField\" was not injected: check your FXML file 'ConceptDetailsView.fxml'.";
      contextComboBox.setButtonCell(new ContextListCell());
      contextComboBox.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
         @Override
         public ListCell<Object> call(ListView<Object> p) {
            return new ContextListCell();
         }
      });

      // initialize your logic here: all @FXML variables will have been injected
      contextLookupResult = Looker.lookupResult(TerminologyContext.class);
      setupChoiceBox(contextLookupResult);
      contextComboBox.getSelectionModel().selectFirst();
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            contextLookupResult.addLookupListener(ConceptDetailsViewController.this);
         }
      });
      contextComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
         @Override
         public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            contextChanged(newValue);
         }
      });
   }

   /**
    * On swing event thread.
    *
    * @param ev
    */
   @Override
   public void resultChanged(LookupEvent ev) {
      Platform.runLater(new Runnable() {
         @Override
         public void run() {
            Object selectedItem = contextComboBox.getSelectionModel().getSelectedItem();

            setupChoiceBox(contextLookupResult);

            if (selectedItem != null) {
               contextComboBox.getSelectionModel().select(selectedItem);
            }
         }
      });
   }

   /**
    * Method description
    *
    *
    * @param result
    */
   private void setupChoiceBox(Lookup.Result<TerminologyContext> result) {
      contextComboBox.getItems().clear();
      contextComboBox.getItems().add("No linked context");

      for (TerminologyContext c : result.allInstances()) {
         contextComboBox.getItems().add(c);
      }
   }

   /**
    * Class description
    *
    *
    * @version        Enter version here..., 13/04/24
    * @author         Enter your name here...
    */
   static class ContextListCell extends ListCell<Object> {

      /**
       * Method description
       *
       *
       * @param item
       * @param empty
       */
      @Override
      protected void updateItem(Object item, boolean empty) {
         super.updateItem(item, empty);

         if ((item != null) && (item instanceof TerminologyContext)) {
            TerminologyContext context = (TerminologyContext) item;

            setGraphic(context.getGraphic());
            setText(context.getWindowName() + ": " + context.getContextName());
         }
      }
   }
}
