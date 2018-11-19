/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ne.wsdlparser.handler;

import java.io.File;
import wsdlparse.ne.ListAvailableWSDLsRequest;
import wsdlparse.ne.ListAvailableWSDLsResponse;


import wsdlparse.ne.WSDLParserFault;

/**
 *
 * @author nour
 */
public class ListAvailableWSDLsHandler extends ServiceHandler<ListAvailableWSDLsRequest, ListAvailableWSDLsResponse> {
    public ListAvailableWSDLsHandler() throws WSDLParserFault{
        super();
    }
 
    @Override
    public ListAvailableWSDLsResponse handle(ListAvailableWSDLsRequest request) throws WSDLParserFault{
        ListAvailableWSDLsResponse response = new ListAvailableWSDLsResponse();
        for (File dir : WORKING_DIR.listFiles()){
            if (!dir.isDirectory()) continue;
            response.getWSDL().add(dir.getName());
        }
        return response;
    }    

}
