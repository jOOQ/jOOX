/**
 * Copyright (c) 2011, Lukas Eder, lukas.eder@gmail.com
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
package org.joox.impl;

import static org.joox.impl.JOOX.iterable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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

import org.joox.Content;
import org.joox.Each;
import org.joox.Elements;
import org.joox.Filter;
import org.joox.Mapper;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Lukas Eder
 */
class ElementsImpl implements Elements {

    private final List<Element> elements;

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    ElementsImpl() {
        this.elements = new ArrayList<Element>();
    }

    ElementsImpl addNodeLists(List<NodeList> lists) {
        for (NodeList list : lists) {
            addNodeList(list);
        }

        return this;
    }

    ElementsImpl addNodeList(NodeList list) {
        for (int i = 0; i < list.getLength(); i++) {
            elements.add((Element) list.item(i));
        }

        return this;
    }

    ElementsImpl addUniqueElements(Element... e) {
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

    ElementsImpl addUniqueElements(List<Element> e) {
        int size = e.size();

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

    ElementsImpl addElements(Element... e) {
        this.elements.addAll(Arrays.asList(e));
        return this;
    }

    ElementsImpl addElements(List<Element> e) {
        this.elements.addAll(e);
        return this;
    }

    // -------------------------------------------------------------------------
    // Iterable API
    // -------------------------------------------------------------------------

    @Override
    public Iterator<Element> iterator() {
        return elements.iterator();
    }

    // -------------------------------------------------------------------------
    // X API
    // -------------------------------------------------------------------------

    @Override
    public Element get(int index) {
        try {
            return elements.get(index);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public List<Element> get() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public ElementsImpl add(Element... e) {
        ElementsImpl x = copy();
        x.addUniqueElements(e);
        return x;
    }

    @Override
    public ElementsImpl add(Elements... e) {
        ElementsImpl x = copy();

        for (Elements element : e) {
            x.addUniqueElements(element.get());
        }

        return x;
    }

    @Override
    public ElementsImpl children() {
        return children(JOOX.all());
    }

    @Override
    public ElementsImpl children(String selector) {
        return children(JOOX.selector(selector));
    }

    @Override
    public ElementsImpl children(Filter filter) {
        List<Element> result = new ArrayList<Element>();

        int index = 0;
        for (Element element : elements) {
            for (Element child : iterable(element.getChildNodes())) {
                if (filter.filter(index++, child)) {
                    result.add(child);
                }
            }
        }

        return new ElementsImpl().addUniqueElements(result);
    }

    @Override
    public ElementsImpl each(Each each) {
        for (int i = 0; i < size(); i++) {
            each.each(i, get(i));
        }

        return this;
    }

    @Override
    public ElementsImpl filter(String selector) {
        return filter(JOOX.selector(selector));
    }

    @Override
    public ElementsImpl filter(Filter filter) {
        List<Element> result = new ArrayList<Element>();

        for (int i = 0; i < size(); i++) {
            if (filter.filter(i, get(i))) {
                result.add(get(i));
            }
        }

        return new ElementsImpl().addElements(result);
    }

    @Override
    public ElementsImpl eq(int index) {
        Element element = get(index);

        if (element != null) {
            return new ElementsImpl().addElements(element);
        }
        else {
            return new ElementsImpl();
        }
    }

    @Override
    public ElementsImpl find() {
        return find(JOOX.all());
    }

    @Override
    public ElementsImpl find(String selector) {
        List<NodeList> result = new ArrayList<NodeList>();

        for (Element element : elements) {
            result.add(element.getElementsByTagName(selector));
        }

        return new ElementsImpl().addNodeLists(result);
    }

    @Override
    public ElementsImpl find(Filter filter) {
        List<Element> result = new ArrayList<Element>();

        int index = 0;
        for (int i = 0; i < size(); i++) {
            for (Element descendant : iterable(get(i).getElementsByTagName("*"))) {
                if (filter.filter(index++, descendant)) {
                    result.add(descendant);
                }
            }
        }

        return new ElementsImpl().addUniqueElements(result);
    }

    @Override
    public ElementsImpl first() {
        if (size() > 0) {
            return new ElementsImpl().addElements(get(0));
        }
        else {
            return new ElementsImpl();
        }
    }

    @Override
    public ElementsImpl has(String selector) {
        return has(JOOX.selector(selector));
    }

    @Override
    public ElementsImpl has(Filter filter) {
        List<Element> result = new ArrayList<Element>();

        for (int i = 0; i < size(); i++) {
            Element element = elements.get(i);

            inner: for (Element child : iterable(element.getElementsByTagName("*"))) {
                if (filter.filter(i, child)) {
                    result.add(element);
                    break inner;
                }
            }
        }

        return new ElementsImpl().addElements(result);
    }

    @Override
    public boolean is(String selector) {
        return is(JOOX.selector(selector));
    }

    @Override
    public boolean is(Filter filter) {
        for (int i = 0; i < size(); i++) {
            if (filter.filter(i, elements.get(i))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ElementsImpl last() {
        if (size() > 0) {
            return new ElementsImpl().addElements(get(size() - 1));
        }
        else {
            return new ElementsImpl();
        }
    }

    @Override
    public <E> List<E> map(Mapper<E> map) {
        final List<E> result = new ArrayList<E>();

        for (int i = 0; i < size(); i++) {
            result.add(map.map(i, elements.get(i)));
        }

        return result;
    }

    @Override
    public ElementsImpl next() {
        return next(JOOX.all());
    }

    @Override
    public ElementsImpl next(String selector) {
        return next(JOOX.selector(selector));
    }

    @Override
    public ElementsImpl next(Filter filter) {
        return next(false, JOOX.none(), filter);
    }

    @Override
    public ElementsImpl nextAll() {
        return nextAll(JOOX.all());
    }

    @Override
    public ElementsImpl nextAll(String selector) {
        return nextAll(JOOX.selector(selector));
    }

    @Override
    public ElementsImpl nextAll(Filter filter) {
        return next(true, JOOX.none(), filter);
    }

    @Override
    public ElementsImpl nextUntil(String until) {
        return nextUntil(JOOX.selector(until));
    }

    @Override
    public ElementsImpl nextUntil(Filter until) {
        return nextUntil(until, JOOX.all());
    }

    @Override
    public ElementsImpl nextUntil(String until, String selector) {
        return nextUntil(JOOX.selector(until), JOOX.selector(selector));
    }

    @Override
    public ElementsImpl nextUntil(String until, Filter filter) {
        return nextUntil(JOOX.selector(until), filter);
    }

    @Override
    public ElementsImpl nextUntil(Filter until, String selector) {
        return nextUntil(until, JOOX.selector(selector));
    }

    @Override
    public ElementsImpl nextUntil(Filter until, Filter filter) {
        return next(true, until, filter);
    }

    private ElementsImpl next(boolean all, Filter until, Filter filter) {
        List<Element> result = new ArrayList<Element>();

        for (Element element : elements) {
            Node node = element;

            for (int i = 0;;) {
                node = node.getNextSibling();

                if (node == null) {
                    break;
                }
                else if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element next = (Element) node;
                    if (until.filter(i, next)) {
                        break;
                    }

                    if (filter.filter(i++, next)) {
                        result.add(next);
                    }

                    if (!all) {
                        break;
                    }
                }
            }
        }

        return new ElementsImpl().addUniqueElements(result);
    }

    @Override
    public ElementsImpl not(String selector) {
        return not(JOOX.selector(selector));
    }

    @Override
    public ElementsImpl not(Filter filter) {
        return filter(JOOX.not(filter));
    }

    @Override
    public ElementsImpl parent() {
        return parent(JOOX.all());
    }

    @Override
    public ElementsImpl parent(String selector) {
        return parent(JOOX.selector(selector));
    }

    @Override
    public ElementsImpl parent(Filter filter) {
        return parents(false, JOOX.none(), filter);
    }

    @Override
    public ElementsImpl parents() {
        return parents(JOOX.all());
    }

    @Override
    public ElementsImpl parents(String selector) {
        return parents(JOOX.selector(selector));
    }

    @Override
    public ElementsImpl parents(Filter filter) {
        return parents(true, JOOX.none(), filter);
    }

    @Override
    public ElementsImpl parentsUntil(String until) {
        return parentsUntil(JOOX.selector(until), JOOX.all());
    }

    @Override
    public ElementsImpl parentsUntil(Filter until) {
        return parentsUntil(until, JOOX.all());
    }

    @Override
    public ElementsImpl parentsUntil(String until, String selector) {
        return parentsUntil(JOOX.selector(until), JOOX.selector(selector));
    }

    @Override
    public ElementsImpl parentsUntil(String until, Filter filter) {
        return parentsUntil(JOOX.selector(until), filter);
    }

    @Override
    public ElementsImpl parentsUntil(Filter until, String selector) {
        return parentsUntil(until, JOOX.selector(selector));
    }

    @Override
    public ElementsImpl parentsUntil(Filter until, Filter filter) {
        return parents(true, until, filter);
    }

    private ElementsImpl parents(boolean all, Filter until, Filter filter) {
        List<Element> result = new ArrayList<Element>();

        // Maybe reverse iteration and reverse result?
        for (Element element : elements) {
            Node node = element;

            for (int i = 0;;) {
                node = node.getParentNode();

                if (node == null) {
                    break;
                }
                else if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element next = (Element) node;
                    if (until.filter(i, next)) {
                        break;
                    }

                    if (filter.filter(i++, next)) {
                        result.add(next);
                    }

                    if (!all) {
                        break;
                    }
                }
            }
        }

        return new ElementsImpl().addUniqueElements(result);
    }

    @Override
    public ElementsImpl prev() {
        return prev(JOOX.all());
    }

    @Override
    public ElementsImpl prev(String selector) {
        return prev(JOOX.selector(selector));
    }

    @Override
    public ElementsImpl prev(Filter filter) {
        return prev(false, JOOX.none(), filter);
    }

    @Override
    public ElementsImpl prevAll() {
        return prevAll(JOOX.all());
    }

    @Override
    public ElementsImpl prevAll(String selector) {
        return prevAll(JOOX.selector(selector));
    }

    @Override
    public ElementsImpl prevAll(Filter filter) {
        return prev(true, JOOX.none(), filter);
    }

    @Override
    public ElementsImpl prevUntil(String until) {
        return prevUntil(JOOX.selector(until));
    }

    @Override
    public ElementsImpl prevUntil(Filter until) {
        return prevUntil(until, JOOX.all());
    }

    @Override
    public ElementsImpl prevUntil(String until, String selector) {
        return prevUntil(JOOX.selector(until), JOOX.selector(selector));
    }

    @Override
    public ElementsImpl prevUntil(String until, Filter filter) {
        return prevUntil(JOOX.selector(until), filter);
    }

    @Override
    public ElementsImpl prevUntil(Filter until, String selector) {
        return prevUntil(until, JOOX.selector(selector));
    }

    @Override
    public ElementsImpl prevUntil(Filter until, Filter filter) {
        return prev(true, until, filter);
    }

    private ElementsImpl prev(boolean all, Filter until, Filter filter) {
        List<Element> result = new ArrayList<Element>();

        for (Element element : elements) {
            Node node = element;

            for (int i = 0;;) {
                node = node.getPreviousSibling();

                if (node == null) {
                    break;
                }
                else if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element prev = (Element) node;
                    if (until.filter(i, prev)) {
                        break;
                    }

                    if (filter.filter(i++, prev)) {
                        result.add(prev);
                    }

                    if (!all) {
                        break;
                    }
                }
            }
        }

        Collections.reverse(result);
        return new ElementsImpl().addUniqueElements(result);
    }

    @Override
    public ElementsImpl siblings() {
        return siblings(JOOX.all());
    }

    @Override
    public ElementsImpl siblings(String selector) {
        return siblings(JOOX.selector(selector));
    }

    @Override
    public ElementsImpl siblings(Filter filter) {
        return prevAll(filter).add(nextAll(filter));
    }

    @Override
    public ElementsImpl slice(int start) {
        return slice(start, Integer.MAX_VALUE);
    }

    @Override
    public ElementsImpl slice(int start, int end) {
        if (start < 0) {
            start = size() + start;
        }
        if (end < 0) {
            end = size() + end;
        }

        start = Math.max(0, start);
        end = Math.min(size(), end);

        if (start > end) {
            return new ElementsImpl();
        }
        if (start == 0 && end == size()) {
            return this;
        }

        return new ElementsImpl().addElements(elements.subList(start, end));
    }

    @Override
    public ElementsImpl after(String content) {
        return after(JOOX.content(content));
    }

    @Override
    public ElementsImpl after(Content content) {
        List<Element> result = new ArrayList<Element>();

        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            result.add(element);
            Document document = element.getOwnerDocument();

            String text = content.content(i, element);
            DocumentFragment imported = createContent(document, text);
            Node parent = element.getParentNode();
            Node next = element.getNextSibling();

            if (imported != null) {
                result.addAll(JOOX.list(imported.getChildNodes()));
                parent.insertBefore(imported, next);
            }
            else {
                parent.insertBefore(document.createTextNode(text), next);
            }
        }

        elements.clear();
        elements.addAll(result);

        return this;
    }

    @Override
    public ElementsImpl before(String content) {
        return before(JOOX.content(content));
    }

    @Override
    public ElementsImpl before(Content content) {
        List<Element> result = new ArrayList<Element>();

        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            Document document = element.getOwnerDocument();

            String text = content.content(i, element);
            DocumentFragment imported = createContent(document, text);
            Node parent = element.getParentNode();

            if (imported != null) {
                result.addAll(JOOX.list(imported.getChildNodes()));
                parent.insertBefore(imported, element);
            }
            else {
                parent.insertBefore(document.createTextNode(text), element);
            }

            result.add(element);
        }

        elements.clear();
        elements.addAll(result);

        return this;
    }

    @Override
    public ElementsImpl append(String content) {
        return append(JOOX.content(content));
    }

    @Override
    public ElementsImpl append(Content content) {
        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            Document document = element.getOwnerDocument();

            String text = content.content(i, element);
            DocumentFragment imported = createContent(document, text);

            if (imported != null) {
                element.appendChild(imported);
            }
            else {
                element.appendChild(document.createTextNode(text));
            }
        }

        return this;
    }

    @Override
    public ElementsImpl prepend(String content) {
        return prepend(JOOX.content(content));
    }

    @Override
    public ElementsImpl prepend(Content content) {
        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            Document document = element.getOwnerDocument();

            String text = content.content(i, element);
            DocumentFragment imported = createContent(document, text);
            Node first = element.getFirstChild();

            if (imported != null) {
                element.insertBefore(imported, first);
            }
            else {
                element.insertBefore(document.createTextNode(text), first);
            }
        }

        return this;
    }

    @Override
    public String attr(String name) {
        if (size() > 0) {
            return attr(get(0), name);
        }

        return null;
    }

    @Override
    public List<String> attrs(String name) {
        List<String> result = new ArrayList<String>();

        for (Element element : elements) {
            result.add(attr(element, name));
        }

        return result;
    }

    private String attr(Element element, String name) {
        if (element.hasAttribute(name)) {
            return element.getAttribute(name);
        }

        return null;
    }

    @Override
    public ElementsImpl attr(String name, String value) {
        return attr(name, JOOX.content(value));
    }

    @Override
    public ElementsImpl attr(String name, Content content) {
        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            String value = content.content(i, element);

            if (value == null) {
                element.removeAttribute(name);
            }
            else {
                element.setAttribute(name, value);
            }
        }

        return this;
    }

    @Override
    public ElementsImpl removeAttr(String name) {
        return attr(name, (String) null);
    }

    @Override
    public String content() {
        if (size() > 0) {
            return content(get(0));
        }
        else {
            return null;
        }
    }

    @Override
    public List<String> contents() {
        List<String> result = new ArrayList<String>();

        for (Element element : elements) {
            result.add(content(element));
        }

        return result;
    }

    private String content(Element element) {
        NodeList children = element.getChildNodes();

        // The element is empty
        if (children.getLength() == 0) {
            return "";
        }

        // The element contains only text
        else if (!hasElementNodes(children)) {
            return element.getTextContent();
        }

        // The element contains content
        else {
            // TODO: Check this code's efficiency
            String name = element.getTagName();
            return toString(element).replaceAll("^<" + name + "(?:[^>]*)>(.*)</" + name + ">$", "$1");
        }
    }

    private boolean hasElementNodes(NodeList list) {
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ElementsImpl content(String content) {
        return content(JOOX.content(content));
    }

    @Override
    public ElementsImpl content(Content content) {
        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            String text = content.content(i, element);

            DocumentFragment imported = createContent(element.getOwnerDocument(), text);
            if (imported != null) {
                element.setTextContent("");
                element.appendChild(imported);
            }
            else {
                element.setTextContent(text);
            }
        }

        return this;
    }

    private DocumentFragment createContent(Document document, String text) {

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

                return (DocumentFragment) document.importNode(fragment, true);
            }

            // This does not occur
            catch (IOException ignore) {}

            // The XML content is invalid
            catch (SAXException ignore) {}
        }

        // Plain text or invalid XML
        return null;
    }

    @Override
    public String text() {
        if (size() > 0) {
            return get(0).getTextContent();
        }
        else {
            return null;
        }
    }

    @Override
    public List<String> texts() {
        List<String> result = new ArrayList<String>();

        for (Element element : elements) {
            result.add(element.getTextContent());
        }

        return result;
    }

    @Override
    public ElementsImpl text(String content) {
        return text(JOOX.content(content));
    }

    @Override
    public ElementsImpl text(Content content) {
        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            String text = content.content(i, element);
            element.setTextContent(text);
        }

        return this;
    }

    @Override
    public Elements empty() {
        for (Element element : elements) {
            empty(element);
        }

        return this;
    }

    @Override
    public ElementsImpl remove() {
        return remove(JOOX.all());
    }

    @Override
    public ElementsImpl remove(String selector) {
        return remove(JOOX.selector(selector));
    }

    @Override
    public ElementsImpl remove(Filter filter) {
        List<Element> remove = new ArrayList<Element>();

        for (int i = 0; i < size(); i++) {
            Element element = get(i);

            if (filter.filter(i, element)) {
                remove.add(element);
            }
        }

        for (Element element : remove) {
            remove(element);
        }

        return this;
    }

    private void remove(Element element) {
        element.getParentNode().removeChild(element);
        elements.remove(element);
    }

    private void empty(Element element) {
        Node child;

        while ((child = element.getFirstChild()) != null) {
            element.removeChild(child);
        }
    }

    @Override
    public ElementsImpl replaceWith(String content) {
        return replaceWith(JOOX.content(content));
    }

    @Override
    public ElementsImpl replaceWith(Content content) {
        List<Element> result = new ArrayList<Element>();

        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            Document document = element.getOwnerDocument();

            String text = content.content(i, element);
            DocumentFragment imported = createContent(document, text);
            Node parent = element.getParentNode();

            if (imported != null) {
                result.addAll(JOOX.list(imported.getChildNodes()));
                parent.replaceChild(imported, element);
            }
            else {
                parent.replaceChild(document.createTextNode(text), element);
            }
        }

        elements.clear();
        elements.addAll(result);

        return this;
    }

    // -------------------------------------------------------------------------
    // Utility API
    // -------------------------------------------------------------------------

    @Override
    public ElementsImpl copy() {
        ElementsImpl copy = new ElementsImpl();
        copy.elements.addAll(elements);
        return copy;
    }

    @Override
    public String tag() {
        return tag(0);
    }

    @Override
    public String tag(int index) {
        Element element = get(index);

        if (element != null) {
            return element.getTagName();
        }
        else {
            return null;
        }
    }

    @Override
    public List<String> tags() {
        List<String> result = new ArrayList<String>();

        for (Element element : elements) {
            result.add(element.getTagName());
        }

        return result;
    }

    @Override
    public String id() {
        return id(0);
    }

    @Override
    public String id(int index) {
        return eq(index).attr("id");
    }

    @Override
    public List<String> ids() {
        return attrs("id");
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        if (elements.size() == 0) {
            return "[]";
        }
        else if (elements.size() == 1) {
            return toString(get(0));
        }
        else {
            StringBuilder sb = new StringBuilder();
            String separator = "";

            sb.append("[");

            for (Element element : elements) {
                sb.append(separator);
                sb.append(toString(element));
                separator = ",\n";
            }

            sb.append("]");
            return sb.toString();
        }
    }

    private String toString(Element element) {
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
}
