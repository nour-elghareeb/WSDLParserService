package ne.wsdlparser.lib;

import ne.wsdlparser.lib.utility.Utils;
import ne.wsdlparser.lib.constant.WSDLProperty;
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
import ne.wsdlparser.lib.xsd.XSDElement;

/**
 * A WSDL Message implementation
 *
 * @author nour
 */
public class WSDLMessage {

    protected String name;
    private boolean isExternal;
    protected final WSDLManagerRetrieval manager;
    protected String prefix;
    public final ArrayList<XSDElement> parts;
    protected Node node;
    protected final WSDLOperation operation;
    protected WSDLProperty encodingStyle;

    /**
     * Constructor for WSDL Message
     *
     * @param manager WSDLManager instance injection
     * @param operation operation associated with the message
     * @param node XML node associated with the message.
     * @throws WSDLException
     */
    public WSDLMessage(WSDLManagerRetrieval manager, WSDLOperation operation, Node node)
            throws WSDLException {
        this.parts = new ArrayList<>();
        this.manager = manager;
        this.node = node;
        this.operation = operation;
        if (node != null) {
            this.loadParams();
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets both message name and prefix.
     * @param name the name to set.
     */
    public void setName(String name) {
        if (name == null) {
            return;
        }
        String[] temp = Utils.splitPrefixes(name);
        this.prefix = temp[0];
        this.name = temp[1];
    }
    /**
     * Generate ESQLLines for this message.
     * @throws WSDLException 
     */
    public void generateESQL() throws WSDLException {
        this.manager.getESQLManager().clearAll();
        this.manager.getESQLManager().levelUp(this.prefix, this.name, !this.parts.isEmpty());
        for (XSDElement element : this.parts) {
            element.toESQL();
        }
        this.manager.getESQLManager().levelDown(this.prefix, this.name, !this.parts.isEmpty());
    }
    /**
     * Load message params for Document operation style.
     * @throws WSDLException 
     */
    private void loadDocumentParams() throws WSDLException {
        try {
            // Check if literal or wrappd..
            NodeList partNodes = (NodeList) this.manager.getXPath().compile("part").evaluate(this.node, XPathConstants.NODESET);
            if (partNodes.getLength() == 1) {
                this.loadDocumentWrappedPart(partNodes.item(0));
            } else if (partNodes.getLength() > 1) {
                this.loadDocumentParts(partNodes);
            } else {
                throw new WSDLException(WSDLExceptionCode.EMPTY_MESSAGE_PARAMS);
            }
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLMessage.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_NOT_VALID, "Could not load message parts.");
        }
    }
    /**
     * Load message params for Document-Wrapped operation style 
     * @param wrappedPart XML node for the wrapped param
     * @throws WSDLException 
     */
    private void loadDocumentWrappedPart(Node wrappedPart) throws WSDLException {
        setName(Utils.getAttrValueFromNode(wrappedPart, "element"));
        Node element;
        try {
            element = (Node) this.manager.getXSDManager().find(
                    String.format(Locale.getDefault(), "/schema/element[@name='%s']", this.name), XPathConstants.NODE);
            this.loadElement(element);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLMessage.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_NOT_VALID, "Could not load message parts.");
        }

    }
    /**
     * Load non-wrapped document style operations
     * @param parts 
     */
    private void loadDocumentParts(NodeList parts) {
        throw new UnsupportedOperationException("Unsupported style!");
    }
    /**
     * Load RPC-Style operation parts.
     * @throws WSDLException 
     */
    private void loadRPCParams()
            throws WSDLException {
        try {
            NodeList partNodes = (NodeList) this.manager.getXPath().compile("part").evaluate(this.node, XPathConstants.NODESET);
            for (int i = 0; i < partNodes.getLength(); i++) {
                loadElement(partNodes.item(i));
            }
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLMessage.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_NOT_VALID, "Could not load message parts.");
        }
    }
    /**
     * Load parts for operation based on operation style.
     * @throws WSDLException 
     */
    void loadParams() throws WSDLException {

        // Check if operation is RPC or Document and then calls the appropriate method
        switch (this.operation.getStyle()) {
            case DOCUMENT:
                this.loadDocumentParams();
                break;
            case RPC:
                this.loadRPCParams();
                break;
            default:
                throw new WSDLException(WSDLExceptionCode.INVALID_OPERATION_STYLE);
        }
    }
    
    /**
     * Load element and its children (if it has any).
     * @param element
     * @throws WSDLException 
     */
    private void loadElement(Node element) throws WSDLException {
        try {
            this.parts.add(XSDElement.getInstance(this.manager, element));
        } catch (XPathExpressionException | SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(WSDLMessage.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_NOT_VALID, "Could not load message parts.");
        }
        
    }

    /**
     * @return String return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setEncodingStyle(String value) {
        if (value == null) {
            return;
        }
        if (value.toLowerCase().trim().equals("encoded")) {
            this.encodingStyle = WSDLProperty.ENCODED;
        } else if (value.toLowerCase().trim().equals("literal")) {
            this.encodingStyle = WSDLProperty.LITERAL;
        }
    }
    /**
     * Node setter
     * @param messageNode XML node associated with this message.
     */
    void setNode(Node messageNode) {
        this.node = messageNode;
    }

    

}
