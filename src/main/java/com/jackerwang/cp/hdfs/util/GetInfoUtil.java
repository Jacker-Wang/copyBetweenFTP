package com.jackerwang.cp.hdfs.util;

import com.jackerwang.cp.hdfs.info.FtpInfo;

public class GetInfoUtil {

    public static FtpInfo getBuildInfo() {
        FtpInfo buildInfo = new FtpInfo();

        String buildHost = CommandUtil.getField("BuildHost");
        String Port = CommandUtil.getField("BuildPort");
        String User = CommandUtil.getField("BuildUser");
        String Passwd = CommandUtil.getField("BuildPasswd");

        buildInfo.setHost(buildHost);
        buildInfo.setPort(Port);
        buildInfo.setUser(User);
        buildInfo.setPasswd(Passwd);
        return buildInfo;
    }

    public static FtpInfo getDebugInfo() {
        FtpInfo debugInfo = new FtpInfo();

        String Host = CommandUtil.getField("DebugHost");
        String Port = CommandUtil.getField("DebugPort");
        String User = CommandUtil.getField("DebugUser");
        String Passwd = CommandUtil.getField("DebugPasswd");

        debugInfo.setHost(Host);
        debugInfo.setPort(Port);
        debugInfo.setUser(User);
        debugInfo.setPasswd(Passwd);
        return debugInfo;
    }
}
