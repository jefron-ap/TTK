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

import javafx.scene.Node;

import org.ihtsdo.ttk.services.action.ActionBI;

/**
 *
 * @author kec
 */
public class FxActionWrapper implements ActionBI {

   /** Field description */
   private Node graphic16x16;

   /** Field description */
   private Node graphic24x24;

   /** Field description */
   private Node graphic32x32;

   /** Field description */
   private Node graphic128x128;

   /** Field description */
   private Node graphic512x512;

   /** Field description */
   private ActionBI wrappedAction;

   /**
    * Constructs ...
    *
    *
    * @param wrappedAction
    */
   public FxActionWrapper(ActionBI wrappedAction) {
      this.wrappedAction = wrappedAction;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public String getDescription() {
      return wrappedAction.getDescription();
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public ExecutionCriterion getExecutionCriterion() {
      return wrappedAction.getExecutionCriterion();
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public ExecutionState getExecutionState() {
      return wrappedAction.getExecutionState();
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public Node getGraphic128x128() {
      return graphic128x128;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public Node getGraphic16x16() {
      return graphic16x16;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public Node getGraphic24x24() {
      return graphic24x24;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public Node getGraphic32x32() {
      return graphic32x32;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public Node getGraphic512x512() {
      return graphic512x512;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public String getLabel() {
      return wrappedAction.getLabel();
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public ThreadForAction getThreadForAction() {
      return wrappedAction.getThreadForAction();
   }

   /**
    * Method description
    *
    *
    * @param graphic128x128
    */
   public void setGraphic128x128(Node graphic128x128) {
      this.graphic128x128 = graphic128x128;
   }

   /**
    * Method description
    *
    *
    * @param graphic16x16
    */
   public void setGraphic16x16(Node graphic16x16) {
      this.graphic16x16 = graphic16x16;
   }

   /**
    * Method description
    *
    *
    * @param graphic24x24
    */
   public void setGraphic24x24(Node graphic24x24) {
      this.graphic24x24 = graphic24x24;
   }

   /**
    * Method description
    *
    *
    * @param graphic32x32
    */
   public void setGraphic32x32(Node graphic32x32) {
      this.graphic32x32 = graphic32x32;
   }

   /**
    * Method description
    *
    *
    * @param graphic512x512
    */
   public void setGraphic512x512(Node graphic512x512) {
      this.graphic512x512 = graphic512x512;
   }
}
