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

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EmMainWindow extends JFrame {
    private JLabel label;
    private JProgressBar progressBar;
    private JTextArea logArea;

    public EmMainWindow() throws HeadlessException {
        super("eve-market sync");
    }

    public void main() {
//        Image icon = Toolkit.getDefaultToolkit().getImage("src/main.png");
//        setIconImage(icon);
        JPanel mainPane = new JPanel(new BorderLayout(0, 5));
        mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setContentPane(mainPane);

        setPreferredSize(new Dimension(700, 550));
        setMinimumSize(new Dimension(600, 440));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // center on the screen
        center();

//        GridBagConstraints c = new GridBagConstraints();
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.anchor = GridBagConstraints.PAGE_START;

        JPanel northPanel = new JPanel(new BorderLayout(5,0));

        label = new JLabel("Initializing...");
        northPanel.add(label, BorderLayout.WEST);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(Color.black);
        northPanel.add(progressBar, BorderLayout.CENTER);



        mainPane.add(northPanel, BorderLayout.NORTH);

//        c.fill = GridBagConstraints.BOTH;
//        c.anchor = GridBagConstraints.PAGE_END;
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logPane = new JScrollPane(logArea);
        logPanel.add(logPane);

        mainPane.add(logPanel, BorderLayout.CENTER);


//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.anchor = GridBagConstraints.LINE_START;
//        panel.add(new JLabel("Initializing"), c);

//        panel.add(progressBar, c);

        setVisible(true);

        // staring spearate thread - it does the work!
        final EmWorker worker = new EmWorker(new EmUIController(this));
        final Thread workerThread = new Thread(worker);
        workerThread.start();

        // add listener to stop the applicaion gracefully
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("got window closing event.");
                worker.stop();
                try {
                    workerThread.interrupt(); // in case it is on wait.
                    workerThread.join(2000); // I'll give you 2 seconds...
                } catch (InterruptedException e1) {
                    // ignore
                }
            }
        });
    }

    private void center() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // Determine the new location of the window
        int w = getSize().width;
        int h = getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;

        // Move the window
        setLocation(x, y);
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JTextArea getLogArea() {
        return logArea;
    }

    public JLabel getLabel() {
        return label;
    }
}
