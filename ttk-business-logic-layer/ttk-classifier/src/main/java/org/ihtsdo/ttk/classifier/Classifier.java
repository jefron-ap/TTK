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



package org.ihtsdo.ttk.classifier;

//~--- non-JDK imports --------------------------------------------------------

import au.csiro.ontology.Factory;
import au.csiro.ontology.IOntology;
import au.csiro.ontology.axioms.IAxiom;
import au.csiro.ontology.classification.IReasoner;
import au.csiro.ontology.model.IConcept;
import au.csiro.snorocket.core.SnorocketReasoner;

import org.ihtsdo.otf.tcc.api.relationship.RelAssertionType;
import org.ihtsdo.otf.tcc.api.store.Ts;
import org.ihtsdo.otf.tcc.api.coordinate.EditCoordinate;
import org.ihtsdo.otf.tcc.api.coordinate.StandardViewCoordinates;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;
import org.ihtsdo.otf.tcc.api.metadata.binding.Snomed;
import org.ihtsdo.otf.tcc.api.metadata.binding.Taxonomies;
import org.ihtsdo.otf.tcc.api.metadata.binding.TermAux;
import org.ihtsdo.ttk.helpers.classifier.FetchKindOf;
import org.ihtsdo.otf.tcc.api.time.TimeHelper;
import org.ihtsdo.ttk.logic.SnomedToLogicTree;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import org.ihtsdo.otf.tcc.api.nid.NativeIdSetBI;
import org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding;

/**
 *
 * @author kec
 */
public class Classifier {

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @SuppressWarnings("unchecked")
   public static void classify() throws Exception {
      ViewCoordinate vc = StandardViewCoordinates.getSnomedInferredLatest();

      vc.setRelationshipAssertionType(RelAssertionType.STATED);

      EditCoordinate ec = new EditCoordinate(TermAux.USER.getNid(), DescriptionLogicBinding.DL_MODULE.getNid(),
                             Snomed.SNOMED_RELEASE_PATH.getNid());

      // Convert to new form.
      Ts.get().suspendChangeNotifications();
      SnomedToLogicTree converter = new SnomedToLogicTree(vc, ec);
      long             time      = System.currentTimeMillis();

      System.out.println(TimeHelper.formatDate(time));
      Ts.get().iterateConceptDataInParallel(converter);
      Ts.get().commit();
      System.out.println("Conversion time: "
                         + TimeHelper.getElapsedTimeString(System.currentTimeMillis() - time));

      Ts.get().resumeChangeNotifications();
      // Implement the action as a FX Service...
      // Step 1: Determine all current descendents of the SNOMED root concept
      // for parallel iteration...
      time = System.currentTimeMillis();
      System.out.println(TimeHelper.formatDate(time));

      FetchKindOf kindOfFetcher = new FetchKindOf(Taxonomies.SNOMED.getLenient().getNid(), vc);

      Ts.get().iterateConceptDataInSequence(kindOfFetcher);

      NativeIdSetBI kindOfConcepts = kindOfFetcher.getKindOfBitSet();

      System.out.println("Kind of fetch: "
                         + TimeHelper.getElapsedTimeString(System.currentTimeMillis() - time));
      time = System.currentTimeMillis();
      System.out.println(TimeHelper.formatDate(time));

      // Step 2: Determine all current descendents of the SNOMED Role concept
      FetchKindOf roleFetcher = new FetchKindOf(Taxonomies.SNOMED_ROLE_ROOT.getLenient().getNid(), vc);

      Ts.get().iterateConceptDataInSequence(roleFetcher);

      NativeIdSetBI roleConcepts = roleFetcher.getKindOfBitSet();

      System.out.println("Role fetch: " + TimeHelper.getElapsedTimeString(System.currentTimeMillis() - time));
      System.out.println("Kind of concepts: " + kindOfConcepts.size());
      System.out.println("Role concepts: " + roleConcepts.size());

      // Step 3:
      time = System.currentTimeMillis();
      System.out.println(TimeHelper.formatDate(time));

      IReasoner<String> reasoner = new SnorocketReasoner<>();
      Factory<String>   f        = new Factory<>();
      AxiomConstructor  ac       = new AxiomConstructor(kindOfConcepts, roleConcepts, f, vc);

      Ts.get().iterateConceptDataInParallel(ac);
      System.out.println("Axiom constructor: "
                         + TimeHelper.getElapsedTimeString(System.currentTimeMillis() - time));
      time = System.currentTimeMillis();
      System.out.println(TimeHelper.formatDate(time));
      reasoner.classify(ac.axioms);
      System.out.println("Classify: " + TimeHelper.getElapsedTimeString(System.currentTimeMillis() - time));
      time = System.currentTimeMillis();
      System.out.println(TimeHelper.formatDate(time));

      @SuppressWarnings("unused") IOntology<String> t = reasoner.getClassifiedOntology();

      System.out.println("Get Ontology: "
                         + TimeHelper.getElapsedTimeString(System.currentTimeMillis() - time));
      time = System.currentTimeMillis();
      System.out.println(TimeHelper.formatDate(time));

      File stateDirectory = new File("target");

      stateDirectory.mkdirs();

      File stateFile = new File(stateDirectory, "classifier_uuid.state");

//    System.out.println("Writing state to disk...");
//    try (FileOutputStream fos = new FileOutputStream(stateFile, false)) {
//        reasoner.save(fos);
//    }
//    System.out.println("Write time: " + TimeHelper.getElapsedTimeString(System.currentTimeMillis() - time));
//    time = System.currentTimeMillis();
//    System.out.println(TimeHelper.formatDate(time));
//    
//    System.out.println("Reading state from disk...");
//
//    FileInputStream fis = new FileInputStream(stateFile);
//    reasoner = SnorocketReasoner.load(fis);
//    f        = new Factory<>();
//    System.out.println("Read time: " + TimeHelper.getElapsedTimeString(System.currentTimeMillis() - time));
//    time = System.currentTimeMillis();

      System.out.println("Adding new concept");

      // Add new concept
      IConcept newConcept = f.createConcept(UUID.randomUUID().toString());

      // Add new is-a
      ArrayList<IConcept> defn = new ArrayList<>();

      defn.add(f.createConcept(AxiomConstructor.getUUID(Taxonomies.SNOMED.getStrict(vc).getNid())));

      IConcept        expr      = f.createConjunction(defn.toArray(new IConcept[defn.size()]));
      HashSet<IAxiom> newAxioms = new HashSet<>();

      newAxioms.add(f.createConceptInclusion(newConcept, expr));
      System.out.println("Start classify");
      reasoner.classify(newAxioms);
      System.out.println("Classify: " + TimeHelper.getElapsedTimeString(System.currentTimeMillis() - time));
      time = System.currentTimeMillis();
      System.out.println(TimeHelper.formatDate(time));
      t = reasoner.getClassifiedOntology();
      System.out.println("Get Ontology: "
                         + TimeHelper.getElapsedTimeString(System.currentTimeMillis() - time));
      time = System.currentTimeMillis();
      System.out.println(TimeHelper.formatDate(time));
   }
}
