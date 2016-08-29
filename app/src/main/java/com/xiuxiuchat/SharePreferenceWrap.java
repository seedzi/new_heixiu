package com.xiuxiuchat;

import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

public class SharePreferenceWrap implements  SharedPreferences{

    private SharedPreferences wrapSharePreference;

    public SharePreferenceWrap(){
        wrapSharePreference = PersonalPreference.getInstance().getPreference(null);
    }

    public SharePreferenceWrap(String file){
        wrapSharePreference = PersonalPreference.getInstance().getPreference(file);
    }

    public int getInt(String key,int defValue){
        return wrapSharePreference.getInt(key,defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return wrapSharePreference.getLong(key,defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return wrapSharePreference.getFloat(key,defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return wrapSharePreference.getBoolean(key,defValue);
    }

    @Override
    public boolean contains(String key) {
        return wrapSharePreference.contains(key);
    }

    /**
     * 外部不要随便调用
     * @return
     */
    @Deprecated
    @Override
    public Editor edit() {
        return wrapSharePreference.edit();
    }

    @Override
    public Map<String, ?> getAll() {
        return wrapSharePreference.getAll();
    }

    @Override
    public String getString(String key,String defValue){
        return  wrapSharePreference.getString(key,defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return wrapSharePreference.getStringSet(key,defValues);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        wrapSharePreference.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        wrapSharePreference.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public boolean putString(String key, String value) {
        return edit().putString(key,value).commit();
    }

    public boolean putStringSet(String key, Set<String> values) {
        return edit().putStringSet(key,values).commit();
    }

    public boolean putInt(String key, int value) {
        return edit().putInt(key,value).commit();
    }

    public boolean putLong(String key, long value) {
        return edit().putLong(key,value).commit();
    }

    public boolean putFloat(String key, float value) {
        return edit().putFloat(key,value).commit();
    }

    public boolean putBoolean(String key, boolean value) {
        return edit().putBoolean(key,value).commit();
    }

    public boolean remove(String key) {
        return edit().remove(key).commit();
    }

    public boolean clear() {
        return edit().clear().commit();
    }

}