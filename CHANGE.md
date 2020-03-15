# CrazyWalker的xxl-job修改和优化记录

**注意：**
- 修改后的xxl-job的基础环境为jdk1.8


## xxl-job-admin

## xxl-job-core (com.xxl.job.core)

### executor

#### XxlJobExecutor类

- 对initAdminBizList类中的嵌套if使用Optional类进行优化


### thread

#### JobLogFileCleanThread

- 对

## xxl-job-executor-samples