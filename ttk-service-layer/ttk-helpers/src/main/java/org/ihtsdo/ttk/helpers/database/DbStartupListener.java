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



package org.ihtsdo.ttk.helpers.database;

//~--- non-JDK imports --------------------------------------------------------

import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import org.ihtsdo.otf.tcc.api.store.Ts;
import org.ihtsdo.otf.tcc.api.coordinate.StandardViewCoordinates;
import org.ihtsdo.ttk.helpers.timer.TtkTimer;
import org.ihtsdo.otf.tcc.lookup.Looker;
import org.ihtsdo.otf.tcc.lookup.TermstoreLatch;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.nio.file.Path;

import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class description
 *
 *
 * @version        Enter version here..., 13/05/08
 * @author         Enter your name here...
 */
public class DbStartupListener extends TimerTask {

   /** Field description */
   private static AtomicBoolean startupInitiated = new AtomicBoolean(false);

   /** Field description */
   EventHandler<ActionEvent>[] handlers;
   
   boolean embedded;

   Path[] initFiles;

   /**
    * Constructs ...
    *
    *
    * @param handlers
    */
   public DbStartupListener(boolean embedded, 
           EventHandler<ActionEvent>... handlers) {
      this(embedded, null, handlers);
   }
   public DbStartupListener(boolean embedded, Path[] initFiles, 
           EventHandler<ActionEvent>... handlers) {
      this.handlers = handlers;
      this.embedded = embedded;
      this.initFiles = initFiles;
      if (startupInitiated.compareAndSet(false, true)) {
         new Thread(new Runnable() {
            @Override
            public void run() {
               try {
                   if (DbStartupListener.this.embedded) {
                       if (DbStartupListener.this.initFiles != null) {
                           Ts.setupEmbedded();
                           Ts.get().loadEconFiles(DbStartupListener.this.initFiles);
                       } else {
                           Ts.setupEmbedded();
                       }
                        
                   } else {
                        Ts.setupClient();
                        Looker.lookup(TermstoreLatch.class).openLatch();
                   }
               } catch (Exception ex) {
                  Logger.getLogger(DbStartupListener.class.getName()).log(Level.SEVERE, null, ex);
               }
            }
         }).start();
      }
      TtkTimer.schedule(this, 200, 200);
   }

   /**
    * Method description
    *
    */
   @Override
   public void run() {
      if (Looker.lookup(TermstoreLatch.class).ready()) {
         this.cancel();
         Platform.runLater(new Runnable() {
            @Override
            public void run() {
               try {
                  Ts.get().putViewCoordinate(StandardViewCoordinates.getSnomedStatedLatest());
                  Ts.get().putViewCoordinate(StandardViewCoordinates.getSnomedInferredLatest());
                  Ts.get().putViewCoordinate(StandardViewCoordinates.getSnomedInferredThenStatedLatest());
                  Ts.get().setGlobalSnapshot(
                      Ts.get().getSnapshot(StandardViewCoordinates.getSnomedInferredThenStatedLatest()));
                  Logger.getLogger(DbStartupListener.class.getName()).log(Level.INFO,
                                   "Database startup complete.");

                  ActionEvent event = new ActionEvent(this, null);

                  for (EventHandler<ActionEvent> h : handlers) {
                     h.handle(event);
                  }
               } catch (IOException ex) {
                  Logger.getLogger(DbStartupListener.class.getName()).log(Level.SEVERE, null, ex);
               }

               handlers = null;
            }
         });
      }
   }
}
