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

import javax.xml.xpath.XPathFunction;

import org.apache.xalan.extensions.ExpressionContext;
import org.joox.Match;
import org.w3c.dom.NodeList;

/**
 * A class with some static methods used for {@link Match#xpath(String)}, when
 * calling custom {@link XPathFunction}'s
 *
 * @author Lukas Eder
 */
public class Functions {

    @SuppressWarnings("unused")
    public static boolean byOrwellWithNodes(ExpressionContext context, NodeList nodes) {
        return $(nodes).find("author").text().toLowerCase().contains("orwell");
    }

    public static boolean byOrwellWithId(int id) {
        return id == 1 || id == 2;
    }
}
