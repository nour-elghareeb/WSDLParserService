package ne.wsdlparser.lib.xsd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.utility.Utils;
import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.exception.WSDLException;

/**
 * An implementation for any XSD type that can/may have children.
 *
 * @author nour
 */
public abstract class XSDComplexElement extends XSDElement {

    protected ArrayList<XSDElement> children;
    protected boolean hasRestriction;
    protected XSDRestriction restriction;

    /**
     * Load its children automatically.
     *
     * @param manager WSDLManager instance injection.
     * @param node node associated with this element
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws WSDLException
     */
    public XSDComplexElement(WSDLManagerRetrieval manager, Node node)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        super(manager, node);
        this.hasRestriction = false;
        this.children = new ArrayList<>();
        this.loadChildren();
    }

    /**
     * Children getter.
     *
     * @return
     */
    public ArrayList<XSDElement> getChildren() {
        return this.children;
    }

    /**
     * Add children as a list
     *
     * @param elements
     */
    public void addChildren(Collection<XSDElement> elements) {
        this.children.addAll(elements);
    }

    /**
     * Add child to children list.
     *
     * @param element
     */
    public void addChild(XSDElement element) {
        this.children.add(element);
    }

    /**
     * Validate each child before inserting it into the children list.
     *
     * @param child current child node
     * @param element current child element
     * @return true to add the element to the children list, false otherwise.
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws WSDLException
     */
    protected boolean validateChild(Node child, XSDElement element)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {

        if (element instanceof XSDRestriction) {
            this.hasRestriction = true;
            this.restriction = (XSDRestriction) element;
        }
        return true;
    }

    /**
     * Load children recursively
     *
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws WSDLException
     */
    protected void loadChildren()
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        Node child = Utils.getFirstXMLChild(this.node);

        while (child != null) {
            child.setUserData("tns", this.node.getUserData("tns"), null);
            child.setUserData("qualified", this.node.getUserData("qualified"), null);
            XSDElement element = XSDElement.getInstance(this.manager, child);

            if (element != null && this.validateChild(child, element) && element.getMaxOccurs() != 0) {
                this.children.add(element);
            }
            child = Utils.getNextXMLSibling(child);
        }
    }

    /**
     * generate this element ESQL and its children.
     *
     * @throws WSDLException
     */
    @Override
    public void toESQL() throws WSDLException {
        if (this.maxOccurs != 0) {
            super.toESQL();
            this.manager.getESQLManager().levelUp(getPrintablePrefix(), this.name, this.hasPrintable());

            for (XSDElement element : this.children) {
                element.toESQL();
            }
            // this.manager.getESQLManager().addEmptyLine(false);
            
        }
        this.manager.getESQLManager().levelDown(getPrintablePrefix(), this.name, this.hasPrintable());
    }

    /**
     * Nullify this node name and prefix along with all its children
     */
    @Override
    public void nullifyChildrenName() {
        super.nullifyChildrenName();
        this.name = null;
        this.prefix = null;
        for (XSDElement element : this.children) {
            element.nullifyChildrenName();
        }
    }

    /**
     * Check if this element has any param to print
     *
     * @return true if this element or any of its children have any param to
     * print
     */
    @Override
    protected boolean hasPrintable() {
        for (XSDElement element : this.children) {
            if (element.hasPrintable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void setFixedValue(String fixedValue) {

    }
}
