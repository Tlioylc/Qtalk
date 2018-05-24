package qumi.com.qumitalk.service.CustomElementProvider;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import qumi.com.qumitalk.service.CustomElement.GroupElement;


public class GroupsExtensionProvider extends IQProvider<GroupElement> {
    @Override
    public GroupElement parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException{

        int eventType = parser.next();
        while(eventType!=XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                // 判断当前事件是否为文档开始事件
                case XmlPullParser.START_DOCUMENT:
                    break;
                // 判断当前事件是否为标签元素开始事件
                case XmlPullParser.START_TAG:
                    if(parser.getName().equals("groups")){
                        if (parser.next() == XmlPullParser.TEXT) {
                            return new GroupElement(parser.getText());
                        }
                    }
                    break;
                // 判断当前事件是否为标签元素结束事件
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = parser.next();
        }

        return new GroupElement(null);
    }
}
