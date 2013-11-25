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

/**
 *
 * @author kec
 */
public enum DefinitionPartType {
    DEFINITION_ROOT("dl-definition-root", 1),
    NECESSARY_SET("dl-necessary-set", 1),
    SUFFICIENT_SET("dl-sufficient-set", 1),
    AND("dl-and", 1),
    OR("dl-or", 1),
    DISJOINT_WITH("dl-disjoint-with", 1),
    EXISTENTIAL_RESTRICTION("dl-existential-restriction", 3),
    UNIVERSAL_RESTRICTION("dl-universal-restriction", 3),
    CONCEPT_REFERENCE_PRIMITIVE("dl-concept-reference-primitive", 3),
    CONCEPT_REFERENCE_DEFINED("dl-concept-reference-defined", 3),
    FIELD_SUBSTITUTION("dl-field-substution", 3),
    TEMPLATE_MERGE("dl-template-merge", 3),
    EDGE_TRUE("dl-edge-true", 1),
    EDGE_FALSE("dl-edge-false", 1),
    
    FEATURE_INT("dl-feature-int", 4),
    FEATURE_LONG("dl-feature-long", 4),
    FEATURE_FLOAT("dl-feature-float", 4),
    ROLE_GROUP("dl-role-group", 1),
    UNKNOWN("dl-unknown", 1); 
    
    final String cssStyle;
    final int columnSpan;

    public int getColumnSpan() {
        return columnSpan;
    }

    private DefinitionPartType(String cssStyle, int columnSpan) {
        this.cssStyle = cssStyle;
        this.columnSpan = columnSpan;
    }

    public String getCssStyleClass() {
        return cssStyle;
    }
    
    
}
