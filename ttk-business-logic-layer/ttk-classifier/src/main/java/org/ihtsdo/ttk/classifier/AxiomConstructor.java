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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.ihtsdo.otf.tcc.api.chronicle.ComponentBI;
import org.ihtsdo.otf.tcc.api.concept.ConceptFetcherBI;
import org.ihtsdo.otf.tcc.api.concept.ProcessUnfetchedConceptDataBI;
import org.ihtsdo.otf.tcc.api.store.Ts;
import org.ihtsdo.otf.tcc.api.concept.ConceptVersionBI;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;
import org.ihtsdo.otf.tcc.api.id.IdBI;
import org.ihtsdo.otf.tcc.api.metadata.binding.Snomed;
import org.ihtsdo.otf.tcc.api.metadata.binding.Taxonomies;
import org.ihtsdo.otf.tcc.api.relationship.RelationshipVersionBI;
import org.ihtsdo.otf.tcc.api.spec.ValidationException;

import au.csiro.ontology.Factory;
import au.csiro.ontology.axioms.IAxiom;
import au.csiro.ontology.axioms.RoleInclusion;
import au.csiro.ontology.model.IConcept;
import au.csiro.ontology.model.INamedRole;
import org.ihtsdo.otf.tcc.api.nid.NativeIdSetBI;
//~--- JDK imports ------------------------------------------------------------

/**
 *
 * @author kec
 */
public class AxiomConstructor implements ProcessUnfetchedConceptDataBI {
    NativeIdSetBI                         kindOfConcepts;
    NativeIdSetBI                         roleConcepts;
    Factory<String>                       f;
    ConcurrentSkipListSet<IAxiom>       axioms;
    ViewCoordinate                      vc;
    INamedRole<String>                    roleGroup;
    ConcurrentHashMap<String, IConcept>   concepts;
    @SuppressWarnings("rawtypes")
	ConcurrentHashMap<String, INamedRole> roles;
    Set<String>                           neverGroupedUuids;

    public AxiomConstructor(NativeIdSetBI kindOfConcepts, NativeIdSetBI roleConcepts, Factory<String> f, ViewCoordinate vc)
            throws IOException, ValidationException {
        this.kindOfConcepts = kindOfConcepts;
        this.roleConcepts   = roleConcepts;
        this.f              = f;
        this.vc             = vc;
        axioms              = new ConcurrentSkipListSet<>();
        concepts            = new ConcurrentHashMap<>(kindOfConcepts.size());
        roles               = new ConcurrentHashMap<>(roleConcepts.size());
        roleGroup           = f.createRole("RoleGroup");

        // Add right identity for SNOMED.
        if (kindOfConcepts.isMember(Taxonomies.SNOMED.getStrict(vc).getNid())) {
            axioms.add(new RoleInclusion(new INamedRole[] { getRole(getUUID(Snomed.DIRECT_SUBSTANCE.getStrict(vc).getNid())),
                    getRole(getUUID(Snomed.HAS_ACTIVE_INGREDIENT.getStrict(vc).getNid())) }, getRole(
                    		getUUID(Snomed.DIRECT_SUBSTANCE.getStrict(vc).getNid()))));
        }
        
        neverGroupedUuids = new HashSet<>();
        neverGroupedUuids.add("b4c3f6f9-6937-30fd-8412-d0c77f8a7f73");
        neverGroupedUuids.add("65bf3b7f-c854-36b5-81c3-4915461020a8");
        neverGroupedUuids.add("26ca4590-bbe5-327c-a40a-ba56dc86996b");
        neverGroupedUuids.add("072e7737-e22e-36b5-89d2-4815f0529c63");
    }
    
    public static String getUUID(int nid) throws IOException {
    	ComponentBI cbi = Ts.get().getComponent(nid);
    	UUID uuid = cbi.getPrimordialUuid();
    	if(uuid == null) {
    		throw new RuntimeException("Unable to find UUID for nid "+nid);
    	}
    	return uuid.toString();
    }
    
    public static Long getSctId(int nid) throws IOException {
    	for(IdBI id : Ts.get().getComponent(nid).getAllIds()) {
    		Object denotation = id.getDenotation();
    		if(denotation instanceof Long) {
    			return (Long) denotation;
    		}
    	}
    	System.err.println("Unable to find SCT_ID for nid "+nid);
    	return null;
    }

    public final IConcept getConcept(String nid) {
        if (concepts.containsKey(nid)) {
            return concepts.get(nid);
        }

        IConcept newC = f.createConcept(nid);
        IConcept oldC = concepts.putIfAbsent(nid, newC);

        if (oldC != null) {
            return oldC;
        }

        return newC;
    }

    @SuppressWarnings("rawtypes")
	public final INamedRole getRole(String nid) {
        if (roles.containsKey(nid)) {
            return roles.get(nid);
        }

        INamedRole newR = f.createRole(nid);
        INamedRole oldR = roles.putIfAbsent(nid, newR);

        if (oldR != null) {
            return oldR;
        }

        return newR;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void processUnfetchedConceptData(int cNid, ConceptFetcherBI fetcher) throws Exception {
        ConceptVersionBI                      cv         = fetcher.fetch(vc);
        IConcept                              c          = getConcept(getUUID(cNid));
        ArrayList<IConcept>                   defn       = new ArrayList<>();
        HashMap<Integer, ArrayList<IConcept>> roleGroups = new HashMap<>();

        // Special handling to support role groups...
        // RoleInclusion axiom of the form new RoleInclusion(new Role<>(childRole), new Role<>(parentRole)).
        if (roleConcepts.isMember(cNid)) {
            for (RelationshipVersionBI rv : cv.getRelationshipsOutgoingActiveIsa()) {
                if (roleConcepts.isMember(rv.getDestinationNid())) {
                    axioms.add(new RoleInclusion(getRole(getUUID(cNid)), getRole(getUUID(rv.getDestinationNid()))));
                }
            }
        }

        // Aggregate the conjuncts of the necessary condition into defn
        for (RelationshipVersionBI rv : cv.getRelationshipsOutgoingActiveIsa()) {
            defn.add(getConcept(getUUID(rv.getDestinationNid())));
        }

        for (RelationshipVersionBI rv : cv.getRelationshipsOutgoingActive()) {
            if (roleConcepts.isMember(rv.getTypeNid())) {
                if (rv.getGroup() == 0) {
                	// TODO: add role group unless never grouped
                	String roleUuid = getUUID(rv.getTypeNid());
                	if(neverGroupedUuids.contains(roleUuid)) {
                		defn.add(f.createExistential(getRole(roleUuid), 
                				getConcept(getUUID(rv.getDestinationNid()))));
                	} else {
                		defn.add(f.createExistential(roleGroup, 
                				f.createExistential(getRole(roleUuid), 
                				getConcept(getUUID(rv.getDestinationNid())))));
                	}
                } else {
                    if (!roleGroups.containsKey(rv.getGroup())) {
                        roleGroups.put(rv.getGroup(), new ArrayList<IConcept>());
                    }
 
                    roleGroups.get(rv.getGroup()).add(f.createExistential(getRole(getUUID(rv.getTypeNid())),
                            getConcept(getUUID(rv.getDestinationNid()))));
                }
            }
        }

        for (ArrayList<IConcept> group : roleGroups.values()) {
        	if(!group.isEmpty()) {
        		IConcept filler = f.createConjunction(group.toArray(new IConcept[group.size()]));
        		defn.add(f.createExistential(roleGroup, filler));
        	}
        }
        
        if(!defn.isEmpty()) {
        	IConcept expr = f.createConjunction(defn.toArray(new IConcept[defn.size()]));

        	axioms.add(f.createConceptInclusion(c, expr));

        	// check if concept is fully defined and add the sufficient condition axiom
        	if ((cv.getConceptAttributesActive() != null) && cv.getConceptAttributesActive().isDefined()) {
        		axioms.add(f.createConceptInclusion(expr, c));
        	}
        }
    }

    @Override
    public NativeIdSetBI getNidSet() throws IOException {
        return kindOfConcepts;
    }

    @Override
    public boolean continueWork() {
        return true;
    }

    @Override
    public boolean allowCancel() {
        return true;
    }

    @Override
    public String getTitle() {
        return "Constructing axioms for classifier";
    }
}
