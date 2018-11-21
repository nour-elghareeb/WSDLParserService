/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ne.wsdlparser.handler;

import java.io.File;
import wsdlparse.ne.FilterAvailableWSDLsRequest;
import wsdlparse.ne.FilterAvailableWSDLsResponse;

import wsdlparse.ne.WSDLParserFault;

/**
 *
 * @author nour
 */
public class FilterAvailableWSDLsHandler extends ServiceHandler<FilterAvailableWSDLsRequest, FilterAvailableWSDLsResponse> {

    public FilterAvailableWSDLsHandler() throws WSDLParserFault {
        super();
    }
    
    @Override
    public FilterAvailableWSDLsResponse handle(FilterAvailableWSDLsRequest request) throws WSDLParserFault {
        FilterAvailableWSDLsResponse response = new FilterAvailableWSDLsResponse();
        for (File dir : WORKING_DIR.listFiles()) {
            if (!dir.isDirectory()) {
                continue;
            }
            if (dir.getName().toLowerCase().contains(request.getWSDLName().toLowerCase())) {
                response.getWSDL().add(dir.getName());
            }
        }
        return response;
    }    
    
}
