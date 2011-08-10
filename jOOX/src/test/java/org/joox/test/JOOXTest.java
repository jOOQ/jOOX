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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.joox.impl.XML.joox;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtil;
import org.joox.Each;
import org.joox.Filter;
import org.joox.Mapper;
import org.joox.X;
import org.joox.impl.XML;
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
    private X joox;

    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        xmlString = IOUtil.toString(JOOXTest.class.getResourceAsStream("/example.xml"));
        xmlDocument = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));
        xmlElement = xmlDocument.getDocumentElement();
        joox = joox(xmlDocument);
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

        X x = joox().add(
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
        assertEquals(0, joox.children(XML.none()).size());
        assertEquals(1, joox.children().children(XML.tag("dvds")).size());
        assertEquals(1, joox.children().children(XML.tag("dvds")).children(XML.tag("dvd")).size());
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
        assertEquals(3, joox.find().filter("actor").filter(XML.all()).size());
        assertEquals(2, joox.find().filter("actor").filter(XML.even()).size());
        assertEquals(1, joox.find().filter("actor").filter(XML.odd()).size());
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
        int total = ((Number) XPathFactory
            .newInstance()
            .newXPath()
            .evaluate("count(//*)", xmlDocument, XPathConstants.NUMBER))
            .intValue() - 1;

        assertEquals(0, joox.find(XML.none()).size());
        assertEquals(total, joox.find().size());
        assertEquals(total, joox.find(XML.all()).size());
        assertEquals((total + 1) / 2, joox.find(XML.even()).size());
        assertEquals(total / 2, joox.find(XML.odd()).size());
        assertEquals(3, joox.find(XML.tag("library")).size());
        assertEquals(8, joox.find(XML.tag("book")).size());
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
        assertTrue(joox.is(XML.even()));
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
            joox.find("book").map(XML.ids()));

        assertEquals(
            Arrays.asList("Amazon", "Rösslitor", "Orell Füssli"),
            joox.find("library").map(XML.attributes("name")));

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
        assertEquals(1, joox.find("book").eq(0).next(XML.all()).size());
        assertEquals(0, joox.find("book").eq(0).next(XML.none()).size());
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
    public void testParent() throws Exception {
        assertEquals(0, joox.parent().size());
        assertEquals(3, joox.find("book").parent().size());
        assertEquals(Arrays.asList("books", "books", "books"), joox.find("book").parent().tags());
    }

    @Test
    public void testPrev() throws Exception {
        assertEquals(0, joox.prev().size());
        assertEquals(5, joox.find("book").prev().size());
        assertEquals(2, joox.find("book").prev().prev().size());
        assertEquals(1, joox.find("book").prev().prev().prev().size());
        assertEquals(0, joox.find("book").prev().prev().prev().prev().size());

        assertEquals(1, joox.find("book").eq(7).prev().size());
        assertEquals(1, joox.find("book").eq(7).prev(XML.all()).size());
        assertEquals(0, joox.find("book").eq(7).prev(XML.none()).size());
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
    public void testAndOrNot() throws Exception {
        assertEquals(1, joox.filter(XML.and(XML.all(), XML.all())).size());
        assertEquals(0, joox.filter(XML.and(XML.all(), XML.none())).size());
        assertEquals(0, joox.filter(XML.and(XML.none(), XML.all())).size());
        assertEquals(0, joox.filter(XML.and(XML.none(), XML.none())).size());

        assertEquals(1, joox.filter(XML.or(XML.all(), XML.all())).size());
        assertEquals(1, joox.filter(XML.or(XML.all(), XML.none())).size());
        assertEquals(1, joox.filter(XML.or(XML.none(), XML.all())).size());
        assertEquals(0, joox.filter(XML.or(XML.none(), XML.none())).size());

        assertEquals(0, joox.filter(XML.not(XML.all())).size());
        assertEquals(1, joox.filter(XML.not(XML.none())).size());
    }
}
