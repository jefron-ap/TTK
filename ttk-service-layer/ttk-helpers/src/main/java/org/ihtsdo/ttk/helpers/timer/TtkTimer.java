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
package org.ihtsdo.ttk.helpers.timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author kec
 */
public class TtkTimer {
    private static final Timer timer = new Timer("TTK Timer", true);
    
    static {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                timer.purge();
            }
        }, 60000, 60000);
        
    }

    public static void schedule(TimerTask task, long delay) {
        timer.schedule(task, delay);
    }

    public static void schedule(TimerTask task, Date time) {
        timer.schedule(task, time);
    }

    public static void schedule(TimerTask task, long delay, long period) {
        timer.schedule(task, delay, period);
    }

    public static void schedule(TimerTask task, Date firstTime, long period) {
        timer.schedule(task, firstTime, period);
    }

    public static void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        timer.scheduleAtFixedRate(task, delay, period);
    }

    public static void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
        timer.scheduleAtFixedRate(task, firstTime, period);
    }

    public int purge() {
        return timer.purge();
    }
    
}
