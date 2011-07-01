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
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.math.BigDecimal;

public class MarketData {
    private String fileName;
    private List<OrderData> buyOrders;
    private List<OrderData> sellOrders;
    private String itemName;
    private String snapshotTime;

    public MarketData(String fileName, String itemName, String exportDate) {
        this.fileName = fileName;
        this.itemName = itemName;
        this.snapshotTime = exportDate;
        buyOrders = new ArrayList<OrderData>();
        sellOrders = new ArrayList<OrderData>();
    }

    public String getFileName() {
        return fileName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getSnapshotTime() {
        return snapshotTime;
    }

    public String toXml() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<market-snapshot>");
        sb.append("<item>").append(itemName).append("</item>");
        sb.append("<time>").append(snapshotTime).append("</time>");
        sb.append("<buy-orders>");
        for (OrderData buyOrder : buyOrders) {
            sb
                    .append("<order>")
                    .append("<price>").append(buyOrder.getPrice()).append("</price>")
                    .append("<volume>").append(buyOrder.getVolume()).append("</volume>")
                    .append("</order>");
        }
        sb.append("</buy-orders>");
        sb.append("<sell-orders>");
        for (OrderData buyOrder : sellOrders) {
            sb
                    .append("<order>")
                    .append("<price>").append(buyOrder.getPrice()).append("</price>")
                    .append("<volume>").append(buyOrder.getVolume()).append("</volume>")
                    .append("</order>");
        }
        sb.append("</sell-orders>");
        sb.append("</market-snapshot>");
        return sb.toString();
    }

    private static class Constants {
        public static final String JITA_4x4_STATION_ID = "60003760";
    }

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

    private void sort() {
        Collections.sort(sellOrders, new OrderData.PriceComparatorAscending());
        Collections.sort(buyOrders, new OrderData.PriceComparatorDescending());
    }

    private void addSellOrder(OrderData order) {
        sellOrders.add(order);
    }

    private void addBuyOrder(OrderData order) {
        buyOrders.add(order);
    }
}
