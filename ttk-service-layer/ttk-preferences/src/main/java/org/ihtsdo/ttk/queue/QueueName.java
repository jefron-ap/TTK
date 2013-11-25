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



package org.ihtsdo.ttk.queue;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.ttk.preferences.EnumBasedPreferences;
import org.ihtsdo.ttk.preferences.PreferenceObject;
import org.ihtsdo.ttk.preferences.PreferenceWithDefaultEnumBI;

//~--- JDK imports ------------------------------------------------------------

import java.util.Objects;

/**
 *
 * @author kec
 */
public class QueueName implements PreferenceObject {
    private String name;

    public QueueName() {
        this.name = Fields.QUEUE_NAME.getDefaultValue();
    }

    public QueueName(EnumBasedPreferences preferences) {
        this.name = preferences.get(Fields.QUEUE_NAME);
    }

    public QueueName(String name) {
        this.name = name;
    }

    enum Fields implements PreferenceWithDefaultEnumBI {
        QUEUE_NAME;

        @Override
        public String getDefaultValue() {
            return "user inbox";
        }
    }

    ;
    @Override
    public void exportFields(EnumBasedPreferences preferences) {
        preferences.put(Fields.QUEUE_NAME, this.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 53 * hash + Objects.hashCode(this.name);

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final QueueName other = (QueueName) obj;

        if (!Objects.equals(this.name, other.name)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "QueueName: " + name;
    }
}
