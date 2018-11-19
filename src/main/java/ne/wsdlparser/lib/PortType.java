package ne.wsdlparser.lib;

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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;

public class PortType {

    private String name;
    private WSDLManagerRetrieval manager;
    private ArrayList<WSDLOperation> operations;
    private Port port;
    private String prefix;

    public PortType(WSDLManagerRetrieval manager, Port port, String portTypeName) throws XPathExpressionException {
        this.setName(portTypeName);
        this.manager = manager;
        this.setPort(port);
    }

    public ArrayList<WSDLOperation> loadOperations()
            throws WSDLException {
        try {
            NodeList operations = (NodeList) this.manager.getXPath()
                    .compile(String.format(Locale.getDefault(), "/definitions/portType[@name='%s']/operation", this.name))
                    .evaluate(this.manager.getWSDLFile(), XPathConstants.NODESET);
            this.operations = new ArrayList<WSDLOperation>();
            for (int i = 0; i < operations.getLength(); i++) {
                // getting opeartion
                WSDLOperation operation = new WSDLOperation(this.manager, this, operations.item(i));
                this.operations.add(operation);
            }
            return this.operations;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(PortType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(PortType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PortType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PortType.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
    }

    private void setName(String name) {
        String[] tmp = Utils.splitPrefixes(name);
        this.prefix = tmp[0];
        this.name = tmp[1];

    }

    /**
     * @return String return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return WSDLManagerRetrieval return the manager
     */
    public WSDLManagerRetrieval getManager() {
        return manager;
    }

    /**
     * @param manager the manager to set
     */
    public void setManager(WSDLManagerRetrieval manager) {
        this.manager = manager;
    }

    /**
     * @param operations the operations to set
     */
    public void setOperations(ArrayList<WSDLOperation> operations) {
        this.operations = operations;
    }

    /**
     * @param port the port to set
     */
    public void setPort(Port port) {
        this.port = port;
    }

    /**
     * @return Port return the port
     */
    public Port getPort() {
        return this.port;
    }

    public WSDLOperation loadOperation(String opName) throws WSDLException {
//       if (this.operations == null){
        try {
            Node opNode = (Node) this.manager.getXPath()
                    .compile(String.format(Locale.getDefault(), "/definitions/portType[@name='%s']/operation[@name='%s']", this.name, opName))
                    .evaluate(this.manager.getWSDLFile(), XPathConstants.NODE);
            if (opNode == null) throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION, "No operation found with this name");
            return new WSDLOperation(this.manager, this, opNode);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(PortType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WSDLException ex) {
            throw ex;
        } catch (SAXException ex) {
            Logger.getLogger(PortType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PortType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PortType.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }
}
