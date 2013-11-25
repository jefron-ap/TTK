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



package org.ihtsdo.ttk.pl.fx.taxonomy.multiparent;

//~--- non-JDK imports --------------------------------------------------------

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

import javafx.util.Callback;

import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;
import org.ihtsdo.otf.tcc.api.coordinate.StandardViewCoordinates;
import org.ihtsdo.otf.tcc.api.metadata.binding.Taxonomies;
import org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding;
import org.ihtsdo.otf.tcc.ddo.TaxonomyReferenceWithConcept;
import org.ihtsdo.otf.tcc.ddo.concept.ConceptChronicleDdo;
import org.ihtsdo.otf.tcc.ddo.fetchpolicy.RefexPolicy;
import org.ihtsdo.otf.tcc.ddo.fetchpolicy.RelationshipPolicy;
import org.ihtsdo.otf.tcc.ddo.fetchpolicy.VersionPolicy;
import org.ihtsdo.otf.tcc.ddo.store.FxTs;
import org.ihtsdo.ttk.pl.fx.helper.GetConceptService;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;

/**
 *
 * @author kec
 */
public class TaxonomySetup implements EventHandler<ActionEvent> {

   /** Field description */
   private GetConceptService conceptService = new GetConceptService();

   /** Field description */
   final BorderPane borderPane;

   /**
    * Constructs ...
    *
    *
    * @param fxPanel
    */
   public TaxonomySetup(BorderPane borderPane) {
      this.borderPane = borderPane;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private Node createTaxonomyNode() {
      TreeView<TaxonomyReferenceWithConcept> treeView = new TaxonomyView("taxonomy selection", "main");

      try {
         treeView.setCellFactory(new Callback<TreeView<TaxonomyReferenceWithConcept>,
             TreeCell<TaxonomyReferenceWithConcept>>() {
            @Override
            public TreeCell<TaxonomyReferenceWithConcept> call(TreeView<TaxonomyReferenceWithConcept> p) {
               return new SimTreeCell();
            }
         });
         treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
         treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
               if (newValue instanceof SimTreeItem) {
                  SimTreeItem simTreeItem = (SimTreeItem) newValue;
                  ConceptChronicleDdo   concept     = simTreeItem.getValue().getConcept();

                  conceptService.setConceptUuid(simTreeItem.getValue().getConcept().getPrimordialUuid());
                  conceptService.setViewCoordinateUuid(concept.getViewCoordinateUuid());
                  conceptService.restart();
               }
            }
         });
         treeView.setShowRoot(false);

         TaxonomyReferenceWithConcept root       = new TaxonomyReferenceWithConcept();
         SimTreeItem                    rootItem   = new SimTreeItem(root);
         TaxonomyReferenceWithConcept snomedRoot = new TaxonomyReferenceWithConcept();

         snomedRoot.setConcept(FxTs.get().getFxConcept(Taxonomies.SNOMED.getUuids()[0],
             StandardViewCoordinates.getSnomedInferredLatest(), VersionPolicy.ACTIVE_VERSIONS,
             RefexPolicy.REFEX_MEMBERS,
             RelationshipPolicy.ORIGINATING_AND_DESTINATION_TAXONOMY_RELATIONSHIPS));

         TaxonomyReferenceWithConcept dlRoot = new TaxonomyReferenceWithConcept();

         dlRoot.setConcept(
             FxTs.get().getFxConcept(
                DescriptionLogicBinding.DESCRIPTION_LOGIC_AUXILIARY.getUuids()[0],
                StandardViewCoordinates.getSnomedStatedLatest(), VersionPolicy.ACTIVE_VERSIONS,
                RefexPolicy.REFEX_MEMBERS,
                RelationshipPolicy.ORIGINATING_AND_DESTINATION_TAXONOMY_RELATIONSHIPS));

         SimTreeItem snomedRootItem = new SimTreeItem(snomedRoot, SimTreeIcons.ROOT.getImageView());

         rootItem.getChildren().add(snomedRootItem);

         SimTreeItem dlRootItem = new SimTreeItem(dlRoot, SimTreeIcons.ROOT.getImageView());

         rootItem.getChildren().add(dlRootItem);
         rootItem.setExpanded(true);

         // item.computeGraphic();
         snomedRootItem.addChildren();
         dlRootItem.addChildren();

         // put this event handler on the root
         snomedRootItem.addEventHandler(TreeItem.branchCollapsedEvent(), new EventHandler() {
            @Override
            public void handle(Event t) {

               // remove grandchildren
               SimTreeItem sourceTreeItem = (SimTreeItem) t.getSource();

               sourceTreeItem.removeGrandchildren();
            }
         });
         dlRootItem.addEventHandler(TreeItem.branchCollapsedEvent(), new EventHandler() {
            @Override
            public void handle(Event t) {

               // remove grandchildren
               SimTreeItem sourceTreeItem = (SimTreeItem) t.getSource();

               sourceTreeItem.removeGrandchildren();
            }
         });
         snomedRootItem.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
            @Override
            public void handle(Event t) {

               // add grandchildren
               SimTreeItem       sourceTreeItem = (SimTreeItem) t.getSource();
               ProgressIndicator p2             = new ProgressIndicator();

               p2.setSkin(new TaxonomyProgressIndicatorSkin(p2));
               p2.setPrefSize(16, 16);
               p2.setProgress(-1);
               sourceTreeItem.setProgressIndicator(p2);
               sourceTreeItem.addChildrenConceptsAndGrandchildrenItems(p2);
            }
         });
         dlRootItem.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
            @Override
            public void handle(Event t) {

               // add grandchildren
               SimTreeItem       sourceTreeItem = (SimTreeItem) t.getSource();
               ProgressIndicator p2             = new ProgressIndicator();

               p2.setSkin(new TaxonomyProgressIndicatorSkin(p2));
               p2.setPrefSize(16, 16);
               p2.setProgress(-1);
               sourceTreeItem.setProgressIndicator(p2);
               sourceTreeItem.addChildrenConceptsAndGrandchildrenItems(p2);
            }
         });
         treeView.setRoot(rootItem);
      } catch (IOException | ContradictionException ex) {
         Logger.getLogger(TaxonomySetup.class.getName()).log(Level.SEVERE, null, ex);
      }

      return treeView;
   }

   /**
    * Method description
    *
    *
    * @param t
    */
   @Override
   public void handle(ActionEvent t) {
       borderPane.setCenter(createTaxonomyNode());
   }
}
