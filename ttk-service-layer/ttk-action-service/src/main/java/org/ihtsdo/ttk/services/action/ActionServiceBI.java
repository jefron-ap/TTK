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

import javafx.collections.ObservableList;
import javafx.event.Event;

/**
 *
 * @author kec
 */
public interface ActionServiceBI {
    
    ObservableList<ActionBI> insertTargetContext(ActionContextBI target);
    void retractTargetContext(ActionContextBI target);

    ObservableList<ActionBI> insertSourceContext(ActionContextBI target);
    void retractSourceContext(ActionContextBI target);

    void processEvent(Event event);
}
