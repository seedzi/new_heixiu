package com.xiuxiu;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PersonalPreference {

    private static PersonalPreference instance;

    private Map<String, SharedPreferences> factory;

    private static final String defaults = "yuekuapp";

    private PersonalPreference() {
        if (factory == null) {
            factory=new ConcurrentHashMap<String, SharedPreferences>();
        }
    }

    public static PersonalPreference getInstance() {
        if (instance == null) {
            synchronized (PersonalPreference.class) {
                if (instance == null) {
                    instance = new PersonalPreference();
                }
            }
        }
        return instance;
    }

    public void destory() {
        factory.clear();
        factory = null;
        instance = null;
    }

    public SharedPreferences getPreference(String file) {
        if (file == null || "".equals(file)) {
            file = defaults;
        }
        SharedPreferences sharePreference = factory.get(file);
        if (sharePreference == null) {
            sharePreference = XiuxiuApplication.getInstance().getSharedPreferences(file, Context.MODE_PRIVATE);
            factory.put(file, sharePreference);
        }
        return sharePreference;
    }

}
