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
package org.ihtsdo.ttk.bdb.nbm;

import org.ihtsdo.otf.tcc.api.store.TerminologyStoreDI;
import org.ihtsdo.otf.tcc.lookup.Hk2Looker;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {
    private static TerminologyStoreDI store;
    @Override
    public void restored() {
      System.setProperty("org.ihtsdo.otf.tcc.datastore.bdb-location", "berkeley-db");
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                store = Hk2Looker.get().getService(TerminologyStoreDI.class);
            }
        }).start();
     }
}
