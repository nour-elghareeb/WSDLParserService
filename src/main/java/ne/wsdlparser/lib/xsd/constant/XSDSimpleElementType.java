package ne.wsdlparser.lib.xsd.constant;

import ne.wsdlparser.lib.esql.constant.ESQLDataType;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;

public enum XSDSimpleElementType {
    BYTE("byte", "A signed 8-bit integer", ESQLDataType.INTEGER), DECIMAL("decimal", "A decimal value", ESQLDataType.DECIMAL),
    INT("int", "A signed 32-bit integer", ESQLDataType.INTEGER), INTEGER("integer", "An integer value", ESQLDataType.DECIMAL),
    LONG("long", "A signed 64-bit integer", ESQLDataType.INTEGER),
    NEGATIVE_INTEGER("negativeInteger", "An integer containing only negative values (..,-2,-1", ESQLDataType.DECIMAL),
    NON_NEGATIVE_INTEGER("nonNegativeInteger", "An integer containing only non-negative values (0,1,2,..)",
    ESQLDataType.DECIMAL),
    NON_PISITIVE_INTEGER("nonPositiveInteger", "An integer containing only non-positive values (..,-2,-1,0)",
    ESQLDataType.DECIMAL),
    POSITIVE_INTEGER("positiveInteger", "An integer containing only positive values (1,2,..)", ESQLDataType.DECIMAL),
    SHORT("short", "A signed 16-bit integer", ESQLDataType.INTEGER),
    UNSIGNED_LONG("unsignedLong", "An unsigned 64-bit integer", ESQLDataType.DECIMAL),
    UNSIGNED_INT("unsignedInt", "An unsigned 32-bit integer", ESQLDataType.INTEGER),
    UNSIGNED_SHORT("unsignedShort", "An unsigned 16-bit integer", ESQLDataType.INTEGER),
    UNSIGNED_BYTE("unsignedByte", "An unsigned 8-bit integer", ESQLDataType.INTEGER),
    DOUBLE("double", "a 64-bit floating-point", ESQLDataType.FLOAT), FLOAT("float", "32-bit floating-point", ESQLDataType.FLOAT),
    BASE64_BINARY("base64Binary", "A base64-encoded binary data", ESQLDataType.BLOB),
    TOKEN("token", "String with no more than one whitespace", ESQLDataType.CHARACTER), STRING("string", "String", ESQLDataType.CHARACTER),
    ANY_URI("anyURI", "URI string, ie: http://example.com", ESQLDataType.CHARACTER),
    DATE("date", "Date format example: YYYY-MM-DD", ESQLDataType.DATE),
    TIME("time", "The time data type is used to specify a time: hh:mm:ss", ESQLDataType.TIME),
    DURATION("duration", "The duration data type is used to specify a time interval", ESQLDataType.INTERVAL),
    DATE_TIME("dateTime", "The dateTime data type is used to specify a date and a time.: YYYY-MM-DDThh:mm:ss", ESQLDataType.TIMESTAMP),
    DATE_DAY("gDay", "Defines a part of a date - the day (DD)", ESQLDataType.DATE),
    DATE_MONTH("gMonth", "Defines a part of a date - the month (MM)", ESQLDataType.DATE),
    DATE_MONTH_DAY("gMonthDay", "Defines a part of a date -  the month and day (MM-DD)", ESQLDataType.DATE),
    DATE_YEAR("gYear", "Defines a part of a date - the year (YYYY)", ESQLDataType.DATE),
    DATE_YEAR_MONTH("gYearMonth", "Defines a part of a date - the year and month (YYYY-MM)", ESQLDataType.DATE),
    DAY_TIME_DURATION("dayTimeDuration", "represents a duration of time expressed as a number of days, hours, minutes, and seconds.", ESQLDataType.DATE),
    BOOLEAN("boolean", "Boolean value, true or false", ESQLDataType.BOOLEAN),
    HEX_BINRARY("hexBinary", "hexadecimal-encoded binary data", ESQLDataType.BLOB), APP_INFO("appInfo", "", ESQLDataType.ESQLComment),
    DOCUMENTATION("documentation", "", ESQLDataType.ESQLComment), LIST("list", "list seperated by a space", ESQLDataType.CHARACTER),
    UNION_CHILDREN("", "Union of multiple types.", ESQLDataType.CHARACTER), ANY("any", "You can put any elements at this level", ESQLDataType.NULL),
    ENTITIES("", "Union of multiple types.", ESQLDataType.CHARACTER);

    private String desc;
    private String type;
    private ESQLDataType esqlDataType;

    XSDSimpleElementType(String type, String desc, ESQLDataType esqlDataType) {
        this.type = type;
        this.desc = desc;
        this.esqlDataType = esqlDataType;
    }

    public static XSDSimpleElementType parse(String value) throws WSDLException {
        for (XSDSimpleElementType type : XSDSimpleElementType.values()) {
            if (value.equals(type.getType()))
                return type;
        }
        throw new WSDLException(WSDLExceptionCode.XSD_NOT_SIMPLE_ELEMENT);
    }

    /**
     * @return String return the desc
     */
    public String getDesc() {
        return desc;
    }

    public String getType() {
        return this.type;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Class<?> return the classType
     */
    public ESQLDataType getESQLDataType() {
        return this.esqlDataType;
    }


}