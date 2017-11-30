/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joox;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Lukas Eder
 */
class Elements implements Iterable<Element> {

    private final NodeList elements;
    private final int      length;

    Elements(NodeList elements) {
        this.elements = elements;
        this.length = elements.getLength();
    }

    @Override
    public Iterator<Element> iterator() {
        return new ElementIterator();
    }

    private class ElementIterator implements Iterator<Element> {

        private int     i    = 0;
        private Element next = null;

        @Override
        public boolean hasNext() {
            return (next != null) || ((next = findNext()) != null);
        }

        @Override
        public Element next() {
            try {
                return findNext();
            }
            finally {
                next = null;
            }
        }

        /**
         * Find next element, skipping all non-element nodes
         */
        private Element findNext() {
            if (next == null) {
                while (i < length) {
                    Node node = elements.item(i++);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        next = (Element) node;
                        break;
                    }
                }
            }

            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
