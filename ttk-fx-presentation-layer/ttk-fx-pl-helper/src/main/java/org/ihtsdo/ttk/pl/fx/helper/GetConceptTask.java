
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.ttk.pl.fx.helper;

//~--- non-JDK imports --------------------------------------------------------

import javafx.concurrent.Task;

import org.ihtsdo.otf.tcc.ddo.concept.ConceptChronicleDdo;
import org.ihtsdo.otf.tcc.ddo.fetchpolicy.RefexPolicy;
import org.ihtsdo.otf.tcc.ddo.fetchpolicy.RelationshipPolicy;
import org.ihtsdo.otf.tcc.ddo.fetchpolicy.VersionPolicy;

//~--- JDK imports ------------------------------------------------------------

import java.util.UUID;
import org.ihtsdo.otf.tcc.ddo.store.FxTs;

/**
 *
 * @author kec
 */
public class GetConceptTask extends Task<ConceptChronicleDdo> {
   private VersionPolicy      versionPolicy      = VersionPolicy.ALL_VERSIONS;
   private RefexPolicy        refexPolicy        = RefexPolicy.REFEX_MEMBERS;
   private RelationshipPolicy relationshipPolicy =
      RelationshipPolicy.ORIGINATING_AND_DESTINATION_RELATIONSHIPS;
   private UUID conceptUuid;
   private UUID viewCoordinateUuid;

   public GetConceptTask(UUID conceptUuid, UUID viewCoordinateUuid) {
      this.conceptUuid        = conceptUuid;
      this.viewCoordinateUuid = viewCoordinateUuid;
   }

   public GetConceptTask(UUID conceptUuid, VersionPolicy versionPolicy, RefexPolicy refexPolicy,
                         RelationshipPolicy relationshipPolicy, UUID viewCoordinateUuid) {
      this.conceptUuid        = conceptUuid;
      this.viewCoordinateUuid = viewCoordinateUuid;
   }

   @Override
   public ConceptChronicleDdo call() throws Exception {
      return FxTs.get().getFxConcept(conceptUuid, viewCoordinateUuid, versionPolicy,
          refexPolicy, relationshipPolicy);
   }
}
