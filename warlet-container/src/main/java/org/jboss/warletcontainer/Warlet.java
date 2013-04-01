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

/**
 * This interface defines a Warlet.
 *
 * @author Stan Silvert
 * @since 1.0
 */
public interface Warlet
{

   /**
    * The name of the WARlet.  This should be named such that it will not
    * cause name collisions with other WARlets in the wild.
    *
    * @return The name.
    */
   public String getName();

   /**
    * The icon for this WARlet.
    *
    * @return The icon.
    */
   public String getIcon();

   /**
    * This is an action method that returns the home page outcome of the WARlet.
    * The outcome can be a literal view ID or an outcome that feeds into a
    * navigation rule.
    *
    * @return The Home Page of the Warlet.
    */
   public String getHomePage();

   /**
    * This is the suggested label for a UI element that launches the WARlet.
    *
    * @return The label.
    */
   public String getMenuLabel();

   /**
    * This method tells if the WARlet can be launched at this time.
    *
    * @return <code>true</code> if this WARlet is in a state where it can/should
    *         be lanuched.  <code>false</code> otherwise.
    */
   public boolean isLaunchable();

   /**
    * Is the given view part of this Warlet?
    *
    * @param viewId The View Id
    *
    * @return <code>true</code> if the view is part of the Warlet,
    *         <code>false</code> otherwise.
    */
   public boolean isViewKnown(String viewId);
}
