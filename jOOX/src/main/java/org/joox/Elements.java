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
 * TODO selectors currently only select tag names!
 *
 * @author Lukas Eder
 */
public interface Elements extends Iterable<Element> {

    // -------------------------------------------------------------------------
    // DOM access
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Traversing
    // -------------------------------------------------------------------------

    /**
     * Add some elements to the set of matched elements
     */
    Elements add(Element... elements);

    /**
     * Add some elements to the set of matched elements
     */
    Elements add(Elements... elements);

    /**
     * Find all children of each element in the current set of matched elements.
     */
    Elements children();

    /**
     * Find all children of each element in the current set of matched elements.
     */
    Elements children(String selector);

    /**
     * Find all children of each element in the current set of matched elements.
     */
    Elements children(Filter filter);

    /**
     * Execute a callback for every element in the current set of matched
     * elements.
     */
    Elements each(Each each);

    /**
     * Reduce the current set of matched elements.
     */
    Elements filter(String selector);

    /**
     * Reduce the current set of matched elements.
     */
    Elements filter(Filter filter);

    /**
     * Reduce the current set of matched elements to the element at a given
     * index
     */
    Elements eq(int index);

    /**
     * Find all descendants of each element in the current set of matched
     * elements.
     */
    Elements find();

    /**
     * Find all descendants of each element in the current set of matched
     * elements.
     */
    Elements find(String selector);

    /**
     * Find all descendants of each element in the current set of matched
     * elements.
     */
    Elements find(Filter filter);

    /**
     * Get the first in a set of matched elements.
     */
    Elements first();

    /**
     * Reduce the set of matched element to those who have a descendant that
     * matches a selector.
     */
    Elements has(String selector);

    /**
     * Reduce the set of matched element to those who have a descendant that
     * matches a filter.
     */
    Elements has(Filter filter);

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
    Elements last();

    /**
     * Map the set of matched elements to a list of something
     */
    <E> List<E> map(Mapper<E> map);

    /**
     * Get the immediate next sibling of every element in set of matched
     * elements.
     */
    Elements next();

    /**
     * Get the immediate next sibling of every element in set of matched
     * elements, matching a selector
     */
    Elements next(String selector);

    /**
     * Get the immediate next sibling of every element in set of matched
     * elements, matching a filter
     */
    Elements next(Filter filter);

    /**
     * Get all next siblings of every element in a set of matched elements
     */
    Elements nextAll();

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a selector
     */
    Elements nextAll(String selector);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a filter
     */
    Elements nextAll(Filter filter);

    /**
     * Get all next siblings of every element in a set of matched elements until
     * the provided selector matches
     */
    Elements nextUntil(String until);

    /**
     * Get all next siblings of every element in a set of matched elements until
     * the provided filter matches
     */
    Elements nextUntil(Filter until);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a selector, until the provided selector matches
     */
    Elements nextUntil(String until, String selector);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a filter, until the provided selector matches
     */
    Elements nextUntil(String until, Filter filter);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a selector until the provided filter matches
     */
    Elements nextUntil(Filter until, String selector);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a filter until the provided filter matches
     */
    Elements nextUntil(Filter until, Filter filter);

    /**
     * Remove elements from the set of matched elements.
     */
    Elements not(String selector);

    /**
     * Remove elements from the set of matched elements.
     */
    Elements not(Filter filter);

    /**
     * Get the immediate parent elements of every element in a set of matched
     * elements
     */
    Elements parent();

    /**
     * Get the immediate parent elements of every element in a set of matched
     * elements, matching a selector
     */
    Elements parent(String selector);

    /**
     * Get the immediate parent elements of every element in a set of matched
     * elements, matching a filter
     */
    Elements parent(Filter filter);

    /**
     * Get all ancestor elements of every element in a set of matched elements
     */
    Elements parents();

    /**
     * Get all ancestor elements of every element in a set of matched elements,
     * matching a selector
     */
    Elements parents(String selector);

    /**
     * Get all ancestor elements of every element in a set of matched elements,
     * matching a filter
     */
    Elements parents(Filter filter);

    /**
     * Get all ancestors of every element in a set of matched elements until the
     * provided selector matches
     */
    Elements parentsUntil(String until);

    /**
     * Get all ancestors of every element in a set of matched elements until the
     * provided filter matches
     */
    Elements parentsUntil(Filter until);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a selector, until the provided selector matches
     */
    Elements parentsUntil(String until, String selector);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a filter, until the provided selector matches
     */
    Elements parentsUntil(String until, Filter filter);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a selector until the provided filter matches
     */
    Elements parentsUntil(Filter until, String selector);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a filter until the provided filter matches
     */
    Elements parentsUntil(Filter until, Filter filter);

    /**
     * Get the immediate previous sibling of every element in set of matched
     * elements.
     */
    Elements prev();

    /**
     * Get the immediate previous sibling of every element in set of matched
     * elements, matching a selector
     */
    Elements prev(String selector);

    /**
     * Get the immediate previous sibling of every element in set of matched
     * elements, matching a filter
     */
    Elements prev(Filter filter);

    /**
     * Get all previous siblings of every element in a set of matched elements
     */
    Elements prevAll();

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a selector
     */
    Elements prevAll(String selector);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a filter
     */
    Elements prevAll(Filter filter);

    /**
     * Get all previous siblings of every element in a set of matched elements
     * until the provided selector matches
     */
    Elements prevUntil(String until);

    /**
     * Get all previous siblings of every element in a set of matched elements
     * until the provided filter matches
     */
    Elements prevUntil(Filter until);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a selector, until the provided selector matches
     */
    Elements prevUntil(String until, String selector);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a filter, until the provided selector matches
     */
    Elements prevUntil(String until, Filter filter);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a selector until the provided filter matches
     */
    Elements prevUntil(Filter until, String selector);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a filter until the provided filter matches
     */
    Elements prevUntil(Filter until, Filter filter);

    /**
     * Get all siblings of every element in a set of matched elements
     */
    Elements siblings();

    /**
     * Get all siblings of every element in a set of matched elements, matching
     * a selector
     */
    Elements siblings(String selector);

    /**
     * Get all siblings of every element in a set of matched elements, matching
     * a filter
     */
    Elements siblings(Filter filter);

    /**
     * Reduce the set of matched elements by specifying a range of indexes
     * <p>
     * This is the same as calling <code>slice(start, Integer.MAX_VALUE)</code>
     */
    Elements slice(int start);

    /**
     * Reduce the set of matched elements by specifying a range of indexes
     */
    Elements slice(int start, int end);

    // -------------------------------------------------------------------------
    // Manipulation of elements
    // -------------------------------------------------------------------------

    /**
     * Add content before each element in the set of matched elements.
     */
    Elements before(String content);

    /**
     * Add content before each element in the set of matched elements.
     */
    Elements before(Content content);

    /**
     * Add content after each element in the set of matched elements.
     */
    Elements after(String content);

    /**
     * Add content after each element in the set of matched elements.
     */
    Elements after(Content content);

    /**
     * Prepend content to the beginning of each element's content in the set of
     * matched elements.
     */
    Elements prepend(String content);

    /**
     * Prepend content to the beginning of each element's content in the set of
     * matched elements.
     */
    Elements prepend(Content content);

    /**
     * Append content to the end of each element's content in the set of matched
     * elements.
     */
    Elements append(String content);

    /**
     * Append content to the end of each element's content in the set of matched
     * elements.
     */
    Elements append(Content content);

    /**
     * Replace all elements in the set of matched elements with some new
     * content.
     */
    Elements replaceWith(String content);

    /**
     * Replace all elements in the set of matched elements with some new
     * content.
     */
    Elements replaceWith(Content content);

    /**
     * Removes all content from all elements in the set of matched elements.
     */
    Elements empty();

    /**
     * Removes all elements in the set of matched elements.
     */
    Elements remove();

    /**
     * Removes all elements in the set of matched elements, matching a selector
     */
    Elements remove(String selector);

    /**
     * Removes all elements in the set of matched elements, matching a filter
     */
    Elements remove(Filter filter);

    // -------------------------------------------------------------------------
    // Manipulation of attributes
    // -------------------------------------------------------------------------

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
    Elements attr(String name, String value);

    /**
     * Set an attribute on all elements in the set of matched elements. If
     * <code>value</code> returns null, then the attribute is removed. If the
     * attribute already exists, then it is replaced.
     */
    Elements attr(String name, Content value);

    /**
     * Remove an attribute from all elements in the set of matched elements.
     * This is the same as calling <code>attr(name, null)</code>.
     */
    Elements removeAttr(String name);

    // -------------------------------------------------------------------------
    // Manipulation of content
    // -------------------------------------------------------------------------

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
    Elements content(String content);

    /**
     * Add some XML content to all elements in the set of matched elements
     * (possibly replacing existing content). If the supplied content is invalid
     * XML or plain text, then it will be added as text just as with
     * {@link #text(String)}
     */
    Elements content(Content content);

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
    Elements text(String content);

    /**
     * Set some text content to all elements in the set of matched elements
     * (possibly replacing existing content).
     */
    Elements text(Content content);

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /**
     * Get a copy of the {@link Elements} wrapper. This is not a deep-copy of
     * wrapped {@link Element} objects. Both this and the copy will reference
     * the same <code>Element</code>'s
     */
    Elements copy();

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
