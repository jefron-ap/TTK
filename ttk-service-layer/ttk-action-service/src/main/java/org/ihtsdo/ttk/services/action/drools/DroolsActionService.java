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
package org.ihtsdo.ttk.services.action.drools;

import java.util.concurrent.ConcurrentHashMap;
import javafx.collections.ObservableList;
import javafx.event.Event;
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
import org.ihtsdo.ttk.services.action.ActionBI;
import org.ihtsdo.ttk.services.action.ActionContextBI;
import org.ihtsdo.ttk.services.action.ActionServiceBI;

/**
 *
 * @author kec
 */
public class DroolsActionService implements ActionServiceBI {
    private final StatefulKnowledgeSession ksession;
    private final WorkingMemoryEntryPoint uiEventStream;
    
    ConcurrentHashMap<ActionContextBI, FactHandle> targetContexts = new ConcurrentHashMap<>();
    ConcurrentHashMap<ActionContextBI, FactHandle> sourceContexts = new ConcurrentHashMap<>();

    public DroolsActionService() {
	  KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(); 
	  kbuilder.add( ResourceFactory.newClassPathResource( "org/ihtsdo/ttk/services/action/ActionRules.drl", getClass() ),
              ResourceType.DRL );

	  if (kbuilder.hasErrors()) {
		  throw new RuntimeException(kbuilder.getErrors().toString());
	  }

       KnowledgeBaseConfiguration kbConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
       kbConfig.setOption(EventProcessingOption.STREAM);
       KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbConfig);

       kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

       KnowledgeSessionConfiguration ksConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();

       ksConfig.setOption(ClockTypeOption.get("realtime"));

      ksession = kbase.newStatefulKnowledgeSession(ksConfig, null);
      uiEventStream = ksession.getWorkingMemoryEntryPoint(EntryPoints.USER_EVENTS);
    }

    @Override
    public ObservableList<ActionBI> insertTargetContext(ActionContextBI target) {
        TargetContext targetContext = new TargetContext(target);
        FactHandle targetHandle = ksession.insert(targetContext);
        targetContexts.put(target, targetHandle);
        return targetContext.getContextualActions();
    }

    @Override
    public void retractTargetContext(ActionContextBI target) {
       FactHandle targetHandle = targetContexts.remove(target);
       if (targetHandle != null) {
         ksession.retract(targetHandle);
       }
    }

    @Override
    public ObservableList<ActionBI> insertSourceContext(ActionContextBI target) {
        SourceContext sourceContext = new SourceContext(target);
        FactHandle sourceHandle = ksession.insert(sourceContext);
        sourceContexts.put(target, sourceHandle);
        return sourceContext.getContextualActions();
    }

    @Override
    public void retractSourceContext(ActionContextBI target) {
       FactHandle sourceHandle = sourceContexts.remove(target);
       if (sourceHandle != null) {
         ksession.retract(sourceHandle);
       }
    }

    @Override
    public void processEvent(Event event) {
        uiEventStream.insert(event);
        ksession.fireAllRules();
    }
    
}
