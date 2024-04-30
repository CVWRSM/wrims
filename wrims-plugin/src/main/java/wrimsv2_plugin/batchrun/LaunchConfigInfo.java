package wrimsv2_plugin.batchrun;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class LaunchConfigInfo {
	
	private HashMap fAttributes=new HashMap();
	
	private ILaunchConfigurationType fType;
	
	public LaunchConfigInfo(String filePath){
		
		Element root = null;
		
		try {
			InputStream stream = new FileInputStream(filePath);
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			root = parser.parse(new InputSource(stream)).getDocumentElement();
			initializeFromXML(root);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
	}
	
	protected void initializeFromXML(Element root) {
		if (!root.getNodeName().equalsIgnoreCase("launchConfiguration")) { //$NON-NLS-1$
			return;
		}
		
		//read type
		String id = root.getAttribute("type"); //$NON-NLS-1$
		if (id == null) {
			return;
		}
		
		DebugPlugin debugPlugin = DebugPlugin.getDefault();
		if (debugPlugin !=null){
			ILaunchManager launchManager = debugPlugin.getLaunchManager();
			ILaunchConfigurationType type = launchManager.getLaunchConfigurationType(id);
			if (type == null) {
				return;
			}
			setType(type);
		}
		
		NodeList list = root.getChildNodes();
		int length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);
			short nodeType = node.getNodeType();
			if (nodeType == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				String nodeName = element.getNodeName();
				if (nodeName.equalsIgnoreCase("stringAttribute")) { //$NON-NLS-1$
					setStringAttribute(element);
				} else if (nodeName.equalsIgnoreCase("intAttribute")) { //$NON-NLS-1$
					setIntegerAttribute(element);
				} else if (nodeName.equalsIgnoreCase("booleanAttribute"))  { //$NON-NLS-1$
					setBooleanAttribute(element);
				} else if (nodeName.equalsIgnoreCase("listAttribute")) {   //$NON-NLS-1$
					setListAttribute(element);					
				} else if (nodeName.equalsIgnoreCase("mapAttribute")) {    //$NON-NLS-1$
					setMapAttribute(element);										
				}
			}		
		}
	}	
	
	protected void setType(ILaunchConfigurationType type) {
		fType = type;
	}
	
	protected void setAttribute(String key, Object value) {
		if (value == null) {
			getAttributeTable().remove(key);
		} else {
			getAttributeTable().put(key, value);
		}
	}
	
	protected void setStringAttribute(Element element) {
		String key = getKeyAttribute(element);
		String value = getValueAttribute(element);
		setAttribute(key, value);
	}
	
	protected void setIntegerAttribute(Element element) {
		String key = getKeyAttribute(element);
		String value = getValueAttribute(element);
		setAttribute(key, new Integer(value));
	}
	
	protected void setBooleanAttribute(Element element) {
		String key = getKeyAttribute(element);
		String value = getValueAttribute(element);
		setAttribute(key, new Boolean(value));
	}
	
	protected void setListAttribute(Element element) {
		String listKey = element.getAttribute("key");  //$NON-NLS-1$		
		NodeList nodeList = element.getChildNodes();
		int entryCount = nodeList.getLength();
		List list = new ArrayList(entryCount);
		for (int i = 0; i < entryCount; i++) {
			Node node = nodeList.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element subElement = (Element) node;
				String nodeName = subElement.getNodeName();				
				if (!nodeName.equalsIgnoreCase("listEntry")) { //$NON-NLS-1$
					return; 
				}
				String value = getValueAttribute(subElement);
				list.add(value);
			}
		}
		setAttribute(listKey, list);
	}
	
	protected void setMapAttribute(Element element) {
		String mapKey = element.getAttribute("key");  //$NON-NLS-1$
		NodeList nodeList = element.getChildNodes();
		int entryCount = nodeList.getLength();
		Map map = new HashMap(entryCount);
		for (int i = 0; i < entryCount; i++) {
			Node node = nodeList.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element subElement = (Element) node;
				String nodeName = subElement.getNodeName();				
				if (!nodeName.equalsIgnoreCase("mapEntry")) { //$NON-NLS-1$
					return; 
				}
				String key = getKeyAttribute(subElement);
				String value = getValueAttribute(subElement);
				map.put(key, value);
			}
		}
		setAttribute(mapKey, map);
	}	
	
	private HashMap getAttributeTable() {
		return fAttributes;
	}
	
	public String getStringAttribute(String key, String defaultValue) {
		Object attr = getAttributeTable().get(key);
		if (attr != null) {
			if (attr instanceof String) {
				return (String)attr;
			} 
		}
		return defaultValue;
	}

	public int getIntAttribute(String key, int defaultValue) {
		Object attr = getAttributeTable().get(key);
		if (attr != null) {
			if (attr instanceof Integer) {
				return ((Integer)attr).intValue();
			} 
		}
		return defaultValue;
	}

	public boolean getBooleanAttribute(String key, boolean defaultValue) {
		Object attr = getAttributeTable().get(key);
		if (attr != null) {
			if (attr instanceof Boolean) {
				return ((Boolean)attr).booleanValue();
			} 
		}
		return defaultValue;
	}

	public List getListAttribute(String key, List defaultValue) {
		Object attr = getAttributeTable().get(key);
		if (attr != null) {
			if (attr instanceof List) {
				return (List)attr;
			} 
		}
		return defaultValue;
	}

	public Map getMapAttribute(String key, Map defaultValue) {
		Object attr = getAttributeTable().get(key);
		if (attr != null) {
			if (attr instanceof Map) {
				return (Map)attr;
			} 
		}
		return defaultValue;
	}
	
	protected String getKeyAttribute(Element element) {
		String key = element.getAttribute("key"); //$NON-NLS-1$
		if (key == null) {
			return null;
		}
		return key;
	}

	protected String getValueAttribute(Element element) {
		String value = element.getAttribute("value"); //$NON-NLS-1$
		if (value == null) {
			return null;
		}
		return value;
	}

}