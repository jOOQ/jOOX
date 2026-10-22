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

import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilderFactory;
import org.openjdk.jmh.annotations.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Fork(value = 3, jvmArgsAppend = {"-Xms512m", "-Xmx512m"})
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class ContentBenchmark {
    @Param({"SMALL", "ATTRIBUTES", "LARGE"})
    public String shape;
    private Element element;
    private String serialized;

    @Setup
    public void setup() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        element = document.createElement("root");
        document.appendChild(element);
        if (shape.equals("ATTRIBUTES"))
            for (int i = 0; i < 32; i++)
                element.setAttribute("attribute" + i, "value > with \"quotes\" and & text");
        element.appendChild(document.createTextNode("before"));
        for (int i = 0; i < (shape.equals("LARGE") ? 1000 : 1); i++) {
            Element child = document.createElement("child");
            child.setAttribute("id", Integer.toString(i));
            child.appendChild(document.createTextNode("mixed text & payload " + i));
            element.appendChild(child);
        }
        element.appendChild(document.createTextNode("after"));
        serialized = Util.toString(element);
        String expected = JOOX.$(element).content();
        if (!expected.equals(regex(serialized)) || !expected.equals(scan(serialized)))
            throw new AssertionError("Implementations disagree for " + shape);
    }

    // Includes the existing DOM serialization, as in the mixed-content branch.
    @Benchmark public String regexFull() { return regex(Util.toString(element)); }
    @Benchmark public String scanFull() { return scan(Util.toString(element)); }
    @Benchmark public String regexOnly() { return regex(serialized); }
    @Benchmark public String scanOnly() { return scan(serialized); }

    private String regex(String xml) {
        String name = element.getTagName();
        return xml.replaceAll("(?s)^<" + name + "(?:[^>]*)>(.*)</" + name + ">$", "$1");
    }

    private static String scan(String xml) {
        char quote = 0;
        int start = 1;
        for (; start < xml.length(); start++) {
            char c = xml.charAt(start);
            if (quote != 0) {
                if (c == quote) quote = 0;
            }
            else if (c == '\'' || c == '"') quote = c;
            else if (c == '>') break;
        }
        return xml.substring(start + 1, xml.lastIndexOf('<'));
    }
}
