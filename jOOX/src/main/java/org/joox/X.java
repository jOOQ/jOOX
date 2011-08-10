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
public interface X extends Iterable<Element> {

    // -------------------------------------------------------------------------
    // DOM access
    // -------------------------------------------------------------------------

    Element get(int index);

    List<Element> get();

    int index();

    int index(String selector);

    int index(Element element);

    int index(X element);

    int size();

    // -------------------------------------------------------------------------
    // Traversing
    // -------------------------------------------------------------------------

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

    X nextUntil(String selector);

    X nextUntil(String selector, Filter filter);

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

    X parentsUntil(String selector);

    X parentsUntil(String selector, Filter filter);

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

    X prevUntil(String selector);

    X prevUntil(String selector, Filter filter);

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

    // -------------------------------------------------------------------------
    // Manipulation
    // -------------------------------------------------------------------------

    X after(String... content);

    X after(Element... elements);

    X after(X... elements);

    X after(XContent content);

    X append(String... content);

    X append(Element... elements);

    X append(X... elements);

    X append(XContent content);

    X appendTo(String selector);

    X appendTo(Element element);

    X appendTo(X element);

    String attr(String name);

    X attr(String name, String value);

    X attr(String name, XContent value);

    X before(String... content);

    X before(Element... elements);

    X before(X... elements);

    X before(XContent content);

    X empty();

    String content();

    X content(String content);

    X content(XContent content);

    String text();

    X text(String content);

    X text(XContent content);

    X insertAfter(String... content);

    X insertAfter(Element... elements);

    X insertAfter(X... elements);

    X insertAfter(XContent content);

    X insertBefore(String... content);

    X insertBefore(Element... elements);

    X insertBefore(X... elements);

    X insertBefore(XContent content);

    X prepend(String... content);

    X prepend(Element... elements);

    X prepend(X... elements);

    X prepend(XContent content);

    X prependTo(String selector);

    X prependTo(Element element);

    X prependTo(X element);

    X remove();

    X remove(String selector);

    X removeAttr(String name);

    X replaceAll(String selector);

    X replaceAll(Element... elements);

    X replaceAll(X... elements);

    X replaceWith(String content);

    X replaceWith(Element... elements);

    X replaceWith(X... elements);

    X replaceWith(XContent content);

    X unwrap();

    X wrap(String content);

    X wrap(Element element);

    X wrap(X element);

    X wrap(XContent content);

    X wrapAll(String content);

    X wrapAll(Element element);

    X wrapAll(X element);

    X wrapInner(String content);

    X wrapInner(Element element);

    X wrapInner(X element);

    X wrapInner(XContent content);

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    X copy();

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
