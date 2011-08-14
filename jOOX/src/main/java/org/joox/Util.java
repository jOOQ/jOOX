/**
 * Copyright (c) 2009-2011, Lukas Eder, lukas.eder@gmail.com
 * All rights reserved.
 *
 * This software is licensed to you under the Apache License, Version 2.0
 * (the "License"); You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * . Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * . Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * . Neither the name "jOOQ" nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.joox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * DOM utilities
 *
 * @author Lukas Eder
 */
class Util {

    /**
     * Create some content in the context of a given document
     *
     * @return <ul>
     *         <li>A {@link DocumentFragment} if <code>text</code> is
     *         well-formed.</li>
     *         <li><code>null</code>, if <code>text</code> is plain text or not
     *         well formed</li>
     *         </ul>
     */
    static final DocumentFragment createContent(Document doc, String text) {

        // Text might hold XML content
        if (text.contains("<")) {
            DocumentBuilder builder = JOOX.builder();

            try {
                String wrapped = "<dummy>" + text + "</dummy>";
                Document parsed = builder.parse(new InputSource(new StringReader(wrapped)));
                DocumentFragment fragment = parsed.createDocumentFragment();
                NodeList children = parsed.getDocumentElement().getChildNodes();

                // appendChild removes children also from NodeList!
                while (children.getLength() > 0) {
                    fragment.appendChild(children.item(0));
                }

                return (DocumentFragment) doc.importNode(fragment, true);
            }

            // This does not occur
            catch (IOException ignore) {}

            // The XML content is invalid
            catch (SAXException ignore) {}
        }

        // Plain text or invalid XML
        return null;
    }

    /**
     * Get an attribute value if it exists, or <code>null</code>
     */
    static final String attr(Element element, String name) {
        if (element.hasAttribute(name)) {
            return element.getAttribute(name);
        }

        return null;
    }

    /**
     * Make a list of elements available in a document.
     * <ul>
     * <li>Any element that is already in the document will be detached from its
     * parent</li>
     * <li>Any element that is not already in the document will be deep-imported
     * </li>
     * </ul>
     *
     * @param document The document to import elements into
     * @param elements The elemenst that are made available to a document.
     * @return Elements that are all in the supplied document, but detached.
     */
    static final List<Element> importOrDetach(Document document, Element... elements) {
        List<Element> detached = new ArrayList<Element>();

        for (Element e : elements) {
            if (document != e.getOwnerDocument()) {
                detached.add((Element) document.importNode(e, true));
            }
            else {
                detached.add((Element) e.getParentNode().removeChild(e));
            }
        }
        return detached;
    }

    /**
     * Transform an {@link X}[] into an {@link Element}[], removing duplicates.
     */
    static final Element[] elements(X... content) {
        Set<Element> result = new LinkedHashSet<Element>();

        for (X x : content) {
            result.addAll(x.get());
        }

        return result.toArray(new Element[result.size()]);
    }

    /**
     * Transform an {@link Element} into a <code>String</code>.
     */
    static final String toString(Element element) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            Source source = new DOMSource(element);
            Result target = new StreamResult(out);
            transformer.transform(source, target);
            return out.toString();
        }
        catch (Exception e) {
            return "[ ERROR IN toString() : " + e.getMessage() + " ]";
        }
    }

    /**
     * Check whether there are any element nodes in a {@link NodeList}
     */
    static final boolean hasElementNodes(NodeList list) {
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return an XPath expression describing an element
     */
    static String xpath(Element element) {
        StringBuilder sb = new StringBuilder();

        Node iterator = element;
        while (iterator.getNodeType() == Node.ELEMENT_NODE) {
            sb.insert(0, "]");
            sb.insert(0, siblingIndex((Element) iterator) + 1);
            sb.insert(0, "[");
            sb.insert(0, ((Element) iterator).getTagName());
            sb.insert(0, "/");

            iterator = iterator.getParentNode();
        }

        return sb.toString();
    }

    private static int siblingIndex(Element element) {
        return JOOX.list(element.getParentNode().getChildNodes()).indexOf(element);
    }
}
