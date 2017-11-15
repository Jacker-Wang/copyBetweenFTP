package com.jackerwang.cp.hdfs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GetBuildDirUtil {

    public static List<String> getDirs() {

        List<String> dirs = new ArrayList<String>();

        BufferedReader reader = null;
        try {
            InputStream inputStream = CommandUtil.class.getResourceAsStream("/build-dirPath");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                dirs.add(tempString.trim());
                line++;
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    return null;
                }
            }
            return dirs;
        }
    }
}
