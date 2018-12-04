package ne.wsdlparser.lib.esql.constant;

public enum ESQLSource {
    INPUT("InputRoot"), OUTPUT("OutputRoot"), ENVIRONMENT("Environment"), LOCAL_ENVIRONMENT("LocalEnvironment"),
    LOCAL_OUTPUT_ENVIRONMENT("OutputLocalEnvironment");
    private String value;

    ESQLSource(String value) {
        this.value = value;
    }

    public String get() {
        return this.value;
    }
}