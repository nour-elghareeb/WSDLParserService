package ne.wsdlparser.lib.xsd.constant;

import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;

public enum XSDRestrictionParamType {
    ENUM("enumeration"), FRACTION_DIGITS("fractionDigits"), LENGTH("length"), MAX_EXCLUSIVE("maxExclusive"),
    MAX_INCLUSIVE("maxInclusive"), MAX_LENGTH("maxLength"), MIN_EXCLUSIVE("minExclusive"),
    MIN_INCLUSIVE("minInclusive"), MIN_LENGTH("minLength"), PATTERN("pattern"), TOTAL_DIGITS("totalDigits"),
    WHITE_SPACE("whiteSpace");
    private String value;

    XSDRestrictionParamType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static XSDRestrictionParamType parse(String value) throws WSDLException {
        for (XSDRestrictionParamType param : XSDRestrictionParamType.values()) {
            if (param.getValue().equals(value))
                return param;
        }
        throw new WSDLException(WSDLExceptionCode.XSD_RESTRICT_PARAM_INVALID);
    }
    
}