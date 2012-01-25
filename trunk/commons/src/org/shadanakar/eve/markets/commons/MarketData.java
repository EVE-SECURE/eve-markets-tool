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
package org.shadanakar.eve.markets.commons;

import java.util.*;

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

    public MarketData(String itemName, String exportDate) {
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
        for (OrderData sellOrder : sellOrders) {
            sb
                    .append("<order>")
                    .append("<price>").append(sellOrder.getPrice()).append("</price>")
                    .append("<volume>").append(sellOrder.getVolume()).append("</volume>")
                    .append("</order>");
        }
        sb.append("</sell-orders>");
        sb.append("</market-snapshot>");
        return sb.toString();
    }

    void sort() {
        Collections.sort(sellOrders, new OrderData.PriceComparatorAscending());
        Collections.sort(buyOrders, new OrderData.PriceComparatorDescending());
    }

    void addSellOrder(OrderData order) {
        if (order.isBuyOrder()) {
            throw new IllegalStateException("expecting sell order but got buy order instead");
        }
        sellOrders.add(order);
    }

    void addBuyOrder(OrderData order) {
        if (!order.isBuyOrder()) {
            throw new IllegalStateException("expecting buy order but got sell order instead");
        }
        buyOrders.add(order);
    }

    public List<OrderData> getBuyOrders() {
        return Collections.unmodifiableList(buyOrders);
    }

    public List<OrderData> getSellOrders() {
        return Collections.unmodifiableList(sellOrders);
    }
}
