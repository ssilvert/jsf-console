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

package org.jboss.jsfconsole.plugin.managementmodel;

import org.jboss.warletcontainer.Warlet;
import org.jboss.warletcontainer.WarletUtil;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2013 Red Hat Inc.
 */
public class MgtModelWarlet implements Warlet {

    @Override
    public String getName() {
        return "management-model";
    }

    @Override
    public String getIcon() {
        return "sitemap";
    }

    @Override
    public String getHomePage() {
        return "/mgt-model/managementModel.xhtml";
    }

    @Override
    public String getMenuLabel() {
        return WarletUtil.expressionAsString("#{mgtmsg.managementmodel}");
    }

    @Override
    public boolean isLaunchable() {
        return true;
    }

    @Override
    public boolean isViewKnown(String viewId) {
        return getHomePage().equals(viewId);
    }

}
