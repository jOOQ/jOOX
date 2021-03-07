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
package org.joox.selector;

import java.util.List;
import java.util.regex.Pattern;

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
     * A selector pattern that can be evaluated using standard DOM API
     */
    private final static Pattern SIMPLE_SELECTOR = Pattern.compile("[\\w\\-]+");

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
        String selectorSeparator = "";
        for (List<Selector> selector : selectors) {
            sb.append(selectorSeparator);

            // [#95] Append a dot if we should match only descendants of a
            // non-root element
            if (!isRoot) {
                sb.append(".");
            }

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

                // [#163] To stay on the safe side, we need namespace unaware XPath expressions here
                //        Do this only for actual tag names, not e.g. * or other special characters
                if (SIMPLE_SELECTOR.matcher(s.getTagName()).matches())
                    sb.append("*[local-name() = '").append(s.getTagName()).append("']");
                else
                    sb.append(s.getTagName());

                // [#163] This would be an XPath 2.0 syntax, not supported by Java's built-in XPath libraries
                // sb.append("*:").append(s.getTagName());

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

                                    if ("empty".equals(value))
                                        sb.append("[not(*|@*|node())]");
                                    else if ("first-child".equals(value))
                                        sb.append("[not(preceding-sibling::*)]");
                                    else if ("last-child".equals(value))
                                        sb.append("[not(following-sibling::*)]");
                                    else if ("only-child".equals(value))
                                        sb.append("[not(preceding-sibling::*) and not(following-sibling::*)]");
                                    else if ("root".equals(value))
                                        sb.append("[not(parent::*)]");
                                }
                                else if (specifier instanceof PseudoNthSpecifier) {
                                    PseudoNthSpecifier p = ((PseudoNthSpecifier) specifier);
                                    String value = p.getValue();

                                    if ("nth-child".equals(value)) {
                                        sb.append("[count(preceding-sibling::*) = ");
                                        sb.append(p.getArgument());
                                        sb.append(" - 1]");
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
