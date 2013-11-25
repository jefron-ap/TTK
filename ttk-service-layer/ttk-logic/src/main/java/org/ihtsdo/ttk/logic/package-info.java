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



/**
 * The logic package allows description logic axioms to be represented as a 
 * <code>{@link org.ihtsdo.ttk.logic.DefinitionTree}</code> 
 * using annotations on the concepts to which the axioms apply. This tree structure will allow
 * a natural transformation between the underlying representation, and a graphical representation such as the 
 * SNOMED Diagraming standard. This representation is extensible and STAMP versioned, such that it can support
 * concurrent distributed development.
 * 
 * <ol>
 *  <li><a href="#snomed-diagramming-standard-example">Snomed Diagramming Standard Example</a></li>
 *  <li><a href="#description-logic-auxiliary">Description Logic Auxiliary taxonomy overview</a></li>
 *  <li><a href="#edges">Edges</a></li>
 *  <li><a href="#nodes">Nodes</a></li>
 *  <ol type="a">
 *      <li><a href="#definition-root">Definition Root</a></li>
 *      <li><a href="#sufficient-set">Sufficient Set</a></li>
 *      <li><a href="#necessary-set">Necessary Set</a></li>
 *      <li><a href="#logical-and">Logical And</a></li>
 *      <li><a href="#logical-or">Logical Or</a></li>
 *      <li><a href="#disjoint-with">Disjoint With</a></li>
 *      <li><a href="#concept">Concept Reference</a></li>
 *      <li><a href="#concept-substitution">Concept Substitution</a></li>
 *      <li><a href="#role">Role</a></li>
 *      <li><a href="#template-merge">Template Merge</a></li>
  *     <li><a href="#integer-feature">Integer Feature</a></li>
 *      <li><a href="#long-feature">Long Feature</a></li>
 *      <li><a href="#float-feature">Float Feature</a></li>
*  </ol>
 *  <li><a href="#definition-tree-example">Definition Tree Example</a></li>
 *  <li><a href="#template-example">Template Example</a></li>
 *  <li><a href="#template-invocation">Template Invocation</a></li>
 * 
 * </ol>
 *
<div>
* <figure id="snomed-diagramming-standard-example">
* <img src="doc-files/skin-of-left-buttock.png"/></div> 
* <figcaption><b>Figure 1:</b> Definition of "skin of left buttock" in 
* SNOMED Diagramming Standard form.</figcaption>
* </figure>
 * 
 * 
 * <div id=description-logic-auxiliary>
 * <p>
(TODO: At some point, would be nice to be able to specify a taxonomy, and have it generate the taxonomy as 
* part of building the book/documentation; and also have it link to a rest server for real-time retrieval)<p>
* 
* The below taxonomy of a "description-logic auxiliary" will be used together with refex annotations to form
* a Definition Tree, that can represent a variety of description logic semantics. Future functions can be
* enabled by adding new concepts to the description-logic auxiliary. 
* <ul type="disc">
<li>description-logic auxiliary
* <ul type="disc">
*       
         <li>set operators<ul type="disc">
         	<li>sufficient set</li>
         	<li>necessary set</ul></li>
  </ul><ul type="disc">
        <li>connective operators<ul type="disc">
         	<li>and</li>
         	<li>or</li>
		<li>disjoint with</li>
		<li>definition root</li></ul>
  </ul><ul type="disc">
         <li>role operators<ul type="disc">
         	<li>universal restriction</li>
         	<li>extensional restriction</li></ul>
  </ul><ul type="disc">
         <li>concrete domain operators<ul type="disc">
         	<li>greater than</li>
         	<li>greater than or equal to</li>
         	<li>equal to</li>
         	<li>less than or equal to</li>
         	<li>less than</li></ul>
  </ul><ul type="disc">
         <li>description-logic family<ul type="disc">
         	<li>EL++</li>
		<li>SH</li></ul>
  </ul><ul type="disc">
	<li>description-logic classifier<ul type="disc">
         	<li>SnoRocket</li>
        	<li>ConDOR</li></ul>
  </ul><ul type="disc">
         <li>intrinsic roles<ul type="disc">
         	<li>role group</li></ul>
  </ul><ul type="disc">
	<li>template operators<ul type="disc">
		<li>template merge</li>
		<li>field substitution</li></ul>
  </ul><ul type="disc">
	<li>template concepts<ul type="disc">
		<li>skin of region template</li></ul>
  </ul><ul type="disc">
	<li>field operators<ul type="disc">
        	<li>component extension 1 id substitution</li>
         	<li>component extension 2 id substitution</li>
         	<li>component extension 3 id substitution</li>
         	<li>integer 1 substitution</li>
        	<li>long 1 substitution</li>
        	<li>float 1 substitution</li></ul>
  </ul><ul type="disc">
	<li>field annotations<ul type="disc">
         	<li>component extension 1 label</li>
         	<li>component extension 1 order</li>
         	<li>component extension 1 default value</li>
         	<li>component extension 2 label</li>
         	<li>component extension 2 order</li>
         	<li>component extension 2 default value</li>
         	<li>component extension 3 label</li>
         	<li>component extension 3 order</li>
        	<li>component extension 3 default value</li>
         	<li>integer 1 label</li>
         	<li>integer 1 order</li>
         	<li>integer 1 default value</li>
         	<li>long 1 label</li>
         	<li>long 1 order</li>
        	<li>long 1 default value</li>
         	<li>float 1 label</li>
         	<li>float 1 order</li>
        	<li>float 1 default value</li></ul>
  </ul><ul type="disc">
	<li>object property characteristics<ul type="disc">
		<li>functional</li>
		<li>transitive</li>
		<li>reflexive</li></ul>
  </ul><ul type="disc">
         <li>object property operators<ul type="disc">
         	<li>sub-property of
         	<li>composition
</li>
* </ul>
* </ul>
* </ul>
 </div>
 * 
 * 
 * 
 * <p>All members of a <code>{@link org.ihtsdo.ttk.logic.DefinitionTree}</code> 
 * 
 * have the same {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#REFEX_EXTENSION_ID}, and that
 * {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#REFEX_EXTENSION_ID} represents the specific variety of
 * description logic. For example: {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#EL_PLUS_PLUS}
 *
 * <p>Annotations represent Nodes and Edges
 *  of the DAG as follows:
 *
 * <dl>
 * <dt><b><a name="edges">Edges</a></b></dt>
 * <dd>
 * <ul>
 * <li>Edges are represented by annotations of type: {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_BOOLEAN}.
 * <br> <img src="doc-files/edge.svg"/></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#REFEX_EXTENSION_ID} field of the edge represents
 * the specific variety of description logic. For example:
 * {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#EL_PLUS_PLUS}.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#REFERENCED_COMPONENT_ID} field of the edge
 * is the identifier of the node from which the edge originates.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the edge is
 * the identifier of the destination node of the edge. The destination node must be an annotation on the
 * Concept that also contains the edge.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#BOOLEAN_EXTENSION_1} field of the edge
 * determines if the destination node is negated.</li>
 * <li>Edges will not have annotations</li>
 * </ul>
 * </dd>
 * <dt id="nodes"><b>Nodes</b></dt>
 * <dd>
 * <ul>
 * <li>Nodes are represented as annotations on the concept to which the axioms apply.</li>
 * <li>Nodes may be represented by a number of refex types, determined by the type of the node.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#REFEX_EXTENSION_ID} field of the node represents
 * the specific variety of description logic. For example:
 * {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#EL_PLUS_PLUS}.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#REFERENCED_COMPONENT_ID} field of the node is the
 * identifier of the concept to which the axioms apply.</li>
 * </ul>
 * Additional refex properties will vary depending on the specific type and purpose of the node as follows:
 *
 *
 * <dl><dt>{@link org.ihtsdo.ttk.api.ToolkitRefexType#CID}</dt>
 * <dd>
 * <dl>
 * <dt id="definition-root"><b>Definition Root</b></dt>
 * <dd><ul>
 * <li>The definition root is represented by an annotation on the concept being defined.  
 * The annotation is of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID}.
 * <br> <img src="doc-files/definition-root.svg"/></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * the identifier for the {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#DEFINITION_ROOT}
 * concept.</li>
 * <li>The Definition Root will have one or more annotations, either Edges that connect the Definition Root
 * to Sufficient or Necessary Sets, and/or annotations indicating template merges.</li>
 * </ul></dd>
 * 
 * 
 * <dt id="sufficient-set"><b>Sufficient Set</b></dt>
 * <dd><ul>
 * <li>A Sufficient set is represented by an annotation of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID}.
 * <br> <img src="doc-files/sufficient-set.svg"/></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * the identifier for the {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#SUFFICIENT_SET}
 * concept.</li>
 * <li>The Sufficient Set will have one or more annotations, either Edges that connect the Sufficient Set
 * to connectives, roles or concepts, and/or an annotations indicating template merges.</li>
 * </ul></dd>
 * 
 * 
 * <dt id="necessary-set"><b>Necessary Set</b></dt>
 * <dd><ul>
 * <li>A necessary set is represented by an annotation of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID}.
 * <br> <img src="doc-files/necessary-set.svg"/></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * the identifier for the {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#NECESSARY_SET}
 * concept.</li>
* <li>The Necessary Set will have one or more annotations, either Edges that connect the Necessary Set
 * to connectives, roles or concepts, and/or an annotations indicating template merges.</li>
 * </ul></dd>
 * 
 * 
 * <dt id="logical-and"><b>Logical And</b></dt>
 * <dd><ul>
 * <li>A logical and is represented by an annotation of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID}.
 * <br> <img src="doc-files/logical-and.svg"/></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * the identifier for the {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#AND}
 * concept.</li>
* <li>The Logical And will have one or more annotations, either Edges that connect the Logical And
 * to connectives, roles or concepts, and/or an annotations indicating template merges.</li>
 * </ul></dd>
 *
 * <dt id="logical-or"><b>Logical Or</b></dt>
 * <dd><ul>
 * <li>A logical or is represented by an annotation of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID}.
 * <br> <img src="doc-files/logical-or.svg"/></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * the identifier for the {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#OR}
 * concept.</li>
* <li>The Logical Or will have one or more annotations, either Edges that connect the Logical Or
 * to connectives, roles or concepts, and/or an annotations indicating template merges.</li>
 * </ul></dd>
 *
 * <dt id="disjoint-with"><b>Disjoint With</b></dt>
 * <dd><ul>
 * <li>Disjoint with is represented by an annotation of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID}.
 * <br> <img src="doc-files/disjoint-with.svg"/></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * the identifier for the {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#DISJOINT_WITH}
 * concept.</li>
* <li>The Disjoint With will have one or more annotations, either Edges that connect the Disjoint With
 * to connectives, concepts, and/or an annotations indicating template merges.</li>
 * </ul></dd>
 *
 * <dt id="concept"><b>Concept Reference</b></dt>
 * <dd><ul>
 * <li>A concept, as used in a conjunction or a role restriction is represented by an annotation of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_CID}.
 * <br><br>NEED TO update diagram!
 * <br> <img src="doc-files/referenced-concept.svg"/></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#CONCEPT_REFERENCE}.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_2_ID} field of the node is
 * the identifier for the concept.</li>
* <li>The Concept Reference node will not have annotations.</li>
 * </ul> 
 * </dd>
 * </dl>
 * </dd>
 *
 *
 * <dt id="concept-substitution"><b>Concept Substitution</b></dt>
 * <dd><ul>
 * <li>A concept substitution, as used in a conjunction or a role restriction is
 *  represented by an annotation of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_CID}.
 * <br><br>NEED TO update diagram!
 * <br> <img src="doc-files/concept-substitution.svg"/></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#FIELD_SUBSTITUTION}</li>
 * <ul>
 * <li></li>
 * </ul>
 * 
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_2_ID} field of the node is
 * the one of the following:</li>
 * <ul>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#COMPONENT_EXTENSION_1_ID_SUBSTITUTION},</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#COMPONENT_EXTENSION_2_ID_SUBSTITUTION},</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#COMPONENT_EXTENSION_3_ID_SUBSTITUTION},</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#INTEGER_1_SUBSTITUTION}, or</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#LONG_1_SUBSTITUTION}.</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#FLOAT_1_SUBSTITUTION}.</li>
 * </ul>
* <li>The Concept substitution node will not have annotations.</li>
 * </ul> 
 * </dd>
 * </dl>
 * </dd>
 *
 * <dt>{@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_CID}
 * <dd>
 * <dl>
 * <dt id="role"><b>Role</b></dt>
 * <dd><ul>
 * <li>A role is represented by an annotation of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_CID}.
 * <br> <img src="doc-files/role.svg"/></dt></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * the identifier for the role operator concept, either
 * {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#EXISTENTIAL_RESTRICTION} or
 * {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#UNIVERSAL_RESTRICTION}.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_2_ID} field of the node is
 * the identifier for the concept that defines the type of role, for example, in the case of role groups, the
 * role type concept would be:
 * {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#ROLE_GROUP}.</li>
* <li>The Role will have one or more annotations, either Edges that connect the Role
 * to connectives, concepts, and/or an annotations indicating template merges.</li>
 * </ul>
 * </dd>

 * <dt id="template-merge"><b>Template Merge</b></dt>
 * <dd><ul>
 * <li>A template merge is represented by an annotation of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_CID}.
 * <br> <img src="doc-files/template-merge.svg"/></dt></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * the identifier for 
 * {@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#TEMPLATE_MERGE}.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_2_ID} field of the node is
 * the identifier for refex member which contains the substitution values for the substitutions specified
 * within the template. The concept id for the template can be retrieved as the enclosing concept of the 
 * refex member. The refex member should be created at the time this Template Merge node is associated 
 * with the Definition Tree, and the reference to the member id should not change. 
 * </li>
* <li>The Template Merge node will not have any annotations.</li>
 * </ul>
 * </dd>


* <dl>
 * </dd>
 *
 *
 *
 * <dt>{@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_CID_CID_INT}</dt>
 * <dd>
 * <dl>
 * <dt  id="integer-feature"><b>Integer Feature</b></dt>
 * <dd><ul>
 * <li>An integer (32 bit signed) feature is represented by an annotation of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_CID_CID_INT}.
 * <br> <img src="doc-files/feature-int.svg"/></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * the identifier for the feature type concept, for example a concept representing <em>area</em>.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_2_ID} field of the node is
 * the identifier for the concept that defines the concrete domain operator, such as:
 * <ul>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#GREATER_THAN},</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#GREATER_THAN_OR_EQUAL_TO},</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#EQUAL_TO},</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#LESS_THAN_OR_EQUAL_TO}, or</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#LESS_THAN}.</li>
 * </ul>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#INTEGER_EXTENSION_1} field
 *     holds the concrete value.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_3_ID} field
 *     holds the identifier for a concept that represents the units of measure for the
 * concrete value of the feature, such as: <em>square centimeters</em>.</li>
 * </li>
 * <li>Integer Features will not have annotations</li>
 * </ul></dd>
 * <dl>
 * </dd>
 *
 *
 *
 *
 * <dt>{@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_CID_CID_LONG}</dt>
 * <dd>
 * <dl>
 * <dt id="long-feature"><b>Long Integer Feature</b></dt>
 * <dd><ul>
 * <li>A long integer (64 bit signed) feature is represented by an annotation of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_CID_CID_LONG}.
 * <br> <img src="doc-files/feature-long.svg"/></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * the identifier for the feature type concept, for example a concept representing <em>area</em>.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_2_ID} field of the node is
 * the identifier for the concept that defines the concrete domain operator, such as:
 * <ul>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#GREATER_THAN},</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#GREATER_THAN_OR_EQUAL_TO},</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#EQUAL_TO},</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#LESS_THAN_OR_EQUAL_TO}, or</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#LESS_THAN}.</li>
 * </ul>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#LONG_EXTENSION_1} field
 *     holds the concrete value.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_3_ID} field
 *     holds the identifier for a concept that represents the units of measure for the
 * concrete value of the feature, such as: <em>square centimeters</em>.</li>
 * </li>
 * <li>Long Features will not have annotations</li>
 * </ul></dd>
 * <dl>
 * </dd>
 *
 *
 * <dt>{@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_CID_CID_FLOAT}</dt>
 * <dd>
 * <dl>
 * <dt  id="float-feature"><b>Float Feature</b></dt>
 * <dd><ul>
 * <li>A float (32-bit IEEE 754) feature is represented by an annotation of type:
 *      {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_CID_CID_FLOAT}.
 * <br> <img src="doc-files/feature-float.svg"/></li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_1_ID} field of the node is
 * the identifier for the feature type concept, for example a concept representing <em>area</em>.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_2_ID} field of the node is
 * the identifier for the concept that defines the concrete domain operator, such as:
 * <ul>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#GREATER_THAN},</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#GREATER_THAN_OR_EQUAL_TO},</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#EQUAL_TO},</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#LESS_THAN_OR_EQUAL_TO}, or</li>
 * <li>{@link org.ihtsdo.ttk.auxiliary.taxonomies.DescriptionLogicBinding#LESS_THAN}.</li>
 * </ul>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#FLOAT_EXTENSION_1} field
 *     holds the concrete value.</li>
 * <li>The {@link org.ihtsdo.ttk.api.blueprint.ComponentProperty#COMPONENT_EXTENSION_3_ID} field
 *     holds the identifier for a concept that represents the units of measure for the
 * concrete value of the feature, such as: <em>square centimeters</em>.</li>
 * </li>
 * <li>Float Features will not have annotations</li>
 * </ul></dd>
 * <dl>
 * </dd>
 *
 *
 *
 *
 *
 * </dl>
 *
 * <div id="definition-tree-example"/>
 * The below diagram shows how a Definition Tree can be used to represent "skin of left buttock."
 * <figure >
 * 
* <img src="doc-files/skin-of-left-buttock-definition-tree.svg"/> 
* <figcaption><b>Figure 2:</b> Definition of "skin of left buttock" using an annotation-based definition tree.
* Compare with Figure 1.</figcaption>
* </figure>
 * 
 * <div id="template-example"/>
 * The below diagram shows how a Substitution Template can be used to represent "skin of region," with
 * substitution values to represent a specific region of skin (given a specification of laterality and of 
 * location). This template uses two different substitution 
 * values in  multiple places for the concepts "left" and "buttock" in the example. These values would be
 * stored in a {@link org.ihtsdo.ttk.api.ToolkitRefexType#CID_CID} type refex where the fields of a member 
 * represent the substitution values. The concept that defines the template (a concept outside of the 
 * taxonomy to be classified) also defines and holds the refex members used in the substitution. The template
 * concept also defines labels for the fields, and default values for the fields using annotations on the 
 * template concept. Difference between this Figure, and Figure 2 are shown in blue and red. 
 * <figure>
 * 
* <img src="doc-files/skin-of-region-template.svg"/> 
* <figcaption><b>Figure 3:</b> Template for "skin of region" using an annotation-based definition tree.
* Compare with Figure 2.</figcaption>
* </figure>
 * 
 * <div id="template-invocation"/>
 * The below diagram shows how a template substitution would be specified for the same "skin of left buttock"
 * from all the previous figures. 
 * <figure>
 * 
* <img src="doc-files/template-invocation.svg"/> 
* <figcaption><b>Figure 4:</b> Invocation of the Template for "skin of region" using a
* template merge annotation. The root of the template is merged with the node that
* contains the template merge annotation. The definition root in this case is merged with the 
* definition root from the template.
* Compare with Figure 2.</figcaption>
* </figure>
 *
 * @author kec
 */
package org.ihtsdo.ttk.logic;
