/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ne.wsdlparser.handler;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import ne.wsdlparser.lib.Service;
import ne.wsdlparser.lib.exception.WSDLException;
import wsdlparse.ne.ListServicesRequest;
import wsdlparse.ne.ListServicesResponse;

import wsdlparse.ne.WSDLParserFault;

/**
 *
 * @author nour
 */
public class ListServicesHandler extends ServiceHandler<ListServicesRequest, ListServicesResponse> {

    public ListServicesHandler() throws WSDLParserFault {
        super();
    }
    
    private final static File EXTRACTING_DIR = new File(TEMP_DIR, "extracted");

    @Override
    public ListServicesResponse handle(ListServicesRequest request) throws WSDLParserFault {
        try {
            ListServicesResponse response = new ListServicesResponse();
            loadWSDL(request.getWSDLName());
            this.manager.loadServices();
            for (Service service : this.manager.getServices()){
                response.getService().add(service.getName());
            }
            return response;
        } catch (WSDLException ex) {
            Logger.getLogger(ListServicesHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw handleFault(ex.getCode().ordinal(), ex.getMessage());
        }
    }

}
