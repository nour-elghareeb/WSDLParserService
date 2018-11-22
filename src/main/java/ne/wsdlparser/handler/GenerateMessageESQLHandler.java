/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ne.wsdlparser.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import ne.wsdlparser.lib.WSDLService;
import ne.wsdlparser.lib.WSDLMessage;
import ne.wsdlparser.lib.WSDLOperation;

import ne.wsdlparser.lib.constant.ESQLVerbosity;
import ne.wsdlparser.lib.esql.ESQLCommentLine;
import ne.wsdlparser.lib.esql.ESQLDeclareLine;
import ne.wsdlparser.lib.esql.ESQLLine;
import ne.wsdlparser.lib.esql.ESQLSetterLine;
import ne.wsdlparser.lib.esql.constant.ESQLSource;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.utility.Utils;
import wsdlparse.ne.GenerateMessageESQLRequest;
import wsdlparse.ne.GenerateMessageESQLResponse;
import wsdlparse.ne.WSDLParserFault;

/**
 *
 * @author nour
 */
public class GenerateMessageESQLHandler extends ServiceHandler<GenerateMessageESQLRequest, GenerateMessageESQLResponse> {

    public GenerateMessageESQLHandler() throws WSDLParserFault {
        super();
    }

    @Override
    public GenerateMessageESQLResponse handle(GenerateMessageESQLRequest request) throws WSDLParserFault {
        try {
            GenerateMessageESQLResponse response = new GenerateMessageESQLResponse();

            loadWSDL(request.getWSDLName());
            WSDLService service = manager.loadService(request.getServiceName());
            WSDLOperation operation = service.loadPort(request.getPortName())
                    .getType().loadOperation(request.getOperationName());
            WSDLMessage message = operation.getMessage(request.getMessageName());
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

            ArrayList<ESQLLine> nsLines = this.manager.getESQLManager().getESQLBlock().generateNSLines();
            nsLines.stream().map((line) -> (ESQLDeclareLine) line).forEachOrdered((dcLine) -> {
                GenerateMessageESQLResponse.NSLine nsLine = new GenerateMessageESQLResponse.NSLine();
                nsLine.setPrefix(dcLine.getParam());
                nsLine.setNamespace(dcLine.getDefaultValue());
                response.getNSLine().add(nsLine);
            });
            ArrayList<ESQLLine> lines = this.manager.getESQLManager().getESQLBlock().getElementsLines();
            HashSet<String> vars = new HashSet<>();
            lines.forEach((line) -> {
                GenerateMessageESQLResponse.ESQLLine esqlLine = new GenerateMessageESQLResponse.ESQLLine();
                if (line instanceof ESQLCommentLine) {
                    GenerateMessageESQLResponse.ESQLLine.ESQLComment commentLine = new GenerateMessageESQLResponse.ESQLLine.ESQLComment();
                    commentLine.setComment(((ESQLCommentLine) line).getValue().replace(">", "&#62;").replace("<", "&#60;"));
                    commentLine.setType(((ESQLCommentLine) line).getVerbosity().name());
                    esqlLine.setESQLComment(commentLine);

                } else {
                    GenerateMessageESQLResponse.ESQLLine.ESQLSetter setterLine = new GenerateMessageESQLResponse.ESQLLine.ESQLSetter();
                    setterLine.setESQLSource(request.getESQLSource());
                    setterLine.setFieldType(((ESQLSetterLine) line).getXsdType().getESQLDataType().getValue());
                    setterLine.setXPath(((ESQLSetterLine) line).getxPath());
                    setterLine.setValueHelp(((ESQLSetterLine) line).getXsdType().getType());
                    if (source.equals(ESQLSource.INPUT)) {
                        String var = Utils.splitPrefixes(((ESQLSetterLine) line).getxPath().substring(((ESQLSetterLine) line).getxPath().lastIndexOf(".") + 1))[1];
                        int i = 2;
                        while (vars.contains(var)) {
                            var = var+String.valueOf(i);
                            i++;
                        }
                        vars.add(var);
                        setterLine.setDefaultValue(var);

                    }else{
                        setterLine.setDefaultValue(((ESQLSetterLine) line).getDefaultValue());
                    }
                    esqlLine.setESQLSetter(setterLine);
                }
                response.getESQLLine().add(esqlLine);
            });

            return response;
        } catch (WSDLException ex) {
            Logger.getLogger(GenerateMessageESQLHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw handleFault(ex.getCode().ordinal(), ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(GenerateMessageESQLHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw handleFault(9999, "Unexpected error occur!");
        }
    }

}
