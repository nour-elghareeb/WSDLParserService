package ne.wsdlparser.lib.esql.constant;

public enum ESQLDataType {
    // Numeric
    DECIMAL("DECIMAL"), INTEGER("INTEGER"), FLOAT("FLOAT"),
    // Boolean
    BOOLEAN("BOOLEAN"),
    // date & time
    DATE("DATE"), TIME("TIME"), GMTTIME("GMTTIME"), TIMESTAMP("TIMESTAMP"), GMTTIMESTAMP("GMTTIMESTAMP"),
    INTERVAL("INTERVAL"),
    // reference
    REFERENCE("REFERENCE TO"),
    // rows & lists
    ROW("ROW"), LIST("LIST"),
    // Strings
    BIT("BIT"), BLOB("BLOB"), CHARACTER("CHARACTER"),
    // Namespace
    NAMESPACE("NAMESPACE"), ESQLComment("-- "), NULL("NULL");
    private String value;

    ESQLDataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}