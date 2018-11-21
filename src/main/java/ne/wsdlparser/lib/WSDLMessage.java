package ne.wsdlparser.lib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;
import ne.wsdlparser.lib.xsd.XSDElement;

public class WSDLMessage {
    protected String name;
    private boolean isExternal;
    protected WSDLManagerRetrieval manager;
    protected String prefix;
    public ArrayList<XSDElement> parts = new ArrayList<XSDElement>();
    protected Node node;
    protected WSDLOperation operation;
    protected WSDLProperty encodingStyle;

    public WSDLMessage(WSDLManagerRetrieval manager, WSDLOperation operation, Node node)
            throws XPathExpressionException, WSDLException, SAXException, IOException, ParserConfigurationException {
        this.manager = manager;
        this.node = node;
        this.operation = operation;
        if (node != null) {
            // this.setName(Utils.getAttrValueFromNode(node, "name"));
            this.loadParams();
        }
    }

    public WSDLMessage(WSDLManagerRetrieval manager2, WSDLOperation operation2) {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     * @throws XPathExpressionException
     */
    public void setName(String name) throws XPathExpressionException {
        if (name == null)
            return;
        String[] temp = Utils.splitPrefixes(name);
        this.prefix = temp[0];
        this.name = temp[1];

        // if (this.isExternal) {
        // this.loadExternalPart();
        // } else {
        // this.loadInternalPart();
        // }
    }

    public String getNameWithPrefix() {
        StringBuilder builder = new StringBuilder();
        if (this.prefix != null) {
            builder.append(this.prefix);
            builder.append(":");
        }
        builder.append(this.name);
        return builder.toString();
    }

    public void generateESQL() throws WSDLException{
         this.manager.getESQLManager().clearAll();
        this.manager.getESQLManager().levelUp(this.prefix, this.name, this.parts.size() != 0);
        for (XSDElement element : this.parts) {
            element.toESQL();
        }
        this.manager.getESQLManager().levelDown(this.name, this.prefix, this.parts.size() != 0);
    }

    private void loadDocumentParams()
            throws XPathExpressionException, WSDLException, SAXException, IOException, ParserConfigurationException {
        // Check if literal or wrappd..
        NodeList parts = (NodeList) this.manager.getXPath().compile("part").evaluate(this.node, XPathConstants.NODESET);
        if (parts.getLength() == 1)
            this.loadDocumentWrappedPart(parts.item(0));
        else if (parts.getLength() > 1) {
            this.loadDocumentParts(parts);
        } else
            throw new WSDLException(WSDLExceptionCode.EMPTY_MESSAGE_PARAMS);
    }

    private void loadDocumentWrappedPart(Node wrappedPart)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        setName(Utils.getAttrValueFromNode(wrappedPart, "element"));
        // Node element = (Node) this.manager.getXPath().compile()
        Node element = (Node) this.manager.getXSDManager().find(
                String.format(Locale.getDefault(), "/schema/element[@name='%s']", this.name), XPathConstants.NODE);

        this.loadElement(element);
    }

    private void loadDocumentParts(NodeList parts) {

    }

    private void loadRPCParams()
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
                NodeList parts = (NodeList) this.manager.getXPath().compile("part").evaluate(this.node, XPathConstants.NODESET);
                for (int i = 0; i < parts.getLength(); i++){
                    loadElement(parts.item(i));
                }
    }

    public void loadParams()
            throws WSDLException, XPathExpressionException, SAXException, IOException, ParserConfigurationException {

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

        // NodeList msgParamsNodes = (NodeList)
        // this.manager.getXPath().compile(String.format(Locale.getDefault(), "part"))
        // .evaluate(this.node, XPathConstants.NODESET);
        // // Loop over parts in curent message
        // for (int i = 0; i < msgParamsNodes.getLength(); i++) {
        // // get part node
        // Node partNode = msgParamsNodes.item(i);
        // // checking if the part DOES refer to another element by checking attr (type)
        // // type exists, no refer here, load part directly
        // if (Utils.getAttrValueFromNode(partNode, "type") != null) {
        // loadPartWithType(partNode);
        // }
        // // Okay, it refers to another element, let's search the inline schema
        // else {
        // loadPartWithElement(partNode);
        // }

        // }
    }

    private void loadExternalPart() {

    }

    private void loadElement(Node element)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        this.parts.add(XSDElement.getInstance(this.manager, element));

    }
    public void setIsExternal(boolean isExternal) {
        this.isExternal = isExternal;
    }

    /**
     * @return boolean return the isExternal
     */
    public boolean isIsExternal() {
        return isExternal;
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

    /**
     * @param node the node to set
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * @return WSDLOperation return the operation
     */
    public WSDLOperation getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(WSDLOperation operation) {
        this.operation = operation;
    }

    public void setEncodingStyle(String value) {
        if (value == null)
            return;
        if (value.toLowerCase().trim().equals("encoded"))
            this.encodingStyle = WSDLProperty.ENCODED;
        else if (value.toLowerCase().trim().equals("literal"))
            this.encodingStyle = WSDLProperty.LITERAL;
    }

}
