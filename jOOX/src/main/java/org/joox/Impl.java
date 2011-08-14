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
import static org.joox.JOOX.iterable;
import static org.joox.JOOX.none;
import static org.joox.JOOX.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Lukas Eder
 */
class Impl implements Match {

    private final Document document;
    private final List<Element> elements;

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    Impl(Document document) {
        this.document = document;
        this.elements = new ArrayList<Element>();
    }

    final Impl addNodeLists(List<NodeList> lists) {
        for (NodeList list : lists) {
            addNodeList(list);
        }

        return this;
    }

    final Impl addNodeList(NodeList list) {
        for (int i = 0; i < list.getLength(); i++) {
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

    final Impl addElements(Element... e) {
        this.elements.addAll(Arrays.asList(e));
        return this;
    }

    final Impl addElements(List<Element> e) {
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
    public final Element get(int index) {
        try {
            if (index >= 0) {
                return elements.get(index);
            }
            else {
                return elements.get(elements.size() + index);
            }
        }
        catch (IndexOutOfBoundsException e) {
            return null;
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
        return Collections.unmodifiableList(elements);
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
    public final Impl child() {
        return child(0);
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
        List<Element> result = new ArrayList<Element>();

        for (Element element : elements) {
            int index = 0;

            for (Element child : iterable(element.getChildNodes())) {
                if (filter.filter(index++, child)) {
                    result.add(child);
                }
            }
        }

        return new Impl(document).addUniqueElements(result);
    }

    @Override
    public final Impl each(Each each) {
        for (int i = 0; i < size(); i++) {
            each.each(i, get(i));
        }

        return this;
    }

    @Override
    public final Impl filter(String selector) {
        return filter(selector(selector));
    }

    @Override
    public final Impl filter(Filter filter) {
        List<Element> result = new ArrayList<Element>();

        for (int i = 0; i < size(); i++) {
            if (filter.filter(i, get(i))) {
                result.add(get(i));
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
        List<NodeList> result = new ArrayList<NodeList>();

        for (Element element : elements) {
            result.add(element.getElementsByTagName(selector));
        }

        return new Impl(document).addNodeLists(result);
    }

    @Override
    public final Impl find(Filter filter) {
        List<Element> result = new ArrayList<Element>();

        int index = 0;
        for (int i = 0; i < size(); i++) {
            for (Element descendant : iterable(get(i).getElementsByTagName("*"))) {
                if (filter.filter(index++, descendant)) {
                    result.add(descendant);
                }
            }
        }

        return new Impl(document).addUniqueElements(result);
    }

    @Override
    public final Impl xpath(String expression) {
        List<Element> result = new ArrayList<Element>();

        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPathExpression xpath = factory.newXPath().compile(expression);

            for (Element element : get()) {
                for (Element match : iterable((NodeList) xpath.evaluate(element, XPathConstants.NODESET))) {
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

        for (int i = 0; i < size(); i++) {
            Element element = elements.get(i);

            inner: for (Element child : iterable(element.getElementsByTagName("*"))) {
                if (filter.filter(i, child)) {
                    result.add(element);
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
        for (int i = 0; i < size(); i++) {
            if (filter.filter(i, elements.get(i))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public final Impl last() {
        if (size() > 0) {
            return new Impl(document).addElements(get(size() - 1));
        }
        else {
            return new Impl(document);
        }
    }

    @Override
    public final <E> List<E> map(Mapper<E> map) {
        final List<E> result = new ArrayList<E>();

        for (int i = 0; i < size(); i++) {
            result.add(map.map(i, elements.get(i)));
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

        return new Impl(document).addUniqueElements(result);
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

        return new Impl(document).addUniqueElements(result);
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
        return new Impl(document).addUniqueElements(result);
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
        if (start < 0) {
            start = size() + start;
        }
        if (end < 0) {
            end = size() + end;
        }

        start = Math.max(0, start);
        end = Math.min(size(), end);

        if (start > end) {
            return new Impl(document);
        }
        if (start == 0 && end == size()) {
            return this;
        }

        return new Impl(document).addElements(elements.subList(start, end));
    }

    @Override
    public final Impl after(String content) {
        return after(JOOX.content(content));
    }

    @Override
    public final Impl after(Content content) {
        List<Element> result = new ArrayList<Element>();

        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            result.add(element);
            Document doc = element.getOwnerDocument();

            String text = content.content(i, element);
            DocumentFragment imported = Util.createContent(doc, text);
            Node parent = element.getParentNode();
            Node next = element.getNextSibling();

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
        List<Element> result = new ArrayList<Element>();
        List<Element> detached = Util.importOrDetach(document, content);

        for (int i = 0; i < size(); i++) {
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
        List<Element> result = new ArrayList<Element>();

        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            Document doc = element.getOwnerDocument();

            String text = content.content(i, element);
            DocumentFragment imported = Util.createContent(doc, text);
            Node parent = element.getParentNode();

            if (imported != null) {
                result.addAll(JOOX.list(imported.getChildNodes()));
                parent.insertBefore(imported, element);
            }
            else {
                parent.insertBefore(doc.createTextNode(text), element);
            }

            result.add(element);
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
        List<Element> result = new ArrayList<Element>();
        List<Element> detached = Util.importOrDetach(document, content);

        for (int i = 0; i < size(); i++) {
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
        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            Document doc = element.getOwnerDocument();

            String text = content.content(i, element);
            DocumentFragment imported = Util.createContent(doc, text);

            if (imported != null) {
                element.appendChild(imported);
            }
            else {
                element.appendChild(doc.createTextNode(text));
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
        List<Element> detached = Util.importOrDetach(document, content);

        for (int i = 0; i < size(); i++) {
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
        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            Document doc = element.getOwnerDocument();

            String text = content.content(i, element);
            DocumentFragment imported = Util.createContent(doc, text);
            Node first = element.getFirstChild();

            if (imported != null) {
                element.insertBefore(imported, first);
            }
            else {
                element.insertBefore(doc.createTextNode(text), first);
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
        List<Element> detached = Util.importOrDetach(document, content);

        for (int i = 0; i < size(); i++) {
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
    public final List<String> attrs(String name) {
        List<String> result = new ArrayList<String>();

        for (Element element : elements) {
            result.add(Util.attr(element, name));
        }

        return result;
    }

    @Override
    public final Impl attr(String name, String value) {
        return attr(name, JOOX.content(value));
    }

    @Override
    public final Impl attr(String name, Content content) {
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
    public final Impl content(Content content) {
        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            String text = content.content(i, element);

            DocumentFragment imported = Util.createContent(element.getOwnerDocument(), text);
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
    public final Impl text(String content) {
        return text(JOOX.content(content));
    }

    @Override
    public final Impl text(Content content) {
        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            String text = content.content(i, element);
            element.setTextContent(text);
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
    public final Impl replaceWith(String content) {
        return replaceWith(JOOX.content(content));
    }

    @Override
    public final Impl replaceWith(Content content) {
        List<Element> result = new ArrayList<Element>();

        for (int i = 0; i < size(); i++) {
            Element element = get(i);
            Document doc = element.getOwnerDocument();

            String text = content.content(i, element);
            DocumentFragment imported = Util.createContent(doc, text);
            Node parent = element.getParentNode();

            if (imported != null) {
                result.addAll(JOOX.list(imported.getChildNodes()));
                parent.replaceChild(imported, element);
            }
            else {
                parent.replaceChild(doc.createTextNode(text), element);
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
        List<Element> result = new ArrayList<Element>();
        List<Element> detached = Util.importOrDetach(document, content);

        for (int i = 0; i < size(); i++) {
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

    // -------------------------------------------------------------------------
    // Utility API
    // -------------------------------------------------------------------------

    @Override
    public final Impl copy() {
        Impl copy = new Impl(document);
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
}
