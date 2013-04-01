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
package org.jboss.jsfconsole.util;

import java.io.IOException;
import java.util.Date;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import org.jboss.cliresolver.NativeExecutor;
import org.jboss.dmr.ModelNode;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2013 Red Hat Inc.
 */
@ManagedBean
@ApplicationScoped
public class CliUtil {

    public static String helpText(ModelNode address) throws IOException {
        ModelNode result = readRscDesc(address);
        return result.get("result", "description").asString();
    }

    public static String helpText(ModelNode address, String attribute) throws IOException {
        ModelNode result = readRscDesc(address);
        return result.get("result", "attributes", attribute, "description").asString();
    }

    public static Date asDate(long millis) {
        return new Date(millis);
    }

    private static ModelNode readRscDesc(ModelNode address) throws IOException {
        ModelNode request = address.clone();
        request.get("operation").set("read-resource-description");
        return NativeExecutor.executeOperation(request);
    }
}
