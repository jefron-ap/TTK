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



package org.ihtsdo.ttk.helpers.metrics;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.otf.tcc.api.chronicle.ComponentChronicleBI;
import org.ihtsdo.otf.tcc.api.concept.ConceptFetcherBI;
import org.ihtsdo.otf.tcc.api.chronicle.ProcessComponentChronicleBI;
import org.ihtsdo.otf.tcc.api.concept.ProcessUnfetchedConceptDataBI;
import org.ihtsdo.otf.tcc.api.concept.ConceptChronicleBI;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.ihtsdo.otf.tcc.api.nid.NativeIdSetBI;

/**
 *
 * @author kec
 */
public class SequentialFetchMetrics implements ProcessComponentChronicleBI, ProcessUnfetchedConceptDataBI {
   AtomicInteger                        processedConceptCount   = new AtomicInteger(0);
   AtomicInteger                        processedComponentCount = new AtomicInteger(0);
   ConcurrentSkipListMap<Integer, Long> processChronicleTime    = new ConcurrentSkipListMap<>();
   long                                 metricsStart            = System.currentTimeMillis();
   ConcurrentSkipListMap<Integer, Long> fetchTime               = new ConcurrentSkipListMap<>();

   //~--- methods -------------------------------------------------------------

   @Override
   public boolean continueWork() {
      return true;
   }

   @Override
   public void process(ComponentChronicleBI cc) throws Exception {
      processedComponentCount.incrementAndGet();
   }

   @Override
   public void processUnfetchedConceptData(int cNid, ConceptFetcherBI fetcher) throws Exception {
      processedConceptCount.incrementAndGet();

      long               fetchStart = System.nanoTime();
      ConceptChronicleBI cc         = fetcher.fetch();
      long               fetchEnd   = System.nanoTime();

      fetchTime.put(cNid, fetchEnd - fetchStart);

      long processComponentStart = System.nanoTime();

      cc.processComponentChronicles(this);

      long processComponentEnd = System.nanoTime();

      processChronicleTime.put(cNid, processComponentEnd - processComponentStart);
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public NativeIdSetBI getNidSet() throws IOException {
      return null;
   }

    @Override
    public boolean allowCancel() {
        return false;
    }

    @Override
    public String getTitle() {
        return "Sequential fetch metrics test";
    }
}
