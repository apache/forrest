/*
* Copyright 2002-2004 The Apache Software Foundation or its licensors,
* as applicable.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.forrest.forrestdoc.ant.doc;

import junit.framework.*;
import org.apache.tools.ant.*;

public class VizPrinterTest extends TestCase {
    VizPrinter printer;
    BufferedWriter writer;

    public VizPrinterTest(String name) {
	super(name);
    }

    public void setUp() {
	printer = new VizPrinter();
	writer = new BufferedWriter();
	printer.setWriter(writer);
    }

    public void testPrintEmpty() {
	printer.print();
	assertEquals("digraph \"G\" {\n" 
		     + "    graph [\"rankdir\"=\"LR\",];\n"
		     + "}\n", writer.getString());
    }

    public void testSetAttributes() {
	printer.setGraphid("build");

	VizAttrStmt graph = new VizAttrStmt();
	graph.setType("graph");
	graph.addAttribute("label", "test");
	graph.addAttribute("label", "test2");
	printer.addAttributeStatement(graph);

	VizAttrStmt edge = new VizAttrStmt();
	edge.setType("edge");
	edge.addAttribute("a", "3");
	edge.addAttribute("b", "2");
	edge.addAttribute("c", "1");
	printer.addAttributeStatement(edge);

	VizAttrStmt node = new VizAttrStmt();
	edge.setType("node");
	edge.addAttribute("c", "1");
	edge.addAttribute("b", "2");
	edge.addAttribute("a", "3");
	printer.addAttributeStatement(node);

	printer.print();
	assertEquals("digraph \"build\" {\n" 
		     + "    graph [\"rankdir\"=\"LR\",\"label\"=\"test2\",];\n"
		     + "    node [\"rankdir\"=\"LR\",\"label\"=\"test2\",];\n"
		     + "}\n", writer.getString());
    }

}
