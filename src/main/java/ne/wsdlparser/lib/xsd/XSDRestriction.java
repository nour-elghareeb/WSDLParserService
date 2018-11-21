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
import ne.wsdlparser.lib.utility.ConsoleStyle;
import ne.wsdlparser.lib.xsd.constant.XSDRestrictionParamType;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;
import ne.wsdlparser.lib.xsd.restriction.XSDRestrictionEnum;
import ne.wsdlparser.lib.xsd.restriction.XSDRestrictionFractionDigits;
import ne.wsdlparser.lib.xsd.restriction.XSDRestrictionLength;
import ne.wsdlparser.lib.xsd.restriction.XSDRestrictionMaxLength;
import ne.wsdlparser.lib.xsd.restriction.XSDRestrictionMinLength;
import ne.wsdlparser.lib.xsd.restriction.XSDRestrictionParam;
import ne.wsdlparser.lib.xsd.restriction.XSDRestrictionPattern;
import ne.wsdlparser.lib.xsd.restriction.XSDRestrictionRange;

public class XSDRestriction extends XSDComplexElement<XSDElement> {
    private boolean isSimpleElement = true;
    private ArrayList<XSDRestrictionParam> params;
    private String base;

    public XSDRestriction(WSDLManagerRetrieval manager, Node node)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        super(manager, node, XSDExtention.class);
    }

    @Override
    public String getNodeHelp() {
        return "The next field has restriction.";
    }

    // TODO: fetch base and load children...
    @Override
    protected void loadChildren()
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        this.setBase(Utils.getAttrValueFromNode(this.node, "base"));
        try {
            // check if simple content
            this.simpletype = XSDSimpleElementType.parse(this.base);
            this.isSimpleElement = true;
            Node child = Utils.getFirstXMLChild(this.node);
            this.params = new ArrayList<XSDRestrictionParam>();
            while (child != null) {
                this.addRestrictionParam(child);
                child = Utils.getNextXMLSibling(child);
            }

        } catch (WSDLException e) {
            Node base = (Node) this.manager.getXSDManager()
                    .find(String.format(Locale.getDefault(), "/schema/*[@name='%s']", this.base), XPathConstants.NODE);
            if (base == null) {
                if (this.base.toLowerCase().equals("array")) {
                    XSDElement any = new XSDAny(this.manager, null);
                    this.children.add(any);
                }
            }else{
                super.loadChildren();
            }
            ;
        }

    }

    private void addRestrictionParam(Node paramNode) {
        try {
            XSDRestrictionParamType type = XSDRestrictionParamType
                    .parse(Utils.splitPrefixes(paramNode.getNodeName())[1]);
            XSDRestrictionRange rangeParam;
            int val;
            switch (type) {
            case ENUM:
                XSDRestrictionEnum enumParam = (XSDRestrictionEnum) this.getParam(XSDRestrictionEnum.class);
                enumParam.addValue(Utils.getAttrValueFromNode(paramNode, "value"));
                break;
            case MAX_EXCLUSIVE:
                rangeParam = (XSDRestrictionRange) this.getParam(XSDRestrictionRange.class);
                rangeParam.setMaxValue(Utils.getAttrValueFromNode(paramNode, "value"));
                break;
            case MIN_EXCLUSIVE:
                rangeParam = (XSDRestrictionRange) this.getParam(XSDRestrictionRange.class);
                rangeParam.setMinValue(Utils.getAttrValueFromNode(paramNode, "value"));
                break;
            case MAX_INCLUSIVE:
                rangeParam = (XSDRestrictionRange) this.getParam(XSDRestrictionRange.class);
                val = Integer.parseInt(Utils.getAttrValueFromNode(paramNode, "value"));
                rangeParam.setMaxValue(String.valueOf(++val));
                break;
            case MIN_INCLUSIVE:
                rangeParam = (XSDRestrictionRange) this.getParam(XSDRestrictionRange.class);
                val = Integer.parseInt(Utils.getAttrValueFromNode(paramNode, "value"));
                rangeParam.setMinValue(String.valueOf(--val));
                break;
            case MIN_LENGTH:
                XSDRestrictionMinLength minLength = (XSDRestrictionMinLength) this
                        .getParam(XSDRestrictionMinLength.class);
                minLength.setValue(Utils.getAttrValueFromNode(paramNode, "value"));
                break;
            case MAX_LENGTH:
                XSDRestrictionMaxLength maxLength = (XSDRestrictionMaxLength) this
                        .getParam(XSDRestrictionMaxLength.class);
                maxLength.setValue(Utils.getAttrValueFromNode(paramNode, "value"));
                break;
            case LENGTH:
                XSDRestrictionLength length = (XSDRestrictionLength) this.getParam(XSDRestrictionLength.class);
                length.setValue(Utils.getAttrValueFromNode(paramNode, "value"));
                break;
            case PATTERN:
                XSDRestrictionPattern pattern = (XSDRestrictionPattern) this.getParam(XSDRestrictionPattern.class);
                pattern.setValue(Utils.getAttrValueFromNode(paramNode, "value"));
                break;
            case FRACTION_DIGITS:
                XSDRestrictionFractionDigits fractions = (XSDRestrictionFractionDigits) this
                        .getParam(XSDRestrictionFractionDigits.class);
                fractions.setValue(Utils.getAttrValueFromNode(paramNode, "value"));
                break;
            }
        } catch (WSDLException e) {
            e.printStackTrace();
        }

    }

    private XSDRestrictionParam getParam(Class<?> cls) {

        for (XSDRestrictionParam param : this.params) {
            if (param.getClass() == cls) {
                // ((XSDRestrictionEnum) param).addValue(Utils.getAttrValueFromNode(paramNode,
                // "value"));
                return param;
            }
        }
        try {
            XSDRestrictionParam param = (XSDRestrictionParam) cls.newInstance();
            this.params.add(param);
            return param;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void setBase(String base) {
        this.base = Utils.splitPrefixes(base)[1];
    }

    @Override
    protected Boolean isESQLPrintable() {
        return true;
    }

    private XSDSimpleElementType simpletype;

    public XSDSimpleElementType getSimpleType() {
        return this.simpletype;
    }

    public ArrayList<XSDRestrictionParam> getRestrictionParams() {
        return this.params;
    }

    @Override
    public void toESQL() throws WSDLException{
        if (getRestrictionParams() == null) {
            if (children == null)
                return;
            super.toESQL();
            return;
        };
        for (XSDRestrictionParam param : getRestrictionParams()) {
            this.manager.getESQLManager().addComment(ESQLVerbosity.VALUE_HELP,
                    "Restriction: " + this.simpletype + " -> " + param.getHelp());
        }
        return;
    }
}