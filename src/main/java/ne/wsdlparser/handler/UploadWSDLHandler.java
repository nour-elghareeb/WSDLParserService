/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ne.wsdlparser.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import ne.utility.CompressionUtils;
import ne.utility.FileUtils;
import wsdlparse.ne.UploadWSDLRequest;
import wsdlparse.ne.UploadWSDLResponse;

import wsdlparse.ne.WSDLParserFault;

/**
 *
 * @author nour
 */
public class UploadWSDLHandler extends ServiceHandler<UploadWSDLRequest, UploadWSDLResponse> {
    public UploadWSDLHandler() throws WSDLParserFault{
        super();
    }
    private final static File EXTRACTING_DIR = new File(TEMP_DIR, "extracted");

    @Override
    public UploadWSDLResponse handle(UploadWSDLRequest request) throws WSDLParserFault {
        try {
            UploadWSDLResponse response = new UploadWSDLResponse();
            if (request.getMimeType()
                    .equals("application/zip")) {

                String filename = request.getFileName() + "." + request.getFileExtension();
                File compressedFile = new File(TEMP_DIR, "compressed.zip");
                OutputStream stream = new FileOutputStream(compressedFile);
                stream.write(request.getFileContents());
                stream.close();
                if (!EXTRACTING_DIR.exists()) {
                    EXTRACTING_DIR.mkdir();
                }
                compressedFile.deleteOnExit();
                if (!CompressionUtils.Unzip(compressedFile, EXTRACTING_DIR, true)) {
                    throw handleFault(1001, "Error Unzipping file.");
                }

                if (EXTRACTING_DIR.list().length == 0) {
                    throw handleFault(1005, "Error decoding file.");
                }

                File fileToBeStored = new File(WORKING_DIR, request.getFileName());
                if (fileToBeStored.exists() && !request.isOverwrite()) {
                    throw handleFault(1002, "A wsdl with same name already exists. Set overwrite option to true to replace.");
                } else if (fileToBeStored.exists() && request.isOverwrite()) {
                    FileUtils.deleteDirectoryStream(fileToBeStored.toPath());
                }
                if (!EXTRACTING_DIR.renameTo(fileToBeStored)) {
                    FileUtils.deleteDirectoryStream(EXTRACTING_DIR.toPath());
                    throw handleFault(1003, "Error moving file");
                }
                try{
                   loadWSDL(request.getFileName());
                }catch(WSDLParserFault ex){
                    FileUtils.deleteDirectoryStream(fileToBeStored.toPath());
                    throw handleFault(1005, "File is not in WSDL format!");
                }
                response.setWSDLName(request.getFileName());
                response.setStatus(true);
                return response;

            } else if (request.getMimeType().equals("application/xml")) {
                File wsdlDir = new File(WORKING_DIR, request.getFileName());
                if (wsdlDir.exists() && request.isOverwrite()) {
                    FileUtils.deleteDirectoryStream(wsdlDir.toPath());
                    
                } else if (wsdlDir.exists() && !request.isOverwrite()) {
                    throw handleFault(1002, "A wsdl with same name already exists. Set overwrite option to true to replace.");
                }
                wsdlDir.mkdir();
                File wsdlFile = new File(wsdlDir, request.getFileName() + "." + request.getFileExtension());
                try (OutputStream stream = new FileOutputStream(wsdlFile)) {
                    stream.write(request.getFileContents());
                }
                try{
                   loadWSDL(request.getFileName());
                }catch(WSDLParserFault ex){
                    FileUtils.deleteDirectoryStream(wsdlDir.toPath());
                    throw handleFault(1005, "File is not in WSDL format!");
                }
                response.setWSDLName(request.getFileName());
                response.setStatus(true);

            } else {
                throw handleFault(1006, "Unsupported file format!");
            }
            return response;
        } catch (IOException ex) {
            Logger.getLogger(UploadWSDLHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw handleFault(9999, "Error decoding uploaded file!");
        } finally {
            EXTRACTING_DIR.delete();
        }

    }

}
