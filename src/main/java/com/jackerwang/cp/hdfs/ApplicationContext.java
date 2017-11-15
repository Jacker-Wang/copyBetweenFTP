package com.jackerwang.cp.hdfs;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.jackerwang.cp.hdfs.tools.CpFromBuildToDebug;

public class ApplicationContext {

    public static void main(String[] args) throws IOException {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.println("@@@@@@@@@@@@@@@@ 日期 " + dateFormatter.format(new Date()) + " 日志@@@@@@@@@@@@@@@@@@@@");
                CpFromBuildToDebug tool = new CpFromBuildToDebug();
                tool.copyData();
            }
        };
        Timer timer = new Timer();
        Date startDate;
        try {
            startDate = dateFormatter.parse("2017/11/15 22:25:00");
            timer.scheduleAtFixedRate(timerTask, startDate, 24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
