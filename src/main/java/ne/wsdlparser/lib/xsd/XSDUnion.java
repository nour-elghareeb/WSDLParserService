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
import ne.wsdlparser.lib.constant.ESQLVerbosity;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;

public class XSDUnion extends XSDComplexElement {
    private ArrayList<XSDSimpleElementType> simpleTypes;

    public XSDUnion(WSDLManagerRetrieval manager, Node node)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        super(manager, node);
    }

    // TODO: load children throw memberTypes
    @Override
    protected void loadChildren()
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        this.simpleTypes = new ArrayList<XSDSimpleElementType>();
        String memberType = Utils.getAttrValueFromNode(this.node, "memberTypes");
        super.loadChildren();
        if (memberType != null) {
            String[] memberTypes = memberType.split("\\s");
            for (String type : memberTypes) {
                try {
                    XSDSimpleElementType simpleType = XSDSimpleElementType.parse(Utils.splitPrefixes(type)[1]);
                    this.simpleTypes.add(simpleType);
                } catch (WSDLException e) {
                    Node node = (Node) this.manager.getXSDManager().find(
                            String.format(Locale.getDefault(), "/schema/*[@name='%s']", Utils.splitPrefixes(type)[1]),
                            XPathConstants.NODE);

                    XSDSimpleType element = (XSDSimpleType) XSDElement.getInstance(this.manager, node);
                    this.simpleTypes.add(element.getSimpleType());
                    element.nullifyChildrenName();
                    this.children.add(element);
                }

            }
        }

        String xx = "";
    }

    @Override
    protected boolean validateChild(Node child, XSDElement element)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        this.simpleTypes.add(((XSDSimpleType) element).getSimpleType());

        return super.validateChild(child, element);
    }

    @Override
    protected Boolean isESQLPrintable() {
        return true;
    }

    @Override
    public void toESQL() throws WSDLException{
        String[] types = new String[this.simpleTypes.size()];
        int i = 0;
        for (XSDSimpleElementType type : this.simpleTypes) {
            types[i] = type.getType().concat("; ").concat(type.getDesc());
            i++;
        }
        this.manager.getESQLManager().addComment(ESQLVerbosity.VALUE_HELP,
                String.format(Locale.getDefault(), "Union of the following types: %s", String.join(", ", types)));

        super.toESQL();
    }
}