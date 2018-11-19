/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ne.wsdlparser.handler;

import java.util.logging.Level;
import java.util.logging.Logger;
import ne.wsdlparser.lib.Port;
import ne.wsdlparser.lib.Service;
import ne.wsdlparser.lib.exception.WSDLException;
import wsdlparse.ne.ListServicePortsRequest;
import wsdlparse.ne.ListServicePortsResponse;

import wsdlparse.ne.WSDLParserFault;

/**
 *
 * @author nour
 */
public class ListServicePortsHandler extends ServiceHandler<ListServicePortsRequest, ListServicePortsResponse> {
    public ListServicePortsHandler() throws WSDLParserFault{
        super();
    }
    @Override
    public ListServicePortsResponse handle(ListServicePortsRequest request) throws WSDLParserFault {
        try {
            ListServicePortsResponse response = new ListServicePortsResponse();
            loadWSDL(request.getWSDLName());
            Service service = manager.loadService(request.getServiceName());
            
            for (Port port : service.loadPorts()) {
                response.getPort().add(port.getName());
            }
            
            return response;
        } catch (WSDLException ex) {
            Logger.getLogger(ListServicePortsHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw handleFault(ex.getCode().ordinal(), ex.getMessage());
        }
    }

}
