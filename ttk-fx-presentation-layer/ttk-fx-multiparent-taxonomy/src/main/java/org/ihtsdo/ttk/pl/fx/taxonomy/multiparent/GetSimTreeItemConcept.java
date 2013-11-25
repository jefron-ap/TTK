
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.ttk.pl.fx.taxonomy.multiparent;

//~--- non-JDK imports --------------------------------------------------------

import javafx.application.Platform;

import org.ihtsdo.otf.tcc.ddo.ComponentReference;
import org.ihtsdo.otf.tcc.ddo.TaxonomyReferenceWithConcept;
import org.ihtsdo.otf.tcc.ddo.concept.ConceptChronicleDdo;
import org.ihtsdo.otf.tcc.ddo.concept.component.relationship.RelationshipChronicleDdo;
import org.ihtsdo.otf.tcc.ddo.concept.component.relationship.RelationshipVersionDdo;
import org.ihtsdo.otf.tcc.ddo.fetchpolicy.RefexPolicy;
import org.ihtsdo.otf.tcc.ddo.fetchpolicy.RelationshipPolicy;
import org.ihtsdo.otf.tcc.ddo.fetchpolicy.VersionPolicy;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;
import org.ihtsdo.otf.tcc.ddo.store.FxTs;

/**
 *
 * @author kec
 */
class GetSimTreeItemConcept implements Callable<Boolean> {
   ArrayList<SimTreeItem> childrenToAdd      = new ArrayList<>();
   boolean                addChildren        = true;
   VersionPolicy          versionPolicy      = VersionPolicy.ACTIVE_VERSIONS;
   RelationshipPolicy     relationshipPolicy =
      RelationshipPolicy.ORIGINATING_AND_DESTINATION_TAXONOMY_RELATIONSHIPS;
   RefexPolicy refexPolicy = RefexPolicy.ANNOTATION_MEMBERS;
   ConceptChronicleDdo   concept;
   SimTreeItem treeItem;

   public GetSimTreeItemConcept(SimTreeItem treeItem) {
      this(treeItem, true);
   }

   public GetSimTreeItemConcept(SimTreeItem treeItem, boolean addChildren) {
      this.treeItem    = treeItem;
      this.addChildren = addChildren;
   }

   public GetSimTreeItemConcept(SimTreeItem treeItem, VersionPolicy versionPolicy, RefexPolicy refexPolicy,
                                RelationshipPolicy relationshipPolicy) {
      this.treeItem           = treeItem;
      this.versionPolicy      = versionPolicy;
      this.refexPolicy        = refexPolicy;
      this.relationshipPolicy = relationshipPolicy;
      assert treeItem.getValue().getRelationshipVersion().getViewCoordinateUuid() != null: 
              "treeItem.getValue() relationshipVersion viewCoordinate is null: " + treeItem.getValue();
   }

   @Override
   public Boolean call() throws Exception {
      ComponentReference reference;

      if (addChildren) {
         reference = treeItem.getValue().getRelationshipVersion().getOriginReference();
      } else {
         reference = treeItem.getValue().getRelationshipVersion().getDestinationReference();
      }

      concept = FxTs.get().getFxConcept(reference,
          treeItem.getValue().getRelationshipVersion().getViewCoordinateUuid(), versionPolicy, refexPolicy,
          relationshipPolicy);

      if ((concept.getConceptAttributes() == null) || concept.getConceptAttributes().getVersions().isEmpty()
          || concept.getConceptAttributes().getVersions().get(0).isDefined()) {
         treeItem.setDefined(true);
      }

      if (concept.getOriginRelationships().size() > 1) {
         treeItem.setMultiParent(true);
      }

      if (addChildren) {
         for (RelationshipChronicleDdo fxrc : concept.getDestinationRelationships()) {
            for (RelationshipVersionDdo rv : fxrc.getVersions()) {
               TaxonomyReferenceWithConcept fxtrc     = new TaxonomyReferenceWithConcept(rv);
               SimTreeItem                    childItem = new SimTreeItem(fxtrc);

               childrenToAdd.add(childItem);
            }
         }
      }

      Collections.sort(childrenToAdd);
      Platform.runLater(new Runnable() {
         @Override
         public void run() {
            TaxonomyReferenceWithConcept itemValue = treeItem.getValue();

            treeItem.setValue(null);
            treeItem.getChildren().clear();
            treeItem.getChildren().addAll(childrenToAdd);
            treeItem.setValue(itemValue);
            treeItem.getValue().conceptProperty().set(concept);
            treeItem.computeGraphic();
         }
      });

      return true;
   }
}
