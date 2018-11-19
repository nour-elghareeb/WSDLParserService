/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ne.wsdlparser.handler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ne.utility.FileUtils;
import wsdlparse.ne.DeleteWSDLRequest;
import wsdlparse.ne.DeleteWSDLResponse;
import wsdlparse.ne.WSDLParserFault;

/**
 *
 * @author nour
 */
public class DeleteWSDLHandler extends ServiceHandler<DeleteWSDLRequest, DeleteWSDLResponse> {
    public DeleteWSDLHandler() throws WSDLParserFault{
        super();
    }
    @Override
    public DeleteWSDLResponse handle(DeleteWSDLRequest request) throws WSDLParserFault {
        try {
            DeleteWSDLResponse response = new DeleteWSDLResponse();
            File wsdl = new File(WORKING_DIR, request.getWSDLName());
            if (!wsdl.exists()){
                throw handleFault(1001, "WSDL file does not exist!");
            }
            FileUtils.deleteDirectoryStream(wsdl.toPath());
            return response;
        } catch (IOException ex) {
            Logger.getLogger(DeleteWSDLHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw handleFault(1001, "Sorry! file could not be deleted!");
        }
    }

}
