package ne.wsdlparser.lib;

import com.sun.istack.Nullable;
import ne.wsdlparser.lib.utility.Utils;
import ne.wsdlparser.lib.constant.WSDLProperty;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;

public class WSDLOperation {

    private String name;
    private WSDLMessage request;
    private WSDLMessage response;
    private WSDLMessage[] faults;
    private WSDLManagerRetrieval manager;
    private Node node;
    private WSDLPortType portType;
    private WSDLProperty style;
    private String soapAction;
    private boolean requestLoaded;
    private boolean responseLoaded;
    private boolean faultLoaded;

    public WSDLOperation(WSDLManagerRetrieval manager, WSDLPortType portType, Node node)
            throws WSDLException {
        this.manager = manager;
        this.node = node;
        this.portType = portType;
        this.name = Utils.getAttrValueFromNode(node, "name");
        this.loadOperationDetails();
    }
    /**
     * Load operation details; messages, style, soapAction, etc...
     * @throws WSDLException 
     */
    private void loadOperationDetails()
            throws WSDLException {
        try {
            this.request = new WSDLMessage(this.manager, this, null);
            this.response = new WSDLMessage(this.manager, this, null);
            Node operation = (Node) this.manager.getXPath()
                    .compile(String.format(Locale.getDefault(), "operation[@name='%s']", this.name))
                    .evaluate(this.portType.getPort().getBinding().getNode(), XPathConstants.NODE);
            
            Node soap = (Node) this.manager.getXPath().compile(String.format(Locale.getDefault(), "operation"))
                    .evaluate(operation, XPathConstants.NODE);
            
            this.setStyle(Utils.getAttrValueFromNode(soap, "style"));
            this.setSoapAction(Utils.getAttrValueFromNode(soap, "soapAction"));
            Node input = (Node) this.manager.getXPath().compile(String.format(Locale.getDefault(), "input/body"))
                    .evaluate(operation, XPathConstants.NODE);
            Node output = (Node) this.manager.getXPath().compile(String.format(Locale.getDefault(), "output/body"))
                    .evaluate(operation, XPathConstants.NODE);
            NodeList faults = (NodeList) this.manager.getXPath().compile(String.format(Locale.getDefault(), "fault/fault"))
                    .evaluate(operation, XPathConstants.NODESET);
            
            this.request.setEncodingStyle(Utils.getAttrValueFromNode(input, "use"));
            this.response.setEncodingStyle(Utils.getAttrValueFromNode(output, "use"));
            this.faults = new WSDLMessage[faults.getLength()];
            for (int i = 0; i < faults.getLength(); i++) {
                this.faults[i] = new WSDLFaultMessage(this.manager, this, null);
                this.faults[i].setEncodingStyle(Utils.getAttrValueFromNode(faults.item(i), "use"));
            }
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
            
        }
    }
    /**
     * Set operation style. RPC|Document
     * @param value 
     */
    private void setStyle(String value) {
        if (value == null) {
            this.style = this.getPortType().getPort().getBinding().getGlobalStyle();
        } else if (value.toLowerCase().trim().equals("document")) {
            this.style = WSDLProperty.DOCUMENT;
        } else if (value.toLowerCase().trim().equals("rpc")) {
            this.style = WSDLProperty.RPC;
        } else {
            this.style = this.getPortType().getPort().getBinding().getGlobalStyle();
        }
    }

    /**
     * @return WSDLProperty return the style
     */
    public WSDLProperty getStyle() {
        return this.style;
    }

    /**
     * Load message node from portType/operation/messageType node
     *
     * @param paramNode portType/operation/messageType node
     * @param message Message associated with this message
     * @throws WSDLException
     */
    private void loadMessageNode(Node messageTypeNode, WSDLMessage message) throws WSDLException {
        try {
            // get message name...
            String paramMsgName[] = Utils.splitPrefixes(Utils.getAttrValueFromNode(messageTypeNode, "message"));
            // get message node
            Node messageNode = (Node) this.manager.getXPath()
                    .compile(String.format(Locale.getDefault(), "/definitions/message[@name='%s']", paramMsgName[1]))
                    .evaluate(this.manager.getWSDLFile(), XPathConstants.NODE);
            if (messageNode == null) {
                return;
            }

            message.setNode(messageNode);
            message.setName(Utils.getAttrValueFromNode(messageTypeNode, "message"));
            message.loadParams();
        } catch (XPathExpressionException | WSDLException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
        }
        

    }

    /**
     * Load fault messages associated with this operation. If there is none, set
     * a default SoapFault message.
     *
     * @throws WSDLException
     */
    private void loadFaultMessages()
            throws WSDLException {
        try {
            NodeList faultNodeTypes = (NodeList) this.manager.getXPath().compile("fault").evaluate(this.node,
                    XPathConstants.NODESET);
            if (faultNodeTypes.getLength() == 0) {
                this.faults = new WSDLMessage[]{new WSDLFaultMessage(this.manager, this, null)};
                return;
            }
            for (int i = 0; i < faultNodeTypes.getLength(); i++) {
                this.loadMessageNode(faultNodeTypes.item(i), this.faults[i]);
            }
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
        }

    }

    /**
     * Load message type node from portType/operation
     *
     * @param messageType input/output.
     * @param message message associated with the messageType
     * @throws WSDLException
     */
    private void loadMessageTypeNode(String messageType, WSDLMessage message)
            throws WSDLException {
        try {
            // Loading param message either [input], [output]
            Node messageTypeNode = (Node) this.manager.getXPath().compile(String.format(Locale.getDefault(), "%s", messageType))
                    .evaluate(this.node, XPathConstants.NODE);
            this.loadMessageNode(messageTypeNode, message);
            return;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);

    }

    /**
     * @return the fault
     * @throws ne.wsdlparser.lib.exception.WSDLException
     */
    public WSDLMessage[] getFault() throws WSDLException {
        if (!faultLoaded) {
            loadFaultMessages();
        }
        return this.faults;
    }

    /**
     * Load the message if not already loaded and return it.
     * @param index
     * @return the fault
     * @throws ne.wsdlparser.lib.exception.WSDLException
     */
    @Nullable
    public WSDLMessage getFault(int index) throws WSDLException {
        if (!faultLoaded) {
            loadFaultMessages();
        }
        faultLoaded = true;
        if (this.faults.length > index) {
            return this.faults[index];
        }
        return null;
    }

    /**
     * Load the message if not already loaded and return it.
     * @return the response
     * @throws WSDLException
     */
    public WSDLMessage getResponse() throws WSDLException {
        if (!responseLoaded) {
            this.loadMessageTypeNode("output", response);
        }
        responseLoaded = true;
        return response;
    }

    /**
     * Load the message if not already loaded and return it.
     * @return the request message
     * @throws WSDLException
     */
    public WSDLMessage getRequest() throws WSDLException {
        if (!requestLoaded) {
            this.loadMessageTypeNode("input", request);
        }
        requestLoaded = true;
        return request;
    }
    /**
     * Operation name setter
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Operation name getter
     * @return operation name
     */
    public String getName() {
        return this.name;
    }
    /**
     * Port type setter.
     * @param portType
     */
    public void setPortType(WSDLPortType portType) {
        this.portType = portType;
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
     * @param node the node to set
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * @return WSDLPortType return the portType
     */
    public WSDLPortType getPortType() {
        return portType;
    }

    /**
     * @return String return the soapAction
     */
    public String getSoapAction() {
        return soapAction;
    }

    /**
     * @param soapAction the soapAction to set
     */
    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }
    /**
     * Retrieve message by its name or type
     * @param msgName or msgType
     * @return Message instance
     * @throws WSDLException 
     */
    public WSDLMessage getMessage(String msgName) throws WSDLException {
        if (msgName.toLowerCase().equals("request") || getRequest().getName().equals(msgName)) {
            return getRequest();
        } else if (msgName.toLowerCase().equals("response") || getResponse().getName().equals(msgName)) {
            return getResponse();
        } else if (msgName.toLowerCase().equals("fault") && this.faults.length == 1) {
            return getFault(0);
        } else if (msgName.toLowerCase().equals("fault") && this.faults.length > 1) {
            throw new WSDLException(WSDLExceptionCode.MULTIPLE_FAULTS_FOUND, "Multiple faults found! please request a fault by name.");
        } else {
            for (WSDLMessage msg : getFault()) {
                if (msg.getName() == null && msgName.equals("SOAPFault")) {
                    return msg;
                } else if (msg.getName() != null && msg.getName().equals(msgName)) {
                    return msg;
                }
            }
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION, "No message found with this name!");
        }
    }

}
