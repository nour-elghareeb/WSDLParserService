/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ne.wsdlparser.handler;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ne.wsdlparser.lib.Service;
import ne.wsdlparser.lib.WSDLMessage;
import ne.wsdlparser.lib.WSDLOperation;

import ne.wsdlparser.lib.constant.ESQLVerbosity;
import ne.wsdlparser.lib.esql.constant.ESQLSource;
import ne.wsdlparser.lib.exception.WSDLException;
import wsdlparse.ne.GenerateMessageESQLRequest;
import wsdlparse.ne.GenerateMessageESQLResponse;
import wsdlparse.ne.WSDLParserFault;

/**
 *
 * @author nour
 */
public class GenerateMessageESQLHandler extends ServiceHandler<GenerateMessageESQLRequest, GenerateMessageESQLResponse> {
    public GenerateMessageESQLHandler() throws WSDLParserFault{
        super();
    }
    @Override
    public GenerateMessageESQLResponse handle(GenerateMessageESQLRequest request) throws WSDLParserFault {
        try {
            GenerateMessageESQLResponse response = new GenerateMessageESQLResponse();
            List<String> lines = response.getESQLLine();

            loadWSDL(request.getWSDLName());
            Service service = manager.loadService(request.getServiceName());       
            WSDLOperation operation = service.loadPort(request.getPortName())
                    .getType().loadOperation(request.getOperationName());
            WSDLMessage message = operation.getMessageByName(request.getMessageName());
            ESQLSource source;
            String s = request.getESQLSource().toUpperCase();
            try {
                source = ESQLSource.valueOf(s);
            } catch (IllegalArgumentException e) {
                throw handleFault(1004, "Invalid ESQLSource Value");
            }
            ESQLVerbosity[] verbosities = new ESQLVerbosity[request.getESQLVerboisty().size()];
            try {
                int i = 0;
                for (String verbosity : request.getESQLVerboisty()) {
                    verbosities[i] = ESQLVerbosity.valueOf(verbosity);
                    i++;
                }
                if (verbosities.length != 0) {
                    manager.getESQLManager().setVerbosity(verbosities);
                }

            } catch (IllegalArgumentException e) {
                throw handleFault(1004, "Invalid ESQLVerbosity Value");
            }
            message.generateESQL();
            lines.addAll(manager.getESQLManager().getESQLBlock().getLinesAsList(source, request.isUseReferenceAsVariables(), false));

            return response;
        } catch (WSDLException ex) {
            Logger.getLogger(GenerateMessageESQLHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw handleFault(ex.getCode().ordinal(), ex.getMessage());
        }catch (Exception ex){
            Logger.getLogger(GenerateMessageESQLHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw handleFault(9999, "Unexpected error occur!");
        }
    }

}
