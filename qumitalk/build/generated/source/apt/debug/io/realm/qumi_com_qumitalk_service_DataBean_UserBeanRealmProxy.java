package io.realm;


import android.annotation.TargetApi;
import android.os.Build;
import android.util.JsonReader;
import android.util.JsonToken;
import io.realm.ProxyUtils;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.ColumnInfo;
import io.realm.internal.OsList;
import io.realm.internal.OsObject;
import io.realm.internal.OsObjectSchemaInfo;
import io.realm.internal.OsSchemaInfo;
import io.realm.internal.Property;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.Row;
import io.realm.internal.Table;
import io.realm.internal.android.JsonUtils;
import io.realm.log.RealmLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("all")
public class qumi_com_qumitalk_service_DataBean_UserBeanRealmProxy extends qumi.com.qumitalk.service.DataBean.UserBean
    implements RealmObjectProxy, qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface {

    static final class UserBeanColumnInfo extends ColumnInfo {
        long uidIndex;
        long nickNameIndex;
        long avatorIndex;
        long publicKeyIndex;

        UserBeanColumnInfo(OsSchemaInfo schemaInfo) {
            super(4);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("UserBean");
            this.uidIndex = addColumnDetails("uid", "uid", objectSchemaInfo);
            this.nickNameIndex = addColumnDetails("nickName", "nickName", objectSchemaInfo);
            this.avatorIndex = addColumnDetails("avator", "avator", objectSchemaInfo);
            this.publicKeyIndex = addColumnDetails("publicKey", "publicKey", objectSchemaInfo);
        }

        UserBeanColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new UserBeanColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final UserBeanColumnInfo src = (UserBeanColumnInfo) rawSrc;
            final UserBeanColumnInfo dst = (UserBeanColumnInfo) rawDst;
            dst.uidIndex = src.uidIndex;
            dst.nickNameIndex = src.nickNameIndex;
            dst.avatorIndex = src.avatorIndex;
            dst.publicKeyIndex = src.publicKeyIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private UserBeanColumnInfo columnInfo;
    private ProxyState<qumi.com.qumitalk.service.DataBean.UserBean> proxyState;

    qumi_com_qumitalk_service_DataBean_UserBeanRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (UserBeanColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<qumi.com.qumitalk.service.DataBean.UserBean>(this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$uid() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.uidIndex);
    }

    @Override
    public void realmSet$uid(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.uidIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.uidIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.uidIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.uidIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$nickName() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.nickNameIndex);
    }

    @Override
    public void realmSet$nickName(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.nickNameIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.nickNameIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.nickNameIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.nickNameIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$avator() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.avatorIndex);
    }

    @Override
    public void realmSet$avator(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.avatorIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.avatorIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.avatorIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.avatorIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$publicKey() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.publicKeyIndex);
    }

    @Override
    public void realmSet$publicKey(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.publicKeyIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.publicKeyIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.publicKeyIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.publicKeyIndex, value);
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("UserBean", 4, 0);
        builder.addPersistedProperty("uid", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("nickName", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("avator", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("publicKey", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static UserBeanColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new UserBeanColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "UserBean";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "UserBean";
    }

    @SuppressWarnings("cast")
    public static qumi.com.qumitalk.service.DataBean.UserBean createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = Collections.<String> emptyList();
        qumi.com.qumitalk.service.DataBean.UserBean obj = realm.createObjectInternal(qumi.com.qumitalk.service.DataBean.UserBean.class, true, excludeFields);

        final qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface objProxy = (qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) obj;
        if (json.has("uid")) {
            if (json.isNull("uid")) {
                objProxy.realmSet$uid(null);
            } else {
                objProxy.realmSet$uid((String) json.getString("uid"));
            }
        }
        if (json.has("nickName")) {
            if (json.isNull("nickName")) {
                objProxy.realmSet$nickName(null);
            } else {
                objProxy.realmSet$nickName((String) json.getString("nickName"));
            }
        }
        if (json.has("avator")) {
            if (json.isNull("avator")) {
                objProxy.realmSet$avator(null);
            } else {
                objProxy.realmSet$avator((String) json.getString("avator"));
            }
        }
        if (json.has("publicKey")) {
            if (json.isNull("publicKey")) {
                objProxy.realmSet$publicKey(null);
            } else {
                objProxy.realmSet$publicKey((String) json.getString("publicKey"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static qumi.com.qumitalk.service.DataBean.UserBean createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        final qumi.com.qumitalk.service.DataBean.UserBean obj = new qumi.com.qumitalk.service.DataBean.UserBean();
        final qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface objProxy = (qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) obj;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (false) {
            } else if (name.equals("uid")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$uid((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$uid(null);
                }
            } else if (name.equals("nickName")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$nickName((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$nickName(null);
                }
            } else if (name.equals("avator")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$avator((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$avator(null);
                }
            } else if (name.equals("publicKey")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$publicKey((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$publicKey(null);
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return realm.copyToRealm(obj);
    }

    public static qumi.com.qumitalk.service.DataBean.UserBean copyOrUpdate(Realm realm, qumi.com.qumitalk.service.DataBean.UserBean object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null) {
            final BaseRealm otherRealm = ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm();
            if (otherRealm.threadId != realm.threadId) {
                throw new IllegalArgumentException("Objects which belong to Realm instances in other threads cannot be copied into this Realm instance.");
            }
            if (otherRealm.getPath().equals(realm.getPath())) {
                return object;
            }
        }
        final BaseRealm.RealmObjectContext objectContext = BaseRealm.objectContext.get();
        RealmObjectProxy cachedRealmObject = cache.get(object);
        if (cachedRealmObject != null) {
            return (qumi.com.qumitalk.service.DataBean.UserBean) cachedRealmObject;
        }

        return copy(realm, object, update, cache);
    }

    public static qumi.com.qumitalk.service.DataBean.UserBean copy(Realm realm, qumi.com.qumitalk.service.DataBean.UserBean newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (qumi.com.qumitalk.service.DataBean.UserBean) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        qumi.com.qumitalk.service.DataBean.UserBean realmObject = realm.createObjectInternal(qumi.com.qumitalk.service.DataBean.UserBean.class, false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface realmObjectSource = (qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) newObject;
        qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface realmObjectCopy = (qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) realmObject;

        realmObjectCopy.realmSet$uid(realmObjectSource.realmGet$uid());
        realmObjectCopy.realmSet$nickName(realmObjectSource.realmGet$nickName());
        realmObjectCopy.realmSet$avator(realmObjectSource.realmGet$avator());
        realmObjectCopy.realmSet$publicKey(realmObjectSource.realmGet$publicKey());
        return realmObject;
    }

    public static long insert(Realm realm, qumi.com.qumitalk.service.DataBean.UserBean object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(qumi.com.qumitalk.service.DataBean.UserBean.class);
        long tableNativePtr = table.getNativePtr();
        UserBeanColumnInfo columnInfo = (UserBeanColumnInfo) realm.getSchema().getColumnInfo(qumi.com.qumitalk.service.DataBean.UserBean.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        String realmGet$uid = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$uid();
        if (realmGet$uid != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.uidIndex, rowIndex, realmGet$uid, false);
        }
        String realmGet$nickName = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$nickName();
        if (realmGet$nickName != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.nickNameIndex, rowIndex, realmGet$nickName, false);
        }
        String realmGet$avator = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$avator();
        if (realmGet$avator != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.avatorIndex, rowIndex, realmGet$avator, false);
        }
        String realmGet$publicKey = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$publicKey();
        if (realmGet$publicKey != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.publicKeyIndex, rowIndex, realmGet$publicKey, false);
        }
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(qumi.com.qumitalk.service.DataBean.UserBean.class);
        long tableNativePtr = table.getNativePtr();
        UserBeanColumnInfo columnInfo = (UserBeanColumnInfo) realm.getSchema().getColumnInfo(qumi.com.qumitalk.service.DataBean.UserBean.class);
        qumi.com.qumitalk.service.DataBean.UserBean object = null;
        while (objects.hasNext()) {
            object = (qumi.com.qumitalk.service.DataBean.UserBean) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            String realmGet$uid = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$uid();
            if (realmGet$uid != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.uidIndex, rowIndex, realmGet$uid, false);
            }
            String realmGet$nickName = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$nickName();
            if (realmGet$nickName != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.nickNameIndex, rowIndex, realmGet$nickName, false);
            }
            String realmGet$avator = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$avator();
            if (realmGet$avator != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.avatorIndex, rowIndex, realmGet$avator, false);
            }
            String realmGet$publicKey = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$publicKey();
            if (realmGet$publicKey != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.publicKeyIndex, rowIndex, realmGet$publicKey, false);
            }
        }
    }

    public static long insertOrUpdate(Realm realm, qumi.com.qumitalk.service.DataBean.UserBean object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(qumi.com.qumitalk.service.DataBean.UserBean.class);
        long tableNativePtr = table.getNativePtr();
        UserBeanColumnInfo columnInfo = (UserBeanColumnInfo) realm.getSchema().getColumnInfo(qumi.com.qumitalk.service.DataBean.UserBean.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        String realmGet$uid = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$uid();
        if (realmGet$uid != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.uidIndex, rowIndex, realmGet$uid, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.uidIndex, rowIndex, false);
        }
        String realmGet$nickName = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$nickName();
        if (realmGet$nickName != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.nickNameIndex, rowIndex, realmGet$nickName, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.nickNameIndex, rowIndex, false);
        }
        String realmGet$avator = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$avator();
        if (realmGet$avator != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.avatorIndex, rowIndex, realmGet$avator, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.avatorIndex, rowIndex, false);
        }
        String realmGet$publicKey = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$publicKey();
        if (realmGet$publicKey != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.publicKeyIndex, rowIndex, realmGet$publicKey, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.publicKeyIndex, rowIndex, false);
        }
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(qumi.com.qumitalk.service.DataBean.UserBean.class);
        long tableNativePtr = table.getNativePtr();
        UserBeanColumnInfo columnInfo = (UserBeanColumnInfo) realm.getSchema().getColumnInfo(qumi.com.qumitalk.service.DataBean.UserBean.class);
        qumi.com.qumitalk.service.DataBean.UserBean object = null;
        while (objects.hasNext()) {
            object = (qumi.com.qumitalk.service.DataBean.UserBean) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            String realmGet$uid = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$uid();
            if (realmGet$uid != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.uidIndex, rowIndex, realmGet$uid, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.uidIndex, rowIndex, false);
            }
            String realmGet$nickName = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$nickName();
            if (realmGet$nickName != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.nickNameIndex, rowIndex, realmGet$nickName, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.nickNameIndex, rowIndex, false);
            }
            String realmGet$avator = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$avator();
            if (realmGet$avator != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.avatorIndex, rowIndex, realmGet$avator, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.avatorIndex, rowIndex, false);
            }
            String realmGet$publicKey = ((qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) object).realmGet$publicKey();
            if (realmGet$publicKey != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.publicKeyIndex, rowIndex, realmGet$publicKey, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.publicKeyIndex, rowIndex, false);
            }
        }
    }

    public static qumi.com.qumitalk.service.DataBean.UserBean createDetachedCopy(qumi.com.qumitalk.service.DataBean.UserBean realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        qumi.com.qumitalk.service.DataBean.UserBean unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new qumi.com.qumitalk.service.DataBean.UserBean();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (qumi.com.qumitalk.service.DataBean.UserBean) cachedObject.object;
            }
            unmanagedObject = (qumi.com.qumitalk.service.DataBean.UserBean) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface unmanagedCopy = (qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) unmanagedObject;
        qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface realmSource = (qumi_com_qumitalk_service_DataBean_UserBeanRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$uid(realmSource.realmGet$uid());
        unmanagedCopy.realmSet$nickName(realmSource.realmGet$nickName());
        unmanagedCopy.realmSet$avator(realmSource.realmGet$avator());
        unmanagedCopy.realmSet$publicKey(realmSource.realmGet$publicKey());

        return unmanagedObject;
    }

    @Override
    @SuppressWarnings("ArrayToString")
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("UserBean = proxy[");
        stringBuilder.append("{uid:");
        stringBuilder.append(realmGet$uid() != null ? realmGet$uid() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{nickName:");
        stringBuilder.append(realmGet$nickName() != null ? realmGet$nickName() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{avator:");
        stringBuilder.append(realmGet$avator() != null ? realmGet$avator() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{publicKey:");
        stringBuilder.append(realmGet$publicKey() != null ? realmGet$publicKey() : "null");
        stringBuilder.append("}");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public ProxyState<?> realmGet$proxyState() {
        return proxyState;
    }

    @Override
    public int hashCode() {
        String realmName = proxyState.getRealm$realm().getPath();
        String tableName = proxyState.getRow$realm().getTable().getName();
        long rowIndex = proxyState.getRow$realm().getIndex();

        int result = 17;
        result = 31 * result + ((realmName != null) ? realmName.hashCode() : 0);
        result = 31 * result + ((tableName != null) ? tableName.hashCode() : 0);
        result = 31 * result + (int) (rowIndex ^ (rowIndex >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        qumi_com_qumitalk_service_DataBean_UserBeanRealmProxy aUserBean = (qumi_com_qumitalk_service_DataBean_UserBeanRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aUserBean.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aUserBean.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aUserBean.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
