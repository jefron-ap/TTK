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

import javafx.animation.Transition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author kec
 */
public class TaskBarTransition extends Transition {
    double xGrow;
    double yGrow;
    Rectangle rect;
    double width;
    double height;
    double x;
    double y;
    double start = 0;

    public void grow() {
        start = 0;
    }
    public void shrink() {
        start = 1;
    }
    public TaskBarTransition(double xGrow, double yGrow, double x, double y, Rectangle rect) {
        this.xGrow = xGrow;
        this.yGrow = yGrow;
        this.rect = rect;
        this.width = rect.getWidth();
        this.height = rect.getHeight();
        this.x = x;
        this.y = y;
        
        setCycleDuration(Duration.seconds(0.5));
    }
    
    @Override
    protected void interpolate(double frac) {
        double factor = Math.abs(frac - start);
        double xGrownPart = xGrow * factor;
        double yGrownPart = yGrow * factor;
        rect.setWidth(width + xGrownPart);
        
        rect.setHeight(height + yGrownPart);
        rect.relocate(x - xGrownPart, y);
    }
    
}
