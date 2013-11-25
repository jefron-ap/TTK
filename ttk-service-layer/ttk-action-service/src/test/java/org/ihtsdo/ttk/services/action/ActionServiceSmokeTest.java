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

import javafx.event.Event;

import javafx.scene.input.MouseEvent;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

import org.ihtsdo.ttk.services.action.drools.EntryPoints;
import org.ihtsdo.ttk.services.action.drools.SourceContext;
import org.ihtsdo.ttk.services.action.drools.TargetContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author kec
 */
public class ActionServiceSmokeTest {

   /** Field description */
   StatefulKnowledgeSession ksession;

   /** Field description */
   WorkingMemoryEntryPoint uiEventStream;

   /**
    * Constructs ...
    *
    */
   public ActionServiceSmokeTest() {}

   /**
    * Method description
    *
    */
   @After
   public void tearDown() {}

   /**
    * Method description
    *
    */
   @AfterClass
   public static void tearDownClass() {}

   /**
    * Method description
    *
    */
   @Test
   public void testDrools() {

      // Insert facts
      SourceContext source        = new SourceContext(null);
      FactHandle    sourceHandle  = ksession.insert(source);
      TargetContext target        = new TargetContext(null);
      FactHandle    targetHandle  = ksession.insert(target);
      TargetContext target2       = new TargetContext(null);
      FactHandle    targetHandle2 = ksession.insert(target2);
      Event         clickEvent    = new Event(MouseEvent.MOUSE_PRESSED);

      uiEventStream.insert(clickEvent);
      ksession.fireAllRules();
      Assert.assertTrue(target.getContextualActions().size() == 3);
      Assert.assertTrue(target2.getContextualActions().size() == 3);
      System.out.println(target.getContextualActions());
      System.out.println(target2.getContextualActions());

      Event releaseEvent = new Event(MouseEvent.MOUSE_RELEASED);

      uiEventStream.insert(releaseEvent);
      ksession.fireAllRules();
      System.out.println(target.getContextualActions());
      System.out.println(target2.getContextualActions());
      Assert.assertTrue(target.getContextualActions().size() == 0);
      Assert.assertTrue(target2.getContextualActions().size() == 0);
   }

   /**
    * Method description
    *
    */
   @Before
   public void setUp() {
      KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

      kbuilder.add(ResourceFactory.newClassPathResource("org/ihtsdo/ttk/services/action/SmokeTest.drl",
          getClass()), ResourceType.DRL);

      if (kbuilder.hasErrors()) {
         throw new RuntimeException(kbuilder.getErrors().toString());
      }

       KnowledgeBaseConfiguration kbConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
       kbConfig.setOption(EventProcessingOption.STREAM);
       KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbConfig);

       kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

       KnowledgeSessionConfiguration ksConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();

       ksConfig.setOption(ClockTypeOption.get("realtime"));
      
      ksession      = kbase.newStatefulKnowledgeSession(ksConfig, null);
      uiEventStream = ksession.getWorkingMemoryEntryPoint(EntryPoints.USER_EVENTS);
   }

   /**
    * Method description
    *
    */
   @BeforeClass
   public static void setUpClass() {}
}
