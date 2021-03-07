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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

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
public final class JOOX {

    // ---------------------------------------------------------------------
    // $ wrapper methods
    // ---------------------------------------------------------------------

    /**
     * Wrap a new empty document
     */
    public static Match $() {
        return $(builder().newDocument());
    }

    /**
     * Wrap a JAXB-marshallable element in a jOOX {@link Match} element set
     *
     * @see #content(Object)
     * @see Match#content(Object)
     */
    public static Match $(Object object) {
        Document document = builder().newDocument();

        if (object != null) {
            Result result = new DOMResult(document);
            JAXB.marshal(object, result);
        }

        return $(document);
    }

    /**
     * Create a new DOM element in an independent document
     */
    public static Match $(String name) {
        Document document = builder().newDocument();
        DocumentFragment fragment = Util.createContent(document, name);

        if (fragment != null)
            document.appendChild(fragment);
        else
            document.appendChild(document.createElement(name));

        return $(document);
    }

    /**
     * Create a new DOM element in an independent document
     */
    public static Match $(String name, String content) {
        return $(name).append(content);
    }

    /**
     * Create a new DOM element in an independent document
     * <p>
     * The added content is cloned into the new document
     */
    public static Match $(String name, Element... content) {
        return $(name).append(content);
    }

    /**
     * Create a new DOM element in an independent document
     * <p>
     * The added content is cloned into the new document
     */
    public static Match $(String name, Match... content) {
        return $(name).append(content);
    }

    /**
     * Wrap a DOM document in a jOOX {@link Match} element set
     */
    public static Match $(Document document) {
        if (document == null)
            return $();
        else if (document.getDocumentElement() == null)
            return new Impl(document, null);
        else
            return $(document.getDocumentElement());
    }

    /**
     * Wrap a DOM element in a jOOX {@link Match} element set
     */
    public static Match $(Element element) {
        if (element == null)
            return $();
        else
            return new Impl(element.getOwnerDocument(), null).addElements(element);
    }

    /**
     * Wrap a DOM {@link Node} in a jOOX {@link Match} element set
     * <p>
     * Supported node types are
     * <ul>
     * <li> {@link Document} : see {@link #$(Document)}</li>
     * <li> {@link Element} : see {@link #$(Element)}</li>
     * </ul>
     * If the supplied Node is of any other type, then an empty Match is created
     */
    public static Match $(Node node) {
        if (node instanceof Document)
            return $((Document) node);
        else if (node instanceof Element)
            return $((Element) node);
        else
            return $();
    }

    /**
     * Wrap a DOM {@link NodeList} in a jOOX {@link Match} element set
     * <p>
     * If the supplied NodeList is empty or null, then an empty Match is created
     */
    public static Match $(NodeList list) {
        if (list != null && list.getLength() > 0)
            return new Impl(list.item(0).getOwnerDocument(), null).addNodeList(list);

        return $();
    }

    /**
     * Convenience method for calling <code>$(context.element())</code>
     */
    public static Match $(Context context) {
        if (context == null)
            return $();
        else
            return $(context.element());
    }

    /**
     * Convenience method for calling <code>$(match)</code>
     */
    public static Match $(Match match) {
        if (match == null)
            return $();
        else
            return match;
    }

    /**
     * Convenience method for calling <code>$(url.openStream())</code>
     */
    public static Match $(URL url) throws SAXException, IOException {
        return $(url.openStream());
    }

    /**
     * Convenience method for calling <code>$(new File(uri))</code>
     */
    public static Match $(URI uri) throws SAXException, IOException {
        return $(new File(uri));
    }

    /**
     * Read a DOM document from a file into a {@link Match} element set
     *
     * @throws IOException
     * @throws SAXException
     */
    public static Match $(File file) throws SAXException, IOException {
        return $(builder().parse(file));
    }

    /**
     * Read a DOM document from a file into a {@link Match} element set
     *
     * @throws IOException
     * @throws SAXException
     */
    public static Match $(Path path) throws SAXException, IOException {
        return $(path.toFile());
    }

    /**
     * Read a DOM document from a stream into a {@link Match} element set
     *
     * @throws IOException
     * @throws SAXException
     */
    public static Match $(InputStream stream) throws SAXException, IOException {
        return $(builder().parse(stream));
    }

    /**
     * Read a DOM document from a reader into a {@link Match} element set
     *
     * @throws IOException
     * @throws SAXException
     */
    public static Match $(Reader reader) throws SAXException, IOException {
        return $(builder().parse(new InputSource(reader)));
    }

    /**
     * Read a DOM document from a file into a {@link Match} element set
     *
     * @throws IOException
     * @throws SAXException
     */
    public static Match $(InputSource source) throws SAXException, IOException {
        return $(builder().parse(source));
    }

    // ---------------------------------------------------------------------
    // Filter factories
    // ---------------------------------------------------------------------

    /**
     * A filter that always returns false
     */
    public static FastFilter none() {
        return NONE;
    }

    /**
     * A filter that always returns true
     */
    public static FastFilter all() {
        return ALL;
    }

    /**
     * A filter that returns true on all even iteration indexes (starting with
     * 0!)
     */
    public static FastFilter even() {
        return EVEN;
    }

    /**
     * A filter that returns true on all odd iteration indexes (starting with
     * 0!)
     */
    public static FastFilter odd() {
        return ODD;
    }

    /**
     * A filter that returns true on leaf elements
     */
    public static FastFilter leaf() {
        return LEAF;
    }

    /**
     * A filter that returns true on elements at given iteration indexes
     */
    public static FastFilter at(final int... indexes) {
        return context -> {
            for (int i : indexes)
                if (i == context.elementIndex())
                    return true;

            return false;
        };
    }

    /**
     * A filter that returns all elements matched by a given selector.
     * <p>
     * In most cases, this is the same as calling {@link #tag(String)}. In
     * {@link Match#find(String)}, the following CSS-style selector syntax
     * elements are also supported:
     * <table border="1">
     * <tr>
     * <th>Selector pattern</th>
     * <th>meaning</th>
     * </tr>
     * <tr>
     * <td>*</td>
     * <td>any element</td>
     * </tr>
     * <tr>
     * <td>E</td>
     * <td>an element of type E</td>
     * </tr>
     * <tr>
     * <td>E[foo]</td>
     * <td>an E element with a "foo" attribute</td>
     * </tr>
     * <tr>
     * <td>E[foo="bar"]</td>
     * <td>an E element whose "foo" attribute value is exactly equal to "bar"</td>
     * </tr>
     * <tr>
     * <td>E[foo~="bar"]</td>
     * <td>an E element whose "foo" attribute value is a list of
     * whitespace-separated values, one of which is exactly equal to "bar"</td>
     * </tr>
     * <tr>
     * <td>E[foo^="bar"]</td>
     * <td>an E element whose "foo" attribute value begins exactly with the
     * string "bar"</td>
     * </tr>
     * <tr>
     * <td>E[foo$="bar"]</td>
     * <td>an E element whose "foo" attribute value ends exactly with the string
     * "bar"</td>
     * </tr>
     * <tr>
     * <td>E[foo*="bar"]</td>
     * <td>an E element whose "foo" attribute value contains the substring "bar"
     * </td>
     * </tr>
     * <tr>
     * <td>E[foo|="en"]</td>
     * <td>an E element whose "foo" attribute has a hyphen-separated list of
     * values beginning (from the left) with "en"</td>
     * </tr>
     * <tr>
     * <td>E:root</td>
     * <td>an E element, root of the document</td>
     * </tr>
     * <tr>
     * <td>E:first-child</td>
     * <td>an E element, first child of its parent</td>
     * </tr>
     * <tr>
     * <td>E:last-child</td>
     * <td>an E element, last child of its parent</td>
     * </tr>
     * <tr>
     * <td>E:only-child</td>
     * <td>an E element, only child of its parent</td>
     * </tr>
     * <tr>
     * <td>E:empty</td>
     * <td>an E element that has no children (including text nodes)</td>
     * </tr>
     * <tr>
     * <td>E#myid</td>
     * <td>an E element with ID equal to "myid".</td>
     * </tr>
     * <tr>
     * <td>E F</td>
     * <td>an F element descendant of an E element</td>
     * </tr>
     * <tr>
     * <td>E > F</td>
     * <td>an F element child of an E element</td>
     * </tr>
     * <tr>
     * <td>E + F</td>
     * <td>an F element immediately preceded by an E element</td>
     * </tr>
     * <tr>
     * <td>E ~ F</td>
     * <td>an F element preceded by an E element</td>
     * </tr>
     * </table>
     * <p>
     * Note that due to the presence of pseudo selectors, such as
     * <code>:root</code>, <code>:empty</code>, etc, namespaces are not
     * supported in selectors. Use jOOX's XPath functionality provided in
     * {@link Match#xpath(String)} along with
     * {@link Match#namespaces(java.util.Map)} if your XML document contains
     * namespaces
     *
     * @see <a
     *      href="http://www.w3.org/TR/selectors/#selectors">http://www.w3.org/TR/selectors/#selectors</a>
     */
    public static Filter selector(final String selector) {
        return tag(selector);
    }

    /**
     * A filter that returns all elements with a given tag name
     * <p>
     * This is the same as calling <code>tag(tagName, true)</code>
     *
     * @see #tag(String, boolean)
     */
    public static FastFilter tag(final String tagName) {
        return tag(tagName, true);
    }

    /**
     * A filter that returns all elements with a given tag name
     * <p>
     * This method allows for specifying whether namespace prefixes should be
     * ignored. This is particularly useful in DOM Level 1 documents, which are
     * namespace-unaware. In those methods
     * {@link Document#getElementsByTagNameNS(String, String)} will not work, as
     * elements do not contain any <code>localName</code>.
     *
     * @param tagName The tag name to match. Use <strong>*</strong> as a special
     *            tag name to match all tag names
     * @param ignoreNamespace Whether namespace prefixes can be ignored. When
     *            set to <code>true</code>, then the namespace prefix is
     *            ignored. When set to <code>false</code>, then
     *            <code>tagName</code> must include the actual namespace prefix.
     */
    public static FastFilter tag(final String tagName, final boolean ignoreNamespace) {
        if (tagName == null || tagName.equals(""))
            return none();

        // [#104] The special * operator is also supported
        else if ("*".equals(tagName))
            return all();
        else
            return context -> {
                String localName = context.element().getTagName();

                // [#103] If namespaces are ignored, consider only local
                // part of possibly namespace-unaware Element
                if (ignoreNamespace)
                    localName = Util.stripNamespace(localName);

                return tagName.equals(localName);
            };
    }

    /**
     * A filter that returns all elements with a given namespace prefix
     * <p>
     * <code>null</code> and the empty string are treated equally to indicate
     * that no namespace prefix should be present.
     */
    public static FastFilter namespacePrefix(final String namespacePrefix) {

        // The special * operator is also supported
        if ("*".equals(namespacePrefix))
            return all();
        else
            return context -> {
                String match = $(context).namespacePrefix();

                if (match == null || "".equals(match))
                    return namespacePrefix == null || "".equals(namespacePrefix);
                else
                    return match.equals(namespacePrefix);
            };
    }

    /**
     * A filter that returns all elements with a given namespace URI
     * <p>
     * <code>null</code> and the empty string are treated equally to indicate
     * that no namespace URI should be present.
     * <p>
     * This only works if the underlying document is namespace-aware
     */
    public static FastFilter namespaceURI(final String namespaceURI) {

        // The special * operator is also supported
        if ("*".equals(namespaceURI))
            return all();
        else
            return context -> {
                String match = $(context).namespaceURI();

                if (match == null || "".equals(match))
                    return namespaceURI == null || "".equals(namespaceURI);
                else
                    return match.equals(namespaceURI);
            };
    }

    /**
     * A filter that returns all elements whose text content matches a given
     * regex
     *
     * @see Pattern#matches(String, CharSequence)
     */
    public static FastFilter matchText(final String regex) {
        if (regex == null || regex.equals("")) {
            return none();
        }
        else {
            Pattern pattern = Pattern.compile(regex);
            return context -> pattern.matcher($(context).text()).matches();
        }
    }

    /**
     * A filter that returns all elements whose text content matches a given
     * regex
     *
     * @see Pattern#matches(String, CharSequence)
     */
    public static FastFilter matchAttr(final String name, final String valueRegex) {
        if (name == null || name.equals("") || valueRegex == null || valueRegex.equals("")) {
            return none();
        }
        else {
            Pattern pattern = Pattern.compile(valueRegex);
            return context -> {
                String value = $(context).attr(name);

                if (value == null)
                    return false;

                return pattern.matcher(value).matches();
            };
        }
    }

    /**
     * A filter that returns all elements whose tag name matches a given regex
     * <p>
     * This is the same as calling <code>matchTag(regex, true)</code>
     *
     * @see Pattern#matches(String, CharSequence)
     */
    public static FastFilter matchTag(final String regex) {
        return matchTag(regex, true);
    }

    /**
     * A filter that returns all elements whose tag name matches a given regex
     * <p>
     * This method allows for specifying whether namespace prefixes should be
     * ignored. This is particularly useful in DOM Level 1 documents, which are
     * namespace-unaware. In those methods
     * {@link Document#getElementsByTagNameNS(String, String)} will not work, as
     * elements do not contain any <code>localName</code>.
     *
     * @param regex The regular expression to use for matching tag names.
     * @param ignoreNamespace Whether namespace prefixes can be ignored. When
     *            set to <code>true</code>, then the namespace prefix is
     *            ignored. When set to <code>false</code>, then
     *            <code>regex</code> must also match potential namespace
     *            prefixes.
     * @see Pattern#matches(String, CharSequence)
     */
    public static FastFilter matchTag(final String regex, final boolean ignoreNamespace) {
        if (regex == null || regex.equals("")) {
            return none();
        }
        else {
            Pattern pattern = Pattern.compile(regex);
            return context -> {
                String localName = context.element().getTagName();

                // [#106] If namespaces are ignored, consider only local
                // part of possibly namespace-unaware Element
                if (ignoreNamespace)
                    localName = Util.stripNamespace(localName);

                return pattern.matcher(localName).matches();
            };
        }
    }

    /**
     * A filter that returns all elements with a given attribute
     */
    public static FastFilter attr(final String name) {
        if (name == null || name.equals(""))
            return context -> context.element().getAttributes().getLength() == 0;
        else
            return context -> $(context).attr(name) != null;
    }

    /**
     * A filter that returns all elements with a given attribute being set to a
     * given value
     */
    public static FastFilter attr(final String name, final String value) {
        if (name == null || name.equals(""))
            return attr(name);
        else
            return context -> Objects.equals($(context).attr(name), value);
    }

    /**
     * A filter that returns all elements with a given attribute being set to a
     * given value
     */
    public static FastFilter attr(final String name, final String... values) {
        final List<String> list = Arrays.asList(values);

        if (name == null || name.equals(""))
            return attr(name);
        else
            return context -> list.contains($(context).attr(name));
    }

    /**
     * Combine filters
     */
    public static Filter and(final Filter... filters) {
        return context -> {
            for (Filter filter : filters)
                if (!filter.filter(context))
                    return false;

            return true;
        };
    }

    /**
     * Combine filters
     */
    public static Filter or(final Filter... filters) {
        return context -> {
            for (Filter filter : filters)
                if (filter.filter(context))
                    return true;

            return false;
        };
    }

    /**
     * Inverse a filter
     */
    public static Filter not(final Filter filter) {
        return context -> !filter.filter(context);
    }

    /**
     * Create a filter matching id attributes
     */
    public static FastFilter ids(String... ids) {
        final Set<String> set = new HashSet<>(Arrays.asList(ids));
        return context -> set.contains($(context).attr("id"));
    }

    // ---------------------------------------------------------------------
    // Content factories
    // ---------------------------------------------------------------------

    /**
     * Get a constant content that returns the same <code>value</code> for all
     * elements.
     */
    public static Content content(final String value) {
        return context -> value;
    }

    /**
     * Get a constant content that returns a marshalled, JAXB-annotated
     * <code>value</code> for all elements.
     *
     * @see #$(Object)
     * @see Match#content(Object)
     */
    public static Content content(final Object value) {
        if (value == null)
            return content("");

        return new Content() {
            private String marshalled;

            @Override
            public String content(Context context) {
                if (marshalled == null) {
                    try {
                        JAXBContext jaxb = JAXBContext.newInstance(value.getClass());
                        Marshaller marshaller = jaxb.createMarshaller();
                        marshaller.setProperty("jaxb.fragment", true);

                        StringWriter writer = new StringWriter();
                        marshaller.marshal(value, writer);
                        marshalled = writer.toString();
                    }
                    catch (JAXBException e) {
                        throw new DataBindingException(e);
                    }
                }

                return marshalled;
            }
        };
    }

    // ---------------------------------------------------------------------
    // Mapper factories
    // ---------------------------------------------------------------------

    /**
     * Create a mapper that returns all <code>id</code> attributes
     */
    public static Mapper<String> ids() {
        return attrs("id");
    }

    /**
     * Create a mapper that returns all attributes with a given name
     */
    public static Mapper<String> attrs(final String attributeName) {
        return context -> $(context).attr(attributeName);
    }

    /**
     * Create a mapper that returns all paths to given elements
     */
    public static Mapper<String> paths() {
        return context -> Util.path(context.element());
    }

    // ---------------------------------------------------------------------
    // API utilities
    // ---------------------------------------------------------------------

    /**
     * Chain several instances of {@link Each} into a single one.
     * <p>
     * The resulting chained <code>Each</code> produces a new <code>Each</code>
     * that can be used in the {@link Match#each(Each)} method. I.e. every node
     * in a set of matched nodes will be passed to every chained
     * <code>Each</code>, sequentially.
     */
    public static Each chain(final Each... each) {
        return chain(Arrays.asList(each));
    }

    /**
     * Chain several instances of {@link Each} into a single one.
     * <p>
     * The resulting chained <code>Each</code> produces a new <code>Each</code>
     * that can be used in the {@link Match#each(Each)} method. I.e. every node
     * in a set of matched nodes will be passed to every chained
     * <code>Each</code>, sequentially.
     */
    public static Each chain(final Iterable<? extends Each> each) {
        return context -> {
            if (each != null)
                for (Each e : each)
                    e.each(context);
        };
    }

    // ---------------------------------------------------------------------
    // DOM utilities
    // ---------------------------------------------------------------------

    /**
     * Wrap a {@link NodeList} into an {@link Iterable}
     */
    public static Iterable<Element> iterable(NodeList elements) {
        return new Elements(elements);
    }

    /**
     * Wrap a {@link NodeList} into an {@link Iterator}
     */
    public static Iterator<Element> iterator(NodeList elements) {
        return new Elements(elements).iterator();
    }

    /**
     * Wrap a {@link NodeList} into an {@link List}
     */
    public static List<Element> list(NodeList elements) {
        List<Element> list = new ArrayList<>();

        for (Element element : iterable(elements))
            list.add(element);

        return list;
    }

    /**
     * Get a namespace-aware document builder
     */
    public static DocumentBuilder builder() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // -----------------------------------------------------------------
            // [#136] FIX START: Prevent OWASP attack vectors
            try {
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            }
            catch (ParserConfigurationException ignore) {}

            try {
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            }
            catch (ParserConfigurationException ignore) {}

            try {
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            }
            catch (ParserConfigurationException ignore) {}

            // [#149] Not implemented on Android
            try {
                factory.setXIncludeAware(false);
            }
            catch (UnsupportedOperationException ignore) {}

            factory.setExpandEntityReferences(false);
            // [#136] FIX END
            // -----------------------------------------------------------------

            // [#9] [#107] In order to take advantage of namespace-related DOM
            // features, the internal builder should be namespace-aware
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            return builder;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------------------------------------------------------------
    // Other utilities
    // ---------------------------------------------------------------------

    private static final Set<String> TRUE_VALUES;
    private static final Set<String> FALSE_VALUES;

    static {
        TRUE_VALUES = new HashSet<>();
        FALSE_VALUES = new HashSet<>();

        TRUE_VALUES.add("1");
        TRUE_VALUES.add("y");
        TRUE_VALUES.add("yes");
        TRUE_VALUES.add("true");
        TRUE_VALUES.add("on");
        TRUE_VALUES.add("enabled");

        FALSE_VALUES.add("0");
        FALSE_VALUES.add("n");
        FALSE_VALUES.add("no");
        FALSE_VALUES.add("false");
        FALSE_VALUES.add("off");
        FALSE_VALUES.add("disabled");
    }

    /**
     * Convert a string value to any of these types:
     * <ul>
     * <li> {@link String}: The conversion has no effect</li>
     * <li> {@link Byte}: Numeric conversion. NaN will return null</li>
     * <li> {@link Short}: Numeric conversion. NaN will return null</li>
     * <li> {@link Integer}: Numeric conversion. NaN will return null</li>
     * <li> {@link Long}: Numeric conversion. NaN will return null</li>
     * <li> {@link Float}: Numeric conversion. NaN will return null</li>
     * <li> {@link Double}: Numeric conversion. NaN will return null</li>
     * <li> {@link BigDecimal}: Numeric conversion. NaN will return null</li>
     * <li> {@link BigInteger}: Numeric conversion. NaN will return null</li>
     * <li> {@link Boolean}: Boolean conversion. Boolean values for
     * <code>true</code> are any of these case-insensitive strings:
     * <ul>
     * <li><code>1</code></li>
     * <li><code>y</code></li>
     * <li><code>yes</code></li>
     * <li><code>true</code></li>
     * <li><code>on</code></li>
     * <li><code>enabled</code></li>
     * </ul>
     * Boolean values for <code>false</code> are any of these case-insensitive
     * strings:
     * <ul>
     * <li><code>0</code></li>
     * <li><code>n</code></li>
     * <li><code>no</code></li>
     * <li><code>false</code></li>
     * <li><code>off</code></li>
     * <li><code>disabled</code></li>
     * </ul>
     * </li>
     * <li>Primitive types: Numeric or boolean conversion, except that
     * <code>null</code> and illegal values will result in <code>0</code> or
     * <code>false</code></li>
     * <li> {@link java.util.Date}: Datetime conversion.</li>
     * <li> {@link java.util.Calendar}: Datetime conversion.</li>
     * <li> {@link java.util.GregorianCalendar}: Datetime conversion.</li>
     * <li> {@link java.sql.Timestamp}: Datetime conversion. Possible patterns
     * for datetime conversion are
     * <ul>
     * <li><code>yyyy</code>: Only the year is parsed</li>
     * <li><code>yyyy[-/]MM</code>: Year and month are parsed. Separator
     * characters are optional</li>
     * <li><code>yyyy[-/]MM[-/]dd</code>: Date is parsed. Separator characters
     * are optional</li>
     * <li><code>dd[-/.]MM[-/.]yyyy</code>: Date is parsed. Separator characters
     * are mandatory</li>
     * <li><code>yyyy[-/]MM[-/]dd[T ]HH</code>: Date and hour are parsed.
     * Separator characters are optional</li>
     * <li><code>yyyy[-/]MM[-/]dd[T ]HH[:]mm</code>: Date and time are parsed.
     * Separator characters are optional</li>
     * <li><code>yyyy[-/]MM[-/]dd[T ]HH[:]mm[:]ss</code>: Date and time are
     * parsed. Separator characters are optional</li>
     * <li><code>yyyy[-/]MM[-/]dd[T ]HH[:]mm[:]ss.SSS</code>: Date and time are
     * parsed. Separator characters are optional</li>
     * </ul>
     * </li>
     * <li> {@link java.sql.Date}: Date conversion. Possible patterns for date
     * conversion are
     * <ul>
     * <li><code>yyyy</code>: Only the year is parsed</li>
     * <li><code>yyyy[-/]MM</code>: Year and month are parsed. Separator
     * characters are optional</li>
     * <li><code>yyyy[-/]MM[-/]dd</code>: Date is parsed. Separator characters
     * are optional</li>
     * <li><code>dd[-/.]MM[-/.]yyyy</code>: Date is parsed. Separator characters
     * are mandatory</li>
     * </ul>
     * </li>
     * <li> {@link java.sql.Time}: Time conversion. Possible patterns for time
     * conversion are
     * <ul>
     * <li><code>HH</code>: Hour is parsed. Separator characters are optional</li>
     * <li><code>HH[:]mm</code>: Hour and minute are parsed. Separator
     * characters are optional</li>
     * <li><code>HH[:]mm[:]ss</code>: Time is parsed. Separator characters are
     * optional</li>
     * </ul>
     * </li>
     * <li>Any of the above as array. Arrays of any type are split by any
     * whitespace character, comma or semi-colon. String literals may be
     * delimited by quotes as well.</li>
     * </ul>
     * <p>
     * All other values evaluate to <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(String value, Class<T> type) {
        if (value == null && type.isPrimitive()) {
            value = "0";
        }

        if (value == null) {
            return null;
        }

        // [#24] TODO: base64-decode binary data
        // else if (type == byte[].class) {
        // }

        // [#28] Array conversion will recurse for split values
        else if (type.isArray()) {
            Class<?> component = type.getComponentType();
            List<String> split = Util.split(value);
            return (T) convert(split, component).toArray((Object[]) Array.newInstance(component, split.size()));
        }

        // Strings are not converted
        else if (type == String.class) {
            return (T) value;
        }

        // All type can be converted to Object
        else if (type == Object.class) {
            return (T) value;
        }

        // Various number types
        else if (type == Byte.class || type == byte.class) {
            try {
                return (T) Byte.valueOf(new BigDecimal(value).byteValue());
            }
            catch (Exception e) {
                return (T) ((type == Byte.class) ? null : (byte) 0);
            }
        }
        else if (type == Short.class || type == short.class) {
            try {
                return (T) Short.valueOf(new BigDecimal(value).shortValue());
            }
            catch (Exception e) {
                return (T) ((type == Short.class) ? null : (short) 0);
            }
        }
        else if (type == Integer.class || type == int.class) {
            try {
                return (T) Integer.valueOf(new BigDecimal(value).intValue());
            }
            catch (Exception e) {
                return (T) ((type == Integer.class) ? null : 0);
            }
        }
        else if (type == Long.class || type == long.class) {
            try {
                return (T) Long.valueOf(new BigDecimal(value).longValue());
            }
            catch (Exception e) {
                return (T) ((type == Long.class) ? null : 0L);
            }
        }
        else if (type == Float.class || type == float.class) {
            try {
                return (T) Float.valueOf(value);
            }
            catch (Exception e) {
                return (T) ((type == Float.class) ? null : 0.0f);
            }
        }
        else if (type == Double.class || type == double.class) {
            try {
                return (T) Double.valueOf(value);
            }
            catch (Exception e) {
                return (T) ((type == Double.class) ? null : 0.0);
            }
        }
        else if (type == BigDecimal.class) {
            try {
                return (T) new BigDecimal(value);
            }
            catch (Exception e) {
                return null;
            }
        }
        else if (type == BigInteger.class) {
            try {
                return (T) new BigDecimal(value).toBigInteger();
            }
            catch (Exception e) {
                return null;
            }
        }

        // Booleans have a set of allowed values
        else if (type == Boolean.class || type == boolean.class) {
            String s = value.toLowerCase();

            if (TRUE_VALUES.contains(s)) {
                return (T) Boolean.TRUE;
            }
            else if (FALSE_VALUES.contains(s)) {
                return (T) Boolean.FALSE;
            }
            else {
                return (T) ((type == Boolean.class) ? null : false);
            }
        }

        // [#29] TODO: Date-time types
        else if (type == java.util.Date.class) {
            try {
                return (T) Util.parseDate(value);
            }
            catch (Exception e) {
                return null;
            }
        }
        else if (type == java.util.Calendar.class) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(Util.parseDate(value));
                return (T) cal;
            }
            catch (Exception e) {
                return null;
            }
        }
        else if (type == java.util.GregorianCalendar.class) {
            try {
                Calendar cal = new GregorianCalendar();
                cal.setTime(Util.parseDate(value));
                return (T) cal;
            }
            catch (Exception e) {
                return null;
            }
        }
        else if (type == java.sql.Timestamp.class) {
            try {
                return (T) new java.sql.Timestamp(Util.parseDate(value).getTime());
            }
            catch (Exception e) {
                return null;
            }
        }
        else if (type == java.sql.Date.class) {
            try {
                return (T) new java.sql.Date(Util.parseDate(value).getTime());
            }
            catch (Exception e) {
                return null;
            }
        }
        else if (type == java.sql.Time.class) {
            try {
                return (T) new java.sql.Time(Util.parseDate(value).getTime());
            }
            catch (Exception e) {
                return null;
            }
        }

        // All other types are ignored
        return null;
    }

    /**
     * Convert several values
     *
     * @see #convert(String, Class)
     */
    public static <T> List<T> convert(List<String> values, Class<T> type) {
        List<T> result = new ArrayList<>();

        for (String value : values)
            result.add(convert(value, type));

        return result;
    }

    // ---------------------------------------------------------------------
    // Static utilities
    // ---------------------------------------------------------------------

    private static final FastFilter NONE = context -> false;

    private static final FastFilter ALL = context -> true;

    private static final FastFilter EVEN = context -> context.elementIndex() % 2 == 0;

    private static final FastFilter ODD = context -> context.elementIndex() % 2 != 0;

    private static final FastFilter LEAF = context -> {
        NodeList children = context.element().getChildNodes();

        for (int i = 0;;i++) {
            Node item = children.item(i);

            if (item == null)
                return true;
            else if (item.getNodeType() == Node.ELEMENT_NODE)
                return false;
        }
    };
}
