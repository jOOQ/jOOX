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

/**
 * A filter indicating whether an element/index should be kept when reducing an
 * {@link Match} node set
 * <p>
 * In addition to the filtering functionality, this marker interface can be used
 * to indicate that {@link Context#elementSize()} may not be needed for
 * filtering. This is particularly interesting for filters, such as
 * {@link JOOX#tag(String)}, {@link JOOX#all()}, etc
 *
 * @author Lukas Eder
 */

@FunctionalInterface

public interface FastFilter extends Filter {

}
