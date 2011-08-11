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
package org.joox.test;

import static java.util.Collections.nCopies;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.joox.impl.JOOX.joox;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtil;
import org.joox.Each;
import org.joox.Elements;
import org.joox.Filter;
import org.joox.Mapper;
import org.joox.impl.JOOX;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author Lukas Eder
 */
public class JOOXTest {

    private String xmlString;
    private Document xmlDocument;
    private Element xmlElement;
    private int totalElements;
    private Elements joox;
    private XPath xPath;

    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        xmlString = IOUtil.toString(JOOXTest.class.getResourceAsStream("/example.xml"));
        xmlDocument = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));
        xmlElement = xmlDocument.getDocumentElement();
        joox = joox(xmlDocument);
        xPath = XPathFactory.newInstance().newXPath();
        totalElements = ((Number) xPath
            .evaluate("count(//*)", xmlDocument, XPathConstants.NUMBER))
            .intValue() - 1;

    }

    // -------------------------------------------------------------------------
    // DOM access
    // -------------------------------------------------------------------------

    @Test
    public void testSize() {
        assertEquals(1, joox.size());
        assertEquals(3, joox.children().size());
    }

    // -------------------------------------------------------------------------
    // Traversing
    // -------------------------------------------------------------------------

    @Test
    public void testAdd() {
        assertEquals(0, joox().size());
        assertEquals(1, joox().add(xmlElement).size());
        assertEquals(1, joox().add(xmlElement, xmlElement).size());

        Elements x = joox().add(
            (Element) xmlElement.getElementsByTagName("director").item(0),
            (Element) xmlElement.getElementsByTagName("actor").item(0));
        assertEquals(2, x.size());
        assertEquals("director", x.get(0).getTagName());
        assertEquals("actor", x.get(1).getTagName());

        x = x.add(joox(xmlElement).find("dvds"));
        assertEquals(3, x.size());
        assertEquals("director", x.get(0).getTagName());
        assertEquals("actor", x.get(1).getTagName());
        assertEquals("dvds", x.get(2).getTagName());

        x = x.add(x.filter("dvds").find());
        assertEquals(9, x.size());
        assertEquals(
            Arrays.asList("dvds", "dvd", "name", "directors", "director", "actors", "actor", "actor", "actor"),
            x.tags());
    }

    @Test
    public void testChildren() {
        assertEquals("library", joox.children().get().get(0).getTagName());
        assertEquals("library", joox.children().get().get(1).getTagName());
        assertEquals("library", joox.children().get().get(2).getTagName());
        assertEquals(2, joox(joox.children().get().get(0)).children().size());
        assertEquals("books", joox(joox.children().get().get(0)).children().get().get(0).getTagName());
        assertEquals(5, joox(joox.children().get().get(0)).children().children().size());
        assertEquals("book", joox(joox.children().get().get(0)).children().children().get().get(0).getTagName());
        assertEquals("1", joox(joox.children().get().get(0)).children().children().get().get(0).getAttribute("id"));
        assertEquals("2", joox(joox.children().get().get(0)).children().children().get().get(1).getAttribute("id"));
        assertEquals("3", joox(joox.children().get().get(0)).children().children().get().get(2).getAttribute("id"));
        assertEquals("4", joox(joox.children().get().get(0)).children().children().get().get(3).getAttribute("id"));

        assertEquals(4, joox.children().children().size());
        assertEquals("books", joox.children().children().get().get(0).getTagName());
        assertEquals("dvds", joox.children().children().get().get(1).getTagName());
        assertEquals("books", joox.children().children().get().get(2).getTagName());
        assertEquals("books", joox.children().children().get().get(3).getTagName());
    }

    @Test
    public void testChildrenSelector() {
        assertEquals(0, joox.children("document").size());
        assertEquals(0, joox.children("asdf").size());
        assertEquals(3, joox.children("library").size());
    }

    @Test
    public void testChildrenFilter() {
        assertEquals(0, joox.children(JOOX.none()).size());
        assertEquals(1, joox.children().children(JOOX.tag("dvds")).size());
        assertEquals(1, joox.children().children(JOOX.tag("dvds")).children(JOOX.tag("dvd")).size());
    }

    @Test
    public void testEach() {
        final Queue<Integer> queue = new LinkedList<Integer>();

        queue.addAll(Arrays.asList(0));
        joox.each(new Each() {
            @Override
            public void each(int index, Element element) {
                assertEquals((int) queue.poll(), index);
                assertEquals("document", element.getTagName());
            }
        });

        assertTrue(queue.isEmpty());
        queue.addAll(Arrays.asList(0, 1, 2));

        joox.children().each(new Each() {
            @Override
            public void each(int index, Element element) {
                assertEquals((int) queue.poll(), index);
                assertEquals("library", element.getTagName());
            }
        });

        assertTrue(queue.isEmpty());
    }

    @Test
    public void testEq() {
        assertEquals("authors", joox.find().eq(4).tag(0));
        assertEquals("author", joox.find().eq(5).tag(0));
        assertEquals("George Orwell", joox.find().eq(5).text());
    }

    @Test
    public void testFilter() {
        assertEquals(0, joox.filter("asdf").size());
        assertEquals(1, joox.filter("document").size());
        assertEquals(3, joox.find().filter("actor").size());
        assertEquals(3, joox.find().filter("actor").filter(JOOX.all()).size());
        assertEquals(2, joox.find().filter("actor").filter(JOOX.even()).size());
        assertEquals(1, joox.find().filter("actor").filter(JOOX.odd()).size());
    }

    @Test
    public void testFind() {
        assertEquals(0, joox.find("document").size());
        assertEquals(0, joox.find("asdf").size());
        assertEquals(0, joox.find("document").find("document").size());
        assertEquals(0, joox.find("document").find("libary").size());
        assertEquals(3, joox.find("library").size());
        assertEquals(8, joox.find("book").size());
        assertEquals(8, joox.find("book").get().size());
        assertEquals("book", joox.find("book").get().get(2).getTagName());
        assertEquals("book", joox.find("book").get(2).getTagName());
        assertEquals("4", joox.find("book").get().get(3).getAttribute("id"));
        assertEquals("4", joox.find("book").get(3).getAttribute("id"));
    }

    @Test
    public void testFindFilter() throws Exception {
        assertEquals(0, joox.find(JOOX.none()).size());
        assertEquals(totalElements, joox.find().size());
        assertEquals(totalElements, joox.find(JOOX.all()).size());
        assertEquals((totalElements + 1) / 2, joox.find(JOOX.even()).size());
        assertEquals(totalElements / 2, joox.find(JOOX.odd()).size());
        assertEquals(3, joox.find(JOOX.tag("library")).size());
        assertEquals(8, joox.find(JOOX.tag("book")).size());
    }

    @Test
    public void testFirst() throws Exception {
        assertEquals(0, joox.find("document").first().size());
        assertEquals(1, joox.first().size());
        assertEquals("document", joox.first().tag());
        assertEquals("books", joox.children().first().children().first().tag());
    }

    @Test
    public void testHas() throws Exception {
        assertEquals(0, joox.has("asdf").size());
        assertEquals(0, joox.has("document").size());
        assertEquals(1, joox.has("library").size());
        assertEquals(1, joox.has("authors").size());
        assertEquals(3, joox.children().has("authors").size());
        assertEquals(1, joox.children().has("dvds").size());
    }

    @Test
    public void testIs() throws Exception {
        assertFalse(joox.is("abc"));
        assertTrue(joox.is("document"));
        assertTrue(joox.is(JOOX.even()));
    }

    @Test
    public void testLast() throws Exception {
        assertEquals(0, joox.find("document").last().size());
        assertEquals(1, joox.last().size());
        assertEquals("document", joox.last().tag());
        assertEquals("dvds", joox.children().eq(0).children().last().tag());
    }

    @Test
    public void testMap() throws Exception {
        assertEquals(
            Arrays.asList("1", "2", "3", "4", "1", "3", "1", "2"),
            joox.find("book").map(JOOX.ids()));

        assertEquals(
            Arrays.asList("Amazon", "Rösslitor", "Orell Füssli"),
            joox.find("library").map(JOOX.attributes("name")));

        assertEquals(Arrays.asList(0, 1, 2, 3), joox.children().first().find("book").map(new Mapper<Integer>() {
            @Override
            public Integer map(int index, Element element) {
                return index;
            }
        }));
    }

    @Test
    public void testNext() throws Exception {
        assertEquals(0, joox.next().size());
        assertEquals(5, joox.find("book").next().size());
        assertEquals(2, joox.find("book").next().next().size());
        assertEquals(1, joox.find("book").next().next().next().size());
        assertEquals(0, joox.find("book").next().next().next().next().size());

        assertEquals(1, joox.find("book").eq(0).next().size());
        assertEquals(1, joox.find("book").eq(0).next(JOOX.all()).size());
        assertEquals(0, joox.find("book").eq(0).next(JOOX.none()).size());
        assertEquals(0, joox.find("book").eq(0).next(new Filter() {
            @Override
            public boolean filter(int index, Element element) {
                return "Paulo Coelho".equals(joox(element).find("author").text());
            }
        }).size());
        assertEquals(1, joox.find("book").eq(1).next(new Filter() {
            @Override
            public boolean filter(int index, Element element) {
                return "Paulo Coelho".equals(joox(element).find("author").text());
            }
        }).size());
    }

    @Test
    public void testNextAll() throws Exception {
        assertEquals(0, joox.nextAll().size());
        assertEquals(5, joox.find("book").nextAll().size());
        assertEquals(2, joox.find("book").nextAll().nextAll().size());
        assertEquals(1, joox.find("book").nextAll().nextAll().nextAll().size());
        assertEquals(0, joox.find("book").nextAll().nextAll().nextAll().nextAll().size());

        assertEquals(3, joox.find("book").eq(0).nextAll().size());
        assertEquals(2, joox.find("book").eq(0).nextAll().nextAll().size());
    }

    @Test
    public void testNextUntil() throws Exception {
        assertEquals(0, joox.nextUntil("asdf").size());
        assertEquals(2, joox.find("dvd").children().eq(0).nextUntil("any").size());
        assertEquals(
            Arrays.asList("directors", "actors"),
            joox.find("dvd").children().eq(0).nextUntil("any").tags());
        assertEquals(1,
            joox.find("dvd").children().eq(0).nextUntil("actors").size());
        assertEquals("directors",
            joox.find("dvd").children().eq(0).nextUntil("actors").tag());
    }

    @Test
    public void testParent() throws Exception {
        assertEquals(0, joox.parent().size());
        assertEquals(3, joox.find("book").parent().size());
        assertEquals(nCopies(3, "books"), joox.find("book").parent().tags());
        assertEquals(nCopies(8, "book"), joox.find("authors").parent().tags());
    }

    @Test
    public void testParents() throws Exception {
        assertEquals(0, joox.parents().size());
        assertEquals(1, joox.find("library").parents().size());
        assertEquals(4, joox.find("books").parents().size());
        assertEquals(7, joox.find("book").parents().size());
        assertEquals(15, joox.find("authors").parents().size());
        assertEquals(23, joox.find("author").parents().size());
        assertEquals(26, joox.find("author").add(joox.find("actor")).parents().size());
    }

    @Test
    public void testParentsUntil() throws Exception {
        assertEquals(0, joox.parentsUntil("books").size());
        assertEquals(1, joox.find("library").parentsUntil("books").size());
        assertEquals(4, joox.find("books").parentsUntil("books").size());
        assertEquals(0, joox.find("book").parentsUntil("books").size());
        assertEquals(8, joox.find("authors").parentsUntil("books").size());
        assertEquals(16, joox.find("author").parentsUntil("books").size());
        assertEquals(21, joox.find("author").add(joox.find("actor")).parentsUntil("books").size());
    }

    @Test
    public void testPrev() throws Exception {
        assertEquals(0, joox.prev().size());
        assertEquals(5, joox.find("book").prev().size());
        assertEquals(2, joox.find("book").prev().prev().size());
        assertEquals(1, joox.find("book").prev().prev().prev().size());
        assertEquals(0, joox.find("book").prev().prev().prev().prev().size());

        assertEquals(1, joox.find("book").eq(7).prev().size());
        assertEquals(1, joox.find("book").eq(7).prev(JOOX.all()).size());
        assertEquals(0, joox.find("book").eq(7).prev(JOOX.none()).size());
        assertEquals(0, joox.find("book").eq(7).prev(new Filter() {
            @Override
            public boolean filter(int index, Element element) {
                return "Paulo Coelho".equals(joox(element).find("author").text());
            }
        }).size());
        assertEquals(1, joox.find("book").eq(3).prev(new Filter() {
            @Override
            public boolean filter(int index, Element element) {
                return "Paulo Coelho".equals(joox(element).find("author").text());
            }
        }).size());
    }

    @Test
    public void testPrevAll() throws Exception {
        assertEquals(0, joox.prevAll().size());
        assertEquals(5, joox.find("book").prevAll().size());
        assertEquals(2, joox.find("book").prevAll().prevAll().size());
        assertEquals(1, joox.find("book").prevAll().prevAll().prevAll().size());
        assertEquals(0, joox.find("book").prevAll().prevAll().prevAll().prevAll().size());

        assertEquals(3, joox.find("book").eq(3).prevAll().size());
        assertEquals(2, joox.find("book").eq(3).prevAll().prevAll().size());
    }

    @Test
    public void testPrevUntil() throws Exception {
        assertEquals(0, joox.prevUntil("asdf").size());
        assertEquals(2, joox.find("dvd").children().eq(2).prevUntil("any").size());
        assertEquals(
            Arrays.asList("name", "directors"),
            joox.find("dvd").children().eq(2).prevUntil("any").tags());
        assertEquals(1,
            joox.find("dvd").children().eq(2).prevUntil("name").size());
        assertEquals("directors",
            joox.find("dvd").children().eq(2).prevUntil("name").tag());
    }

    @Test
    public void testSiblings() throws Exception {
        assertEquals(0, joox.siblings().size());
        assertEquals(3, joox.find("library").siblings().size());
        assertEquals(
            Arrays.asList("library", "library", "library"),
            joox.find("library").siblings().tags());
        assertEquals(2, joox.find("library").eq(0).siblings().size());
        assertEquals(2, joox.find("library").eq(1).siblings().size());
        assertEquals(2, joox.find("library").eq(2).siblings().size());
        assertEquals(
            Arrays.asList("library", "library"),
            joox.find("library").eq(0).siblings().tags());
        assertEquals(0, joox.find("library").eq(3).siblings().size());
    }

    @Test
    public void testSlice() throws Exception {
        assertEquals(0, joox.slice(1).size());
        assertEquals(1, joox.slice(0).size());
        assertEquals(1, joox.slice(-1).size());
        assertEquals(1, joox.slice(-2).size());

        assertEquals(0, joox.slice(1, 1).size());
        assertEquals(1, joox.slice(0, 1).size());
        assertEquals(1, joox.slice(-1, 1).size());
        assertEquals(1, joox.slice(-2, 1).size());

        assertEquals(8, joox.find("book").slice(-9).size());
        assertEquals(8, joox.find("book").slice(-8).size());
        assertEquals(2, joox.find("book").slice(-2).size());
        assertEquals(1, joox.find("book").slice(-1).size());
        assertEquals(8, joox.find("book").slice(0).size());
        assertEquals(7, joox.find("book").slice(1).size());
        assertEquals(6, joox.find("book").slice(2).size());
        assertEquals(5, joox.find("book").slice(3).size());
        assertEquals(4, joox.find("book").slice(4).size());
        assertEquals(3, joox.find("book").slice(5).size());
        assertEquals(2, joox.find("book").slice(6).size());
        assertEquals(1, joox.find("book").slice(7).size());
        assertEquals(0, joox.find("book").slice(8).size());
        assertEquals(0, joox.find("book").slice(9).size());

        assertEquals(5, joox.find("book").slice(-9, 5).size());
        assertEquals(5, joox.find("book").slice(-8, 5).size());
        assertEquals(0, joox.find("book").slice(-2, 5).size());
        assertEquals(0, joox.find("book").slice(-1, 5).size());
        assertEquals(5, joox.find("book").slice(0, 5).size());
        assertEquals(4, joox.find("book").slice(1, 5).size());
        assertEquals(3, joox.find("book").slice(2, 5).size());
        assertEquals(2, joox.find("book").slice(3, 5).size());
        assertEquals(1, joox.find("book").slice(4, 5).size());
        assertEquals(0, joox.find("book").slice(5, 5).size());
        assertEquals(0, joox.find("book").slice(6, 5).size());
        assertEquals(0, joox.find("book").slice(7, 5).size());
        assertEquals(0, joox.find("book").slice(8, 5).size());
        assertEquals(0, joox.find("book").slice(9, 5).size());

        assertEquals(3, joox.find("book").slice(-9, -5).size());
        assertEquals(3, joox.find("book").slice(-8, -5).size());
        assertEquals(0, joox.find("book").slice(-2, -5).size());
        assertEquals(0, joox.find("book").slice(-1, -5).size());
        assertEquals(3, joox.find("book").slice(0, -5).size());
        assertEquals(2, joox.find("book").slice(1, -5).size());
        assertEquals(1, joox.find("book").slice(2, -5).size());
        assertEquals(0, joox.find("book").slice(3, -5).size());
        assertEquals(0, joox.find("book").slice(4, -5).size());
        assertEquals(0, joox.find("book").slice(5, -5).size());
        assertEquals(0, joox.find("book").slice(6, -5).size());
        assertEquals(0, joox.find("book").slice(7, -5).size());
        assertEquals(0, joox.find("book").slice(8, -5).size());
        assertEquals(0, joox.find("book").slice(9, -5).size());
    }

    @Test
    public void testDOMAccess() throws Exception {
        assertEquals(xmlElement, joox.get(0));
        assertEquals(xmlElement, joox.get().get(0));
        assertNull(joox.get(1));

        assertEquals("document", joox.tag());
        assertEquals("document", joox.tags().get(0));
        assertEquals("document", joox.tag(0));
        assertNull(joox.tag(1));
        assertNull(joox.next().tag());
        assertNull(joox.next().tag(0));
        assertNull(joox.next().tag(1));
    }

    @Test
    public void testAndOrNot() throws Exception {
        assertEquals(1, joox.filter(JOOX.and(JOOX.all(), JOOX.all())).size());
        assertEquals(0, joox.filter(JOOX.and(JOOX.all(), JOOX.none())).size());
        assertEquals(0, joox.filter(JOOX.and(JOOX.none(), JOOX.all())).size());
        assertEquals(0, joox.filter(JOOX.and(JOOX.none(), JOOX.none())).size());

        assertEquals(1, joox.filter(JOOX.or(JOOX.all(), JOOX.all())).size());
        assertEquals(1, joox.filter(JOOX.or(JOOX.all(), JOOX.none())).size());
        assertEquals(1, joox.filter(JOOX.or(JOOX.none(), JOOX.all())).size());
        assertEquals(0, joox.filter(JOOX.or(JOOX.none(), JOOX.none())).size());

        assertEquals(0, joox.filter(JOOX.not(JOOX.all())).size());
        assertEquals(1, joox.filter(JOOX.not(JOOX.none())).size());
    }

    @Test
    public void testAttr() throws Exception {
        assertNull(joox.attr("any"));
        assertNull(joox.attr("id"));
        assertEquals(Arrays.asList((String) null), joox.attrs("any"));
        assertEquals(Arrays.asList((String) null), joox.attrs("id"));
        assertEquals("1", joox.find("book").attr("id"));
        assertEquals(
            Arrays.asList("1", "2", "3", "4", "1", "3", "1", "2"),
            joox.find("book").attrs("id"));

        assertEquals(
            Collections.nCopies(totalElements, "y"),
            joox.find().attr("x", "y").attrs("x"));
        assertEquals(
            Collections.nCopies(totalElements, (String) null),
            joox.find().attr("x", (String) null).attrs("x"));

        assertEquals(
            Collections.nCopies(totalElements, (String) null),
            joox.find().removeAttr("id").attrs("id"));
    }

    @Test
    public void testEmpty() throws Exception {
        assertEquals(0, joox.find("directors").empty().find().size());
        assertEquals(0, joox.find("director").size());
        assertEquals(1, joox.empty().size());
        assertEquals(0, joox.find().size());
    }

    @Test
    public void testRemove() throws Exception {
        assertEquals(0, joox.find("director").remove().size());
        assertEquals(0, joox.find("director").size());
        assertEquals(3, joox.find("book").remove(JOOX.ids("1", "2")).size());
        assertEquals(3, joox.find("book").remove(JOOX.ids("1", "2")).size());
        assertEquals(0, joox.remove().size());
        assertEquals(0, joox.find().size());
        assertEquals(0, joox.size());
    }

    @Test
    public void testText() throws Exception {
        assertNull(joox.find("any").text());
        assertEquals("Sergio Leone", joox.find("director").text());
        assertEquals("Charles Bronson", joox.find("actor").text());
        assertEquals(
            Arrays.asList("Charles Bronson", "Jason Robards", "Claudia Cardinale"),
            joox.find("actor").texts());

        assertEquals(
            Collections.nCopies(3, "Lukas Eder"),
            joox.find("actor").text("Lukas Eder").texts());

        assertEquals("<abc/>", joox.find("actors").text("<abc/>").text());
        assertEquals("<><aa>", joox.find("actors").text("<><aa>").text());
    }

    @Test
    public void testContent() throws Exception {
        assertEquals("Sergio Leone", joox.find("director").content());
        assertEquals(Arrays.asList(
            "Charles Bronson",
            "Jason Robards",
            "Claudia Cardinale"),
            joox.find("actor").contents());

        assertEquals("<><aa>", joox.find("actors").content("<><aa>").text());
        assertEquals("<><aa>", joox.find("actors").content());
        assertEquals("<abc><x></abc>", joox.find("actors").content("<abc><x></abc>").text());
        assertEquals("<abc><x></abc>", joox.find("actors").content());
        assertEquals("", joox.find("actors").content("<abc><x/></abc>").text());
        assertEquals("<abc><x/></abc>", joox.find("actors").content());
        assertEquals(1, joox.find("abc").size());
        assertEquals(1, joox.find("x").size());
        assertEquals(8, joox.find("book").content("<book-content/>").size());
        assertEquals(8, joox.find("book-content").size());
        assertEquals(
            Collections.nCopies(8, "book"),
            joox.find("book-content").parent().tags());

        assertEquals("<xx/><xx/>", joox.find("actors").content("<xx/><xx/>").content());
        assertEquals(2, joox.find("xx").size());
    }

    @Test
    public void testAppend() throws Exception {
        assertEquals(1, joox.find("dvds").append("<dvd id=\"6\"/>").size());
        assertEquals(2, joox.find("dvd").size());
        assertEquals(2, joox.find("dvds").children().size());
        assertEquals(
            Arrays.asList("5", "6"),
            joox.find("dvd").ids());

        assertEquals(1, joox.find("dvds").append("<dvd id=\"7\"/><dvd id=\"8\"/>").size());
        assertEquals(4, joox.find("dvd").size());
        assertEquals(4, joox.find("dvds").children().size());
        assertEquals(
            Arrays.asList("5", "6", "7", "8"),
            joox.find("dvd").ids());

        assertEquals(1, joox.find("director").append("<><aa>").size());
        assertEquals(0, joox.find("director").children().size());
        assertEquals("Sergio Leone<><aa>", joox.find("director").text());
        assertEquals("Sergio Leone<><aa>", joox.find("director").content());
    }

    @Test
    public void testPrepend() throws Exception {
        assertEquals(1, joox.find("dvds").prepend("<dvd id=\"6\"/>").size());
        assertEquals(2, joox.find("dvd").size());
        assertEquals(2, joox.find("dvds").children().size());
        assertEquals(
            Arrays.asList("6", "5"),
            joox.find("dvd").ids());

        assertEquals(1, joox.find("dvds").prepend("<dvd id=\"7\"/><dvd id=\"8\"/>").size());
        assertEquals(4, joox.find("dvd").size());
        assertEquals(4, joox.find("dvds").children().size());
        assertEquals(
            Arrays.asList("7", "8", "6", "5"),
            joox.find("dvd").ids());

        assertEquals(1, joox.find("director").prepend("<><aa>").size());
        assertEquals(0, joox.find("director").children().size());
        assertEquals("<><aa>Sergio Leone", joox.find("director").text());
        assertEquals("<><aa>Sergio Leone", joox.find("director").content());
    }

    @Test
    public void testReplaceWith() throws Exception {
        assertEquals(
            "best-director-in-the-world",
            joox.find("director").replaceWith("<best-director-in-the-world>Jean Claude van Damme</best-director-in-the-world>").tag());
        assertEquals(0, joox.find("director").size());
        assertEquals(1, joox.find("best-director-in-the-world").size());
        assertEquals("directors", joox.find("best-director-in-the-world").parent().tag());

        assertEquals(0, joox.find("best-director-in-the-world").replaceWith("<><aa>").size());
        assertEquals("<><aa>", joox.find("directors").text().trim());
        assertEquals("<><aa>", joox.find("directors").content().trim());
    }

    // @Test
    public void testUnwrap() throws Exception {
        assertEquals(1, joox.find("director").unwrap().size());
        assertEquals(0, joox.find("directors").size());
        assertEquals("dvd", joox.find("director").parent().tag());
        assertEquals(1, joox.find("dvd").children(JOOX.tag("director")).size());
        assertEquals("Sergio Leone", joox.find("director").text());
    }
}
