package com.jackerwang.cp.hdfs.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;

import com.jackerwang.cp.hdfs.base.AbstractBaseOperateFtp;
import com.jackerwang.cp.hdfs.info.FtpInfo;
import com.jackerwang.cp.hdfs.util.CommandUtil;
import com.jackerwang.cp.hdfs.util.GetInfoUtil;

/**
 * Hello world!
 *
 */
public class UpToDebug extends AbstractBaseOperateFtp {
    static FtpInfo debugInfo;
    static String BaseDebugPath;

    public UpToDebug() {
        debugInfo = GetInfoUtil.getDebugInfo();
        init();
        connect(debugInfo);
        BaseDebugPath = CommandUtil.getField("BaseDebugPath");
    }

    /**
     * 上传单个文件，并重命名
     * 
     * @param localFile--本地文件路径
     * @param localRootFile--本地文件父文件夹路径
     * @param distFolder--新的文件名,可以命名为空""
     * @return true 上传成功，false 上传失败
     * @throws IOException
     */
    public boolean uploadFile(String localFilePath) {
        // 根据本地文件路径生成远程文件路径
        String remoteFilePath = BaseDebugPath + localFilePath;
        remoteFilePath = remoteFilePath.replace(separator, "/");

        localFilePath = BaseLocalPath + localFilePath;
        boolean flag = false;

        // 得到文件名
        String remoteFileName = remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);
        // 创建服务器远程目录结构，创建失败直接返回
        try {
            if (!CreateDirecroty(remoteFilePath)) {
                return false;
            }
            File localFile = new File(localFilePath);
            flag = uploadFile(localFile, remoteFileName) ? true : false;
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 上传文件
     * 
     * @param remoteFile
     *            远程文件路径,支持多级目录嵌套
     * @param localFile
     *            本地文件名称，绝对路径
     * 
     */
    public boolean uploadFile(File localFile, String remoteFileName) {
        boolean flag = false;
        try {
            FTPFile[] files = ftpClient.listFiles();
            for (FTPFile file : files) {
                System.out.println(file);
                String fileName = file.toString().split("\\s+")[8];
                if (fileName.equals(remoteFileName)) {
                    if (ftpClient.deleteFile(remoteFileName)) {
                        System.out.println("删除联调测试环境文件 " + remoteFileName + " 成功");
                    } else {
                        System.out.println("删除联调测试环境文件 " + remoteFileName + " 失败");
                    }
                }
            }
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }

        InputStream in = null;
        try {
            in = new FileInputStream(localFile);
            // String remote = new String(remoteFile.getBytes("GBK"),
            // "iso-8859-1");
            if (ftpClient.storeFile(remoteFileName, in)) {
                flag = true;
                // logger.debug(localFile.getAbsolutePath() + "上传文件成功！");
                System.out.println(localFile.getAbsolutePath() + "上传文件成功！");
            } else {
                // logger.debug(localFile.getAbsolutePath() + "上传文件失败！");
                System.out.println(localFile.getAbsolutePath() + "上传文件失败！");
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 上传文件夹内的所有文件
     * 
     * 
     * @param filename
     *            本地文件夹绝对路径
     * @param uploadpath
     *            上传到FTP的路径,形式为/或/dir1/dir2/../
     * @return true 上传成功，false 上传失败
     * @throws IOException
     */
    public Boolean uploadManyFile(String LocalDir) {
        boolean flag = true;
        // 根据本地文件路径生成远程文件路径
        String remoteDir = BaseDebugPath + LocalDir;
        remoteDir = remoteDir.replace(separator, "/");
        String AbsoluteLocalDir = BaseLocalPath + LocalDir;
        StringBuffer strBuf = new StringBuffer();
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            ftpClient.changeWorkingDirectory("/");
            File fileDir = new File(AbsoluteLocalDir);
            File fileList[] = fileDir.listFiles();
            if (fileList != null) {
                for (File upfile : fileList) {
                    String upfileName = upfile.toString();
                    upfileName = upfileName.substring(upfileName.lastIndexOf(separator) + 1);
                    if (upfile.isDirectory()) {
                        uploadManyFile(LocalDir + upfileName + separator);
                    } else {
                        System.out.println("");
                        System.out.println("***开始上传文件*** " + LocalDir + upfileName + " *************");
                        flag = uploadFile(LocalDir + upfileName);
                        ftpClient.changeWorkingDirectory("/");
                    }
                }
            }
        } catch (NullPointerException e) {
            flag = false;
            e.printStackTrace();
            // logger.debug("本地文件上传失败！找不到上传文件！", e);
            System.out.println("本地文件上传失败！找不到上传文件！");
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
            // logger.debug("本地文件上传失败！", e);
            System.out.println("本地文件上传失败！");
        }
        return flag;
    }

    /**
     * 递归创建远程服务器目录
     * 
     * @param remote
     *            远程服务器文件绝对路径
     * 
     * @return 目录创建是否成功
     * @throws IOException
     */
    public boolean CreateDirecroty(String remote) throws IOException {
        boolean success = true;
        String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
            int start = directory.startsWith("/") ? 1 : 0;
            int end = 0;
            end = directory.indexOf("/", start);
            while (true) {
                String subDirectory = remote.substring(start, end);
                if (!changeWorkingDirectory(subDirectory)) {
                    if (makeDirectory(subDirectory)) {
                        changeWorkingDirectory(subDirectory);
                    } else {
                        // logger.debug("创建目录[" + subDirectory + "]失败");
                        System.out.println("创建目录[" + subDirectory + "]失败");
                        success = false;
                        return success;
                    }
                }
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }

    /**
     * 在服务器上创建一个文件夹
     * 
     * @param dir
     *            文件夹名称，不能含有特殊字符，如 \ 、/ 、: 、* 、?、 "、 <、>...
     */
    public boolean makeDirectory(String dir) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
            if (flag) {
                // logger.debug("创建文件夹" + dir + " 成功！");
                System.out.println("创建文件夹" + dir + " 成功！");

            } else {
                // logger.debug("创建文件夹" + dir + " 失败！");
                System.out.println("创建文件夹" + dir + " 失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

}
