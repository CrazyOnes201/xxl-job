package com.xxl.job.core.biz.impl;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * *****************************************
 * **        Author : CrazyWalker         **
 * *****************************************
 * *****************************************
 * ** @date: 周一,03/30 2020 11:31 下午 GMT+8 **
 * *****************************************
 * *****************************************
 * **     用途:        **
 * *****************************************
 */
@RestController
@RequestMapping("biz")
public class RestExecutorServerImpl implements ExecutorBiz {
    @Autowired
    private HttpServletRequest httpServletRequest;

    private static final String HEADER_TOKEN_KEY = "jobToken";
    private static final String FAILED_MESSAGE = "验证失败";
    private static final ExecutorBiz EXECUTOR_BIZ_IMPL = new ExecutorBizImpl();

    @GetMapping("beat")
    @Override
    public ReturnT<String> beat() {
        if (!validToken(httpServletRequest)) {
            return ReturnT.FAIL;
        }
        return EXECUTOR_BIZ_IMPL.beat();
    }

    @GetMapping("idleBeat")
    @Override
    public ReturnT<String> idleBeat(@RequestParam("jobId") int jobId) {
        if (!validToken(httpServletRequest)) {
            return ReturnT.FAIL;
        }
        return EXECUTOR_BIZ_IMPL.idleBeat(jobId);
    }

    @GetMapping("kill")
    @Override
    public ReturnT<String> kill(@RequestParam("jobId") int jobId) {
        if (!validToken(httpServletRequest)) {
            return ReturnT.FAIL;
        }
        return EXECUTOR_BIZ_IMPL.kill(jobId);
    }

    @GetMapping("log")
    @Override
    public ReturnT<LogResult> log(@RequestParam("logDateTim") long logDateTim, @RequestParam("logId") long logId,
                                  @RequestParam("fromLineNum") int fromLineNum) {
        if (!validToken(httpServletRequest)) {
            return new ReturnT<>(new LogResult(fromLineNum, 0, "readLog fail, logFile not found", true));
        }
        return EXECUTOR_BIZ_IMPL.log(logDateTim, logId, fromLineNum);
    }

    @PostMapping("run")
    @Override
    public ReturnT<String> run(@RequestBody TriggerParam triggerParam) {
        if (!validToken(httpServletRequest)) {
            return ReturnT.FAIL;
        }
        return EXECUTOR_BIZ_IMPL.run(triggerParam);
    }

    private boolean validToken(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader(HEADER_TOKEN_KEY);
        // TODO 验证超时并验证token一致性
        return true;
    }
}
