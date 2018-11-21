/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ne.wsdlparser.handler;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ne.wsdlparser.lib.WSDLOperation;
import ne.wsdlparser.lib.WSDLPort;
import ne.wsdlparser.lib.WSDLService;
import ne.wsdlparser.lib.exception.WSDLException;
import wsdlparse.ne.ListPortOperationsRequest;
import wsdlparse.ne.ListPortOperationsResponse;
import wsdlparse.ne.WSDLParserFault;

/**
 *
 * @author nour
 */
public class ListPortOperationsHandler extends ServiceHandler<ListPortOperationsRequest, ListPortOperationsResponse> {
    public ListPortOperationsHandler() throws WSDLParserFault{
        super();
    }
    @Override
    public ListPortOperationsResponse handle(ListPortOperationsRequest request) throws WSDLParserFault {
        ListPortOperationsResponse response = new ListPortOperationsResponse();
        List<String> operations = response.getOperation();
        loadWSDL(request.getWSDLName());
        try {
            WSDLService service = manager.loadService(request.getServiceName());
            WSDLPort port = service.loadPort(request.getPortName());
            
            for (WSDLOperation op : port.getType().loadOperations()) {
                operations.add(op.getName());
            }
        } catch (WSDLException ex) {
            Logger.getLogger(ListPortOperationsHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw handleFault(ex.getCode().ordinal(), ex.getMessage());
        }

        return response;
    }

}
