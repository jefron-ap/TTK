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



package org.ihtsdo.ttk.pl.fx.helper;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.ttk.services.action.ActionBI;
import org.ihtsdo.ttk.services.action.terminology.TerminologyCopy;
import org.ihtsdo.ttk.services.action.terminology.TerminologyLink;
import org.ihtsdo.ttk.services.action.terminology.TerminologyMove;

/**
 *
 * @author kec
 */
public class FxActionWrapperFactory {

   /**
    * Method description
    *
    *
    * @param action
    *
    * @return
    */
   public static FxActionWrapper wrap(ActionBI action) {
      if (action instanceof TerminologyCopy) {
         FxActionWrapper wrapper = new FxActionWrapper(action);

         wrapper.setGraphic16x16(
             IconHelper.setupImageView("/fugue/16x16/icons-shadowless/scissors.png"));

         return wrapper;
      }

      if (action instanceof TerminologyLink) {
         FxActionWrapper wrapper = new FxActionWrapper(action);

         wrapper.setGraphic16x16(
             IconHelper.setupImageView("/fugue/16x16/icons-shadowless/molecule.png"));

         return wrapper;
      }

      if (action instanceof TerminologyMove) {
         FxActionWrapper wrapper = new FxActionWrapper(action);

         wrapper.setGraphic16x16(
             IconHelper.setupImageView("/fugue/16x16/icons-shadowless/arrow-transition.png"));

         return wrapper;
      }

      return new FxActionWrapper(action);
   }
}
