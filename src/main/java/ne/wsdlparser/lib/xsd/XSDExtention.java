package ne.wsdlparser.lib.xsd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.utility.Utils;
import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;

/**
 * An implementation for XSD Extension node
 *
 * @author nour
 */
public class XSDExtention extends XSDComplexElement {

    private String base;
    private XSDSimpleElementType simpleType;

    public XSDExtention(WSDLManagerRetrieval manager, Node node)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        super(manager, node);

    }

    /**
     * @param base the base to set
     */
    public void setBase(String base) {
        this.base = Utils.splitPrefixes(base)[1];
    }

    /**
     * Load base and check if it is simple, or complex. and then load the
     * children and add them to the extended element
     *
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws WSDLException
     */
    @Override
    protected void loadChildren()
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        //set base.. from node
        this.setBase(Utils.getAttrValueFromNode(node, "base"));
        try {
            //try parsing base element type as simple
            this.simpleType = XSDSimpleElementType.parse(this.base);
        } catch (WSDLException e) {
            // not simple so, we load the referred base and parse it.
            Node base = (Node) this.manager.getXSDManager()
                    .find(String.format(Locale.getDefault(), "/schema/*[@name='%s']", this.base), XPathConstants.NODE);
            XSDComplexElement baseElement = (XSDComplexElement) ((XSDComplexElement) XSDElement
                    .getInstance(this.manager, base)).getChildren().get(0);
            // load children if any...
            super.loadChildren();
            XSDComplexElement extensionChild;
            try {
                //get first element from the extension
                extensionChild = (XSDComplexElement) getChildren().get(0);
                // add chilren of this extension child to the base element
                extensionChild.getChildren().forEach((el) -> {
                    // set the tns and qualification of the extension schema
                    el.explicitlySetTargetNameSpace(this.getTargetTamespace());
                    el.setQualified(isQualified());
                    baseElement.addChild(el);
                });
            } catch (IndexOutOfBoundsException e2) {

            }
            // set the children to be the next base element
            this.children = new ArrayList<XSDElement>();
            this.children.add(baseElement);
        }

    }

    @Override
    protected Boolean isESQLPrintable() {
        return true;
    }

    public XSDSimpleElementType getSimpleType() {
        return this.simpleType;
    }
}
