package com.alex.develop.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by alex on 15-5-24.
 * 读取文件
 */
public class FileHelper {

    public static void init(Context context) {
        FileHelper.context = context;
    }

    /**
     * 从Assets中读取文件为字符串
     * @param fileName
     */
    public static String getStringFromAssets(String fileName) {
        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();
        try {
            inputStream = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;
            while (null != (line=reader.readLine())) {
                builder.append(line);
                Log.d("Print", line);
                builder.append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(null != inputStream) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

    private FileHelper(){}

    private static Context context;
}
