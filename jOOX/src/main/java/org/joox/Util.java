/**
 * Copyright (c) 2009-2011, Lukas Eder, lukas.eder@gmail.com
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
 * . Neither the name "jOOQ" nor the names of its contributors may be
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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * DOM utilities
 *
 * @author Lukas Eder
 */
class Util {

    /**
     * Create some content in the context of a given document
     *
     * @return <ul>
     *         <li>A {@link DocumentFragment} if <code>text</code> is
     *         well-formed.</li>
     *         <li><code>null</code>, if <code>text</code> is plain text or not
     *         well formed</li>
     *         </ul>
     */
    static DocumentFragment createContent(Document doc, String text) {

        // Text might hold XML content
        if (text.contains("<")) {
            DocumentBuilder builder = JOOX.builder();

            try {
                String wrapped = "<dummy>" + text + "</dummy>";
                Document parsed = builder.parse(new InputSource(new StringReader(wrapped)));
                DocumentFragment fragment = parsed.createDocumentFragment();
                NodeList children = parsed.getDocumentElement().getChildNodes();

                // appendChild removes children also from NodeList!
                while (children.getLength() > 0) {
                    fragment.appendChild(children.item(0));
                }

                return (DocumentFragment) doc.importNode(fragment, true);
            }

            // This does not occur
            catch (IOException ignore) {}

            // The XML content is invalid
            catch (SAXException ignore) {}
        }

        // Plain text or invalid XML
        return null;
    }

}
