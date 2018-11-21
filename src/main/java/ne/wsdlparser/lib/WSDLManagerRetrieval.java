package ne.wsdlparser.lib;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.esql.ESQLManager;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.XSDManager;

public interface WSDLManagerRetrieval {
    Document getWSDLFile();

    XSDManager getXSDManager()throws ParserConfigurationException, XPathExpressionException, SAXException, IOException, FileNotFoundException, WSDLException ;

    XPath getXPath();

    String getTargetNameSpace();

    ESQLManager getESQLManager();

    String getNamespaceURI(String prefix);

    String getPrefix(String targetTamespace) throws WSDLException;

}