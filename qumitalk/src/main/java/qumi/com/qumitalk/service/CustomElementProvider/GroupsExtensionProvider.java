package qumi.com.qumitalk.service.CustomElementProvider;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import qumi.com.qumitalk.service.CustomElement.GroupElement;

/**
 * IQ解析器
 *
 * */
public class GroupsExtensionProvider extends IQProvider<GroupElement> {
    @Override
    public GroupElement parse(XmlPullParser parser, int initialDepth)  throws XmlPullParserException, IOException{


            int eventType2;
            StringBuilder stringBuilder = new StringBuilder();
            outerloop: while (true)
            {
                int eventType = parser.next();
                switch (eventType) {
                case XmlPullParser.START_TAG:
                    if(parser.getName().equals("groups")){
                        eventType2 = parser.next();
                        if (eventType2 == XmlPullParser.START_TAG && parser.getName().equals("room")) {
                            parser.next();
                            stringBuilder.append(parser.getText());
                        }
                    }if(parser.getName().equals("room")){
                        parser.next();
                        stringBuilder.append(parser.getText()+"&");
                    }
                    break;
                    case XmlPullParser.END_TAG:
                        if (parser.getDepth() == initialDepth) {
                            break outerloop;
                        }
                        break;
                }
            }
            return  new GroupElement(stringBuilder.toString());
    }
}
