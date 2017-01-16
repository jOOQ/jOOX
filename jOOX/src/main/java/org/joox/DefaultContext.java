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

import static org.joox.JOOX.$;

import org.w3c.dom.Element;

/**
 * @author Lukas Eder
 */
class DefaultContext implements Context {

    private final Element match;
    private final Element element;
    private final int matchIndex;
    private final int elementIndex;
    private final int matchSize;
    private final int elementSize;

    DefaultContext(Element match, int matchIndex, int matchSize, Element element, int elementIndex, int elementSize) {
        this.match = match;
        this.matchIndex = matchIndex;
        this.matchSize = matchSize;
        this.element = element;
        this.elementIndex = elementIndex;
        this.elementSize = elementSize;
    }

    DefaultContext(Element match, int matchIndex, int matchSize) {
        this(match, matchIndex, matchSize, match, matchIndex, matchSize);
    }

    @Override
    public final Element element() {
        return element;
    }

    @Override
    public int elementIndex() {
        return elementIndex;
    }

    @Override
    public int elementSize() {
        return elementSize;
    }

    @Override
    public Element match() {
        return match;
    }

    @Override
    public int matchIndex() {
        return matchIndex;
    }

    @Override
    public int matchSize() {
        return matchSize;
    }

    // -------------------------------------------------------------------------
    // XXX: Object
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return $(element).toString();
    }
}
