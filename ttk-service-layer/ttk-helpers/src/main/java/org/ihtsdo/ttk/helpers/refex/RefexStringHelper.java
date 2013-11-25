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



package org.ihtsdo.ttk.helpers.refex;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;
import org.ihtsdo.otf.tcc.api.store.Ts;
import org.ihtsdo.otf.tcc.api.concept.ConceptVersionBI;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;
import org.ihtsdo.otf.tcc.api.refex.RefexVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_boolean.RefexBooleanVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_int.RefexIntVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_long.RefexLongVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid.RefexNidVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_boolean.RefexNidBooleanVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_float.RefexNidFloatVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_int.RefexNidIntVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_long.RefexNidLongVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_nid.RefexNidNidVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_nid_nid.RefexNidNidNidVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_nid_nid_float.RefexNidNidNidFloatVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_nid_nid_int.RefexNidNidNidIntVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_nid_nid_long.RefexNidNidNidLongVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_nid_nid_string.RefexNidNidNidStringVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_nid_string.RefexNidNidStringVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_string.RefexNidStringVersionBI;
import org.ihtsdo.otf.tcc.api.refex.type_string.RefexStringVersionBI;
import org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding;

import static org.ihtsdo.otf.tcc.api.refex.RefexType.BOOLEAN;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_BOOLEAN;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_CID;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_CID_CID;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_CID_CID_FLOAT;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_CID_CID_INT;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_CID_CID_LONG;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_CID_CID_STRING;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_CID_STR;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_FLOAT;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_INT;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_LONG;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.CID_STR;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.INT;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.LONG;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.MEMBER;
import static org.ihtsdo.otf.tcc.api.refex.RefexType.STR;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

/**
 *
 * @author kec
 */
public class RefexStringHelper {

   /**
    * Method description
    *
    *
    * @param node
    * @param sb
    * @param vc
    *
    * @throws ContradictionException
    * @throws IOException
    */
   public static void appendToStringBuilder(RefexVersionBI node, StringBuilder sb, ViewCoordinate vc)
           throws ContradictionException, IOException {
      switch (node.getRefexType()) {
      case BOOLEAN :
         RefexBooleanVersionBI case1 = (RefexBooleanVersionBI) node;

         sb.append(case1.getBoolean1());

         break;

      case CID :
         RefexNidVersionBI case2 = (RefexNidVersionBI) node;

         sb.append(getTextForNid(vc, case2.getNid1()));

         break;

      case CID_BOOLEAN :
         RefexNidBooleanVersionBI case3 = (RefexNidBooleanVersionBI) node;

         if (Ts.get().isConceptNid(case3.getNid1())) {
            sb.append(getTextForNid(vc, case3.getNid1()));
         }

         if (case3.getBoolean1()) {
            sb.append("   ");
         } else {
            sb.append(" ");
            sb.append(case3.getBoolean1());
         }

         break;

      case CID_CID :
         RefexNidNidVersionBI case4 = (RefexNidNidVersionBI) node;

         if (case4.getNid1() != DescriptionLogicBinding.CONCEPT_REFERENCE.getNid()) {
            sb.append(getTextForNid(vc, case4.getNid1()));
            sb.append(" ");
         }

         sb.append(getTextForNid(vc, case4.getNid2()));

         break;

      case CID_CID_CID :
         RefexNidNidNidVersionBI case5 = (RefexNidNidNidVersionBI) node;

         sb.append(getTextForNid(vc, case5.getNid1()));
         sb.append(" ");
         sb.append(getTextForNid(vc, case5.getNid2()));
         sb.append(" ");
         sb.append(getTextForNid(vc, case5.getNid3()));

         break;

      case CID_CID_CID_FLOAT :
         RefexNidNidNidFloatVersionBI case6 = (RefexNidNidNidFloatVersionBI) node;

         sb.append(getTextForNid(vc, case6.getNid1()));
         sb.append(" ");
         sb.append(getTextForNid(vc, case6.getNid2()));
         sb.append(" ");
         sb.append(getTextForNid(vc, case6.getNid3()));
         sb.append(" ");
         sb.append(case6.getFloat1());

         break;

      case CID_CID_CID_INT :
         RefexNidNidNidIntVersionBI case7 = (RefexNidNidNidIntVersionBI) node;

         sb.append(getTextForNid(vc, case7.getNid1()));
         sb.append(" ");
         sb.append(getTextForNid(vc, case7.getNid2()));
         sb.append(" ");
         sb.append(getTextForNid(vc, case7.getNid3()));
         sb.append(" ");
         sb.append(case7.getInt1());

         break;

      case CID_CID_CID_LONG :
         RefexNidNidNidLongVersionBI case8 = (RefexNidNidNidLongVersionBI) node;

         sb.append(getTextForNid(vc, case8.getNid1()));
         sb.append(" ");
         sb.append(getTextForNid(vc, case8.getNid2()));
         sb.append(" ");
         sb.append(getTextForNid(vc, case8.getNid3()));
         sb.append(" ");
         sb.append(case8.getLong1());

         break;

      case CID_CID_CID_STRING :
         RefexNidNidNidStringVersionBI case9 = (RefexNidNidNidStringVersionBI) node;

         sb.append(getTextForNid(vc, case9.getNid1()));
         sb.append(" ");
         sb.append(getTextForNid(vc, case9.getNid2()));
         sb.append(" ");
         sb.append(getTextForNid(vc, case9.getNid3()));
         sb.append(" ");
         sb.append(case9.getString1());

         break;

      case CID_CID_STR :
         RefexNidNidStringVersionBI case10 = (RefexNidNidStringVersionBI) node;

         sb.append(getTextForNid(vc, case10.getNid1()));
         sb.append(" ");
         sb.append(getTextForNid(vc, case10.getNid2()));
         sb.append(" ");
         sb.append(case10.getString1());

         break;

      case CID_FLOAT :
         RefexNidFloatVersionBI case11 = (RefexNidFloatVersionBI) node;

         sb.append(getTextForNid(vc, case11.getNid1()));
         sb.append(" ");
         sb.append(case11.getFloat1());

         break;

      case CID_INT :
         RefexNidIntVersionBI case12 = (RefexNidIntVersionBI) node;

         sb.append(getTextForNid(vc, case12.getNid1()));
         sb.append(" ");
         sb.append(case12.getInt1());

         break;

      case CID_LONG :
         RefexNidLongVersionBI case13 = (RefexNidLongVersionBI) node;

         sb.append(getTextForNid(vc, case13.getNid1()));
         sb.append(" ");
         sb.append(case13.getLong1());

         break;

      case CID_STR :
         RefexNidStringVersionBI case14 = (RefexNidStringVersionBI) node;

         sb.append(getTextForNid(vc, case14.getNid()));
         sb.append(" ");
         sb.append(case14.getString1());

         break;

      case INT :
         RefexIntVersionBI case15 = (RefexIntVersionBI) node;

         sb.append(case15.getInt1());

         break;

      case LONG :
         RefexLongVersionBI case16 = (RefexLongVersionBI) node;

         sb.append(case16.getLong1());

         break;

      case MEMBER :
         break;

      case STR :
         RefexStringVersionBI case18 = (RefexStringVersionBI) node;

         sb.append(case18.getString1());

         break;
      }
   }

   /**
    * Method description
    *
    *
    * @param node
    * @param vc
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    */
   public static String getString(RefexVersionBI node, ViewCoordinate vc)
           throws ContradictionException, IOException {
      StringBuilder builder = new StringBuilder();

      appendToStringBuilder(node, builder, vc);

      return builder.toString();
   }

   /**
    * Method description
    *
    *
    * @param node
    * @param vc
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    */
   public static String getStringNid2(RefexVersionBI node, ViewCoordinate vc)
           throws ContradictionException, IOException {
      StringBuilder        builder = new StringBuilder();
      RefexNidNidVersionBI case4   = (RefexNidNidVersionBI) node;

      builder.append(getTextForNid(vc, case4.getNid2()));

      return builder.toString();
   }

   /**
    * Method description
    *
    *
    * @param vc
    * @param nid
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    */
   private static String getTextForNid(ViewCoordinate vc, int nid)
           throws IOException, ContradictionException {
      if (Ts.get().isConceptNid(nid)) {
         ConceptVersionBI concept = Ts.get().getConceptVersion(vc, nid);

         if (concept.getPreferredDescription() != null) {
            return Ts.get().getConceptVersion(vc, nid).getPreferredDescription().getText();
         }

         return "null preferred description for: " + nid;
      }

      return Ts.get().informAboutNid(nid).toString();
   }
}
