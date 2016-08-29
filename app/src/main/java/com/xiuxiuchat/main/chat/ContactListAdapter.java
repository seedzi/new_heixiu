package com.xiuxiuchat.main.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.EMLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiuchat.R;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.main.MainActivity;
import com.xiuxiuchat.user.voice.VoicePlayManager;
import com.xiuxiuchat.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzhi on 16-6-5.
 */
public class ContactListAdapter extends ArrayAdapter<EaseUser> implements SectionIndexer {

    private static final String TAG = "ContactAdapter";
    List<String> list;
    List<EaseUser> userList;
    List<EaseUser> copyUserList;
    private LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private int res;
    private MyFilter myFilter;
    private boolean notiyfyByFilter;

    public ContactListAdapter(Context context, int resource, List<EaseUser> objects) {
        super(context, resource, objects);
        this.res = resource;
        this.userList = objects;
        copyUserList = new ArrayList<EaseUser>();
        copyUserList.addAll(objects);
        layoutInflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        ImageView avatar;
        TextView nameView;
//        TextView headerView;
        TextView userAgeTv; //年纪
        ImageView vipGradeIv; //vip等级
        TextView signatureTv; //签名
        TextView onlineTimeTv; //在线时间
        ImageView listItemPlayIv; //播放按钮
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            if(res == 0)
                convertView = layoutInflater.inflate(R.layout.contact_item_layout, null);
            else
                convertView = layoutInflater.inflate(res, null);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.nameView = (TextView) convertView.findViewById(R.id.name);
            holder.userAgeTv = (TextView)convertView.findViewById(R.id.user_age);
            holder.vipGradeIv = (ImageView) convertView.findViewById(R.id.vip_grade);
            holder.signatureTv = (TextView) convertView.findViewById(R.id.signature);
            holder.onlineTimeTv = (TextView) convertView.findViewById(R.id.online_time);
            holder.listItemPlayIv = (ImageView) convertView.findViewById(R.id.list_item_play);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        EaseUser user = getItem(position);
        if(user == null)
            Log.d("ContactAdapter", position + "");
        final String username = user.getUsername();
        String header = user.getInitialLetter();
        /*
        if (position == 0 || header != null && !header.equals(getItem(position - 1).getInitialLetter())) {
            if (TextUtils.isEmpty(header)) {
                holder.headerView.setVisibility(View.GONE);
            } else {
                holder.headerView.setVisibility(View.VISIBLE);
                holder.headerView.setText(header);
            }
        } else {
            holder.headerView.setVisibility(View.GONE);
        }
        */
        EaseUser u = EaseUserUtils.getUserInfo(username);
        //设置昵称
//        EaseUserUtils.setUserNick(username, holder.nameView);
        if(u!=null){
            if(u.getNick()!=null) {
                holder.nameView.setText(u.getNick());
            }else{
                holder.nameView.setText(u.getUsername());
            }
        }
        //设置头像 changed by huzhi
        if(u!=null && u.getAvatar()!=null){
            android.util.Log.d(TAG,"u.getAvatar() = " + u.getAvatar());
            ImageLoader.getInstance().displayImage(u.getAvatar(), holder.avatar);
            /*
            holder.avatar.setTag(u.getUsername());
            holder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonDetailActivity.startActivity(MainActivity.getInstance(), (String) v.getTag(),true);
                }
            });*/
        }
//        EaseUserUtils.setUserAvatar(/*getContext()*/MainActivity.getInstance(), username, holder.avatar);
        //设置签名
//        EaseUserUtils.setUserSignature(username, holder.signatureTv);
        if(u!=null){
            if(u.getNick()!=null) {
                holder.signatureTv.setText(u.getSign());
            }
        }
        //设置魅力等级或者财富值
        if(EaseUserUtils.getUserInfo(username)!=null) {
            if (XiuxiuUserInfoResult.isMale(EaseUserUtils.getUserInfo(username).getSex())) {
                XiuxiuUserInfoResult.setWealthValue(holder.vipGradeIv, EaseUserUtils.getUserInfo(username).getFortune());
            } else {
                XiuxiuUserInfoResult.setCharmValue(holder.vipGradeIv, EaseUserUtils.getUserInfo(username).getCharm());
            }
        }
        //设置用户年纪
        if(u != null ){
            holder.userAgeTv.setText(u.getAge());
            if(XiuxiuUserInfoResult.isMale(u.getSex())){
                holder.userAgeTv.setBackgroundResource(R.drawable.male_age_bg);
            }else{
                holder.userAgeTv.setBackgroundResource(R.drawable.female_age_bg);
            }
        }

        try {
            if (u != null && holder.onlineTimeTv != null) {
                holder.onlineTimeTv.setText(DateUtils.getTextByTime(
                        getContext(),
                        getItem(position).getOnlineTime(),
                        R.string.date_fromate_anecdote));
            }
        }catch (Exception e){}

        //播放语音
        holder.listItemPlayIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoicePlayManager.getInstance().release();
                VoicePlayManager.getInstance().init(MainActivity.getInstance(), holder.listItemPlayIv,
                        R.drawable.list_item_stop, R.drawable.list_item_play);
                VoicePlayManager.getInstance().play(HttpUrlManager.QI_NIU_HOST + EaseUserUtils.getUserInfo(username).getVoice());
            }
        });

        if(primaryColor != 0)
            holder.nameView.setTextColor(primaryColor);
        if(primarySize != 0)
            holder.nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
//        if(initialLetterBg != null)
//            holder.headerView.setBackgroundDrawable(initialLetterBg);
//        if(initialLetterColor != 0)
//            holder.headerView.setTextColor(initialLetterColor);

        return convertView;
    }


    @Override
    public EaseUser getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        list = new ArrayList<String>();
        list.add(getContext().getString(com.hyphenate.easeui.R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {

            String letter = getItem(i).getInitialLetter();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public Filter getFilter() {
        if(myFilter==null){
            myFilter = new MyFilter(userList);
        }
        return myFilter;
    }

    protected class  MyFilter extends Filter{
        List<EaseUser> mOriginalList = null;

        public MyFilter(List<EaseUser> myList) {
            this.mOriginalList = myList;
        }

        @Override
        protected synchronized FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if(mOriginalList==null){
                mOriginalList = new ArrayList<EaseUser>();
            }
            EMLog.d(TAG, "contacts original size: " + mOriginalList.size());
            EMLog.d(TAG, "contacts copy size: " + copyUserList.size());

            if(prefix==null || prefix.length()==0){
                results.values = copyUserList;
                results.count = copyUserList.size();
            }else{
                String prefixString = prefix.toString();
                final int count = mOriginalList.size();
                final ArrayList<EaseUser> newValues = new ArrayList<EaseUser>();
                for(int i=0;i<count;i++){
                    final EaseUser user = mOriginalList.get(i);
//                    String username = user.getNick();
//                    if(username == null)
//                        username = user.getNick();
                    String username = user.getUsername();

                    if(username.startsWith(prefixString)){
                        newValues.add(user);
                    }
                    else{
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(user);
                                break;
                            }
                        }
                    }
                }
                results.values=newValues;
                results.count=newValues.size();
            }
            EMLog.d(TAG, "contacts filter results size: " + results.count);
            return results;
        }

        @Override
        protected synchronized void publishResults(CharSequence constraint,
                                                   FilterResults results) {
            userList.clear();
            userList.addAll((List<EaseUser>)results.values);
            EMLog.d(TAG, "publish contacts filter results size: " + results.count);
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
                notiyfyByFilter = false;
            } else {
                notifyDataSetInvalidated();
            }
        }
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(!notiyfyByFilter){
            copyUserList.clear();
            copyUserList.addAll(userList);
        }
    }

    protected int primaryColor;
    protected int primarySize;
    protected Drawable initialLetterBg;
    protected int initialLetterColor;

    public ContactListAdapter setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }


    public ContactListAdapter setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
        return this;
    }

    public ContactListAdapter setInitialLetterBg(Drawable initialLetterBg) {
        this.initialLetterBg = initialLetterBg;
        return this;
    }

    public ContactListAdapter setInitialLetterColor(int initialLetterColor) {
        this.initialLetterColor = initialLetterColor;
        return this;
    }
}
