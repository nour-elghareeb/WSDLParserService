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

public abstract class XSDComplexElement extends XSDElement {

    protected ArrayList<XSDElement> children = new ArrayList<>();
    protected boolean hasRestriction = false;
    protected XSDRestriction restriction;

    public XSDComplexElement(WSDLManagerRetrieval manager, Node node, Class<?> classType)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        super(manager, node, classType);
        this.loadChildren();
    }

    public ArrayList<XSDElement> getChildren() {
        return this.children;
    }

    public void addChildren(Collection<XSDElement> elements) {
        this.children.addAll(elements);
    }

    public void addChild(XSDElement element) {
        this.children.add(element);
    }

    protected boolean validateChild(Node child, XSDElement element)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {

        if (element instanceof XSDRestriction) {
            this.hasRestriction = true;
            this.restriction = (XSDRestriction) element;
        }
        return true;
    }

    protected void loadChildren()
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        Node child = Utils.getFirstXMLChild(this.node);

        while (child != null) {
            child.setUserData("tns", this.node.getUserData("tns"), null);
            XSDElement element = XSDElement.getInstance(this.manager, child);

            if (element != null && this.validateChild(child, element) && element.getMaxOccurs() != 0)
                this.children.add(element);
            child = Utils.getNextXMLSibling(child);
        }
    }

    @Override
    public void toESQL() throws WSDLException{

        String prefix = this.prefix;
        if (this.prefix == null) {
            String ns = this.getExplicitlySetTargetTamespace();
            if (ns == null) {
                ns = this.getTargetTamespace();
            }
            if (!this.manager.getTargetNameSpace().equals(ns)) {
                prefix = this.manager.getPrefix(ns);
            }
        }

        if (this.maxOccurs != 0) {
            super.toESQL();
            this.manager.getESQLManager().levelUp(prefix, this.name, this.hasPrintable());

            for (XSDElement element : this.children) {
                element.toESQL();
            }
        }
        // this.manager.getESQLManager().addEmptyLine(false);
        this.manager.getESQLManager().levelDown(this.name, prefix, this.hasPrintable());

        // String temp = xPath;
        // if (name != null && !xPath.contains(this.name))
        // temp += "." + Utils.getParamWithPrefix(this.prefix, this.name);

        // ArrayList<ESQLLine> esql = new ArrayList<ESQLLine>();
        // for (XSDElement element : this.children) {
        // element.toESQL(manager, temp);
        // }
        // return "";
    }


    @Override
    public void nullifyChildrenName() {
        super.nullifyChildrenName();
        this.name = null;
        this.prefix = null;
        for (XSDElement element : this.children) {
            element.nullifyChildrenName();
        }
    }

    @Override
    protected boolean hasPrintable() {
        for (XSDElement element : this.children) {
            if (element.hasPrintable())
                return true;
        }
        return false;
    }
    @Override
    protected void setFixedValue(String fixedValue) {
        
    }
}