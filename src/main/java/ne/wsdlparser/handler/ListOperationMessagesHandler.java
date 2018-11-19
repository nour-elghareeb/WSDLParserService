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
import ne.wsdlparser.lib.Port;
import ne.wsdlparser.lib.Service;
import ne.wsdlparser.lib.WSDLMessage;
import ne.wsdlparser.lib.exception.WSDLException;
import wsdlparse.ne.ListOperationMessagesRequest;
import wsdlparse.ne.ListOperationMessagesResponse;
import wsdlparse.ne.ListOperationMessagesResponse.Message;


import wsdlparse.ne.WSDLParserFault;

/**
 *
 * @author nour
 */
public class ListOperationMessagesHandler extends ServiceHandler<ListOperationMessagesRequest, ListOperationMessagesResponse> {

    @Override
    public ListOperationMessagesResponse handle(ListOperationMessagesRequest request) throws WSDLParserFault {
        try {
            ListOperationMessagesResponse response = new ListOperationMessagesResponse();
            List<Message> messages = response.getMessage();
            loadWSDL(request.getWSDLName());
            Service service = manager.loadService(request.getServiceName());
            Port port = service.loadPort(request.getPortName());
            WSDLOperation operation = port.getType().loadOperation(request.getOperationName());
            Message requestMsg = new Message();
            requestMsg.setName(operation.getRequest().getName());
            requestMsg.setType("request");
            messages.add(requestMsg);
            Message responseMsg = new Message();
            responseMsg.setName(operation.getResponse().getName());
            responseMsg.setType("response");
            messages.add(responseMsg);
            for (WSDLMessage msg : operation.getFault()){
                Message faultMsg = new Message();
                faultMsg.setName(msg.getName() == null ? "SOAPFault" : msg.getName());
                faultMsg.setType("fault");
                messages.add(faultMsg);
            }
            return response;
        } catch (WSDLException ex) {
            Logger.getLogger(ListOperationMessagesHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw handleFault(ex.getCode().ordinal(), ex.getMessage());
        }
    }

}
