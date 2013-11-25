/*
 * Copyright 2012 International Health Terminology Standards Development Organisation.
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
package org.ihtsdo.ttk.services.constraint;

import javafx.collections.ObservableList;
import org.ihtsdo.otf.tcc.api.concept.ConceptVersionBI;

/**
 * May include QA constraints, definitional constraints (precoordination or postcoordination)
 * @TODO -- How can we store constraints in refsets...
 * @TODO -- Should/Must the constraints inherit along the definitional taxonomy
 * @author kec
 */
public interface ConstraintBI {
    public enum MessageType { INFORMATION, WARNING, ERROR };
    public class Message {
        MessageType messageType;
        int componentNid;
        String message;
        ObservableList<Object> fixups;
    }
    
    ObservableList<Message> getConstraintMessages(ConceptVersionBI cv);
    
    ObservableList<Object> getRefinements(ConceptVersionBI cv, int componentNid);
}
