/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ne.wsdlparser.handler;

import java.io.File;
import ne.wsdlparser.lib.Port;
import ne.wsdlparser.lib.Service;
import ne.wsdlparser.lib.WSDLManager;
import ne.wsdlparser.lib.exception.WSDLException;
import wsdlparse.ne.WSDLParserFault;
import wsdlparse.ne.WSDLParserFaultDetails;


/**
 *
 * @author nour
 */
public abstract class ServiceHandler<RQ, RS> {
    protected final static File WORKING_DIR = new File("/usr/share/wsdlparser/files");
    protected final static File TEMP_DIR = new File("/usr/share/wsdlparser/tmp");
    protected WSDLManager manager;
    
    public abstract RS handle(RQ request) throws WSDLParserFault;
    public WSDLParserFault handleFault(int code, String message){
        WSDLParserFault fault = new WSDLParserFault(message, new WSDLParserFaultDetails());
        fault.getFaultInfo().setErrorCode(code);
        fault.getFaultInfo().setErrorMessage(message);
        return fault;
    }
    
    protected void loadWSDL(String wsdlname) throws WSDLParserFault{
        File dir = new File(WORKING_DIR, wsdlname);
        if (!dir.exists()) throw handleFault(1001, "WSDL file could not be found!");
        File wsdlFile = null;
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".wsdl")) {
                wsdlFile = file;
                break;
            }
        }
        if (wsdlFile == null) {
            throw handleFault(1001, "Could not load wsdl file!");
        }
        try {
            manager = new WSDLManager(wsdlFile.getAbsolutePath());            
        } catch (WSDLException ex) {
            throw handleFault(9999, ex.getMessage());
        } catch (Exception ex) {
            throw handleFault(9999, ex.getMessage());
        }
    }
}
