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

import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * A wrapper for a {@link NamedNodeMap} that is returned when calling
 * {@link Element#getAttributes()} on a matched element.
 * <p>
 * The <code>Attrs</code> type is much more convenient than the
 * <code>NamedNodeMap</code>, as it does not provide the unnecessary abstraction
 * of modelling an attribute as a {@link org.w3c.dom.Node}
 *
 * @author Lukas Eder
 */
public interface Attrs extends Map<String, String> {

}
