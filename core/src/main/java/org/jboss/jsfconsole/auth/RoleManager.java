/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.jsfconsole.auth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.jboss.dmr.ModelNode;
import org.jboss.jsfconsole.managementmodel.ManagementModelNode;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2013 Red Hat Inc.
 */
@ManagedBean(name = "roleManager")
@ApplicationScoped
public class RoleManager {

    private String[] roles = {"user", "superuser", "super-duper-user"};

    private Map<String, ManagementModelNode> mgtAuthRoots = new HashMap<String, ManagementModelNode>();

    private final Map<String, Set<String>> pluginsBlockedAccess = new HashMap<String, Set<String>>();

    // <role, Set<address>>
    private final Map<String, Set<ModelNode>> resourceBlockedAccess = new HashMap<String, Set<ModelNode>>();

    // <role, Map<address, Set<attribute>>
    private final Map<String, Map<ModelNode, Set<String>>> attributeBlockedAccess = new HashMap<String, Map<ModelNode, Set<String>>>();

    public RoleManager() {
        for (String role : roles) {
            mgtAuthRoots.put(role, new ManagementModelNode());
            pluginsBlockedAccess.put(role, new HashSet());
            resourceBlockedAccess.put(role, new HashSet());
            attributeBlockedAccess.put(role, new HashMap());
        }
    }

    public String[] getRoles() {
        return this.roles;
    }

    public ManagementModelNode getMgtAuthRoot(String role) {
        return mgtAuthRoots.get(role);
    }

    /**
     * Determine the role for this user.  Assumes user can only be in one role at a time.
     *
     * @return The role
     */
    public String myRole() {
        ExternalContext extCtx = FacesContext.getCurrentInstance().getExternalContext();
        if (extCtx.isUserInRole("admin")) return "admin";

        for (String role : roles) {
            if (extCtx.isUserInRole(role)) return role;
        }

        throw new IllegalStateException("Current user is not assigned to any known role");
    }

    // possibly a better way?  allows you to use just one button decl in xhtml
    // but isPluginAccessBlocked gets called 3 times
    public void togglePluginAccess(String role, String pluginName) {
        synchronized(pluginsBlockedAccess) {
            if (isPluginAccessBlocked(role, pluginName)) {
                grantPluginAccess(role, pluginName);
            } else {
                blockPluginAccess(role, pluginName);
            }
        }
    }

    // TODO: optimize the thread locking
    public void blockPluginAccess(String role, String pluginName) {
        //System.out.println("blocking access for role=" + role + " plugin=" + pluginName);
        synchronized (pluginsBlockedAccess) {
            pluginsBlockedAccess.get(role).add(pluginName);
        }
    }

    public void grantPluginAccess(String role, String pluginName) {
        //System.out.println("granting access for role=" + role + " plugin=" + pluginName);
        synchronized (pluginsBlockedAccess) {
            pluginsBlockedAccess.get(role).remove(pluginName);
        }
    }

    public boolean isPluginAccessBlocked(String role, String pluginName) {
        if (role.equals("admin")) return false;
        //System.out.println("is access blocked for role=" + role + " plugin=" + pluginName + " : " + pluginsBlockedAccess.get(role).contains(pluginName));
        synchronized (pluginsBlockedAccess) {
            return pluginsBlockedAccess.get(role).contains(pluginName);
        }
    }

    public void blockResourceAccess(String role, ModelNode address) {
        synchronized (resourceBlockedAccess) {
            resourceBlockedAccess.get(role).add(address);
        }
    }

    public void grantResourceAccess(String role, ModelNode address) {
        synchronized (resourceBlockedAccess) {
            resourceBlockedAccess.get(role).remove(address);
        }
    }

    public boolean isResourceAccessBlocked(String role, ModelNode address) {
        //System.out.println("isResourceBlocked role=" + role + " addr=" + address);
        if (role.equals("admin")) return false;
        synchronized (resourceBlockedAccess) {
            return resourceBlockedAccess.get(role).contains(address);
        }
    }

    public void blockAttributeAccess(String role, ModelNode address, String attribute) {
        synchronized (attributeBlockedAccess) {
            Set<String> blockedAttribsForAddress = attributeBlockedAccess.get(role).get(address);
            if (blockedAttribsForAddress == null) {
                blockedAttribsForAddress = new HashSet<String>();
                attributeBlockedAccess.get(role).put(address, blockedAttribsForAddress);
            }
            blockedAttribsForAddress.add(attribute);
        }
    }

    public void grantAttributeAccess(String role, ModelNode address, String attribute) {
        synchronized (attributeBlockedAccess) {
            Set<String> blockedAttribsForAddress = attributeBlockedAccess.get(role).get(address);
            if (blockedAttribsForAddress == null) return;

            blockedAttribsForAddress.remove(attribute);
        }
    }

    public boolean isAttributeAccessBlocked(String role, ModelNode address, String attribute) {
        //System.out.println("isAttribBlocked role=" + role + " addr=" + address + " attrib=" + attribute);
        if (role.equals("admin")) return false;
        synchronized (attributeBlockedAccess) {
            Set<String> blockedAttribsForAddress = attributeBlockedAccess.get(role).get(address);
            if (blockedAttribsForAddress == null) return false;
            return blockedAttribsForAddress.contains(attribute);
        }
    }

}
