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



package org.ihtsdo.ttk.pl.fx.concept.details;

//~--- non-JDK imports --------------------------------------------------------

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.ObservableList;

import javafx.event.EventHandler;

import javafx.geometry.Point2D;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.ihtsdo.otf.tcc.api.chronicle.ComponentVersionBI;
import org.ihtsdo.otf.tcc.api.coordinate.EditCoordinate;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;
import org.ihtsdo.ttk.logic.DefinitionPart;
import org.ihtsdo.ttk.logic.DefinitionPartType;
import org.ihtsdo.otf.tcc.lookup.Looker;
import org.ihtsdo.ttk.pl.fx.helper.Drag;
import org.ihtsdo.ttk.services.action.ActionBI;
import org.ihtsdo.ttk.services.action.ActionContextBI;
import org.ihtsdo.ttk.services.action.ActionServiceBI;
import org.ihtsdo.ttk.services.action.InterfaceContext;

//~--- JDK imports ------------------------------------------------------------

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 *
 * @author kec
 */
public class ContextualDragAndDropNode extends Label implements ActionContextBI {

   /** Field description */
   private static Set<ContextualDragAndDropNode> contextualNodeSet =
      Collections.newSetFromMap(new WeakHashMap<ContextualDragAndDropNode, Boolean>());

   /** Field description */
   double xTranslation = 140;

   /** Field description */
   double yTranslation = 40;

   /** Field description */
   UUID actionContextUuid = UUID.randomUUID();

   /** Field description */
   DefinitionPart part;

   /** Field description */
   DefinitionPartType partType;

   /** Field description */
   Rectangle dropNode;

   /** Field description */
   TaskBarTransition growTransition;

   /** Field description */
   EnumSet<InterfaceContext> contextSet;

   /** Field description */
   ViewCoordinate viewCoordinate;

   /** Field description */
   EditCoordinate editCoordinate;

   /** Field description */
   ObservableList<ActionBI> contextualActions;

   /**
    * Constructs ...
    *
    *
    * @param part
    * @param partType
    * @param contextSet
    * @param vc
    * @param ec
    */
   public ContextualDragAndDropNode(DefinitionPart part, DefinitionPartType partType,
                                    EnumSet<InterfaceContext> contextSet, ViewCoordinate vc,
                                    EditCoordinate ec) {
      setup(part, partType, contextSet, vc, ec);

   }

   /**
    * Constructs ...
    *
    *
    * @param text
    * @param part
    * @param partType
    * @param contextSet
    * @param vc
    * @param ec
    */
   public ContextualDragAndDropNode(String text, DefinitionPart part, DefinitionPartType partType,
                                    EnumSet<InterfaceContext> contextSet, ViewCoordinate vc,
                                    EditCoordinate ec) {
      super(text);
      setup(part, partType, contextSet, vc, ec);
   }

   /**
    * Constructs ...
    *
    *
    * @param text
    * @param graphic
    * @param part
    * @param partType
    * @param contextSet
    * @param vc
    * @param ec
    */
   public ContextualDragAndDropNode(String text, Node graphic, DefinitionPart part,
                                    DefinitionPartType partType, EnumSet<InterfaceContext> contextSet,
                                    ViewCoordinate vc, EditCoordinate ec) {
      super(text, graphic);
      setup(part, partType, contextSet, vc, ec);
   }

   /**
    * Method description
    *
    */
   public void disableDrop() {
      if (this.getScene() != null) {
         dropNode.setVisible(false);
         ((AnchorPane) getScene().getRoot()).getChildren().remove(dropNode);
      }
   }

   /**
    * Method description
    *
    */
   public void enableDrop() {
      if (this != Drag.getDragComponent()) {
         if (this.getScene() != null) {
            ((AnchorPane) getScene().getRoot()).getChildren().add(dropNode);
            dropNode.setVisible(true);

            Point2D scenePt = getParent().localToScene(this.getBoundsInParent().getMinX()
                                 - dropNode.getWidth(), this.getBoundsInParent().getMinY());

            dropNode.relocate(scenePt.getX(), scenePt.getY());
            growTransition = new TaskBarTransition(xTranslation, yTranslation, scenePt.getX(),
                scenePt.getY(), dropNode);
         }
      }
   }

   /**
    * Method description
    *
    *
    * @param part
    * @param partType
    * @param contextSet
    * @param vc
    * @param ec
    */
   private void setup(DefinitionPart part, DefinitionPartType partType, EnumSet<InterfaceContext> contextSet,
                      ViewCoordinate vc, EditCoordinate ec) {
      this.part       = part;
      this.partType   = partType;
      this.contextSet = contextSet.clone();
      this.contextSet.add(InterfaceContext.COMPONENT_PANEL);
      this.viewCoordinate = vc;
      this.editCoordinate = ec;
      this.setId(actionContextUuid.toString());
      setCursor(Cursor.OPEN_HAND);
      dropNode = new Rectangle(0, 0, 10, 10);
      dropNode.setOpacity(0.5);
      dropNode.getStyleClass().add("dl-drop-disclosure");
      contextualNodeSet.add(this);
      setOnDragDetected(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent event) {
            System.out.println("OnDragDetected: " + getText());

            SnapshotParameters snapParams = new SnapshotParameters();

            snapParams.setFill(Color.TRANSPARENT);

            ImageView dragImageView = new ImageView();

            dragImageView.setImage(snapshot(snapParams, null));
            dragImageView.setOpacity(0.5);
            ((AnchorPane) getScene().getRoot()).getChildren().add(dragImageView);
            ((Node) event.getSource()).setCursor(Cursor.CLOSED_HAND);
            Drag.startDrag(ContextualDragAndDropNode.this, ContextualDragAndDropNode.this.part,
                           dragImageView);
            startFullDrag();

            for (ContextualDragAndDropNode node : contextualNodeSet) {
               node.enableDrop();
            }
         }
      });
      setOnMouseReleased(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent me) {
            System.out.println("OnMouseReleased: " + getText());
            ((Node) me.getSource()).setCursor(Cursor.OPEN_HAND);
            getStyleClass().remove("dl-drag-accept");

            if (Drag.getDragImageView() != null) {
               Drag.getDragImageView().setVisible(false);
               ((AnchorPane) getScene().getRoot()).getChildren().remove(Drag.getDragImageView());
               Drag.endDrag();
            }

            for (ContextualDragAndDropNode node : contextualNodeSet) {
               node.disableDrop();
            }
         }
      });
      setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {
         @Override
         public void handle(MouseDragEvent t) {
            System.out.println("OnMouseDragEntered: " + getText());

            if (t.getGestureSource() != t.getTarget()) {
               getStyleClass().add("dl-drag-accept");
            }

            growTransition.grow();
            growTransition.play();
         }
      });
      setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
         @Override
         public void handle(MouseDragEvent t) {
            getStyleClass().remove("dl-drag-accept");
            System.out.println("OnMouseDragExited: " + getText());
            growTransition.stop();
            growTransition.shrink();
            growTransition.play();
         }
      });
      setOnMouseDragged(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent event) {
            if (Drag.getDragImageView() != null) {
               Drag.getDragImageView().relocate(event.getSceneX(), event.getSceneY());
            }

            // event.consume();
         }
      });
      sceneProperty().addListener(new ChangeListener<Scene>() {
         @Override
         public void changed(ObservableValue<? extends Scene> ov, Scene oldValue, Scene newValue) {
            if (newValue == null) {
               contextualActions = null;
               Looker.lookup(ActionServiceBI.class).retractTargetContext(ContextualDragAndDropNode.this);
            } else {
               contextualActions =
                  Looker.lookup(ActionServiceBI.class).insertTargetContext(ContextualDragAndDropNode.this);
            }
         }
      });
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public UUID getActionContextUuid() {
      return this.actionContextUuid;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public ComponentVersionBI getComponentForContext() {
      return part.getRefexVersion();
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public EditCoordinate getEditCoordinate() {
      return this.editCoordinate;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public EnumSet<InterfaceContext> getInterfaceContextSet() {
      return contextSet;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public List<ActionContextBI> getLinkedContexts() {
      return Collections.EMPTY_LIST;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public ViewCoordinate getViewCoordinate() {
      return this.viewCoordinate;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public boolean hasFocus() {
      return hasFocus();
   }

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public boolean isSelected() {
      return isSelected();
   }
}
