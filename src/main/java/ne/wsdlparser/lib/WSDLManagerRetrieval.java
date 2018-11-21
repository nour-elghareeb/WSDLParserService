package ne.wsdlparser.lib;

import com.sun.istack.Nullable;
import javax.validation.constraints.NotNull;

import javax.xml.xpath.XPath;

import org.w3c.dom.Document;

import ne.wsdlparser.lib.esql.ESQLManager;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.XSDManager;
/**
 * A retriever interface to pass from WSDLManager to its children;
 * @author nour
 */
public interface WSDLManagerRetrieval {
    /**
     * Retrieve WSDL file.
     * @return WSDL document
     */
    Document getWSDLFile();
    /**
     * Retrieve XSDManager instance
     * @return XSDManager or an Exception
     * @throws WSDLException if any error occur while loading schema
     */
    
    XSDManager getXSDManager() throws WSDLException;
    
    /**
     * Retrieve WSDL XPath instance
     * @return XPath instance
     */
    @NotNull
    XPath getXPath();
    
    /**
     * Retrieve target nameSpace from WSDL document
     * @return targetNamespace
     */
    String getTargetNameSpace();
    /**
     * @return esqlMnager instance
     */
    @NotNull
    ESQLManager getESQLManager();
    /**
     * Search for NameSpace that matches this prefix within WSDL and XSD schema
     * @param prefix to search with
     * @return a NameSpace or null
     */
    @Nullable
    String getNamespaceURI(String prefix);
    /**
     * Search for a prefix that matches this namespace within WSDL and XSD schema
     * @param targetTamespace namespace to search for
     * @return prefix or null
     * @throws WSDLException 
     */
    String getPrefix(String targetTamespace) throws WSDLException;

}