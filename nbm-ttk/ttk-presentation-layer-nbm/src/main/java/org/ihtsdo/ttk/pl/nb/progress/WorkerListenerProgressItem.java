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
package org.ihtsdo.ttk.pl.nb.progress;

//~--- non-JDK imports --------------------------------------------------------
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import static javafx.concurrent.Worker.State.CANCELLED;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 *
 * @author kec
 */
public class WorkerListenerProgressItem implements ActionListener, UpdateProgressItemBI {

    /**
     * Determines when progress is updated.
     */
    protected static final Timer timer = new Timer(1000, null);

    static {
        timer.start();
    }
    /**
     * Used to display the progress.
     */
    ProgressHandle ph;
    /**
     * Current state of this progress item.
     */
    ProgressState progressState = ProgressState.UNKNOWN;
    /**
     * worker for which progress is being monitored
     */
    Worker<?> worker;

    /**
     * Constructs ...
     *
     *
     *
     * @param worker
     * @param cancelable
     */
    private WorkerListenerProgressItem(Worker<?> worker, boolean cancelable) {
        this.worker = worker;

        if (cancelable) {
            this.ph = ProgressHandleFactory.createHandle(worker.getTitle(), new CancelableWorker(worker));
        } else {
            this.ph = ProgressHandleFactory.createHandle(worker.getTitle());
        }

        ph.start();
        timer.addActionListener(this);
    }

    /**
     * Method description
     *
     *
     * @param worker
     * @param cancelable
     *
     * @return
     */
    public static WorkerListenerProgressItem create(Worker<?> worker, boolean cancelable) {
        return new WorkerListenerProgressItem(worker, cancelable);
    }

    /**
     * Method description
     *
     */
    public void finish() {
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ph.finish();
                timer.removeActionListener(WorkerListenerProgressItem.this);
            }
        });
        
    }

    /**
     * Method description
     *
     *
     * @param workunit
     */
    public void progress(int workunit) {
        ph.progress(workunit);
    }

    /**
     * Method description
     *
     *
     * @param message
     */
    public void progress(String message) {
        ph.progress(message);
    }

    /**
     * Method description
     *
     *
     * @param message
     * @param workunit
     */
    public void progress(String message, int workunit) {
        ph.progress(message, workunit);
    }

    /**
     * Method description
     *
     *
     * @param workunits
     */
    public void switchToDeterminate(int workunits) {
        ph.switchToDeterminate(workunits);
    }

    /**
     * Method description
     *
     *
     * @param workunits
     * @param estimate
     */
    public void switchToDeterminate(int workunits, long estimate) {
        ph.switchToDeterminate(workunits, estimate);
    }

    /**
     * Method description
     *
     */
    public void switchToIndeterminate() {
        ph.switchToIndeterminate();
    }

    /**
     * Method description
     *
     *
     * @param newDisplayName
     */
    public void setDisplayName(String newDisplayName) {
        ph.setDisplayName(newDisplayName);
    }

    /**
     * Representation of progress indicator states.
     *
     */
    public enum ProgressState {

        DETERMINATE, INDETERMINATE, UNKNOWN
    }

    /**
     * Method description
     *
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }

    /**
     * Since worker represents progress in double as a ratio (between 0 and 1), this method provides an
     * integer safe representation of progress with 4 significant digits.
     *
     * @return
     */
    protected int getProgressAsBasisPoints() {
        double basisPoints = worker.getProgress() * 10000;
        return (int) basisPoints;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    protected ProgressState getProgressState() {
        if (worker.getWorkDone() < 0.0) {
            return ProgressState.INDETERMINATE;
        }
        return ProgressState.DETERMINATE;
    }

    /**
     * Method description
     *
     */
    @Override
    public void update() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
//                System.out.println(worker.getTitle() + ": " + worker.getState() + " " + worker.getWorkDone() + 
//                        " " + worker.getProgress() + " " + worker.isRunning());
                switch (getProgressState()) {
                    case DETERMINATE:
                        if (progressState == ProgressState.DETERMINATE) {
                            progress(worker.getMessage(), getProgressAsBasisPoints());
                        } else {
                            progressState = ProgressState.DETERMINATE;
                            switchToDeterminate(10000);
                            progress(worker.getMessage(), getProgressAsBasisPoints());
                        }
                        break;
                    case INDETERMINATE:
                        progressState = ProgressState.INDETERMINATE;
                        switchToIndeterminate();
                        progress(worker.getMessage());
                        break;
                    default:
                        throw new IllegalStateException("State of: " + getProgressState() + " should never happen.");
                }
                switch (worker.getState()) {
                    case CANCELLED:
                    case FAILED:
                    case SUCCEEDED:
                        finish();
                }
            }
        });
    }
}
