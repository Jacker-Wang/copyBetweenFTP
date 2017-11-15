package com.jackerwang.cp.hdfs.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class CommandUtil {
    // 使用命令行选项
    public static CommandLine getCommandLine(String[] args) {
        Options options = new Options();
        options.addOption(new Option("fh", "fromHost", true, "mysql dataBaseHost"));
        options.addOption(new Option("fd", "fromDataBase", true, "mysql dataBase"));
        options.addOption(new Option("fu", "fromUser", true, "mysql userName"));
        options.addOption(new Option("fp", "fromPassWord", true, "mysql passWord"));
        options.addOption(new Option("ft", "fromTable", true, "mysql table"));

        options.addOption(new Option("th", "toHost", true, "oracle Host"));
        options.addOption(new Option("td", "toDataBase", true, " oracle Database"));
        options.addOption(new Option("tu", "toUserName", true, " oracle userName"));
        options.addOption(new Option("tp", "toPassWord", true, " oracle passWord"));

        // create the command line parser
        @SuppressWarnings("deprecation")
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cmd;
    }

    // 读取配置文件参数
    public static String getField(String field) {
        String result = null;
        Properties properties = new Properties();
        InputStream inputStream = CommandUtil.class.getResourceAsStream("/properties");
        try {
            properties.load(inputStream);
            result = properties.getProperty(field);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
