package ne.wsdlparser.lib.exception;

public enum WSDLExceptionCode {
    INVALID_OPERATION_STYLE("Invalid operation style! Valid styles are document and rpc."),
    EMPTY_MESSAGE_PARAMS("Message does not seem to have any parts"),
    XSD_NOT_SIMPLE_ELEMENT("The element is not simple."), XSD_ELEMENT_NOT_FOUND("XSD element could not be found!"),
    XSD_NOT_COMPLEX_TYPE("The element is not complex"), XSD_NODE_IS_ELEMENT("the node is element not type"),
    XSD_RESTRICT_PARAM_INVALID("Invalid restrict param"), XSD_SCHEMA_FILE_NOT_FOUND("XSD Schema file cannot be loaded (not found)"), MULTIPLE_FAULTS_FOUND("Multiple faults messages found."), WSDL_PARSING_EXCEPTION("An error occurr while parsing wsdl."),
    XSD_SCHEMA_LOADING_ERROR("Error loading schema!"), WSDL_FILE_NOT_FOUND("WDSL file could not be found at the given path."),
    WSDL_NOT_VALID("Invalid WSDL file");

    private String message;

    WSDLExceptionCode(String message) {
        this.message = message;
    }

    /**
     * @return String return the message
     */
    String getMessage() {
        return message;
    }
}