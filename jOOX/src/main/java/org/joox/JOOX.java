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
package org.joox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
     * Create a new DOM element in an independent document
     */
    public static Match $(String name) {
        Document document = builder().newDocument();
        DocumentFragment fragment = Util.createContent(document, name);

        if (fragment != null) {
            document.appendChild(fragment);
        }
        else {
            document.appendChild(document.createElement(name));
        }

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
        return $(document.getDocumentElement());
    }

    /**
     * Wrap a DOM element in a jOOX {@link Match} element set
     */
    public static Match $(Element element) {
        return new Impl(element.getOwnerDocument()).addElements(element);
    }

    /**
     * Wrap a DOM element or document in a jOOX {@link Match} element set
     * <p>
     * If the supplied Node is neither an element nor a document, then an empty
     * Match is created
     */
    public static Match $(Node node) {
        if (node instanceof Document) {
            return $((Document) node);
        }
        else if (node instanceof Element) {
            return $((Element) node);
        }

        return $(builder().newDocument());
    }

    /**
     * Convenience method for calling <code>$(context.match())</code>
     */
    public static Match $(Context context) {
        return $(context.match());
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
     * A filter that returns true on elements at given iteration indexes
     */
    public static FastFilter at(final int... indexes) {
        return new FastFilter() {
            @Override
            public boolean filter(Context context) {
                for (int i : indexes) {
                    if (i == context.elementIndex()) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * A filter that returns all elements matched by a given selector.
     * <p>
     * As of jOOX 0.9.0, this is the same as calling {@link #tag(String)}. More
     * sophisticated selector expressions will be added in the future.
     */
    public static Filter selector(final String selector) {
        return tag(selector);
    }

    /**
     * A filter that returns all elements with a given tag name
     */
    public static FastFilter tag(final String tagName) {
        if (tagName == null || tagName.equals("")) {
            return none();
        }
        else {
            return new FastFilter() {
                @Override
                public boolean filter(Context context) {
                    return tagName.equals(context.element().getTagName());
                }
            };
        }
    }

    /**
     * Combine filters
     */
    public static Filter and(final Filter... filters) {
        return new Filter() {
            @Override
            public boolean filter(Context context) {
                for (Filter filter : filters) {
                    if (!filter.filter(context)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Combine filters
     */
    public static Filter or(final Filter... filters) {
        return new Filter() {
            @Override
            public boolean filter(Context context) {
                for (Filter filter : filters) {
                    if (filter.filter(context)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Inverse a filter
     */
    public static Filter not(final Filter filter) {
        return new Filter() {
            @Override
            public boolean filter(Context context) {
                return !filter.filter(context);
            }
        };
    }

    /**
     * Create a filter matching id attributes
     */
    public static FastFilter ids(String... ids) {
        final Set<String> set = new HashSet<String>(Arrays.asList(ids));

        return new FastFilter() {
            @Override
            public boolean filter(Context context) {
                return set.contains(context.element().getAttribute("id"));
            }
        };
    }

    // ---------------------------------------------------------------------
    // Content factories
    // ---------------------------------------------------------------------

    /**
     * Get a constant content that returns the same <code>value</code> for all
     * elements.
     */
    public static Content content(final String value) {
        return new Content() {
            @Override
            public String content(Context context) {
                return value;
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
        return new Mapper<String>() {
            @Override
            public String map(Context context) {
                return $(context.element()).attr(attributeName);
            }
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
        List<Element> list = new ArrayList<Element>();

        for (Element element : iterable(elements)) {
            list.add(element);
        }

        return list;
    }

    /**
     * Get a document builder
     */
    public static DocumentBuilder builder() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
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
        TRUE_VALUES = new HashSet<String>();
        FALSE_VALUES = new HashSet<String>();

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
     * <li> {@link Byte}: Numeric conversion</li>
     * <li> {@link Short}: Numeric conversion</li>
     * <li> {@link Integer}: Numeric conversion</li>
     * <li> {@link Long}: Numeric conversion</li>
     * <li> {@link Float}: Numeric conversion</li>
     * <li> {@link Double}: Numeric conversion</li>
     * <li> {@link BigDecimal}: Numeric conversion</li>
     * <li> {@link BigInteger}: Numeric conversion</li>
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
     * <li> {@link java.util.Date}: Datetime conversion.</li>
     * <li> {@link java.util.Calendar}: Datetime conversion.</li>
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
        if (value == null) {
            return null;
        }

        // [#24] TODO: base64-decode binary data
        // else if (type == byte[].class) {
        // }

        // [#28] TODO: Array conversion will recurse for split values
        // else if (type.isArray()) {
        // }

        // Strings are not converted
        else if (type == String.class) {
            return (T) value;
        }

        // All type can be converted to Object
        else if (type == Object.class) {
            return (T) value;
        }

        // Various number types
        else if (type == Byte.class) {
            return (T) Byte.valueOf(new BigDecimal(value).byteValue());
        }
        else if (type == Short.class) {
            return (T) Short.valueOf(new BigDecimal(value).shortValue());
        }
        else if (type == Integer.class) {
            return (T) Integer.valueOf(new BigDecimal(value).intValue());
        }
        else if (type == Long.class) {
            return (T) Long.valueOf(new BigDecimal(value).longValue());
        }
        else if (type == Float.class) {
            return (T) Float.valueOf(value);
        }
        else if (type == Double.class) {
            return (T) Double.valueOf(value);
        }
        else if (type == BigDecimal.class) {
            return (T) new BigDecimal(value);
        }
        else if (type == BigInteger.class) {
            return (T) new BigDecimal(value).toBigInteger();
        }

        // Booleans have a set of allowed values
        else if (type == Boolean.class) {
            String s = value.toLowerCase();

            if (TRUE_VALUES.contains(s)) {
                return (T) Boolean.TRUE;
            }
            else if (FALSE_VALUES.contains(s)) {
                return (T) Boolean.FALSE;
            }
            else {
                return null;
            }
        }

        // [#29] TODO: Date-time types
        // else if (type == java.util.Date.class) {
        // }
        // else if (type == java.util.Calendar.class) {
        // }
        // else if (type == java.sql.Timestamp.class) {
        // }
        // else if (type == java.sql.Date.class) {
        // }
        // else if (type == java.sql.Time.class) {
        // }

        // All other types are ignored
        return null;
    }

    /**
     * Convert several values
     *
     * @see #convert(String, Class)
     */
    public static <T> List<T> convert(List<String> values, Class<T> type) {
        List<T> result = new ArrayList<T>();

        for (String value : values) {
            result.add(convert(value, type));
        }

        return result;
    }

    // ---------------------------------------------------------------------
    // Static utilities
    // ---------------------------------------------------------------------

    private static final FastFilter NONE = new FastFilter() {
        @Override
        public boolean filter(Context context) {
            return false;
        }
    };

    private static final FastFilter ALL = new FastFilter() {
        @Override
        public boolean filter(Context context) {
            return true;
        }
    };

    private static final FastFilter EVEN = new FastFilter() {
        @Override
        public boolean filter(Context context) {
            return context.elementIndex() % 2 == 0;
        }
    };

    private static final FastFilter ODD = new FastFilter() {
        @Override
        public boolean filter(Context context) {
            return context.elementIndex() % 2 == 1;
        }
    };
}
