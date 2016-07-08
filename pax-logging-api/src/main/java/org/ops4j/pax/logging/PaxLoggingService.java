/*
 * Copyright 2005 Niclas Hedhman.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.logging;

import java.util.Set;

import org.knopflerfish.service.log.LogService;
import org.osgi.framework.Bundle;

public interface PaxLoggingService extends LogService
{

    String APPENDER_NAME_PROPERTY = "org.ops4j.pax.logging.appender.name";

    String LAYOUT_NAME_PROPERTY = "org.ops4j.pax.logging.layout.name";

    String FILTER_NAME_PROPERTY = "org.ops4j.pax.logging.filter.name";

    String ERRORHANDLER_NAME_PROPERTY = "org.ops4j.pax.logging.errorhandler.name";

    PaxLogger getLogger( Bundle bundle, String category, String fqcn );

    int getLogLevel();
    
    PaxContext getPaxContext();

    Set<PaxLogger> getLoggers();

    void setLogLevel(String name, String level);
    
}
