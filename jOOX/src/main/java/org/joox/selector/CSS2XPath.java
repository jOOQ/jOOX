/**
 * Copyright (c) 2011-2012, Lukas Eder, lukas.eder@gmail.com
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
package org.joox.selector;

import java.util.List;

/**
 * A utility class converting CSS selector expressions to XPath expressions.
 * <p>
 * This class is a facade for the more extensive functionality provided by the
 * <code>css-selectors</code> project created by Christer Sandberg. jOOX does
 * not expose that functionality publicly.
 *
 * @author Lukas Eder
 * @see <a
 *      href="https://github.com/chrsan/css-selectors">https://github.com/chrsan/css-selectors</a>
 * @see <a
 *      href="http://www.w3.org/TR/selectors/#selectors">http://www.w3.org/TR/selectors/#selectors</a>
 */
public final class CSS2XPath {

    /**
     * Convert a CSS selector expression to an XPath expression
     */
    public static final String css2xpath(String css) {
        return css2xpath(css, true);
    }

    /**
     * Convert a CSS selector expression to an XPath expression
     */
    public static final String css2xpath(String css, boolean isRoot) {
        Scanner scanner = new Scanner(css);
        List<List<Selector>> selectors = scanner.scan();

        StringBuilder sb = new StringBuilder();
        if (!isRoot) {
            sb.append(".");
        }

        String selectorSeparator = "";
        for (List<Selector> selector : selectors) {
            sb.append(selectorSeparator);

            for (Selector s : selector) {
                switch (s.getCombinator()) {
                    case CHILD:
                        sb.append("/");
                        break;
                    case DESCENDANT:
                        sb.append("//");
                        break;
                    case ADJACENT_SIBLING:

                        // TODO: Implement this
                        sb.append("?????");
                        break;
                    case GENERAL_SIBLING:

                        // TODO: Implement this
                        sb.append("?????");
                        break;
                }

                sb.append(s.getTagName());

                if (s.hasSpecifiers()) {
                    for (Specifier specifier : s.getSpecifiers()) {
                        switch (specifier.getType()) {
                            case ATTRIBUTE: {
                                AttributeSpecifier a = ((AttributeSpecifier) specifier);

                                sb.append("[");

                                if (a.getMatch() == null) {
                                    sb.append("@");
                                    sb.append(a.getName());
                                }
                                else {
                                    switch (a.getMatch()) {
                                        case EXACT: {
                                            sb.append("@");
                                            sb.append(a.getName());
                                            sb.append("='");
                                            sb.append(a.getValue().replace("'", "\\'"));
                                            sb.append("'");
                                            break;
                                        }

                                        case CONTAINS: {
                                            sb.append("contains(@");
                                            sb.append(a.getName());
                                            sb.append(", '");
                                            sb.append(a.getValue().replace("'", "\\'"));
                                            sb.append("')");
                                            break;
                                        }

                                        case HYPHEN: {
                                            sb.append("@");
                                            sb.append(a.getName());
                                            sb.append("='");
                                            sb.append(a.getValue().replace("'", "\\'"));
                                            sb.append("' or starts-with(@");
                                            sb.append(a.getName());
                                            sb.append(", '");
                                            sb.append(a.getValue().replace("'", "\\'"));
                                            sb.append("-')");
                                            break;
                                        }

                                        case PREFIX: {
                                            sb.append("starts-with(@");
                                            sb.append(a.getName());
                                            sb.append(", '");
                                            sb.append(a.getValue().replace("'", "\\'"));
                                            sb.append("')");
                                            break;
                                        }

                                        case SUFFIX: {
                                            endsWith(sb, a.getName(), a.getValue());
                                            break;
                                        }

                                        case LIST: {
                                            sb.append("@");
                                            sb.append(a.getName());
                                            sb.append("='");
                                            sb.append(a.getValue().replace("'", "\\'"));
                                            sb.append("' or starts-with(@");
                                            sb.append(a.getName());
                                            sb.append(", '");
                                            sb.append(a.getValue().replace("'", "\\'"));
                                            sb.append(" ')");
                                            sb.append(" or ");
                                            endsWith(sb, a.getName(), " " + a.getValue());
                                            sb.append(" or contains(@");
                                            sb.append(a.getName());
                                            sb.append(", ' ");
                                            sb.append(a.getValue().replace("'", "\\'"));
                                            sb.append(" ')");
                                            break;
                                        }
                                    }
                                }

                                sb.append("]");
                                break;
                            }

                            case NEGATION: {
                                sb.append("[not(");

                                // TODO: implement this..??

                                sb.append(")]");
                                break;
                            }

                            case PSEUDO: {
                                if (specifier instanceof PseudoClassSpecifier) {
                                    PseudoClassSpecifier p = ((PseudoClassSpecifier) specifier);
                                    String value = p.getValue();

                                    if ("empty".equals(value)) {
                                        sb.append("[not(*|@*|node())]");
                                    }
                                    else if ("first-child".equals(value)) {
                                        sb.append("[not(preceding-sibling::*)]");
                                    }
                                    else if ("last-child".equals(value)) {
                                        sb.append("[not(following-sibling::*)]");
                                    }
                                    else if ("only-child".equals(value)) {
                                        sb.append("[not(preceding-sibling::*) and not(following-sibling::*)]");
                                    }
                                    else if ("root".equals(value)) {
                                        sb.append("[not(parent::*)]");
                                    }
                                }
                                else if (specifier instanceof PseudoNthSpecifier) {
                                    PseudoNthSpecifier p = ((PseudoNthSpecifier) specifier);
                                    String value = p.getValue();

                                    if ("nth-child".equals(value)) {
                                        sb.append("[position() = ");
                                        sb.append(p.getArgument());
                                        sb.append("]");
                                    }

                                    // TODO: Implement this...?
                                }
                                break;
                            }
                        }
                    }
                }
            }

            selectorSeparator = " | ";
        }

        return sb.toString();
    }

    /**
     * XPath 2.0<br/><br/><code>ends-with($str1, $str2)</code><br/><br/> is equivalent to XPath 1.0<br/><br/>
     * <code>$str2 = substring($str1, string-length($str1) - string-length($str2) + 1)</code>
     */
    private static void endsWith(StringBuilder sb, String attr, String value) {
        sb.append("'");
        sb.append(value.replace("'", "\\'"));
        sb.append("' = substring(@");
        sb.append(attr);
        sb.append(", string-length(@");
        sb.append(attr);
        sb.append(") - string-length('");
        sb.append(value.replace("'", "\\'"));
        sb.append("') + 1)");
    }

}
