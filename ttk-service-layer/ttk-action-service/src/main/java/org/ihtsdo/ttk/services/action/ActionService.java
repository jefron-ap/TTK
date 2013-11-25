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



package org.ihtsdo.ttk.services.action;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.otf.tcc.lookup.InstanceWrapper;
import org.ihtsdo.otf.tcc.lookup.Looker;
import org.ihtsdo.ttk.services.action.drools.DroolsActionService;

//~--- JDK imports ------------------------------------------------------------

import java.util.UUID;

/**
 *
 * @author kec
 */
public class ActionService {

   /**
    * Method description
    *
    */
   public static void start() {
      DroolsActionService     actionService = new DroolsActionService();
      InstanceWrapper<String> instance      = new InstanceWrapper(actionService,
                                                 UUID.randomUUID().toString(), "Action Service", null);

      Looker.addPair(instance);
   }
}
