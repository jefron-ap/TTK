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
import org.ihtsdo.otf.tcc.api.concept.ConceptVersionBI;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;
import org.ihtsdo.otf.tcc.api.refex.RefexVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid.RefexNidVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_boolean.RefexNidBooleanVersionBI;
import org.ihtsdo.otf.tcc.api.spec.ValidationException;
import org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding;
import org.ihtsdo.ttk.helpers.refex.RefexStringHelper;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kec
 */
public class DefinitionTree {

   /**
    * The size of indent used for converting the definition to a indented text tree.
    */
   private static final String indent = "    ";

   /** Field description */
   private Map<DefinitionPart, List<DefinitionPart>> childMap = new HashMap<>();

   /**
    * Map from the refex's id, to the refex version.
    */
   private Map<Integer, DefinitionPart> parts = new HashMap<>();

   /**
    * Root node of the definition tree.
    */
   private DefinitionPart definitionRoot = null;

   /**
    * Number of rows for this definition tree.
    */
   private int rows = 0;

   /**
    * Number of columns for this definition tree.
    */
   private int columns = 0;

   /**
    * The version of the concept that this definition is derived from.
    */
   private ConceptVersionBI cv;

   /**
    * Extension identifier for the refex that holds the definition.
    */
   private int refexExtensionNid;

   /**
    * Constructs ...
    *
    *
    * @param cv
    * @param refexExtensionNid
    *
    * @throws ContradictionException
    * @throws IOException
    */
   public DefinitionTree(ConceptVersionBI cv, int refexExtensionNid)
           throws IOException, ContradictionException {
      this.cv                = cv;
      this.refexExtensionNid = refexExtensionNid;

      for (RefexVersionBI<?> annotation :
          cv.getConceptAttributesActive().getAnnotationsActive(cv.getViewCoordinate())) {
         if (annotation.getAssemblageNid() == refexExtensionNid) {
            DefinitionPart part = new DefinitionPart(annotation);

            parts.put(annotation.getNid(), part);

            for (RefexVersionBI<?> edge :
                part.getRefexVersion().getAnnotationsActive(cv.getViewCoordinate())) {
               parts.put(edge.getNid(), new DefinitionPart(edge));
            }

            if ((definitionRoot == null) && (annotation instanceof RefexNidVersionBI)) {
               if (((RefexNidVersionBI) annotation).getNid1()
                   == DescriptionLogicBinding.DEFINITION_ROOT.getNid(cv.getViewCoordinate())) {
                  definitionRoot = part;
               }
            }
         }
      }

      computeRowsAndColumns(definitionRoot, 0, 0);
   }

   /**
    * Method description
    *
    *
    * @param part
    * @param row
    * @param column
    *
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws ValidationException
    */
   private void computeRowsAndColumns(DefinitionPart part, int row, int column)
           throws IOException, ValidationException, ContradictionException {
      if (part == null) {
          return;
      }
      rows    = Math.max(rows, row);
      columns = Math.max(columns, column);
      part.setColumnIndex(column);
      part.setRowIndex(row);

      int                       rowIncrement = 0;
      ArrayList<DefinitionPart> childList    = new ArrayList<>();

      childMap.put(part, childList);

      for (DefinitionPart childPart : part.getChildren(parts, cv.getViewCoordinate(), refexExtensionNid)) {
         childList.add(childPart);
         computeRowsAndColumns(childPart, row + rowIncrement,
                               column + part.getPartType(cv.getViewCoordinate()).columnSpan);
         rowIncrement++;
      }
   }

   /**
    * Method description
    *
    *
    *
    * @return
    * @throws ContradictionException
    * @throws IOException
    */
   public StringBuilder dfsPrint() throws IOException, ContradictionException {
      return dfsPrint(new StringBuilder());
   }

   /**
    * Method description
    *
    *
    * @param sb
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    */
   public StringBuilder dfsPrint(StringBuilder sb) throws IOException, ContradictionException {
      dfsPrint(true, definitionRoot, 1, sb);
      sb.append("\n");

      return sb;
   }

   /**
    * Method description
    *
    *
    * @param truth
    * @param node
    * @param depth
    * @param sb
    *
    * @throws ContradictionException
    * @throws IOException
    */
   private void dfsPrint(boolean truth, DefinitionPart node, int depth, StringBuilder sb)
           throws IOException, ContradictionException {
      sb.append("\n");

      for (int i = 0; i < depth; i++) {
         sb.append(indent);
      }

      if (!truth) {
         sb.append("FALSE: ");
      }

      RefexStringHelper.appendToStringBuilder(node.getRefexVersion(), sb, cv.getViewCoordinate());

      Collection<? extends RefexVersionBI<?>> edges =
         node.getRefexVersion().getAnnotationsActive(cv.getViewCoordinate());

      for (RefexVersionBI<?> edge : edges) {
         if ((edge.getAssemblageNid() == refexExtensionNid)
             && (edge instanceof RefexNidBooleanVersionBI)) {
            RefexNidBooleanVersionBI theEdge = (RefexNidBooleanVersionBI) edge;

            dfsPrint(theEdge.getBoolean1(), parts.get(theEdge.getNid1()), depth + 1, sb);
         }
      }
   }

   /**
    * Method description
    *
    *
    * @param parent
    *
    * @return
    */
   public List<DefinitionPart> getChildren(DefinitionPart parent) {
      List<DefinitionPart> children = childMap.get(parent);

      if (children == null) {
         children = Collections.EMPTY_LIST;
      }

      return children;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getColumns() {
      return columns;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public Collection<DefinitionPart> getParts() {
      return parts.values();
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getRows() {
      return rows;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public ViewCoordinate getViewCoordinate() {
      return cv.getViewCoordinate();
   }
}
