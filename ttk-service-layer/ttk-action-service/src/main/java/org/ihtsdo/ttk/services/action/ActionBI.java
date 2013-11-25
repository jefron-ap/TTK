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
package org.ihtsdo.ttk.services.action;

/**
 *
 * @author kec
 */
public interface ActionBI {
    enum ExecutionCriterion { USER_SELECTION, IMMEDIATE };
    enum ThreadForAction { SWING_EVENT_THREAD, FX_EVENT_THREAD, BACKGROUND_THREAD };
    enum ExecutionState { READY, EXECUTING, COMPLETED, FAILED };
    
    ExecutionCriterion getExecutionCriterion();
    ThreadForAction getThreadForAction();
    ExecutionState getExecutionState();
    String getLabel();
    String getDescription();
    
    
}
