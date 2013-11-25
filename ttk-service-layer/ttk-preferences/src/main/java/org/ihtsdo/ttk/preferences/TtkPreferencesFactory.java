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
package org.ihtsdo.ttk.preferences;

import java.io.File;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 *
 * @author kec
 */
public class TtkPreferencesFactory implements PreferencesFactory {
    static final File systemRoot = new File(System.getProperty("java.util.prefs.systemRoot"));
    static final File userRoot = new File(System.getProperty("java.util.prefs.userRoot"));
    
    @Override
    public Preferences systemRoot() {
        return new TtkPreferencesProvider(systemRoot); 
    }

    @Override
    public Preferences userRoot() {
        return new TtkPreferencesProvider(userRoot); 
    }
    
}
