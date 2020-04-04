package com.xxl.job.core.executor;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.client.AdminBizClient;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.thread.ExecutorRegistryThread;
import com.xxl.job.core.thread.JobLogFileCleanThread;
import com.xxl.job.core.thread.JobThread;
import com.xxl.job.core.thread.TriggerCallbackThread;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.serialize.impl.HessianSerializer;
import com.xxl.rpc.util.IpUtil;
import com.xxl.rpc.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobExecutor  {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);

    // ---------------------- param ----------------------
    private String[] adminAddresses;
    private String appName;
    private String ip;
    private int port;
    private String accessToken;
    private String logPath;
    private int logRetentionDays;

    private static final String ADDRESSES_SPLIT_FLAG = ",";

    public void setAdminAddresses(String[] adminAddresses) {
        this.adminAddresses = adminAddresses;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }


    // ---------------------- start + stop ----------------------
    public void start() throws Exception {
        // init logpath
        XxlJobFileAppender.initLogPath(logPath);
        // init invoker, admin-client
        initAdminBizList(adminAddresses, accessToken);
        // init JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().start(logRetentionDays);
        // init TriggerCallbackThread
        TriggerCallbackThread.getInstance().start();
        // init executor-server
        port = port > 0 ? port : NetUtil.findAvailablePort(9999);
        ip = (ip != null && ip.trim().length() > 0) ? ip : IpUtil.getIp();
        initRpcProvider(ip, port, appName, accessToken);
    }
    public void destroy(){
        // destory executor-server
        removeProvider();
        // destory jobThreadRepository
        if (jobThreadRepository.size() > 0) {
            for (Map.Entry<Integer, JobThread> item: jobThreadRepository.entrySet()) {
                removeJobThread(item.getKey(), "web container destroy and kill the job.");
            }
            jobThreadRepository.clear();
        }
        jobHandlerRepository.clear();


        // destory JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().toStop();

        // destory TriggerCallbackThread
        TriggerCallbackThread.getInstance().toStop();

    }


    // ---------------------- admin-client (rpc invoker) ----------------------
    private static List<AdminBiz> adminBizList;
    private static Serializer serializer = new HessianSerializer();

    /**
     * 初始化调度中心列表。对调度中心地址字符串前后去除空格后按照ADDRESSES_SPLIT_FLAG进行分割，然后
     * 逐个创建由调度中心地址和token组成的AdminBizClient类对象，并添加到adminBizList数组中
     * @param adminAddresses 调度中心地址字符串，若多个地址由ADDRESSES_SPLIT_FLAG分隔
     * @param accessToken 连接token，可不设定，用于连接调度中心时使用
     * @throws Exception (CrazyWalker:好像没有什么异常可以抛出)
     */
    private void initAdminBizList(String[] adminAddresses, String accessToken) throws Exception {
        if (adminAddresses == null) {
            return;
        }
        for (String address: adminAddresses) {
            if (address!=null && address.trim().length()>0) {
                AdminBiz adminBiz = new AdminBizClient(address.trim(), accessToken);
                if (adminBizList == null) {
                    adminBizList = new ArrayList<>();
                }
                adminBizList.add(adminBiz);
            }
        }
    }

    /**
     * 获取调度中心客户端类列表
     * @return 返回调度中心客户端类列表
     */
    public static List<AdminBiz> getAdminBizList(){
        return adminBizList;
    }

    /**
     *
     * @return
     */
    public static Serializer getSerializer() {
        return serializer;
    }


    // ---------------------- executor-server (rpc provider) ----------------------

    private void initRpcProvider(String ip, int port, String appName, String accessToken) throws Exception {

        // init, provider factory
        String address = IpUtil.getIpPort(ip, port);
        Map<String, String> serviceRegistryParam = new HashMap<String, String>();
        serviceRegistryParam.put("appName", appName);
        serviceRegistryParam.put("address", address);
        registerProvider(serviceRegistryParam);

    }

    private void registerProvider(Map<String, String> param) {
        ExecutorRegistryThread.getInstance().start(param.get("appName"), param.get("address"));
    }

    private void removeProvider() {
        ExecutorRegistryThread.getInstance().toStop();
    }


    // ---------------------- job handler repository ----------------------
    private static ConcurrentMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<String, IJobHandler>();
    public static IJobHandler registJobHandler(String name, IJobHandler jobHandler){
        logger.info(">>>>>>>>>>> xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }
    public static IJobHandler loadJobHandler(String name){
        return jobHandlerRepository.get(name);
    }


    // ---------------------- job thread repository ----------------------
    private static ConcurrentMap<Integer, JobThread> jobThreadRepository = new ConcurrentHashMap<Integer, JobThread>();
    public static JobThread registJobThread(int jobId, IJobHandler handler, String removeOldReason){
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}", new Object[]{jobId, handler});

        JobThread oldJobThread = jobThreadRepository.put(jobId, newJobThread);	// putIfAbsent | oh my god, map's put method return the old value!!!
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }
    public static void removeJobThread(int jobId, String removeOldReason){
        JobThread oldJobThread = jobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }
    }
    public static JobThread loadJobThread(int jobId){
        JobThread jobThread = jobThreadRepository.get(jobId);
        return jobThread;
    }

}
