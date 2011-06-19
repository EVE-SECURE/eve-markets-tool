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

import java.util.Comparator;
import java.math.BigDecimal;

public class OrderData {
    public static class PriceComparatorAscending implements Comparator<OrderData> {
        public int compare(OrderData o1, OrderData o2) {
            return o1.price.compareTo(o2.price);
        }
    }

    public static class PriceComparatorDescending implements Comparator<OrderData> {
        public int compare(OrderData o1, OrderData o2) {
            return o2.price.compareTo(o1.price);
        }
    }

    public static enum Type {
        buy, sell
    }

    private BigDecimal price;
    private Long volume;
    private Type type;

    public OrderData(BigDecimal price, Long volume, Type type) {
        this.price = price;
        this.volume = volume;
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getVolume() {
        return volume;
    }

    public boolean isBuyOrder() {
        return Type.buy == type;
    }
}
