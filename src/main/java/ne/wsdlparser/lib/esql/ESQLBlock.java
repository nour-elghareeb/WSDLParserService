package ne.wsdlparser.lib.esql;

import java.util.ArrayList;
import java.util.HashSet;

import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.constant.ESQLVerbosity;
import ne.wsdlparser.lib.esql.constant.ESQLDataType;
import ne.wsdlparser.lib.esql.constant.ESQLSource;

/**
 * Block of ESQL lines
 *
 * @author nour
 */
public class ESQLBlock {

    private final WSDLManagerRetrieval manager;
    private final ArrayList<ESQLLine> elementsLines;
    private final ArrayList<ESQLLine> nsDeclarations;
    private final HashSet<String> prefixes;
    private boolean lastWasEmpty = false;

    public ArrayList<ESQLLine> getElementsLines() {
        return elementsLines;
    }

    /**
     *
     * @param manager
     */
    public ESQLBlock(WSDLManagerRetrieval manager) {
        this.manager = manager;
        this.elementsLines = new ArrayList<>();
        this.nsDeclarations = new ArrayList<>();
        this.prefixes = new HashSet<>();
    }

    /**
     * Add a line to the blokc
     *
     * @param line
     */
    void addLine(ESQLLine line) {
        this.elementsLines.add(line);
        this.lastWasEmpty = false;
    }

    /**
     * add a prefix to the block with no duplicates.
     *
     * @param prefix
     */
    void addPrefix(String prefix) {
        if (prefix == null) {
            return;
        }
        this.prefixes.add(prefix);
    }

    /**
     *
     * Generate NS lines from the set of prefix used in this block..
     *
     * @return
     */
    public ArrayList<ESQLLine> generateNSLines() {
        this.nsDeclarations.clear();
        for (String prefix : prefixes) {
            this.nsDeclarations
                    .add(new ESQLDeclareLine(prefix, ESQLDataType.NAMESPACE, this.manager.getNamespaceURI(prefix)));
        }
        return this.nsDeclarations;
    }

    /**
     * Print line as output with colors
     */
    public void printOutputSetters() {
        this.printESQL(ESQLSource.OUTPUT, true);
    }

    /**
     * Generate ESQL lines and returns them as a list.
     *
     * @param source Input/Output
     * @param useRef REFERENCE/ESQL Type
     * @param useColors true to use colors.
     * @return
     */
    public ArrayList<String> getLinesAsList(ESQLSource source, boolean useRef, boolean useColors) {
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

    /**
     * Generate ESQL lines and returns as String block
     *
     * @param source Input/Output
     * @param useRef REFERENCE/ESQL Type
     * @param useColors true to use colors.
     * @return String block of Lines
     */
    public String getLinesAsString(ESQLSource source, boolean useRef, boolean useColors) {
        StringBuilder lines = new StringBuilder();
        ArrayList<String> lineList = this.getLinesAsList(source, useRef, useColors);
        for (String line : lineList) {
            lines.append(line);
            lines.append(System.getProperty("line.separator"));
        }
        return lines.toString();

    }

    /**
     * Print lines to the console with colors (bash)..
     *
     * @param source
     * @param useRef
     */
    private void printESQL(ESQLSource source, boolean useRef) {
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

    /**
     * print lines as Input with colors
     */
    public void printInputVariables() {
        this.printESQL(ESQLSource.INPUT, true);
    }

    /**
     * print lines as output with colors
     */
    public void printInputReferences() {
        this.printESQL(ESQLSource.INPUT, true);
    }

    /**
     * Add an empty line.
     *
     * @param allowMultiSuccessiveEmpty false to prevent successive empty lines.
     */
    public void addEmptyLine(boolean allowMultiSuccessiveEmpty) {
        if (!this.lastWasEmpty || (allowMultiSuccessiveEmpty && this.lastWasEmpty)) {
            this.elementsLines.add(new ESQLCommentLine(ESQLVerbosity.EMPTY_LINES, null));
        }
        this.lastWasEmpty = true;
    }

    /**
     * Clear block of lines, prefixes, and namespaces..
     */
    public void clear() {
        this.elementsLines.clear();
        this.prefixes.clear();
        this.nsDeclarations.clear();
        this.lastWasEmpty = false;
    }

}
