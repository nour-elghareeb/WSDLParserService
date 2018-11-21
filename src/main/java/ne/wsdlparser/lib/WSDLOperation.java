package ne.wsdlparser.lib;

import java.io.IOException;
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

public class WSDLOperation {

    private String name;
    private WSDLMessage request;
    private WSDLMessage response;
    private WSDLMessage[] faults;
    private WSDLManagerRetrieval manager;
    private Node node;
    private PortType portType;
    private WSDLProperty style;
    private String soapAction;
    private boolean requestLoaded;
    private boolean responseLoaded;
    private boolean faultLoaded;

    public WSDLOperation(WSDLManagerRetrieval manager, PortType portType, Node node)
            throws XPathExpressionException, WSDLException, SAXException, IOException, ParserConfigurationException {

        this.manager = manager;
        this.node = node;
        setPortType(portType);
        this.setName(Utils.getAttrValueFromNode(node, "name"));
        this.loadOperationDetails();
    }


    private void loadOperationDetails()
            throws XPathExpressionException, WSDLException, SAXException, IOException, ParserConfigurationException {
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
            this.faults[i] = new FaultMessage(this.manager, this, null);
            this.faults[i].setEncodingStyle(Utils.getAttrValueFromNode(faults.item(i), "use"));
        }
    }

    private void setStyle(String value) {
        if (value == null)
            this.style = this.getPortType().getPort().getBinding().getGlobalStyle();
        else if (value.toLowerCase().trim().equals("document"))
            this.style = WSDLProperty.DOCUMENT;
        else if (value.toLowerCase().trim().equals("rpc"))
            this.style = WSDLProperty.RPC;
        else
            this.style = this.getPortType().getPort().getBinding().getGlobalStyle();
    }

    /**
     * @return WSDLProperty return the style
     */
    public WSDLProperty getStyle() {
        return this.style;
    }

    private void loadParamNode(Node paramNode, WSDLMessage message) throws WSDLException
            {
        try {
            // get message name...
            String paramMsgName[] = Utils.splitPrefixes(Utils.getAttrValueFromNode(paramNode, "message"));
            // get message node
            Node messageNode = (Node) this.manager.getXPath()
                    .compile(String.format(Locale.getDefault(), "/definitions/message[@name='%s']", paramMsgName[1]))
                    .evaluate(this.manager.getWSDLFile(), XPathConstants.NODE);
            if (messageNode == null)
                return;
            
            message.setNode(messageNode);
            message.setName(Utils.getAttrValueFromNode(paramNode, "message"));
            message.loadParams();
            return;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WSDLException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);

    }

    private void loadFaultParams()
            throws WSDLException {
        try {
            NodeList faults = (NodeList) this.manager.getXPath().compile("fault").evaluate(this.node,
                    XPathConstants.NODESET);
            if (faults.getLength() == 0) {
                this.faults = new WSDLMessage[] { new FaultMessage(this.manager, this, null) };
                return;
            }
            for (int i = 0; i < faults.getLength(); i++) {
                this.loadParamNode(faults.item(i), this.faults[i]);
            }
            return;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (SAXException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);

    }

    private void loadParamNode(String paramName, WSDLMessage message)
            throws WSDLException {
        try {
            // Loading param message either [input], [output], [fault]
            Node paramNode = (Node) this.manager.getXPath().compile(String.format(Locale.getDefault(), "%s", paramName))
                    .evaluate(this.node, XPathConstants.NODE);
            if (paramNode == null) {
                return;
            }
            this.loadParamNode(paramNode, message);
            return;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);

    }

    /**
     * @return the fault
     */
    public WSDLMessage[] getFault() throws WSDLException {
        if (!faultLoaded) loadFaultParams();
        return this.faults;
    }

    /**
     * @return the fault
     */
    public WSDLMessage getFault(int index) throws WSDLException {
        if (!faultLoaded) loadFaultParams();
        faultLoaded = true;
        return this.faults[index];
    }

    /**
     * @return the response
     */
    public WSDLMessage getResponse() throws WSDLException {
        if (!responseLoaded) this.loadParamNode("output", response);
        responseLoaded = true;
        return response;
    }

    /**
     * @return the request
     */
    public WSDLMessage getRequest() throws WSDLException {
        if (!requestLoaded) this.loadParamNode("input", request);
        requestLoaded = true;
        return request;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPortType(PortType portType) throws XPathExpressionException {
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
     * @return PortType return the portType
     */
    public PortType getPortType() {
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

    public WSDLMessage getMessageByName(String msgName) throws WSDLException {
        if (msgName.toLowerCase().equals("request") || getRequest().getName().equals(msgName) ) return getRequest();
        else if (msgName.toLowerCase().equals("response") || getResponse().getName().equals(msgName)) return getResponse();
        else if (msgName.toLowerCase().equals("fault") && this.faults.length == 1) return getFault(0);
        else if (msgName.toLowerCase().equals("fault") && this.faults.length > 1) throw new WSDLException(WSDLExceptionCode.MULTIPLE_FAULTS_FOUND,  "Multiple faults found! please request a fault by name.");
        else{
            for (WSDLMessage msg :getFault() ){
                if (msg.getName() == null && msgName.equals("SOAPFault")) return msg;
                else if (msg.getName() != null && msg.getName().equals(msgName)) return msg;
            }            
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION, "No message found with this name!");
        }
    }

}