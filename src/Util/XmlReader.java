package Util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlReader extends DefaultHandler {
	private StringBuilder curValue = new StringBuilder();
	private ArrayList<XmlNode> lstNodes = new ArrayList<XmlNode>();
	private XmlNode rootNode;
	private XmlNode curNode;

	public static XmlReader Read(String FileName) {
		XmlReader xmlReader = null;
		SAXParser saxPareser = null;
		try {
			saxPareser = SAXParserFactory.newInstance().newSAXParser();
			xmlReader = new XmlReader();
			saxPareser.parse(FileName, xmlReader);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			saxPareser = null;
		}
		return xmlReader;
	}

	public static XmlReader Read(byte[] arg) {
		XmlReader xmlReader = null;
		SAXParser saxPareser = null;
		InputStream input = new ByteArrayInputStream(arg);
		try {
			saxPareser = SAXParserFactory.newInstance().newSAXParser();
			xmlReader = new XmlReader();
			saxPareser.parse(input, xmlReader);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			input = null;
			saxPareser = null;
		}
		return xmlReader;
	}

	public String GetXmlValue(String Key) {
		XmlNode node = GetXmlNode(Key);
		if (node == null) {
			return null;
		}
		return node.getInnerText();
	}

	public XmlNode GetXmlNode(String Key) {
		String[] arrKey = Key.split("/");
		XmlNode node = rootNode;
		for (int i = 1; i < arrKey.length; i++) {
			if (node == null) {
				break;
			}
			node = node.getChildNode(arrKey[i]);
		}

		if (node == null) {
			return null;
		}
		return node;
	}

	public String[] GetXmlValues(String Key) {
		List<XmlNode> lstNode = GetXmlNodes(Key);
		int iCount = lstNode.size();
		String[] arrRet = new String[iCount];
		for (int i = 0; i < iCount; i++) {
			arrRet[i] = lstNode.get(i).getInnerText();
		}
		lstNode = null;
		return arrRet;
	}

	public List<XmlNode> GetXmlNodes(String Key) {
		String[] arrKey = Key.split("/");
		XmlNode node = rootNode;
		List<XmlNode> lstNode = node.getChildNodes(arrKey[1]);
		for (int i = 2; i < arrKey.length; i++) {
			lstNode = GetXmlValues(lstNode, arrKey[i]);
		}
		return lstNode;
	}

	private List<XmlNode> GetXmlValues(List<XmlNode> nodes, String name) {
		List<XmlNode> lstRet = new ArrayList<XmlNode>();
		List<XmlNode> lstNode;
		Iterator<XmlNode> iNode = nodes.iterator();
		XmlNode node;
		while (iNode.hasNext()) {
			node = iNode.next();
			lstNode = node.getChildNodes(name);
			for (XmlNode nodeTemp : lstNode) {
				lstRet.add(nodeTemp);
			}
		}
		return lstRet;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		XmlNode node;
		if (rootNode == null) {
			rootNode = new XmlNode(qName);
			node = rootNode;
		} else {
			node = new XmlNode(qName);
			curNode.setChildNode(qName, node);
		}
		PushNode(node);
		curNode = node;

		int iLength = attributes.getLength();
		for (int i = 0; i < iLength; i++) {
			curNode.setAttribute(attributes.getLocalName(i), attributes.getValue(i));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		PopNode();
		int iTextLen = curValue.length();
		if (iTextLen > 0) {
			curNode.setInnerText(curValue.toString());
			curValue.delete(0, iTextLen);
		}
		curNode = GetCurNode();
	}

	@Override
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		super.characters(arg0, arg1, arg2);
		curValue.append(new String(arg0, arg1, arg2).trim());
	}

	private XmlNode GetCurNode() {
		int iSize = lstNodes.size();
		if (iSize == 0) {
			return null;
		}
		return lstNodes.get(iSize - 1);
	}

	private void PushNode(XmlNode node) {
		lstNodes.add(node);
	}

	private XmlNode PopNode() {
		int iSize = lstNodes.size();
		XmlNode node = lstNodes.get(iSize - 1);
		lstNodes.remove(iSize - 1);
		return node;
	}
}
