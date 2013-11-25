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
package org.ihtsdo.ttk.pl.nb;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javax.swing.Timer;
import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;
import org.ihtsdo.otf.tcc.api.store.Ts;
import org.ihtsdo.otf.tcc.api.coordinate.StandardViewCoordinates;
import org.ihtsdo.otf.tcc.api.metadata.binding.Taxonomies;
import org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding;
import org.ihtsdo.otf.tcc.ddo.TaxonomyReferenceWithConcept;
import org.ihtsdo.otf.tcc.ddo.concept.ConceptChronicleDdo;
import org.ihtsdo.otf.tcc.ddo.fetchpolicy.RefexPolicy;
import org.ihtsdo.otf.tcc.ddo.fetchpolicy.RelationshipPolicy;
import org.ihtsdo.otf.tcc.ddo.fetchpolicy.VersionPolicy;
import org.ihtsdo.otf.tcc.ddo.store.FxTs;
import org.ihtsdo.otf.tcc.lookup.Looker;
import org.ihtsdo.otf.tcc.lookup.TermstoreLatch;
import org.ihtsdo.ttk.pl.fx.helper.GetConceptService;
import org.ihtsdo.ttk.pl.fx.taxonomy.multiparent.SimTreeCell;
import org.ihtsdo.ttk.pl.fx.taxonomy.multiparent.SimTreeIcons;
import org.ihtsdo.ttk.pl.fx.taxonomy.multiparent.SimTreeItem;
import org.ihtsdo.ttk.pl.fx.taxonomy.multiparent.TaxonomyProgressIndicatorSkin;
import org.ihtsdo.ttk.pl.fx.taxonomy.multiparent.TaxonomyView;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.ihtsdo.ttk.pl.nb//Taxonomy//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "TaxonomyTopComponent",
        iconBase = "org/ihtsdo/ttk/pl/nb/node.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "org.ihtsdo.ttk.pl.nb.TaxonomyTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_TaxonomyAction",
        preferredID = "TaxonomyTopComponent")
@Messages({
    "CTL_TaxonomyAction=Taxonomy",
    "CTL_TaxonomyTopComponent=Taxonomy Window",
    "HINT_TaxonomyTopComponent=This is a Taxonomy window"
})
public final class TaxonomyTopComponent extends TopComponent {

    JFXPanel fxPanel;
    DbStartupListener dbStartupListener;
    GetConceptService conceptService = new GetConceptService();

    private class DbStartupListener implements ActionListener {

        Timer timer = new Timer(1000, this);

        {
            timer.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (Looker.lookup(TermstoreLatch.class).ready()) {
                timer.removeActionListener(DbStartupListener.this);
                timer.stop();
                timer = null;
                dbStartupListener = null;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Ts.get().putViewCoordinate(StandardViewCoordinates.getSnomedStatedLatest());
                            Ts.get().putViewCoordinate(StandardViewCoordinates.getSnomedInferredLatest());
                            Ts.get().putViewCoordinate(StandardViewCoordinates.getSnomedInferredThenStatedLatest());
                            Ts.get().setGlobalSnapshot(Ts.get().getSnapshot(StandardViewCoordinates.getSnomedInferredThenStatedLatest()));
                            initFX(fxPanel);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                });
            }
        }
    }

    public TaxonomyTopComponent() {

        StatusDisplayer.getDefault().setStatusText("Loading database and interface...");
        initComponents();
        setName(Bundle.CTL_TaxonomyTopComponent());
        setToolTipText(Bundle.HINT_TaxonomyTopComponent());
        fxPanel = new JFXPanel();
        setLayout(new BorderLayout());
        add(fxPanel, BorderLayout.CENTER);
        dbStartupListener = new DbStartupListener();
    }
private void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    private Scene createScene() {
        TreeView<TaxonomyReferenceWithConcept> treeView = new TaxonomyView("taxonomy selection","main");
        try {

            treeView.setCellFactory(new Callback<TreeView<TaxonomyReferenceWithConcept>, TreeCell<TaxonomyReferenceWithConcept>>() {
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
                        ConceptChronicleDdo concept = simTreeItem.getValue().getConcept();
                        conceptService.setConceptUuid(simTreeItem.getValue().getConcept().getPrimordialUuid());
                        conceptService.setViewCoordinateUuid(concept.getViewCoordinateUuid());
                        conceptService.restart();
                    }
                }
            });
            treeView.setShowRoot(false);


            TaxonomyReferenceWithConcept root = new TaxonomyReferenceWithConcept();
            SimTreeItem rootItem = new SimTreeItem(root);
            TaxonomyReferenceWithConcept snomedRoot = new TaxonomyReferenceWithConcept();
            snomedRoot.setConcept(FxTs.get().getFxConcept(Taxonomies.SNOMED.getUuids()[0],
                    StandardViewCoordinates.getSnomedInferredLatest(), VersionPolicy.ACTIVE_VERSIONS,
                    RefexPolicy.REFEX_MEMBERS,
                    RelationshipPolicy.ORIGINATING_AND_DESTINATION_TAXONOMY_RELATIONSHIPS));
            TaxonomyReferenceWithConcept dlRoot = new TaxonomyReferenceWithConcept();
            dlRoot.setConcept(FxTs.get().getFxConcept(DescriptionLogicBinding.DESCRIPTION_LOGIC_AUXILIARY.getUuids()[0],
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
                    SimTreeItem sourceTreeItem = (SimTreeItem) t.getSource();
                    ProgressIndicator p2 = new ProgressIndicator();

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
                    SimTreeItem sourceTreeItem = (SimTreeItem) t.getSource();
                    ProgressIndicator p2 = new ProgressIndicator();

                    p2.setSkin(new TaxonomyProgressIndicatorSkin(p2));
                    p2.setPrefSize(16, 16);
                    p2.setProgress(-1);
                    sourceTreeItem.setProgressIndicator(p2);
                    sourceTreeItem.addChildrenConceptsAndGrandchildrenItems(p2);
                }
            });
            treeView.setRoot(rootItem);



        } catch (IOException | ContradictionException ex) {
            Exceptions.printStackTrace(ex);
        }
        BorderPane border = new BorderPane();
        border.setCenter(treeView);
        return new Scene(border);
    }
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this
     * code. The content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
