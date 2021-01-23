package top.heyx.kettle.runner;


import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;

import org.pentaho.di.core.ProgressNullMonitorListener;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;

import org.pentaho.di.core.logging.LogLevel;

import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobEntryResult;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.AbstractRepository;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import top.heyx.kettle.listener.LogListenerImpl;
import top.heyx.kettle.utils.CollectionUtil;
import top.heyx.kettle.utils.FileUtil;
import top.heyx.kettle.utils.JsonUtil;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Slf4j
public class JobRunner {
   // private static Job job;
    /**
     * kjb执行单元，所有的kjb执行都调用该方法
     *
     * @param rep kjb所在资源库, 单个文件执行时传入null
     * @param jm kjb元对象
     * @param params kjb需要的命名参数
     */
    private static ReturnT<String> executeJob(Repository rep, JobMeta jm, Map<String, String> params) throws KettleException {
        ReturnT<String> returnT;

        // 通过元数据获取kjb的实例
        Job job= new Job(rep, jm);
        // 开启进程守护
        job.setDaemon(true);
        // 传入kjb需要的变量
        if (CollectionUtil.isNotEmpty(params)) {
            //log.info();
            XxlJobLogger.log("传入kettle的参数：{}", JsonUtil.toJsonString(params));
            for (Map.Entry<String, String> entry : params.entrySet()) {
                job.setVariable(entry.getKey(),entry.getValue());
            }
        }
        // 开始执行kjb
        job.start();

        LogListenerImpl logListener = new LogListenerImpl();
        logListener.setChannelId(job.getLogChannelId());
        logListener.setIncludeGeneral(true);
        KettleLogStore.getAppender().addLoggingEventListener(logListener);
        // 线程等待，直到kjb执行完成
        job.waitUntilFinished();


        // 判断执行过程中是否有错误
        List<String> returnTS = checkErr(job);

        String msg = String.join(",\r\n", returnTS);
        if(!job.getResult().getResult()){
            String errMsg = KettleLogStore.getAppender().getBuffer(job.getLogChannelId(), false).toString();
            returnT = new ReturnT<>(IJobHandler.FAIL.getCode(), "作业" +job.getJobname() + " 运行失败\r\n"+errMsg);
            return returnT;
        }
        job.setFinished(true);
        job.eraseParameters();
        returnT = new ReturnT<>(IJobHandler.SUCCESS.getCode(),msg);
        return returnT;
    }

    private static List<String> checkErr(Job job){
        List<JobEntryResult> jobEntryResults = job.getJobEntryResults();
        List<String> returnTS = new ArrayList<>();
        jobEntryResults.forEach(jobEntryResult -> {
            Result result = jobEntryResult.getResult();
            if (result.getNrErrors()>0){
                returnTS.add(jobEntryResult.getJobEntryName()+"运行失败");
            }
        });
        return returnTS;
    }

    /**
     * 运行单个kjb
     *
     * @param fullPathName kjb全路径名
     * @param params kjb需要的命名参数
     * @param logLevel 日志级别
     */
    public static ReturnT<String> run(String fullPathName, Map<String, String> params, LogLevel logLevel) throws KettleException {
        // 通过ktr全路径名获取ktr元数据
        JobMeta jm = new JobMeta(FileUtil.replaceSeparator(fullPathName), null);
        // 设置日志级别
        if (logLevel != null) {
            jm.setLogLevel(logLevel);
        }
        // 开始执行kjb
        return executeJob(null, jm, params);
    }

    /**
     * 运行资源库中的kjb
     * @param rep 资源库对象
     * @param jobPath kjb所在路径
     * @param jobName kjb名称
     * @param versionLabel 版本号，传入null表示执行最新的kjb
     * @param params kjb需要的命名参数
     * @param logLevel 日志级别
     */
    public static ReturnT<String> run(AbstractRepository rep, String jobPath, String jobName, String versionLabel, Map<String, String> params, LogLevel logLevel) throws KettleException {
        // 根据相对目录地址获取ktr所在目录信息
        RepositoryDirectoryInterface rdi = rep.loadRepositoryDirectoryTree().findDirectory(FileUtil.getParentPath(jobPath));
        // 在指定资源库的目录下找到要执行的转换
        JobMeta jm = rep.loadJob(FileUtil.getFileName(jobName), rdi, new ProgressNullMonitorListener(), versionLabel);
        // 设置日志级别
        if (logLevel != null) {
            jm.setLogLevel(logLevel);
        }

        // 开始执行kjb
        return executeJob(rep, jm, params);
    }

    /**
     * 运行资源库中的kjb
     * @param rep 资源库对象
     * @param jobDirectoryID kjb所在路径的ID
     * @param jobName kjb名字
     * @param versionLabel 版本号，传入null表示执行最新的kjb
     * @param params 参数
     * @param logLevel 日志级别
     * @return
     * @throws KettleException
     */
    public static ReturnT<String> run(AbstractRepository rep, ObjectId jobDirectoryID, String jobName, String versionLabel, Map<String, String> params, LogLevel logLevel) throws KettleException {
        // 根据相对目录地址获取ktr所在目录信息
        RepositoryDirectoryInterface rdi = rep.loadRepositoryDirectoryTree().findDirectory(jobDirectoryID);
        // 在指定资源库的目录下找到要执行的转换
        JobMeta jm = rep.loadJob(FileUtil.getFileName(jobName), rdi, new ProgressNullMonitorListener(), versionLabel);
        // 设置日志级别
        if (logLevel != null) {
            jm.setLogLevel(logLevel);
        }
        // 开始执行kjb
        return executeJob(rep, jm, params);
    }


}
