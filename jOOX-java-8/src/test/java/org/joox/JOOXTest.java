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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.nCopies;
import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;
import static org.joox.JOOX.chain;
import static org.joox.JOOX.paths;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.joox.selector.CSS2XPath;
import org.junit.Assume;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Lukas Eder
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JOOXTest {

    private String   xmlExampleString;
    private Document xmlExampleDocument;
    private Element  xmlExampleElement;

    private String   xmlDatesString;
    private Document xmlDatesDocument;
    @SuppressWarnings("unused")
    private Element  xmlDatesElement;

    private String   xmlNamespacesString;
    private Document xmlNamespacesDocument;
    @SuppressWarnings("unused")
    private Element  xmlNamespacesElement;

    private int      totalElements;
    private Match    $;
    private XPath    xPath;

    @Before
    public void setUp() throws Exception {
        DocumentBuilder builder = JOOX.builder();

        xmlExampleString = IOUtils.toString(JOOXTest.class.getResourceAsStream("/example.xml"));
        xmlExampleDocument = builder.parse(new ByteArrayInputStream(xmlExampleString.getBytes()));
        xmlExampleElement = xmlExampleDocument.getDocumentElement();

        xmlDatesString = IOUtils.toString(JOOXTest.class.getResourceAsStream("/dates.xml"));
        xmlDatesDocument = builder.parse(new ByteArrayInputStream(xmlDatesString.getBytes()));
        xmlDatesElement = xmlDatesDocument.getDocumentElement();

        xmlNamespacesString = IOUtils.toString(JOOXTest.class.getResourceAsStream("/namespaces.xml"));
        xmlNamespacesDocument = builder.parse(new ByteArrayInputStream(xmlNamespacesString.getBytes()));
        xmlNamespacesElement = xmlNamespacesDocument.getDocumentElement();

        $ = $(xmlExampleDocument);
        xPath = XPathFactory.newInstance().newXPath();
        totalElements = ((Number) xPath
            .evaluate("count(//*)", xmlExampleDocument, XPathConstants.NUMBER))
            .intValue() - 1;

    }

    // -------------------------------------------------------------------------
    // DOM access
    // -------------------------------------------------------------------------

    @Test
    public void testSize() {
        assertEquals(1, $.size());
        assertEquals(3, $.children().size());
    }

    // -------------------------------------------------------------------------
    // Traversing
    // -------------------------------------------------------------------------

    @Test
    public void testAdd() {
        assertEquals(1, $(xmlExampleElement).size());
        assertEquals(1, $(xmlExampleElement).add(xmlExampleElement).size());
        assertEquals(1, $(xmlExampleElement).add(xmlExampleElement, xmlExampleElement).size());

        Match x = $(xmlExampleElement).add(
            (Element) xmlExampleElement.getElementsByTagName("director").item(0),
            (Element) xmlExampleElement.getElementsByTagName("actor").item(0));
        assertEquals(3, x.size());
        assertEquals("document", x.get(0).getTagName());
        assertEquals("director", x.get(1).getTagName());
        assertEquals("actor", x.get(2).getTagName());

        x = x.add($(xmlExampleElement).find("dvds"));
        assertEquals(4, x.size());
        assertEquals("document", x.get(0).getTagName());
        assertEquals("director", x.get(1).getTagName());
        assertEquals("actor", x.get(2).getTagName());
        assertEquals("dvds", x.get(3).getTagName());

        x = x.add(x.filter("dvds").find());
        assertEquals(10, x.size());
        assertEquals(
            Arrays.asList("document", "dvds", "dvd", "name", "directors", "director", "actors", "actor", "actor", "actor"),
            x.tags());
    }

    @Test
    public void testReverse() {
        assertEquals(
            Arrays.asList("2", "1", "3", "1", "4", "3", "2", "1"),
            $.find("book").reverse().ids());

        assertEquals(
            Arrays.asList("document", "library", "books", "book", "authors"),
            $.find("author").eq(0).parents().reverse().tags());
    }

    @Test
    public void testChildren() {
        assertEquals("library", $.child().tag());
        assertEquals("library", $.child("*").tag());
        assertEquals(3, $.children().size());
        assertEquals(3, $.children("*").size());

        assertEquals("library", $.children().get().get(0).getTagName());
        assertEquals("library", $.children().get().get(1).getTagName());
        assertEquals("library", $.children().get().get(2).getTagName());
        assertEquals(2, $($.children().get().get(0)).children().size());
        assertEquals("books", $($.children().get().get(0)).children().get().get(0).getTagName());
        assertEquals(5, $($.children().get().get(0)).children().children().size());
        assertEquals("book", $($.children().get().get(0)).children().children().get().get(0).getTagName());
        assertEquals("1", $($.children().get().get(0)).children().children().get().get(0).getAttribute("id"));
        assertEquals("2", $($.children().get().get(0)).children().children().get().get(1).getAttribute("id"));
        assertEquals("3", $($.children().get().get(0)).children().children().get().get(2).getAttribute("id"));
        assertEquals("4", $($.children().get().get(0)).children().children().get().get(3).getAttribute("id"));

        assertEquals(4, $.children().children().size());
        assertEquals("books", $.children().children().get().get(0).getTagName());
        assertEquals("dvds", $.children().children().get().get(1).getTagName());
        assertEquals("books", $.children().children().get().get(2).getTagName());
        assertEquals("books", $.children().children().get().get(3).getTagName());

        assertEquals(1, $.child().size());
        assertEquals(4, $.child().child().children().size());
        assertEquals(4, $.child(0).child().children().size());
        assertEquals(2, $.child(1).child().children().size());
        assertEquals(2, $.child(2).child().children().size());

        assertEquals(1, $.child("library").size());
        assertEquals("Amazon", $.child("library").attr("name"));
        assertEquals(1, $.child("library").child("dvds").size());
        assertEquals(1, $.child("library").child("dvds").child("dvd").size());
        assertEquals("5", $.child("library").child("dvds").child("dvd").id());
        assertEquals("Sergio Leone", $.child("library").child("dvds").child("dvd").child("directors").child("director").text());

        assertEquals(4, $.find("books").children(1, 2).size());
        assertEquals("Animal Farm", $.find("books").children(1, 2).children("name").text(0));
        assertEquals("O Alquimista", $.find("books").children(1, 2).children("name").text(1));
        assertEquals("O Alquimista", $.find("books").children(1, 2).children("name").text(2));
        assertEquals("Animal Farm", $.find("books").children(1, 2).children("name").text(3));
    }

    @Test
    public void testChildrenSelector() {
        assertEquals(0, $.children("document").size());
        assertEquals(0, $.children("asdf").size());
        assertEquals(3, $.children("library").size());
    }

    @Test
    public void testChildrenFilter() {
        assertEquals(0, $.children(JOOX.none()).size());
        assertEquals(1, $.children().children(JOOX.tag("dvds")).size());
        assertEquals(1, $.children().children(JOOX.tag("dvds")).children(JOOX.tag("dvd")).size());
    }

    @Test
    public void testAndSelf() {
        assertEquals(1, $.andSelf().size());
        assertEquals("document", $.andSelf().tag());
        assertEquals(9, $.find("book").andSelf().size());
        assertEquals(
            Arrays.asList("book", "book", "book", "book", "book", "book", "book", "book", "document"),
            $.find("book").andSelf().tags());
        assertEquals(9, $.find("book").andSelf().andSelf().size());
        assertEquals(1, $.find("any").andSelf().size());
        assertEquals(1, $.find("any").andSelf().andSelf().size());
        assertEquals("document", $.find("any").andSelf().tag());
        assertEquals(0, $.find("any").find("any-other").andSelf().size());
        assertEquals(0, $.find("any").find("any-other").andSelf().andSelf().size());


        assertEquals(2, $.child().andSelf().size());
        assertEquals(2, $.child().andSelf().andSelf().size());
        assertEquals(
            Arrays.asList("library", "document"),
            $.child().andSelf().tags());


        assertEquals(2, $.child().andSelf().child().size());
        assertEquals(
            Arrays.asList("books", "library"),
            $.child().andSelf().child().tags());
        assertEquals(3, $.child().andSelf().child().andSelf().size());
        assertEquals(
            Arrays.asList("books", "library", "document"),
            $.child().andSelf().child().andSelf().tags());


        assertEquals(4, $.children().andSelf().size());
        assertEquals(
            Arrays.asList("library", "library", "library", "document"),
            $.children().andSelf().tags());


        assertEquals(2, $.find("book").eq(1).next().andSelf().size());
        assertEquals(
            Arrays.asList("book", "book"),
            $.find("book").eq(1).next().andSelf().tags());
        assertEquals(
            Arrays.asList("3", "2"),
            $.find("book").eq(1).next().andSelf().ids());

        assertEquals(3, $.find("book").eq(1).nextAll().andSelf().size());
        assertEquals(
            Arrays.asList("book", "book", "book"),
            $.find("book").eq(1).nextAll().andSelf().tags());
        assertEquals(
            Arrays.asList("3", "4", "2"),
            $.find("book").eq(1).nextAll().andSelf().ids());


        assertEquals(2, $.find("book").eq(2).prev().andSelf().size());
        assertEquals(
            Arrays.asList("book", "book"),
            $.find("book").eq(2).prev().andSelf().tags());
        assertEquals(
            Arrays.asList("2", "3"),
            $.find("book").eq(2).prev().andSelf().ids());

        assertEquals(3, $.find("book").eq(2).prevAll().andSelf().size());
        assertEquals(
            Arrays.asList("book", "book", "book"),
            $.find("book").eq(2).prevAll().andSelf().tags());
        assertEquals(
            Arrays.asList("1", "2", "3"),
            $.find("book").eq(2).prevAll().andSelf().ids());


        // TODO: Test also siblings and parents
    }

    @Test
    public void testEachLoop() {
        assertEquals(1, $.each().size());
        assertEquals("document", $.each().get(0).tag());

        List<Match> each = $.find("book").each();
        assertEquals(8, each.size());
        for (int i = 0; i < each.size(); i++) {
            assertEquals($.find("book").eq(i), each.get(i));
        }
    }

    @Test
    public void testEachCallback() {
        final Queue<Integer> queue = new LinkedList<Integer>();

        queue.addAll(Arrays.asList(0));
        $.each(new Each() {
            @Override
            public void each(Context context) {
                assertEquals(context.element(), context.match());
                assertEquals(context.elementIndex(), context.matchIndex());
                assertEquals(context.elementSize(), context.matchSize());

                assertEquals((int) queue.poll(), context.matchIndex());
                assertEquals(1, context.matchSize());
                assertEquals("document", context.element().getTagName());
            }
        });

        assertTrue(queue.isEmpty());
        queue.addAll(Arrays.asList(0, 1, 2));

        $.children().each(new Each() {
            @Override
            public void each(Context context) {
                assertEquals(context.element(), context.match());
                assertEquals(context.elementIndex(), context.matchIndex());
                assertEquals(context.elementSize(), context.matchSize());

                assertEquals((int) queue.poll(), context.matchIndex());
                assertEquals(3, context.matchSize());
                assertEquals("library", context.element().getTagName());
            }
        });

        assertTrue(queue.isEmpty());
    }

    @Test
    public void testEq() {
        assertEquals("authors", $.find().eq(4).tag(0));
        assertEquals("author", $.find().eq(5).tag(0));
        assertEquals(Arrays.asList("authors", "author"), $.find().eq(4, 5).tags());
        assertEquals("George Orwell", $.find().eq(5).text());

        assertEquals("author", $.find().eq(-1).tag());
        assertEquals("George Orwell", $.find().eq(-1).text());
        assertEquals("library", $.find().eq(-10).tag());
        assertEquals("Orell Fuessli", $.find().eq(-10).attr("name"));
    }

    @Test
    public void testFilter() {
        assertEquals(0, $.filter("asdf").size());
        assertEquals(1, $.filter("document").size());
        assertEquals(3, $.find().filter("actor").size());
        assertEquals(3, $.find().filter("actor").filter(JOOX.all()).size());
        assertEquals(2, $.find().filter("actor").filter(JOOX.even()).size());
        assertEquals(1, $.find().filter("actor").filter(JOOX.odd()).size());
    }

    @Test
    public void testFind() {
        assertEquals(0, $.find("document").size());
        assertEquals(0, $.find("asdf").size());
        assertEquals(0, $.find("document").find("document").size());
        assertEquals(0, $.find("document").find("libary").size());
        assertEquals(3, $.find("library").size());
        assertEquals(8, $.find("book").size());
        assertEquals(8, $.find("book").get().size());
        assertEquals("book", $.find("book").get().get(2).getTagName());
        assertEquals("book", $.find("book").get(2).getTagName());
        assertEquals("4", $.find("book").get().get(3).getAttribute("id"));
        assertEquals("4", $.find("book").get(3).getAttribute("id"));
    }

    @Test
    public void testFindCSS() {

        // Combinator tests
        // ----------------

        // Descendants of root
        assertEquals(8, $.find("document book").size());
        assertEquals(
            $.find("book"),
            $.find("document book"));
        assertEquals(8, $.find("library book").size());
        assertEquals(
            $.find("book"),
            $.find("library book"));
        assertEquals(8, $.find("books book").size());
        assertEquals(
            $.find("book"),
            $.find("books book"));
        assertEquals(0, $.find("book book").size());
        assertEquals(0, $.find("dvd book").size());
        assertEquals(0, $.find("author book").size());

        // Descendants of the first library
        assertEquals(4, $.find("library").eq(0).find("books book").size());
        assertEquals(
            $.find("library").eq(0).find("book"),
            $.find("library").eq(0).find("books book"));
        assertEquals(4, $.find("library").eq(0).find("books author").size());
        assertEquals(
            $.find("library").eq(0).find("author"),
            $.find("library").eq(0).find("books author"));
        assertEquals(
            $.find("library").eq(0).find("author").texts(),
            $.find("library").eq(0).find("books author").texts());

        assertEquals(0, $.find("library").eq(0).find("book book").size());
        assertEquals(0, $.find("library").eq(0).find("author book").size());

        // Children of root
        assertEquals(3, $.find("document > library").size());
        assertEquals(
            $.find("library"),
            $.find("document > library"));

        assertEquals(8, $.find("books > book").size());
        assertEquals(
            $.find("book"),
            $.find("books > book"));

        assertEquals(0, $.find("document > book").size());
        assertEquals(0, $.find("library>book").size());

        // Children of the first library
        assertEquals(4, $.find("library").eq(0).find("books > book").size());
        assertEquals(
            $.find("library").eq(0).find("book"),
            $.find("library").eq(0).find("books > book"));
        assertEquals(4, $.find("library").eq(0).find("books > * > * > author").size());
        assertEquals(
            $.find("library").eq(0).find("author"),
            $.find("library").eq(0).find("books > * > * > author"));

        // TODO: TEST + and ~ combinators!

        // Multiple selectors tests
        // ------------------------
        assertEquals(4, $.find("document, library").size());
        assertEquals(
            $.add($.find("library")),
            $.find("document, library"));

        assertEquals(9, $.find("library book, library dvd").size());
        assertEquals(
            $.xpath("//book | //dvd"),
            $.find("library book, library dvd"));

        assertEquals(9, $.find("library > * > book, library > dvds > dvd").size());
        assertEquals(
            $.xpath("//book | //dvd"),
            $.find("library book, library dvd"));

        assertEquals(8, $.find("books").eq(0).find("name, author").size());
        assertEquals(
            $.find("books").eq(0).xpath(".//name | .//author"),
            $.find("books").eq(0).find("name, author"));

        // ID selector tests
        // -----------------
        assertEquals(3, $.find("#1").size());
        assertEquals(
            $.xpath("//book[@id = 1]"),
            $.find("#1"));
        assertEquals(3, $.find("book#1").size());
        assertEquals(
            $.xpath("//book[@id = 1]"),
            $.find("book#1"));

        assertEquals(5, $.find("#1, #2").size());
        assertEquals(
            $.xpath("//book[@id = 1 or @id = 2]"),
            $.find("#1, #2"));
        assertEquals(8, $.find("#1, #2, #3, #4").size());
        assertEquals(
            $.xpath("//book"),
            $.find("#1, #2, #3, #4"));

        // Attribute selector tests
        // ------------------------
        assertEquals(3, $.find("*[name]").size());
        assertEquals(
            $.find("library"),
            $.find("*[name]"));
        assertEquals(3, $.find("library[name]").size());
        assertEquals(
            $.find("library"),
            $.find("library[name]"));
        assertEquals(0, $.find("*[test]").size());

        assertEquals(1, $.find("library[name=Amazon]").size());
        assertEquals(
            $.find("library").eq(0),
            $.find("library[name=Amazon]"));
        assertEquals(1, $.find("library[name='Amazon']").size());
        assertEquals(
            $.find("library").eq(0),
            $.find("library[name='Amazon']"));
        assertEquals(1, $.find("library[name=\"Amazon\"]").size());
        assertEquals(
            $.find("library").eq(0),
            $.find("library[name=\"Amazon\"]"));

        assertEquals(1, $.find("library[name^=Ama]").size());
        assertEquals(
            $.find("library").eq(0),
            $.find("library[name^=Ama]"));
        assertEquals(1, $.find("library[name$=zon]").size());
        assertEquals(
            $.find("library").eq(0),
            $.find("library[name$=zon]"));
        assertEquals(1, $.find("library[name*=mazo]").size());
        assertEquals(
            $.find("library").eq(0),
            $.find("library[name*=mazo]"));

        assertEquals(1, $("<document attr=\"prefix\"/>").find("*[attr|=prefix]").size());
        assertEquals(
            $("<document attr=\"prefix\"/>").toString(),
            $("<document attr=\"prefix\"/>").find("*[attr|=prefix]").toString());
        assertEquals(1, $("<document attr=\"prefix-value\"/>").find("*[attr|=prefix]").size());
        assertEquals(
            $("<document attr=\"prefix-value\"/>").toString(),
            $("<document attr=\"prefix-value\"/>").find("*[attr|=prefix]").toString());

        assertEquals(1, $.find("*[name~=Orell]").size());
        assertEquals(
            $.find("library").eq(2),
            $.find("*[name~=Orell]"));
        assertEquals(1, $.find("*[name~=Fuessli]").size());
        assertEquals(
            $.find("library").eq(2),
            $.find("*[name~=Fuessli]"));

        // :root pseudo selector tests
        // ---------------------------
        assertEquals(1, $.find(":root").size());
        assertEquals($, $.find(":root"));
        assertEquals(1, $.find("document:root").size());
        assertEquals($, $.find("document:root"));
        assertEquals(0, $.find("abc:root").size());
        assertEquals(0, $.find("book:root").size());
        assertEquals(3, $.find(":root library").size());
        assertEquals(
            $.find("library"),
            $.find(":root library"));
        assertEquals(3, $.find(":root > library").size());
        assertEquals(
            $.find("library"),
            $.find(":root library"));

        // :empty pseudo selector tests
        // ----------------------------
        assertEquals(0, $.find(":empty").size());
        assertEquals(1, $("<document><a/></document>").find(":empty").size());
        assertEquals(
            "<a/>",
            $("<document><a/></document>").find(":empty").toString());

        assertEquals(2, $("<document><a/><b/></document>").find(":empty").size());
        assertEquals(
            asList("a", "b"),
            $("<document><a/><b/></document>").find(":empty").tags());

        assertEquals(2, $("<document><a/><b/><c> </c></document>").find(":empty").size());
        assertEquals(
            asList("a", "b"),
            $("<document><a/><b/><c> </c></document>").find(":empty").tags());

        assertEquals(2, $("<document><a/><b/><c test=\"test\"/></document>").find(":empty").size());
        assertEquals(
            asList("a", "b"),
            $("<document><a/><b/><c test=\"test\"/></document>").find(":empty").tags());

        // :first-child pseudo selector tests
        // ----------------------------------
        assertEquals(28, $.find(":first-child").size());
        assertEquals(2, $.xpath("//library[1]//book[@id=1]").find(":first-child").size());
        assertEquals(
            $.xpath("//library[1]//book[@id=1]//name | //library[1]//book[@id=1]//author"),
            $.xpath("//library[1]//book[@id=1]").find(":first-child"));
        assertEquals(3, $.find("book:first-child").size());
        assertEquals(
            $.xpath("//book[@id=1]"),
            $.find("book:first-child"));

        // :last-child pseudo selector tests
        // ---------------------------------
        assertEquals(28, $.find(":last-child").size());
        assertEquals(2, $.xpath("//library[1]//book[@id=1]").find(":last-child").size());
        assertEquals(
            $.xpath("//library[1]//book[@id=1]//authors | //library[1]//book[@id=1]//author"),
            $.xpath("//library[1]//book[@id=1]").find(":last-child"));
        assertEquals(3, $.find("book:last-child").size());
        assertEquals(
            asList("4", "3", "2"),
            $.find("book:last-child").ids());

        // :only-child pseudo selector tests
        // ---------------------------------
        assertEquals(13, $.find(":only-child").size());
        assertEquals(0, $.find("dvds:only-child").size());
        assertEquals(0, $.find("library").find("dvds:only-child").size());
        assertEquals(1, $.find("dvd:only-child").size());
        assertEquals(1, $.find("library").find("dvd:only-child").size());
        assertEquals(
            $.find("dvd"),
            $.find("library").find("dvd:only-child"));
    }

    @Test
    public void testFindCSSNthChild() {
        assertEquals("Charles Bronson", $.find("actor:nth-child(1)").text());
        assertEquals("Jason Robards", $.find("actor:nth-child(2)").text());
        assertEquals("Claudia Cardinale", $.find("actor:nth-child(3)").text());
        assertNull($.find("actor:nth-child(4)").text());

        assertEquals("1", $.find("library[name='Amazon']").find("book:nth-child(1)").id());
        assertEquals("2", $.find("library[name='Amazon']").find("book:nth-child(2)").id());
        assertEquals("3", $.find("library[name='Amazon']").find("book:nth-child(3)").id());
        assertEquals("4", $.find("library[name='Amazon']").find("book:nth-child(4)").id());
        assertNull($.find("library[name='Amazon']").find("book:nth-child(5)").id());

        assertEquals(asList("1", "1", "1"), $.find("book:nth-child(1)").ids());
        assertEquals(asList("2", "3", "2"), $.find("book:nth-child(2)").ids());
        assertEquals(asList("3"), $.find("book:nth-child(3)").ids());
        assertEquals(asList("4"), $.find("book:nth-child(4)").ids());
        assertEquals(asList(), $.find("book:nth-child(5)").ids());
    }

    @Test
    public void testFindFilter() throws Exception {
        assertEquals(0, $.find(JOOX.none()).size());
        assertEquals(totalElements, $.find().size());
        assertEquals(totalElements, $.find(JOOX.all()).size());
        assertEquals((totalElements + 1) / 2, $.find(JOOX.even()).size());
        assertEquals(totalElements / 2, $.find(JOOX.odd()).size());
        assertEquals(3, $.find(JOOX.tag("library")).size());
        assertEquals(8, $.find(JOOX.tag("book")).size());

        assertEquals($.find(), $.find(JOOX.tag("*")));
        assertEquals($.find("abcd"), $.find(JOOX.tag(null)));
        assertEquals($.find("abcd"), $.find(JOOX.tag("")));
    }

    @Test
    public void testMatchXPath() throws Exception {
        assertEquals(totalElements + 1, $.xpath("//*").size());
        assertEquals(totalElements, $.xpath(".//*").size());
        assertEquals(8, $.xpath("//book").size());
        assertEquals(0, $.xpath("//book/@id").size());
        assertEquals(8, $.xpath("//book/name").size());
        assertEquals(0, $.xpath("//book/name/text()").size());
        assertEquals(
            Arrays.asList("1", "2", "3", "4", "1", "3", "1", "2"),
            $.xpath("//book").ids());
        assertEquals(
            Arrays.asList("O Alquimista", "Brida"),
            $.xpath("//book[../../@name = 'Amazon'][@id = 3 or @id = 4]/name").texts());
        assertEquals(
            Arrays.asList("O Alquimista", "Brida"),
            $.find("book").xpath("self::node()[../../@name = 'Amazon'][@id = 3 or @id = 4]/name").texts());
    }

    @Test
    public void testConvertNumeric() throws Exception {
        assertEquals(
            Arrays.asList(1, 2, 3, 4, 1, 3, 1, 2),
            $.find("book").attrs("id", Integer.class));
        assertEquals(
            Arrays.asList(1, 2, 3, 4, 1, 3, 1, 2),
            $.find("book").ids(Integer.class));

        assertEquals(1, (int) $.find("book").eq(0).id(Integer.class));
        assertEquals(2L, (long) $.find("book").eq(1).id(Long.class));
        assertEquals((short) 3, (short) $.find("book").eq(2).id(Short.class));
        assertEquals((byte) 4, (byte) $.find("book").eq(3).id(Byte.class));

        assertEquals(1, (int) $.find("book").eq(0).id(int.class));
        assertEquals(2L, (long) $.find("book").eq(1).id(long.class));
        assertEquals((short) 3, (short) $.find("book").eq(2).id(short.class));
        assertEquals((byte) 4, (byte) $.find("book").eq(3).id(byte.class));

        assertEquals(1984, (int) $.find("name").text(Integer.class));
        assertEquals(1984, (long) $.find("name").text(Long.class));
        assertEquals((short) 1984, (short) $.find("name").text(Short.class));
        assertEquals((byte) 1984, (byte) $.find("name").text(Byte.class));
        assertEquals(1984.0f, $.find("name").text(Float.class), 0.0f);
        assertEquals(1984.0, $.find("name").text(Double.class), 0.0);
        assertEquals(new BigInteger("1984"), $.find("name").text(BigInteger.class));
        assertEquals(new BigDecimal("1984"), $.find("name").text(BigDecimal.class));

        assertEquals(1984, (int) $.find("name").text(int.class));
        assertEquals(1984, (long) $.find("name").text(long.class));
        assertEquals((short) 1984, (short) $.find("name").text(short.class));
        assertEquals((byte) 1984, (byte) $.find("name").text(byte.class));
        assertEquals(1984.0f, $.find("name").text(float.class), 0.0f);
        assertEquals(1984.0, $.find("name").text(double.class), 0.0);

        assertNull($.find("name").eq(1).text(Integer.class));
        assertNull($.find("name").eq(1).text(Long.class));
        assertNull($.find("name").eq(1).text(Short.class));
        assertNull($.find("name").eq(1).text(Byte.class));
        assertNull($.find("name").eq(1).text(Float.class));
        assertNull($.find("name").eq(1).text(Double.class));
        assertNull($.find("name").eq(1).text(BigInteger.class));
        assertNull($.find("name").eq(1).text(BigDecimal.class));

        assertEquals(0, (int) $.find("name").eq(1).text(int.class));
        assertEquals(0, (long) $.find("name").eq(1).text(long.class));
        assertEquals(0, (short) $.find("name").eq(1).text(short.class));
        assertEquals(0, (byte) $.find("name").eq(1).text(byte.class));
        assertEquals(0.0f, $.find("name").eq(1).text(float.class), 0.0f);
        assertEquals(0.0, $.find("name").eq(1).text(double.class), 0.0);
        assertFalse($.find("name").eq(1).text(boolean.class));
    }

    @Test
    public void testConvertArrays() throws Exception {

        // Null / empty checks
        assertEquals(0, $.find("abc").texts(String[].class).size());
        assertNull($.find("abc").text(String[].class));
        assertEquals(String[].class, $("<root/>").text(String[].class).getClass());
        assertEquals(emptyList(), asList($("<root/>").text(String[].class)));

        // Simple checks on the <actor/> elements (first, last names)
        assertEquals(3, $.find("actor").texts(String[].class).size());
        assertEquals(String[].class, $.find("actor").text(String[].class).getClass());
        assertEquals(asList("Charles", "Bronson"), asList($.find("actor").text(String[].class)));
        assertEquals(asList("Charles", "Bronson"), asList($.find("actor").texts(String[].class).get(0)));
        assertEquals(asList("Jason", "Robards"), asList($.find("actor").texts(String[].class).get(1)));
        assertEquals(asList("Claudia", "Cardinale"), asList($.find("actor").texts(String[].class).get(2)));

        // More sophisticated checks
        Match m = $("root",
            $("child", "a b, c,,d"),
            $("child", ";a;b;c;d"),
            $("child", "\"a b\" \"c d\"\" e\";f\" g;\"h\""),
            $("child", "1,2,3 4;5;6 \"7.8\" \"9.0\";11")
        ).attr("class", "a b c,d;e \"f g\"");

        assertEquals(4, m.find("child").texts(String[].class).size());
        assertEquals(
            asList("a", "b", "c", "", "d"),
            asList(m.find("child").eq(0).text(String[].class)));
        assertEquals(
            asList("", "a", "b", "c", "d"),
            asList(m.find("child").eq(1).text(String[].class)));
        assertEquals(
            asList("a b", "c d\" e", "f\"", "g", "h"),
            asList(m.find("child").eq(2).text(String[].class)));
        assertEquals(
            asList("1", "2", "3", "4", "5", "6", "7.8", "9.0", "11"),
            asList(m.find("child").eq(3).text(String[].class)));
        assertEquals(
            asList(1, 2, 3, 4, 5, 6, 7, 9, 11),
            asList(m.find("child").eq(3).text(Integer[].class)));
        assertEquals(
            asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 9L, 11L),
            asList(m.find("child").eq(3).text(Long[].class)));
        assertEquals(
            asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.8, 9.0, 11.0),
            asList(m.find("child").eq(3).text(Double[].class)));
        assertEquals(
            asList("a", "b", "c", "d", "e", "f g"),
            asList(m.attr("class", String[].class)));
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals($, $);
        assertEquals($, $.add(new Element[0]));
        assertEquals($.find(), $.find());
        assertEquals($.find("book"), $.find("book"));
        assertEquals($.find().filter("book").eq(0, 2, 4),
            $.xpath("/document/library[1]//book[1] | " +
                    "/document/library[1]//book[3] | " +
                    "/document/library[2]//book[1]"));
    }

    @Test
    public void testFirst() throws Exception {
        assertEquals(0, $.find("document").first().size());
        assertEquals(1, $.first().size());
        assertEquals("document", $.first().tag());
        assertEquals("books", $.children().first().children().first().tag());
    }

    @Test
    public void testHas() throws Exception {
        assertEquals(0, $.has("asdf").size());
        assertEquals(0, $.has("document").size());
        assertEquals(1, $.has("library").size());
        assertEquals(1, $.has("authors").size());
        assertEquals(3, $.children().has("authors").size());
        assertEquals(1, $.children().has("dvds").size());
    }

    @Test
    public void testIs() throws Exception {
        assertFalse($.is("abc"));
        assertTrue($.is("document"));
        assertTrue($.is(JOOX.even()));
    }

    @Test
    public void testLast() throws Exception {
        assertEquals(0, $.find("document").last().size());
        assertEquals(1, $.last().size());
        assertEquals("document", $.last().tag());
        assertEquals("dvds", $.children().eq(0).children().last().tag());
    }

    @Test
    public void testMap() throws Exception {
        assertEquals(
            Arrays.asList("1", "2", "3", "4", "1", "3", "1", "2"),
            $.find("book").map(JOOX.ids()));

        assertEquals(
            Arrays.asList("Amazon", "Roesslitor", "Orell Fuessli"),
            $.find("library").map(JOOX.attrs("name")));

        assertEquals(Arrays.asList(0, 1, 2, 3), $.children().first().find("book").map(new Mapper<Integer>() {
            @Override
            public Integer map(Context context) {
                assertEquals(context.element(), context.match());
                assertEquals(context.elementIndex(), context.matchIndex());
                assertEquals(context.elementSize(), context.matchSize());

                assertEquals(4, context.matchSize());

                return context.matchIndex();
            }
        }));
    }

    @Test
    public void testNext() throws Exception {
        assertEquals(0, $.next().size());
        assertEquals(5, $.find("book").next().size());
        assertEquals(2, $.find("book").next().next().size());
        assertEquals(1, $.find("book").next().next().next().size());
        assertEquals(0, $.find("book").next().next().next().next().size());

        assertEquals(1, $.find("book").eq(0).next().size());
        assertEquals(1, $.find("book").eq(0).next(JOOX.all()).size());
        assertEquals(0, $.find("book").eq(0).next(JOOX.none()).size());
        assertEquals(0, $.find("book").eq(0).next(new Filter() {
            @Override
            public boolean filter(Context context) {
                assertEquals(0, context.matchIndex());
                assertEquals(1, context.matchSize());

                assertEquals(1, context.elementIndex());
                assertEquals(context.match(), $(context.element()).prev().get(0));
                assertEquals("1", $(context.match()).id());
                assertEquals("2", $(context.element()).id());

                return "Paulo Coelho".equals($(context.element()).find("author").text());
            }
        }).size());
        assertEquals(1, $.find("book").eq(1).next(new Filter() {
            @Override
            public boolean filter(Context context) {
                assertEquals(0, context.matchIndex());
                assertEquals(1, context.matchSize());

                assertEquals(1, context.elementIndex());
                assertEquals(context.match(), $(context.element()).prev().get(0));
                assertEquals("2", $(context.match()).id());
                assertEquals("3", $(context.element()).id());

                return "Paulo Coelho".equals($(context.element()).find("author").text());
            }
        }).size());
    }

    @Test
    public void testNextAll() throws Exception {
        assertEquals(0, $.nextAll().size());
        assertEquals(5, $.find("book").nextAll().size());
        assertEquals(2, $.find("book").nextAll().nextAll().size());
        assertEquals(1, $.find("book").nextAll().nextAll().nextAll().size());
        assertEquals(0, $.find("book").nextAll().nextAll().nextAll().nextAll().size());

        assertEquals(3, $.find("book").eq(0).nextAll().size());
        assertEquals(2, $.find("book").eq(0).nextAll().nextAll().size());
    }

    @Test
    public void testNextUntil() throws Exception {
        assertEquals(0, $.nextUntil("asdf").size());
        assertEquals(2, $.find("dvd").children().eq(0).nextUntil("any").size());
        assertEquals(
            Arrays.asList("directors", "actors"),
            $.find("dvd").children().eq(0).nextUntil("any").tags());
        assertEquals(1,
            $.find("dvd").children().eq(0).nextUntil("actors").size());
        assertEquals("directors",
            $.find("dvd").children().eq(0).nextUntil("actors").tag());
    }

    @Test
    public void testParent() throws Exception {
        assertEquals(0, $.parent().size());
        assertEquals(3, $.find("book").parent().size());
        assertEquals(nCopies(3, "books"), $.find("book").parent().tags());
        assertEquals(nCopies(8, "book"), $.find("authors").parent().tags());
    }

    @Test
    public void testParents() throws Exception {
        assertEquals(0, $.parents().size());
        assertEquals(1, $.find("library").parents().size());
        assertEquals(4, $.find("books").parents().size());
        assertEquals(7, $.find("book").parents().size());
        assertEquals(15, $.find("authors").parents().size());
        assertEquals(23, $.find("author").parents().size());
        assertEquals(26, $.find("author").add($.find("actor")).parents().size());
    }

    @Test
    public void testParentsUntil() throws Exception {
        assertEquals(0, $.parentsUntil("books").size());
        assertEquals(1, $.find("library").parentsUntil("books").size());
        assertEquals(4, $.find("books").parentsUntil("books").size());
        assertEquals(0, $.find("book").parentsUntil("books").size());
        assertEquals(8, $.find("authors").parentsUntil("books").size());
        assertEquals(16, $.find("author").parentsUntil("books").size());
        assertEquals(21, $.find("author").add($.find("actor")).parentsUntil("books").size());
    }

    @Test
    public void testPrev() throws Exception {
        assertEquals(0, $.prev().size());
        assertEquals(5, $.find("book").prev().size());
        assertEquals(2, $.find("book").prev().prev().size());
        assertEquals(1, $.find("book").prev().prev().prev().size());
        assertEquals(0, $.find("book").prev().prev().prev().prev().size());

        assertEquals(1, $.find("book").eq(7).prev().size());
        assertEquals(1, $.find("book").eq(7).prev(JOOX.all()).size());
        assertEquals(0, $.find("book").eq(7).prev(JOOX.none()).size());
        assertEquals(0, $.find("book").eq(7).prev(new Filter() {
            @Override
            public boolean filter(Context context) {
                assertEquals(0, context.matchIndex());
                assertEquals(1, context.matchSize());

                assertEquals(1, context.elementIndex());
                assertEquals(context.match(), $(context.element()).next().get(0));
                assertEquals("2", $(context.match()).id());
                assertEquals("1", $(context.element()).id());

                return "Paulo Coelho".equals($(context.element()).find("author").text());
            }
        }).size());
        assertEquals(1, $.find("book").eq(3).prev(new Filter() {
            @Override
            public boolean filter(Context context) {
                assertEquals(0, context.matchIndex());
                assertEquals(1, context.matchSize());

                assertEquals(1, context.elementIndex());
                assertEquals(context.match(), $(context.element()).next().get(0));
                assertEquals("4", $(context.match()).id());
                assertEquals("3", $(context.element()).id());

                return "Paulo Coelho".equals($(context.element()).find("author").text());
            }
        }).size());
    }

    @Test
    public void testPrevAll() throws Exception {
        assertEquals(0, $.prevAll().size());
        assertEquals(5, $.find("book").prevAll().size());
        assertEquals(2, $.find("book").prevAll().prevAll().size());
        assertEquals(1, $.find("book").prevAll().prevAll().prevAll().size());
        assertEquals(0, $.find("book").prevAll().prevAll().prevAll().prevAll().size());

        assertEquals(3, $.find("book").eq(3).prevAll().size());
        assertEquals(2, $.find("book").eq(3).prevAll().prevAll().size());
    }

    @Test
    public void testPrevUntil() throws Exception {
        assertEquals(0, $.prevUntil("asdf").size());
        assertEquals(2, $.find("dvd").children().eq(2).prevUntil("any").size());
        assertEquals(
            Arrays.asList("name", "directors"),
            $.find("dvd").children().eq(2).prevUntil("any").tags());
        assertEquals(1,
            $.find("dvd").children().eq(2).prevUntil("name").size());
        assertEquals("directors",
            $.find("dvd").children().eq(2).prevUntil("name").tag());
    }

    @Test
    public void testSiblings() throws Exception {
        assertEquals(0, $.siblings().size());
        assertEquals(3, $.find("library").siblings().size());
        assertEquals(
            Arrays.asList("library", "library", "library"),
            $.find("library").siblings().tags());
        assertEquals(2, $.find("library").eq(0).siblings().size());
        assertEquals(2, $.find("library").eq(1).siblings().size());
        assertEquals(2, $.find("library").eq(2).siblings().size());
        assertEquals(
            Arrays.asList("library", "library"),
            $.find("library").eq(0).siblings().tags());
        assertEquals(0, $.find("library").eq(3).siblings().size());
        assertEquals(3, $.find("books").eq(0).find("book[id='3']").siblings().size());

        // This test still fails
        // assertEquals(
        //    asList(-1, 1),
        //    $.find("book[id='3']").siblings(JOOX.odd()).ids(Integer.class));
    }

    @Test
    public void testSlice() throws Exception {
        assertEquals(0, $.slice(1).size());
        assertEquals(1, $.slice(0).size());
        assertEquals(1, $.slice(-1).size());
        assertEquals(1, $.slice(-2).size());

        assertEquals(0, $.slice(1, 1).size());
        assertEquals(1, $.slice(0, 1).size());
        assertEquals(1, $.slice(-1, 1).size());
        assertEquals(1, $.slice(-2, 1).size());

        assertEquals(8, $.find("book").slice(-9).size());
        assertEquals(8, $.find("book").slice(-8).size());
        assertEquals(2, $.find("book").slice(-2).size());
        assertEquals(1, $.find("book").slice(-1).size());
        assertEquals(8, $.find("book").slice(0).size());
        assertEquals(7, $.find("book").slice(1).size());
        assertEquals(6, $.find("book").slice(2).size());
        assertEquals(5, $.find("book").slice(3).size());
        assertEquals(4, $.find("book").slice(4).size());
        assertEquals(3, $.find("book").slice(5).size());
        assertEquals(2, $.find("book").slice(6).size());
        assertEquals(1, $.find("book").slice(7).size());
        assertEquals(0, $.find("book").slice(8).size());
        assertEquals(0, $.find("book").slice(9).size());

        assertEquals(5, $.find("book").slice(-9, 5).size());
        assertEquals(5, $.find("book").slice(-8, 5).size());
        assertEquals(0, $.find("book").slice(-2, 5).size());
        assertEquals(0, $.find("book").slice(-1, 5).size());
        assertEquals(5, $.find("book").slice(0, 5).size());
        assertEquals(4, $.find("book").slice(1, 5).size());
        assertEquals(3, $.find("book").slice(2, 5).size());
        assertEquals(2, $.find("book").slice(3, 5).size());
        assertEquals(1, $.find("book").slice(4, 5).size());
        assertEquals(0, $.find("book").slice(5, 5).size());
        assertEquals(0, $.find("book").slice(6, 5).size());
        assertEquals(0, $.find("book").slice(7, 5).size());
        assertEquals(0, $.find("book").slice(8, 5).size());
        assertEquals(0, $.find("book").slice(9, 5).size());

        assertEquals(3, $.find("book").slice(-9, -5).size());
        assertEquals(3, $.find("book").slice(-8, -5).size());
        assertEquals(0, $.find("book").slice(-2, -5).size());
        assertEquals(0, $.find("book").slice(-1, -5).size());
        assertEquals(3, $.find("book").slice(0, -5).size());
        assertEquals(2, $.find("book").slice(1, -5).size());
        assertEquals(1, $.find("book").slice(2, -5).size());
        assertEquals(0, $.find("book").slice(3, -5).size());
        assertEquals(0, $.find("book").slice(4, -5).size());
        assertEquals(0, $.find("book").slice(5, -5).size());
        assertEquals(0, $.find("book").slice(6, -5).size());
        assertEquals(0, $.find("book").slice(7, -5).size());
        assertEquals(0, $.find("book").slice(8, -5).size());
        assertEquals(0, $.find("book").slice(9, -5).size());
    }

    @Test
    public void testDOMAccess() throws Exception {
        assertEquals(xmlExampleDocument, $.document());
        assertEquals(xmlExampleDocument, $.get(0).getOwnerDocument());
        assertEquals(xmlExampleElement, $.get(0));
        assertEquals(xmlExampleElement, $.get().get(0));
        assertNull($.get(1));

        assertEquals("document", $.tag());
        assertEquals("document", $.tags().get(0));
        assertEquals("document", $.tag(0));
        assertNull($.tag(1));
        assertNull($.next().tag());
        assertNull($.next().tag(0));
        assertNull($.next().tag(1));
    }

    @Test
    public void testAndOrNot() throws Exception {
        assertEquals(1, $.filter(JOOX.and(JOOX.all(), JOOX.all())).size());
        assertEquals(0, $.filter(JOOX.and(JOOX.all(), JOOX.none())).size());
        assertEquals(0, $.filter(JOOX.and(JOOX.none(), JOOX.all())).size());
        assertEquals(0, $.filter(JOOX.and(JOOX.none(), JOOX.none())).size());

        assertEquals(1, $.filter(JOOX.or(JOOX.all(), JOOX.all())).size());
        assertEquals(1, $.filter(JOOX.or(JOOX.all(), JOOX.none())).size());
        assertEquals(1, $.filter(JOOX.or(JOOX.none(), JOOX.all())).size());
        assertEquals(0, $.filter(JOOX.or(JOOX.none(), JOOX.none())).size());

        assertEquals(0, $.filter(JOOX.not(JOOX.all())).size());
        assertEquals(1, $.filter(JOOX.not(JOOX.none())).size());
    }

    @Test
    public void testAttr() throws Exception {
        assertNull($.attr("any"));
        assertNull($.attr("id"));
        assertEquals(Arrays.asList((String) null), $.attrs("any"));
        assertEquals(Arrays.asList((String) null), $.attrs("id"));
        assertEquals("1", $.find("book").attr("id"));
        assertEquals(
            Arrays.asList("1", "2", "3", "4", "1", "3", "1", "2"),
            $.find("book").attrs("id"));

        assertEquals(
            Collections.nCopies(totalElements, "y"),
            $.find().attr("x", "y").attrs("x"));
        assertEquals(
            Collections.nCopies(totalElements, (String) null),
            $.find().attr("x", (String) null).attrs("x"));

        assertEquals(
            Collections.nCopies(totalElements, (String) null),
            $.find().removeAttr("id").attrs("id"));
    }

    @Test
    public void testAttrFilter() throws Exception {
        assertEquals(35, $.find().filter(attr(null)).size());
        assertEquals(35, $.find().filter(attr("")).size());
        assertEquals(35, $.find().filter(attr(null, "1", "2")).size());
        assertEquals(35, $.find().filter(attr("", "1", "2")).size());
        assertEquals(
            $.xpath("/document//*[not(@*)]").size(),
            $.find().filter(attr(null)).size());

        assertEquals(9, $.find().filter(attr("id")).size());
        assertEquals(
            $.find("book, dvd"),
            $.find().filter(attr("id")));

        assertEquals(3, $.find().filter(attr("id", "1")).size());
        assertEquals(
            $.find("book[id='1']"),
            $.find().filter(attr("id", "1")));

        assertEquals(5, $.find().filter(attr("id", "1", "2")).size());
        assertEquals(
            $.find("book[id='1'], book[id='2']"),
            $.find().filter(attr("id", "1", "2")));
    }

    @Test
    public void testEmpty() throws Exception {
        assertEquals(0, $.find("directors").empty().find().size());
        assertTrue($.find("directors").find().isEmpty());
        assertFalse($.find("directors").find().isNotEmpty());

        assertEquals(0, $.find("director").size());
        assertTrue($.find("director").isEmpty());
        assertFalse($.find("director").isNotEmpty());

        assertEquals(1, $.empty().size());
        assertFalse($.isEmpty());
        assertTrue($.isNotEmpty());

        assertEquals(0, $.find().size());
    }

    @Test
    public void testRemove() throws Exception {
        assertEquals(0, $.find("director").remove().size());
        assertEquals(0, $.find("director").size());
        assertEquals(3, $.find("book").remove(JOOX.ids("1", "2")).size());
        assertEquals(3, $.find("book").remove(JOOX.ids("1", "2")).size());
        assertEquals(0, $.remove().size());
        assertEquals(0, $.find().size());
        assertEquals(0, $.size());
    }

    @Test
    public void testWrap() throws Exception {
        assertEquals(0, $.find("abc").wrap("parent").size());
        assertEquals(0, $.find("parent").size());

        Match wrapped = $.find("author").wrap("parent");
        assertEquals(8, wrapped.size());
        assertEquals(Collections.nCopies(8, "author"), wrapped.tags());
        assertEquals($.find("author"), wrapped);
        assertEquals(8, $.find("parent").size());
        assertEquals(8, $.find("parent").children("author").size());
        assertEquals($.find("author"), $.find("parent").children("author"));
        assertEquals($.find("parent"), $.find("author").parent());

        wrapped = $.wrap("newroot");
        assertEquals(1, wrapped.size());
        assertEquals("newroot", wrapped.parent().tag());
        assertEquals("newroot", wrapped.document().getDocumentElement().getTagName());
        assertEquals($.xpath("/newroot/document"), wrapped);
    }

    @Test
    public void testUnwrap() throws Exception {
        assertEquals(0, $.find("abc").unwrap().size());

        Match unwrapped = $.find("author").unwrap();
        assertEquals(8, unwrapped.size());
        assertEquals(Collections.nCopies(8, "author"), unwrapped.tags());
        assertEquals($.find("author"), unwrapped);
        assertEquals(0, $.find("authors").size());
        assertEquals(Collections.nCopies(8, "book"), unwrapped.parent().tags());
        assertEquals($.find("book").children("author"), unwrapped);

        try {
            $.unwrap();
            fail();
        } catch (RuntimeException expected) {}

        try {
            $.find("library").unwrap();
            fail();
        } catch (RuntimeException expected) {}
    }

    @Test
    public void testText() throws Exception {
        assertNull($.find("any").text());
        assertEquals("Sergio Leone", $.find("director").text());
        assertEquals("Charles Bronson", $.find("actor").text());
        assertEquals("Charles Bronson", $.find("actor").text(0));
        assertEquals("Jason Robards", $.find("actor").text(1));
        assertEquals("Claudia Cardinale", $.find("actor").text(2));
        assertEquals(
            Arrays.asList("Charles Bronson", "Jason Robards", "Claudia Cardinale"),
            $.find("actor").texts());

        assertEquals(
            Collections.nCopies(3, "Lukas Eder"),
            $.find("actor").text("Lukas Eder").texts());

        assertEquals("<abc/>", $.find("actors").text("<abc/>").text());
        assertEquals("<><aa>", $.find("actors").text("<><aa>").text());
    }

    @Test
    public void testCData() throws Exception {
        assertNull($.find("any").cdata());
        assertEquals("Sergio Leone", $.find("director").cdata());
        assertEquals("Charles Bronson", $.find("actor").cdata());
        assertEquals("Charles Bronson", $.find("actor").cdata(0));
        assertEquals("Jason Robards", $.find("actor").cdata(1));
        assertEquals("Claudia Cardinale", $.find("actor").cdata(2));
        assertEquals(
            Arrays.asList("Charles Bronson", "Jason Robards", "Claudia Cardinale"),
            $.find("actor").cdatas());

        assertEquals(
            Collections.nCopies(3, "Lukas Eder"),
            $.find("actor").cdata("Lukas Eder").cdatas());
        assertEquals(
            Collections.nCopies(3, "<actor><![CDATA[Lukas Eder]]></actor>"),
            $.find("actor").map(new Mapper<Object>() {
                @Override
                public Object map(Context context) {
                    return context.toString();
                }
            }));

        assertEquals("<abc/>", $.find("actors").cdata("<abc/>").cdata());
        assertEquals("<actors><![CDATA[<abc/>]]></actors>", $.find("actors").toString());

        assertEquals("<><aa>", $.find("actors").cdata("<><aa>").cdata());
        assertEquals("<actors><![CDATA[<><aa>]]></actors>", $.find("actors").toString());
    }

    @Test
    public void testContent() throws Exception {
        assertNull($.find("non-existing").content());
        assertEquals("Sergio Leone", $.find("director").content());
        assertEquals(Arrays.asList(
            "Charles Bronson",
            "Jason Robards",
            "Claudia Cardinale"),
            $.find("actor").contents());

        assertEquals("Charles Bronson", $.find("actor").content());
        assertEquals("Charles Bronson", $.find("actor").content(0));
        assertEquals("Jason Robards", $.find("actor").content(1));
        assertEquals("Claudia Cardinale", $.find("actor").content(2));

        assertEquals("<><aa>", $.find("actors").content("<><aa>").text());
        assertEquals("&lt;&gt;&lt;aa&gt;", $.find("actors").content());
        assertEquals("<abc><x></abc>", $.find("actors").content("<abc><x></abc>").text());
        assertEquals("&lt;abc&gt;&lt;x&gt;&lt;/abc&gt;", $.find("actors").content());
        assertEquals("", $.find("actors").content("<abc><x/></abc>").text());
        assertEquals("<abc><x/></abc>", $.find("actors").content());
        assertEquals(1, $.find("abc").size());
        assertEquals(1, $.find("x").size());
        assertEquals(8, $.find("book").content("<book-content/>").size());
        assertEquals(8, $.find("book-content").size());
        assertEquals(
            Collections.nCopies(8, "book"),
            $.find("book-content").parent().tags());

        assertEquals("<xx/><xx/>", $.find("actors").content("<xx/><xx/>").content());
        assertEquals(2, $.find("xx").size());

        // Null and empty string checks
        assertEquals("<document/>", $.content((String) null).toString());
        assertEquals("<document/>", $.content("").toString());
    }

    @Test
    public void testContentWithCDATA() {
        assertEquals("<![CDATA[x]]>", $("<a><![CDATA[x]]></a>").content());
        assertEquals("<![CDATA[x]]>y<![CDATA[z]]>", $("<a><![CDATA[x]]>y<![CDATA[z]]></a>").content());
        assertEquals("<b><![CDATA[x]]></b>", $("<a><b><![CDATA[x]]></b></a>").content());
        assertEquals("<b><![CDATA[x]]></b><c><![CDATA[y]]></c>", $("<a><b><![CDATA[x]]></b><c><![CDATA[y]]></c></a>").content());
    }

    @Test
    public void testContentWithNewlines() {
        // Xalan seems to replace \n by \r\n on Windows... o_O. We should treat
        // this in another bug, perhaps

        assertEquals("a\nb\nc", $("<x>a\nb\nc</x>").content().replace("\r", ""));
        assertEquals("a\nb\nc", $("<y><x>a\nb\nc</x></y>").find("x").content().replace("\r", ""));

        assertEquals("a\n<b/>\nc", $("<x>a\n<b/>\nc</x>").content().replace("\r", ""));
        assertEquals("a\n<b/>\nc", $("<y><x>a\n<b/>\nc</x></y>").find("x").content().replace("\r", ""));
    }

    @Test
    public void testContentJAXB() throws Exception {
        assertEquals("<customer id=\"0\"><age>0</age></customer>",
            $.content(new Customer()).content());
        assertEquals($(new Customer()).toString(), $.content());
        assertEquals("<customer id=\"0\"><customer id=\"0\"><age>0</age></customer></customer>",
            $.find("customer").content(new Customer()).parent().content());

        assertEquals("<document/>", $.content((Object) null).toString());
    }

    @Test
    public void testTransform() throws Exception {
        // Transform the book nodes
        assertEquals(
            asList(2, 3, 4, 5, 2, 4, 2, 3),
            $.find("book").transform(JOOXTest.class.getResourceAsStream("/book-id-increment.xsl")).ids(Integer.class));
        assertEquals(
            asList(1, 2, 3, 4, 1, 3, 1, 2),
            $.find("book").transform(JOOXTest.class.getResourceAsStream("/book-id-decrement.xsl")).ids(Integer.class));
        assertEquals($(xmlExampleString).toString(), $.toString());

        // Transform irrelevant nodes
        assertEquals(
            asList(1, 2, 3, 4, 1, 3, 1, 2),
            $.find("author").transform(new File(JOOXTest.class.getResource("/book-id-increment.xsl").toURI()))
                            .parents("document")
                            .find("book")
                            .ids(Integer.class));
        assertEquals(
            asList(1, 2, 3, 4, 1, 3, 1, 2),
            $.find("author").transform(new File(JOOXTest.class.getResource("/book-id-decrement.xsl").toURI()))
                            .parents("document")
                            .find("book")
                            .ids(Integer.class));
        assertEquals($(xmlExampleString).toString(), $.toString());

        // Transform the library nodes
        assertEquals(
            asList(2, 3, 4, 5, 2, 4, 2, 3),
            $.find("library").transform(JOOXTest.class.getResource("/book-id-increment.xsl")).find("book").ids(Integer.class));
        assertEquals(
            asList(1, 2, 3, 4, 1, 3, 1, 2),
            $.find("library").transform(JOOXTest.class.getResource("/book-id-decrement.xsl")).find("book").ids(Integer.class));
        assertEquals($(xmlExampleString).toString(), $.toString());
    }

    @Test
    public void testAfter() throws Exception {
        assertEquals(2, $.find("dvds").after("<cds/>").size());
        assertEquals(1, $.find("cds").size());
        assertEquals(3, $.find("library").eq(0).children().size());
        assertEquals(
            Arrays.asList("books", "dvds", "cds"),
            $.find("library").eq(0).children().tags());

        assertEquals(2, $.find("dvds").after("<postcards/>").size());
        assertEquals(1, $.find("postcards").size());
        assertEquals(4, $.find("library").eq(0).children().size());
        assertEquals(
            Arrays.asList("books", "dvds", "postcards", "cds"),
            $.find("library").eq(0).children().tags());

        // Append a new book
        // -----------------
        assertEquals(16, $.find("author").after($("author", "Alfred Hitchcock")).size());
        assertEquals(16, $.find("authors").children().size());
        assertEquals(Arrays.asList(
            "George Orwell", "Alfred Hitchcock",
            "George Orwell", "Alfred Hitchcock",
            "Paulo Coelho", "Alfred Hitchcock",
            "Paulo Coelho", "Alfred Hitchcock",
            "George Orwell", "Alfred Hitchcock",
            "Paulo Coelho", "Alfred Hitchcock",
            "George Orwell", "Alfred Hitchcock",
            "George Orwell", "Alfred Hitchcock"), $.find("author").texts());
    }

    @Test
    public void testBefore() throws Exception {
        assertEquals(2, $.find("dvds").before("<cds/>").size());
        assertEquals(1, $.find("cds").size());
        assertEquals(3, $.find("library").eq(0).children().size());
        assertEquals(
            Arrays.asList("books", "cds", "dvds"),
            $.find("library").eq(0).children().tags());

        assertEquals(2, $.find("dvds").before("<postcards/>").size());
        assertEquals(1, $.find("postcards").size());
        assertEquals(4, $.find("library").eq(0).children().size());
        assertEquals(
            Arrays.asList("books", "cds", "postcards", "dvds"),
            $.find("library").eq(0).children().tags());

        // Prepend a new book
        // ------------------
        assertEquals(16, $.find("author").before($("author", "Alfred Hitchcock")).size());
        assertEquals(16, $.find("authors").children().size());
        assertEquals(Arrays.asList(
            "Alfred Hitchcock", "George Orwell",
            "Alfred Hitchcock", "George Orwell",
            "Alfred Hitchcock", "Paulo Coelho",
            "Alfred Hitchcock", "Paulo Coelho",
            "Alfred Hitchcock", "George Orwell",
            "Alfred Hitchcock", "Paulo Coelho",
            "Alfred Hitchcock", "George Orwell",
            "Alfred Hitchcock", "George Orwell"), $.find("author").texts());
    }

    @Test
    public void testAppend() throws Exception {
        assertEquals(1, $.find("dvds").append("<dvd id=\"6\"/>").size());
        assertEquals(2, $.find("dvd").size());
        assertEquals(2, $.find("dvds").children().size());
        assertEquals(
            Arrays.asList("5", "6"),
            $.find("dvd").ids());

        assertEquals(1, $.find("dvds").append("<dvd id=\"7\"/><dvd id=\"8\"/>").size());
        assertEquals(4, $.find("dvd").size());
        assertEquals(4, $.find("dvds").children().size());
        assertEquals(
            Arrays.asList("5", "6", "7", "8"),
            $.find("dvd").ids());

        assertEquals(1, $.find("director").append("<><aa>").size());
        assertEquals(0, $.find("director").children().size());
        assertEquals("Sergio Leone<><aa>", $.find("director").text());
        assertEquals("Sergio Leone&lt;&gt;&lt;aa&gt;", $.find("director").content());

        // Append a new book
        // -----------------
        assertEquals(1,
            $.find("books").eq(0).append(
                $("book",
                    $("name", "The Da Vinci Code"),
                    $("authors",
                        $("author", "Dan Brown"))).attr("id", "5")).size());
        assertEquals(5, $.find("books").eq(0).children().size());
        assertEquals(1, $.find("book").eq(4).children("name").size());
        assertEquals("The Da Vinci Code", $.find("book").eq(4).children("name").text());
        assertEquals(1, $.find("book").eq(4).children("authors").size());
        assertEquals(1, $.find("book").eq(4).children("authors").children("author").size());
        assertEquals("Dan Brown", $.find("book").eq(4).children("authors").children("author").text());
        assertEquals(Arrays.asList("1", "2", "3", "4", "5"), $.find("books").eq(0).children("book").ids());
    }

    @Test
    public void testAppendDetached() {
        Document document = $("<a/>").document();
        Element e = document.createElement("e");
        $(document).append(e);
        assertEquals("<a><e/></a>", $(document).toString());
    }

    @Test
    public void testPrepend() throws Exception {
        assertEquals(1, $.find("dvds").prepend("<dvd id=\"6\"/>").size());
        assertEquals(2, $.find("dvd").size());
        assertEquals(2, $.find("dvds").children().size());
        assertEquals(
            Arrays.asList("6", "5"),
            $.find("dvd").ids());

        assertEquals(1, $.find("dvds").prepend("<dvd id=\"7\"/><dvd id=\"8\"/>").size());
        assertEquals(4, $.find("dvd").size());
        assertEquals(4, $.find("dvds").children().size());
        assertEquals(
            Arrays.asList("7", "8", "6", "5"),
            $.find("dvd").ids());

        assertEquals(1, $.find("director").prepend("<><aa>").size());
        assertEquals(0, $.find("director").children().size());
        assertEquals("<><aa>Sergio Leone", $.find("director").text());
        assertEquals("&lt;&gt;&lt;aa&gt;Sergio Leone", $.find("director").content());

        // Prepend a new book
        // ------------------
        assertEquals(1,
            $.find("books").eq(0).prepend(
                $("book",
                    $("name", "The Da Vinci Code"),
                    $("authors",
                        $("author", "Dan Brown"))).attr("id", "5")).size());
        assertEquals(5, $.find("books").eq(0).children().size());
        assertEquals(1, $.find("book").eq(0).children("name").size());
        assertEquals("The Da Vinci Code", $.find("book").eq(0).children("name").text());
        assertEquals(1, $.find("book").eq(0).children("authors").size());
        assertEquals(1, $.find("book").eq(0).children("authors").children("author").size());
        assertEquals("Dan Brown", $.find("book").eq(0).children("authors").children("author").text());
        assertEquals(Arrays.asList("5", "1", "2", "3", "4"), $.find("books").eq(0).children("book").ids());
    }

    @Test
    public void testReplaceWith() throws Exception {
        assertEquals(
            "best-director-in-the-world",
            $.find("director").replaceWith("<best-director-in-the-world>Jean Claude van Damme</best-director-in-the-world>").tag());
        assertEquals(0, $.find("director").size());
        assertEquals(1, $.find("best-director-in-the-world").size());
        assertEquals("directors", $.find("best-director-in-the-world").parent().tag());

        assertEquals(0, $.find("best-director-in-the-world").replaceWith("<><aa>").size());
        assertEquals("<><aa>", $.find("directors").text().trim());
        assertEquals("&lt;&gt;&lt;aa&gt;", $.find("directors").content().trim());

        // Replace a new book
        // ------------------
        assertEquals(1,
            $.find("book").eq(1).replaceWith(
                $("book",
                    $("name", "The Da Vinci Code"),
                    $("authors",
                        $("author", "Dan Brown"))).attr("id", "5")).size());
        assertEquals(4, $.find("books").eq(0).children().size());
        assertEquals(1, $.find("book").eq(1).children("name").size());
        assertEquals("The Da Vinci Code", $.find("book").eq(1).children("name").text());
        assertEquals(1, $.find("book").eq(1).children("authors").size());
        assertEquals(1, $.find("book").eq(1).children("authors").children("author").size());
        assertEquals("Dan Brown", $.find("book").eq(1).children("authors").children("author").text());
        assertEquals(Arrays.asList("1", "5", "3", "4"), $.find("books").eq(0).children("book").ids());
    }

    @Test
    public void testRename() throws Exception {
        assertEquals(
            Collections.nCopies(8, "xx"),
            $.find("book").rename("xx").tags());

        assertTrue($.find("book").isEmpty());
        assertFalse($.find("book").isNotEmpty());
        assertEquals(8, $.find("xx").size());
        assertEquals(8, $.find("books").children().size());
        assertEquals(8, $.find("books").children("xx").size());

        assertEquals(
            asList("b1", "b2", "b3", "b4", "b5", "b6", "b7", "b8"),
            $.find("xx").rename(new Content() {
                @Override
                public String content(Context context) {
                    return "b" + (context.matchIndex() + 1);
                }
            }).tags());

        assertEquals(
            asList("b1", "b2", "b3", "b4", "b5", "b6", "b7", "b8"),
            $.find("books").children().tags());
    }

    @Test
    public void testElementCreation() throws Exception {
        assertEquals(1, $("<hello><world/></hello>").size());
        assertEquals(1, $("<hello><world/></hello>").children().size());
        assertEquals(0, $("<hello><world/></hello>").find("any").size());
        assertEquals("hello", $("<hello><world/></hello>").tag());
        assertEquals("world", $("<hello><world/></hello>").children().eq(0).tag());
        assertEquals(1, $("<hello><world/></hello>").find("world").size());

        Match x = $("root",
                $("child1", "value1"),
                $("child2", "value2").attr("id", "5"));
        assertEquals(1, x.size());
        assertEquals("root", x.tag());
        assertEquals(2, x.children().size());
        assertEquals("child1", x.children().tag(0));
        assertEquals("child2", x.children().tag(1));
        assertEquals("value1", x.children().text(0));
        assertEquals("value2", x.children().text(1));
        assertEquals("5", x.children().attrs("id").get(1));
        assertEquals(Arrays.asList(null, "5"), x.children().ids());
    }

    @Test
    public void testXPath() throws Exception {
        assertEquals("/document[1]", $.xpath());
        assertEquals("/document[1]", $.xpath(0));
        assertEquals(null, $.xpath(1));
        assertEquals(Arrays.asList("/document[1]"), $.xpaths());
        assertEquals(Arrays.asList("/document[1]", null), $.xpaths(0, 1));

        assertEquals("/document[1]/library[1]/books[1]/book[1]", $.find("book").xpath());
        assertEquals("/document[1]/library[1]/books[1]/book[1]", $.find("book").xpath(0));
        assertEquals("/document[1]/library[1]/books[1]/book[2]", $.find("book").xpath(1));
        assertEquals("/document[1]/library[1]/books[1]/book[3]", $.find("book").xpath(2));
        assertEquals("/document[1]/library[1]/books[1]/book[4]", $.find("book").xpath(3));
        assertEquals("/document[1]/library[2]/books[1]/book[1]", $.find("book").xpath(4));
        assertEquals("/document[1]/library[2]/books[1]/book[2]", $.find("book").xpath(5));
        assertEquals("/document[1]/library[3]/books[1]/book[1]", $.find("book").xpath(6));
        assertEquals("/document[1]/library[3]/books[1]/book[2]", $.find("book").xpath(7));
        assertEquals(null, $.find("book").xpath(8));
        assertEquals(Arrays.asList(
            "/document[1]/library[1]/books[1]/book[1]",
            "/document[1]/library[1]/books[1]/book[2]",
            "/document[1]/library[1]/books[1]/book[3]",
            "/document[1]/library[1]/books[1]/book[4]",
            "/document[1]/library[2]/books[1]/book[1]",
            "/document[1]/library[2]/books[1]/book[2]",
            "/document[1]/library[3]/books[1]/book[1]",
            "/document[1]/library[3]/books[1]/book[2]"),
            $.find("book").xpaths());

        assertEquals(asList(
            "/document[1]/library[1]/dvds[1]/dvd[1]/actors[1]/actor[1]",
            "/document[1]/library[1]/dvds[1]/dvd[1]/actors[1]/actor[2]",
            "/document[1]/library[1]/dvds[1]/dvd[1]/actors[1]/actor[3]"),
            $.find("actor").xpaths());
    }

    @Test
    public void testPath() throws Exception {
        assertEquals("/document", $.map(paths()).get(0));
        assertEquals(Arrays.asList("/document"), $.map(paths()));

        assertEquals("/document/library/books/book", $.find("book").map(paths()).get(0));
        assertEquals(Arrays.asList(
            "/document/library/books/book",
            "/document/library/books/book",
            "/document/library/books/book",
            "/document/library/books/book",
            "/document/library/books/book",
            "/document/library/books/book",
            "/document/library/books/book",
            "/document/library/books/book"),
            $.find("book").map(paths()));

        assertEquals(asList(
            "/document/library/dvds/dvd/actors/actor",
            "/document/library/dvds/dvd/actors/actor",
            "/document/library/dvds/dvd/actors/actor"),
            $.find("actor").map(paths()));
    }

    @Test
    public void testXPathWithVariables() throws Exception {
        assertEquals(2, $.xpath("//*[@id > $1]", 3).size());
        assertEquals(asList(4, 5), $.xpath("//*[@id > $1]", 3).ids(Integer.class));

        assertEquals(5, $.xpath("//book[count(authors/author) = $1][authors/author[text() = $2]]", 1, "George Orwell").size());
        assertEquals(5, $.xpath("//book[count(authors/author) = $2][authors/author[text() = $1]]", "George Orwell", 1).size());

        assertEquals(asList(1, 2, 1, 1, 2), $.xpath("//book[count(authors/author) = $1][authors/author[text() = $2]]", 1, "George Orwell").ids(Integer.class));

        try {
            $.xpath("//*[$1 = $2]", 1);
            fail();
        }
        catch (IndexOutOfBoundsException expected) {}
    }

    @Test
    public void testXPathWithFunctions() throws Exception {




        assertEquals(1, $.xpath("//book[number(@id) = math:max(//book/@id)]").size());
        assertEquals(4, (int) $.xpath("//book[number(@id) = math:max(//book/@id)]").id(Integer.class));

        assertEquals(5, $.xpath("//book[java:org.joox.Functions.byOrwellWithNodes(.)]").size());
        assertEquals(asList(1, 2, 1, 1, 2), $.xpath("//book[java:org.joox.Functions.byOrwellWithNodes(.)]").ids(Integer.class));

        assertEquals(5, $.xpath("//book[java:org.joox.Functions.byOrwellWithId(number(@id))]").size());
        assertEquals(asList(1, 2, 1, 1, 2), $.xpath("//book[java:org.joox.Functions.byOrwellWithId(number(@id))]").ids(Integer.class));
    }



    @Test
    public void testTags() throws Exception {
        assertEquals("document", $.tag());
        assertEquals("document", $.tag(0));
        assertEquals(null, $.tag(1));
        assertEquals(Arrays.asList("document"), $.tags());
        assertEquals(Arrays.asList("document", null), $.tags(0, 1));

        assertEquals("book", $.find("books").eq(0).children().tag());
        assertEquals("book", $.find("books").eq(0).children().tag(0));
        assertEquals("book", $.find("books").eq(0).children().tag(1));
        assertEquals("book", $.find("books").eq(0).children().tag(2));
        assertEquals("book", $.find("books").eq(0).children().tag(3));
        assertEquals(null, $.find("books").eq(0).children().tag(4));
        assertEquals(Arrays.asList("book", "book", "book", "book"), $.find("books").eq(0).children().tags());
        assertEquals(Arrays.asList("book", "book", "book", "book"), $.find("books").eq(0).children().tags(0, 1, 2, 3));
        assertEquals(Arrays.asList("book", "book", "book", "book", null), $.find("books").eq(0).children().tags(0, 1, 2, 3, 4));
        assertEquals(Arrays.asList("book", "book"), $.find("books").eq(0).children().tags(1, 2));
    }

    @Test
    public void testIds() throws Exception {
        assertEquals(null, $.id());
        assertEquals(null, $.id(0));
        assertEquals(null, $.id(1));
        assertEquals(Arrays.asList((Object) null), $.ids());
        assertEquals(Arrays.asList(null, null), $.ids(0, 1));

        assertEquals("1", $.find("books").eq(0).children().id());
        assertEquals("1", $.find("books").eq(0).children().id(0));
        assertEquals("2", $.find("books").eq(0).children().id(1));
        assertEquals("3", $.find("books").eq(0).children().id(2));
        assertEquals("4", $.find("books").eq(0).children().id(3));
        assertEquals(null, $.find("books").eq(0).children().id(4));
        assertEquals(Arrays.asList("1", "2", "3", "4"), $.find("books").eq(0).children().ids());
        assertEquals(Arrays.asList("1", "2", "3", "4"), $.find("books").eq(0).children().ids(0, 1, 2, 3));
        assertEquals(Arrays.asList("1", "2", "3", "4", null), $.find("books").eq(0).children().ids(0, 1, 2, 3, 4));
        assertEquals(Arrays.asList("2", "3"), $.find("books").eq(0).children().ids(1, 2));
    }

    @Test
    public void test$Node() throws Exception {
        assertEquals(0, $((Node) null).find("book").size());
        assertEquals(8, $((Node) xmlExampleDocument).find("book").size());
        assertEquals(8, $((Node) xmlExampleElement).find("book").size());
    }

    @Test
    public void test$NodeList() throws Exception {
        assertEquals(0, $((NodeList) null).size());
        assertEquals(0, $(xmlExampleDocument.getElementsByTagName("xxx")).size());
        assertEquals(8, $(xmlExampleDocument.getElementsByTagName("book")).size());
        assertEquals(
            Arrays.asList("1", "2", "3", "4", "1", "3", "1", "2"),
            $(xmlExampleDocument.getElementsByTagName("book")).ids());
    }

    @Test
    public void test$Object() throws Exception {
        Customer c = new Customer();

        assertEquals(0, $((Object) null).size());
        assertEquals(1, $(c).size());
        assertEquals("customer", $(c).tag());
        assertEquals(null, $(c).find("name").text());
        assertEquals(0, (int) $(c).attr("id", Integer.class));
        assertEquals(0, (int) $(c).find("age").text(Integer.class));

        assertEquals(1, $(getCustomer()).size());
        assertEquals("customer", $(getCustomer()).tag());
        assertEquals("Lukas", $(getCustomer()).find("name").text());
        assertEquals(13, (int) $(getCustomer()).attr("id", Integer.class));
        assertEquals(30, (int) $(getCustomer()).find("age").text(Integer.class));
    }

    @Test
    public void test$String() throws Exception {
        assertEquals("<a/>", $("a").toString());
        assertEquals("<a/>", $("<a></a>").toString());
        assertEquals("<a/>", $("<a/>").toString());
        assertEquals("<a/>", $("<!-- some comment --><a/>").toString());
        assertEquals("<a/>", $("<?xml version=\"1.0\"?><!-- some comment --><a/>").toString());
    }

    @Test
    public void test$URLandURI() throws Exception {
        assertEquals($.toString(), $(JOOXTest.class.getResource("/example.xml")).toString());
        assertEquals($.toString(), $(JOOXTest.class.getResource("/example.xml").toURI()).toString());
    }

    @Test
    public void testWrite() throws Exception {
        StringWriter writer = new StringWriter();
        $.write(writer);
        assertEquals($.toString(), writer.toString());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        $.write(stream);
        assertEquals($.toString(), stream.toString());

        writer = new StringWriter();
        stream = new ByteArrayOutputStream();
        assertEquals($, $.write(writer).write(stream));
        assertEquals($.toString(), writer.toString());
        assertEquals($.toString(), stream.toString());

        writer = new StringWriter();
        $.find("abc").write(writer);
        assertEquals("", writer.toString());

        writer = new StringWriter();
        $.find("author").slice(0, 2).write(writer);
        assertEquals("<author>George Orwell</author><author>George Orwell</author>", writer.toString());
    }

    @Test
    public void testUnmarshal() throws Exception {
        Match match = $("customer",
            $("age", "30"),
            $("name", "Lukas")).attr("id", "13");

        assertEquals(getCustomer(), match.unmarshalOne(Customer.class));
        assertEquals(getCustomer(), match.unmarshalOne(Customer.class, 0));
        assertEquals(null, match.unmarshalOne(Customer.class, 1));

        assertEquals(1, match.unmarshal(Customer.class).size());
        assertEquals(1, match.unmarshal(Customer.class, 0, 1, 2).size());
        assertEquals(0, match.unmarshal(Customer.class, 1, 2, 3).size());
        assertEquals(getCustomer(), match.unmarshal(Customer.class).get(0));
        assertEquals(getCustomer(), match.unmarshal(Customer.class, 0).get(0));
    }

    @Test
    public void testRegex() throws Exception {
        assertEquals(11, $.find().matchTag("books?").size());
        assertEquals(11, $.find().matchTag("books?", true).size());
        assertEquals($.find().size() - 11,
            $.find().matchTag("books?", false).size());

        assertEquals(2, $.find("name, actor").matchText(".*in.*").size());
        assertEquals(2, $.find("name, actor").matchText(".*in.*", true).size());
        assertEquals($.find("name, actor").size() - 2,
            $.find("name, actor").matchText(".*in.*", false).size());

        assertEquals(2, $.find("library").matchAttr("name", ".*i.*").size());
        assertEquals(1, $.find("library").matchAttr("name", ".*i.*", false).size());
        assertEquals(asList("Roesslitor", "Orell Fuessli"), $.find("library").matchAttr("name", ".*i.*").attrs("name"));
        assertEquals(asList("Amazon"), $.find("library").matchAttr("name", ".*i.*", false).attrs("name"));
    }

    @Test
    public void testSort() throws Exception {
        assertEquals(11, $.find().matchTag("books?").sort(new SimpleElementComparator()).size());

        assertEquals(asList("books", "book", "book", "book", "book", "books", "book", "book", "books", "book", "book"),
                $.find().matchTag("books?").tags());

        assertEquals(asList("book", "book", "book", "book","book", "book", "book", "book", "books", "books", "books"),
                $.find().matchTag("books?").sort(new SimpleElementComparator()).tags());
    }

    class SimpleElementComparator implements Comparator<Element> {
        @Override
        public int compare(final Element element1, final Element element2) {
            return element1.getTagName().compareTo(element2.getTagName());
        }
    }

    @Test
    public void testLeaf() throws Exception {
        assertEquals($.xpath("//*[not(*)]"), $.find().leaf());
        assertEquals($.find("books").eq(0).find("name, author"), $.find("books").eq(0).find().leaf());
    }

    @Test
    public void testDates() throws Exception {
        $ = $(xmlDatesDocument);

        // Some general conversion tests
        // -----------------------------
        assertEquals(java.sql.Date.valueOf("1981-07-10"),
            $.find("sql-date").texts(java.sql.Date.class).get(0));
        assertEquals(java.sql.Timestamp.valueOf("1981-07-10 00:00:00.0"),
            $.find("sql-date").texts(java.sql.Timestamp.class).get(0));
        assertEquals(new java.util.Date(java.sql.Date.valueOf("1981-07-10").getTime()),
            $.find("sql-date").texts(java.util.Date.class).get(0));
        assertEquals(new java.util.Date(java.sql.Date.valueOf("1981-07-10").getTime()),
            $.find("sql-date").texts(java.util.Date.class).get(0));

        assertEquals("1981-07-10",
            $.find("sql-timestamp").texts(java.sql.Date.class).get(0).toString());
        assertEquals(java.sql.Timestamp.valueOf("1981-07-10 09:15:37.0"),
            $.find("sql-timestamp").texts(java.sql.Timestamp.class).get(0));
        assertEquals(new java.util.Date(java.sql.Timestamp.valueOf("1981-07-10 09:15:37.0").getTime()),
            $.find("sql-timestamp").texts(java.util.Date.class).get(0));
        assertEquals(new java.util.Date(java.sql.Timestamp.valueOf("1981-07-10 09:15:37.0").getTime()),
            $.find("sql-timestamp").texts(java.util.Date.class).get(0));

        assertEquals(java.sql.Time.valueOf("09:15:37"),
            $.find("sql-time").text(java.sql.Time.class));

        // Conversion tests on attributes
        // ------------------------------
        assertEquals(java.sql.Date.valueOf("1981-07-10"),
            $.find("record2").attr("sql-date", java.sql.Date.class));
        assertEquals(java.sql.Timestamp.valueOf("1981-07-10 09:15:37.0"),
            $.find("record2").attr("sql-timestamp", java.sql.Timestamp.class));
        assertEquals(java.sql.Time.valueOf("09:15:37"),
            $.find("record2").attr("sql-time", java.sql.Time.class));
    }

    private Customer getCustomer() {
        Customer c = new Customer();

        c.age = 30;
        c.name = "Lukas";
        c.id = 13;

        return c;
    }

    @Test
    public void testNamespacesAPI() {
        assertEquals(nCopies(3, null), $.find("library").namespacePrefixes());
        assertEquals(nCopies(2, null), $.find("library").namespacePrefixes(0, 1));
        assertEquals(null, $.find("library").namespacePrefix());
        assertEquals(nCopies(3, null), $.find("library").namespaceURIs());
        assertEquals(nCopies(2, null), $.find("library").namespaceURIs(0, 1));
        assertEquals(null, $.find("library").namespaceURI());

        assertEquals(totalElements, $.find().filter(JOOX.namespacePrefix(null)).size());
        assertEquals(totalElements, $.find().filter(JOOX.namespacePrefix("")).size());
        assertEquals(0, $.find().filter(JOOX.namespacePrefix("ns")).size());
        assertEquals(totalElements, $.find().filter(JOOX.namespaceURI(null)).size());
        assertEquals(totalElements, $.find().filter(JOOX.namespaceURI("")).size());
        assertEquals(0, $.find().filter(JOOX.namespaceURI("ns")).size());

        $ = $(xmlNamespacesDocument);

        assertEquals(null, $.namespacePrefix());
        assertEquals("http://www.example.com/root", $.namespaceURI());

        assertEquals(null, $.find("node").eq(1).namespacePrefix());
        assertEquals("http://www.example.com/root", $.find("node").eq(1).namespaceURI());

        assertEquals("ns", $.find("node").eq(2).namespacePrefix());
        assertEquals("http://www.example.com/root/ns", $.find("node").eq(2).namespaceURI());

        assertEquals(
            asList(      null, null, "ns", "ns",
                   null, null, null, "ns", "ns",
                   null, null, null, "xx", "xx"),
            $.find().namespacePrefixes());
        assertEquals(
            asList("http://www.example.com/root",
                   "http://www.example.com/root",
                   "http://www.example.com/root/ns",
                   "http://www.example.com/root/ns",

                   "http://www.example.com/nested",
                   "http://www.example.com/nested",
                   "http://www.example.com/nested",
                   "http://www.example.com/nested/ns",
                   "http://www.example.com/nested/ns"),
            $.find().namespaceURIs(0, 1, 2, 3, 4, 5, 6, 7, 8));

        assertEquals(8, $.find().filter(JOOX.namespacePrefix(null)).size());
        assertEquals(8, $.find().filter(JOOX.namespacePrefix("")).size());
        assertEquals(4, $.find().filter(JOOX.namespacePrefix("ns")).size());
        assertEquals(2, $.find().filter(JOOX.namespacePrefix("xx")).size());

        assertEquals(2, $.find().filter(JOOX.namespaceURI("http://www.example.com/root")).size());
        assertEquals(2, $.find().filter(JOOX.namespaceURI("http://www.example.com/root/ns")).size());
        assertEquals(6, $.find().filter(JOOX.namespaceURI("http://www.example.com/nested")).size());
        assertEquals(4, $.find().filter(JOOX.namespaceURI("http://www.example.com/nested/ns")).size());
    }

    @Test
    public void testNamespacesMatchAPI() {
        $ = $(xmlNamespacesDocument);

        assertEquals(1, $.size());
        assertEquals("root", $.tag());

        // Namespace-unaware find() method (elements by tag name)
        assertEquals(6, $.find(JOOX.tag("node", false)).size());
        assertEquals(12, $.find(JOOX.tag("node", true)).size());
        assertEquals(2, $.find(JOOX.tag("xx:node", false)).size());
        assertEquals(nCopies(2, "node"), $.find(JOOX.tag("xx:node", false)).tags());
        assertEquals(12, $.find("node").size());
        assertEquals(nCopies(12, "node"), $.find("node").tags());

        // Combinations of the above
        assertEquals(4, $.child("nested1").find("node").size());
        assertEquals(2, $.child("nested1").find(JOOX.tag("node", false)).size());
        assertEquals(4, $.child("nested1").find(JOOX.tag("node", true)).size());

        // Check children() as well
        assertEquals(2, $.child("nested1").children("node").size());
        assertEquals(1, $.child("nested1").children(JOOX.tag("node", false)).size());
        assertEquals(2, $.child("nested1").children(JOOX.tag("node", true)).size());

        // Check matchTag()
        assertEquals(12, $.find().matchTag("node").size());
        assertEquals(12, $.find(JOOX.matchTag("node", true)).size());
        assertEquals(6, $.find(JOOX.matchTag("node", false)).size());
        assertEquals(0, $.find(JOOX.matchTag("xx:.*", true)).size());
        assertEquals(2, $.find(JOOX.matchTag("xx:.*", false)).size());

        // Check attributes
        assertEquals(2, $.find("node").filter(JOOX.attr("a1")).size());
        assertEquals(2, $.find("node").filter(JOOX.attr("a1", "value")).size());
        assertEquals(2, $.find("node").filter(JOOX.attr("a2")).size());
        assertEquals(2, $.find("node").filter(JOOX.attr("a2", "ns:value")).size());
        assertEquals(1, $.find("node").filter(JOOX.attr("a3")).size());
        assertEquals(1, $.find("node").filter(JOOX.attr("a3", "value")).size());
        assertEquals(asList("value", "value"), $.child("node").find().attrs("a1"));
        assertEquals(asList("ns:value", "ns:value"), $.child("node").find().attrs("a2"));

        // Namespace-overloaded attributes are not supported!
        assertEquals(1, $.find("node").filter(JOOX.attr("a3", "value")).size());
        assertEquals(0, $.find("node").filter(JOOX.attr("a3", "ns:value")).size());

        // TODO: restrict resulting elements to matching namespaces
    }

    @Test
    public void testNamespacesSelectors() {
        $ = $(xmlNamespacesDocument);

        // [#107] Short circuiting find implementation
        assertEquals(12, $.find("node").size());

        // [#163] "Complex" selector
        assertEquals(4, $.find("nested1 node").size());
        assertEquals(8, $.find("nested1 node, nested2 node").size());
    }

    @Test
    public void testNamespacesXPath() {
        $ = $(xmlNamespacesDocument);

        // No explicit namespace specification
        try {
            assertEquals(2, $.xpath("//root-ns:node").size());
            fail();
        }
        catch (Exception expected) {}

        // Add some namespace awareness
        assertEquals(2, $.namespace("root-ns", "http://www.example.com/root/ns")
                         .xpath("//root-ns:node").size());

        // Check for immutability of Match w.r.t. namespace(...)
        try {
            assertEquals(2, $.xpath("//root-ns:node").size());
            fail();
        }
        catch (Exception expected) {}

        // Add several namespaces
        assertEquals(6, $.namespace("root-ns", "http://www.example.com/root/ns")
                         .namespace("nested-ns", "http://www.example.com/nested/ns")
                         .xpath("//root-ns:node | //nested-ns:node").size());

        // Check if state remains after ordinary operations
        assertEquals(2, $.namespace("root-ns", "http://www.example.com/root/ns")
                         .find()
                         .xpath("//root-ns:node").size());
    }

    @Test
    public void testNamespacesXPathListing() {
        $ = $(xmlNamespacesDocument);

        List<String> xpaths = new ArrayList<String>();
        for (Match m : $.find("*").each())
            xpaths.add(m.xpath());

        assertEquals(
            asList(
                "/root[1]/node[1]",
                "/root[1]/node[1]/node[1]",
                "/root[1]/node[1]/ns:node[1]",
                "/root[1]/ns:node[1]",
                "/root[1]/nested1[1]",
                "/root[1]/nested1[1]/node[1]",
                "/root[1]/nested1[1]/node[1]/node[1]",
                "/root[1]/nested1[1]/node[1]/ns:node[1]",
                "/root[1]/nested1[1]/ns:node[1]",
                "/root[1]/nested2[1]",
                "/root[1]/nested2[1]/node[1]",
                "/root[1]/nested2[1]/node[1]/node[1]",
                "/root[1]/nested2[1]/node[1]/xx:node[1]",
                "/root[1]/nested2[1]/xx:node[1]"
            ),
            xpaths);
    }

    @Test
    public void testChain() {
        final List<String> result = new ArrayList<String>();

        Match books = $.find("library[name=Amazon]").find("book");
        Each each1 = new Each() {
            @Override
            public void each(Context context) {
                result.add($(context).tag());
            }
        };
        Each each2 = new Each() {
            @Override
            public void each(Context context) {
                result.add($(context).id());
            }
        };

        result.clear();
        books.each(chain(each1, each2));
        assertEquals(asList("book", "1", "book", "2", "book", "3", "book", "4"), result);

        result.clear();
        books.each(chain(asList(each1, each2)));
        assertEquals(asList("book", "1", "book", "2", "book", "3", "book", "4"), result);

        result.clear();
        books.each(each1, each2);
        assertEquals(asList("book", "1", "book", "2", "book", "3", "book", "4"), result);

        result.clear();
        books.each(asList(each1, each2));
        assertEquals(asList("book", "1", "book", "2", "book", "3", "book", "4"), result);
    }

    @Test
    public void testEncoding() {
        String xml = "<tag1><tag2>éâ</tag2></tag1>";
        assertEquals(xml, "<tag2>éâ</tag2>", $(xml).content());
    }

    @Test
    public void testTrailingNewlines() {
        assertEquals("<test/>", $("\n<test/>\n").toString());
    }

    @Test
    public void testXMLEntities() {
        assertEquals("a &amp; b"    , $("<test>a &amp; b</test>").content());
        assertEquals("a & b"        , $("<test>a &amp; b</test>").text());
        assertEquals("a &lt; &gt; b", $("<test>a &lt; &gt; b</test>").content());
        assertEquals("a < > b"      , $("<test>a &lt; &gt; b</test>").text());

        assertEquals("a &amp; b"    , $("<test/>").content("a &amp; b").content());
        assertEquals("a & b"        , $("<test/>").content("a &amp; b").text());
        assertEquals("a &lt; &gt; b", $("<test/>").content("a &lt; &gt; b").content());
        assertEquals("a < > b"      , $("<test/>").content("a &lt; &gt; b").text());

        assertEquals("a &amp;amp; b"        , $("<test/>").text("a &amp; b").content());
        assertEquals("a &amp; b"            , $("<test/>").text("a &amp; b").text());
        assertEquals("a &amp;lt; &amp;gt; b", $("<test/>").text("a &lt; &gt; b").content());
        assertEquals("a &lt; &gt; b"        , $("<test/>").text("a &lt; &gt; b").text());
    }
}
