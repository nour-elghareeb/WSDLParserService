package ne.wsdlparser.lib.xsd;

import com.sun.istack.Nullable;
import java.io.IOException;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.utility.Utils;
import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.constant.ESQLVerbosity;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;

/**
 * Abstract base class for any XSD element/type
 *
 * @author nour
 */
public abstract class XSDElement {

    protected String nodeHelp;
    protected String valueHelp;
    protected String value;
    protected String prefix;
    protected String name;
    protected String fixedValue;
    protected int maxOccurs = -1;
    protected int minOccurs = -1;
    protected String defaultValue;
    protected boolean nillable;
    protected boolean qualified;
    private String explicitTNS;

    public void setExplicitTNS(String explicitTNS) {
        this.explicitTNS = explicitTNS;
    }

    public boolean isQualified() {
        try {
            this.qualified = (boolean) node.getUserData("qualified");
        } catch (Exception ex) {
            this.qualified = false;
        }
        return this.qualified;
    }

    public void setQualified(boolean qualified) {
        if (this.node != null) {
            this.node.setUserData("qualified", qualified, null);
        }
        this.qualified = qualified;
    }
    protected Node node;
    protected WSDLManagerRetrieval manager;

    @Nullable
    public String getValueHelp() {
        return valueHelp;
    }

    /**
     *
     * @param manager WSDLManager instance injection
     * @param node XML node associated with this element
     */
    public XSDElement(WSDLManagerRetrieval manager, Node node) {
        this.prefix = "";
        this.node = node;
        this.manager = manager;
        loadAttributes();
    }

    /**
     * XSD node help.. To be overridden if subclasses have use for it.
     *
     * @return
     */
    @Nullable
    public String getNodeHelp() {
        return null;
    }

    /**
     * Check if this element is an instance of XSDComplex element
     *
     * @param element element to check
     * @return true if complex.
     */
    public static boolean isComplex(XSDElement element) {
        Boolean val = element instanceof XSDComplexElement;
        return val;
    }

    /**
     * Set XSD node help
     *
     * @param nodeHelp
     */
    public void setNodeHelp(String nodeHelp) {
        this.nodeHelp = nodeHelp;
    }

    /**
     * Check if this node has parameters to print.
     *
     * @return
     */
    protected abstract Boolean isESQLPrintable();

    /**
     * @return this node namespace embedded inside UserData during element
     * parsing.
     */
    public String getTargetTamespace() {
        if (this.node == null) {
            if (isQualified()) {
                return this.explicitTNS == null ? this.manager.getTargetNameSpace() : null;
            }
            return null;
        }
        String ns = (String) this.node.getUserData("tns");
        if (ns == null) {
            if (isQualified()) {
                return this.manager.getTargetNameSpace();
            }
        }
        return ns;
    }

    /**
     *
     * @return this node namespace explicitly set to override normal namespace
     * (if the element is imported from another XSD with a different NS.
     */
    public String getExplicitlySetTargetTamespace() {
        if (this.node == null) {
            return null;
        }
        String ns = (String) this.node.getUserData("EX_tns");
        return ns;
    }

    /**
     * Parse node to get the appropriate XSD element implementation.
     *
     * @param manager WSDLManager instance injection
     * @param element XML node associated with this element
     * @return XSDElement subclass based on the node type.
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws WSDLException
     */
    @Nullable
    public static XSDElement getInstance(WSDLManagerRetrieval manager, Node element)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        XSDElement xsdElement;
        if (element == null) {
            return null;
        }
        boolean isQualified;
        try {
            isQualified = (boolean) element.getUserData("qualified");
        } catch (Exception ex) {
            isQualified = false;
        }
        // get nodeName. ie: XSD:complexType
        String nodeNameWithPrefix = element.getNodeName();
        // Split nodeName to get the bare name. ie: complexType
        String nodeName = Utils.splitPrefixes(element.getNodeName())[1];
        // Get element name. may be null
        String name = Utils.getAttrValueFromNode(element, "name");
        // get element type. may be null
        String type = Utils.getAttrValueFromNode(element, "type");
        // Check if node is element
        /**
         * if this is an attribute, just ignore it.
         */
        if (nodeName.equals("attribute")) {
            return null;
        }
        // Get target namespace embedded during XPath retrieved.
        String tns = (String) element.getUserData("tns");
        // If this node has a type attribute.. Usually means this is an <element>
        if (type != null) {
            // Try parsing this element type as a simple type. ie: string, int, boolean, etc...
            try {
                xsdElement = XSDElement.getInstanceForSimpleElement(manager, element, Utils.splitPrefixes(type)[1]);
                xsdElement.setName(name);
                return xsdElement;
            } catch (WSDLException e) {
                // This element type is probably refers to another complex type.
                if (e.getCode().equals(WSDLExceptionCode.XSD_NOT_SIMPLE_ELEMENT)) {
                    element = (Node) manager.getXSDManager()
                            .find(String.format(Locale.getDefault(), "/schema/*[name() != '%s' and @name = '%s']",
                                    nodeNameWithPrefix, Utils.splitPrefixes(type)[1]), XPathConstants.NODE);
                    xsdElement = XSDElement.getInstance(manager, element);
                    tns = (String) element.getUserData("tns");

                    if (xsdElement == null) {
                        return null;
                    }

                    xsdElement.setName(name);
                    return xsdElement;
                }
                throw e;
            }

        }
        // This node does not have a type attribute.
        try {
            // Try parsing this xml node name as simple element
            xsdElement = XSDElement.getInstanceForSimpleElement(manager, element, nodeName);
            xsdElement.setName(name);
            return xsdElement;
        } catch (WSDLException e) {
            // try this node as a complex type.
            try {
                xsdElement = XSDElement.getInstanceForComplexElement(manager, element);
                xsdElement.setName(name);
                return xsdElement;
            } catch (WSDLException e2) {

                // elemen is in fact a <element> with no type. Probably has a child type..                 
                if (e2.getCode().equals(WSDLExceptionCode.XSD_NODE_IS_ELEMENT)) {
                    // get element first valid child and parse it.
                    element = Utils.getFirstXMLChild(element);
                    element.setUserData("tns", tns, null);
                    element.setUserData("qualified", isQualified, null);
                    // Parse it recursively.
                    xsdElement = XSDElement.getInstance(manager, element);
                    if (xsdElement == null) {
                        return null;
                    }
                    xsdElement.setName(name);
                    return xsdElement;
                }
                throw e;
            }
        }

    }

    /**
     * Parse node for a Complex Type element
     *
     * @param manager WSDLManager instance injection
     * @param node XML node to parse
     * @return XSDComplexElement subclass or throws an exception.
     * @throws WSDLException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private static XSDComplexElement getInstanceForComplexElement(WSDLManagerRetrieval manager, Node node)
            throws WSDLException, XPathExpressionException, SAXException, IOException, ParserConfigurationException {
        String nodeName = Utils.splitPrefixes(node.getNodeName())[1];

        if (nodeName.equals("element")) {
            throw new WSDLException(WSDLExceptionCode.XSD_NODE_IS_ELEMENT);
        } else if (nodeName.equals("all")) {
            return new XSDAll(manager, node);
        } else if (nodeName.equals("annotation")) {
            return new XSDAnnotation(manager, node);
        } else if (nodeName.equals("choice")) {
            return new XSDChoice(manager, node);
        } else if (nodeName.equals("complexContent")) {
            return new XSDComplexContent(manager, node);
        } else if (nodeName.equals("complexType")) {
            return new XSDComplexType(manager, node);
        } else if (nodeName.equals("simpleType")) {
            return new XSDSimpleType(manager, node);
        } else if (nodeName.equals("simpleContent")) {
            return new XSDSimpleContent(manager, node);
        } else if (nodeName.equals("extension")) {
            return new XSDExtention(manager, node);
        } else if (nodeName.equals("group")) {
            return new XSDGroup(manager, node);
        } else if (nodeName.equals("restriction")) {
            return new XSDRestriction(manager, node);
        } else if (nodeName.equals("sequence")) {
            return new XSDSequence(manager, node);
        } else if (nodeName.equals("union")) {
            return new XSDUnion(manager, node);
        } else {
            throw new WSDLException(WSDLExceptionCode.XSD_NOT_COMPLEX_TYPE);
        }
    }

    /**
     * Parse node as XSDSimpleElement type
     *
     * @param manager WSDLManager instance injection
     * @param node XML node to parse
     * @return XSDSimpleElement subclass or throws an exception.
     * @param type XSD type attribute
     * @return
     * @throws WSDLException
     */
    private static XSDSimpleElement getInstanceForSimpleElement(WSDLManagerRetrieval manager, Node node, String type)
            throws WSDLException {
        XSDSimpleElement element;

        XSDSimpleElementType simpleType = XSDSimpleElementType.parse(type);
        switch (simpleType) {
            case LIST:
                element = new XSDList(manager, node);
                break;
            case ANY:
                element = new XSDAny(manager, node);
                break;
            default:
                element = new XSDSimpleElement(manager, node, simpleType);
                break;
        }
        return element;
    }

    /**
     * Load attributes for this node.
     */
    private void loadAttributes() {
        this.setNillable(Utils.getAttrValueFromNode(this.node, "nillable"));
        this.setName(Utils.getAttrValueFromNode(this.node, "name"));
        this.setMaxOccurs(Utils.getAttrValueFromNode(this.node, "maxOccurs"));
        this.setMinOccurs(Utils.getAttrValueFromNode(this.node, "minOccurs"));
        this.setDefaultValue(Utils.getAttrValueFromNode(this.node, "default"));
        this.setFixedValue(Utils.getAttrValueFromNode(this.node, "fixed"));
    }

    /**
     * Set fixed value for this element if there is one
     *
     * @param fixedValue
     */
    protected abstract void setFixedValue(String fixedValue);

    /**
     * return if the element is Nill-able..
     */
    public boolean isNillable() {
        return this.nillable;
    }

    /**
     * @param value nillable
     */
    public void setNillable(String value) {
        if (value == null) {
            return;
        }
        this.nillable = Boolean.parseBoolean(value);
    }

    /**
     * return maximum occurrance..
     */
    public int getMaxOccurs() {
        return this.maxOccurs;
    }

    /**
     * @param value the maximum occur value to set
     */
    public void setMaxOccurs(String value) {
        if (value == null) {
            return;
        }
        if (value.equals("unbounded")) {
            this.maxOccurs = -1;
            return;
        }
        this.maxOccurs = Integer.parseInt(value);
    }

    /**
     * return minimum occurrence..
     * @return this node min occurs or -1;
     */
    public int getMinOccurs() {
        return this.minOccurs;
    }

    /**
     * @param value the miniumum occur value to set
     */
    public void setMinOccurs(String value) {
        if (value == null) {
            return;
        }

        this.minOccurs = Integer.parseInt(value);
    }

    /**
     * @param value the name to set
     */
    public void setDefaultValue(String value) {
        // this.defaultValue = value;
    }

    /**
     * @return the default value
     */
    public String getDefaultValue() {
        return this.value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        if (name == null && this.name != null) {
            return;
        }
        String[] tmp = Utils.splitPrefixes(name);
        this.prefix = tmp[0];
        this.name = tmp[1];
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

    /**
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Generate ESQL lines for this element and its children.
     *
     * @throws WSDLException
     */
    public void toESQL() throws WSDLException {
        this.addHelpComments();
    }

    /**
     * Add helpComments
     */
    protected void addHelpComments() {
        if (this instanceof XSDAnnotation) {
            this.manager.getESQLManager().addComment(ESQLVerbosity.DOCUMENTATION, this.getNodeHelp());
        } else {
            this.manager.getESQLManager().addComment(ESQLVerbosity.NODE_HELP, this.getNodeHelp());
        }

        if (this.minOccurs == 0) {
            this.manager.getESQLManager().addComment(ESQLVerbosity.MULTIPLICITY, "Optional");
        } else if (this.minOccurs == 1 && this.minOccurs == this.maxOccurs) {
            this.manager.getESQLManager().addComment(ESQLVerbosity.MULTIPLICITY, String.format(Locale.getDefault(), "Required"));
        } else if (this.minOccurs > 1 && this.minOccurs == this.maxOccurs) {
            this.manager.getESQLManager().addComment(ESQLVerbosity.MULTIPLICITY, String.format(Locale.getDefault(), "Must appear exactly %s times", this.minOccurs));
        } else if (this.minOccurs > 1) {
            this.manager.getESQLManager().addComment(ESQLVerbosity.MULTIPLICITY, String.format(Locale.getDefault(), "Must appear more than %s and less than %s", this.minOccurs, this.maxOccurs));
        }
        if (this.nillable) {
            this.manager.getESQLManager().addComment(ESQLVerbosity.VALUE_HELP, String.format(Locale.getDefault(), "This field is nillable."));
        }
        if (this.fixedValue != null) {
            this.manager.getESQLManager().addComment(ESQLVerbosity.VALUE_HELP, String.format(Locale.getDefault(), "This field has a fixed value: %s", this.fixedValue));
        }
    }

    /**
     * Set a different namespace different from this node's parent. (if it is
     * imported from another XSD file with different namespace)
     *
     * @param targetTamespace
     */
    public void explicitlySetTargetNameSpace(String targetTamespace) {
        this.node.setUserData("EX_tns", targetTamespace, null);
    }

    /**
     * Set this element name and prefix to null.
     */
    public void nullifyChildrenName() {
        this.name = null;
        this.prefix = null;
    }

    /**
     * Override this if the field has any printable parameters.
     *
     * @return
     */
    protected boolean hasPrintable() {
        return false;
    }

    /**
     * get the appropriate prefix for element based on if it come from another
     * schema and whether that schema use qualified form or not.
     *
     * @return prefix or null
     * @throws WSDLException
     */
    protected String getPrintablePrefix() throws WSDLException {
        String _prefix = this.prefix;
        if (this.prefix == null) {
            String ns = this.getExplicitlySetTargetTamespace();
            if (ns == null) {
                ns = this.getTargetTamespace();
            }
            if ((isQualified())) {
                _prefix = this.manager.getPrefix(ns);
            }
        }
        return _prefix;
    }
}
