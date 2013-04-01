/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.warletcontainer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PostConstructCustomScopeEvent;
import javax.faces.event.PreDestroyCustomScopeEvent;
import javax.faces.event.ScopeContext;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * Custom scope for managing the lifecycle of a WARlet.
 *
 * @author Stan Silvert
 * @since 1.0
 */
public class WarletScope extends HashMap implements HttpSessionBindingListener
{
    public static final String WARLETSCOPE_SESSION_KEY = "warletScope";
    private static final Application application;
    
    // Get the Application once so we don't have to get it over and over.
    static
    {
        ApplicationFactory appFactory = (ApplicationFactory)
             FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        application = appFactory.getApplication();
    }

    private final Warlet warlet;
    private final ScopeContext scopeContext;

    private boolean preDestroyEmitted = false;

    public WarletScope(Warlet warlet)
    {
        this.warlet = warlet;
        this.scopeContext = new ScopeContext("WarletScope for " + warlet.getName(), this);
        this.put("useTemplate", "/warletcontainer/defaulttemplate.xhtml");
    }

    private static ExternalContext externalContext()
    {
        return FacesContext.getCurrentInstance().getExternalContext();
    }
    
    /**
     *  Static accessor for the WarletScope in the current session.
     * 
     * @return The WarletScope if active.  Otherwise, return <code>null</code>.
     */
    public static WarletScope getInstance()
    {
        ExternalContext extCtx = externalContext();
        if (extCtx.getSession(false) == null) return null;

        Map sessionMap = extCtx.getSessionMap();
        return (WarletScope)sessionMap.get(WARLETSCOPE_SESSION_KEY);
    }

    /**
     * This method starts the WarletScope.  It first adds any params found
     * in the HttpServletRequest that have a prefix of 'warletparam.'
     * Then it adds the WarletScope to the HttpSession and emits the
     * PostConstructCustomScopeEvent.
     */
    public void start()
    {
        // add warlet params
        Map<String, String> paramMap = externalContext().getRequestParameterMap();
        for (Iterator<String> params = paramMap.keySet().iterator(); params.hasNext();)
        {
            String param = params.next();
            if (param.startsWith("warletparam."))
            {
                String paramName = param.substring("warletparam.".length(), param.length());
                String paramValue = paramMap.get(param);
                //System.out.println("putting warlet scope name=" + paramName + " value=" + paramValue);
                this.put(paramName, paramValue);
            }
        }

        externalContext().getSessionMap().put(WARLETSCOPE_SESSION_KEY, this);
    }

    /**
     * This method stops the WarletScope by emitting the
     * PreDestroyCustomScopeEvent and removing it from the HttpSession.
     */
    public void stop()
    {
        emitScopeEvent(PreDestroyCustomScopeEvent.class);
        preDestroyEmitted = true;
        externalContext().getSessionMap().remove(WARLETSCOPE_SESSION_KEY);
    }

    /**
     * Get the WARlet associated with this scope.
     *
     * @return The WARlet
     */
    public Warlet getWarlet()
    {
        return this.warlet;
    }

    // implementation of HttpSessionBindingListener
    @Override
    public void valueBound(HttpSessionBindingEvent event)
    {
        emitScopeEvent(PostConstructCustomScopeEvent.class);
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event)
    {
        // Removal of the WarletScope from the HttpSession constitutes
        // "stopping" the scope.  So normally the pre-destroy event would be
        // emitted before the scope is unbound.  However, in instances such as
        // shutdown the scope can be unbound without an explicit call to stop().
        // So we emit the pre-destroy event here for completeness.
        if (!preDestroyEmitted)
        {
            emitScopeEvent(PreDestroyCustomScopeEvent.class);
            preDestroyEmitted = true;
        }
    }

    private void emitScopeEvent(Class scopeEventClass)
    {
        application.publishEvent(FacesContext.getCurrentInstance(),
                                 scopeEventClass,
                                 scopeContext);
    }
}