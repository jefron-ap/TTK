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



package org.ihtsdo.ttk.logic;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;
import org.ihtsdo.otf.tcc.api.refex.RefexType;
import org.ihtsdo.otf.tcc.api.blueprint.TerminologyBuilderBI;
import org.ihtsdo.otf.tcc.api.blueprint.BlueprintBuilder;
import org.ihtsdo.otf.tcc.api.blueprint.InvalidCAB;
import org.ihtsdo.otf.tcc.api.blueprint.RefexCAB;
import org.ihtsdo.otf.tcc.api.blueprint.ComponentProperty;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;
import org.ihtsdo.otf.tcc.api.spec.ConceptSpec;
import org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.ihtsdo.otf.tcc.api.blueprint.IdDirective;
import org.ihtsdo.otf.tcc.api.blueprint.RefexDirective;

/**
 *
 * @author kec
 */
public class LogicBlueprintBuilder extends BlueprintBuilder {

   /** Field description */
   public static boolean verbose = false;

   /** Field description */
   private AtomicInteger count = new AtomicInteger(0);

   /** Field description */
   private AtomicInteger dots = new AtomicInteger(0);

   /** Field description */
   private UUID refexExtensionUuid;

   
   /** 
    */
   private final ThreadLocal <UUID> conceptUuid = 
         new ThreadLocal<UUID> () {
             @Override protected UUID initialValue() {
                 return null;
         }
     };
 
   /** 
    * 
    */
     private final ThreadLocal <Stack<RefexCAB>> nodes = 
         new ThreadLocal<Stack<RefexCAB>> () {
             @Override protected Stack<RefexCAB> initialValue() {
                 return new Stack<>();
         }
     };


   /**
    * Constructs ...
    *
    *
    * @param refexExtensionUuid
    * @param vc
    * @param moduleUuid
    */
   public LogicBlueprintBuilder(UUID refexExtensionUuid, ViewCoordinate vc, UUID moduleUuid) {
      super(vc, moduleUuid);
      this.refexExtensionUuid = refexExtensionUuid;
   }

   /**
    * Add a segment (an newEdge and node) to the definition tree.
    *
    *
    * @param originNode
    * @param specs
    *
    * @return terminal node blueprint of this segment.
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public RefexCAB add(RefexCAB originNode, ConceptSpec... specs)
           throws IOException, InvalidCAB, ContradictionException {
      return addSegment(originNode, true, specs);
   }

   /**
    * Method description
    *
    *
    * @param originNode
    * @param nids
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public RefexCAB add(RefexCAB originNode, int... nids)
           throws IOException, InvalidCAB, ContradictionException {
      return addSegment(originNode, true, nids);
   }

   /**
    * Method description
    *
    *
    * @param root
    * @param typeNid
    * @param destinationNid
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public void addExtensionalRole(RefexCAB root, int typeNid, int destinationNid)
           throws IOException, InvalidCAB, ContradictionException {
      RefexCAB role = addSegment(root, true, DescriptionLogicBinding.EXISTENTIAL_RESTRICTION, typeNid);

      add(role, DescriptionLogicBinding.CONCEPT_REFERENCE.getNid(), destinationNid);
   }

   /**
    * Method description
    *
    *
    * @param originNode
    * @param truth
    * @param specs
    *
    * @return terminal node blueprint of this segment.
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public RefexCAB addSegment(RefexCAB originNode, boolean truth, ConceptSpec... specs)
           throws IOException, InvalidCAB, ContradictionException {
      RefexCAB destinationNode = newNode(specs);
      newEdge(originNode, truth, destinationNode);
      return destinationNode;
   }

   /**
    * Method description
    *
    *
    * @param originNode
    * @param truth
    * @param nids
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public RefexCAB addSegment(RefexCAB originNode, boolean truth, int... nids)
           throws IOException, InvalidCAB, ContradictionException {
      RefexCAB destinationNode = newNode(nids);
      newEdge(originNode, truth, destinationNode);
      return destinationNode;
   }

   /**
    * Method description
    *
    *
    * @param originNode
    * @param truth
    * @param restricionType
    * @param destinationNid
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public RefexCAB addSegment(RefexCAB originNode, boolean truth, ConceptSpec restricionType,
                              int destinationNid)
           throws IOException, InvalidCAB, ContradictionException {
      RefexCAB destinationNode = newNode(restricionType, destinationNid);
      newEdge(originNode, truth, destinationNode);
      return destinationNode;
   }

   /**
    * Method description
    *
    *
    * @param builder
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public void build(TerminologyBuilderBI builder)
           throws IOException, InvalidCAB, ContradictionException {
      Stack<RefexCAB> threadLocalNodes = nodes.get();
      while (!threadLocalNodes.isEmpty()) {
         count.incrementAndGet();

         if (count.get() % 1000 == 0) {
            System.out.print(".");
            System.out.flush();
            dots.incrementAndGet();

            if (dots.get() > 80) {
               dots.set(0);
               System.out.println();
               System.out.print(count.get() + ": ");
            }
         }

         RefexCAB blueprint = threadLocalNodes.pop();

          //System.out.println("Constructing: " + blueprint);
          try {
              builder.construct(blueprint);
          } catch (AssertionError assertionError) {
              System.out.println("\n\nProcessing: " + blueprint + "\n\n" + assertionError.getMessage());
              throw assertionError;
          } 
      }
   }

   /**
    * Create a new newEdge for a DefinitionTree. <code>truth</code> defaults to true.
    *
    *
    * @param originNode
    * @param destinationNode
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public RefexCAB newEdge(RefexCAB originNode, RefexCAB destinationNode)
           throws IOException, InvalidCAB, ContradictionException {
      return newEdge(originNode, true, destinationNode);
   }

   /**
    * Create a new newEdge for a DefinitionTree.
    *
    *
    * @param originNode
    * @param truth
    * @param destinationNode
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public RefexCAB newEdge(RefexCAB originNode, boolean truth, RefexCAB destinationNode)
           throws IOException, InvalidCAB, ContradictionException {
      RefexCAB newEdge = new RefexCAB(
              RefexType.CID_BOOLEAN, 
              originNode.getMemberUUID(),
              refexExtensionUuid, 
              IdDirective.GENERATE_RANDOM, RefexDirective.EXCLUDE);

      newEdge.put(ComponentProperty.MODULE_ID, moduleUuid);
      newEdge.put(ComponentProperty.ENCLOSING_CONCEPT_ID, conceptUuid.get());
      newEdge.put(ComponentProperty.COMPONENT_EXTENSION_1_ID, destinationNode.getComponentNid());
      newEdge.put(ComponentProperty.BOOLEAN_EXTENSION_1, truth);
      newEdge.put(ComponentProperty.ENCLOSING_CONCEPT_ID, conceptUuid.get());
      originNode.getAnnotationBlueprints().add(newEdge);

      if (verbose) {
         System.out.println(newEdge);
      }

      return newEdge;
   }

   /**
    * Method description
    *
    *
    * @param specs
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public RefexCAB newNode(ConceptSpec... specs)
           throws IOException, InvalidCAB, ContradictionException {
      RefexCAB newNode;

      switch (specs.length) {
      case 1 :
         newNode = newNode(RefexType.CID);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_1_ID, specs[0]);
         newNode.put(ComponentProperty.ENCLOSING_CONCEPT_ID, conceptUuid.get());

         if (verbose) {
            System.out.println(newNode);
         }

         return newNode;

      case 2 :
         newNode = newNode(RefexType.CID_CID);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_1_ID, specs[0]);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_2_ID, specs[1]);
         newNode.put(ComponentProperty.ENCLOSING_CONCEPT_ID, conceptUuid.get());

         if (verbose) {
            System.out.println(newNode);
         }


         return newNode;

      case 3 :
         newNode = newNode(RefexType.CID_CID_CID);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_1_ID, specs[0]);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_2_ID, specs[1]);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_3_ID, specs[2]);
         newNode.put(ComponentProperty.ENCLOSING_CONCEPT_ID, conceptUuid.get());

         if (verbose) {
            System.out.println(newNode);
         }

         return newNode;

      default :
         throw new InvalidCAB("Invalid spec number: " + specs);
      }
   }

   /**
    * Method description
    *
    *
    * @param spec
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public RefexCAB newNode(ConceptSpec spec)
           throws IOException, InvalidCAB, ContradictionException {
      return newNode(new ConceptSpec[] { spec });
   }

   /**
    * Method description
    *
    *
    * @param specs
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public RefexCAB newNode(int... specs)
           throws IOException, InvalidCAB, ContradictionException {
      RefexCAB newNode;

      switch (specs.length) {
      case 1 :
         newNode = newNode(RefexType.CID);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_1_ID, specs[0]);
         newNode.put(ComponentProperty.ENCLOSING_CONCEPT_ID, conceptUuid.get());

         if (verbose) {
            System.out.println(newNode);
         }

         return newNode;

      case 2 :
         newNode = newNode(RefexType.CID_CID);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_1_ID, specs[0]);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_2_ID, specs[1]);
         newNode.put(ComponentProperty.ENCLOSING_CONCEPT_ID, conceptUuid.get());

         if (verbose) {
            System.out.println(newNode);
         }

         return newNode;

      case 3 :
         newNode = newNode(RefexType.CID_CID_CID);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_1_ID, specs[0]);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_2_ID, specs[1]);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_3_ID, specs[2]);
         newNode.put(ComponentProperty.ENCLOSING_CONCEPT_ID, conceptUuid.get());

         if (verbose) {
            System.out.println(newNode);
         }

         return newNode;

      default :
         throw new InvalidCAB("Invalid spec number: " + specs);
      }
   }

   /**
    * Method description
    *
    *
    * @param refexType
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public RefexCAB newNode(RefexType refexType)
           throws IOException, InvalidCAB, ContradictionException {
      RefexCAB newNode = new RefexCAB(refexType, conceptUuid.get(), refexExtensionUuid,
                                      IdDirective.GENERATE_RANDOM, RefexDirective.EXCLUDE);

      newNode.put(ComponentProperty.MODULE_ID, moduleUuid);
      newNode.put(ComponentProperty.ENCLOSING_CONCEPT_ID, conceptUuid.get());
      nodes.get().push(newNode);

      if (verbose) {
         System.out.println(newNode);
      }

      return newNode;
   }

   /**
    * Method description
    *
    *
    * @param spec
    * @param nids
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws InvalidCAB
    */
   public RefexCAB newNode(ConceptSpec spec, int... nids)
           throws IOException, InvalidCAB, ContradictionException {
      RefexCAB newNode;

      switch (nids.length) {
      case 1 :
         newNode = newNode(RefexType.CID_CID);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_1_ID, spec);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_2_ID, nids[0]);
         newNode.put(ComponentProperty.ENCLOSING_CONCEPT_ID, conceptUuid.get());

         if (verbose) {
            System.out.println(newNode);
         }

         return newNode;

      case 2 :
         newNode = newNode(RefexType.CID_CID_CID);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_1_ID, spec);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_2_ID, nids[0]);
         newNode.put(ComponentProperty.COMPONENT_EXTENSION_3_ID, nids[1]);
         newNode.put(ComponentProperty.ENCLOSING_CONCEPT_ID, conceptUuid.get());

         if (verbose) {
            System.out.println(newNode);
         }

         return newNode;

      default :
         throw new InvalidCAB("Invalid nid number: " + nids);
      }
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public UUID getConceptUuid() {
      return conceptUuid.get();
   }

   /**
    * Method description
    *
    *
    * @param conceptUuid
    */
   public void setConceptUuid(UUID conceptUuid) {
      this.conceptUuid.set(conceptUuid);
   }
}
