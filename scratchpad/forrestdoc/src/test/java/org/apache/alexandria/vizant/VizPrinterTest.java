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
