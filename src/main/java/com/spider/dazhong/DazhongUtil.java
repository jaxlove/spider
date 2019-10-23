package com.spider.dazhong;

import com.spider.util.StringUtil;
import org.apache.commons.lang3.StringUtils;


public class DazhongUtil {

    public static String getPower(Object object){
        String powerClass = StringUtil.getString(object);
        if(StringUtils.isNotBlank(powerClass)){
            return powerClass.replace("sml-rank-stars sml-str","");
        }else {
            return null;
        }


    }

}
