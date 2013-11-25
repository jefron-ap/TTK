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



package org.ihtsdo.ttk.pl.nb;

//~--- non-JDK imports --------------------------------------------------------

import javafx.concurrent.Worker;

import javafx.embed.swing.JFXPanel;

import org.ihtsdo.otf.tcc.lookup.Looker;
import org.ihtsdo.otf.tcc.lookup.TtkEnvironment;
import org.ihtsdo.ttk.pl.nb.progress.WorkerListener;

import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;

//~--- JDK imports ------------------------------------------------------------

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

/**
 * Class description
 *
 *
 * @version        Enter version here..., 13/04/18
 * @author         Enter your name here...    
 */
public class Installer extends ModuleInstall {

    private static WorkerListener workerListener;
    private static Lookup.Result<Worker> lookupResult;
   /**
    * Method description
    *
    */
   @Override
   public void restored() {
       try {
           final CountDownLatch latch = new CountDownLatch(1);

           SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                 new JFXPanel();    // initializes JavaFX environment
                 latch.countDown();
              }
           });
           latch.await();
           Looker.lookup(TtkEnvironment.class).setUseFxWorkers(true);
           
           // Setup integration of FX progress info with Netbeans progress info. 

           lookupResult = Looker.lookupResult(Worker.class);
           workerListener = new WorkerListener(lookupResult);
           lookupResult.addLookupListener(workerListener);
       } catch (InterruptedException ex) {
           Exceptions.printStackTrace(ex);
       }
   }
}
