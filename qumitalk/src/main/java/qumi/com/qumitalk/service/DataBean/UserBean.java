package qumi.com.qumitalk.service.DataBean;

import io.realm.Realm;
import io.realm.RealmObject;

public class UserBean extends RealmObject{
    private String uid;
    private String nickName;
    private String avator;
    private boolean isAvailable;//在线状态
    private String mood;//签名
    private String publicKey;//公钥
    private int friendRelationship;//好友关系

    public int getFriendRelationship() {
        return friendRelationship;
    }

    public void setFriendRelationship(int friendRelationship) {
        this.friendRelationship = friendRelationship;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
