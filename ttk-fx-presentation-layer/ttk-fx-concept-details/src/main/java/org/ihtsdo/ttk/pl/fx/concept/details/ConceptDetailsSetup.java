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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.fxml.FXMLLoader;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kec
 */
public class ConceptDetailsSetup implements EventHandler<ActionEvent> {

   /** Field description */
   final BorderPane borderPane;

   /**
    * Constructs ...
    *
    *
    * @param borderPane
    */
   public ConceptDetailsSetup(BorderPane borderPane) {
      this.borderPane = borderPane;
   }

   /**
    * Method description
    *
    *
    * @param t
    */
   @Override
   public void handle(ActionEvent t) {
      try {
            
          
         Pane conceptDetailsPane = (Pane) FXMLLoader.load(
                                       getClass().getResource(
                                          "/org/ihtsdo/ttk/pl/fx/concept/details/fxml/ConceptDetailsView.fxml"));

         borderPane.setCenter(conceptDetailsPane);
      } catch (IOException ex) {
         Logger.getLogger(ConceptDetailsSetup.class.getName()).log(Level.SEVERE, null, ex);
      } catch (Exception ex) {
           Logger.getLogger(ConceptDetailsSetup.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
}
