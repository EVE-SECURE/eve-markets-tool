/**
 *  $Id$
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
import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                // and ignore
            }
        }

        EmMainWindow window = new EmMainWindow();

        window.main();
    }

    private void run() throws URISyntaxException, MalformedURLException {
        // lets open a window..
/*
        System.out.println("Scanning " + folder);

        File marketlogsFolder = new File(folder);

        if (!marketlogsFolder.isDirectory()) {
            throw new RuntimeException("This is not directory: " + folder);
        }

        final Set<String> sentFiles = new HashSet<String>();

        URL checkUrl = new URI(checkAddress).toURL();
        URL postUrl = new URI(postAddress).toURL();

        while(true) {
            List<MarketData> marketDataToSend = new LinkedList<MarketData>();

            String[] files = marketlogsFolder.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("The Forge-") && name.endsWith(".txt") && !sentFiles.contains(name);
                }
            });

            if (files.length > 0) {

                System.out.println("Found " + files.length + " mew files for The Forge.. processing.");
                for (String fileName : files) {
                    System.out.print("-- " + fileName + " ... ");
                    try {
                        MarketData data = MarketData.createFromFile(folder, fileName);
                        marketDataToSend.add(data);
                        System.out.println("done");
                    } catch (FileNotFoundException ex) {
                        System.out.println("error: File is not found.");
                    } catch (IOException ex) {
                        System.out.println("error: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }

                System.out.println("Sending files to the server.");

                try {
                    for (MarketData marketData : marketDataToSend) {
                        System.out.print("Uploading " + marketData.getItemName() + " for " + marketData.getSnapshotTime()+ " ... ");
                        boolean needsUpload = checkSnapshot(checkUrl, marketData.getItemName(), marketData.getSnapshotTime());

                        if (!needsUpload) {
                            System.out.println("had already been uploaded (skipping).");
                            sentFiles.add(marketData.getFileName());
                            continue;
                        }

                        postData(postUrl, marketData.toXml());

                        sentFiles.add(marketData.getFileName());
                        System.out.println("sent.");
                    }
                } catch (IOException e) {
                    System.out.println("error");
                    System.err.println("Error while communicating with server");
                    e.printStackTrace();
                }

                System.out.println("Waiting for data...");
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }
        */
    }
}
