package com.jackerwang.cp.hdfs.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPFile;

import com.jackerwang.cp.hdfs.base.AbstractBaseOperateFtp;
import com.jackerwang.cp.hdfs.info.FtpInfo;
import com.jackerwang.cp.hdfs.util.CommandUtil;
import com.jackerwang.cp.hdfs.util.GetInfoUtil;

/**
 * Hello world!
 *
 */
/**
 * @author Pin-Wang
 *
 */
public class LoadFromBuild extends AbstractBaseOperateFtp {
    static FtpInfo buildInfo;
    static String BaseBuildPath;

    public LoadFromBuild() {
        buildInfo = GetInfoUtil.getBuildInfo();
        BaseBuildPath = CommandUtil.getField("BaseBuildPath");
        init();
        connect(buildInfo);
    }

    /**
     * 下载文件
     * 
     * @param remoteFileName
     *            --服务器上的文件名
     * @param localFileName--本地文件名
     * @return true 下载成功，false 下载失败
     * @throws IOException
     */
    public boolean loadFile(String remoteFileName) throws IOException {
        boolean flag = false;
        // 根据相应生产环境文件目录生成本地文件目录
        String localFileName = BaseLocalPath;
        localFileName = localFileName + remoteFileName.replaceAll("/", separator + separator);
        // 删除本地文件
        // 创建本地文件父目录
        File ParentDir = new File(localFileName).getParentFile();
        if (OperateLocalFile.deleteFile(localFileName) && OperateLocalFile.createDir(ParentDir)) {
            System.out.println("成功创建本地文件" + localFileName + "父目录");
            // 下载文件
            BufferedOutputStream buffOut = null;
            try {
                buffOut = new BufferedOutputStream(new FileOutputStream(localFileName));
                flag = ftpClient.retrieveFile(remoteFileName, buffOut);
            } catch (Exception e) {
                e.printStackTrace();
                // logger.debug("本地文件下载失败！", e);
                System.out.println("本地文件下载失败！");
                flag = false;
            } finally {
                try {
                    if (buffOut != null)
                        buffOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            return false;
        }
        if (flag) {
            System.out.println("成功下载文件 " + BaseBuildPath + remoteFileName + "-->>" + localFileName + "\n");
        }
        return flag;
    }

    // 下载目录dir中的所有目录的文件
    public Boolean loadDir(String dir) {
        System.out.println("-------------------------检查目录 " + dir + " ----------------------------------");
        Boolean flag = false;
        FTPFile[] subDirs = listRemoteDirs(dir);
        if (null == subDirs || subDirs.length == 0) {
            String[] files = listRemoteFiles(dir);
            if (files != null && files.length != 0) {

                for (int i = 0; i < files.length && i < 5; i++) {
                    String file = dir + files[i];
                    System.out.println("准备下载文件 " + file);
                    try {
                        flag = loadFile(file) ? true : false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            for (FTPFile subDir : subDirs) {
                String fileName = subDir.toString().split("\\s+")[8];
                loadDir(dir + fileName + "/");
            }
        }
        return flag;
    }

}
