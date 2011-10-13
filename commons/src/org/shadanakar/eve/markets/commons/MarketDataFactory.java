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
package org.shadanakar.eve.markets.commons;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.math.BigDecimal;

public final class MarketDataFactory {
    private MarketDataFactory() {} // not constructable...

    public static MarketData createFromFile(String folder, String fileName) throws IOException {
        // Lets parse it and get infomration about item and date
        String itemName;
        String exportDate;
        {
            int firstDash = fileName.indexOf("-");
            int lastDash = fileName.lastIndexOf("-");
            if (firstDash == -1 || lastDash == -1) {
                throw new RuntimeException("Invalid file name format");
            }
            String suffix = fileName.substring(lastDash+1);
            itemName = fileName.substring(firstDash+1, lastDash);
            int dotIndex = suffix.lastIndexOf('.');
            if (dotIndex == -1) {
                throw new RuntimeException("Invalid file name format");
            }
            exportDate = suffix.substring(0, dotIndex);
            try {
                // checking format...
                new SimpleDateFormat("yyyy.MM.dd hhmmss").parse(exportDate);
            } catch (ParseException e) {
                throw new RuntimeException("Invalid file format", e);
            }
        }

        BufferedReader reader = new BufferedReader(new FileReader(folder + fileName));

        Map<String, Integer> keys = new HashMap<String, Integer>();
        List<List<String>> table = new LinkedList<List<String>>();

        try {
            // first line is list of keys
            String line = reader.readLine();

            if (null == line) {
                throw new IOException("Invalid file format.");
            }

            String[] keysArr = line.split("\\,");
            int rowCount = keysArr.length;

            for (int i = 0; i < keysArr.length; i++) {
                String key = keysArr[i];
                keys.put(key, i);
            }

            // now lets get over the values;
            while (null != (line = reader.readLine())) {
                // skip empty lines
                if(line.trim().isEmpty()) continue;

                String[] values = line.split("\\,");
                List<String> row = new ArrayList<String>(rowCount);
                for (String value : values) {
                    row.add(value.trim());
                }

                table.add(row);
            }
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                // and ignore...
            }
        }

        MarketData data = new MarketData(fileName, itemName, exportDate);

        int bidIndex = keys.get("bid");
        int priceIndex = keys.get("price");
        int volumeIndex = keys.get("volRemaining");
        int stationIdIndex = keys.get("stationID");

        for (List<String> row : table) {
            if(!Constants.JITA_4x4_STATION_ID.equals(row.get(stationIdIndex))) {
                // skipping non-JITA orders
                continue;
            }

            BigDecimal price = new BigDecimal(row.get(priceIndex)).setScale(2, BigDecimal.ROUND_HALF_UP);
            Long volume = new Double(row.get(volumeIndex)).longValue();
            OrderData.Type type = "True".equals(row.get(bidIndex)) ? OrderData.Type.buy : OrderData.Type.sell;

            // create order
            OrderData order = new OrderData(price, volume, type);
            if (order.isBuyOrder()) {
                data.addBuyOrder(order);
            } else {
                data.addSellOrder(order);
            }
        }

        data.sort();

        return data;
    }

    public static MarketData createFromXml(final String xml) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        //Using factory get an instance of document builder
        DocumentBuilder db = dbf.newDocumentBuilder();

        //parse using builder to get DOM representation of the XML file
        Document doc = db.parse(new InputSource(new StringReader(xml)));

        Element rootNode = doc.getDocumentElement();
        // validate node name
        if (!"market-snapshot".equals(rootNode.getNodeName())) {
            throw new RuntimeException("Invalid root node: " + rootNode.getNodeName());
        }

        String name = XmlUtils.getUniqueSubnode(rootNode, "item").getTextContent();
        String exportDate = XmlUtils.getUniqueSubnode(rootNode, "time").getTextContent(); 

        MarketData result = new MarketData(name, exportDate);

        {
            Element orders = XmlUtils.getUniqueSubnode(rootNode, "buy-orders");
            NodeList orderNodes = orders.getElementsByTagName("order");

            for (int i = 0; i<orderNodes.getLength(); ++i) {
                OrderData orderData = parseOrderXml((Element) orderNodes.item(i), OrderData.Type.buy);
                result.addBuyOrder(orderData);
            }

        }

        {
            Element orders = XmlUtils.getUniqueSubnode(rootNode, "sell-orders");
            NodeList orderNodes = orders.getElementsByTagName("order");

            for (int i = 0; i<orderNodes.getLength(); ++i) {
                OrderData orderData = parseOrderXml((Element) orderNodes.item(i), OrderData.Type.sell);
                result.addSellOrder(orderData);
            }
        }

        return result;
    }

    private static OrderData parseOrderXml(Element orderElement, OrderData.Type type) {
        String priceStr = XmlUtils.getUniqueSubnodeTest(orderElement, "price");
        BigDecimal price = new BigDecimal(priceStr);
        Long volume = new Long(XmlUtils.getUniqueSubnode(orderElement, "volume").getTextContent());
        return new OrderData(price, volume, type);
    }

    private static class Constants {
        public static final String JITA_4x4_STATION_ID = "60003760";
    }
}
