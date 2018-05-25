package qumi.com.qumitalk.service.CustomElement;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.iqprivate.packet.PrivateData;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import qumi.com.qumitalk.service.Util.LogUtil;

public class GroupElement extends IQ {

    public static final String ELEMENT = "groups";
    public static final String NAMESPACE = "com:qumi:group";


    private final String xml;

    public GroupElement(String xml) {//receive response
        this(xml, null, null);
        setType(Type.set);
    }

    public GroupElement() {//send request
        super(ELEMENT, NAMESPACE);
        this.xml = null;
    }

    private GroupElement(String xml, String getElement, String getNamespace) {
        super(ELEMENT, NAMESPACE);
        this.xml = xml;
//        this.getElement = getElement;
//        this.getNamespace = getNamespace;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();

        if (this.xml != null) {
            xml.append(this.xml);
        } else {
            xml.emptyElement("action type=\"getrooms\"");
        }
        return xml;
    }

}
