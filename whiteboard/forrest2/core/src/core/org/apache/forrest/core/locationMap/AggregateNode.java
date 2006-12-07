package org.apache.forrest.core.locationMap;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AggregateNode extends AbstractSourceNode {
	List<AbstractSourceNode> nodes = new ArrayList<AbstractSourceNode>();

	public AggregateNode(Node element) throws URISyntaxException, IOException {
		super();

		final NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			String nodeName = child.getNodeName();
			if (nodeName != null && nodeName.equals("source")) {
				nodes.add(new SourceNode(child));
			}
		}
	}

	public List<AbstractSourceNode> getNodes() {
		return nodes;
	}

}
