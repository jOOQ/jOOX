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

import org.w3c.dom.Element;

/**
 * The context object passed to callback methods in {@link Content},
 * {@link Each}, {@link Filter}, and {@link Mapper}
 *
 * @author Lukas Eder
 */
public interface Context {

    /**
     * The element in the set of matched elements from which this callback is
     * made.
     */
    Element match();

    /**
     * The index of the element in the set of matched elements from which this
     * callback is made.
     */
    int matchIndex();

    /**
     * The number of elements in the set of matched elements from which this
     * callback is made.
     */
    int matchSize();

    /**
     * The element currently being iterated on.
     * <p>
     * If not further specified, this is the same as {@link #match()}
     */
    Element element();

    /**
     * The index of the element currently being iterated on.
     * <p>
     * If not further specified, this is the same as {@link #matchIndex()}
     */
    int elementIndex();

    /**
     * The number of elements currently being iterated on.
     * <p>
     * If not further specified, this is the same as {@link #matchSize()}
     */
    int elementSize();
}
