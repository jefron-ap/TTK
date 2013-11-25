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



package org.ihtsdo.ttk.concept.nbm;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.otf.tcc.model.cc.P;
import org.ihtsdo.otf.tcc.model.cc.termstore.PersistentStoreI;
import org.ihtsdo.otf.tcc.ddo.store.FxTs;

import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;

/**
 * Class description
 *
 *
 * @version        Enter version here..., 13/04/08
 * @author         Enter your name here...    
 */
public class Installer extends ModuleInstall {

   /**
    * Method description
    *
    */
   @Override
   public void restored() {
      PersistentStoreI persistentStore = Lookup.getDefault().lookup(PersistentStoreI.class);

      P.s = persistentStore;
      FxTs.set(P.s);
      System.out.println("Set PersistentStoreI");
   }
}
