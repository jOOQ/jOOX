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
 * A Callback providing (mostly new) content for each element
 *
 * @author Lukas Eder
 */
@FunctionalInterface
public interface Content {

    /**
     * The callback method invoked for every matched element.
     *
     * @param context The context for the current callback call.
     * @return The new content
     */
    String content(Context context);
}
