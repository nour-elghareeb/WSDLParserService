/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ne.wsdlparser.listener;

import javax.jws.WebService;
import ne.wsdlparser.handler.DeleteWSDLHandler;
import ne.wsdlparser.handler.FilterAvailableWSDLsHandler;
import ne.wsdlparser.handler.GenerateMessageESQLHandler;
import ne.wsdlparser.handler.ListAvailableWSDLsHandler;
import ne.wsdlparser.handler.ListOperationMessagesHandler;
import ne.wsdlparser.handler.ListPortOperationsHandler;
import ne.wsdlparser.handler.ListServicePortsHandler;
import ne.wsdlparser.handler.ListServicesHandler;
import ne.wsdlparser.handler.UploadWSDLHandler;
import wsdlparse.ne.WSDLParserFault;

/**
 *
 * @author nour
 */
@WebService(serviceName = "WSDLParser", portName = "WSDLParserSOAP", endpointInterface = "wsdlparse.ne.WSDLParser", targetNamespace = "ne.wsdlparse", wsdlLocation = "WEB-INF/wsdl/WSDLParser.wsdl")
public class WSDLParserListener extends com.sun.xml.ws.transport.http.servlet.WSServlet{

     public wsdlparse.ne.UploadWSDLResponse uploadWSDL(wsdlparse.ne.UploadWSDLRequest parameters) throws WSDLParserFault {
        return new UploadWSDLHandler().handle(parameters);
    }

    public wsdlparse.ne.ListServicesResponse listServices(wsdlparse.ne.ListServicesRequest parameters) throws WSDLParserFault {
        return new ListServicesHandler().handle(parameters);
    }

    public wsdlparse.ne.ListAvailableWSDLsResponse listAvailableWSDLs(wsdlparse.ne.ListAvailableWSDLsRequest parameters) throws WSDLParserFault {
        return new ListAvailableWSDLsHandler().handle(parameters);
    }
    public wsdlparse.ne.FilterAvailableWSDLsResponse filterAvailableWSDLs(wsdlparse.ne.FilterAvailableWSDLsRequest parameters) throws WSDLParserFault {
        return new FilterAvailableWSDLsHandler().handle(parameters);
    }

    public wsdlparse.ne.ListServicePortsResponse listServicePorts(wsdlparse.ne.ListServicePortsRequest parameters) throws WSDLParserFault {
        return new ListServicePortsHandler().handle(parameters);
    }

    public wsdlparse.ne.ListPortOperationsResponse listPortOperations(wsdlparse.ne.ListPortOperationsRequest parameters) throws WSDLParserFault {
        return new ListPortOperationsHandler().handle(parameters);
    }

    public wsdlparse.ne.ListOperationMessagesResponse listOperationMessages(wsdlparse.ne.ListOperationMessagesRequest parameters) throws WSDLParserFault {
        return new ListOperationMessagesHandler().handle(parameters);
    }

    public wsdlparse.ne.GenerateMessageESQLResponse generateMessageESQL(wsdlparse.ne.GenerateMessageESQLRequest parameters) throws WSDLParserFault {
        return new GenerateMessageESQLHandler().handle(parameters);
    }
    
    public wsdlparse.ne.DeleteWSDLResponse deleteWSDL(wsdlparse.ne.DeleteWSDLRequest parameters) throws WSDLParserFault {
        return new DeleteWSDLHandler().handle(parameters);
    }
    
}