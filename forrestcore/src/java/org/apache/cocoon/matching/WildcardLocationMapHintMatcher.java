/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.matching;

import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.components.modules.input.lm.LocationMap;

/**
 * Match a LocationMap hint - the part after the module name (module_name:<b>hint</b>) - 
 * against a wildcard pattern.
 */
public class WildcardLocationMapHintMatcher extends AbstractWildcardMatcher {

    protected String getMatchString(Map objectModel, Parameters parameters) {
        return parameters.getParameter(LocationMap.HINT_PARAM,"");
    }

}
