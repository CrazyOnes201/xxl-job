package com.xxl.job.admin.core.biz;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * *****************************************
 * **        Author : CrazyWalker         **
 * *****************************************
 * *****************************************
 * **@date: 周一,03/30 2020 12:00上午 GMT+8**
 * *****************************************
 * *****************************************
 * **     用途:        **
 * *****************************************
 */
public class RestExecutorBizImpl implements ExecutorBiz {

    private String address;

    private String accessToken;

    public RestExecutorBizImpl(String address, String accessToken) {
        this.address = address;
        this.accessToken = accessToken;
    }

    private static final String PATH_PREFIX = "/biz/";

    public ReturnT<String> beat() {
        RestTemplate restTemplate = new RestTemplate();
        ReturnT<String> returnT = restTemplate.getForObject(address + PATH_PREFIX + "beat", ReturnT.class);
        return returnT;
    }

    public ReturnT<String> idleBeat(int jobId) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> map = new HashMap<>();
        map.put("jobId", jobId);
        ReturnT<String> returnT = restTemplate.getForObject(address + PATH_PREFIX + "idleBeat?jobId={jobId}", ReturnT.class, map);
        return returnT;
    }

    /**
     * kill
     * @param jobId
     * @return
     */
    public ReturnT<String> kill(int jobId) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> map = new HashMap<>();
        map.put("jobId", jobId);
        ReturnT<String> returnT = restTemplate.getForObject(address + PATH_PREFIX + "kill?jobId={jobId}", ReturnT.class, map);
        return returnT;
    }

    /**
     * log
     * @param logDateTim
     * @param logId
     * @param fromLineNum
     * @return
     */
    public ReturnT<LogResult> log(long logDateTim, long logId, int fromLineNum) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> map = new HashMap<>();
        map.put("logDateTime", logDateTim);
        map.put("logId", logId);
        map.put("fromLineNum", fromLineNum);
        ReturnT<LogResult> returnT = restTemplate.getForObject(address + PATH_PREFIX + "log?logDateTim={logDateTim}" +
                "&logId={logId}&fromLineNum={fromLineNum}", ReturnT.class, map);
        return returnT;
    }

    /**
     * run
     * @param triggerParam
     * @return
     */
    public ReturnT<String> run(TriggerParam triggerParam) {
        RestTemplate restTemplate = new RestTemplate();
        ReturnT<String> returnT = restTemplate.postForObject(address + PATH_PREFIX + "run", triggerParam, ReturnT.class);
        return returnT;
    }
}
