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



package org.ihtsdo.ttk.pl.nb.progress;

//~--- non-JDK imports --------------------------------------------------------

import javafx.concurrent.Worker;

import org.ihtsdo.otf.tcc.ddo.progress.AggregateProgressItem;
import org.ihtsdo.otf.tcc.lookup.InstanceWrapper;
import org.ihtsdo.otf.tcc.lookup.properties.AllowItemCancel;
import org.ihtsdo.otf.tcc.lookup.properties.ShowGlobalTaskProgress;

import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashSet;

/**
 *
 * @author kec
 */
public class WorkerListener implements LookupListener {

   /** Field description */
   HashSet<Lookup.Item> handledItems = new HashSet<>();

   /**
    * Constructs ...
    *
    *
    * @param initialResults
    */
   public WorkerListener(Lookup.Result<Worker> initialResults) {
      handleResults(initialResults);
   }

   /**
    * Method description
    *
    *
    * @param result
    */
   private void handleResults(Lookup.Result result) {
      HashSet<Lookup.Item> newItems = new HashSet<>(result.allItems()); 
      //System.out.println("Lookup results: " + result.allItems());
      newItems.removeAll(handledItems);
      handledItems.retainAll(result.allItems());    // remove old items

      for (Lookup.Item item : newItems) {
         if (item instanceof InstanceWrapper) {
            InstanceWrapper wrapper = (InstanceWrapper) item;

            if (wrapper.getInstanceProperties().contains(new ShowGlobalTaskProgress())) {
               boolean cancelable = wrapper.getInstanceProperties().contains(new AllowItemCancel());

               if (item.getInstance() instanceof AggregateProgressItem) {
                  AggregateProgressItem aggregateProgressItem = (AggregateProgressItem) item.getInstance();

                  WorkerListenerProgressItem.create(aggregateProgressItem, cancelable);

                  for (Worker w : aggregateProgressItem.getSubordinates()) {
                     WorkerListenerProgressItem.create(w, cancelable);
                  }
               } else if (item.getInstance() instanceof Worker) {    // single progress item
                  Worker worker = (Worker) item.getInstance();

                  WorkerListenerProgressItem.create(worker, cancelable);
               }                                                     // else not a worker.
            }
         }
      }

      handledItems.addAll(newItems);
   }

   /**
    * Method description
    *
    *
    * @param ev
    */
   @Override
   public final void resultChanged(LookupEvent ev) {
      System.out.println("WorkerListener resultChanged: " + ev);
      Lookup.Result result = (Lookup.Result) ev.getSource();

      handleResults(result);
   }
}
