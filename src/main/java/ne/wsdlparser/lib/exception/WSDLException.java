package ne.wsdlparser.lib.exception;

public class WSDLException extends Exception {
    private static final long serialVersionUID = 1L;
    private WSDLExceptionCode code;

    public WSDLException(WSDLExceptionCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public WSDLException(WSDLExceptionCode code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * @return int return the code
     */
    public WSDLExceptionCode getCode() {
        return code;
    }
}