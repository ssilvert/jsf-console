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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2013 Red Hat Inc.
 */
@ManagedBean(name = "snapControl")
@ViewScoped
public class ShowSnapshotController implements Serializable {

    private Set<String> openFiles = new HashSet<String>();
    private String hostSelection = "";

    public void setHostSelection(String host) {
        this.hostSelection = host;
    }

    public String getHostSelection() {
        return this.hostSelection;
    }

    public String getHostAddress() {
        if (hostSelection == null || hostSelection.equals("")) {
            return "";
        }

        return "/host=" + hostSelection;
    }

    public String fileContents(String directory, String fileName) {
        try {
            Scanner scanner = new Scanner(new File(directory, fileName), "UTF-8");
            String text = scanner.useDelimiter("\\A").next();
            scanner.close();
            return text;
        } catch (FileNotFoundException e) {
            return "File not found: " + directory + fileName;
        }
    }

    public void eyeClickOpen(AjaxBehaviorEvent event) {
        String id = event.getComponent().getClientId();
        String fileName = id.replaceFirst("open", "");
        openFiles.add(fileName);
    }

    public void eyeClickClose(AjaxBehaviorEvent event) {
        String id = event.getComponent().getClientId();
        String fileName = id.replaceFirst("close", "");
        openFiles.remove(fileName);
    }

    public boolean isOpen(String fileName) {
        return openFiles.contains(fileName);
    }
}
