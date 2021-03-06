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

package org.jboss.jsfconsole.plugin.snapshotmgr;

import javax.faces.context.FacesContext;
import org.jboss.warletcontainer.Warlet;
import org.jboss.warletcontainer.WarletUtil;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2013 Red Hat Inc.
 */
public class SnapshotMgrWarlet implements Warlet {

    public String getName() {
        return "snapshot-mgr";
    }

    public String getIcon() {
        return "camera";
    }

    public String getHomePage() {
        return "/snapshot-mgr/snapshotMgr.xhtml";
    }

    public String getMenuLabel() {
        return WarletUtil.expressionAsString("#{snapmsg.configsnapshots}");
    }

    public boolean isLaunchable() {
        return true;
    }

    public boolean isViewKnown(String viewId) {
        return getHomePage().equals(viewId);
    }

}
