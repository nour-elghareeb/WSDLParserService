package ne.wsdlparser.lib.esql;

import ne.wsdlparser.lib.esql.constant.ESQLSource;
/**
 * An abstract ESQL line implementation for generic purposes.
 * @author nour
 */
public abstract class ESQLLine {

    protected ESQLSource source;
    protected boolean useReference;

    ESQLLine() {

    }

    abstract String generate(boolean useColors);

    abstract public void print();

    public void setSource(ESQLSource source) {
        this.source = source;
    }

    public void useReferences(boolean useReference) {
        this.useReference = useReference;
    }
}
