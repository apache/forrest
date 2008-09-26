/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.dispatcher.impl.helper;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.OMNamespaceImpl;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.JaxenException;

/**
 * Issue: WSCOMMONS-389
 * 
 * As soon the above issue is fixed we need to drop this implementation since it
 * will be in the AXIOMXPath.
 * 
 * The process should be
 * <ol>
 * <li>stripping this class that it is only extending super and mark this class
 * as deprecated
 * <li>remove it the next release.
 * 
 * @version 1.0
 * 
 */
public class AXIOMXPathPatched extends AXIOMXPath {

  private static final long serialVersionUID = -4670206430863692541L;
  private String xpathExpr;
  private OMFactory factory;

  public AXIOMXPathPatched(String xpathExpr) throws JaxenException {
    super(xpathExpr);
    this.xpathExpr = xpathExpr;
    this.factory = OMAbstractFactory.getOMFactory();
  }

  public Object selectSingleNode(OMElement root, boolean create)
      throws JaxenException {
    Object xpathNode = super.selectSingleNode(root);
    if (xpathNode == null && create) {
      int endIndex = xpathExpr.lastIndexOf("/");
      String xpath = xpathExpr.substring(0, endIndex);
      String rest = xpathExpr.substring(endIndex + 1);
      xpathNode = createNode(root, xpath, rest);
    }
    return xpathNode;
  }

  private Object createNode(OMElement root, String path, String rest)
      throws JaxenException {
    String m_xpath = path;
    Object xpathNode = null;
    while (true) {
      AXIOMXPath xpath = getNsXPath(m_xpath);
      xpathNode = xpath.selectSingleNode(root);
      if (xpathNode == null) {
        int endIndex = path.lastIndexOf("/");
        m_xpath = path.substring(0, endIndex);
        rest = path.substring(endIndex + 1) + "/" + rest;
      } else if (xpathNode != null) {
        xpathNode = calculatePath(root, (OMElement) xpathNode, rest);
        break;
      }

    }
    return xpathNode;
  }

  /**
   * @param root
   * @param node
   * @param rest
   * @return
   */
  private Object calculatePath(OMElement root, OMElement node, String rest) {
    String[] tokenizer = rest.split("/");
    LinkedHashSet<OMElement> pathNodes = new LinkedHashSet<OMElement>();
    for (String pathPart : tokenizer) {
      if (!pathPart.equals("")) {
        calculateNsElements(pathNodes, pathPart);
      }
    }// now we have all nodes created but with no hierarchy
    OMElement[] elements = new OMElement[0];
    elements = pathNodes.toArray(new OMElement[pathNodes.size()]);
    // create the hierarchy by adding the last node to the
    // previous one till we reached the first one.
    for (int j = elements.length - 1; j > 0; j--) {
      elements[j - 1].addChild(elements[j]);
    }
    // Get rid of the overhead and expose just one node
    OMElement firstNode = elements[0];
    if (node == null) {
      root.addChild(firstNode);
    } else {
      node.addChild(firstNode);
    }
    // get the last not since it is the one we are looking for
    OMElement lastNode = elements[elements.length - 1];
    return lastNode;
  }

  /**
   * @param pathNodes
   * @param pathPart
   */
  private void calculateNsElements(LinkedHashSet<OMElement> pathNodes,
      String pathPart) {

    /*
     * FIXME: Need to test xpath expression such as [@id='example'] I think
     * their are NOT generated correctly!
     */
    OMElement element = null;
    OMNamespaceImpl localSpace = null;
    String localName = pathPart;
    String[] nameSpacedNode = localName.split(":");
    if (nameSpacedNode.length == 2) {
      final String prefix = nameSpacedNode[0];
      final String uri = (String) getNamespaces().get(prefix);
      localSpace = new OMNamespaceImpl(uri, prefix);
      localName = nameSpacedNode[1];
    }
    element = factory.createOMElement(localName, localSpace);
    pathNodes.add(element);
  }

  /**
   * @param path
   * @return
   * @throws JaxenException
   */
  private AXIOMXPath getNsXPath(String path) throws JaxenException {
    AXIOMXPath xpath = new AXIOMXPath(path);
    Iterator iterator = this.getNamespaces().keySet().iterator();
    while (iterator.hasNext()) {
      String key = (String) iterator.next();
      String value = (String) getNamespaces().get(key);
      xpath.addNamespace(key, value);
    }
    return xpath;
  }

}
