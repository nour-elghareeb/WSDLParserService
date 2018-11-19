package ne.wsdlparser.lib.esql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ne.wsdlparser.lib.Utils;
import ne.wsdlparser.lib.WSDLManager;
import ne.wsdlparser.lib.constant.ESQLVerbosity;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;

public class ESQLManager {
    private int level = 0;
    private WSDLManager manager;
    private ESQLBlock block;
    private ArrayList<String> paramTree;
    private boolean levelIsRaised = false;
    private HashMap<String, Boolean> isRaised = new HashMap<String, Boolean>();

    public ESQLManager(WSDLManager manager) {
        this.manager = manager;
        this.block = new ESQLBlock(manager);
        this.paramTree = new ArrayList<String>();
    }

    private void addPrefix(String prefix) {
        this.block.addPrefix(prefix);
    }

    private boolean existsInParamTree(String param) {
        for (String par : this.paramTree) {
            String paramWithoutPrefix = Utils.splitPrefixes(par)[1];
            if (paramWithoutPrefix == null)
                paramWithoutPrefix = par;
            if (param.equals(paramWithoutPrefix))
                return true;
        }
        return false;
    }

    public void levelUp(String prefix, String param, boolean hasChildren) {
        if (param == null) {
            return;
        }
        if (this.existsInParamTree(param))
            return;
        // this.levelIsRaised = true;
        // this.isRaised.put(Utils.getParamWithPrefix(param, prefix), true);

        this.level++;
        this.paramTree.add(Utils.getParamWithPrefix(prefix, param));
        this.addPrefix(prefix);
        if (hasChildren)
            this.addLevelUpComment(param);
    }

    public void addComment(ESQLVerbosity verbosity, String title, String comment) {
        if (comment == null)
            return;
        this.block.addLine(new ESQLCommentLine(verbosity, title, comment));
    }

    public void addComment(ESQLVerbosity verbosity, String comment) {
        if (comment == null)
            return;
        this.block.addLine(new ESQLCommentLine(verbosity, comment));
    }

    public void addParam(String prefix, String param, XSDSimpleElementType type, String defaultValue) {
        if (param == null)
            return;
        this.addPrefix(prefix);
        ESQLSetterLine line;
        if (param.isEmpty())
            line = new ESQLSetterLine(String.join(".", this.paramTree), type, defaultValue);
        else
            line = new ESQLSetterLine("".concat(
                    String.join(".", this.paramTree).concat(".").concat(Utils.getParamWithPrefix(prefix, param).trim())),
                    type, defaultValue);
        this.block.addLine(line);
    }

    public void levelDown(String param, String prefix, boolean hasChildren) {
        // if (!this.levelIsRaised)
        // return;
        if (param == null)
            return;

        String nameWithPrefix = Utils.getParamWithPrefix(prefix, param);
        if (this.paramTree.contains(nameWithPrefix) && hasChildren)
            this.addLevelDownComment(param);
        this.paramTree.remove(nameWithPrefix);
        this.level--;

    }

    public ESQLBlock getESQLBlock() {
        return this.block;
    }

    public void addEmptyLine(boolean allowMultiSuccessiveEmpty) {
        this.block.addEmptyLine(allowMultiSuccessiveEmpty);
    }

    public void clearTree() {
        this.paramTree.clear();
    }

    public void clearAll() {
        this.clearTree();
        this.block.clear();
    }

    public void addLevelUpComment(String name) {
        if (name == null)
            return;
        String levelSplitter = "";
        for (int i = 0; i < this.paramTree.size(); i++) {
            levelSplitter += "====>> ";
        }
        this.addComment(ESQLVerbosity.STRUCTURE, levelSplitter + name);
    }

    public void addLevelDownComment(String nameWithPrefix) {
        if (nameWithPrefix == null)
            return;
        String levelSplitter = "";
        for (int i = 0; i < this.paramTree.size(); i++) {
            levelSplitter += "<<==== ";
        }
        if (!levelSplitter.isEmpty())
            this.addComment(ESQLVerbosity.STRUCTURE, levelSplitter + nameWithPrefix);
    }

    public void setVerbosity(ESQLVerbosity... verbosity) {
        this.block.setVerbosity(verbosity);
    }
}