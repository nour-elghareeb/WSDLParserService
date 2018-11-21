package ne.wsdlparser.lib.esql;

import java.util.ArrayList;
import java.util.HashSet;

import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.constant.ESQLVerbosity;
import ne.wsdlparser.lib.esql.constant.ESQLDataType;
import ne.wsdlparser.lib.esql.constant.ESQLSource;

public class ESQLBlock {
    private WSDLManagerRetrieval manager;
    private ArrayList<ESQLLine> elementsLines;

    public ArrayList<ESQLLine> getElementsLines() {
        return elementsLines;
    }

    public ArrayList<ESQLLine> getNsDeclarations() {
        generateNSLines();
        return nsDeclarations;
    }
    private ArrayList<ESQLLine> nsDeclarations;
    private HashSet<String> prefixes;
    private boolean lastWasEmpty = false;
    

    
    
    public ESQLBlock(WSDLManagerRetrieval manager) {
        this.manager = manager;
        this.elementsLines = new ArrayList<ESQLLine>();
        this.nsDeclarations = new ArrayList<ESQLLine>();
        this.prefixes = new HashSet<String>();
    }

    void addLine(ESQLLine line) {
        this.elementsLines.add(line);
        this.lastWasEmpty = false;
    }

    void addPrefix(String prefix) {
        if (prefix == null)
            return;
        this.prefixes.add(prefix);
    }

    private void generateNSLines() {
        this.nsDeclarations.clear();
        for (String prefix : prefixes) {
            this.nsDeclarations
                    .add(new ESQLDeclareLine(prefix, ESQLDataType.NAMESPACE, this.manager.getNamespaceURI(prefix)));
        }
    }

    public void printOutputSetters() {
        this.printESQL(ESQLSource.OUTPUT, true);
    }
    public ArrayList<String> getLinesAsList(ESQLSource source, boolean useRef, boolean useColors){
        ArrayList<String> lines = new ArrayList<>();
        this.generateNSLines();
        for (ESQLLine line : this.nsDeclarations) {
            line.setSource(source);
            line.useReferences(useRef);
            lines.add(line.generate(useColors));
//            lines.append(System.getProperty("line.separator"));
        }
        for (ESQLLine line : this.elementsLines) {
            line.setSource(source);
            line.useReferences(useRef);            
            lines.add(line.generate(useColors));
//            lines.append(System.getProperty("line.separator"));

        }
        return lines;
        
    }
    public String getLinesAsString(ESQLSource source, boolean useRef, boolean useColors){
        StringBuilder lines = new StringBuilder();
        ArrayList<String> lineList = this.getLinesAsList(source, useRef, useColors);
        for (String line : lineList){
            lines.append(line);            
            lines.append(System.getProperty("line.separator"));
        }
        return lines.toString();

    }
    private void printESQL(ESQLSource source, boolean useRef){
        this.generateNSLines();
        for (ESQLLine line : this.nsDeclarations) {
            line.setSource(source);
            line.useReferences(useRef);
            line.print();
        }
        for (ESQLLine line : this.elementsLines) {
            line.setSource(source);
            line.useReferences(useRef);            
            line.print();

        }
    }
    public void printInputVariables() {
        this.printESQL(ESQLSource.INPUT, false);
    }
    public void printInputReferences() {
        this.printESQL(ESQLSource.INPUT, true);
    }

    

    public void addEmptyLine(boolean allowMultiSuccessiveEmpty) {
        if (!this.lastWasEmpty || (allowMultiSuccessiveEmpty && this.lastWasEmpty))
            this.elementsLines.add(new ESQLCommentLine(ESQLVerbosity.EMPTY_LINES, null));
        this.lastWasEmpty = true;
    }

    public void clear() {
        this.elementsLines.clear();
        this.prefixes.clear();
        this.nsDeclarations.clear();
        this.lastWasEmpty = false;
    }

 
}