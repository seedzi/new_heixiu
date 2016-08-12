package com.hyphenate.easeui.model;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojicon.Type;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.utils.EaseSmileUtils;

import java.util.Arrays;

public class EmojiconXiuxiuGroupData {


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


}
