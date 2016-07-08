/*
 * Copyright 2006 Niclas Hedhman.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ops4j.pax.logging.internal.BundleHelper;
import org.ops4j.pax.logging.internal.TrackingLogger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class OSGIPaxLoggingManager extends ServiceTracker
        implements PaxLoggingManager
{

    private PaxLoggingService m_service;

    private BundleContext m_context;

    private Map<String, TrackingLogger> m_loggers;

    private ServiceReference m_logServiceRef;

    public OSGIPaxLoggingManager(BundleContext context)
    {
        super(context, PaxLoggingService.class.getName(), null);
        m_loggers = new HashMap<String, TrackingLogger>();
        m_context = context;
        // retrieve the service if any exist at this point.
        ServiceReference ref = context.getServiceReference(PaxLoggingService.class.getName());
        if (ref != null)
        {
            m_service = (PaxLoggingService) context.getService(ref);
        }
    }

    public Object addingService(ServiceReference reference)
    {
        m_logServiceRef = reference;
        m_service = (PaxLoggingService) m_context.getService(reference);
        synchronized (m_loggers) {
            for (TrackingLogger logger : m_loggers.values()) {
                logger.added(m_service);
            }
        }
        return m_service;
    }

    public void removedService(ServiceReference reference, Object service)
    {
        synchronized (m_service) {
            m_service = null;
        }
        if (m_logServiceRef == null) {
            m_context.ungetService(m_logServiceRef);
            m_logServiceRef = null;
        }

        synchronized (m_loggers) {
            for (TrackingLogger logger : m_loggers.values()) {
                logger.removed();
            }
        }
    }

    public PaxLogger getLogger(String category, String fqcn)
    {
        if (fqcn == null)
        {
            fqcn = PaxLogger.class.getName();
        }
        Bundle bundle = BundleHelper.getCallerBundle(m_context.getBundle());
        String key = fqcn + "#" + category + "#" + (bundle != null ? Long.toString(bundle.getBundleId()) : "0");
        synchronized (m_loggers) {
            TrackingLogger logger = m_loggers.get(key);
            if (logger == null)
            {
                logger = new TrackingLogger(m_service, category, bundle, fqcn);
                m_loggers.put(key, logger);
            }
            return logger;
        }
    }

    @Override
    public Set<PaxLogger> getLoggers() {
        synchronized (m_service) {
            if (m_service != null) {
                return m_service.getLoggers();
            }else{
                return Collections.emptySet();
            }
        }
    }

    @Override
    public void setLogLevel(String name, String level) {
        synchronized (m_service) {
            if (m_service != null) {
                m_service.setLogLevel(name, level);
            }
        }
    }

    public PaxLoggingService getPaxLoggingService()
    {
        return m_service;
    }

    public void dispose()
    {
        if (m_logServiceRef != null)
        {
            m_context.ungetService(m_logServiceRef);
            m_logServiceRef = null;
        }
        m_context = null;
    }

    public Bundle getBundle()
    {
        return m_context.getBundle();
    }
}
