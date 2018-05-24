package qumi.com.qumitalk.service.CustomElement;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.iqprivate.packet.PrivateData;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import qumi.com.qumitalk.service.Util.LogUtil;

public class GroupElement extends IQ {

    public static final String ELEMENT = "groups";
    public static final String NAMESPACE = "com:qumi:group";


    private final String xml;
    private final String getElement;
    private final String getNamespace;

    public GroupElement(String xml) {
        this(xml, null, null);
        LogUtil.e("test--GroupElement--"+xml);
        setType(Type.set);
    }

    public GroupElement(String element, String namespace) {
        this(null, element, namespace);
        setType(Type.get);
    }

    private GroupElement(String xml, String getElement, String getNamespace) {
        super(ELEMENT, NAMESPACE);
        this.xml = xml;
        this.getElement = getElement;
        this.getNamespace = getNamespace;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();

        LogUtil.e("test--IQChildElementXmlStringBuilder--"+this.xml);
        if (xml != null) {
            xml.append(this.xml);
        } else {
            xml.halfOpenElement(getElement).xmlnsAttribute(getNamespace).closeEmptyElement();
        }
        return xml;
    }

}
