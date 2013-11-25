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
import javafx.scene.image.ImageView;

import org.ihtsdo.otf.tcc.api.chronicle.ComponentBI;

/**
 *
 * @author kec
 */
public class Drag {

   /** Field description */
   private static Node dragOrigin;

   /** Field description */
   private static Object dragComponent;

   /** Field description */
   private static ImageView dragImageView;

   /**
    * Method description
    *
    */
   public static void endDrag() {
      Drag.dragOrigin    = null;
      Drag.dragComponent = null;
      Drag.dragImageView = null;
   }

   /**
    * Method description
    *
    *
    * @param dragOrigin
    * @param dragComponent
    * @param dragImageView
    */
   public static void startDrag(Node dragOrigin, Object dragComponent, ImageView dragImageView) {
      assert dragOrigin != null;
      assert dragComponent != null;
      assert dragImageView != null;

      if (Drag.dragOrigin != null) {
         throw new IllegalStateException("dragOrigin is not null: " + Drag.dragOrigin);
      }

      if (Drag.dragComponent != null) {
         throw new IllegalStateException("dragComponent is not null: " + Drag.dragComponent);
      }

      if (Drag.dragImageView != null) {
         throw new IllegalStateException("dragImageView is not null: " + Drag.dragImageView);
      }

      Drag.dragOrigin    = dragOrigin;
      Drag.dragComponent = dragComponent;
      Drag.dragImageView = dragImageView;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public static Object getDragComponent() {
      return dragComponent;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public static ImageView getDragImageView() {
      return dragImageView;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public static Node getDragOrigin() {
      return dragOrigin;
   }
}
