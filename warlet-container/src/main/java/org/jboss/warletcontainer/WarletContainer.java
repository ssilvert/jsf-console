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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

/**
 * The WarletContainer manages all WARlets deployed in the WAR.
 *
 * @author Stan Silvert
 * @since 1.0
 */
@HandlesTypes({org.jboss.warletcontainer.Warlet.class})
public class WarletContainer implements ServletContainerInitializer {

    public static final String APP_SCOPE_KEY = "warlets";
    private List<Warlet> registeredWarlets = new ArrayList<Warlet>();
    private Map<String, Warlet> warletsByName = new HashMap<String, Warlet>();

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
        ctx.setAttribute(APP_SCOPE_KEY, this);

        if (classes == null) return;

        for (Class clazz : classes) {
            if (clazz.isInterface()) {
                continue;
            }

            try {
                //System.out.println("***** found warlet: " + clazz.getName());
                Warlet warlet = (Warlet) clazz.newInstance();
                registeredWarlets.add(warlet);
                warletsByName.put(warlet.getName(), warlet);
            } catch (Exception e) {
                throw new ServletException("Unable to load Warlet: " + clazz.getName(), e);
            }
        }
    }

    /**
     * Get a list of all registered WARlets.
     *
     * @return
     */
    public List<Warlet> getList() {
        return this.registeredWarlets;
    }

    /**
     * Get a WARlet by name.
     *
     * @param warletName The name of the WARlet to retrieve.
     *
     * @return The WARlet, or <code>null</code> if no WARlet by that name is deployed.
     */
    public Warlet getWarlet(String warletName) {
        return this.warletsByName.get(warletName);
    }
    /*
     private void populateViews(Warlet warlet) throws IOException
     {
     String warletName = warlet.getName();
     String myJar = findMyJar(warlet);
     System.out.println("*************************");
     System.out.println("warletName=" + warletName);
     System.out.println("my jar=" + findMyJar(warlet));
     ClassLoader myLoader = warlet.getClass().getClassLoader();
     Set<String> paths = servletCtx.getResourcePaths("/");
     if (paths == null) return;

     for (String path : paths)
     {
     System.out.println("found Resource=" + path);
     //URL url = myLoader.getResource(path);

     if (servletCtx.getRealPath(path).startsWith(myJar))
     {
     System.out.println("found view=" + path);
     }
     }
     System.out.println("*************************");
     }

     private String findMyJar(Warlet warlet)
     {
     String name = warlet.getClass().getName();
     String path = name.replace('.', '/') + ".class";
     URL url = warlet.getClass().getClassLoader().getResource("/" + path);
     String urlPath = url.getPath();
     return urlPath.substring(0, urlPath.length() - path.length());
     }
     */
}
