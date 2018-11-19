package ne.wsdlparser.lib.xsd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;

public class XSDManager {

    private WSDLManagerRetrieval wsdlManager;
    private String targetNS;
    private String name;
    private Node inlineSchema;
    private String workingdir;
    private XSDFile xsd;

    public XSDManager(WSDLManagerRetrieval wsdlManager, String workingdir, NodeList schemas) throws WSDLException {
        this.workingdir = workingdir;
        // this.targetNS = Utils.getAttrValueFromNode(schemas, "targetNamespace");

        this.xsd = new XSDFile(workingdir, schemas);

    }

    public Object find(String xpath, Object source, QName returnType) throws XPathExpressionException {
        return this.xsd.find(xpath, source, returnType);

    }

    public Object find(String xpath, QName returnType) throws XPathExpressionException {
        return this.xsd.find(xpath, returnType);
    }

    public String getNamespaceURI(String prefix) {
        return this.xsd.getNamespaceURI(prefix);
    }

    public String getPrefix(String ns) {
        return this.xsd.getPrefix(ns);
    }
}
