package qumi.com.qumitalk.service.Util;

import org.jivesoftware.smack.packet.IQ;


/**
 * 自定义IQ
 <iq id="0001" to="search@jiyq.cn" type=”get”>
 <query xmls="emcc.jiyq" condition="what"/>
 <extrament>element</extrament>
 <query>
 </iq>


 <iq type="get" from="mm#201805161532.dfh@openfire.qumitech.com/Spark" id="123-98">
 <groups xmlns="com:qumi:group">
 <action type="getrooms" />
 81
 </groups></iq>

 * */

public class QMCostomIQ extends IQ{



    /**
     * childElementName ： groups
     * childElementNamespace : com:qumi:group
     *
     * */

    public QMCostomIQ(String childElementName, String childElementNamespace,
                       String condition, String value)
    {
        super(childElementName, childElementNamespace);

    }

    @Override
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {

        xml.rightAngleBracket();
        xml.emptyElement("action type=\"getrooms\"");
        return xml;
    }


}
