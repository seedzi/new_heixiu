package com.xiuxiuchat.bean;




import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 自动实现json字段的填充转换，具体参考 UpdateInfo 的实现使用
 * 例如：
 * UpdateInfo info = new UpdateInfo();
 * info.populateData(your_json_obj);
 * @author zhch
 *
 */
public class JsonBean {

    /**
     * 使用JSONObject填充数据
     * @param data 一般是个JSONObject
     */
    public void populateData(Map data){
        JsonBean.populateData(this, data);
    }

    /**
     * 当需要填充的对象不是this时，指定填充具体的填充对象，主要用在递归填充对象时使用
     * @param object
     * @param data
     */
    public static void populateData(Object object, Map data){
        if (data == null || data.size() == 0) {
            return;
        }

        Set<String> keySet = data.keySet();
        for (String key : keySet) {
            Object value = data.get(key);
            if (value == null) {
                continue;
            }

            try {
                Method method = object.getClass().getMethod("set"+key.substring(0, 1).toUpperCase()+key.substring(1)
                        , Object.class);
                method.invoke(object, value);
            } catch (NoSuchMethodException e) {
                Field field = null;
                try {

                    field = object.getClass().getField(key);

                    if (value instanceof JSONObject) {
                      JsonBean jsonBean  = (JsonBean)field.getType().newInstance();
                      JsonBean.populateData(jsonBean,(JSONObject)value);

                      field.set(object, jsonBean);
                    }else if(value instanceof JSONArray){
                        ArrayList list = new ArrayList();
                        JSONArray jsonList = (JSONArray)value;
                        Class listItemClass = (Class) ((ParameterizedType)field.getGenericType())
                                .getActualTypeArguments()[0];

                        for (Object item : jsonList) {

                            // 不处理数组嵌套数组
                            if (item instanceof JSONObject) {
                                JsonBean bean = (JsonBean)listItemClass.newInstance();
                                populateData(bean, (JSONObject)item);
                                list.add(bean);
                            }else {
                                list.add(item);
                            }
                        }

                        field.set(object, list);
                    }else {
                        if (field.getType() == int.class) {
                            field.setInt(object, Integer.parseInt(value.toString()));
                        }else {
                            field.set(object, value);
                        }
                    }

                } catch (Exception e1) {
//                    e1.printStackTrace();
                    continue;
                }
            }catch (Exception e) {
//                e.printStackTrace();
                continue;
            }

        }

    }

    /**
     * 将JsonBean转为Map方便存储和打印使用
     * @return
     */
    public Map convertToMap(){
        Field[] fields = getClass().getFields();
        if (fields != null) {
            HashMap hashMap = new HashMap();
            for (Field field : fields) {
                try {
                    hashMap.put(field.getName(), field.get(this));
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
            return hashMap;
        }
        return null;
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(convertToMap());
    }
}
