/**
 * Copyright (c) 2011-2012, Lukas Eder, lukas.eder@gmail.com
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
 * . Neither the name "jOOX" nor the names of its contributors may be
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

import static java.util.Arrays.asList;
import static org.joox.JOOX.$;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFunctionResolver;

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
     * A flag indicating whether xalan extensions have been loaded
     */
    private static volatile boolean      xalanExtensionLoaded = false;

    /**
     * The xalan extensions {@link NamespaceContext} if available
     */
    private static NamespaceContext      xalanNamespaceContext;

    /**
     * The xalan extensions {@link XPathFunctionResolver} if available
     */
    private static XPathFunctionResolver xalanFunctionResolver;

    /**
     * A pattern for the dd.mm.yyyy format
     */
    private static final Pattern         PATTERN_DD_MM_YYYY   = Pattern.compile(
        "^(\\d{2})[-\\./](\\d{2})[-\\./](\\d{4})(?:\\s(\\d{2})(?:[-\\./:](\\d{2})(?:[-\\./:](\\d{2})(?:\\.(\\d+))?)?)?)?$");

    /**
     * A pattern for various yyyy-mm-dd formats
     */
    private static final Pattern         PATTERN_YYYY_MM_DD   = Pattern.compile(
        "^(\\d{4})(?:[-\\./](\\d{2})(?:[-\\./](\\d{2})(?:(?:[\\sT]|'T')(\\d{2})(?:[-\\./:](\\d{2})(?:[-\\./:](\\d{2})(?:\\.(\\d+))?)?)?)?)?)?$");

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
        if (text != null && text.contains("<")) {
            DocumentBuilder builder = JOOX.builder();

            try {

                // There is a processing instruction. We can safely assume
                // valid XML and parse it as such
                if (text.startsWith("<?xml")) {
                    Document parsed = builder.parse(new InputSource(new StringReader(text)));
                    DocumentFragment fragment = parsed.createDocumentFragment();
                    fragment.appendChild(parsed.getDocumentElement());

                    return (DocumentFragment) doc.importNode(fragment, true);
                }

                // Any XML document fragment. To be on the safe side, fragments
                // are wrapped in a dummy root node
                else {
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
     * Transform an {@link Match}[] into an {@link Element}[], removing duplicates.
     */
    static final Element[] elements(Match... content) {
        Set<Element> result = new LinkedHashSet<Element>();

        for (Match x : content) {
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
        final int length = list.getLength();

        for (int i = 0; i < length; i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return an XPath expression describing an element
     */
    static final String xpath(Element element) {
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

    /**
     * Return an path expression describing an element
     */
    static final String path(Element element) {
        StringBuilder sb = new StringBuilder();

        Node iterator = element;
        while (iterator.getNodeType() == Node.ELEMENT_NODE) {
            sb.insert(0, ((Element) iterator).getTagName());
            sb.insert(0, "/");

            iterator = iterator.getParentNode();
        }

        return sb.toString();
    }

    /**
     * Find the index among siblings of the same tag name
     */
    private static final int siblingIndex(Element element) {

        // The document element has index 0
        if (element.getParentNode() == element.getOwnerDocument()) {
            return 0;
        }

        // All other elements are compared with siblings with the same name
        else {
            return $(element).parent().children(element.getTagName()).get().indexOf(element);
        }
    }

    /**
     * Create a context object
     */
    static final Context context(Element match, int matchIndex, int matchSize) {
        return new DefaultContext(match, matchIndex, matchSize);
    }

    /**
     * Create a context object
     */
    static final Context context(Element match, int matchIndex, int matchSize, Element element, int elementIndex, int elementSize) {
        return new DefaultContext(match, matchIndex, matchSize, element, elementIndex, elementSize);
    }

    /**
     * Return <code>string</code> or <code>""</code> if <code>string</code> is
     * <code>null</code>
     */
    static final String nonNull(String string) {
        return string == null ? "" : string;
    }

    /**
     * Split a string into values
     */
    static final List<String> split(String value) {
        List<String> result = new ArrayList<String>();

        SplitState state = SplitState.NEW;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            stateSwitch:
            switch (state) {

                // Seeking the first character of a new word
                case NEW:
                case NEW_WITH_AT_LEAST_ONE_WORD: {
                    newSwitch:
                    switch (c) {

                        // Empty word
                        case ',':
                        case ';': {
                            state = SplitState.NEW_WITH_AT_LEAST_ONE_WORD;
                            result.add("");
                            break newSwitch;
                        }

                        // Ignorable whitespace
                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            break newSwitch;
                        }

                        // Start of a delimited word
                        case '"': {
                            state = SplitState.DELIMITED;
                            break newSwitch;
                        }

                        // Start of a non-delimited word
                        default: {
                            state = SplitState.NON_DELIMITED;
                            sb.append(c);
                            break newSwitch;
                        }
                    }

                    break stateSwitch;
                }

                // Within a delimited word
                case DELIMITED: {
                    delimitedSwitch:
                    switch (c) {

                        // Potential ending delimiter
                        case '"': {

                            // Escaped delimiter, consume subsequent quote char
                            if (i + 1 < value.length() && value.charAt(i + 1) == '"') {
                                sb.append(c);
                                i++;
                            }

                            // Delimiter not followed by whitespace or word stop
                            else if (i + 1 < value.length() && !asList(',', ';', ' ', '\t', '\n', '\r').contains(value.charAt(i + 1))) {
                                sb.append(c);
                            }

                            // Consume word stop following delimiter
                            else if (i + 1 < value.length() && asList(',', ';').contains(value.charAt(i + 1))) {
                                result.add(sb.toString());
                                sb = new StringBuilder();
                                state = SplitState.NEW_WITH_AT_LEAST_ONE_WORD;
                                i++;
                            }

                            // Ending delimiter. Either it's the last character
                            // or it is followed by whitespace
                            else {
                                result.add(sb.toString());
                                sb = new StringBuilder();
                                state = SplitState.NEW;
                            }

                            break delimitedSwitch;
                        }

                        // Any word content
                        default: {
                            sb.append(c);
                            break delimitedSwitch;
                        }
                    }

                    break stateSwitch;
                }

                case NON_DELIMITED: {
                    nonDelimitedSwitch:
                    switch (c) {

                        // Hard word stop
                        case ',':
                        case ';': {
                            result.add(sb.toString());
                            sb = new StringBuilder();
                            state = SplitState.NEW_WITH_AT_LEAST_ONE_WORD;
                            break nonDelimitedSwitch;
                        }

                        // Soft word stop
                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            result.add(sb.toString());
                            sb = new StringBuilder();
                            state = SplitState.NEW;
                            break nonDelimitedSwitch;
                        }

                        // Any word content
                        default: {
                            sb.append(c);
                            break nonDelimitedSwitch;
                        }
                    }

                    break stateSwitch;
                }
            }
        }

        // Cleaning up the last word
        switch (state) {

            // We were beginning a new word, so ignore sb content
            case NEW:
                break;

            // The content of sb is relevant, so add it
            case NEW_WITH_AT_LEAST_ONE_WORD:
            case DELIMITED:
            case NON_DELIMITED:
                result.add(sb.toString());
                break;
        }

        return result;
    }

    /**
     * The states in the state machine for splitting strings into lists
     */
    static enum SplitState {

        /**
         * This is the initial state before a new word
         */
        NEW,

        /**
         * Like {@link #NEW}, but there will be at least one word. This is
         * useful for trailing empty strings when content ends with
         * <code>','</code> or <code>';'</code>
         */
        NEW_WITH_AT_LEAST_ONE_WORD,

        /**
         * The state within a word delimited by <code>'"'</code>
         */
        DELIMITED,

        /**
         * The state not within a word delimited by <code>'"'</code>
         */
        NON_DELIMITED,
    }

    /**
     * Make a given {@link XPath} object "xalan-extension aware", if Xalan is on
     * the classpath.
     */
    static final void xalanExtensionAware(XPath xpath) {

        // Load xalan extensions thread-safely for all of jOOX
        if (!xalanExtensionLoaded) {
            synchronized (Util.class) {
                if (!xalanExtensionLoaded) {
                    xalanExtensionLoaded = true;

                    try {
                        xalanNamespaceContext = (NamespaceContext)
                            Class.forName("org.apache.xalan.extensions.ExtensionNamespaceContext").newInstance();

                        xalanFunctionResolver = (XPathFunctionResolver)
                            Class.forName("org.apache.xalan.extensions.XPathFunctionResolverImpl").newInstance();
                    }
                    catch (Exception ignore) {
                    }
                }
            }
        }

        if (xalanNamespaceContext != null && xalanFunctionResolver != null) {
            xpath.setNamespaceContext(xalanNamespaceContext);
            xpath.setXPathFunctionResolver(xalanFunctionResolver);
        }
    }

    /**
     * Parse any date format
     */
    static java.util.Date parseDate(String formatted) {
        if (formatted == null || formatted.trim().equals("")) {
            return null;
        }

        try {
            DatatypeFactory factory = DatatypeFactory.newInstance();
            XMLGregorianCalendar calendar = factory.newXMLGregorianCalendar(formatted);
            return calendar.toGregorianCalendar().getTime();
        }
        catch (Exception e) {
            Matcher matcher = PATTERN_DD_MM_YYYY.matcher(formatted);

            // Try matching dd.MM.yyyy date formats first
            if (matcher.find()) {
                String yyyy = matcher.group(3);
                String mm = matcher.group(2);
                String dd = matcher.group(1);
                String hh = defaultIfEmpty(matcher.group(4), "0");
                String min = defaultIfEmpty(matcher.group(5), "0");
                String ss = defaultIfEmpty(matcher.group(6), "0");
                String ms = defaultIfEmpty(matcher.group(7), "0");

                return getDate(Integer.parseInt(yyyy),
                               Integer.parseInt(mm),
                               Integer.parseInt(dd),
                               Integer.parseInt(hh),
                               Integer.parseInt(min),
                               Integer.parseInt(ss),
                               Integer.parseInt(ms));
            }

            // Then try matching yyyy-MM-dd date formats
            else {
                Matcher matcher2 = PATTERN_YYYY_MM_DD.matcher(formatted);

                if (matcher2.find()) {
                    String yyyy = matcher2.group(1);
                    String mm = defaultIfEmpty(matcher2.group(2), "1");
                    String dd = defaultIfEmpty(matcher2.group(3), "1");
                    String hh = defaultIfEmpty(matcher2.group(4), "0");
                    String min = defaultIfEmpty(matcher2.group(5), "0");
                    String ss = defaultIfEmpty(matcher2.group(6), "0");
                    String ms = defaultIfEmpty(matcher2.group(7), "0");

                    return getDate(Integer.parseInt(yyyy),
                                   Integer.parseInt(mm),
                                   Integer.parseInt(dd),
                                   Integer.parseInt(hh),
                                   Integer.parseInt(min),
                                   Integer.parseInt(ss),
                                   Integer.parseInt(ms));
                }

                // Finally, try matching plain timestamps
                else {
                    try {
                        return new Date(Long.parseLong(formatted));
                    } catch (NumberFormatException ignore) {
                        return null;
                    }
                }
            }
        }
    }

    private static Date getDate(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }

    static String defaultIfEmpty(String string, String defaultString) {
        if (string == null || string.equals("")) {
            return defaultString;
        }

        return string;
    }
}
