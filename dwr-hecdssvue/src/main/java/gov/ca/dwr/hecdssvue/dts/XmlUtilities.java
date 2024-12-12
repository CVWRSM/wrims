/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.TreeWalker;

public class XmlUtilities {

    public static Document newDocument() throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(false);
            return factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        }

    }


    public static Element getNextElement(TreeWalker treeWalker, String nodeName) {
        for (Node nextNode = treeWalker.nextNode(); nextNode != null; nextNode = treeWalker.nextNode()) {
            if (nextNode.getNodeType() == Node.ELEMENT_NODE &&
                (nodeName == null || nodeName.equals(nextNode.getNodeName()))) {
                return (Element) nextNode;
            }
        }
        return null;
    }

    /**
     * @param document
     * @param filename
     * @throws Exception
     */
    public static void saveTo(Node document, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(writer);
            transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }
}
