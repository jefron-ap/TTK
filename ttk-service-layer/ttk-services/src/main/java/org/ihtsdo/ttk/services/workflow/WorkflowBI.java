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
package org.ihtsdo.ttk.services.workflow;

import org.ihtsdo.otf.tcc.api.concept.ConceptVersionBI;

/**
 *
 * @author kec
 */
public interface WorkflowBI {
    /**
     * Get a set of possible user selectable actions given a <code>ConceptVersionBI</code>
     * @param cv
     * @return 
     */
    Object[] getActions(ConceptVersionBI cv);
    
    
    /**
     * Get a set of automatic actions if uncommitted changes to a concept version<code>ConceptVersionBI</code> are committed
     * @param cv
     * @return 
     */
    Object[] getAutoCommitActions(ConceptVersionBI cv);
    
    /**
     * Get a string representation of the current workflow states of a given <code>ConceptVersionBI</code>.
     * For example: "waiting for approval", 
     *              "approved", 
     *              "waiting for translation approval", 
     *              "waiting for pharmacy approval".
     * @param cv
     * @return 
     */
    String[] getState(ConceptVersionBI cv);
    
}
