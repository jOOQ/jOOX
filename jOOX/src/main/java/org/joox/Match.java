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

import java.util.List;

import org.w3c.dom.Element;

/**
 * A wrapper type for {@link org.w3c.dom.Element}
 * <p>
 * This is the main type of the jOOX library. It wraps an ordered list of DOM
 * elements without duplicates and provides useful operations upon all of the
 * contained elements. The wrapped DOM elements have been previously "matched"
 * by a jOOX operation.
 * <p>
 * The API has been inspired by <a
 * href="http://jquery.com">http://jquery.com</a>, a fantastic DOM abstraction
 * library for JavaScript.
 *
 * @author Lukas Eder
 * @see <a href="http://jquery.com">http://jquery.com</a>
 */
public interface Match extends Iterable<Element> {

    // ---------------------------------------------------------------------
    // DOM access
    // ---------------------------------------------------------------------

    /**
     * Get an element from the set of matched elements at a given index
     * <p>
     * Negative indexes are possible, too.
     * <ul>
     * <li> <code>-1</code> corresponds to the last element in the set of matched
     * elements.</li>
     * <li> <code>-2</code> corresponds to the second-last element, etc.</li>
     * </ul>
     */
    Element get(int index);

    /**
     * Get some elements from the set of matched elements at the given indexes
     * <p>
     * Negative indexes are possible, too.
     * <ul>
     * <li> <code>-1</code> corresponds to the last element in the set of matched
     * elements.</li>
     * <li> <code>-2</code> corresponds to the second-last element, etc.</li>
     * </ul>
     */
    List<Element> get(int... indexes);

    /**
     * Get an the set of matched elements
     */
    List<Element> get();

    /**
     * Get the number of matched elements in the set of matched elements
     */
    int size();

    /**
     * Whether there are any matched elements in the set of matched elements
     */
    boolean isEmpty();

    // ---------------------------------------------------------------------
    // Traversing
    // ---------------------------------------------------------------------

    /**
     * Add some elements to the set of matched elements
     */
    Match add(Element... elements);

    /**
     * Add some elements to the set of matched elements
     */
    Match add(Match... elements);

    /**
     * Find the first child of each element in the current set of matched
     * elements.
     * <p>
     * This is the same as calling <code>child(0)</code>.
     */
    Match child();

    /**
     * Find the child at a given index of each element in the current set of
     * matched elements.
     */
    Match child(int index);

    /**
     * Find all children of each element in the current set of matched elements.
     */
    Match children();

    /**
     * Find all children of each element in the current set of matched elements.
     */
    Match children(String selector);

    /**
     * Find all children of each element in the current set of matched elements.
     */
    Match children(Filter filter);

    /**
     * Find all children of each element at given indexes in the current set of
     * matched elements.
     */
    Match children(int... indexes);

    /**
     * Execute a callback for every element in the current set of matched
     * elements.
     */
    Match each(Each each);

    /**
     * Reduce the current set of matched elements.
     */
    Match filter(String selector);

    /**
     * Reduce the current set of matched elements.
     */
    Match filter(Filter filter);

    /**
     * Reduce the current set of matched elements to the elements at the given
     * indexes. If the given indexes are not in the range of indexes, the
     * resulting set will be empty.
     * <p>
     * Negative indexes are possible, too.
     * <ul>
     * <li> <code>-1</code> corresponds to the last element in the set of matched
     * elements.</li>
     * <li> <code>-2</code> corresponds to the second-last element, etc.</li>
     * </ul>
     */
    Match eq(int... indexes);

    /**
     * Find all descendants of each element in the current set of matched
     * elements.
     */
    Match find();

    /**
     * Find all descendants of each element in the current set of matched
     * elements.
     */
    Match find(String selector);

    /**
     * Find all descendants of each element in the current set of matched
     * elements.
     */
    Match find(Filter filter);

    /**
     * Match all elements given a certain XPath expression applied to each
     * element in the current set of matched element.
     * <p>
     * The XPath expression is evaluated using standard
     * {@link javax.xml.xpath.XPath}. Note that only matched elements will be
     * considered in the results. Examples:
     * <ul>
     * <li>Match all elements : <code>xpath("//*")</code></li>
     * <li>Match all books : <code>xpath("/library/books/book")</code></li>
     * <li>Match all book ID's : <code>xpath("//book").ids()</code></li>
     * <li>Match all book names : <code>xpath("//book/name").texts()</code></li>
     * </ul>
     * This doesn't work (not matching elements):
     * <ul>
     * <li>Match all book ID's : <code>xpath("//book/@id")</code></li>
     * <li>Match all book names : <code>xpath("//book/name/text()")</code></li>
     * </ul>
     */
    Match xpath(String expression);

    /**
     * Get the first in a set of matched elements.
     */
    Match first();

    /**
     * Reduce the set of matched element to those who have a descendant that
     * matches a selector.
     */
    Match has(String selector);

    /**
     * Reduce the set of matched element to those who have a descendant that
     * matches a filter.
     */
    Match has(Filter filter);

    /**
     * Check if at least one element in the set of matched elements satisfies a
     * selector.
     */
    boolean is(String selector);

    /**
     * Check if at least one element in the set of matched elements satisfies a
     * filter.
     */
    boolean is(Filter filter);

    /**
     * Get the last in a set of matched elements.
     */
    Match last();

    /**
     * Map the set of matched elements to a list of something
     */
    <E> List<E> map(Mapper<E> map);

    /**
     * Get the immediate next sibling of every element in set of matched
     * elements.
     */
    Match next();

    /**
     * Get the immediate next sibling of every element in set of matched
     * elements, matching a selector
     */
    Match next(String selector);

    /**
     * Get the immediate next sibling of every element in set of matched
     * elements, matching a filter
     */
    Match next(Filter filter);

    /**
     * Get all next siblings of every element in a set of matched elements
     */
    Match nextAll();

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a selector
     */
    Match nextAll(String selector);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a filter
     */
    Match nextAll(Filter filter);

    /**
     * Get all next siblings of every element in a set of matched elements until
     * the provided selector matches
     */
    Match nextUntil(String until);

    /**
     * Get all next siblings of every element in a set of matched elements until
     * the provided filter matches
     */
    Match nextUntil(Filter until);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a selector, until the provided selector matches
     */
    Match nextUntil(String until, String selector);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a filter, until the provided selector matches
     */
    Match nextUntil(String until, Filter filter);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a selector until the provided filter matches
     */
    Match nextUntil(Filter until, String selector);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a filter until the provided filter matches
     */
    Match nextUntil(Filter until, Filter filter);

    /**
     * Remove elements from the set of matched elements.
     */
    Match not(String selector);

    /**
     * Remove elements from the set of matched elements.
     */
    Match not(Filter filter);

    /**
     * Get the immediate parent elements of every element in a set of matched
     * elements
     */
    Match parent();

    /**
     * Get the immediate parent elements of every element in a set of matched
     * elements, matching a selector
     */
    Match parent(String selector);

    /**
     * Get the immediate parent elements of every element in a set of matched
     * elements, matching a filter
     */
    Match parent(Filter filter);

    /**
     * Get all ancestor elements of every element in a set of matched elements
     */
    Match parents();

    /**
     * Get all ancestor elements of every element in a set of matched elements,
     * matching a selector
     */
    Match parents(String selector);

    /**
     * Get all ancestor elements of every element in a set of matched elements,
     * matching a filter
     */
    Match parents(Filter filter);

    /**
     * Get all ancestors of every element in a set of matched elements until the
     * provided selector matches
     */
    Match parentsUntil(String until);

    /**
     * Get all ancestors of every element in a set of matched elements until the
     * provided filter matches
     */
    Match parentsUntil(Filter until);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a selector, until the provided selector matches
     */
    Match parentsUntil(String until, String selector);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a filter, until the provided selector matches
     */
    Match parentsUntil(String until, Filter filter);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a selector until the provided filter matches
     */
    Match parentsUntil(Filter until, String selector);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a filter until the provided filter matches
     */
    Match parentsUntil(Filter until, Filter filter);

    /**
     * Get the immediate previous sibling of every element in set of matched
     * elements.
     */
    Match prev();

    /**
     * Get the immediate previous sibling of every element in set of matched
     * elements, matching a selector
     */
    Match prev(String selector);

    /**
     * Get the immediate previous sibling of every element in set of matched
     * elements, matching a filter
     */
    Match prev(Filter filter);

    /**
     * Get all previous siblings of every element in a set of matched elements
     */
    Match prevAll();

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a selector
     */
    Match prevAll(String selector);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a filter
     */
    Match prevAll(Filter filter);

    /**
     * Get all previous siblings of every element in a set of matched elements
     * until the provided selector matches
     */
    Match prevUntil(String until);

    /**
     * Get all previous siblings of every element in a set of matched elements
     * until the provided filter matches
     */
    Match prevUntil(Filter until);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a selector, until the provided selector matches
     */
    Match prevUntil(String until, String selector);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a filter, until the provided selector matches
     */
    Match prevUntil(String until, Filter filter);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a selector until the provided filter matches
     */
    Match prevUntil(Filter until, String selector);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a filter until the provided filter matches
     */
    Match prevUntil(Filter until, Filter filter);

    /**
     * Get all siblings of every element in a set of matched elements
     */
    Match siblings();

    /**
     * Get all siblings of every element in a set of matched elements, matching
     * a selector
     */
    Match siblings(String selector);

    /**
     * Get all siblings of every element in a set of matched elements, matching
     * a filter
     */
    Match siblings(Filter filter);

    /**
     * Reduce the set of matched elements by specifying a range of indexes
     * <p>
     * This is the same as calling <code>slice(start, Integer.MAX_VALUE)</code>
     */
    Match slice(int start);

    /**
     * Reduce the set of matched elements by specifying a range of indexes
     */
    Match slice(int start, int end);

    // ---------------------------------------------------------------------
    // Manipulation of elements
    // ---------------------------------------------------------------------

    /**
     * Add content before each element in the set of matched elements.
     */
    Match before(String content);

    /**
     * Add content before each element in the set of matched elements.
     */
    Match before(Content content);

    /**
     * Add content before each element in the set of matched elements.
     * <p>
     * If the added content is already contained in this document, then it is
     * moved. Otherwise, it is cloned. If there are several elements in the set
     * of matched elements, then the added content is duplicated.
     */
    Match before(Match... content);

    /**
     * Add content before each element in the set of matched elements.
     * <p>
     * If the added content is already contained in this document, then it is
     * moved. Otherwise, it is cloned. If there are several elements in the set
     * of matched elements, then the added content is duplicated.
     */
    Match before(Element... content);

    /**
     * Add content after each element in the set of matched elements.
     */
    Match after(String content);

    /**
     * Add content after each element in the set of matched elements.
     */
    Match after(Content content);

    /**
     * Add content after each element in the set of matched elements.
     * <p>
     * If the added content is already contained in this document, then it is
     * moved. Otherwise, it is cloned. If there are several elements in the set
     * of matched elements, then the added content is duplicated.
     */
    Match after(Match... content);

    /**
     * Add content after each element in the set of matched elements.
     * <p>
     * If the added content is already contained in this document, then it is
     * moved. Otherwise, it is cloned. If there are several elements in the set
     * of matched elements, then the added content is duplicated.
     */
    Match after(Element... content);

    /**
     * Prepend content to the beginning of each element's content in the set of
     * matched elements.
     */
    Match prepend(String content);

    /**
     * Prepend content to the beginning of each element's content in the set of
     * matched elements.
     */
    Match prepend(Content content);

    /**
     * Prepend content to the beginning of each element's content in the set of
     * matched elements.
     * <p>
     * If the added content is already contained in this document, then it is
     * moved. Otherwise, it is cloned. If there are several elements in the set
     * of matched elements, then the added content is duplicated.
     */
    Match prepend(Match... content);

    /**
     * Prepend content to the beginning of each element's content in the set of
     * matched elements.
     * <p>
     * If the added content is already contained in this document, then it is
     * moved. Otherwise, it is cloned. If there are several elements in the set
     * of matched elements, then the added content is duplicated.
     */
    Match prepend(Element... content);

    /**
     * Append content to the end of each element's content in the set of matched
     * elements.
     */
    Match append(String content);

    /**
     * Append content to the end of each element's content in the set of matched
     * elements.
     */
    Match append(Content content);

    /**
     * Append content to the end of each element's content in the set of matched
     * elements.
     * <p>
     * If the added content is already contained in this document, then it is
     * moved. Otherwise, it is cloned. If there are several elements in the set
     * of matched elements, then the added content is duplicated.
     */
    Match append(Match... content);

    /**
     * Append content to the end of each element's content in the set of matched
     * elements.
     * <p>
     * If the added content is already contained in this document, then it is
     * moved. Otherwise, it is cloned. If there are several elements in the set
     * of matched elements, then the added content is duplicated.
     */
    Match append(Element... content);

    /**
     * Replace all elements in the set of matched elements with some new
     * content.
     */
    Match replaceWith(String content);

    /**
     * Replace all elements in the set of matched elements with some new
     * content.
     */
    Match replaceWith(Content content);

    /**
     * Replace all elements in the set of matched elements with some new
     * content.
     * <p>
     * If the added content is already contained in this document, then it is
     * moved. Otherwise, it is cloned. If there are several elements in the set
     * of matched elements, then the added content is duplicated.
     */
    Match replaceWith(Match... content);

    /**
     * Replace all elements in the set of matched elements with some new
     * content.
     * <p>
     * If the added content is already contained in this document, then it is
     * moved. Otherwise, it is cloned. If there are several elements in the set
     * of matched elements, then the added content is duplicated.
     */
    Match replaceWith(Element... content);

    /**
     * Removes all content from all elements in the set of matched elements.
     */
    Match empty();

    /**
     * Removes all elements in the set of matched elements.
     */
    Match remove();

    /**
     * Removes all elements in the set of matched elements, matching a selector
     */
    Match remove(String selector);

    /**
     * Removes all elements in the set of matched elements, matching a filter
     */
    Match remove(Filter filter);

    // ---------------------------------------------------------------------
    // Manipulation of attributes
    // ---------------------------------------------------------------------

    /**
     * Get the attribute <code>name</code> from the first element in the set of
     * matched elements, or <code>null</code> if the first element does not have
     * that attribute.
     */
    String attr(String name);

    /**
     * Get the attribute <code>name</code> from all elements in the set of
     * matched elements
     */
    List<String> attrs(String name);

    /**
     * Set an attribute on all elements in the set of matched elements. If
     * <code>value</code> is null, then the attribute is removed. If the
     * attribute already exists, then it is replaced.
     */
    Match attr(String name, String value);

    /**
     * Set an attribute on all elements in the set of matched elements. If
     * <code>value</code> returns null, then the attribute is removed. If the
     * attribute already exists, then it is replaced.
     */
    Match attr(String name, Content value);

    /**
     * Remove an attribute from all elements in the set of matched elements.
     * This is the same as calling <code>attr(name, null)</code>.
     */
    Match removeAttr(String name);

    // ---------------------------------------------------------------------
    // Manipulation of content
    // ---------------------------------------------------------------------

    /**
     * Get all XML content of the elements in the set of matched elements.
     */
    List<String> contents();

    /**
     * Get all XML content of the elements at given indexes in the set of
     * matched elements.
     */
    List<String> contents(int... indexes);

    /**
     * Get the XML content of the first element in the set of matched elements,
     * or <code>null</code> if there are no matched elements
     * <p>
     * This is the same as calling <code>content(0)</code>
     */
    String content();

    /**
     * Get the XML content at a given index in the current set of matched
     * elements.
     */
    String content(int index);

    /**
     * Add some XML content to all elements in the set of matched elements
     * (possibly replacing existing content). If the supplied content is invalid
     * XML or plain text, then it will be added as text just as with
     * {@link #text(String)}
     */
    Match content(String content);

    /**
     * Add some XML content to all elements in the set of matched elements
     * (possibly replacing existing content). If the supplied content is invalid
     * XML or plain text, then it will be added as text just as with
     * {@link #text(String)}
     */
    Match content(Content content);

    /**
     * Get all text content of the elements in the set of matched elements.
     */
    List<String> texts();

    /**
     * Get all text content of the elements at given indexes in the set of
     * matched elements.
     */
    List<String> texts(int... indexes);

    /**
     * Get the text content of the first element in the set of matched elements,
     * or <code>null</code> if there are no matched elements.
     * <p>
     * This is the same as calling <code>text(0)</code>
     */
    String text();

    /**
     * Get the text content at a given index in the current set of matched
     * elements.
     */
    String text(int index);

    /**
     * Set some text content to all elements in the set of matched elements
     * (possibly replacing existing content).
     */
    Match text(String content);

    /**
     * Set some text content to all elements in the set of matched elements
     * (possibly replacing existing content).
     */
    Match text(Content content);

    // ---------------------------------------------------------------------
    // Convenience
    // ---------------------------------------------------------------------

    /**
     * Get a copy of the {@link Match} wrapper. This is not a deep-copy of
     * wrapped {@link Element} objects. Both this and the copy will reference
     * the same <code>Element</code>'s
     */
    Match copy();

    /**
     * Get a list of XPath expressions describing the elements in the current
     * set of matched elements
     */
    List<String> xpaths();

    /**
     * Get a list of XPath expressions describing the elements at the given
     * indexes in the current set of matched elements
     */
    List<String> xpaths(int... indexes);

    /**
     * Get an XPath expression describing the first element in the current set
     * of matched elements
     * <p>
     * This is the same as calling <code>xpath(0)</code>
     */
    String xpath();

    /**
     * Get an XPath expression describing the element at a given index in the
     * current set of matched elements
     */
    String xpath(int index);

    /**
     * Get a list of tag names of the elements in the current set of matched
     * elements.
     */
    List<String> tags();

    /**
     * Get a list of tag names of the elements at given indexes in the current
     * set of matched elements.
     */
    List<String> tags(int... indexes);

    /**
     * Get the tag name of the first element in the current set of matched
     * elements.
     * <p>
     * This is the same as calling <code>tag(0)</code>
     */
    String tag();

    /**
     * Get a tag name of the element at a given index in the current set of
     * matched elements.
     */
    String tag(int index);

    /**
     * Get a list of id values in the current set of matched elements.
     * <p>
     * This is the same as calling <code>attrs("id")</code>
     */
    List<String> ids();

    /**
     * Get a list of id values at given indexes in the current set of matched
     * elements.
     */
    List<String> ids(int... indexes);

    /**
     * Get the first id value
     * <p>
     * This is the same as calling <code>id(0)</code>
     */
    String id();

    /**
     * Get a list of id values in the current set of matched elements.
     * <p>
     * This is the same as calling <code>eq(index).attr("id")</code>
     */
    String id(int index);
}
