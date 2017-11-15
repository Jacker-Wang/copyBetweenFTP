package com.jackerwang.cp.hdfs.base;

import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.jackerwang.cp.hdfs.info.FtpInfo;
import com.jackerwang.cp.hdfs.util.CommandUtil;

public class AbstractBaseOperateFtp {

    protected FTPClient ftpClient = null;
    protected String BaseLocalPath = null;
    protected static String separator = null;
    static {
        separator = CommandUtil.getField("LocalEnvFileSeparator");
    }

    public void init() {
        // 得到当前时间
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currenTime = dataFormat.format(new Date());
        currenTime = currenTime.substring(0, currenTime.indexOf(" "));
        BaseLocalPath = CommandUtil.getField("BaseLocalPath") + currenTime + separator;
    }

    // 连接FTP服务
    public FTPClient connect(FtpInfo ftpInfo) {
        ftpClient = new FTPClient();
        String ip = ftpInfo.getHost();
        String port = ftpInfo.getPort();
        String user = ftpInfo.getUser();
        String password = ftpInfo.getPasswd();

        ftpClient = new FTPClient();
        int IntPort = Integer.valueOf(port);
        int reply;
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.connect(ip, IntPort);
            ftpClient.login(user, password);
            ftpClient.setDataTimeout(20 * 60 * 1000);
            reply = ftpClient.getReplyCode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                System.out.println("FTP 服务拒绝连接！");
            }
            if (reply == 230) {
                System.out.println("登录ftp服务器 " + ip + ":" + port + " 成功");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("登录ftp服务器 " + ip + " 失败,连接超时！");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("登录ftp服务器 " + ip + " 失败，FTP服务器无法打开！");
        }
        return ftpClient;
    }

    /**
     * 进入到FTP服务的某个目录下
     * 
     * @param directory
     */
    public boolean changeWorkingDirectory(String directory) {
        boolean flag = false;
        try {
            flag = ftpClient.changeWorkingDirectory(directory);
            if (flag) {
                // logger.debug("进入文件夹" + directory + " 成功！");
                System.out.println("进入文件夹" + directory + " 成功！");

            } else {
                // logger.debug("进入文件夹" + directory + " 失败！");
                System.out.println("进入文件夹" + directory + " 失败！");
            }
        } catch (IOException ioe) {
            flag = false;
            ioe.printStackTrace();
        }
        return flag;
    }

    /**
     * 列出服务器上目录dir下面的所有子目录
     * 
     * @param regStr
     *            --匹配的正则表达式
     */
    public FTPFile[] listRemoteDirs(String dir) {
        FTPFile[] dirs = null;
        try {
            dirs = ftpClient.listDirectories(dir);
            if (dirs == null || dirs.length == 0)
                // logger.debug("没有任何文件!");
                System.out.println("目录 " + dir + " 没有任何子目录!");
            else {
                System.out.println("FTP服务目录 " + dir + " 中的所有目录：");
                for (int i = 0; i < dirs.length; i++) {
                    System.out.println(dirs[i].toString());
                }
                System.out.println("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirs;
    }

    /**
     * 列出Ftp服务器上的所有文件和目录
     */
    public String[] listRemoteFiles(String dir) {
        String[] files = null;
        try {
            files = ftpClient.listNames(dir);
            if (files == null || files.length == 0) {
                System.out.println("FTP服务目录 " + dir + " 中没有任何文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        return files;
    }

    /**
     * 关闭连接
     */
    public void closeConnect() {
        try {
            if (ftpClient != null) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBaseLocalPath() {
        return BaseLocalPath;
    }

    public void setBaseLocalPath(String baseLocalPath) {
        BaseLocalPath = baseLocalPath;
    }

}
