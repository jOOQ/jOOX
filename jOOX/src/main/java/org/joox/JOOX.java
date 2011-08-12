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
import org.w3c.dom.NodeList;

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
    public static X $(String name) {
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
     * Wrap a DOM document in a jOOX {@link X} element set
     */
    public static X $(Document document) {
        return $(document.getDocumentElement());
    }

    /**
     * Wrap a DOM element in a jOOX {@link X} element set
     */
    public static X $(Element element) {
        return new Impl(element.getOwnerDocument()).addElements(element);
    }

    // ---------------------------------------------------------------------
    // Filter factories
    // ---------------------------------------------------------------------

    /**
     * A filter that always returns false
     */
    public static Filter none() {
        return new Filter() {
            @Override
            public boolean filter(int index, Element element) {
                return false;
            }
        };
    }

    /**
     * A filter that always returns true
     */
    public static Filter all() {
        return new Filter() {
            @Override
            public boolean filter(int index, Element element) {
                return true;
            }
        };
    }

    /**
     * A filter that returns true on all even indexes (starting with 0!)
     */
    public static Filter even() {
        return new Filter() {
            @Override
            public boolean filter(int index, Element element) {
                return index % 2 == 0;
            }
        };
    }

    /**
     * A filter that returns true on all odd indexes (starting with 0!)
     */
    public static Filter odd() {
        return new Filter() {
            @Override
            public boolean filter(int index, Element element) {
                return index % 2 == 1;
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
    public static Filter tag(final String tagName) {
        if (tagName == null || tagName.equals("")) {
            return none();
        }
        else {
            return new Filter() {
                @Override
                public boolean filter(int index, Element element) {
                    return tagName.equals(element.getTagName());
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
            public boolean filter(int index, Element element) {
                for (Filter filter : filters) {
                    if (!filter.filter(index, element)) {
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
            public boolean filter(int index, Element element) {
                for (Filter filter : filters) {
                    if (filter.filter(index, element)) {
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
            public boolean filter(int index, Element element) {
                return !filter.filter(index, element);
            }
        };
    }

    /**
     * Create a filter matching id attributes
     */
    public static Filter ids(String... ids) {
        final Set<String> set = new HashSet<String>(Arrays.asList(ids));

        return new Filter() {
            @Override
            public boolean filter(int index, Element element) {
                return set.contains(element.getAttribute("id"));
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
            public String content(int index, Element element) {
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
            public String map(int index, Element element) {
                return element.getAttribute(attributeName);
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
}
