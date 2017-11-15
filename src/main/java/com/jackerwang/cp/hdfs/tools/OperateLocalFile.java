package com.jackerwang.cp.hdfs.tools;

import java.io.File;
import java.io.IOException;

public class OperateLocalFile {

    // 删除本地文件
    public static Boolean deleteFile(String destFile) {
        File file = new File(destFile);
        if (file.isFile() && file.exists()) {
            System.out.println("文件 " + destFile + " 已经存在");
            if (file.delete()) {
                System.out.println("成功删除文件 " + destFile);
                return true;
            } else {
                System.out.println("删除文件 " + destFile + " 失败");
                return false;
            }
        } else {
            return true;
        }
    }

    // 递归创建本地文件父目录
    public static boolean createDir(File dir) throws IOException {
        System.out.println("dir " + dir);
        if (dir.exists()) {
            return true;
        } else {
            // 循环创建文件父目录
            if (!dir.getParentFile().exists()) { // 如果目标文件所在的目录不存在，则创建父目录
                createDir(dir.getParentFile());
            }
            return dir.mkdir();
        }
    }

}
