
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.ttk.pl.fx.taxonomy.multiparent;

//~--- non-JDK imports --------------------------------------------------------
import javafx.collections.ObservableList;

import javafx.event.EventHandler;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;

import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ihtsdo.otf.tcc.api.concurrency.FutureHelper;
import org.ihtsdo.otf.tcc.ddo.TaxonomyReferenceWithConcept;
import org.ihtsdo.otf.tcc.ddo.concept.ConceptChronicleDdo;
import org.ihtsdo.otf.tcc.ddo.concept.component.relationship.RelationshipChronicleDdo;
import org.ihtsdo.otf.tcc.ddo.concept.component.relationship.RelationshipVersionDdo;

/**
 *
 * @author kec
 */
public final class SimTreeCell extends TreeCell<TaxonomyReferenceWithConcept> {

    public SimTreeCell() {
        setOnMouseClicked(new ClickListener());
    }

    //~--- methods -------------------------------------------------------------
    private void openOrCloseParent(SimTreeItem treeItem) throws IOException, ContradictionException {
        TaxonomyReferenceWithConcept value = treeItem.getValue();
        ConceptChronicleDdo c = value.getConcept();

        if (c != null) {
            treeItem.setValue(null);

            SimTreeItem parentItem = (SimTreeItem) treeItem.getParent();
            ObservableList<TreeItem<TaxonomyReferenceWithConcept>> siblings = parentItem.getChildren();

            if (treeItem.isSecondaryParentOpened()) {
                removeExtraParents(treeItem, siblings);
            } else {
                ArrayList<RelationshipChronicleDdo> extraParents = new ArrayList<>(c.getOriginRelationships());

                extraParents.remove(value.getRelationshipVersion().getChronicle());

                ArrayList<SimTreeItem> extraParentItems = new ArrayList<>(extraParents.size());

                for (RelationshipChronicleDdo extraParent : extraParents) {
                    for (RelationshipVersionDdo extraParentVersion : extraParent.getVersions()) {
                        SimTreeItem extraParentItem =
                                new SimTreeItem(new TaxonomyReferenceWithConcept(extraParentVersion,
                                TaxonomyReferenceWithConcept.WhichConcept.DESTINATION));
                        ProgressIndicator indicator = new ProgressIndicator();

                        indicator.setSkin(new TaxonomyProgressIndicatorSkin(indicator));
                        indicator.setPrefSize(16, 16);
                        indicator.setProgress(-1);
                        extraParentItem.setGraphic(indicator);
                        extraParentItem.setMultiParentDepth(treeItem.getMultiParentDepth() + 1);
                        extraParentItems.add(extraParentItem);
                    }
                }

                Collections.sort(extraParentItems);
                Collections.reverse(extraParentItems);

                int startIndex = siblings.indexOf(treeItem);

                for (SimTreeItem extraParent : extraParentItems) {
                    parentItem.getChildren().add(startIndex++, extraParent);
                    treeItem.getExtraParents().add(extraParent);
                    GetSimTreeItemConcept fetcher = new GetSimTreeItemConcept(extraParent, false);
                    FutureHelper.addFuture(SimTreeItem.conceptFetcherPool.submit(fetcher));
                }
            }

            treeItem.setValue(value);
            treeItem.setSecondaryParentOpened(!treeItem.isSecondaryParentOpened());
            treeItem.computeGraphic();
        }
    }

    @Override
    protected void updateItem(TaxonomyReferenceWithConcept t, boolean bln) {
        super.updateItem(t, bln);
        double opacity = 0.0;

        if (t != null) {
            final SimTreeItem treeItem = (SimTreeItem) getTreeItem();

            if (treeItem.getMultiParentDepth() > 0) {
                if (treeItem.isLeaf()) {
                    BorderPane graphicBorderPane = new BorderPane();
                    int multiParentInset = treeItem.getMultiParentDepth() * 16;
                    Rectangle leftRect =
                            RectangleBuilder.create().width(multiParentInset).height(16).build();

                    leftRect.setOpacity(opacity);
                    graphicBorderPane.setLeft(leftRect);
                    graphicBorderPane.setCenter(treeItem.computeGraphic());
                    setGraphic(graphicBorderPane);
                }

                setText(t.toString());

                return;
            }

            BorderPane disclosureBorderPane = new BorderPane();

            if (treeItem.isExpanded()) {
                ImageView iv = SimTreeIcons.TAXONOMY_CLOSE.getImageView();

                if (treeItem.getProgressIndicator() != null) {
                    disclosureBorderPane.setCenter(treeItem.getProgressIndicator());
                } else {
                    disclosureBorderPane.setCenter(iv);
                }

                setDisclosureNode(disclosureBorderPane);
            } else {
                ImageView iv = SimTreeIcons.TAXONOMY_OPEN.getImageView();

                if (treeItem.getProgressIndicator() != null) {
                    disclosureBorderPane.setCenter(treeItem.getProgressIndicator());
                } else {
                    disclosureBorderPane.setCenter(iv);
                }

                setDisclosureNode(disclosureBorderPane);
            }

            setText(t.toString());

            BorderPane graphicBorderPane = new BorderPane();

            if (treeItem.isLeaf()) {
                int multiParentInset = treeItem.getMultiParentDepth() * 16;
                Rectangle leftRect =
                        RectangleBuilder.create().width(multiParentInset).height(16).build();

                leftRect.setOpacity(opacity);
                graphicBorderPane.setLeft(leftRect);
                    graphicBorderPane.setCenter(treeItem.computeGraphic());
                setGraphic(graphicBorderPane);
            } else {

                Rectangle leftRect = RectangleBuilder.create().width(6).height(16).build();

                leftRect.setOpacity(opacity);
                graphicBorderPane.setLeft(leftRect);
                    graphicBorderPane.setCenter(treeItem.computeGraphic());
                setGraphic(graphicBorderPane);
            }
        }
    }

    private void removeExtraParents(SimTreeItem treeItem, ObservableList<TreeItem<TaxonomyReferenceWithConcept>> siblings) {
        for (SimTreeItem extraParent : treeItem.getExtraParents()) {
            removeExtraParents(extraParent, siblings);
            siblings.remove(extraParent);
        }
    }

    //~--- inner classes -------------------------------------------------------
    private class ClickListener implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent t) {
            if (getItem() != null) {
                if (getGraphic().getBoundsInParent().contains(t.getX(), t.getY())) {
                    SimTreeItem item = (SimTreeItem) getTreeItem();

                    if (item.isMultiParent() || item.getMultiParentDepth() > 0) {
                        try {
                            openOrCloseParent(item);
                        } catch (ContradictionException | IOException ex) {
                            Logger.getLogger(SimTreeCell.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }
}