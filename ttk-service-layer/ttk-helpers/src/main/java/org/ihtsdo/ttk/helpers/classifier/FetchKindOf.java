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



package org.ihtsdo.ttk.helpers.classifier;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.otf.tcc.api.concept.ConceptFetcherBI;
import org.ihtsdo.otf.tcc.api.concept.ProcessUnfetchedConceptDataBI;
import org.ihtsdo.otf.tcc.api.store.TerminologyStoreDI;
import org.ihtsdo.otf.tcc.api.store.Ts;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ihtsdo.otf.tcc.api.nid.NativeIdSetBI;

/**
 *
 * @author kec
 */
public class FetchKindOf implements ProcessUnfetchedConceptDataBI {

   /** Field description */
   private int kindOfNid;

   /** Field description */
   private TerminologyStoreDI store;

   /** Field description */
   private ViewCoordinate vc;

   /** Field description */
   private NativeIdSetBI kindOfBitSet;

   /**
    * Constructs ...
    *
    *
    * @param kindOfNid
    * @param vc
    *
    * @throws IOException
    */
   public FetchKindOf(int kindOfNid, ViewCoordinate vc) throws IOException {
      this.kindOfNid    = kindOfNid;
      this.vc           = vc;
      this.store        = Ts.get();
      this.kindOfBitSet = store.getEmptyNidSet();
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public boolean allowCancel() {
      return false;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public boolean continueWork() {
      return true;
   }

   /**
    * Method description
    *
    *
    * @param cNid
    * @param fetcher
    *
    * @throws Exception
    */
   @Override
   public void processUnfetchedConceptData(int cNid, ConceptFetcherBI fetcher) throws Exception {
      if (store.isKindOf(cNid, kindOfNid, vc)) {
         kindOfBitSet.setMember(cNid);
      }
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public NativeIdSetBI getKindOfBitSet() {
      return kindOfBitSet;
   }

   /**
    * Method description
    *
    *
    * @return
    *
    * @throws IOException
    */
   @Override
   public NativeIdSetBI getNidSet() throws IOException {
      return null;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public String getTitle() {
      try {
         return "Fetching kind-of: " + store.getConcept(kindOfNid).toString();
      } catch (IOException ex) {
         Logger.getLogger(FetchKindOf.class.getName()).log(Level.SEVERE, null, ex);
      }

      return "Fetching kind-of: " + kindOfNid;
   }
}
