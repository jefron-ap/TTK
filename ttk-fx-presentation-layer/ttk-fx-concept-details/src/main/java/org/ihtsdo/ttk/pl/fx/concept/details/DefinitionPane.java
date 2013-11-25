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

import javafx.beans.property.ObjectProperty;

import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;

import javafx.event.EventHandler;

import javafx.fxml.FXMLLoader;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;
import org.ihtsdo.ttk.logic.DefinitionPart;
import org.ihtsdo.ttk.logic.DefinitionPartType;
import org.ihtsdo.ttk.logic.DefinitionTree;

import static javafx.geometry.HPos.LEFT;
import static javafx.geometry.HPos.RIGHT;
import static javafx.geometry.VPos.BOTTOM;
import static javafx.geometry.VPos.CENTER;
import static javafx.geometry.VPos.TOP;

import static org.ihtsdo.ttk.logic.DefinitionPartType.AND;
import static org.ihtsdo.ttk.logic.DefinitionPartType.CONCEPT_REFERENCE_DEFINED;
import static org.ihtsdo.ttk.logic.DefinitionPartType.DEFINITION_ROOT;
import static org.ihtsdo.ttk.logic.DefinitionPartType.DISJOINT_WITH;
import static org.ihtsdo.ttk.logic.DefinitionPartType.EDGE_FALSE;
import static org.ihtsdo.ttk.logic.DefinitionPartType.EDGE_TRUE;
import static org.ihtsdo.ttk.logic.DefinitionPartType.EXISTENTIAL_RESTRICTION;
import static org.ihtsdo.ttk.logic.DefinitionPartType.FEATURE_FLOAT;
import static org.ihtsdo.ttk.logic.DefinitionPartType.FEATURE_INT;
import static org.ihtsdo.ttk.logic.DefinitionPartType.FEATURE_LONG;
import static org.ihtsdo.ttk.logic.DefinitionPartType.FIELD_SUBSTITUTION;
import static org.ihtsdo.ttk.logic.DefinitionPartType.NECESSARY_SET;
import static org.ihtsdo.ttk.logic.DefinitionPartType.OR;
import static org.ihtsdo.ttk.logic.DefinitionPartType.ROLE_GROUP;
import static org.ihtsdo.ttk.logic.DefinitionPartType.SUFFICIENT_SET;
import static org.ihtsdo.ttk.logic.DefinitionPartType.UNIVERSAL_RESTRICTION;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;


import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ihtsdo.otf.tcc.api.coordinate.EditCoordinate;
import org.ihtsdo.ttk.pl.fx.helper.Drag;
import org.ihtsdo.ttk.services.action.InterfaceContext;

/**
 *
 * @author kec
 */
public class DefinitionPane extends Pane {

   /** Field description */
   private static int NODE_WIDTH = 125;

   /**
    * Sentinel value which may be set on a child's row/column span constraint to
    * indicate that it should span the remaining rows/columns.
    */
   public static final int REMAINING = Integer.MAX_VALUE;

   /**
    *  BEGIN static methods
    */
   private static final String MARGIN_CONSTRAINT = "defpane-margin";

   /** Field description */
   private static final String HALIGNMENT_CONSTRAINT = "defpane-halignment";

   /** Field description */
   private static final String VALIGNMENT_CONSTRAINT = "defpane-valignment";

   /** Field description */
   private static final String ROW_INDEX_CONSTRAINT = "defpane-row";

   /** Field description */
   private static final String COLUMN_INDEX_CONSTRAINT = "defpane-column";

   /** Field description */
   private static final String ROW_SPAN_CONSTRAINT = "defpane-row-span";

   /** Field description */
   private static final String COLUMN_SPAN_CONSTRAINT = "defpane-column-span";

   /** Field description */
   private static final Color GRID_LINE_COLOR = Color.rgb(30, 30, 30);

   /** Field description */
   private static final double GRID_LINE_DASH = 3;

   /** Field description */
   private static final String PART_FOR_NODE = "part for node";

   /** Field description */
   private static Insets standardInset = new Insets(2, 0, 2, 0);

   /** Field description */
   private boolean metricsDirty = true;

   // This is set to true while in layoutChildren and set false on the conclusion.
   // It is used to decide whether to update metricsDirty in requestLayout().

   /** Field description */
   private boolean performingLayout = false;

   /**
    * Edges of the definition tree.
    */
   private Group edges = new Group();

   /** Field description */
   private final Map<DefinitionPart, Node> partNodeMap = new HashMap<>();

   /** Field description */
   private DefinitionTree definitionTree;
   private EditCoordinate editCoordinate;

   /** Field description */
   private ObjectProperty<Pos> alignment;

   /** Field description */
   private Group gridLines;

   /** Field description */
   private double[] rowPrefHeight;

   /** Field description */
   private double[] rowBaseline;

   /** Field description */
   private double[] rowHeights;

   /** Field description */
   private double[] rowMaxY;

   /** Field description */
   private double[] columnPrefWidth;

   /** Field description */
   private double[] columnMaxX;
   
   EnumSet<InterfaceContext> contextSet;

   {
      getStyleClass().add("dl-grid");
      setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
   }

   /**
    *  END static methods
    */

   /**
    * Creates a DefinitionPane layout with localHGap/vgap = 0 and TOP_LEFT alignment.
    */
   public DefinitionPane() {
      super();
      this.contextSet = EnumSet.of(InterfaceContext.CONCEPT_DETAILS_PANEL, InterfaceContext.CONCEPT_DEFINITION_PANEL);
      setOnDragOver(new EventHandler<DragEvent>() {
         @Override
         public void handle(DragEvent event) {
            System.out.println("OnDragOver2: " + event.getSceneX() + "," + event.getSceneY());

            if (Drag.getDragImageView() != null) {
               Point2D localPoint = sceneToLocal(new Point2D(event.getSceneX(), event.getSceneY()));

               Drag.getDragImageView().relocate((int) localPoint.getX(), (int) localPoint.getY());
            }
         }
      });
      getChildren().addListener(new ListChangeListener<Node>() {
         @Override
         public void onChanged(Change<? extends Node> c) {
            requestLayout();
         }
      });
   }

   /**
    * Adds a child to the defpane at the specified column,row position.
    * This convenience method will set the defpane column and row constraints
    * on the child.
    * @param child the parentNode being added to the defpane
    * @param columnIndex the column index position for the child within the defpane
    * @param rowIndex the row index position for the child within the defpane
    */
   public void add(Node child, int columnIndex, int rowIndex) {
      setConstraints(child, columnIndex, rowIndex);
      getChildren().add(child);
   }

   /**
    * Method description
    *
    */
   private void addDefinitionTreeEdges() {
      this.getChildren().remove(this.edges);
      this.edges.getChildren().clear();

      // Add edges here.
      for (Node parentNode : getChildren()) {
         try {
            DefinitionPart defPart =
               (DefinitionPart) parentNode.getProperties().get(DefinitionPane.PART_FOR_NODE);

            if ((defPart != null) && isOriginNodeOrDestinationNode(defPart)) {
               Integer columnIndex    = getColumnIndex(parentNode);
               Integer columnSpan     = getColumnSpan(parentNode);
               Integer endColumnIndex = columnIndex + columnSpan - 1;
               Integer rowIndex       = getRowIndex(parentNode);
               double  startX         = columnMaxX[endColumnIndex];
               double  startY         = Math.ceil(getRowAverage(rowIndex));

               for (DefinitionPart child : getDestinationNodes(defPart)) {
                  Node   childNode = partNodeMap.get(child);
                  double endX      = columnMaxX[getColumnIndex(childNode) - 1];
                  double endY      = Math.ceil(getRowAverage(getRowIndex(childNode)));
                  Path   path      = new Path();

                  path.getElements().add(new MoveTo(startX, startY));
                  path.getElements().add(new LineTo(startX, endY));
                  path.getElements().add(new LineTo(endX, endY));
                  edges.getChildren().add(path);
               }
            }
         } catch (IOException | ContradictionException ex) {
            Logger.getLogger(DefinitionPane.class.getName()).log(Level.SEVERE, null, ex);
         }
      }

      edges.setLayoutX(0);
      edges.setLayoutY(0);
      this.getChildren().add(this.edges);
   }

   /**
    * Method description
    *
    *
    * @param areaWidths
    * @param width
    *
    * @return
    */
   private double adjustColumnWidths(double areaWidths[], double width) {
      final double snaphgap     = snapSpace(getHgap());
      final double left         = snapSpace(getInsets().getLeft());
      final double right        = snapSpace(getInsets().getRight());
      final int    numColumns   = columnPrefWidth.length;
      final double hgaps        = snaphgap * (numColumns - 1);
      double       columnTotal  = hgaps;

      // compute non-percentage column widths
      for (int i = 0; i < numColumns; i++) {
         columnPrefWidth[i] = Math.min(areaWidths[i], columnPrefWidth[i]);
         columnTotal     += columnPrefWidth[i];
      }

      double widthAvailable = ((width == -1)
                               ? prefWidth(-1)
                               : width) - left - right - columnTotal;

      // now that both fixed and percentage columns have been computed, divy up any surplus or deficit
      if (widthAvailable != 0) {

         // maybe grow or shrink column widths
         double remaining = growOrShrinkColumnWidths(Priority.ALWAYS, widthAvailable);

         remaining   = growOrShrinkColumnWidths(Priority.SOMETIMES, remaining);
         columnTotal += (widthAvailable - remaining);
      }

      return columnTotal;
   }

   /**
    * Method description
    *
    *
    * @param areaHeights
    * @param height
    *
    * @return
    */
   private double adjustRowHeights(double areaHeights[], double height) {
      final double snapvgap = snapSpace(getVgap());
      final int    numRows  = rowHeights.length;
      final double vgaps    = snapvgap * (numRows - 1);
      double       rowTotal = vgaps;

      // compute non-percentage row heights
      for (int i = 0; i < numRows; i++) {
         rowHeights[i] = Math.min(areaHeights[i], rowPrefHeight[i]);

         if (i > 0) {
            rowMaxY[i] = rowHeights[i] + rowMaxY[i - 1];
         } else {
            rowMaxY[i] = rowHeights[i];
         }

         rowTotal += rowHeights[i];
      }

      return rowTotal;
   }

   /**
    * Method description
    *
    *
    * @param value
    * @param min
    * @param max
    *
    * @return
    */
   static double boundedSize(double value, double min, double max) {

      // if max < value, return max
      // if min > value, return min
      // if min > max, return min
      return Math.min(Math.max(value, min), Math.max(min, max));
   }


   /**
    * Method description
    *
    *
    * @param child
    * @param margin
    * @param width
    *
    * @return
    */
   private double computeChildPrefAreaHeight(Node child, Insets margin, double width) {
      double top    = (margin != null)
                      ? Math.ceil(margin.getTop())
                      : 0;
      double bottom = (margin != null)
                      ? Math.ceil(margin.getBottom())
                      : 0;
      double left   = (margin != null)
                      ? Math.ceil(margin.getLeft())
                      : 0;
      double right  = (margin != null)
                      ? Math.ceil(margin.getRight())
                      : 0;
      double alt    = -1;

      if (child.getContentBias() == Orientation.HORIZONTAL) {    // width depends on height
         alt = snapSize(boundedSize((width != -1)
                                    ? width - left - right
                                    : child.prefWidth(-1), child.minWidth(-1), child.maxWidth(-1)));
      }

      return top + snapSize(boundedSize(child.prefHeight(alt), child.minHeight(alt), child.maxHeight(alt)))
             + bottom;
   }

   /**
    * Method description
    *
    *
    * @param child
    * @param margin
    * @param height
    *
    * @return
    */
   private double computeChildPrefAreaWidth(Node child, Insets margin, double height) {
      double top    = (margin != null)
                      ? Math.ceil(margin.getTop())
                      : 0;
      double bottom = (margin != null)
                      ? Math.ceil(margin.getBottom())
                      : 0;
      double left   = (margin != null)
                      ? Math.ceil(margin.getLeft())
                      : 0;
      double right  = (margin != null)
                      ? Math.ceil(margin.getRight())
                      : 0;
      double alt    = -1;

      if (child.getContentBias() == Orientation.VERTICAL) {    // width depends on height
         alt = snapSize(boundedSize((height != -1)
                                    ? height - top - bottom
                                    : child.prefHeight(-1), child.minHeight(-1), child.maxHeight(-1)));
      }

      return left + snapSize(boundedSize(child.prefWidth(alt), child.minWidth(alt), child.maxWidth(alt)))
             + right;
   }

   /**
    * Method description
    *
    *
    * @param numColumns
    * @param heights
    */
   private void computeColumnMetrics(int numColumns, double heights[]) {
      columnPrefWidth = createDoubleArray(numColumns, 0);
      columnMaxX      = createDoubleArray(numColumns, 0);

      final double snaphgap = snapSpace(getHgap());

      for (int i = 0; i < numColumns; i++) {
         List<Node> startNodes  = new ArrayList<>();
         List<Node> endNodes    = new ArrayList<>();

         for (int j = 0; j < getChildren().size(); j++) {
            Node child = getChildren().get(j);

            if (child.isManaged()) {
               if (getNodeColumnIndex(child) == i) {
                  startNodes.add(child);
               }

               int columnEnd = getNodeColumnEnd(child);

               if (((columnEnd == REMAINING) && (i == (numColumns - 1))) || (columnEnd == i)) {
                  endNodes.add(child);
               }
            }
         }


            // compute from content
            for (int j = 0; j < endNodes.size(); j++) {
               Node   child       = endNodes.get(j);
               Insets margin      = getMargin(child);
               int    columnIndex = getNodeColumnIndex(child);
               int    colspan     = getNodeColumnSpan(child);

               if (colspan == REMAINING) {
                  colspan = numColumns - columnIndex;
               }

               int rowIndex = getNodeRowIndex(child);

               
                  double preferredWidth = computeChildPrefAreaWidth(child, margin, heights[rowIndex]);

                  if (colspan > 1) {
                     double w = 0.0f;

                     for (int k = columnIndex; k < columnIndex + colspan - 1; k++) {
                        w += columnPrefWidth[k];
                     }

                     preferredWidth -= w + ((colspan - 1) * snaphgap);
                  }

                  columnPrefWidth[i] = Math.max(columnPrefWidth[i], preferredWidth);
               
            }
         

//       System.out.println("computeColumnMetrics: column " + i + ": h=" + columnWidths[i] + " min="
//                          + columnMinWidth[i] + " pref=" + columnPrefWidth[i]);
      }
   }

   /**
    * Method description
    *
    */
   private void computeGridMetrics() {
      if (metricsDirty) {
         int numRows    = 0;
         int numColumns = 0;

         for (int i = 0; i < getChildren().size(); i++) {
            Node child = getChildren().get(i);

            if (child.isManaged()) {
               int rowIndex    = getNodeRowIndex(child);
               int columnIndex = getNodeColumnIndex(child);
               int rowEnd      = getNodeRowEnd(child);
               int columnEnd   = getNodeColumnEnd(child);

               numRows    = Math.max(numRows, ((rowEnd != REMAINING)
                                               ? rowEnd
                                               : rowIndex) + 1);
               numColumns = Math.max(numColumns, ((columnEnd != REMAINING)
                                                  ? columnEnd
                                                  : columnIndex) + 1);
            }
         }

         // println("computeGridMetrics: rows={numRows} columns={numColumns}");
         computeRowMetrics(numRows, createDoubleArray(numColumns, -1));
         computeColumnMetrics(numColumns, createDoubleArray(numRows, -1));
         metricsDirty = false;
      }
   }

   /**
    * Method description
    *
    *
    * @param width
    *
    * @return
    */
   @Override
   protected double computeMinHeight(double width) {
      return computePrefHeight(width);
   }

   /**
    * Method description
    *
    *
    * @param height
    *
    * @return
    */
   @Override
   protected double computeMinWidth(double height) {
      return computePrefWidth(height);
   }

   /**
    * Method description
    *
    *
    * @param width
    *
    * @return
    */
   @Override
   protected double computePrefHeight(double width) {
      computeGridMetrics();

      if (getContentBias() == Orientation.HORIZONTAL) {
         adjustColumnWidths(columnPrefWidth, width);
         computeRowMetrics(rowHeights.length, columnPrefWidth);
      }

      addDefinitionTreeEdges();

      return snapSpace(getInsets().getTop())
             + (computeTotalHeight(rowPrefHeight) + (rowPrefHeight.length - 1) * snapSpace(getVgap()))
             + snapSpace(getInsets().getBottom());
   }

   /**
    * Method description
    *
    *
    * @param height
    *
    * @return
    */
   @Override
   protected double computePrefWidth(double height) {
      computeGridMetrics();

      if (getContentBias() == Orientation.VERTICAL) {
         adjustRowHeights(rowPrefHeight, height);
         computeColumnMetrics(columnPrefWidth.length, rowHeights);
      }

      addDefinitionTreeEdges();

      return snapSpace(getInsets().getLeft())
             + (computeTotalWidth(columnPrefWidth) + (columnPrefWidth.length - 1) * snapSpace(getHgap()))
             + snapSpace(getInsets().getRight());
   }

   /**
    * Method description
    *
    *
    * @param numRows
    * @param widths
    */
   private void computeRowMetrics(int numRows, double widths[]) {
      rowPrefHeight = createDoubleArray(numRows, 0);
      rowHeights    = createDoubleArray(numRows, 0);
      rowMaxY       = createDoubleArray(numRows, 0);
      rowBaseline   = createDoubleArray(numRows, 0);

      double snapvgap = snapSpace(getVgap());

      for (int i = 0; i < numRows; i++) {
         boolean    computePref = true;
         List<Node> startNodes  = new ArrayList<>();
         List<Node> endNodes    = new ArrayList<>();

         for (int j = 0; j < getChildren().size(); j++) {
            Node child = getChildren().get(j);

            if (child.isManaged()) {
               if (getNodeRowIndex(child) == i) {
                  startNodes.add(child);
               }

               int rowEnd = getNodeRowEnd(child);

               if (((rowEnd == REMAINING) && (i == (numRows - 1))) || (rowEnd == i)) {
                  endNodes.add(child);
               }
            }
         }

         VPos       rowVPos       = VPos.CENTER;
         Insets     margins[]     = new Insets[startNodes.size()];
         List<Node> baselineNodes = new ArrayList<>();

         for (int j = 0, k = 0; j < startNodes.size(); j++) {
            Node n = startNodes.get(j);
         }

         rowBaseline[i] = getMaxAreaBaselineOffset(baselineNodes, margins);
         baselineNodes.clear();

         if (computePref || (rowVPos == VPos.BASELINE)) {

            // compute from content
            for (int j = 0; j < endNodes.size(); j++) {
               Node   child      = endNodes.get(j);
               Insets margin     = getMargin(child);
               double top        = (margin != null)
                                   ? margin.getTop()
                                   : 0;
               int    rowIndex   = getNodeRowIndex(child);
               int    rowspan    = getNodeRowSpan(child);
               int    columnSpan = getNodeColumnSpan(child);

               if (rowspan == REMAINING) {
                  rowspan = numRows - rowIndex;
               }

               int colIndex = getNodeColumnIndex(child);

               if (computePref) {
                  double widthOfColumnSpan = widths[colIndex];

                  for (int columnSpanIndex = 1; columnSpanIndex < columnSpan; columnSpanIndex++) {
                     widthOfColumnSpan += widths[colIndex + columnSpanIndex];
                  }

                  double preferredHeight = computeChildPrefAreaHeight(child, margin, widthOfColumnSpan);

                  if (rowspan > 1) {
                     double h = 0.0f;

                     for (int k = rowIndex; k < rowIndex + rowspan - 1; k++) {
                        h += rowPrefHeight[k];
                     }

                     preferredHeight -= h + ((rowspan - 1) * snapvgap);
                  } else if (rowVPos == VPos.BASELINE) {
                     preferredHeight = rowBaseline[i] + (preferredHeight - child.getBaselineOffset() - top);
                  }

                  rowPrefHeight[i] = Math.max(rowPrefHeight[i], preferredHeight);
               }
            }
         }

//       System.out.println("computeRowMetrics: row " + i + ": h=" + rowHeights[i] + " min="
//                          + rowMinHeight[i] + " pref=" + rowPrefHeight[i]);
      }
   }

   /**
    * Method description
    *
    *
    * @param heights
    *
    * @return
    */
   private double computeTotalHeight(double heights[]) {
      double totalHeight = 0;

      for (int i = 0; i < heights.length; i++) {
         totalHeight += heights[i];
      }

      return totalHeight;
   }

   /**
    * Method description
    *
    *
    * @param widths
    *
    * @return
    */
   private double computeTotalWidth(double widths[]) {
      double totalWidth = 0;

      for (int i = 0; i < widths.length; i++) {
         totalWidth += widths[i];
      }

      return totalWidth;
   }

   /**
    * Method description
    *
    *
    * @param width
    * @param contentWidth
    * @param hpos
    *
    * @return
    */
   private static double computeXOffset(double width, double contentWidth, HPos hpos) {
      switch (hpos) {
      case LEFT :
         return 0;

      case CENTER :
         return (width - contentWidth) / 2;

      case RIGHT :
         return width - contentWidth;
      }

      return 0;
   }

   /**
    * Method description
    *
    *
    * @param height
    * @param contentHeight
    * @param vpos
    *
    * @return
    */
   private static double computeYOffset(double height, double contentHeight, VPos vpos) {
      switch (vpos) {
      case TOP :
         return 0;

      case CENTER :
         return (height - contentHeight) / 2;

      case BOTTOM :
         return height - contentHeight;
      }

      return 0;
   }

   /**
    * Method description
    *
    *
    * @param length
    * @param value
    *
    * @return
    */
   private static double[] createDoubleArray(int length, double value) {
      double[] array = new double[length];

      for (int i = 0; i < length; i++) {
         array[i] = value;
      }

      return array;
   }

   /**
    * Method description
    *
    *
    * @param startX
    * @param startY
    * @param endX
    * @param endY
    *
    * @return
    */
   private Line createGridLine(double startX, double startY, double endX, double endY) {
      Line line = new Line();

      line.setStrokeWidth(0.1);
      line.setStartX(startX);
      line.setStartY(startY);
      line.setEndX(endX);
      line.setEndY(endY);
      line.setStroke(GRID_LINE_COLOR);
      line.setStrokeDashOffset(GRID_LINE_DASH);

      return line;
   }

   /**
    * Method description
    *
    *
    * @param priority
    * @param extraWidth
    *
    * @return
    */
   private double growOrShrinkColumnWidths(Priority priority, double extraWidth) {
      final boolean shrinking = extraWidth < 0;
      List<Integer> adjusting = new ArrayList<>();
      List<Integer> adjusted  = new ArrayList<>();

      for (int i = 0; i < columnPrefWidth.length; i++) {
         if (shrinking) {
            adjusting.add(i);
         }
      }

      double  available       = extraWidth;    // will be negative in shrinking case
      boolean handleRemainder = false;
      int     portion         = 0;

      while ((available != 0) && (adjusting.size() > 0)) {
         if (!handleRemainder) {
            portion = (int) available / adjusting.size();    // negative in shrinking case
         }

         if (portion != 0) {
            for (int i = 0; i < adjusting.size(); i++) {
               final int    index = adjusting.get(i);
               final double limit = (shrinking
                                     ? columnPrefWidth[index]
                                     : columnPrefWidth[index]) - columnPrefWidth[index];    // negative in shrinking case
               final double change = (Math.abs(limit) <= Math.abs(portion))
                                     ? limit
                                     : portion;

               columnPrefWidth[index] += change;

               // if (parentNode.id.startsWith("debug.")) println("{if (shrinking) "vshrink" else "vgrow"}: {parentNode.id} portion({portion})=available({available})/({sizeof adjusting}) change={change}");
               available -= change;

               if (Math.abs(change) < Math.abs(portion)) {
                  adjusted.add(index);
               }

               if (available == 0) {
                  break;
               }
            }

            for (int i = 0; i < adjusted.size(); i++) {
               adjusting.remove(adjusted.get(i));
            }

            adjusted.clear();
         } else {

            // Handle the remainder
            portion = (int) (available) % adjusting.size();

            if (portion == 0) {
               break;
            } else {

               // We have a remainder evenly distribute it.
               portion         = shrinking
                                 ? -1
                                 : 1;
               handleRemainder = true;
            }
         }
      }

      for (int i = 0; i < columnPrefWidth.length; i++) {
         columnPrefWidth[i] = Math.ceil(columnPrefWidth[i]);

         if (i == 0) {
            columnMaxX[i] = columnPrefWidth[i];
         } else {
            columnMaxX[i] = columnMaxX[i - 1] + columnPrefWidth[i];
         }
      }

      return available;    // might be negative in shrinking case
   }

   /**
    * Method description
    *
    */
   @Override
   protected void layoutChildren() {
      performingLayout = true;

      final double snaphgap      = snapSpace(getHgap());
      final double snapvgap      = snapSpace(getVgap());
      final double top           = snapSpace(getInsets().getTop());
      final double bottom        = snapSpace(getInsets().getBottom());
      final double left          = snapSpace(getInsets().getLeft());
      final double right         = snapSpace(getInsets().getRight());
      final double width         = getWidth();
      final double height        = getHeight();    // When is height computed?
      final double contentHeight = height - top - bottom;
      final double contentWidth  = width - left - right;
      double       columnTotal   = 0;
      double       rowTotal      = 0;

      computeGridMetrics();

      Orientation contentBias = getContentBias();

      if (contentBias == null) {
         rowTotal    = adjustRowHeights(rowPrefHeight, height);
         columnTotal = adjustColumnWidths(columnPrefWidth, width);
      } else if (contentBias == Orientation.HORIZONTAL) {
         columnTotal = adjustColumnWidths(columnPrefWidth, width);
         computeRowMetrics(rowHeights.length, columnPrefWidth);
         rowTotal = adjustRowHeights(rowPrefHeight, height);
      } else if (contentBias == Orientation.VERTICAL) {
         rowTotal = adjustRowHeights(rowPrefHeight, height);
         computeColumnMetrics(columnPrefWidth.length, rowHeights);
         columnTotal = adjustColumnWidths(columnPrefWidth, width);
      }

      final double x = left + computeXOffset(contentWidth, columnTotal, getAlignment().getHpos());
      final double y = top + computeYOffset(contentHeight, rowTotal, getAlignment().getVpos());

      for (int i = 0; i < getChildren().size(); i++) {
         Node child = getChildren().get(i);

         if (child.isManaged()) {
            int    rowIndex    = getNodeRowIndex(child);
            int    columnIndex = getNodeColumnIndex(child);
            int    colspan     = getNodeColumnSpan(child);
            int    rowspan     = getNodeRowSpan(child);
            double areaX       = x;

            for (int j = 0; j < columnIndex; j++) {
               areaX += columnPrefWidth[j] + snaphgap;
            }

            double areaY = y;

            for (int j = 0; j < rowIndex; j++) {
               areaY += rowHeights[j] + snapvgap;
            }

            double areaW = columnPrefWidth[columnIndex];

            for (int j = 2; j <= colspan; j++) {
               areaW += columnPrefWidth[columnIndex + j - 1] + snaphgap;
            }

            double areaH = rowHeights[rowIndex];

            for (int j = 2; j <= rowspan; j++) {
               areaH += rowHeights[rowIndex + j - 1] + snapvgap;
            }

            Insets margin = getMargin(child);

            // System.out.println("layoutNode("+child.toString()+" row/span="+rowIndex+"/"+rowspan+" col/span="+columnIndex+"/"+colspan+" area="+areaX+","+areaY+" "+areaW+"x"+areaH+""+" rowBaseline="+rowBaseline[rowIndex]);
            layoutInArea(child, areaX, areaY, areaW, areaH, rowBaseline[rowIndex], margin,
                         true, true, HPos.LEFT,
                         VPos.CENTER);
         }
      }

      edges.getChildren().clear();
      layoutGridLines(x, y, rowTotal, columnTotal);
      addDefinitionTreeEdges();
      performingLayout = false;
   }

   /**
    * Method description
    *
    *
    * @param x
    * @param y
    * @param columnHeight
    * @param rowWidth
    */
   private void layoutGridLines(double x, double y, double columnHeight, double rowWidth) {
      x            = Math.ceil(x);
      y            = Math.ceil(y);
      columnHeight = Math.ceil(columnHeight);
      rowWidth     = Math.ceil(rowWidth);
      getChildren().remove(gridLines);
      gridLines = new Group();
      gridLines.setManaged(false);
      getChildren().add(gridLines);

      if (!gridLines.getChildren().isEmpty()) {
         gridLines.getChildren().clear();
      }

      // create vertical lines
      double linex = x;
      double liney = y;

      for (int i = 0; i <= columnPrefWidth.length; i++) {
         gridLines.getChildren().add(createGridLine(linex, liney, linex, liney + columnHeight));

         if ((i > 0) && (i < columnPrefWidth.length) && (getHgap() != 0)) {
            linex += getHgap();
            gridLines.getChildren().add(createGridLine(linex, liney, linex, liney + columnHeight));
         }

         if (i < columnPrefWidth.length) {
            linex += columnPrefWidth[i];
         }
      }

      // create horizontal lines
      linex = x;

      for (int i = 0; i <= rowHeights.length; i++) {
         gridLines.getChildren().add(createGridLine(linex, liney, linex + rowWidth, liney));

         if ((i > 0) && (i < rowHeights.length) && (getVgap() != 0)) {
            liney += getVgap();
            gridLines.getChildren().add(createGridLine(linex, liney, linex + rowWidth, liney));
         }

         if (i < rowHeights.length) {
            liney += rowHeights[i];
         }
      }
   }

   /**
    * Method description
    *
    */
   @Override
   public void requestLayout() {

      // RT-18878: Do not update metrics dirty if we are performing layout.
      // If metricsDirty is set true during a layout pass the next call to computeGridMetrics()
      // will clear all the cell bounds resulting in out of date info until the
      // next layout pass.
      if (!metricsDirty &&!performingLayout) {
         metricsDirty = true;
      }

      super.requestLayout();
   }

   /**
    * Method description
    *
    *
    * @param part
    * @param definitionTree
    * @param partType
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    */
   private Node setupNode(final DefinitionPart part, DefinitionTree definitionTree,
                          DefinitionPartType partType)
           throws IOException, ContradictionException {
      final ContextualDragAndDropNode partLabel = 
              new ContextualDragAndDropNode(part.getText(definitionTree.getViewCoordinate()), 
              part, partType, contextSet, definitionTree.getViewCoordinate(), editCoordinate);
      setWidthAndStyleClass(partLabel, partType);


      return partLabel;
   }

   /**
    * Method description
    *
    *
    * @param part
    * @param definitionTree
    * @param partType
    * @param graphic
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    */
   private Node setupNodeTextFromNid2(DefinitionPart part, DefinitionTree definitionTree,
                                      DefinitionPartType partType, Node graphic)
           throws IOException, ContradictionException {
      Label partLabel = new Label(part.getTextNid2(definitionTree.getViewCoordinate()), graphic);

      setWidthAndStyleClass(partLabel, partType);

      return partLabel;
   }


   /**
    * Returns a string representation of this {@code DefinitionPane} object.
    * @return a string representation of this {@code DefinitionPane} object.
    */
   @Override
   public String toString() {
      return "Grid hgap=" + getHgap() + ", vgap=" + getVgap() + ", alignment=" + getAlignment();
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public final Pos getAlignment() {
      return (alignment == null)
             ? Pos.TOP_LEFT
             : alignment.get();
   }

   /**
    * Returns the child's column index constraint if set.
    * @param child the child parentNode of a defpane
    * @return the column index for the child or null if no column index was set
    */
   private static Integer getColumnIndex(Node child) {
      return (Integer) getConstraint(child, COLUMN_INDEX_CONSTRAINT);
   }

   /**
    * Returns the child's column-span constraint if set.
    * @param child the child parentNode of a defpane
    * @return the column span for the child or null if no column span was set
    */
   private static Integer getColumnSpan(Node child) {
      return (Integer) getConstraint(child, COLUMN_SPAN_CONSTRAINT);
   }

   /**
    * Method description
    *
    *
    *
    * @param node
    * @param key
    *
    * @return
    */
   private static Object getConstraint(Node node, Object key) {
      if (node.hasProperties()) {
         Object value = node.getProperties().get(key);

         if (value != null) {
            return value;
         }
      }

      return null;
   }

   /**
    *
    * @return null unless one of its children has a content bias.
    */
   @Override
   public Orientation getContentBias() {
      for (int i = 0; i < getChildren().size(); i++) {
         Node child = getChildren().get(i);

         if (child.isManaged() && (child.getContentBias() != null)) {
            return child.getContentBias();
         }
      }

      return null;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public DefinitionTree getDefinitionTree() {
      return definitionTree;
   }

   /**
    * Method description
    *
    *
    * @param defPart
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    */
   List<DefinitionPart> getDestinationNodes(DefinitionPart defPart)
           throws ContradictionException, IOException {
      return getDestinationNodes(defPart, new ArrayList<DefinitionPart>());
   }

   /**
    * Method description
    *
    *
    * @param defPart
    * @param destinationNodes
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    */
   List<DefinitionPart> getDestinationNodes(DefinitionPart defPart,
       ArrayList<DefinitionPart> destinationNodes)
           throws ContradictionException, IOException {
      for (DefinitionPart child : definitionTree.getChildren(defPart)) {
         if (isOriginNodeOrDestinationNode(child)) {
            destinationNodes.add(child);
         } else {
            getDestinationNodes(child, destinationNodes);
         }
      }

      return destinationNodes;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private double getHgap() {
      return 0;
   }

   /**
    * Returns the child's margin constraint if set.
    * @param child the child parentNode of a defpane
    * @return the margin for the child or null if no margin was set
    */
   private static Insets getMargin(Node child) {
      return (Insets) getConstraint(child, MARGIN_CONSTRAINT);
   }

   /**
    * Method description
    *
    *
    * @param content
    * @param margins
    *
    * @return
    */
   private static double getMaxAreaBaselineOffset(List<Node> content, Insets margins[]) {
      double max = 0;

      for (int i = 0; i < content.size(); i++) {
         Node   node   = content.get(i);
         Insets margin = (margins[i] != null)
                         ? margins[i]
                         : Insets.EMPTY;

         max = Math.max(max, ((margin != null)
                              ? margin.getTop()
                              : 0) + node.getBaselineOffset());
      }

      return max;
   }

   /* utility method for computing the max of children's min or pref heights, taking into account baseline alignment */

   /**
    * Method description
    *
    *
    *
    * @param node
    *
    * @return
    */
   private static int getNodeColumnEnd(Node node) {
      int columnSpan = getNodeColumnSpan(node);

      return (columnSpan != REMAINING)
             ? getNodeColumnIndex(node) + columnSpan - 1
             : REMAINING;
   }

   /**
    * Method description
    *
    *
    *
    * @param node
    *
    * @return
    */
   private static int getNodeColumnIndex(Node node) {
      Integer columnIndex = getColumnIndex(node);

      return (columnIndex != null)
             ? columnIndex
             : 0;
   }

   /**
    * Method description
    *
    *
    *
    * @param node
    *
    * @return
    */
   private static int getNodeColumnSpan(Node node) {
      Integer colspan = getColumnSpan(node);

      return (colspan != null)
             ? colspan
             : 1;
   }

   /**
    * Method description
    *
    *
    *
    * @param node
    *
    * @return
    */
   private static int getNodeRowEnd(Node node) {
      int rowSpan = getNodeRowSpan(node);

      return (rowSpan != REMAINING)
             ? getNodeRowIndex(node) + rowSpan - 1
             : REMAINING;
   }

   /**
    * Method description
    *
    *
    *
    * @param node
    *
    * @return
    */
   private static int getNodeRowIndex(Node node) {
      Integer rowIndex = getRowIndex(node);

      return (rowIndex != null)
             ? rowIndex
             : 0;
   }

   /**
    * Method description
    *
    *
    *
    * @param node
    *
    * @return
    */
   private static int getNodeRowSpan(Node node) {
      Integer rowspan = getRowSpan(node);

      return (rowspan != null)
             ? rowspan
             : 1;
   }

   /**
    * Method description
    *
    *
    * @param rowIndex
    *
    * @return
    */
   private double getRowAverage(int rowIndex) {
      return (getRowStart(rowIndex) + getRowEnd(rowIndex)) / 2;
   }

   /**
    * Method description
    *
    *
    * @param rowIndex
    *
    * @return
    */
   private double getRowEnd(int rowIndex) {
      return rowMaxY[rowIndex];
   }

   /**
    * Returns the child's row index constraint if set.
    * @param child the child parentNode of a defpane
    * @return the row index for the child or null if no row index was set
    */
   private static Integer getRowIndex(Node child) {
      return (Integer) getConstraint(child, ROW_INDEX_CONSTRAINT);
   }

   /**
    * Returns the child's row-span constraint if set.
    * @param child the child parentNode of a defpane
    * @return the row span for the child or null if no row span was set
    */
   private static Integer getRowSpan(Node child) {
      return (Integer) getConstraint(child, ROW_SPAN_CONSTRAINT);
   }

   /**
    * Method description
    *
    *
    * @param rowIndex
    *
    * @return
    */
   private double getRowStart(int rowIndex) {
      if (rowIndex == 0) {
         return 0;
      }

      return rowMaxY[rowIndex - 1];
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private double getVgap() {
      return 0;
   }

   /**
    * Method description
    *
    *
    * @param defPart
    *
    * @return
    *
    * @throws ContradictionException
    * @throws IOException
    */
   private boolean isOriginNodeOrDestinationNode(DefinitionPart defPart)
           throws ContradictionException, IOException {
      if (defPart == null) {
         return false;
      }

      switch (defPart.getPartType(definitionTree.getViewCoordinate())) {
      case AND :
      case OR :
      case CONCEPT_REFERENCE_DEFINED :
      case CONCEPT_REFERENCE_PRIMITIVE :
      case UNIVERSAL_RESTRICTION :
      case EXISTENTIAL_RESTRICTION :
      case ROLE_GROUP :
      case SUFFICIENT_SET :
      case NECESSARY_SET :
      case FEATURE_FLOAT :
      case FEATURE_INT :
      case FEATURE_LONG :
      case FIELD_SUBSTITUTION :
         return true;

      default :
         return false;
      }
   }

   /**
    * Sets the column index for the child when contained by a defpane
    * so that it will be positioned starting in that column of the defpane.
    * If a defpane child has no column index set, it will be positioned in
    * the first column.
    * Setting the value to null will remove the constraint.
    * @param child the child parentNode of a defpane
    * @param value the column index of the child
    */
   private static void setColumnIndex(Node child, Integer value) {
      if ((value != null) && (value < 0)) {
         throw new IllegalArgumentException("columnIndex must be greater or equal to 0, but was " + value);
      }

      setConstraint(child, COLUMN_INDEX_CONSTRAINT, value);
   }

   /**
    * Sets the column span for the child when contained by a defpane
    * so that it will span that number of columns horizontally.   This may be
    * set to REMAINING, which will cause the span to extend across all the remaining
    * columns.
    * <p>
    * If a defpane child has no column span set, it will default to spanning one column.
    * Setting the value to null will remove the constraint.
    * @param child the child parentNode of a defpane
    * @param value the column span of the child
    */
   private static void setColumnSpan(Node child, Integer value) {
      if ((value != null) && (value < 1)) {
         throw new IllegalArgumentException("columnSpan must be greater or equal to 1, but was " + value);
      }

      setConstraint(child, COLUMN_SPAN_CONSTRAINT, value);
   }

   /**
    * Method description
    *
    *
    *
    * @param node
    * @param key
    * @param value
    */
   private static void setConstraint(Node node, Object key, Object value) {
      if (value == null) {
         node.getProperties().remove(key);
      } else {
         node.getProperties().put(key, value);
      }

      if (node.getParent() != null) {
         node.getParent().requestLayout();
      }
   }

   /**
    * Sets the column,row indeces for the child when contained in a defpane.
    * @param child the child parentNode of a defpane
    * @param columnIndex the column index position for the child
    * @param rowIndex the row index position for the child
    */
   private static void setConstraints(Node child, int columnIndex, int rowIndex) {
      setRowIndex(child, rowIndex);
      setColumnIndex(child, columnIndex);
   }

   /**
    * Sets the grid position, spans, and alignment for the child when contained in a defpane.
    * @param child the child parentNode of a defpane
    * @param columnIndex the column index position for the child
    * @param rowIndex the row index position for the child
    * @param columnspan the number of columns the child should span
    * @param rowspan the number of rows the child should span
    * @param halignment the horizontal alignment of the child
    * @param valignment the vertical alignment of the child
    */
   private static void setConstraints(Node child, int columnIndex, int rowIndex, int columnspan, int rowspan,
                                      HPos halignment, VPos valignment) {
      setRowIndex(child, rowIndex);
      setColumnIndex(child, columnIndex);
      setRowSpan(child, rowspan);
      setColumnSpan(child, columnspan);
      setHalignment(child, halignment);
      setValignment(child, valignment);
   }

   /**
    * Method description
    *
    *
    * @param definitionTree
    *
    * @throws ContradictionException
    * @throws IOException
    */
   public void setDefinitionTree(DefinitionTree definitionTree, EditCoordinate editCoordinate) throws ContradictionException, IOException {
      this.editCoordinate = editCoordinate;
      this.definitionTree = definitionTree;
      this.getChildren().clear();
      this.edges.getChildren().clear();
      this.getChildren().add(this.edges);

      for (DefinitionPart part : definitionTree.getParts()) {
         if (part.getColumnIndex() > 1) {
            Node               node;
            DefinitionPartType partType = part.getPartType(definitionTree.getViewCoordinate());

            switch (partType) {
            case CONCEPT_REFERENCE_DEFINED :
               node = setupNode(part, definitionTree, partType);

               break;

            case CONCEPT_REFERENCE_PRIMITIVE :
               node = setupNode(part, definitionTree, partType);

               break;

            case AND :
               node = setupNode(part, definitionTree, partType);

               break;

            case DEFINITION_ROOT :
               node = setupNode(part, definitionTree, partType);

               break;

            case DISJOINT_WITH :
               node = setupNode(part, definitionTree, partType);

               break;

            case EDGE_FALSE :
               node = setupNode(part, definitionTree, partType);

               break;

            case EDGE_TRUE :
               node = setupNode(part, definitionTree, partType);

               break;

            case EXISTENTIAL_RESTRICTION :
               node = setupNodeTextFromNid2(
                  part, definitionTree, partType,
                  (Node) FXMLLoader.load(
                     getClass().getResource(
                        "/org/ihtsdo/ttk/pl/fx/concept/details/fxml/ExistentialRestriction.fxml")));

               break;

            case FEATURE_FLOAT :
               node = setupNode(part, definitionTree, partType);

               break;

            case FEATURE_INT :
               node = setupNode(part, definitionTree, partType);

               break;

            case FEATURE_LONG :
               node = setupNode(part, definitionTree, partType);

               break;

            case FIELD_SUBSTITUTION :
               node = setupNode(part, definitionTree, partType);

               break;

            case NECESSARY_SET :
               node = FXMLLoader.load(
                  getClass().getResource("/org/ihtsdo/ttk/pl/fx/concept/details/fxml/NecessarySet.fxml"));

               break;

            case OR :
               node = setupNode(part, definitionTree, partType);

               break;

            case SUFFICIENT_SET :
               node = FXMLLoader.load(
                  getClass().getResource("/org/ihtsdo/ttk/pl/fx/concept/details/fxml/SufficientSet.fxml"));

               break;

            case TEMPLATE_MERGE :
               node = setupNode(part, definitionTree, partType);

               break;

            case UNIVERSAL_RESTRICTION :
               node = setupNodeTextFromNid2(
                  part, definitionTree, partType,
                  (Node) FXMLLoader.load(
                     getClass().getResource(
                        "/org/ihtsdo/ttk/pl/fx/concept/details/fxml/ExistentialRestriction.fxml")));

               break;

            case ROLE_GROUP :
               node = FXMLLoader.load(
                  getClass().getResource("/org/ihtsdo/ttk/pl/fx/concept/details/fxml/RoleGroup.fxml"));

               break;

            default :
               node = new Label(part.getText(definitionTree.getViewCoordinate()));
            }

            // parentNode to tree part
            node.getProperties().put(PART_FOR_NODE, part);

            // tree part to parentNode
            partNodeMap.put(part, node);
            DefinitionPane.setConstraints(node, part.getColumnIndex(), part.getRowIndex(),
                                          partType.getColumnSpan(), 1, HPos.LEFT, VPos.CENTER);
            this.add(node, part.getColumnIndex(), part.getRowIndex());
         }
      }

      System.out.println("DefinitionPane->setDefinitionTree");
   }

   /**
    * Sets the horizontal alignment for the child when contained by a defpane.
    * If set, will override the defpane's default horizontal alignment.
    * Setting the value to null will remove the constraint.
    * @param child the child parentNode of a defpane
    * @param value the hozizontal alignment for the child
    */
   private static void setHalignment(Node child, HPos value) {
      setConstraint(child, HALIGNMENT_CONSTRAINT, value);
   }

   /**
    * Sets the margin for the child when contained by a defpane.
    * If set, the defpane will lay it out with the margin space around it.
    * Setting the value to null will remove the constraint.
    * @param child the child parentNode of a defpane
    * @param value the margin of space around the child
    */
   private static void setMargin(Node child, Insets value) {
      setConstraint(child, MARGIN_CONSTRAINT, value);
   }

   /**
    * Sets the row index for the child when contained by a defpane
    * so that it will be positioned starting in that row of the defpane.
    * If a defpane child has no row index set, it will be positioned in the
    * first row.
    * Setting the value to null will remove the constraint.
    * @param child the child parentNode of a defpane
    * @param value the row index of the child
    */
   private static void setRowIndex(Node child, Integer value) {
      if ((value != null) && (value < 0)) {
         throw new IllegalArgumentException("rowIndex must be greater or equal to 0, but was " + value);
      }

      setConstraint(child, ROW_INDEX_CONSTRAINT, value);
   }

   /**
    * Sets the row span for the child when contained by a defpane
    * so that it will span that number of rows vertically.  This may be
    * set to REMAINING, which will cause the span to extend across all the remaining
    * rows.
    * <p>
    * If a defpane child has no row span set, it will default to spanning one row.
    * Setting the value to null will remove the constraint.
    * @param child the child parentNode of a defpane
    * @param value the row span of the child
    */
   private static void setRowSpan(Node child, Integer value) {
      if ((value != null) && (value < 1)) {
         throw new IllegalArgumentException("rowSpan must be greater or equal to 1, but was " + value);
      }

      setConstraint(child, ROW_SPAN_CONSTRAINT, value);
   }

   /**
    * Sets the vertical alignment for the child when contained by a defpane.
    * If set, will override the defpane's default vertical alignment.
    * Setting the value to null will remove the constraint.
    * @param child the child parentNode of a defpane
    * @param value the vertical alignment for the child
    */
   public static void setValignment(Node child, VPos value) {
      setConstraint(child, VALIGNMENT_CONSTRAINT, value);
   }

   /**
    * Method description
    *
    *
    * @param partLabel
    * @param partType
    */
   private void setWidthAndStyleClass(Label partLabel, DefinitionPartType partType) {
      partLabel.setMaxWidth(NODE_WIDTH);
      partLabel.getStyleClass().add(partType.getCssStyleClass());
      setMargin(partLabel, standardInset);
   }
}
