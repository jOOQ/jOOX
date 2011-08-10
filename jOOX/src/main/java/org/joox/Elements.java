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

    int index();

    int index(String selector);

    int index(Element element);

    int index(Elements element);

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
    // Manipulation
    // -------------------------------------------------------------------------

    Elements after(String... content);

    Elements after(Element... elements);

    Elements after(Elements... elements);

    Elements after(Content content);

    Elements append(String... content);

    Elements append(Element... elements);

    Elements append(Elements... elements);

    Elements append(Content content);

    Elements appendTo(String selector);

    Elements appendTo(Element element);

    Elements appendTo(Elements element);

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

    Elements before(String... content);

    Elements before(Element... elements);

    Elements before(Elements... elements);

    Elements before(Content content);

    /**
     * Removes all content from all elements in the set of matched elements.
     */
    Elements empty();

    String content();

    Elements content(String content);

    Elements content(Content content);

    /**
     * Get the text content of the first element in the set of matched elements,
     * or <code>null</code> if there are no matched elements
     */
    String text();

    /**
     * Get all text content of the elements in the set of matched elements.
     */
    List<String> texts();

    Elements text(String content);

    Elements text(Content content);

    Elements insertAfter(String... content);

    Elements insertAfter(Element... elements);

    Elements insertAfter(Elements... elements);

    Elements insertAfter(Content content);

    Elements insertBefore(String... content);

    Elements insertBefore(Element... elements);

    Elements insertBefore(Elements... elements);

    Elements insertBefore(Content content);

    Elements prepend(String... content);

    Elements prepend(Element... elements);

    Elements prepend(Elements... elements);

    Elements prepend(Content content);

    Elements prependTo(String selector);

    Elements prependTo(Element element);

    Elements prependTo(Elements element);

    Elements remove();

    Elements remove(String selector);

    Elements replaceAll(String selector);

    Elements replaceAll(Element... elements);

    Elements replaceAll(Elements... elements);

    Elements replaceWith(String content);

    Elements replaceWith(Element... elements);

    Elements replaceWith(Elements... elements);

    Elements replaceWith(Content content);

    Elements unwrap();

    Elements wrap(String content);

    Elements wrap(Element element);

    Elements wrap(Elements element);

    Elements wrap(Content content);

    Elements wrapAll(String content);

    Elements wrapAll(Element element);

    Elements wrapAll(Elements element);

    Elements wrapInner(String content);

    Elements wrapInner(Element element);

    Elements wrapInner(Elements element);

    Elements wrapInner(Content content);

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    Elements copy();

    /**
     * Get the first tag name
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
}
