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
import org.ihtsdo.otf.tcc.api.store.Ts;
import org.ihtsdo.otf.tcc.api.concept.ConceptVersionBI;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;
import org.ihtsdo.otf.tcc.api.refex.RefexVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid.RefexNidVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_boolean.RefexNidBooleanVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_nid.RefexNidNidVersionBI;
import org.ihtsdo.otf.tcc.api.spec.ValidationException;
import org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding;
import org.ihtsdo.ttk.helpers.refex.RefexStringHelper;

import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_CID_CID_INT;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author kec
 */
public class DefinitionPart {

   /**
    * rowIndex and columnIndex of this definition part, as computed by a depth
    * first walk of the tree.
    */
   private int rowIndex, columnIndex;

   /** Field description */
   private final RefexVersionBI refexVersion;

   /**
    * Constructs ...
    *
    *
    * @param refexVersion
    */
   public DefinitionPart(RefexVersionBI refexVersion) {
      this.refexVersion = refexVersion;
   }

   /**
    * Method description
    *
    *
    * @param obj
    *
    * @return
    */
   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }

      if (getClass() != obj.getClass()) {
         return false;
      }

      final DefinitionPart other = (DefinitionPart) obj;

      if (this.refexVersion.getNid() != other.refexVersion.getNid()) {
         return false;
      }

      return true;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public int hashCode() {
      int hash = 3;

      hash = 53 * hash + Objects.hashCode(this.refexVersion.getNid());

      return hash;
   }

   /**
    * Method description
    *
    *
    * @param parts
    * @param xyz
    * @param refexNid
    *
    * @return
    *
    * @throws IOException
    */
   public List<DefinitionPart> getChildren(Map<Integer, DefinitionPart> parts, ViewCoordinate xyz,
       int refexNid)
           throws IOException {
      if (refexVersion instanceof RefexNidBooleanVersionBI) {
         List<DefinitionPart>     children = new ArrayList<>(1);
         RefexNidBooleanVersionBI edge     = (RefexNidBooleanVersionBI) refexVersion;

         children.add(parts.get(edge.getNid1()));

         return children;
      }

      Collection<? extends RefexVersionBI<?>> partAnnotations = refexVersion.getAnnotationsActive(xyz,
                                                                   refexNid);
      List<DefinitionPart> children = new ArrayList<>(partAnnotations.size());

      for (RefexVersionBI<?> partAnnotation : partAnnotations) {
         children.add(parts.get(partAnnotation.getNid()));
      }

      return children;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getColumnIndex() {
      return columnIndex;
   }

   /**
    * Method description
    *
    *
    *
    * @param vc
    * @return
    *
    *
    * @throws ContradictionException
    * @throws IOException
    * @throws ValidationException
    */
   public DefinitionPartType getPartType(ViewCoordinate vc)
           throws ValidationException, IOException, ContradictionException {
      switch (refexVersion.getRefexType()) {
      case CID :
         RefexNidVersionBI nidNode = (RefexNidVersionBI) refexVersion;

         if (nidNode.getNid1() == DescriptionLogicBinding.DEFINITION_ROOT.getNid()) {
            return DefinitionPartType.DEFINITION_ROOT;
         }

         if (nidNode.getNid1() == DescriptionLogicBinding.NECESSARY_SET.getNid()) {
            return DefinitionPartType.NECESSARY_SET;
         }

         if (nidNode.getNid1() == DescriptionLogicBinding.SUFFICIENT_SET.getNid()) {
            return DefinitionPartType.SUFFICIENT_SET;
         }

         if (nidNode.getNid1() == DescriptionLogicBinding.AND.getNid()) {
            return DefinitionPartType.AND;
         }

         if (nidNode.getNid1() == DescriptionLogicBinding.OR.getNid()) {
            return DefinitionPartType.OR;
         }

         if (nidNode.getNid1() == DescriptionLogicBinding.DISJOINT_WITH.getNid()) {
            return DefinitionPartType.DISJOINT_WITH;
         }

         if (nidNode.getNid1() == DescriptionLogicBinding.ROLE_GROUP.getNid()) {
            return DefinitionPartType.ROLE_GROUP;
         }

         break;

      case CID_BOOLEAN :
         RefexNidBooleanVersionBI edge = (RefexNidBooleanVersionBI) refexVersion;

         if (edge.getBoolean1()) {
            return DefinitionPartType.EDGE_TRUE;
         }

         return DefinitionPartType.EDGE_FALSE;

      case CID_CID :
         RefexNidNidVersionBI nidNidNode = (RefexNidNidVersionBI) refexVersion;

         if (nidNidNode.getNid1() == DescriptionLogicBinding.CONCEPT_REFERENCE.getNid()) {
            ConceptVersionBI cv = Ts.get().getConceptVersion(vc, nidNidNode.getNid2());

            if (cv.getConceptAttributesActive().isDefined()) {
               return DefinitionPartType.CONCEPT_REFERENCE_DEFINED;
            }

            return DefinitionPartType.CONCEPT_REFERENCE_PRIMITIVE;
         }

         if (nidNidNode.getNid1() == DescriptionLogicBinding.FIELD_SUBSTITUTION.getNid()) {
            return DefinitionPartType.FIELD_SUBSTITUTION;
         }

         if (nidNidNode.getNid1() == DescriptionLogicBinding.TEMPLATE_MERGE.getNid()) {
            return DefinitionPartType.TEMPLATE_MERGE;
         }

         if (nidNidNode.getNid1() == DescriptionLogicBinding.EXISTENTIAL_RESTRICTION.getNid()) {
            return DefinitionPartType.EXISTENTIAL_RESTRICTION;
         }

         if (nidNidNode.getNid1() == DescriptionLogicBinding.UNIVERSAL_RESTRICTION.getNid()) {
            return DefinitionPartType.UNIVERSAL_RESTRICTION;
         }

         break;

      case CID_CID_CID_INT :
         return DefinitionPartType.FEATURE_INT;

      case CID_CID_CID_LONG :
         return DefinitionPartType.FEATURE_LONG;

      case CID_CID_CID_FLOAT :
         return DefinitionPartType.FEATURE_FLOAT;
      }

      return DefinitionPartType.UNKNOWN;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public RefexVersionBI getRefexVersion() {
      return refexVersion;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getRowIndex() {
      return rowIndex;
   }

   /**
    * Method description
    *
    *
    * @param vc
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    */
   public String getText(ViewCoordinate vc) throws ContradictionException, IOException {
      return RefexStringHelper.getString(refexVersion, vc);
   }

   /**
    * Method description
    *
    *
    * @param vc
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    */
   public String getTextNid2(ViewCoordinate vc) throws ContradictionException, IOException {
      return RefexStringHelper.getStringNid2(refexVersion, vc);
   }

   /**
    * Method description
    *
    *
    * @param columnIndex
    */
   public void setColumnIndex(int columnIndex) {
      this.columnIndex = columnIndex;
   }

   /**
    * Method description
    *
    *
    * @param rowIndex
    */
   public void setRowIndex(int rowIndex) {
      this.rowIndex = rowIndex;
   }
}
