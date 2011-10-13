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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public final class XmlUtils {
    private XmlUtils() {}

    public static Element getUniqueSubnode(Element rootNode, String name) {
        Element result = null;
        NodeList childNodes = rootNode.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); ++i) {
            Node aNode = childNodes.item(i);
            if (aNode.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) aNode;
                if (name.equals(el.getNodeName())) {
                    if (null == result) {
                        result = el;
                    } else {
                        throw new RuntimeException("Non-unique node: " + name);
                    }
                }
            }
        }

        if (result == null) {
            throw new RuntimeException("Node not found: " + name);
        }

        return result;
    }

    public static String getUniqueSubnodeTest(Element rootElement, String name) {
        Element el = getUniqueSubnode(rootElement, name);
        return el.getTextContent();
    }
}
