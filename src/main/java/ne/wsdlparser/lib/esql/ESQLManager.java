package ne.wsdlparser.lib.esql;

import com.sun.istack.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

import ne.wsdlparser.lib.utility.Utils;
import ne.wsdlparser.lib.WSDLManager;
import ne.wsdlparser.lib.constant.ESQLVerbosity;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;
/**
 * Handles ESQL generation stuff..
 * @author nour */
public class ESQLManager {
    
    private int level = 0;
    private final WSDLManager manager;
    private final ESQLBlock block;
    private final ArrayList<String> paramTree;
    private ESQLVerbosity[] verbosities;
    /**
     * 
     * @param manager WSDLManager instance injection
     */
    public ESQLManager(WSDLManager manager) {
        this.verbosities = new ESQLVerbosity[0];
        this.manager = manager;
        this.block = new ESQLBlock(manager);
        this.paramTree = new ArrayList<>();
    }
    /**
     * Pass prefix to the ESQL block instance
     * @param prefix 
     */
    private void addPrefix(String prefix) {
        this.block.addPrefix(prefix);
    }
    /**
     * Checks if param is already added to the xpath.
     * @param param parameter name
     * @return true if was added before.
     */
    private boolean existsInParamTree(String param) {
        for (String par : this.paramTree) {
            String paramWithoutPrefix = Utils.splitPrefixes(par)[1];
            if (paramWithoutPrefix == null) {
                paramWithoutPrefix = par;
            }
            if (param.equals(paramWithoutPrefix)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Increase the ESQL xPath level with a new Complex Element.
     * @param prefix prefix of the element
     * @param param name of the element.
     * @param hasChildren true if the element has printable stuff.
     */
    public void levelUp(String prefix, String param, boolean hasChildren) {
        if (param == null) {
            return;
        }
        if (this.existsInParamTree(param)) {
            return;
        }
        // this.levelIsRaised = true;
        // this.isRaised.put(Utils.getParamWithPrefix(param, prefix), true);

        this.level++;
        this.paramTree.add(Utils.getParamWithPrefix(prefix, param));
        this.addPrefix(prefix);
        if (this.isVerbosityEnabled(ESQLVerbosity.STRUCTURE) && hasChildren) {
            this.addLevelUpComment(param);
        }
    }
    
    /**
     * Add a comment to the ESQL block of lines.
     * @param verbosity Comment verbosity level
     * @param comment comment value
     */
    public void addComment(ESQLVerbosity verbosity, String comment) {
        if (comment == null) {
            return;
        }
        if (isVerbosityEnabled(verbosity)) {
            this.block.addLine(new ESQLCommentLine(verbosity, comment));
        }
    }
    /**
     * End the ESQL line with a simple element value
     * @param prefix element prefix
     * @param param element name
     * @param type simple element type 
     * @param defaultValue default or fixed value if found
     */
    public void addParam(@Nullable String prefix, String param, XSDSimpleElementType type, String defaultValue) {
        if (param == null) {
            return;
        }
        this.addPrefix(prefix);
        ESQLSetterLine line;
        if (param.isEmpty()) {
            line = new ESQLSetterLine(String.join(".", this.paramTree), type, defaultValue);
        } else {
            line = new ESQLSetterLine("".concat(
                    String.join(".", this.paramTree).concat(".").concat(Utils.getParamWithPrefix(prefix, param).trim())),
                    type, defaultValue);
        }
        this.block.addLine(line);
    }
    /**
     * Level down after all children of a complex element has been finished
     *  @param prefix element prefix
     * @param param element name
     * @param hasChildren true if it had had children
     */
    public void levelDown(String prefix, String param, boolean hasChildren) {
        // if (!this.levelIsRaised)
        // return;
        if (param == null) {
            return;
        }
        
        String nameWithPrefix = Utils.getParamWithPrefix(prefix, param);
        if (this.isVerbosityEnabled(ESQLVerbosity.STRUCTURE) && this.paramTree.contains(nameWithPrefix) && hasChildren) {
            this.addLevelDownComment(param);
        }
        this.paramTree.remove(nameWithPrefix);
        this.level--;
        
    }
    /** 
     * Getter for the ESQL block
     * @return 
     */
    public ESQLBlock getESQLBlock() {
        return this.block;
    }
    /**
     * Adds a new empty line.
     * @param allowMultiSuccessiveEmpty 
     */
    public void addEmptyLine(boolean allowMultiSuccessiveEmpty) {
        this.block.addEmptyLine(allowMultiSuccessiveEmpty);
    }
    /**
     * Clear ParamTree...
     */
    public void clearTree() {
        this.paramTree.clear();
    }
    /**
     * CLear Param Tree and ESQL block of lines as well
     */
    public void clearAll() {
        this.clearTree();
        this.block.clear();
    }
    /**
     * Add a structure level up comment
     * @param name 
     */
    public void addLevelUpComment(String name) {
        if (name == null) {
            return;
        }
        String levelSplitter = "";
        for (int i = 0; i < this.paramTree.size(); i++) {
            levelSplitter += "====>> ";
        }
        
        this.addComment(ESQLVerbosity.STRUCTURE, levelSplitter + name);
    }
    /**
     * Add a structure level down comment
     * @param nameWithPrefix 
     */
    public void addLevelDownComment(String nameWithPrefix) {
        if (nameWithPrefix == null) {
            return;
        }
        String levelSplitter = "";
        for (int i = 0; i < this.paramTree.size(); i++) {
            levelSplitter += "<<==== ";
        }
        if (!levelSplitter.isEmpty()) {
            this.addComment(ESQLVerbosity.STRUCTURE, levelSplitter + nameWithPrefix);
        }
    }
    /**
     * Set ESQL verbosity level
     * @param verbosity 
     */
    public void setVerbosity(ESQLVerbosity... verbosity) {
        this.verbosities = verbosity;
    }
    /**
     * Check if verbosity is enabled
     * @param v
     * @return 
     */
    public boolean isVerbosityEnabled(ESQLVerbosity v) {
        return Arrays.stream(this.verbosities).anyMatch(verbosity -> verbosity == v);
    }
}
