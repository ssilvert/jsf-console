/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.jsfconsole.managementmodel;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jboss.cliresolver.NativeExecutor;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.dmr.Property;

/**
 * A node in the management tree.  Non-leaves are addressable entities in a DMR command.  Leaves are attributes.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2012 Red Hat Inc.
 */
public class ManagementModelNode extends DefaultMutableTreeNode {

    //private CommandExecutor executor;
    private boolean isLeaf = false;
    private boolean isGeneric = false;

    /**
     * Constructor for root node only.
     */
    public ManagementModelNode() {
        //this.executor = new CommandExecutor();
        this.isLeaf = false;
        setUserObject(new UserObject());
    }

    private ManagementModelNode(UserObject userObject) {
        //this.executor = new CommandExecutor();
        this.isLeaf = userObject.isLeaf;
        this.isGeneric = userObject.isGeneric;
        if (isGeneric) setAllowsChildren(false);
        setUserObject(userObject);
    }

    @Override
    public Enumeration children() {
        explore();
        return super.children();
    }

    /**
     * Refresh children using read-resource operation.
     *
     * @return this
     */
    public ManagementModelNode explore() {
        if (isLeaf) return this;
        if (isGeneric) return this;
        removeAllChildren();
        //System.out.println("Exploring: " + this.addressPath());
        try {
            String addressPath = addressPath();
            ModelNode resourceDesc = NativeExecutor.executeCLI(addressPath + ":read-resource-description");
            resourceDesc = resourceDesc.get("result");
            ModelNode response = NativeExecutor.executeCLI(addressPath + ":read-resource(include-runtime=true,include-defaults=true)");
            ModelNode result = response.get("result");
            if (!result.isDefined()) return this;

            List<String> childrenTypes = getChildrenTypes(addressPath);
            for (ModelNode node : result.asList()) {
                Property prop = node.asProperty();
                if (childrenTypes.contains(prop.getName())) { // resource node
                    if (hasGenericOperations(addressPath, prop.getName())) {
                        add(new ManagementModelNode(new UserObject(node, prop.getName())));
                    }
                    if (prop.getValue().isDefined()) {
                        for (ModelNode innerNode : prop.getValue().asList()) {
                            String rvalue = innerNode.asProperty().getName();
                            String helpText = resourceDesc.get("children", prop.getName(), "model-description", rvalue,"description").asString();
                            UserObject usrObj = new UserObject(innerNode, prop.getName(), rvalue, helpText);
                            add(new ManagementModelNode(usrObj));
                        }
                    }
                } else { // attribute node
                    UserObject usrObj = new UserObject(node, resourceDesc, prop.getName(), prop.getValue().asString());
                    add(new ManagementModelNode(usrObj));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    private boolean hasGenericOperations(String addressPath, String resourceName) throws Exception {
        ModelNode response = NativeExecutor.executeCLI(addressPath + resourceName + "=*/:read-operation-names");//executor.doCommand(addressPath + resourceName + "=*/:read-operation-names");
        if (response.get("outcome").asString().equals("failed")) return false;

        for (ModelNode node : response.get("result").asList()) {
            if (node.asString().equals("add")) return true;
        }

        return false;
    }

    private List<String> getChildrenTypes(String addressPath) throws Exception {
        List<String> childrenTypes = new ArrayList<String>();
        ModelNode readChildrenTypes = (ModelNode)NativeExecutor.executeCLI(addressPath + ":read-children-types");
        for (ModelNode type : readChildrenTypes.get("result").asList()) {
            childrenTypes.add(type.asString());
        }
        return childrenTypes;
    }

    /**
     * Get the DMR path for this node.  For leaves, the DMR path is the path of its parent.
     * @return The DMR path for this node.
     */
    public String addressPath() {
        if (isLeaf) {
            ManagementModelNode parent = (ManagementModelNode)getParent();
            return parent.addressPath();
        }

        StringBuilder builder = new StringBuilder("/"); // start with root
        for (Object pathElement : getUserObjectPath()) {
            String pathElementStr = pathElement.toString();
            if (pathElementStr.equals("/")) continue; // don't want to escape root

            UserObject userObj = (UserObject)pathElement;
            builder.append(userObj.getName());
            builder.append("=");
            builder.append(userObj.getEscapedValue());
            builder.append("/");
        }

        return builder.toString();
    }

    public ModelNode getDmrAddress() {
        if (isLeaf) {
            ManagementModelNode parent = (ManagementModelNode)getParent();
            return parent.getDmrAddress();
        }

        ModelNode address = new ModelNode();
        address.get("address").setEmptyList();

        for (Object pathElement : getUserObjectPath()) {
            String pathElementStr = pathElement.toString();
            if (pathElementStr.equals("/")) continue; // don't want to include root

            UserObject userObj = (UserObject)pathElement;
            ModelNode localAddr = new ModelNode();
            localAddr.get(userObj.getName()).set(userObj.getValue());
            address.get("address").add(localAddr);
        }
        return address;
    }

    @Override
    public boolean isLeaf() {
        return this.isLeaf;
    }

    @Override
    public UserObject getUserObject() {
        return (UserObject)super.getUserObject();
    }

    /**
     * A generic resource is not a true resource.  It only exists as a placeholder for
     * generic commands such as "add".  In CLI GUI it is displayed as something like "resource=*"
     */
    public boolean isGeneric() {
        return this.isGeneric;
    }

    /**
     * A defined resource is one that may have child resources or child attributes.
     */
    public boolean isDefinedResource() {
        return !this.isGeneric && !getUserObject().isAttribute();
    }

    /**
     * Used by rich:tree
     *
     * @return
     */
    public String getNodeType() {
       if (isGeneric()) return "generic";
       if (isDefinedResource()) return "definedResource";
       if (isRoot()) return "root";
       return "attribute";
    }

    public static String escapeAddressElement(String element) {
        element = element.replace(":", "\\:");
        element = element.replace("/", "\\/");
        element = element.replace("=", "\\=");
        element = element.replace(" ", "\\ ");
        return element;
    }

    /**
     * Encapsulates name/value pair.  Also encapsulates escaping of the value.
     */
    public class UserObject {
        private ModelNode backingNode;
        private String name;
        private String value;
        private boolean isLeaf;
        private boolean isGeneric = false;
        private String separator;
        private AttributeDescription attribDesc = null;

        /**
         * Constructor for the root node.
         */
        public UserObject() {
            this.backingNode = new ModelNode();
            this.name = "/";
            this.value = "";
            this.isLeaf = false;
            this.separator = "";
        }

        /**
         * Constructor for generic folder where resource=*.
         *
         * @param name The name of the resource.
         */
        public UserObject(ModelNode backingNode, String name) {
            this.backingNode = backingNode;
            this.name = name;
            this.value = "*";
            this.isLeaf = false;
            this.isGeneric = true;
            this.separator = "=";
        }

        // resource node such as subsystem=weld
        public UserObject(ModelNode backingNode, String name, String value, String helpText) {
            this.backingNode = backingNode;
            this.name = name;
            this.value = value;
            this.isLeaf = false;
            this.separator = "=";
        }

        // attribute
        public UserObject(ModelNode backingNode, ModelNode resourceDesc, String name, String value) {
            //System.out.println("Creating attribute " + name);
            //System.out.println("attr description: " + resourceDesc.get("attributes", name));
            this.attribDesc = new AttributeDescription(resourceDesc.get("attributes", name));
            this.backingNode = backingNode;
            this.name = name;
            this.value = value;
            this.isLeaf = true;
            if (this.attribDesc.isGraphable()) {
                this.separator = " \u2245 ";
            } else {
                this.separator = " => ";
            }
        }

        public ModelNode getBackingNode() {
            return this.backingNode;
        }

        public AttributeDescription getAttributeDescription() {
            return this.attribDesc;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public String getEscapedValue() {
            return ManagementModelNode.escapeAddressElement(this.value);
        }

        public boolean isLeaf() {
            return this.isLeaf;
        }

        public boolean isGeneric() {
            return this.isGeneric;
        }

        public boolean isAttribute() {
            return this.attribDesc != null;
        }

        @Override
        public String toString() {
            return this.name + this.separator + this.value;
        }
    }

    public class AttributeDescription {

        private ModelNode attributes;

        AttributeDescription(ModelNode attributes) {
            this.attributes = attributes;
        }

        public String getHelpText() {
            return attributes.get("description").asString();
        }

        /**
         * Is this a runtime attribute?
         */
        public boolean isRuntime() {
            return attributes.get("storage").asString().equals("runtime");
        }

        public ModelType getType() {
            /*if (attributes.get("type").getType() == ModelType.OBJECT) {
                return ModelType.STRING;
            }*/

            ModelNode type = attributes.get("type");
            if (!type.isDefined()) return ModelType.STRING;

            return type.asType();
        }

        public boolean isReadOnly() {
            return attributes.get("access-type").asString().equals("read-only");
        }

        public boolean isGraphable() {
            return isRuntime() && isNumeric();
        }

        public boolean isNumeric() {
            ModelType type = getType();
            return (type == ModelType.BIG_DECIMAL) ||
                   (type == ModelType.BIG_INTEGER) ||
                   (type == ModelType.DOUBLE) ||
                   (type == ModelType.INT) ||
                   (type == ModelType.LONG);
        }

        public boolean isSimpleText() {
            return !isBoolean() && !isValueLimited();
        }

        public boolean isBoolean() {
            return getType() == ModelType.BOOLEAN;
        }

        public boolean isValueLimited() {
            return !getAllowedValues().isEmpty();
        }

        public List<String> getAllowedValues() {
            List<String> allowedValues = new ArrayList<String>();
            if (!attributes.hasDefined("allowed")) return allowedValues;

            for (ModelNode value : attributes.get("allowed").asList()) {
                allowedValues.add(value.asString());
            }

            return allowedValues;
        }
    }

}