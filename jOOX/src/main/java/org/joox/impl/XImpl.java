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

import static org.joox.impl.XML.iterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.joox.Each;
import org.joox.Filter;
import org.joox.Mapper;
import org.joox.X;
import org.joox.XContent;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Lukas Eder
 */
class XImpl implements X {

    private final List<Element> elements;

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    XImpl() {
        this.elements = new ArrayList<Element>();
    }

    XImpl addNodeLists(List<NodeList> lists) {
        for (NodeList list : lists) {
            addNodeList(list);
        }

        return this;
    }

    XImpl addNodeList(NodeList list) {
        for (int i = 0; i < list.getLength(); i++) {
            elements.add((Element) list.item(i));
        }

        return this;
    }

    XImpl addUniqueElements(Element... e) {
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

    XImpl addUniqueElements(List<Element> e) {
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

    XImpl addElements(Element... e) {
        this.elements.addAll(Arrays.asList(e));
        return this;
    }

    XImpl addElements(List<Element> e) {
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
        return elements.get(index);
    }

    @Override
    public List<Element> get() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public int index() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int index(String selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int index(Element element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int index(X element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public XImpl add(Element... e) {
        XImpl x = copy();
        x.addUniqueElements(e);
        return x;
    }

    @Override
    public XImpl add(X... e) {
        XImpl x = copy();

        for (X element : e) {
            x.addUniqueElements(element.get());
        }

        return x;
    }

    @Override
    public XImpl children() {
        return children(XML.all());
    }

    @Override
    public XImpl children(String selector) {
        return children(XML.selector(selector));
    }

    @Override
    public XImpl children(Filter filter) {
        List<Element> result = new ArrayList<Element>();

        int index = 0;
        for (Element element : elements) {
            for (Element child : iterable(element.getChildNodes())) {
                if (filter.filter(index++, child)) {
                    result.add(child);
                }
            }
        }

        return new XImpl().addUniqueElements(result);
    }

    @Override
    public XImpl each(Each each) {
        for (int i = 0; i < size(); i++) {
            each.each(i, get(i));
        }

        return this;
    }

    @Override
    public XImpl filter(String selector) {
        return filter(XML.selector(selector));
    }

    @Override
    public XImpl filter(Filter filter) {
        List<Element> result = new ArrayList<Element>();

        for (int i = 0; i < size(); i++) {
            if (filter.filter(i, get(i))) {
                result.add(get(i));
            }
        }

        return new XImpl().addElements(result);
    }

    @Override
    public XImpl eq(int index) {
        return new XImpl().addElements(get(index));
    }

    @Override
    public XImpl find() {
        return find(XML.all());
    }

    @Override
    public XImpl find(String selector) {
        List<NodeList> result = new ArrayList<NodeList>();

        for (Element element : elements) {
            result.add(element.getElementsByTagName(selector));
        }

        return new XImpl().addNodeLists(result);
    }

    @Override
    public XImpl find(Filter filter) {
        List<Element> result = new ArrayList<Element>();

        int index = 0;
        for (int i = 0; i < size(); i++) {
            for (Element descendant : iterable(get(i).getElementsByTagName("*"))) {
                if (filter.filter(index++, descendant)) {
                    result.add(descendant);
                }
            }
        }

        return new XImpl().addUniqueElements(result);
    }

    @Override
    public XImpl first() {
        if (size() > 0) {
            return new XImpl().addElements(get(0));
        }
        else {
            return new XImpl();
        }
    }

    @Override
    public XImpl has(String selector) {
        return has(XML.selector(selector));
    }

    @Override
    public XImpl has(Filter filter) {
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

        return new XImpl().addElements(result);
    }

    @Override
    public boolean is(String selector) {
        return is(XML.selector(selector));
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
    public XImpl last() {
        if (size() > 0) {
            return new XImpl().addElements(get(size() - 1));
        }
        else {
            return new XImpl();
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
    public XImpl next() {
        return next(XML.all());
    }

    @Override
    public XImpl next(String selector) {
        return next(XML.selector(selector));
    }

    @Override
    public XImpl next(Filter filter) {
        return next(false, filter);
    }

    @Override
    public XImpl nextAll() {
        return nextAll(XML.all());
    }

    @Override
    public XImpl nextAll(String selector) {
        return nextAll(XML.selector(selector));
    }

    @Override
    public XImpl nextAll(Filter filter) {
        return next(true, filter);
    }

    private XImpl next(boolean all, Filter filter) {
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
                    if (filter.filter(i++, next)) {
                        result.add(next);
                    }

                    if (!all) {
                        break;
                    }
                }
            }
        }

        return new XImpl().addUniqueElements(result);
    }

    @Override
    public XImpl nextUntil(String selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl nextUntil(String selector, Filter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl not(String selector) {
        return not(XML.selector(selector));
    }

    @Override
    public XImpl not(Filter filter) {
        return filter(XML.not(filter));
    }

    @Override
    public XImpl parent() {
        return parent(XML.all());
    }

    @Override
    public XImpl parent(String selector) {
        return parent(XML.selector(selector));
    }

    @Override
    public XImpl parent(Filter filter) {
        List<Element> result = new ArrayList<Element>();

        for (int i = 0; i < size(); i++) {
            Node node = get(i).getParentNode();

            if (node instanceof Element) {
                Element parent = (Element) node;

                if (filter.filter(i, parent)) {
                    result.add(parent);
                }
            }
        }

        return new XImpl().addUniqueElements(result);
    }

    @Override
    public XImpl parents() {
        return parents(XML.all());
    }

    @Override
    public XImpl parents(String selector) {
        return parents(XML.selector(selector));
    }

    @Override
    public XImpl parents(Filter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl parentsUntil(String selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl parentsUntil(String selector, Filter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl prev() {
        return prev(XML.all());
    }

    @Override
    public XImpl prev(String selector) {
        return prev(XML.selector(selector));
    }

    @Override
    public XImpl prev(Filter filter) {
        return prev(false, filter);
    }

    @Override
    public XImpl prevAll() {
        return prevAll(XML.all());
    }

    @Override
    public XImpl prevAll(String selector) {
        return prevAll(XML.selector(selector));
    }

    @Override
    public XImpl prevAll(Filter filter) {
        return prev(true, filter);
    }

    private XImpl prev(boolean all, Filter filter) {
        List<Element> result = new ArrayList<Element>();

        for (Element element : elements) {
            Node node = element;

            for (int i = 0;;) {
                node = node.getPreviousSibling();

                if (node == null) {
                    break;
                }
                else if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element next = (Element) node;
                    if (filter.filter(i++, next)) {
                        result.add(next);
                    }

                    if (!all) {
                        break;
                    }
                }
            }
        }

        Collections.reverse(result);
        return new XImpl().addUniqueElements(result);
    }

    @Override
    public XImpl prevUntil(String selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl prevUntil(String selector, Filter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl siblings() {
        return siblings(XML.all());
    }

    @Override
    public XImpl siblings(String selector) {
        return siblings(XML.selector(selector));
    }

    @Override
    public XImpl siblings(Filter filter) {
        return null;
    }

    @Override
    public XImpl slice(int start) {
        return slice(start, Integer.MAX_VALUE);
    }

    @Override
    public XImpl slice(int start, int end) {
        if (start < 0) {
            start = size() + start;
        }
        if (end < 0) {
            end = size() + end;
        }

        start = Math.max(0, start);
        end = Math.min(size(), end);

        if (start > end) {
            return new XImpl();
        }
        if (start == 0 && end == size()) {
            return this;
        }

        return new XImpl().addElements(elements.subList(start, end));
    }

    @Override
    public XImpl after(String... content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl after(Element... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl after(X... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl after(XContent content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl append(String... content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl append(Element... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl append(X... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl append(XContent content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl appendTo(String selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl appendTo(Element element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl appendTo(X element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String attr(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl attr(String name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl attr(String name, XContent value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl before(String... content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl before(Element... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl before(X... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl before(XContent content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl empty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String content() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl content(String content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl content(XContent content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String text() {
        StringBuilder sb = new StringBuilder();

        for (Element element : elements) {
            sb.append(element.getTextContent());
        }

        return sb.toString();
    }

    @Override
    public XImpl text(String content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl text(XContent content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl insertAfter(String... content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl insertAfter(Element... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl insertAfter(X... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl insertAfter(XContent content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl insertBefore(String... content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl insertBefore(Element... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl insertBefore(X... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl insertBefore(XContent content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl prepend(String... content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl prepend(Element... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl prepend(X... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl prepend(XContent content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl prependTo(String selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl prependTo(Element element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl prependTo(X element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl remove(String selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl removeAttr(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl replaceAll(String selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl replaceAll(Element... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl replaceAll(X... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl replaceWith(String content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl replaceWith(Element... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl replaceWith(X... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl replaceWith(XContent content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl unwrap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl wrap(String content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl wrap(Element element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl wrap(X element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl wrap(XContent content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl wrapAll(String content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl wrapAll(Element element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl wrapAll(X element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl wrapInner(String content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl wrapInner(Element element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl wrapInner(X element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XImpl wrapInner(XContent content) {
        throw new UnsupportedOperationException();
    }

    // -------------------------------------------------------------------------
    // Utility API
    // -------------------------------------------------------------------------

    @Override
    public XImpl copy() {
        XImpl copy = new XImpl();
        copy.elements.addAll(elements);
        return copy;
    }

    @Override
    public String tag() {
        return tag(0);
    }

    @Override
    public String tag(int index) {
        return get(index).getTagName();
    }

    @Override
    public List<String> tags() {
        List<String> result = new ArrayList<String>();

        for (Element element : elements) {
            result.add(element.getTagName());
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return elements.toString();
    }
}
