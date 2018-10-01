package Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class XmlNode {
	private Map<String, String> Attributes = new HashMap<String, String>();
	private List<XmlNode> ChildNodes = new ArrayList<XmlNode>();
	private String NodeName;
	private String InnerText;

	public XmlNode(String nodeName) {
		NodeName = nodeName;
	}

	public Map<String, String> getAttributes() {
		return Attributes;
	}

	public String getAttribute(String name) {
		return Attributes.get(name);
	}

	public void setAttribute(String name, String value) {
		Attributes.put(name, value);
	}

	public List<XmlNode> getChildNodes() {
		return ChildNodes;
	}

	public XmlNode getChildNode(String name) {
		Iterator<XmlNode> iNode = ChildNodes.iterator();
		XmlNode node = null;
		boolean bolFlag = false;
		while (iNode.hasNext()) {
			node = iNode.next();
			if (name.equalsIgnoreCase(node.getNodeName())) {
				bolFlag = true;
				break;
			}
		}
		if (!bolFlag) {
			node = null;
		}
		return node;
	}

	public List<XmlNode> getChildNodes(String name) {
		List<XmlNode> lstNode = new ArrayList<XmlNode>();
		Iterator<XmlNode> iNode = ChildNodes.iterator();
		XmlNode node = null;
		while (iNode.hasNext()) {
			node = iNode.next();
			if (name.equalsIgnoreCase(node.getNodeName())) {
				lstNode.add(node);
			}
		}
		return lstNode;
	}

	public void setChildNode(String name, XmlNode node) {
		ChildNodes.add(node);
	}

	public String getInnerText() {
		return InnerText;
	}

	public void setInnerText(String innerText) {
		InnerText = innerText;
	}

	public String getNodeName() {
		return NodeName;
	}

	public void setNodeName(String nodeName) {
		NodeName = nodeName;
	}

	public String getInnerXML() {
		if (ChildNodes.size() == 0) {
			return InnerText;
		}
		StringBuilder sbXml = new StringBuilder();
		for (XmlNode nodeTemp : ChildNodes) {
			sbXml.append(getInnerXML(nodeTemp));
		}
		return sbXml.toString();
	}

	private String getInnerXML(XmlNode node) {
		if (node == null) {
			return null;
		}
		String strName = node.getNodeName();
		String strText = node.getInnerText();
		Map<String, String> mapAttr = node.getAttributes();
		StringBuilder sbXml = new StringBuilder();
		sbXml.append("<").append(strName);
		Iterator<Entry<String, String>> iEntry = mapAttr.entrySet().iterator();
		Entry<String, String> entryAttr;
		while (iEntry.hasNext()) {
			entryAttr = iEntry.next();
			sbXml.append(" ").append(entryAttr.getKey()).append("=\"").append(entryAttr.getValue()).append("\"");
		}
		sbXml.append(">");
		if (strText != null) {
			sbXml.append(strText);
		} else {
			for (XmlNode nodeTemp : node.getChildNodes()) {
				sbXml.append(getInnerXML(nodeTemp));
			}
		}
		sbXml.append("</").append(strName).append(">");
		return sbXml.toString();
	}
}
