package com.jackerwang.cp.hdfs.tools;

import java.util.ArrayList;
import java.util.List;

import com.jackerwang.cp.hdfs.info.FtpInfo;
import com.jackerwang.cp.hdfs.util.CommandUtil;
import com.jackerwang.cp.hdfs.util.GetBuildDirUtil;
import com.jackerwang.cp.hdfs.util.GetInfoUtil;

public class CpFromBuildToDebug {
    static FtpInfo buildInfo;
    static FtpInfo debugInfo;

    // 远程目录
    static List<String> buildDirs = new ArrayList<>();
    // static Logger logger = LoggerFactory.getLogger(CopyData.class);
    static String separator = CommandUtil.getField("LocalEnvFileSeparator");
    static {
        buildInfo = GetInfoUtil.getBuildInfo();
        debugInfo = GetInfoUtil.getDebugInfo();
        buildDirs = GetBuildDirUtil.getDirs();
    }

    // 从生产环境复制数据到联调测试环境
    public Boolean copyData() {
        Boolean flag = false;
        LoadFromBuild loadFromBuild = new LoadFromBuild();
        loadFromBuild.connect(buildInfo);

        for (String remoteDirPath : buildDirs) {
            if (loadFromBuild.loadDir(remoteDirPath)) {
                System.out.println("目录 " + remoteDirPath + " 中的所有文件下载完成");
            }
        }
        loadFromBuild.closeConnect();
        System.out.println("********************************************************************************");
        System.out.println("*********************************开始上传文件*************************************");

        UpToDebug upToDebug = new UpToDebug();
        if (upToDebug.uploadManyFile("")) {
            flag = true;
            System.out.println("上传目录成功");
        } else {
            System.out.println("上传目录失败");
        }
        upToDebug.closeConnect();
        return flag;
    }

}
