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

import javax.faces.component.UIViewRoot;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PreRenderViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

/**
 * This SystemEventListener listens for the PreRenderViewEvent.  If JSF is
 * about to render a view that the WARlet doesn't handle then stop the WARlet.
 *
 * @author Stan Silvert
 * @since 1.0
 */
public class WarletPreRenderViewListener implements SystemEventListener
{
   
   public WarletPreRenderViewListener()
   {
   }
   
   @Override
   public boolean isListenerForSource(Object source)
   {
      return (source instanceof UIViewRoot);
   }

   /**
    * If JSF is about to render a view that the WARlet doesn't handle then
    * stop the WARlet.
    *
    * @param event The event from JSF
    *
    * @throws AbortProcessingException
    */
   @Override
   public void processEvent(SystemEvent event) throws AbortProcessingException
   {
      if (!(event instanceof PreRenderViewEvent)) return;
      
      WarletScope warletScope = WarletScope.getInstance();
      if (warletScope == null) return;
      
      UIViewRoot uiViewRoot = (UIViewRoot)event.getSource();
      if (!warletScope.getWarlet().isViewKnown(uiViewRoot.getViewId()))
      {
         warletScope.stop();
      }
   }
   
}