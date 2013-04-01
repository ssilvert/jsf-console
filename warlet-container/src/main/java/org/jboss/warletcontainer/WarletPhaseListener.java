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

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * If requested, start a WARlet in the before phase of RENDER_RESPONSE.
 *
 * @author ssilvert
 * @since 1.0
 */
public class WarletPhaseListener implements PhaseListener
{
    private WarletContainer warletContainer;

    public WarletPhaseListener()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext extContext = facesContext.getExternalContext();
        this.warletContainer = (WarletContainer) extContext.getApplicationMap().get(WarletContainer.APP_SCOPE_KEY);
    }

    @Override
    public void afterPhase(PhaseEvent pe)
    {
        // do nothing
    }

    @Override
    public PhaseId getPhaseId()
    {
        return PhaseId.RENDER_RESPONSE;
    }

    @Override
    public void beforePhase(PhaseEvent pe)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (isStartWarlet(facesContext))
        {
            processStartWarlet(facesContext);
        }
    }

    private boolean isStartWarlet(FacesContext facesContext)
    {
        return facesContext.getExternalContext().getRequestParameterMap().containsKey("warlet.start");
    }

    private void processStartWarlet(FacesContext facesContext)
    {

        String warletName = facesContext.getExternalContext().getRequestParameterMap().get("warlet.start");
        Warlet warlet = warletContainer.getWarlet(warletName);
        if (warlet == null)
        {
            throw new RuntimeException("warlet.start for unknown warlet " + warletName);
        }

        WarletScope warletScope = new WarletScope(warlet);
        warletScope.start();
    }
}