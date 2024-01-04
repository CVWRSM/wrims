package gov.ca.dwr.jdiagram;

import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.mindfusion.diagramming.XmlException;

public class XmlUtilities {

	public static Document newDocument() throws Exception {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			return factory.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException ex) {
			throw new XmlException(ex);
		}

	}

	/**
	 * 
	 * @param document
	 * @param filename
	 * @throws Exception
	 */
	public static void saveTo(Node document, String filename) throws Exception {
		FileWriter writer = null;
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(writer = new FileWriter(
					filename));
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
			transformer.transform(source, result);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
