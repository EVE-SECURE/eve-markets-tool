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

import java.io.*;
import java.util.*;
import java.net.*;

public class Main {
    private String folder;
    private String checkAddress;
    private String postAddress;
    private String userKey;

    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    private void run() throws URISyntaxException, MalformedURLException {
        init();
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
    }

    private boolean checkSnapshot(URL checkUrl, String itemName, String snapshotTime) throws IOException {
        String data = "__KEY="+ URLEncoder.encode(userKey, "UTF-8");
        data += "&item=" + URLEncoder.encode(itemName, "UTF-8");
        data += "&time=" + URLEncoder.encode(snapshotTime, "UTF-8");
        HttpURLConnection connection = (HttpURLConnection) checkUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length) );
        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String res = reader.readLine();
        if (res == null) {
            throw new IOException("Unexpected end of file from " + checkAddress);
        }

        res = res.trim();
        if (!"imported".equals(res) && !"send".equals(res)) {
            String line;
            while(null != (line = reader.readLine())) {
                res += "\n" + line;
            }
            writer.close();
            reader.close();
            throw new RuntimeException("Unexpected reply from the server: " + res);
        }

        writer.close();
        reader.close();

        return !"imported".equals(res);
    }

    private void postData(URL postUrl, String xml) throws IOException {
        String data = "__KEY="+ URLEncoder.encode(userKey, "UTF-8");
        data += "&xml=" + URLEncoder.encode(xml, "UTF-8");
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length) );
        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String res = reader.readLine();
        if (res == null) {
            throw new IOException("Unexpected end of file from " + postAddress);
        } else {
            //System.out.println("-- Debug: " + res);
        }

        writer.close();
        reader.close();
    }

    private void init() {
        String homeDir = System.getenv("HOME");
        // Windows?
        if (null == homeDir) {
            String homeDrive = System.getenv("HOMEDRIVE");
            String homePath = System.getenv("HOMEPATH");
            if (homeDrive != null && homePath != null) {
                homeDir = homeDrive + homePath;
            }
        }

        if (homeDir == null) {
            throw new RuntimeException("Cannot identify the forlder where eve stores markets data import files.");
        }

        folder = homeDir + File.separator + "Documents" + File.separator +
                "EVE" + File.separator + "logs" + File.separator + "Marketlogs" + File.separator;

        ResourceBundle resourceBundle = ResourceBundle.getBundle("evemarkets");
        userKey = resourceBundle.getString("auth.key");
        checkAddress = resourceBundle.getString("check.url");
        postAddress = resourceBundle.getString("post.url");
    }
}
