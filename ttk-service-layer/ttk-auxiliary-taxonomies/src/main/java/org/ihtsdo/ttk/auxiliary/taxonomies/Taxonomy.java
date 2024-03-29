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



package org.ihtsdo.ttk.auxiliary.taxonomies;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.otf.tcc.api.blueprint.ConceptCB;
import org.ihtsdo.otf.tcc.api.lang.LanguageCode;
import org.ihtsdo.otf.tcc.api.spec.ConceptSpec;
import org.ihtsdo.otf.tcc.api.uuid.UuidT5Generator;
import org.ihtsdo.otf.tcc.dto.UuidDtoBuilder;

//~--- JDK imports ------------------------------------------------------------

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;
import java.util.UUID;
import org.ihtsdo.otf.tcc.api.blueprint.IdDirective;

/**
 *
 * @author kec
 */
public class Taxonomy {
   private TreeMap<String, ConceptCB> conceptBps                 =
      new TreeMap<>();
   private List<ConceptCB>            conceptBpsInInsertionOrder =
      new ArrayList<>();
   private Stack<ConceptCB>           parentStack                =
      new Stack<>();
   private ConceptCB                  current;
   private ConceptSpec                isaTypeSpec;
   private ConceptSpec                moduleSpec;
   private ConceptSpec                pathSpec;
   private ConceptSpec                authorSpec;
   private String                     semanticTag;
   private LanguageCode                  lang;

   public Taxonomy(ConceptSpec path, ConceptSpec author, ConceptSpec module,
                   ConceptSpec isaType, String semanticTag, LanguageCode lang) {
      this.pathSpec    = path;
      this.authorSpec  = author;
      this.moduleSpec  = module;
      this.isaTypeSpec = isaType;
      this.semanticTag = semanticTag;
      this.lang        = lang;
   }

   public Taxonomy(ConceptSpec pathSpec, ConceptSpec authorSpec,
                   String moduleName, ConceptSpec isaType, String semanticTag,
                   LanguageCode lang)
           throws NoSuchAlgorithmException, UnsupportedEncodingException {
      this.pathSpec    = pathSpec;
      this.authorSpec  = authorSpec;
      this.moduleSpec  = new ConceptSpec(moduleName, getUuid(moduleName));
      this.isaTypeSpec = isaType;
      this.semanticTag = semanticTag;
      this.lang        = lang;
   }

   public Taxonomy(String pathName, ConceptSpec author, String moduleName,
                   ConceptSpec isaType, String semanticTag, LanguageCode lang)
           throws NoSuchAlgorithmException, UnsupportedEncodingException {
      this.pathSpec    = new ConceptSpec(pathName, getUuid(moduleName));
      this.authorSpec  = author;
      this.moduleSpec  = new ConceptSpec(moduleName, getUuid(moduleName));
      this.isaTypeSpec = isaType;
      this.semanticTag = semanticTag;
      this.lang        = lang;
   }

   public Taxonomy(String pathName, String authorName, String moduleName,
                   ConceptSpec isaType, String semanticTag, LanguageCode lang)
           throws NoSuchAlgorithmException, UnsupportedEncodingException {
      this.pathSpec    = new ConceptSpec(pathName, getUuid(pathName));
      this.authorSpec  = new ConceptSpec(authorName, getUuid(moduleName));
      this.moduleSpec  = new ConceptSpec(moduleName, getUuid(moduleName));
      this.isaTypeSpec = isaType;
      this.semanticTag = semanticTag;
      this.lang        = lang;
   }

   protected ConceptCB createConcept(String name) throws Exception {
      ConceptCB cb = new ConceptCB(name + " " + semanticTag, 
              name, lang, 
              isaTypeSpec.getUuids()[0],
              IdDirective.GENERATE_HASH,
              moduleSpec.getUuids()[0], 
              getParentArray());

      if (conceptBps.containsKey(name)) {
         throw new Exception("Concept is already added");
      }

      conceptBps.put(name, cb);
      conceptBpsInInsertionOrder.add(cb);
      current = cb;

      return cb;
   }

   protected ConceptCB createModuleConcept(String name) throws Exception {
      ConceptCB cb = new ConceptCB(name + " " + semanticTag, 
              name, lang, 
              isaTypeSpec.getUuids()[0],
              IdDirective.PRESERVE_CONCEPT_REST_HASH,
              moduleSpec.getUuids()[0], 
              moduleSpec.getUuids()[0],
              getParentArray());

      if (conceptBps.containsKey(name)) {
         throw new Exception("Concept is already added");
      }

      conceptBps.put(name, cb);
      conceptBpsInInsertionOrder.add(cb);
      current = cb;

      return cb;
   }

   public void exportEConcept(DataOutputStream out) throws Exception {
      UuidDtoBuilder dtoBuilder = new UuidDtoBuilder(System.currentTimeMillis(),
                                 authorSpec.getUuids()[0],
                                 pathSpec.getUuids()[0],
                                 moduleSpec.getUuids()[0]);

      for (ConceptCB concept : conceptBpsInInsertionOrder) {
         dtoBuilder.construct(concept).writeExternal(out);
      }
   }

   public void exportJavaBinding(Writer out, String packageName,
                                 String className)
           throws IOException {
      out.append("package " + packageName + ";\n");
      out.append("\n\nimport java.util.UUID;\n");
      out.append("import org.ihtsdo.ttk.api.spec.ConceptSpec;\n");
      out.append("\n\npublic class " + className + " {\n");

      for (ConceptCB concept : conceptBpsInInsertionOrder) {
         String preferredName = concept.getPreferredName();
         String constantName  = preferredName.toUpperCase();

         constantName = constantName.replace(" ", "_");
         constantName = constantName.replace("-", "_");
         constantName = constantName.replace("+", "_PLUS");
         constantName = constantName.replace("/", "_AND");
         out.append("\n\n   /** Java binding for the concept described as <strong><em>"
                 + preferredName
                 + "</em></strong>;\n    * identified by UUID: <code>\n    * "
                 + "<a href=\"http://localhost:8080/terminology/rest/concept/"
                 + concept.getComponentUuid().toString()
                 + "\">\n    * "
                 + concept.getComponentUuid().toString()
                 + "</a></code>.*/");
         
         out.append("\n   public static ConceptSpec " + constantName + " =");
         out.append("\n             new ConceptSpec(\"" + preferredName
                    + "\",");
         out.append("\n                    UUID.fromString(\""
                    + concept.getComponentUuid().toString() + "\"));");
      }

      out.append("\n}\n");
   }

   protected ConceptCB current() {
      return current;
   }

   protected void popParent() {
      parentStack.pop();
   }

   protected void pushParent(ConceptCB parent) {
      parentStack.push(parent);
   }

   private UUID[] getParentArray() {
      if (parentStack.size() == 0) {
         return new UUID[0];
      }

      return new UUID[] { parentStack.peek().getComponentUuid() };
   }

   private UUID getUuid(String name)
           throws NoSuchAlgorithmException, UnsupportedEncodingException {
      return UuidT5Generator.get(this.getClass().getName() + name);
   }
}
