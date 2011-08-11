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
 * contained elements. The API has been inspired by <a
 * href="http://jquery.com">http://jquery.com</a>, a fantastic DOM abstraction
 * library for JavaScript.
 *
 * @author Lukas Eder
 * @see <a href="http://jquery.com">http://jquery.com</a>
 */
public interface X extends Iterable<Element> {

    // ---------------------------------------------------------------------
    // DOM access
    // ---------------------------------------------------------------------

    /**
     * Get an element from the set of matched elements at a given index
     */
    Element get(int index);

    /**
     * Get an the set of matched elements
     */
    List<Element> get();

    /**
     * Get the number of matched elements in the set of matched elements
     */
    int size();

    // ---------------------------------------------------------------------
    // Traversing
    // ---------------------------------------------------------------------

    /**
     * Add some elements to the set of matched elements
     */
    X add(Element... elements);

    /**
     * Add some elements to the set of matched elements
     */
    X add(X... elements);

    /**
     * Find all children of each element in the current set of matched elements.
     */
    X children();

    /**
     * Find all children of each element in the current set of matched elements.
     */
    X children(String selector);

    /**
     * Find all children of each element in the current set of matched elements.
     */
    X children(Filter filter);

    /**
     * Execute a callback for every element in the current set of matched
     * elements.
     */
    X each(Each each);

    /**
     * Reduce the current set of matched elements.
     */
    X filter(String selector);

    /**
     * Reduce the current set of matched elements.
     */
    X filter(Filter filter);

    /**
     * Reduce the current set of matched elements to the element at a given
     * index
     */
    X eq(int index);

    /**
     * Find all descendants of each element in the current set of matched
     * elements.
     */
    X find();

    /**
     * Find all descendants of each element in the current set of matched
     * elements.
     */
    X find(String selector);

    /**
     * Find all descendants of each element in the current set of matched
     * elements.
     */
    X find(Filter filter);

    /**
     * Get the first in a set of matched elements.
     */
    X first();

    /**
     * Reduce the set of matched element to those who have a descendant that
     * matches a selector.
     */
    X has(String selector);

    /**
     * Reduce the set of matched element to those who have a descendant that
     * matches a filter.
     */
    X has(Filter filter);

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
    X last();

    /**
     * Map the set of matched elements to a list of something
     */
    <E> List<E> map(Mapper<E> map);

    /**
     * Get the immediate next sibling of every element in set of matched
     * elements.
     */
    X next();

    /**
     * Get the immediate next sibling of every element in set of matched
     * elements, matching a selector
     */
    X next(String selector);

    /**
     * Get the immediate next sibling of every element in set of matched
     * elements, matching a filter
     */
    X next(Filter filter);

    /**
     * Get all next siblings of every element in a set of matched elements
     */
    X nextAll();

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a selector
     */
    X nextAll(String selector);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a filter
     */
    X nextAll(Filter filter);

    /**
     * Get all next siblings of every element in a set of matched elements until
     * the provided selector matches
     */
    X nextUntil(String until);

    /**
     * Get all next siblings of every element in a set of matched elements until
     * the provided filter matches
     */
    X nextUntil(Filter until);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a selector, until the provided selector matches
     */
    X nextUntil(String until, String selector);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a filter, until the provided selector matches
     */
    X nextUntil(String until, Filter filter);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a selector until the provided filter matches
     */
    X nextUntil(Filter until, String selector);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a filter until the provided filter matches
     */
    X nextUntil(Filter until, Filter filter);

    /**
     * Remove elements from the set of matched elements.
     */
    X not(String selector);

    /**
     * Remove elements from the set of matched elements.
     */
    X not(Filter filter);

    /**
     * Get the immediate parent elements of every element in a set of matched
     * elements
     */
    X parent();

    /**
     * Get the immediate parent elements of every element in a set of matched
     * elements, matching a selector
     */
    X parent(String selector);

    /**
     * Get the immediate parent elements of every element in a set of matched
     * elements, matching a filter
     */
    X parent(Filter filter);

    /**
     * Get all ancestor elements of every element in a set of matched elements
     */
    X parents();

    /**
     * Get all ancestor elements of every element in a set of matched elements,
     * matching a selector
     */
    X parents(String selector);

    /**
     * Get all ancestor elements of every element in a set of matched elements,
     * matching a filter
     */
    X parents(Filter filter);

    /**
     * Get all ancestors of every element in a set of matched elements until the
     * provided selector matches
     */
    X parentsUntil(String until);

    /**
     * Get all ancestors of every element in a set of matched elements until the
     * provided filter matches
     */
    X parentsUntil(Filter until);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a selector, until the provided selector matches
     */
    X parentsUntil(String until, String selector);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a filter, until the provided selector matches
     */
    X parentsUntil(String until, Filter filter);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a selector until the provided filter matches
     */
    X parentsUntil(Filter until, String selector);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a filter until the provided filter matches
     */
    X parentsUntil(Filter until, Filter filter);

    /**
     * Get the immediate previous sibling of every element in set of matched
     * elements.
     */
    X prev();

    /**
     * Get the immediate previous sibling of every element in set of matched
     * elements, matching a selector
     */
    X prev(String selector);

    /**
     * Get the immediate previous sibling of every element in set of matched
     * elements, matching a filter
     */
    X prev(Filter filter);

    /**
     * Get all previous siblings of every element in a set of matched elements
     */
    X prevAll();

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a selector
     */
    X prevAll(String selector);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a filter
     */
    X prevAll(Filter filter);

    /**
     * Get all previous siblings of every element in a set of matched elements
     * until the provided selector matches
     */
    X prevUntil(String until);

    /**
     * Get all previous siblings of every element in a set of matched elements
     * until the provided filter matches
     */
    X prevUntil(Filter until);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a selector, until the provided selector matches
     */
    X prevUntil(String until, String selector);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a filter, until the provided selector matches
     */
    X prevUntil(String until, Filter filter);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a selector until the provided filter matches
     */
    X prevUntil(Filter until, String selector);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a filter until the provided filter matches
     */
    X prevUntil(Filter until, Filter filter);

    /**
     * Get all siblings of every element in a set of matched elements
     */
    X siblings();

    /**
     * Get all siblings of every element in a set of matched elements, matching
     * a selector
     */
    X siblings(String selector);

    /**
     * Get all siblings of every element in a set of matched elements, matching
     * a filter
     */
    X siblings(Filter filter);

    /**
     * Reduce the set of matched elements by specifying a range of indexes
     * <p>
     * This is the same as calling <code>slice(start, Integer.MAX_VALUE)</code>
     */
    X slice(int start);

    /**
     * Reduce the set of matched elements by specifying a range of indexes
     */
    X slice(int start, int end);

    // ---------------------------------------------------------------------
    // Manipulation of elements
    // ---------------------------------------------------------------------

    /**
     * Add content before each element in the set of matched elements.
     */
    X before(String content);

    /**
     * Add content before each element in the set of matched elements.
     */
    X before(Content content);

    /**
     * Add content after each element in the set of matched elements.
     */
    X after(String content);

    /**
     * Add content after each element in the set of matched elements.
     */
    X after(Content content);

    /**
     * Prepend content to the beginning of each element's content in the set of
     * matched elements.
     */
    X prepend(String content);

    /**
     * Prepend content to the beginning of each element's content in the set of
     * matched elements.
     */
    X prepend(Content content);

    /**
     * Append content to the end of each element's content in the set of matched
     * elements.
     */
    X append(String content);

    /**
     * Append content to the end of each element's content in the set of matched
     * elements.
     */
    X append(Content content);

    /**
     * Replace all elements in the set of matched elements with some new
     * content.
     */
    X replaceWith(String content);

    /**
     * Replace all elements in the set of matched elements with some new
     * content.
     */
    X replaceWith(Content content);

    /**
     * Removes all content from all elements in the set of matched elements.
     */
    X empty();

    /**
     * Removes all elements in the set of matched elements.
     */
    X remove();

    /**
     * Removes all elements in the set of matched elements, matching a selector
     */
    X remove(String selector);

    /**
     * Removes all elements in the set of matched elements, matching a filter
     */
    X remove(Filter filter);

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
    X attr(String name, String value);

    /**
     * Set an attribute on all elements in the set of matched elements. If
     * <code>value</code> returns null, then the attribute is removed. If the
     * attribute already exists, then it is replaced.
     */
    X attr(String name, Content value);

    /**
     * Remove an attribute from all elements in the set of matched elements.
     * This is the same as calling <code>attr(name, null)</code>.
     */
    X removeAttr(String name);

    // ---------------------------------------------------------------------
    // Manipulation of content
    // ---------------------------------------------------------------------

    /**
     * Get the XML content of the first element in the set of matched elements,
     * or <code>null</code> if there are no matched elements
     */
    String content();

    /**
     * Get all XML content of the elements in the set of matched elements.
     */
    List<String> contents();

    /**
     * Add some XML content to all elements in the set of matched elements
     * (possibly replacing existing content). If the supplied content is invalid
     * XML or plain text, then it will be added as text just as with
     * {@link #text(String)}
     */
    X content(String content);

    /**
     * Add some XML content to all elements in the set of matched elements
     * (possibly replacing existing content). If the supplied content is invalid
     * XML or plain text, then it will be added as text just as with
     * {@link #text(String)}
     */
    X content(Content content);

    /**
     * Get the text content of the first element in the set of matched elements,
     * or <code>null</code> if there are no matched elements.
     */
    String text();

    /**
     * Get all text content of the elements in the set of matched elements.
     */
    List<String> texts();

    /**
     * Set some text content to all elements in the set of matched elements
     * (possibly replacing existing content).
     */
    X text(String content);

    /**
     * Set some text content to all elements in the set of matched elements
     * (possibly replacing existing content).
     */
    X text(Content content);

    // ---------------------------------------------------------------------
    // Utility
    // ---------------------------------------------------------------------

    /**
     * Get a copy of the {@link X} wrapper. This is not a deep-copy of wrapped
     * {@link Element} objects. Both this and the copy will reference the same
     * <code>Element</code>'s
     */
    X copy();

    /**
     * Get the first tag name in the current set of matched elements.
     * <p>
     * This is the same as calling <code>tag(0)</code>
     */
    String tag();

    /**
     * Get a list of tag names in the current set of matched elements.
     */
    String tag(int index);

    /**
     * Get a list of tag names in the current set of matched elements.
     */
    List<String> tags();

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

    /**
     * Get a list of id values in the current set of matched elements.
     * <p>
     * This is the same as calling <code>attrs("id")</code>
     */
    List<String> ids();
}
