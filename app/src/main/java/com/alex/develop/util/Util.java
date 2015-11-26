package com.alex.develop.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DecimalFormat;
/**
 * Created by Administrator on 2015-11-23.
 */
public class Util {
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("^\\d+$|-\\d+$"+"|\\d+\\.\\d+$|-\\d+\\.\\d+$");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    public static DecimalFormat DecimalFormat = new DecimalFormat("######0.00");
}
