package com.xxl.job.core.thread;

import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * job file clean thread
 *
 * @author xuxueli 2017-12-29 16:23:43
 */
public class JobLogFileCleanThread {
    private static Logger logger = LoggerFactory.getLogger(JobLogFileCleanThread.class);

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter  DATE_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static JobLogFileCleanThread instance = new JobLogFileCleanThread();
    /**
     * 获取类实例
     * @return 返回JobLogFileCleanThread实例
     */
    public static JobLogFileCleanThread getInstance(){
        return instance;
    }

    private Thread localThread;
    private volatile boolean toStop = false;

    /**
     * 启动日志文件清除线程，且设定线程为守护线程。检索日志根目录下所有文件，将以年月日命名的日志
     * 文件转换为日期并计算与当前日期的时间差，若时间差大于设定的日志保存时间，则清除对应的日志文
     * 件
     * @param logRetentionDays 日志保存时间
     */
    public void start(final long logRetentionDays){
        // 限制日志保存时间最小值，若保存时间小于三天则返回
        if (logRetentionDays < 3 ) {
            return;
        }

        localThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // clean log dir, over logRetentionDays
                        File[] childDirs = new File(XxlJobFileAppender.getLogPath()).listFiles();
                        if (childDirs!=null && childDirs.length>0) {
                            // today
                            LocalDate todayDate = LocalDate.now();
                            for (File childFile: childDirs) {
                                // valid
                                if (!childFile.isDirectory()) {
                                    continue;
                                }
                                if (childFile.getName().contains("-")) {
                                    continue;
                                }

                                LocalDate logFileCreateDate = LocalDate
                                        .parse(childFile.getName(), DATE_PATTERN_FORMATTER);
                                if (todayDate.isAfter(logFileCreateDate.plusDays(logRetentionDays))) {
                                    childFile.delete();
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    try {
                        // 每天执行一次
                        TimeUnit.DAYS.sleep(1);
                    } catch (InterruptedException e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-job, executor JobLogFileCleanThread thread destory.");

            }
        });
        localThread.setDaemon(true);
        localThread.setName("xxl-job, executor JobLogFileCleanThread");
        localThread.start();
    }

    public void toStop() {
        toStop = true;
        if (localThread == null) {
            return;
        }
        // interrupt and wait
        localThread.interrupt();
        try {
            localThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
