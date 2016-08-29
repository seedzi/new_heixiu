package com.xiuxiuchat.widget.city;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wheel.widget.OnWheelChangedListener;
import com.wheel.widget.WheelView;
import com.wheel.widget.adapters.ArrayWheelAdapter;
import com.xiuxiuchat.R;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by huzhi on 16-5-25.
 */
public class CityDialog extends AlertDialog implements DialogInterface.OnClickListener, OnWheelChangedListener {

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        }
    }

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        void onDateSet(String city);
    }


    private OnDateSetListener mCallBack;
    private WheelView mViewCity;
    private WheelView mViewProvince;
    private ViewGroup mLayout;

    public CityDialog(Context context,OnDateSetListener callBack) {
        super(context, 0);

        initProvinceDatas();

        mCallBack = callBack;

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, (CharSequence)"确认", this);
        setButton(BUTTON_NEGATIVE, (CharSequence) "取消", this);
        setIcon(0);

        LayoutInflater inflater =
                (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (ViewGroup)inflater.inflate(R.layout.city_picker_dialog, null);
        setView(mLayout);
        setTitle("请选择城市");
        setupViews();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == BUTTON_POSITIVE){
            tryNotifyDateSet();
        }
    }

    private void setupViews(){
        mViewCity = (WheelView) mLayout.findViewById(R.id.city);
        mViewCity.setWheelForeground(R.drawable.wheel_val_holo);
        mViewCity.setShadowColor(0x88FFFFFF, 0x88FFFFFF, 0x88FFFFFF);
        mViewCity.setVisibleItems(7);
        mViewCity.addChangingListener(this);

        mViewProvince = (WheelView) mLayout.findViewById(R.id.province);
        mViewProvince.setWheelForeground(R.drawable.wheel_val_holo);
        mViewProvince.setShadowColor(0x88FFFFFF, 0x88FFFFFF, 0x88FFFFFF);
        mViewProvince.setViewAdapter(new ArrayWheelAdapter(getContext(), mProvinceDatas));
        mViewProvince.setVisibleItems(7);
        mViewProvince.addChangingListener(this);


        updateCities();
        updateAreas();
    }

    private void tryNotifyDateSet(){
        if (mCallBack != null) {
            String value = "";
            if(mCurrentProviceName.equals(mCurrentCityName)){
                value = mCurrentCityName;
            }else{
                value = mCurrentProviceName+mCurrentCityName;
            }
            mCallBack.onDateSet(value.replaceAll("市",""));
        }
    }



    //==========================================================================================
    // 城市
    //==========================================================================================
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[] { "" };
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
    }


    protected String[] mProvinceDatas;
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();
    protected String mCurrentProviceName;
    protected String mCurrentCityName;
    protected String mCurrentZipCode = "";


    protected void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = getContext().getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            provinceList = handler.getDataList();
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                }
            }
            // */
            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    cityNames[j] = cityList.get(j).getName();
                }
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }

}
