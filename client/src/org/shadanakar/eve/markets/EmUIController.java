/**
 *  Copyright (C) 2011 by Dimitry Ivanov
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.shadanakar.eve.markets;

import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.io.*;

/**
 * This class controls everything that should be done in
 * the Swing Main Thread, in other word, all the UI stuff.
 */
public final class EmUIController {
    private EmMainWindow mainWindow;

    public EmUIController(EmMainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void fatalError(String message) {
        fatalError(message, null);
    }

    public void fatalError(final String message, final Exception cause) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Utils.showErrorDialog(mainWindow, cause, message);
                mainWindow.setVisible(false); // hopefully this will stop application
            }
        });
    }

    public void addDetectedFiles(final String[] fileNames) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainWindow.getLogArea().append("Detected " + fileNames.length + " files.\n");
            }
        });
    }


    public void setStateProcessing(final String fileName) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainWindow.getLogArea().append("Processing " + fileName + "... ");
            }
        });
    }

    public void setStateProcessed(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (message == null) {
                    mainWindow.getLogArea().append("done.\n");
                } else {
                    mainWindow.getLogArea().append(message +".\n");
                }
                JProgressBar progressBar = mainWindow.getProgressBar();
                progressBar.setValue(progressBar.getValue()+1);
            }
        });
    }

    public void setStateProcessingError(String fileName, String errorMessage, Exception cause) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainWindow.getLogArea().append("error.\n");
                JProgressBar progressBar = mainWindow.getProgressBar();
                progressBar.setValue(progressBar.getValue()+1);
            }
        });
    }

    public void startProcessing(final int filesCount) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JProgressBar progressBar = mainWindow.getProgressBar();
                mainWindow.getLabel().setText("Processing");
                progressBar.setIndeterminate(false);
                progressBar.setMaximum(filesCount);
                progressBar.setValue(0);
            }
        });

    }

    public void stopProcessing() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JProgressBar progressBar = mainWindow.getProgressBar();
                progressBar.setString("");
                progressBar.setIndeterminate(true);
            }
        });
    }

    public void startUploading(final int count) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JProgressBar progressBar = mainWindow.getProgressBar();
                mainWindow.getLabel().setText("Uploading");
                progressBar.setIndeterminate(false);
                progressBar.setMaximum(count*2);
                progressBar.setValue(0);
            }
        });
    }

    public void stopUploading() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JProgressBar progressBar = mainWindow.getProgressBar();
                mainWindow.getLabel().setText("Idle");
                progressBar.setIndeterminate(true);
            }
        });
    }

    public void setStateUploading(final String fileName) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainWindow.getLogArea().append("done.\nUploading " + fileName + "... ");
                JProgressBar progressBar = mainWindow.getProgressBar();
                progressBar.setValue(progressBar.getValue()+1);
            }
        });
    }

    public void setStateSkipped(final String fileName) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainWindow.getLogArea().append("skipped.\n");
                JProgressBar progressBar = mainWindow.getProgressBar();
                progressBar.setValue(progressBar.getValue()+2);
            }
        });
    }

    public void setStateUploaded(String fileName) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainWindow.getLogArea().append("sent.\n");
                JProgressBar progressBar = mainWindow.getProgressBar();
                progressBar.setValue(progressBar.getValue()+1);
            }
        });
    }

    public void setStateChecking(final String fileName) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainWindow.getLogArea().append("Checking " + fileName + "... ");
            }
        });
    }

    public void setStateCheckError(String fileName, String message, Exception cause) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainWindow.getLogArea().append("error.\n");
                JProgressBar progressBar = mainWindow.getProgressBar();
                progressBar.setValue(progressBar.getValue()+2);
            }
        });
    }

    public void setStateUploadError(String fileName, String message, Exception cause) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainWindow.getLogArea().append("error.\n");
                JProgressBar progressBar = mainWindow.getProgressBar();
                progressBar.setValue(progressBar.getValue()+1);
            }
        });
    }

    public void startWaiting() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JProgressBar progressBar = mainWindow.getProgressBar();
                mainWindow.getLabel().setText("Waiting");
                progressBar.setIndeterminate(true);
            }
        });
    }

    private static class Utils {
        public static void showErrorDialog(JFrame mainWindow, Exception cause, String message) {
            String stackTrace = "";
            if (cause != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    PrintStream printStream = new PrintStream(byteArrayOutputStream, false, "UTF-16");
                    cause.printStackTrace(printStream);
                    stackTrace = "\n" + byteArrayOutputStream.toString("UTF-16");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    // go witout an exception
                }
            }

            JOptionPane pane = new JOptionPane(message + stackTrace);
            JDialog errorDialog = pane.createDialog(mainWindow, "Fatal Error");
            errorDialog.setVisible(true);
        }

    }
}
