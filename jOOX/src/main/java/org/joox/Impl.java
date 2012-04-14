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
 * . Neither the name "JOOX" nor the names of its contributors may be
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

import static org.joox.JOOX.all;
import static org.joox.JOOX.convert;
import static org.joox.JOOX.iterable;
import static org.joox.JOOX.list;
import static org.joox.JOOX.none;
import static org.joox.JOOX.selector;
import static org.joox.Util.context;
import static org.joox.Util.nonNull;
import static org.joox.selector.CSS2XPath.css2xpath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Lukas Eder
 */
class Impl implements Match {

    private final Document      document;
    private final List<Element> elements;
    private final Impl          previousMatch;

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    Impl(Document document) {
        this(document, null);
    }

    Impl(Document document, Impl previousMatch) {
        this.document = document;
        this.elements = new ArrayList<Element>();
        this.previousMatch = previousMatch;
    }

    final Impl addNodeLists(List<NodeList> lists) {
        for (NodeList list : lists) {
            addNodeList(list);
        }

        return this;
    }

    final Impl addNodeList(NodeList list) {
        final int length = list.getLength();

        for (int i = 0; i < length; i++) {
            elements.add((Element) list.item(i));
        }

        return this;
    }

    final Impl addUniqueElements(Element... e) {
        if (e.length == 1) {
            Element element = e[0];

            elements.remove(element);
            elements.add(element);
        }
        else if (e.length > 1) {
            Set<Element> set = new LinkedHashSet<Element>(Arrays.asList(e));

            this.elements.removeAll(set);
            this.elements.addAll(set);
        }

        return this;
    }

    final Impl addUniqueElements(List<Element> e) {
        final int size = e.size();

        if (size == 1) {
            Element element = e.get(0);

            elements.remove(element);
            elements.add(element);
        }
        else if (size > 1) {
            Set<Element> set = new LinkedHashSet<Element>(e);

            elements.removeAll(set);
            elements.addAll(set);
        }

        return this;
    }

    final Impl addElements(Element... e) {
        this.elements.addAll(Arrays.asList(e));
        return this;
    }

    final Impl addElements(Collection<Element> e) {
        this.elements.addAll(e);
        return this;
    }

    // -------------------------------------------------------------------------
    // Iterable API
    // -------------------------------------------------------------------------

    @Override
    public final Iterator<Element> iterator() {
        return elements.iterator();
    }

    // -------------------------------------------------------------------------
    // Match API
    // -------------------------------------------------------------------------

    @Override
    public final Document document() {
        return document;
    }

    @Override
    public final Element get(int index) {
        final int size = elements.size();

        if (index >= 0) {
            if (index < size) {
                return elements.get(index);
            }
            else {
                return null;
            }
        }
        else {
            final int calculated = size + index;

            if (calculated >= 0 && calculated < size) {
                return elements.get(calculated);
            }
            else {
                return null;
            }
        }
    }

    @Override
    public final List<Element> get(int... indexes) {
        List<Element> result = new ArrayList<Element>();

        for (int i : indexes) {
            result.add(get(i));
        }

        return result;
    }

    @Override
    public final List<Element> get() {
        return elements;
    }

    @Override
    public final int size() {
        return elements.size();
    }

    @Override
    public final boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public final boolean isNotEmpty() {
        return !isEmpty();
    }

    @Override
    public final Impl add(Element... e) {
        Impl x = copy();
        x.addUniqueElements(e);
        return x;
    }

    @Override
    public final Impl add(Match... e) {
        Impl x = copy();

        for (Match element : e) {
            x.addUniqueElements(element.get());
        }

        return x;
    }

    @Override
    public final Impl reverse() {
        List<Element> reversed = new ArrayList<Element>(elements);
        Collections.reverse(reversed);
        return new Impl(document).addElements(reversed);
    }

    @Override
    public final Impl andSelf() {
        if (previousMatch != null) {
            addUniqueElements(previousMatch.get());
        }

        return this;
    }

    @Override
    public final Impl child() {
        return child(0);
    }

    @Override
    public final Impl child(String selector) {
        return child(selector(selector));
    }

    @Override
    public final Impl child(Filter filter) {
        return children(filter).eq(0);
    }

    @Override
    public final Impl child(int index) {
        return children(JOOX.at(index));
    }

    @Override
    public final Impl children() {
        return children(all());
    }

    @Override
    public final Impl children(int... indexes) {
        return children(JOOX.at(indexes));
    }

    @Override
    public final Impl children(String selector) {
        return children(selector(selector));
    }

    @Override
    public final Impl children(Filter filter) {
        final int size = size();

        List<Element> result = new ArrayList<Element>();
        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            List<Element> list = list(match.getChildNodes());
            int elementSize = list.size();

            for (int elementIndex = 0; elementIndex < elementSize; elementIndex++) {
                Element e = list.get(elementIndex);

                if (filter.filter(context(match, matchIndex, size, e, elementIndex, elementSize))) {
                    result.add(e);
                }
            }
        }

        return new Impl(document, this).addUniqueElements(result);
    }

    @Override
    public final List<Match> each() {
        List<Match> result = new ArrayList<Match>();

        for (Element element : elements) {
            result.add(new Impl(document).addElements(element));
        }

        return result;
    }

    @Override
    public final Impl each(Each each) {
        final int size = size();

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            each.each(context(get(matchIndex), matchIndex, size));
        }

        return this;
    }

    @Override
    public final Impl filter(String selector) {
        return filter(selector(selector));
    }

    @Override
    public final Impl filter(Filter filter) {
        final int size = size();

        List<Element> result = new ArrayList<Element>();
        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);

            if (filter.filter(context(match, matchIndex, size))) {
                result.add(match);
            }
        }

        return new Impl(document).addElements(result);
    }

    @Override
    public final Impl eq(int... indexes) {
        Impl result = new Impl(document);

        for (Element e : get(indexes)) {
            if (e != null) {
                result.addElements(e);
            }
        }

        return result;
    }

    @Override
    public final Impl find() {
        return find(all());
    }

    @Override
    public final Impl find(String selector) {

        // Simple selectors are either valid XML element names, or *. They can
        // be evaluated using standard DOM API
        if (SIMPLE_SELECTOR.matcher(selector).matches()) {
            List<NodeList> result = new ArrayList<NodeList>();

            for (Element element : elements) {
                result.add(element.getElementsByTagName(selector));
            }

            return new Impl(document, this).addNodeLists(result);
        }

        // CSS selectors are transformed to XPath expressions
        else {
            return new Impl(document, this).addElements(xpath(css2xpath(selector, isRoot())).get());
        }
    }

    /**
     * Temporary utility method to indicate whether the root element is among
     * the matched elements
     */
    private boolean isRoot() {
        for (Element element : elements) {
            if (element.getParentNode().getNodeType() == Node.DOCUMENT_NODE) {
                return true;
            }
        }

        return false;
    }

    @Override
    public final Impl find(Filter filter) {
        List<Element> result = new ArrayList<Element>();

        final int size = size();
        final boolean fast = isFast(filter);

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);

            final NodeList nodes = match.getElementsByTagName("*");
            final int elementSize = fast ? -1 : nodes.getLength();

            inner: for (int elementIndex = 0;; elementIndex++) {
                Element e = (Element) nodes.item(elementIndex);

                if (e == null) {
                    break inner;
                }
                else if (filter.filter(context(match, matchIndex, size, e, elementIndex, elementSize))) {
                    result.add(e);
                }
            }
        }

        return new Impl(document, this).addUniqueElements(result);
    }

    @Override
    public final Impl xpath(String expression) {
        return xpath(expression, new Object[0]);
    }

    @Override
    public final Impl xpath(String expression, Object... variables) {
        List<Element> result = new ArrayList<Element>();

        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            // Add the xalan ExtensionNamespaceContext if Xalan is available
            Util.xalanExtensionAware(xpath);

            // Add a variable resolver if we have any variables
            if (variables != null && variables.length != 0) {
                xpath.setXPathVariableResolver(new VariableResolver(expression, variables));
            }

            XPathExpression exp = xpath.compile(expression);
            for (Element element : get()) {
                for (Element match : iterable((NodeList) exp.evaluate(element, XPathConstants.NODESET))) {
                    result.add(match);
                }
            }
        }
        catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }

        return new Impl(document).addUniqueElements(result);
    }

    @Override
    public final Impl first() {
        if (size() > 0) {
            return new Impl(document).addElements(get(0));
        }
        else {
            return new Impl(document);
        }
    }

    @Override
    public final Impl has(String selector) {
        return has(selector(selector));
    }

    @Override
    public final Impl has(Filter filter) {
        List<Element> result = new ArrayList<Element>();

        final int size = size();
        final boolean fast = isFast(filter);

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);

            final NodeList nodes = match.getElementsByTagName("*");
            final int elementSize = fast ? -1 : nodes.getLength();

            inner: for (int elementIndex = 0;; elementIndex++) {
                Element e = (Element) nodes.item(elementIndex);

                if (e == null) {
                    break inner;
                }
                else if (filter.filter(context(match, matchIndex, size, e, elementIndex, elementSize))) {
                    result.add(match);
                    break inner;
                }
            }
        }

        return new Impl(document).addElements(result);
    }

    @Override
    public final boolean is(String selector) {
        return is(selector(selector));
    }

    @Override
    public final boolean is(Filter filter) {
        final int size = size();

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);

            if (filter.filter(context(match, matchIndex, size))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public final Impl last() {
        final int size = size();

        if (size > 0) {
            return new Impl(document).addElements(get(size - 1));
        }
        else {
            return new Impl(document);
        }
    }

    @Override
    public final <E> List<E> map(Mapper<E> map) {
        final int size = size();
        final List<E> result = new ArrayList<E>();

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            result.add(map.map(context(get(matchIndex), matchIndex, size)));
        }

        return result;
    }

    @Override
    public final Impl next() {
        return next(all());
    }

    @Override
    public final Impl next(String selector) {
        return next(selector(selector));
    }

    @Override
    public final Impl next(Filter filter) {
        return next(false, none(), filter);
    }

    @Override
    public final Impl nextAll() {
        return nextAll(all());
    }

    @Override
    public final Impl nextAll(String selector) {
        return nextAll(selector(selector));
    }

    @Override
    public final Impl nextAll(Filter filter) {
        return next(true, none(), filter);
    }

    @Override
    public final Impl nextUntil(String until) {
        return nextUntil(selector(until));
    }

    @Override
    public final Impl nextUntil(Filter until) {
        return nextUntil(until, all());
    }

    @Override
    public final Impl nextUntil(String until, String selector) {
        return nextUntil(selector(until), selector(selector));
    }

    @Override
    public final Impl nextUntil(String until, Filter filter) {
        return nextUntil(selector(until), filter);
    }

    @Override
    public final Impl nextUntil(Filter until, String selector) {
        return nextUntil(until, selector(selector));
    }

    @Override
    public final Impl nextUntil(Filter until, Filter filter) {
        return next(true, until, filter);
    }

    private final Impl next(boolean all, Filter until, Filter filter) {
        final int size = size();

        List<Element> result = new ArrayList<Element>();
        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            Node node = match;

            for (int elementIndex = 1;;) {
                node = node.getNextSibling();

                if (node == null) {
                    break;
                }
                else if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    // TODO: [#34] Calculate elementSize()
                    if (until.filter(context(match, matchIndex, size, e, elementIndex, -1))) {
                        break;
                    }

                    // TODO: [#34] Calculate elementSize()
                    if (filter.filter(context(match, matchIndex, size, e, elementIndex++, -1))) {
                        result.add(e);
                    }

                    if (!all) {
                        break;
                    }
                }
            }
        }

        return new Impl(document, this).addUniqueElements(result);
    }

    @Override
    public final Impl not(String selector) {
        return not(selector(selector));
    }

    @Override
    public final Impl not(Filter filter) {
        return filter(JOOX.not(filter));
    }

    @Override
    public final Impl parent() {
        return parent(all());
    }

    @Override
    public final Impl parent(String selector) {
        return parent(selector(selector));
    }

    @Override
    public final Impl parent(Filter filter) {
        return parents(false, none(), filter);
    }

    @Override
    public final Impl parents() {
        return parents(all());
    }

    @Override
    public final Impl parents(String selector) {
        return parents(selector(selector));
    }

    @Override
    public final Impl parents(Filter filter) {
        return parents(true, none(), filter);
    }

    @Override
    public final Impl parentsUntil(String until) {
        return parentsUntil(selector(until), all());
    }

    @Override
    public final Impl parentsUntil(Filter until) {
        return parentsUntil(until, all());
    }

    @Override
    public final Impl parentsUntil(String until, String selector) {
        return parentsUntil(selector(until), selector(selector));
    }

    @Override
    public final Impl parentsUntil(String until, Filter filter) {
        return parentsUntil(selector(until), filter);
    }

    @Override
    public final Impl parentsUntil(Filter until, String selector) {
        return parentsUntil(until, selector(selector));
    }

    @Override
    public final Impl parentsUntil(Filter until, Filter filter) {
        return parents(true, until, filter);
    }

    private final Impl parents(boolean all, Filter until, Filter filter) {
        final int size = size();
        List<Element> result = new ArrayList<Element>();

        // Maybe reverse iteration and reverse result?
        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            Node node = match;

            for (int elementIndex = 1;;) {
                node = node.getParentNode();

                if (node == null) {
                    break;
                }
                else if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    // TODO: [#34] Calculate elementSize()
                    if (until.filter(context(match, matchIndex, size, e, elementIndex, -1))) {
                        break;
                    }

                    // TODO: [#34] Calculate elementSize()
                    if (filter.filter(context(match, matchIndex, size, e, elementIndex++, -1))) {
                        result.add(e);
                    }

                    if (!all) {
                        break;
                    }
                }
            }
        }

        return new Impl(document, this).addUniqueElements(result);
    }

    @Override
    public final Impl prev() {
        return prev(all());
    }

    @Override
    public final Impl prev(String selector) {
        return prev(selector(selector));
    }

    @Override
    public final Impl prev(Filter filter) {
        return prev(false, none(), filter);
    }

    @Override
    public final Impl prevAll() {
        return prevAll(all());
    }

    @Override
    public final Impl prevAll(String selector) {
        return prevAll(selector(selector));
    }

    @Override
    public final Impl prevAll(Filter filter) {
        return prev(true, none(), filter);
    }

    @Override
    public final Impl prevUntil(String until) {
        return prevUntil(selector(until));
    }

    @Override
    public final Impl prevUntil(Filter until) {
        return prevUntil(until, all());
    }

    @Override
    public final Impl prevUntil(String until, String selector) {
        return prevUntil(selector(until), selector(selector));
    }

    @Override
    public final Impl prevUntil(String until, Filter filter) {
        return prevUntil(selector(until), filter);
    }

    @Override
    public final Impl prevUntil(Filter until, String selector) {
        return prevUntil(until, selector(selector));
    }

    @Override
    public final Impl prevUntil(Filter until, Filter filter) {
        return prev(true, until, filter);
    }

    private final Impl prev(boolean all, Filter until, Filter filter) {
        final int size = size();

        List<Element> result = new ArrayList<Element>();
        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            Node node = match;

            for (int elementIndex = 1;;) {
                node = node.getPreviousSibling();

                if (node == null) {
                    break;
                }
                else if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    // TODO: [#34] Calculate elementSize()
                    if (until.filter(context(match, matchIndex, size, e, elementIndex, -1))) {
                        break;
                    }

                    // TODO: [#34] Calculate elementSize()
                    if (filter.filter(context(match, matchIndex, size, e, elementIndex++, -1))) {
                        result.add(e);
                    }

                    if (!all) {
                        break;
                    }
                }
            }
        }

        Collections.reverse(result);
        return new Impl(document, this).addUniqueElements(result);
    }

    @Override
    public final Impl siblings() {
        return siblings(all());
    }

    @Override
    public final Impl siblings(String selector) {
        return siblings(selector(selector));
    }

    @Override
    public final Impl siblings(Filter filter) {
        return prevAll(filter).add(nextAll(filter));
    }

    @Override
    public final Impl slice(int start) {
        return slice(start, Integer.MAX_VALUE);
    }

    @Override
    public final Impl slice(int start, int end) {
        final int size = size();

        if (start < 0) {
            start = size + start;
        }
        if (end < 0) {
            end = size + end;
        }

        start = Math.max(0, start);
        end = Math.min(size, end);

        if (start > end) {
            return new Impl(document);
        }
        if (start == 0 && end == size) {
            return this;
        }

        return new Impl(document).addElements(elements.subList(start, end));
    }

    @Override
    public final Impl matchText(String regex) {
        return matchText(regex, true);
    }

    @Override
    public final Impl matchText(String regex, boolean keepMatches) {
        if (keepMatches) {
            return filter(JOOX.matchText(regex));
        }
        else {
            return not(JOOX.matchText(regex));
        }
    }

    @Override
    public final Impl matchTag(String regex) {
        return matchTag(regex, true);
    }

    @Override
    public final Impl matchTag(String regex, boolean keepMatches) {
        if (keepMatches) {
            return filter(JOOX.matchTag(regex));
        }
        else {
            return not(JOOX.matchTag(regex));
        }
    }

    @Override
    public final Impl leaf() {
        return filter(JOOX.leaf());
    }

    @Override
    public final Impl after(String content) {
        return after(JOOX.content(content));
    }

    @Override
    public final Impl after(Content content) {
        final int size = size();

        List<Element> result = new ArrayList<Element>();
        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            result.add(match);
            Document doc = match.getOwnerDocument();

            String text = nonNull(content.content(context(match, matchIndex, size)));
            DocumentFragment imported = Util.createContent(doc, text);
            Node parent = match.getParentNode();
            Node next = match.getNextSibling();

            if (imported != null) {
                result.addAll(JOOX.list(imported.getChildNodes()));
                parent.insertBefore(imported, next);
            }
            else {
                parent.insertBefore(doc.createTextNode(text), next);
            }
        }

        elements.clear();
        elements.addAll(result);

        return this;
    }

    @Override
    public final Impl after(Match... content) {
        return after(Util.elements(content));
    }

    @Override
    public final Impl after(Element... content) {
        final int size = size();

        List<Element> result = new ArrayList<Element>();
        List<Element> detached = Util.importOrDetach(document, content);

        for (int i = 0; i < size; i++) {
            Element element = get(i);
            result.add(element);

            Node parent = element.getParentNode();
            Node next = element.getNextSibling();

            for (Element e : detached) {
                if (i == 0) {
                    result.add((Element) parent.insertBefore(e, next));
                }
                else {
                    result.add((Element) parent.insertBefore(e.cloneNode(true), next));
                }
            }
        }

        elements.clear();
        elements.addAll(result);

        return this;
    }

    @Override
    public final Impl before(String content) {
        return before(JOOX.content(content));
    }

    @Override
    public final Impl before(Content content) {
        final int size = size();

        List<Element> result = new ArrayList<Element>();
        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            Document doc = match.getOwnerDocument();

            String text = nonNull(content.content(context(match, matchIndex, size)));
            DocumentFragment imported = Util.createContent(doc, text);
            Node parent = match.getParentNode();

            if (imported != null) {
                result.addAll(JOOX.list(imported.getChildNodes()));
                parent.insertBefore(imported, match);
            }
            else {
                parent.insertBefore(doc.createTextNode(text), match);
            }

            result.add(match);
        }

        elements.clear();
        elements.addAll(result);

        return this;
    }

    @Override
    public final Impl before(Match... content) {
        return before(Util.elements(content));
    }

    @Override
    public final Impl before(Element... content) {
        final int size = size();

        List<Element> result = new ArrayList<Element>();
        List<Element> detached = Util.importOrDetach(document, content);

        for (int i = 0; i < size; i++) {
            Element element = get(i);
            Node parent = element.getParentNode();

            for (Element e : detached) {
                if (i == 0) {
                    result.add((Element) parent.insertBefore(e, element));
                }
                else {
                    result.add((Element) parent.insertBefore(e.cloneNode(true), element));
                }
            }

            result.add(element);
        }

        elements.clear();
        elements.addAll(result);

        return this;
    }

    @Override
    public final Impl append(String content) {
        return append(JOOX.content(content));
    }

    @Override
    public final Impl append(Content content) {
        final int size = size();

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            Document doc = match.getOwnerDocument();

            String text = nonNull(content.content(context(match, matchIndex, size)));
            DocumentFragment imported = Util.createContent(doc, text);

            if (imported != null) {
                match.appendChild(imported);
            }
            else {
                match.appendChild(doc.createTextNode(text));
            }
        }

        return this;
    }

    @Override
    public final Impl append(Match... content) {
        return append(Util.elements(content));
    }

    @Override
    public final Impl append(Element... content) {
        final int size = size();

        List<Element> detached = Util.importOrDetach(document, content);

        for (int i = 0; i < size; i++) {
            for (Element e : detached) {
                if (i == 0) {
                    get(i).appendChild(e);
                }
                else {
                    get(i).appendChild(e.cloneNode(true));
                }
            }
        }

        return this;
    }

    @Override
    public final Impl prepend(String content) {
        return prepend(JOOX.content(content));
    }

    @Override
    public final Impl prepend(Content content) {
        final int size = size();

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            Document doc = match.getOwnerDocument();

            String text = nonNull(content.content(context(match, matchIndex, size)));
            DocumentFragment imported = Util.createContent(doc, text);
            Node first = match.getFirstChild();

            if (imported != null) {
                match.insertBefore(imported, first);
            }
            else {
                match.insertBefore(doc.createTextNode(text), first);
            }
        }

        return this;
    }

    @Override
    public final Impl prepend(Match... content) {
        return prepend(Util.elements(content));
    }

    @Override
    public final Impl prepend(Element... content) {
        final int size = size();

        List<Element> detached = Util.importOrDetach(document, content);

        for (int i = 0; i < size; i++) {
            for (Element e : detached) {
                Element element = get(i);
                Node first = element.getFirstChild();

                if (i == 0) {
                    element.insertBefore(e, first);
                }
                else {
                    element.insertBefore(e.cloneNode(true), first);
                }
            }
        }

        return this;
    }

    @Override
    public final String attr(String name) {
        if (size() > 0) {
            return Util.attr(get(0), name);
        }

        return null;
    }

    @Override
    public final <T> T attr(String name, Class<T> type) {
        return convert(attr(name), type);
    }

    @Override
    public final List<String> attrs(String name) {
        List<String> result = new ArrayList<String>();

        for (Element element : elements) {
            result.add(Util.attr(element, name));
        }

        return result;
    }

    @Override
    public final <T> List<T> attrs(String name, Class<T> type) {
        return convert(attrs(name), type);
    }

    @Override
    public final Impl attr(String name, String value) {
        return attr(name, JOOX.content(value));
    }

    @Override
    public final Impl attr(String name, Content content) {
        final int size = size();

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            String value = content.content(context(match, matchIndex, size));

            if (value == null) {
                match.removeAttribute(name);
            }
            else {
                match.setAttribute(name, value);
            }
        }

        return this;
    }

    @Override
    public final Impl removeAttr(String name) {
        return attr(name, (String) null);
    }

    @Override
    public final String content() {
        return content(0);
    }

    @Override
    public final String content(int index) {
        return content(get(index));
    }

    @Override
    public final List<String> contents() {
        List<String> result = new ArrayList<String>();

        for (Element element : elements) {
            result.add(content(element));
        }

        return result;
    }

    @Override
    public final List<String> contents(int... indexes) {
        List<String> result = new ArrayList<String>();

        for (int index : indexes) {
            result.add(content(index));
        }

        return result;
    }

    private final String content(Element element) {
        if (element == null) {
            return "";
        }

        NodeList children = element.getChildNodes();

        // The element is empty
        if (children.getLength() == 0) {
            return "";
        }

        // The element contains only text
        else if (!Util.hasElementNodes(children)) {
            return element.getTextContent();
        }

        // The element contains content
        else {
            // TODO: Check this code's efficiency
            String name = element.getTagName();
            return Util.toString(element).replaceAll("^<" + name + "(?:[^>]*)>(.*)</" + name + ">$", "$1");
        }
    }

    @Override
    public final Impl content(String content) {
        return content(JOOX.content(content));
    }

    @Override
    public final Impl content(Object content) {
        return content(JOOX.content(content));
    }

    @Override
    public final Impl content(Content content) {
        final int size = size();

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            String text = content.content(context(match, matchIndex, size));

            DocumentFragment imported = Util.createContent(match.getOwnerDocument(), text);
            if (imported != null) {
                match.setTextContent("");
                match.appendChild(imported);
            }
            else {
                match.setTextContent(text);
            }
        }

        return this;
    }

    @Override
    public final String text() {
        return text(0);
    }

    @Override
    public final String text(int index) {
        Element element = get(index);

        if (element != null) {
            return element.getTextContent();
        }

        return null;
    }

    @Override
    public final <T> T text(Class<T> type) {
        return convert(text(), type);
    }

    @Override
    public final List<String> texts() {
        List<String> result = new ArrayList<String>();

        for (Element element : elements) {
            result.add(element.getTextContent());
        }

        return result;
    }

    @Override
    public final List<String> texts(int... indexes) {
        List<String> result = new ArrayList<String>();

        for (int index : indexes) {
            result.add(text(index));
        }

        return result;
    }

    @Override
    public final <T> List<T> texts(Class<T> type) {
        return convert(texts(), type);
    }

    @Override
    public final Impl text(String content) {
        return text(JOOX.content(content));
    }

    @Override
    public final Impl text(Content content) {
        final int size = size();

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            String text = content.content(context(match, matchIndex, size));
            match.setTextContent(text);
        }

        return this;
    }

    @Override
    public final Match empty() {
        for (Element element : elements) {
            empty(element);
        }

        return this;
    }

    @Override
    public final Impl remove() {
        return remove(all());
    }

    @Override
    public final Impl remove(String selector) {
        return remove(selector(selector));
    }

    @Override
    public final Impl remove(Filter filter) {
        final int size = size();

        List<Element> remove = new ArrayList<Element>();
        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);

            if (filter.filter(context(match, matchIndex, size))) {
                remove.add(match);
            }
        }

        for (Element element : remove) {
            remove(element);
        }

        return this;
    }

    private final void remove(Element element) {
        element.getParentNode().removeChild(element);
        elements.remove(element);
    }

    private final void empty(Element element) {
        Node child;

        while ((child = element.getFirstChild()) != null) {
            element.removeChild(child);
        }
    }

    @Override
    public final Impl wrap(String content) {
        return wrap(JOOX.content(content));
    }

    @Override
    public final Impl wrap(Content content) {
        final int size = size();

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            Node parent = match.getParentNode();
            Document doc = match.getOwnerDocument();

            String text = nonNull(content.content(context(match, matchIndex, size)));
            Element wrapper = doc.createElement(text);
            parent.replaceChild(wrapper, match);
            wrapper.appendChild(match);
        }

        return this;
    }

    @Override
    public final Impl unwrap() {
        final int size = size();

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            Node wrapper = match.getParentNode();
            Node parent = wrapper.getParentNode();

            // match or wrapper is the document element
            if (wrapper.getNodeType() == Node.DOCUMENT_NODE ||
                parent.getNodeType() == Node.DOCUMENT_NODE) {

                throw new RuntimeException("Cannot unwrap document element or direct children thereof");
            }

            parent.replaceChild(match, wrapper);
        }

        return this;
    }

    @Override
    public final Impl replaceWith(String content) {
        return replaceWith(JOOX.content(content));
    }

    @Override
    public final Impl replaceWith(Content content) {
        final int size = size();

        List<Element> result = new ArrayList<Element>();
        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);
            Document doc = match.getOwnerDocument();

            String text = nonNull(content.content(context(match, matchIndex, size)));
            DocumentFragment imported = Util.createContent(doc, text);
            Node parent = match.getParentNode();

            if (imported != null) {
                result.addAll(JOOX.list(imported.getChildNodes()));
                parent.replaceChild(imported, match);
            }
            else {
                parent.replaceChild(doc.createTextNode(text), match);
            }
        }

        elements.clear();
        elements.addAll(result);

        return this;
    }

    @Override
    public final Impl replaceWith(Match... content) {
        return replaceWith(Util.elements(content));
    }

    @Override
    public final Impl replaceWith(Element... content) {
        final int size = size();

        List<Element> result = new ArrayList<Element>();
        List<Element> detached = Util.importOrDetach(document, content);

        for (int i = 0; i < size; i++) {
            Element element = get(i);
            Node parent = element.getParentNode();

            for (Element e : detached) {
                Element replacement;

                if (i == 0) {
                    replacement = e;
                }
                else {
                    replacement = (Element) e.cloneNode(true);
                }

                parent.insertBefore(replacement, element);
                result.add(replacement);
            }

            parent.removeChild(element);
        }

        elements.clear();
        elements.addAll(result);

        return this;
    }

    @Override
    public final Match rename(String tag) {
        return rename(JOOX.content(tag));
    }

    @Override
    public final Match rename(Content tag) {
        final int size = size();

        List<Element> result = new ArrayList<Element>();

        for (int matchIndex = 0; matchIndex < size; matchIndex++) {
            Element match = get(matchIndex);

            String text = nonNull(tag.content(context(match, matchIndex, size)));
            result.add((Element) document.renameNode(match, "", text));
        }

        elements.clear();
        elements.addAll(result);

        return this;
    }

    // -------------------------------------------------------------------------
    // Utility API
    // -------------------------------------------------------------------------

    private final boolean isFast(Filter filter) {
        return filter instanceof FastFilter;
    }

    @Override
    public final Impl copy() {
        Impl copy = new Impl(document, previousMatch);
        copy.elements.addAll(elements);
        return copy;
    }

    @Override
    public final String xpath() {
        return xpath(0);
    }

    @Override
    public final String xpath(int index) {
        Element element = get(index);

        if (element != null) {
            return Util.xpath(element);
        }
        else {
            return null;
        }
    }

    @Override
    public final List<String> xpaths() {
        List<String> result = new ArrayList<String>();

        for (Element element : elements) {
            result.add(Util.xpath(element));
        }

        return result;
    }

    @Override
    public final List<String> xpaths(int... indexes) {
        List<String> result = new ArrayList<String>();

        for (int index : indexes) {
            result.add(xpath(index));
        }

        return result;
    }

    @Override
    public final String tag() {
        return tag(0);
    }

    @Override
    public final String tag(int index) {
        Element element = get(index);

        if (element != null) {
            return element.getTagName();
        }
        else {
            return null;
        }
    }

    @Override
    public final List<String> tags() {
        List<String> result = new ArrayList<String>();

        for (Element element : elements) {
            result.add(element.getTagName());
        }

        return result;
    }

    @Override
    public final List<String> tags(int... indexes) {
        List<String> result = new ArrayList<String>();

        for (int index : indexes) {
            result.add(tag(index));
        }

        return result;
    }

    @Override
    public final String id() {
        return id(0);
    }

    @Override
    public final String id(int index) {
        return eq(index).attr("id");
    }

    @Override
    public final <T> T id(Class<T> type) {
        return JOOX.convert(id(), type);
    }

    @Override
    public final List<String> ids() {
        return attrs("id");
    }

    @Override
    public final List<String> ids(int... indexes) {
        List<String> result = new ArrayList<String>();

        for (int index : indexes) {
            result.add(id(index));
        }

        return result;
    }

    @Override
    public final <T> List<T> ids(Class<T> type) {
        return JOOX.convert(ids(), type);
    }

    // ---------------------------------------------------------------------
    // Transformation
    // ---------------------------------------------------------------------

    @Override
    public final Match write(Writer writer) throws IOException {
        try {
            for (Element e : this) {
                writer.write(JOOX.$(e).toString());
            }
        }
        finally {
            writer.close();
        }

        return this;
    }

    @Override
    public final Match write(OutputStream stream) throws IOException {
        return write(new OutputStreamWriter(stream));
    }

    @Override
    public final Match write(File file) throws IOException {
        return write(new FileOutputStream(file));
    }

    @Override
    public final <T> List<T> unmarshal(Class<T> type) {
        List<T> result = new ArrayList<T>();

        for (Element element : elements) {
            result.add(JAXB.unmarshal(new DOMSource(element), type));
        }

        return result;
    }

    @Override
    public final <T> List<T> unmarshal(Class<T> type, int... indexes) {
        return eq(indexes).unmarshal(type);
    }

    @Override
    public final <T> T unmarshalOne(Class<T> type) {
        List<T> list = unmarshal(type);

        if (list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    @Override
    public final <T> T unmarshalOne(Class<T> type, int index) {
        return eq(index).unmarshalOne(type);
    }

    @Override
    public final Impl transform(Transformer transformer) {
        List<DOMResult> results = new ArrayList<DOMResult>();
        List<Element> newElements = new ArrayList<Element>();

        // Transform all matched elements
        try {
            for (Element element : get()) {
                DOMResult result = new DOMResult();
                transformer.transform(new DOMSource(element), result);
                results.add(result);
            }
        }
        catch (TransformerException e) {
            throw new RuntimeException(e);
        }

        // Replace all matched elements by their resulting transformations
        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            Element result = ((Document) results.get(i).getNode()).getDocumentElement();

            result = (Element) document().importNode(result, true);
            element.getParentNode().replaceChild(result, element);
            newElements.add(result);
        }

        return new Impl(document).addElements(newElements);
    }

    @Override
    public final Impl transform(Source transformer) {
        try {
            return transform(TransformerFactory.newInstance().newTransformer(transformer));
        }
        catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final Impl transform(InputStream transformer) {
        return transform(new StreamSource(transformer));
    }

    @Override
    public final Impl transform(Reader transformer) {
        return transform(new StreamSource(transformer));
    }

    @Override
    public final Impl transform(URL transformer) {
        try {
            return transform(transformer.openStream());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final Impl transform(File transformer) {
        return transform(new StreamSource(transformer));
    }

    @Override
    public final Impl transform(String transformer) {
        return transform(new StreamSource(new File(transformer)));
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public final String toString() {
        if (elements.size() == 0) {
            return "[]";
        }
        else if (elements.size() == 1) {
            return Util.toString(get(0));
        }
        else {
            StringBuilder sb = new StringBuilder();
            String separator = "";

            sb.append("[");

            for (Element element : elements) {
                sb.append(separator);
                sb.append(Util.toString(element));
                separator = ",\n";
            }

            sb.append("]");
            return sb.toString();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((document == null) ? 0 : document.hashCode());
        result = prime * result + ((elements == null) ? 0 : elements.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        // Compare types
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        // Compare documents
        Impl other = (Impl) obj;
        if (document == null) {
            if (other.document != null) {
                return false;
            }
        }
        else if (!document.equals(other.document)) {
            return false;
        }

        // Compare elements
        if (elements == null) {
            if (other.elements != null) {
                return false;
            }
        }
        else if (!elements.equals(other.elements)) {
            return false;
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // Utilities
    // -------------------------------------------------------------------------

    /**
     * A selector pattern that can be evaluated using standard DOM API
     */
    public static final Pattern SIMPLE_SELECTOR = Pattern.compile("\\*|[\\w\\-]+");

    /**
     * A simple variable resolver mapping variable names to their respective
     * index in an XPath expression.
     */
    private static class VariableResolver implements XPathVariableResolver {

        private final String expression;
        private final Object[] variables;

        VariableResolver(String expression, Object[] variables) {
            this.expression = expression;
            this.variables = variables;
        }

        @Override
        public Object resolveVariable(QName variable) {
            int index;

            try {
                index = Integer.parseInt(variable.getLocalPart()) - 1;
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Variable " + variable + " is not supported by jOOX. Only numerical variables can be used for " + expression);
            }

            if (index < variables.length) {
                return variables[index];
            }
            else {
                throw new IndexOutOfBoundsException("No variable defined for " + variable + " in " + expression);
            }
        }
    }
}
