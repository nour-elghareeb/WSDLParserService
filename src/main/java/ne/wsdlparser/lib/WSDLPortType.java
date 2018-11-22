package ne.wsdlparser.lib;

import ne.wsdlparser.lib.utility.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;

/**
 * WSDL PortType implementation
 *
 * @author nour
 */
public class WSDLPortType {

    private final String name;
    private final WSDLManagerRetrieval manager;
    private ArrayList<WSDLOperation> operations;
    private final WSDLPort port;

    /**
     * Constructor for PortType instance
     *
     * @param manager WSDLManager instance injection
     * @param port port associated with this portType
     * @param portTypeName portType name
     */
    public WSDLPortType(WSDLManagerRetrieval manager, WSDLPort port, String portTypeName) {
        this.name = Utils.splitPrefixes(portTypeName)[1];
        this.manager = manager;
        this.port = port;
    }

    /**
     * Load all operations associated with this port.
     *
     * @return operation list or throws an exception.
     * @throws WSDLException
     */
    public ArrayList<WSDLOperation> loadOperations() throws WSDLException {
        try {
            NodeList operationNodes = (NodeList) this.manager.getXPath()
                    .compile(String.format(Locale.getDefault(), "/definitions/portType[@name='%s']/operation", this.name))
                    .evaluate(this.manager.getWSDLFile(), XPathConstants.NODESET);
            this.operations = new ArrayList<>();
            for (int i = 0; i < operationNodes.getLength(); i++) {
                // getting opeartion
                WSDLOperation operation = new WSDLOperation(this.manager, this, operationNodes.item(i));
                this.operations.add(operation);
            }
            return this.operations;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLPortType.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
    }

    /**
     * @return WSDLPort instance associated with this portType.
     */
    public WSDLPort getPort() {
        return this.port;
    }

    /**
     * Load operation by name
     *
     * @param opName name of the operation to load
     * @return operation instance or throws an exception.
     * @throws WSDLException
     */
    public WSDLOperation loadOperation(String opName) throws WSDLException {
        try {
            Node opNode = (Node) this.manager.getXPath()
                    .compile(String.format(Locale.getDefault(), "/definitions/portType[@name='%s']/operation[@name='%s']", this.name, opName))
                    .evaluate(this.manager.getWSDLFile(), XPathConstants.NODE);
            if (opNode == null) {
                throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION, "No operation found with this name");
            }
            return new WSDLOperation(this.manager, this, opNode);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLPortType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WSDLException ex) {
            throw ex;
        }
        throw new WSDLException(WSDLExceptionCode.WSDL_NOT_VALID, "Could not load this port operations.");

    }
}
