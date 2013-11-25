/*
 * Copyright 2013 VA Office of Informatics and Analytics.
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
package org.ihtsdo.ttk.fx.app;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;
import org.ihtsdo.otf.tcc.api.store.TerminologySnapshotDI;
import org.ihtsdo.otf.tcc.api.store.Ts;
import org.ihtsdo.otf.tcc.api.concept.ConceptVersionBI;
import org.ihtsdo.otf.tcc.api.coordinate.StandardViewCoordinates;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;
import org.ihtsdo.otf.tcc.api.metadata.binding.Snomed;
import org.ihtsdo.otf.tcc.dto.chronicle.writer.ExternalChronicleWriterZip;

/**
 *
 * @author kec
 */
public class ConceptTest implements EventHandler<ActionEvent> {

    @Override
    public void handle(ActionEvent t) {
        //ExternalChronicleWriterZip.main(null);
        try {
            System.out.println("Staring action test");
            ViewCoordinate sil = StandardViewCoordinates.getSnomedInferredLatest();
            ConceptVersionBI asthma = Snomed.ALLERGIC_ASTHMA.getStrict(sil);
            ConceptVersionBI respiratory = Snomed.RESPIRATORY_DISORDER.getStrict(sil);
            System.out.println("Is asthma a kind-of respiratory disorder: " + asthma.isKindOf(respiratory));
            
            TerminologySnapshotDI ts = Ts.get().getSnapshot(sil);
            
            asthma = ts.getConceptForNid(Snomed.ALLERGIC_ASTHMA.getNid());
            respiratory = ts.getConceptForNid(Snomed.RESPIRATORY_DISORDER.getNid());
            System.out.println("[2] Is asthma a kind-of respiratory disorder: " + asthma.isKindOf(respiratory));
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(ConceptTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ContradictionException ex) {
            Logger.getLogger(ConceptTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
