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
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;

import org.joox.selector.CSS2XPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
    // Namespace configuration
    // ---------------------------------------------------------------------

    /**
     * Get a new Match with added namespace configuration for subsequent XPath
     * calls
     * <p>
     * This is a convenience method for {@link #namespaces(java.util.Map)}
     *
     * @param namespacePrefix A namespace prefix
     * @param namespaceURI A namespace URI
     * @return A modified <code>Match</code>
     * @see #namespaces(Map)
     */
    Match namespace(String namespacePrefix, String namespaceURI);

    /**
     * Get a new Match with added namespace configuration for subsequent XPath
     * calls
     *
     * @param map A mapping between prefix and namespace URI
     * @return A modified <code>Match</code>
     */
    Match namespaces(Map<String, String> map);

    /**
     * Get a list of namespace URIs of the elements in the current set of
     * matched elements.
     * <p>
     * This only works if the underlying document is namespace-aware
     *
     * @see Node#getNamespaceURI()
     */
    List<String> namespaceURIs();

    /**
     * Get a list of namespace URIs of the elements at given indexes in the
     * current set of matched elements.
     * <p>
     * This only works if the underlying document is namespace-aware
     *
     * @see Node#getNamespaceURI()
     */
    List<String> namespaceURIs(int... indexes);

    /**
     * Get the namespace URI of the first element in the current set of matched
     * elements.
     * <p>
     * This is the same as calling <code>namespaceURI(0)</code>
     * <p>
     * This only works if the underlying document is namespace-aware
     *
     * @see Node#getNamespaceURI()
     */
    String namespaceURI();

    /**
     * Get a namespace URI of the element at a given index in the current set of
     * matched elements.
     * <p>
     * This only works if the underlying document is namespace-aware
     *
     * @see Node#getNamespaceURI()
     */
    String namespaceURI(int index);

    /**
     * Get a list of namespace prefixes of the elements in the current set of
     * matched elements.
     */
    List<String> namespacePrefixes();

    /**
     * Get a list of namespace prefixes of the elements at given indexes in the
     * current set of matched elements.
     */
    List<String> namespacePrefixes(int... indexes);

    /**
     * Get the namespace prefix of the first element in the current set of
     * matched elements.
     * <p>
     * This is the same as calling <code>namespaceURI(0)</code>
     */
    String namespacePrefix();

    /**
     * Get a namespace prefix of the element at a given index in the current set
     * of matched elements.
     */
    String namespacePrefix(int index);

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
     * Get the underlying document of the set of matched elements.
     * <p>
     * This will also return a document if there are no elements in the set of
     * matched elements, either because a new document has been created
     * previously, or the set of matched elements has been reduced to an empty
     * set.
     */
    Document document();

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

    /**
     * Whether there are any matched elements in the set of matched elements
     */
    boolean isNotEmpty();

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
     * Reverse the order of the set of matched elements
     */
    Match reverse();

    /**
     * Add the previous set of matched elements to the current one. This works
     * after any of these methods (including all overloaded variants):
     * <ul>
     * <li>{@link #child()}</li>
     * <li>{@link #children()}</li>
     * <li>{@link #find()}</li>
     * <li>{@link #next()}</li>
     * <li>{@link #nextAll()}</li>
     * <li>{@link #nextUntil(Filter)}</li>
     * <li>{@link #parent()}</li>
     * <li>{@link #parents()}</li>
     * <li>{@link #parentsUntil(Filter)}</li>
     * <li>{@link #prev()}</li>
     * <li>{@link #prevAll()}</li>
     * <li>{@link #prevUntil(Filter)}</li>
     * <li>{@link #siblings()}</li>
     * </ul>
     * In all other cases, this just returns the same match this was called
     * upon. For instance, it does not make sense to first reduce a set of
     * matched elements using {@link #eq(int...)}, and then add the removed
     * elements again, using {@link #andSelf()}.
     */
    Match andSelf();

    /**
     * Find the first child of each element in the current set of matched
     * elements.
     * <p>
     * This is the same as calling <code>child(0)</code>.
     */
    Match child();

    /**
     * Find the first matching child of each element in the current set of
     * matched elements
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match child(String selector);

    /**
     * Find the first matching child of each element in the current set of
     * matched elements
     */
    Match child(Filter filter);

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
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match children(String selector);

    /**
     * Find all children of each element in the current set of matched elements.
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose children are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * children are searched</li>
     * <li> {@link Context#element()} - the child candidate that is being
     * filtered</li>
     * <li> {@link Context#elementIndex()} - the index within its parent of the
     * child candidate that is being filtered</li>
     * </ul>
     */
    Match children(Filter filter);

    /**
     * Find all children of each element at given indexes in the current set of
     * matched elements.
     */
    Match children(int... indexes);

    /**
     * Get all elements in the set of matched elements in a list of matches,
     * every match representing one element
     */
    List<Match> each();

    /**
     * Execute a callback for every element in the current set of matched
     * elements.
     */
    Match each(Each each);

    /**
     * Execute several callbacks for every element in the current set of matched
     * elements.
     *
     * @see JOOX#chain(Each...)
     */
    Match each(Each... each);

    /**
     * Execute several callbacks for every element in the current set of matched
     * elements.
     *
     * @see JOOX#chain(Iterable)
     */
    Match each(Iterable<? extends Each> each);

    /**
     * Reduce the current set of matched elements.
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match filter(String selector);

    /**
     * Reduce the current set of matched elements.
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element being filtered</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element being
     * filtered</li>
     * </ul>
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
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)} Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * <li><strong>CSS selectors</strong> can be used to select XML elements
     * using XPath (see {@link CSS2XPath#css2xpath(String)})</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match find(String selector);

    /**
     * Find all descendants of each element in the current set of matched
     * elements.
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose descendants are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * descendants are searched</li>
     * <li> {@link Context#element()} - the descendant candidate that is being
     * filtered</li>
     * <li> {@link Context#elementIndex()} - the iteration index of the
     * descendant candidate that is being filtered</li>
     * </ul>
     */
    Match find(Filter filter);

    /**
     * Match all elements given a certain XPath expression applied to each
     * element in the current set of matched elements.
     * <p>
     * The XPath expression is evaluated using standard
     * {@link javax.xml.xpath.XPath}. It must not contain any variables. Use
     * {@link #xpath(String, Object...)} instead, if you wish to use variables.
     * Note that only matched elements will be considered in the results. You
     * cannot match attributes or text nodes, for instance. Examples:
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
     * <p>
     * <h5>Using jOOX with Namespaces</h5>
     * <p>
     * Namespace declarations are supported in XPath expressions. If you wish to
     * use namespace-specific XPath elements, call
     * {@link #namespace(String, String)} prior to {@link #xpath(String)}
     * <p>
     * <h5>Using jOOX with Xalan</h5>
     * <p>
     * If Xalan is on your classpath, jOOX will automatically load xalan's
     * namespace and function extensions. All functionality supported by <a
     * href="http://exslt.org">http://exslt.org</a> will be available in your
     * XPath expressions. Some examples:
     * <ul>
     * <li>Match the book with the highest ID :
     * <code>xpath("//book[number(@id) = math:max(//book/@id)]")</code></li>
     * <li>Match books written by Orwell :
     * <code>xpath("//book[java:org.joox.test.Functions.byOrwellWithNodes(.)]</code>
     * </li>
     * </ul>
     */
    Match xpath(String expression);

    /**
     * Match all elements given a certain XPath expression applied to each
     * element in the current set of matched elements.
     * <p>
     * The XPath expression is evaluated using standard
     * {@link javax.xml.xpath.XPath}. It may contain numerical variables,
     * declared as <code>$1</code>, <code>$2</code>, etc, starting with
     * <code>$1</code>. Other variables, such as <code>$myVar</code> are not
     * supported. You must provide at least one variable in the
     * <code>variables</code> argument for every variable index. Note that only
     * matched elements will be considered in the results. You cannot match
     * attributes or text nodes, for instance. Examples:
     * <ul>
     * <li>Match all elements with id greater than 5:
     * <code>xpath("//*[@id > $1]", 5)</code></li>
     * <li>Match all books with more than two authors and one author is
     * "George Orwell" :
     * <code>xpath("/library/books/book[count(authors/author) > $1][authors/author[text() = $2]]", 2, "George Orwell")</code>
     * </li>
     * </ul>
     * This doesn't work (not matching elements):
     * <ul>
     * <li>Match all book ID's : <code>xpath("//book/@id")</code></li>
     * <li>Match all book names : <code>xpath("//book/name/text()")</code></li>
     * </ul>
     * <p>
     * <h5>Using jOOX with Namespaces</h5>
     * <p>
     * Namespace declarations are supported in XPath expressions. If you wish to
     * use namespace-specific XPath elements, call
     * {@link #namespace(String, String)} prior to {@link #xpath(String)}
     * <p>
     * <h5>Using jOOX with Xalan</h5>
     * <p>
     * If Xalan is on your classpath, jOOX will automatically load xalan's
     * namespace and function extensions. All functionality supported by <a
     * href="http://exslt.org">http://exslt.org</a> will be available in your
     * XPath expressions. Some examples:
     * <ul>
     * <li>Match the book with the highest ID :
     * <code>xpath("//book[number(@id) = math:max(//book/@id)]")</code></li>
     * <li>Match books written by Orwell :
     * <code>xpath("//book[java:org.joox.test.Functions.byOrwellWithNodes(.)]</code>
     * </li>
     * </ul>
     */
    Match xpath(String expression, Object... variables);

    /**
     * Get the first in a set of matched elements.
     */
    Match first();

    /**
     * Reduce the set of matched element to those who have a descendant that
     * matches a selector.
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match has(String selector);

    /**
     * Reduce the set of matched element to those who have a descendant that
     * matches a filter.
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose descendants are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * descendants are searched</li>
     * <li> {@link Context#element()} - the descendant candidate that is being
     * filtered</li>
     * <li> {@link Context#elementIndex()} - the iteration index of the
     * descendant candidate that is being filtered</li>
     * </ul>
     */
    Match has(Filter filter);

    /**
     * Check if at least one element in the set of matched elements satisfies a
     * selector.
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    boolean is(String selector);

    /**
     * Check if at least one element in the set of matched elements satisfies a
     * filter.
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element being checked</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element being
     * checked</li>
     * </ul>
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
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match next(String selector);

    /**
     * Get the immediate next sibling of every element in set of matched
     * elements, matching a filter
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose next sibling is
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * next sibling is searched</li>
     * <li> {@link Context#element()} - the next sibling that is being filtered</li>
     * <li> {@link Context#elementIndex()} - 1</li>
     * </ul>
     */
    Match next(Filter filter);

    /**
     * Get all next siblings of every element in a set of matched elements
     */
    Match nextAll();

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a selector
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match nextAll(String selector);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a filter
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose next siblings are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * next siblings are searched</li>
     * <li> {@link Context#element()} - the next siblings that is being filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the next
     * siblings that are being filtered</li>
     * </ul>
     */
    Match nextAll(Filter filter);

    /**
     * Get all next siblings of every element in a set of matched elements until
     * the provided selector matches
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match nextUntil(String until);

    /**
     * Get all next siblings of every element in a set of matched elements until
     * the provided filter matches
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose next siblings are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * next siblings are searched</li>
     * <li> {@link Context#element()} - the next siblings that is being filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the next
     * siblings that are being filtered</li>
     * </ul>
     */
    Match nextUntil(Filter until);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a selector, until the provided selector matches
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match nextUntil(String until, String selector);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a filter, until the provided selector matches
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose next siblings are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * next siblings are searched</li>
     * <li> {@link Context#element()} - the next siblings that is being filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the next
     * siblings that are being filtered</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match nextUntil(String until, Filter filter);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a selector until the provided filter matches
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose next siblings are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * next siblings are searched</li>
     * <li> {@link Context#element()} - the next siblings that is being filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the next
     * siblings that are being filtered</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match nextUntil(Filter until, String selector);

    /**
     * Get all next siblings of every element in a set of matched elements,
     * matching a filter until the provided filter matches
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose next siblings are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * next siblings are searched</li>
     * <li> {@link Context#element()} - the next siblings that is being filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the next
     * siblings that are being filtered</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match nextUntil(Filter until, Filter filter);

    /**
     * Remove elements from the set of matched elements.
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match not(String selector);

    /**
     * Remove elements from the set of matched elements.
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element being checked</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element being
     * checked</li>
     * </ul>
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
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match parent(String selector);

    /**
     * Get the immediate parent elements of every element in a set of matched
     * elements, matching a filter
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose parent is
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * parent is searched</li>
     * <li> {@link Context#element()} - the parent that is being filtered</li>
     * <li> {@link Context#elementIndex()} - 1</li>
     * </ul>
     */
    Match parent(Filter filter);

    /**
     * Get all ancestor elements of every element in a set of matched elements
     */
    Match parents();

    /**
     * Get all ancestor elements of every element in a set of matched elements,
     * matching a selector
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match parents(String selector);

    /**
     * Get all ancestor elements of every element in a set of matched elements,
     * matching a filter
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose parents are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * parents are searched</li>
     * <li> {@link Context#element()} - the parent that is being filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the parent
     * that is being filtered</li>
     * </ul>
     */
    Match parents(Filter filter);

    /**
     * Get all ancestors of every element in a set of matched elements until the
     * provided selector matches
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match parentsUntil(String until);

    /**
     * Get all ancestors of every element in a set of matched elements until the
     * provided filter matches
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose parents are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * parents are searched</li>
     * <li> {@link Context#element()} - the parent that is being filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the parent
     * that is being filtered</li>
     * </ul>
     */
    Match parentsUntil(Filter until);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a selector, until the provided selector matches
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match parentsUntil(String until, String selector);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a filter, until the provided selector matches
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose parents are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * parents are searched</li>
     * <li> {@link Context#element()} - the parent that is being filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the parent
     * that is being filtered</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match parentsUntil(String until, Filter filter);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a selector until the provided filter matches
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose parents are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * parents are searched</li>
     * <li> {@link Context#element()} - the parent that is being filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the parent
     * that is being filtered</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match parentsUntil(Filter until, String selector);

    /**
     * Get all ancestors of every element in a set of matched elements, matching
     * a filter until the provided filter matches
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose parents are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * parents are searched</li>
     * <li> {@link Context#element()} - the parent that is being filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the parent
     * that is being filtered</li>
     * </ul>
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
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match prev(String selector);

    /**
     * Get the immediate previous sibling of every element in set of matched
     * elements, matching a filter
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose previous sibling
     * is searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * previous sibling is searched</li>
     * <li> {@link Context#element()} - the previous sibling that is being
     * filtered</li>
     * <li> {@link Context#elementIndex()} - 1</li>
     * </ul>
     */
    Match prev(Filter filter);

    /**
     * Get all previous siblings of every element in a set of matched elements
     */
    Match prevAll();

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a selector
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match prevAll(String selector);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a filter
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose previous siblings
     * are searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * previous siblings are searched</li>
     * <li> {@link Context#element()} - the previous siblings that is being
     * filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the previous
     * siblings that are being filtered</li>
     * </ul>
     */
    Match prevAll(Filter filter);

    /**
     * Get all previous siblings of every element in a set of matched elements
     * until the provided selector matches
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match prevUntil(String until);

    /**
     * Get all previous siblings of every element in a set of matched elements
     * until the provided filter matches
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose previous siblings
     * are searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * previous siblings are searched</li>
     * <li> {@link Context#element()} - the previous siblings that is being
     * filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the previous
     * siblings that are being filtered</li>
     * </ul>
     */
    Match prevUntil(Filter until);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a selector, until the provided selector matches
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match prevUntil(String until, String selector);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a filter, until the provided selector matches
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose previous siblings
     * are searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * previous siblings are searched</li>
     * <li> {@link Context#element()} - the previous siblings that is being
     * filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the previous
     * siblings that are being filtered</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match prevUntil(String until, Filter filter);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a selector until the provided filter matches
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose previous siblings
     * are searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * previous siblings are searched</li>
     * <li> {@link Context#element()} - the previous siblings that is being
     * filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the previous
     * siblings that are being filtered</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match prevUntil(Filter until, String selector);

    /**
     * Get all previous siblings of every element in a set of matched elements,
     * matching a filter until the provided filter matches
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose previous siblings
     * are searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * previous siblings are searched</li>
     * <li> {@link Context#element()} - the previous siblings that is being
     * filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the previous
     * siblings that are being filtered</li>
     * </ul>
     */
    Match prevUntil(Filter until, Filter filter);

    /**
     * Get all siblings of every element in a set of matched elements
     */
    Match siblings();

    /**
     * Get all siblings of every element in a set of matched elements, matching
     * a selector
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match siblings(String selector);

    /**
     * Get all siblings of every element in a set of matched elements, matching
     * a filter
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element whose siblings are
     * searched</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element whose
     * siblings are searched</li>
     * <li> {@link Context#element()} - the sibling that is being filtered</li>
     * <li> {@link Context#elementIndex()} - the relative index of the sibling
     * that is being filtered. This is less than zero if it is a previous
     * sibling or more than zero if it is a subsequent sibling, compared to the
     * element in {@link Context#match()}</li>
     * </ul>
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

    /**
     * Reduce the set of matched elements by filtering out those whose text
     * content doesn't match a given regex
     * <p>
     * This is the same as calling <code>matchText(regex, true)</code>
     *
     * @see JOOX#matchText(String)
     */
    Match matchText(String regex);

    /**
     * Reduce the set of matched elements by filtering out those whose text
     * content doesn't match a given regex <code>(keepMatches = true)</code>, or
     * those whose text content matches a given regex
     * <code>(keepMatches = false)</code>
     *
     * @see JOOX#matchText(String)
     */
    Match matchText(String regex, boolean keepMatches);

    /**
     * Reduce the set of matched elements by filtering out those whose attribute
     * content doesn't match a given regex
     * <p>
     * This is the same as calling <code>matchAttr(name, valueRegex, true)</code>
     *
     * @see JOOX#matchAttr(String, String)
     */
    Match matchAttr(String name, String valueRegex);

    /**
     * Reduce the set of matched elements by filtering out those whose attribute
     * content doesn't match a given regex <code>(keepMatches = true)</code>, or
     * those whose text content matches a given regex
     * <code>(keepMatches = false)</code>
     *
     * @see JOOX#matchAttr(String, String)
     */
    Match matchAttr(String name, String valueRegex, boolean keepMatches);

    /**
     * Reduce the set of matched elements by filtering out those whose tag name
     * doesn't match a given regex
     * <p>
     * This is the same as calling <code>matchText(regex, true)</code>
     *
     * @see JOOX#matchTag(String)
     */
    Match matchTag(String regex);

    /**
     * Reduce the set of matched elements by filtering out those whose tag name
     * doesn't match a given regex <code>(keepMatches = true)</code>, or those
     * whose tag name matches a given regex <code>(keepMatches = false)</code>
     *
     * @see JOOX#matchTag(String)
     */
    Match matchTag(String regex, boolean keepMatches);

    /**
     * Reduce the set of matched elements to the ones that are leaf elements
     *
     * @see JOOX#leaf()
     */
    Match leaf();

    // ---------------------------------------------------------------------
    // Manipulation of elements
    // ---------------------------------------------------------------------

    /**
     * Add content before each element in the set of matched elements.
     */
    Match before(String content);

    /**
     * Add content before each element in the set of matched elements.
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element being prepended before</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element being
     * prepended before</li>
     * </ul>
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
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element being appended after</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element being
     * appended after</li>
     * </ul>
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
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element being prepended to</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element being
     * prepended to</li>
     * </ul>
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
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element being appended to</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element being
     * appended to</li>
     * </ul>
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
     * Rename all tags in the set of matched elements to some new tag name
     */
    Match rename(String tag);

    /**
     * Rename all tags in the set of matched elements to some new tag name
     */
    Match rename(Content tag);

    /**
     * Removes all content from all elements in the set of matched elements.
     */
    Match empty();

    /**
     * Removes all elements from their parent nodes in the set of matched
     * elements.
     */
    Match remove();

    /**
     * Removes all elements from their parent nodes in the set of matched
     * elements, matching a selector
     * <p>
     * The selector provided to this method supports the following features:
     * <ul>
     * <li><strong>*</strong> can be used to select everything</li>
     * <li><strong>tag names</strong> can be used to select XML elements by tag
     * names (see {@link Element#getElementsByTagName(String)}. Tag names are
     * namespace-unaware. This means that existing namespaces will be ignored</li>
     * </ul>
     * The following features are not supported:
     * <ul>
     * <li><strong>CSS selectors</strong> cannot be used (yet) to select XML
     * elements from this method. Use {@link #find(String)} instead</li>
     * <li><strong>XPath</strong> cannot be used. Use {@link #xpath(String)}
     * instead</li>
     * <li><strong>Namespaces</strong> cannot be used. Use
     * {@link #xpath(String)} with {@link #namespaces(Map)} instead</li>
     * </ul>
     *
     * @see JOOX#selector(String)
     */
    Match remove(String selector);

    /**
     * Removes all elements from their parent nodes in the set of matched
     * elements, matching a filter
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element being removed</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element being
     * removed</li>
     * </ul>
     */
    Match remove(Filter filter);

    /**
     * Wrap all elements from their parent nodes in the set of matched elements
     * in a new parent element
     * <p>
     * The resulting set of matched elements contains the newly wrapped elements
     *
     * @see #unwrap()
     */
    Match wrap(String parent);

    /**
     * Wrap all elements in the set of matched elements in a new parent element
     * <p>
     * The resulting set of matched elements contains the newly wrapped elements
     *
     * @see #unwrap()
     */
    Match wrap(Content parent);

    /**
     * Removes all elements in the set of matched elements from their parents
     * <p>
     * The resulting set of matched elements contains the newly unwrapped
     * elements
     *
     * @see #wrap(String)
     */
    Match unwrap();

    // ---------------------------------------------------------------------
    // Manipulation of attributes
    // ---------------------------------------------------------------------

    /**
     * Get an attribute from the first element in the set of matched elements,
     * or <code>null</code> if the first element does not have that attribute.
     * <p>
     * jOOX is namespace-unaware. The supplied attribute name will be compared
     * against all attributes, matching the first one that has the given name.
     */
    String attr(String name);

    /**
     * Get a converted attribute from the first element in the set of matched
     * elements, or <code>null</code> if the first element does not have that
     * attribute.
     * <p>
     * jOOX is namespace-unaware. The supplied attribute name will be compared
     * against all attributes, matching the first one that has the given name.
     *
     * @see JOOX#convert(String, Class)
     */
    <T> T attr(String name, Class<T> type);

    /**
     * Get an attribute from all elements in the set of matched elements
     * <p>
     * jOOX is namespace-unaware. The supplied attribute name will be compared
     * against all attributes, matching the first one that has the given name.
     */
    List<String> attrs(String name);

    /**
     * Get a converted attribute from all elements in the set of matched
     * elements
     * <p>
     * jOOX is namespace-unaware. The supplied attribute name will be compared
     * against all attributes, matching the first one that has the given name.
     *
     * @see JOOX#convert(String, Class)
     */
    <T> List<T> attrs(String name, Class<T> type);

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
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element being attributed</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element being
     * attributed</li>
     * </ul>
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
     * or <code>null</code> if there are no matched elements.
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
     * Add some JAXB-marshallable XML content to all elements in the set of
     * matched elements (possibly replacing existing content).
     *
     * @see JOOX#$(Object)
     * @see JOOX#content(Object)
     */
    Match content(Object content);

    /**
     * Add some XML content to all elements in the set of matched elements
     * (possibly replacing existing content). If the supplied content is invalid
     * XML or plain text, then it will be added as text just as with
     * {@link #text(String)}
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element being added to</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element being
     * added to</li>
     * </ul>
     */
    Match content(Content content);

    /**
     * Get all text content of the elements in the set of matched elements.
     */
    List<String> texts();

    /**
     * Get all converted text content of the elements in the set of matched
     * elements.
     *
     * @see JOOX#convert(String, Class)
     */
    <T> List<T> texts(Class<T> type);

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
     * Get the converted text content of the first element in the set of matched
     * elements, or <code>null</code> if there are no matched elements.
     *
     * @see JOOX#convert(String, Class)
     */
    <T> T text(Class<T> type);

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
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li> {@link Context#match()} - the matched element being added to</li>
     * <li> {@link Context#matchIndex()} - the index of the matched element being
     * added to</li>
     * </ul>
     */
    Match text(Content content);

    /**
     * Get all CDATA content of the elements in the set of matched elements.
     * <p>
     * This is the same as {@link #texts()}.
     */
    List<String> cdatas();

    /**
     * Get all converted CDATA content of the elements in the set of matched
     * elements.
     * <p>
     * This is the same as {@link #texts(Class)}.
     *
     * @see JOOX#convert(String, Class)
     */
    <T> List<T> cdatas(Class<T> type);

    /**
     * Get all CDATA content of the elements at given indexes in the set of
     * matched elements.
     * <p>
     * This is the same as {@link #texts(int...)}.
     */
    List<String> cdatas(int... indexes);

    /**
     * Get the CDATA content of the first element in the set of matched
     * elements, or <code>null</code> if there are no matched elements.
     * <p>
     * This is the same as calling <code>cdata(0)</code> or {@link #text()}.
     */
    String cdata();

    /**
     * Get the converted CDATA content of the first element in the set of
     * matched elements, or <code>null</code> if there are no matched elements.
     * <p>
     * This is the same as {@link #text(Class)}.
     *
     * @see JOOX#convert(String, Class)
     */
    <T> T cdata(Class<T> type);

    /**
     * Get the CDATA content at a given index in the current set of matched
     * elements.
     * <p>
     * This is the same as {@link #text(int)}.
     */
    String cdata(int index);

    /**
     * Set some CDATA content to all elements in the set of matched elements
     * (possibly replacing existing content).
     * <p>
     * Unlike {@link #text(Content)}, this generates a
     * {@link Node#CDATA_SECTION_NODE}.
     */
    Match cdata(String content);

    /**
     * Set some CDATA content to all elements in the set of matched elements
     * (possibly replacing existing content).
     * <p>
     * The callback {@link Context} is populated like this:
     * <ul>
     * <li>{@link Context#match()} - the matched element being added to</li>
     * <li>{@link Context#matchIndex()} - the index of the matched element being
     * added to</li>
     * </ul>
     * <p>
     * Unlike {@link #text(Content)}, this generates a
     * {@link Node#CDATA_SECTION_NODE}.
     */
    Match cdata(Content content);

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
     * Get a list of converted id values in the current set of matched elements.
     *
     * @see JOOX#convert(String, Class)
     */
    <T> List<T> ids(Class<T> type);

    /**
     * Get the first id value
     * <p>
     * This is the same as calling <code>id(0)</code>
     */
    String id();

    /**
     * Get an id value at a given index in the current set of matched elements.
     * <p>
     * This is the same as calling <code>eq(index).attr("id")</code>
     */
    String id(int index);

    /**
     * Get the first converted id value
     *
     * @see JOOX#convert(String, Class)
     */
    <T> T id(Class<T> type);

    // ---------------------------------------------------------------------
    // Transformation, marshalling and streaming
    // ---------------------------------------------------------------------

    /**
     * Write the set of matched elements into a writer
     * <p>
     * If the set contains more or less than <code>1</code> element, this will
     * result in writing non-well-formed XML
     */
    Match write(Writer writer) throws IOException;

    /**
     * Write the set of matched elements into a stream
     * <p>
     * If the set contains more or less than <code>1</code> element, this will
     * result in writing non-well-formed XML
     */
    Match write(OutputStream stream) throws IOException;

    /**
     * Write the set of matched elements into a file
     * <p>
     * If the set contains more or less than <code>1</code> element, this will
     * result in writing non-well-formed XML
     */
    Match write(File file) throws IOException;

    /**
     * Unmarshal the current set of matched elements into a JAXB-annotated type.
     */
    <T> List<T> unmarshal(Class<T> type);

    /**
     * Unmarshal the current set of matched elements at given indexes into a
     * JAXB-annotated type.
     */
    <T> List<T> unmarshal(Class<T> type, int... indexes);

    /**
     * Unmarshal the first element in the current set of matched elements into a
     * JAXB-annotated type.
     * <p>
     * This is the same as calling <code>unmarshalOne(type, 0)</code>
     */
    <T> T unmarshalOne(Class<T> type);

    /**
     * Unmarshal the element at a given index in the current set of matched
     * elements into a JAXB-annotated type.
     * <p>
     * This is the same as calling <code>unmarshalOne(type, 0)</code>
     */
    <T> T unmarshalOne(Class<T> type, int index);

    /**
     * Transform all elements in the set of matched elements.
     * <p>
     * This will apply a given {@link Transformer} to every element in the set
     * of matched elements. Every element in the set of matched elements will be
     * replaced by its corresponding {@link Result} obtained from the
     * <code>transformer</code>.
     * <p>
     * <h5>Example Input:</h5>
     * <p>
     * <code><pre>
     * &lt;books>
     *   &lt;book id="1"/>
     *   &lt;book id="2"/>
     * &lt;/books>
     * </pre></code>
     * <p>
     * <h5>Example XSLT:</h5>
     * <p>
     * <code><pre>
     * &lt;?xml version="1.0" encoding="ISO-8859-1"?>
     * &lt;xsl:stylesheet version="1.0"
     *     xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
     *
     *     &lt;xsl:template match="book">
     *         &lt;book id="{@id + 1}">
     *             &lt;xsl:apply-templates/>
     *         &lt;/book>
     *     &lt;/xsl:template>
     *
     *     &lt;xsl:template match="@*|*">
     *         &lt;xsl:copy>
     *             &lt;xsl:apply-templates select="*|@*"/>
     *         &lt;/xsl:copy>
     *     &lt;/xsl:template>
     * &lt;/xsl:stylesheet>
     * </pre></code>
     * <p>
     * <h5>Apply transformation:</h5>
     * <p>
     * <code><pre>
     * // Applies transformation to the document element:
     * $(document).transform("increment.xsl");
     *
     * // Applies transformation to every book element:
     * $(document).find("book").transform("increment.xsl");
     * </pre></code>
     * <p>
     * <h5>Result:</h5>
     * <p>
     * <code><pre>
     * &lt;books>
     *   &lt;book id="2"/>
     *   &lt;book id="3"/>
     * &lt;/books>
     * </pre></code>
     */
    Match transform(Transformer transformer);

    /**
     * Transform all elements in the set of matched elements.
     *
     * @see #transform(Transformer)
     */
    Match transform(Source transformer);

    /**
     * Transform all elements in the set of matched elements.
     *
     * @see #transform(Transformer)
     */
    Match transform(InputStream transformer);

    /**
     * Transform all elements in the set of matched elements.
     *
     * @see #transform(Transformer)
     */
    Match transform(Reader transformer);

    /**
     * Transform all elements in the set of matched elements.
     *
     * @see #transform(Transformer)
     */
    Match transform(URL transformer);

    /**
     * Transform all elements in the set of matched elements.
     *
     * @see #transform(Transformer)
     */
    Match transform(File transformer);

    /**
     * Transform all elements in the set of matched elements.
     *
     * @see #transform(Transformer)
     */
    Match transform(String transformer);

    /**
     * Allows to sort the result with the given comparator.
     *
     * @param comparator The element comparator.
     * @return
     */
    Match sort(Comparator<Element> comparator);
}
