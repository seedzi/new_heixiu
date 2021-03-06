package com.hyphenate.easeui.model;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojicon.Type;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.utils.EaseSmileUtils;

import java.util.Arrays;

public class EmojiconExampleGroupData {

    private static String[] emojis = new String[]{
            EaseSmileUtils.ee_1,
            EaseSmileUtils.ee_2,
            EaseSmileUtils.ee_3,
            EaseSmileUtils.ee_4,
            EaseSmileUtils.ee_5,
            EaseSmileUtils.ee_6,
            EaseSmileUtils.ee_7,
            EaseSmileUtils.ee_8,
            EaseSmileUtils.ee_9,
            EaseSmileUtils.ee_10,
            EaseSmileUtils.ee_11,
            EaseSmileUtils.ee_12,
            EaseSmileUtils.ee_13,
            EaseSmileUtils.ee_14,
            EaseSmileUtils.ee_15,
            EaseSmileUtils.ee_16,
            EaseSmileUtils.ee_17,
            EaseSmileUtils.ee_18,
            EaseSmileUtils.ee_19,
            EaseSmileUtils.ee_20,
            EaseSmileUtils.ee_21,
            EaseSmileUtils.ee_22,
            EaseSmileUtils.ee_23,
            EaseSmileUtils.ee_24,
            EaseSmileUtils.ee_25,
            EaseSmileUtils.ee_26,
            EaseSmileUtils.ee_27,
            EaseSmileUtils.ee_28,
            EaseSmileUtils.ee_29,
            EaseSmileUtils.ee_30,
            EaseSmileUtils.ee_31,
            EaseSmileUtils.ee_32,
            EaseSmileUtils.ee_33,
            EaseSmileUtils.ee_34,
            EaseSmileUtils.ee_35,

    };
    
    private static int[] icons = new int[]{
        R.drawable.icon_001_cover,
        R.drawable.icon_002_cover,
        R.drawable.icon_003_cover,
        R.drawable.icon_004_cover,
        R.drawable.icon_005_cover,
        R.drawable.icon_006_cover,
        R.drawable.icon_007_cover,
        R.drawable.icon_008_cover,
        R.drawable.icon_009_cover,
        R.drawable.icon_010_cover,
        R.drawable.icon_011_cover,
        R.drawable.icon_012_cover,
        R.drawable.icon_013_cover,
        R.drawable.icon_014_cover,
        R.drawable.icon_015_cover,
        R.drawable.icon_016_cover,
        R.drawable.icon_017_cover,
        R.drawable.icon_018_cover,
        R.drawable.icon_019_cover,
        R.drawable.icon_020_cover,
        R.drawable.icon_021_cover,
        R.drawable.icon_022_cover,
        R.drawable.icon_023_cover,
        R.drawable.icon_024_cover,
        R.drawable.icon_025_cover,
        R.drawable.icon_026_cover,
    };
    
    private static int[] bigIcons = new int[]{
            R.drawable.icon_001_cover,
            R.drawable.icon_002_cover,
            R.drawable.icon_003_cover,
            R.drawable.icon_004_cover,
            R.drawable.icon_005_cover,
            R.drawable.icon_006_cover,
            R.drawable.icon_007_cover,
            R.drawable.icon_008_cover,
            R.drawable.icon_009_cover,
            R.drawable.icon_010_cover,
            R.drawable.icon_011_cover,
            R.drawable.icon_012_cover,
            R.drawable.icon_013_cover,
            R.drawable.icon_014_cover,
            R.drawable.icon_015_cover,
            R.drawable.icon_016_cover,
            R.drawable.icon_017_cover,
            R.drawable.icon_018_cover,
            R.drawable.icon_019_cover,
            R.drawable.icon_020_cover,
            R.drawable.icon_021_cover,
            R.drawable.icon_022_cover,
            R.drawable.icon_023_cover,
            R.drawable.icon_024_cover,
            R.drawable.icon_025_cover,
            R.drawable.icon_026_cover,
    };
    
    /*
    private static final EaseEmojiconGroupEntity DATA = createData();
    
    private static EaseEmojiconGroupEntity createData(){
        EaseEmojiconGroupEntity emojiconGroupEntity = new EaseEmojiconGroupEntity();
        EaseEmojicon[] datas = new EaseEmojicon[icons.length];
        for(int i = 0; i < icons.length; i++){
            datas[i] = new EaseEmojicon(icons[i], null, Type.BIG_EXPRESSION);
            datas[i].setBigIcon(bigIcons[i]);
            datas[i].setName("示例"+ (i+1));
            datas[i].setIdentityCode("em"+ (1000+i+1));
        }
        emojiconGroupEntity.setEmojiconList(Arrays.asList(datas));
        emojiconGroupEntity.setIcon(R.drawable.ee_2);
        emojiconGroupEntity.setType(Type.BIG_EXPRESSION);
        return emojiconGroupEntity;
    }
    
    
    public static EaseEmojiconGroupEntity getData(){
        return DATA;
    }
    */


    private static final EaseEmojiconGroupEntity DATA = createData();

    private static EaseEmojiconGroupEntity createData(){
        EaseEmojiconGroupEntity emojiconGroupEntity = new EaseEmojiconGroupEntity();
        EaseEmojicon[] datas = new EaseEmojicon[icons.length];
        for(int i = 0; i < icons.length; i++){
            datas[i] = new EaseEmojicon(icons[i], emojis[i], Type.NORMAL);
        }
        emojiconGroupEntity.setEmojiconList(Arrays.asList(datas));
        emojiconGroupEntity.setIcon(R.drawable.ee_2);
        emojiconGroupEntity.setType(Type.NORMAL);
        return emojiconGroupEntity;
    }

    public static EaseEmojiconGroupEntity getData(){
        return DATA;
    }
}
