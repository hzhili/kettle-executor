package top.heyx.kettle.runner;

import com.xxl.job.core.log.XxlJobLogger;
import jdk.nashorn.internal.scripts.JO;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.ProgressNullMonitorListener;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.AbstractRepository;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.www.SlaveServerJobStatus;
import sun.misc.JavaObjectInputStreamAccess;
import top.heyx.kettle.utils.CollectionUtil;
import top.heyx.kettle.utils.FileUtil;
import top.heyx.kettle.utils.JsonUtil;

import java.util.Map;

/**
 * @AUTHOR HZL
 * @MAIL HZL031612@gmail.com
 * @DATE 2020/8/19
 */
public class ClusterJobExecute {
    /**
     * 远程执行任务
     * @param remoteSlaveServer 远程集群
     * @param jobName 作业名称
     * @param jobMeta
     * @param rep
     * @param params
     */
    private static void executeJobRemote(SlaveServer remoteSlaveServer,String jobName, JobMeta jobMeta, Repository rep, Map<String, String> params) {
        JobExecutionConfiguration jobExecutionConfiguration = new JobExecutionConfiguration();

        jobExecutionConfiguration.setRemoteServer(remoteSlaveServer);
        jobExecutionConfiguration.setRepository(rep);
        // 传入kjb需要的变量
        if (CollectionUtil.isNotEmpty(params)) {
            //log.info();
            XxlJobLogger.log("传入kettle的参数：{}", JsonUtil.toJsonString(params));
            jobExecutionConfiguration.setVariables(params);
        }
        Result result = new Result();
        try {
            String lastCarteObjectId = Job.sendToSlaveServer(jobMeta, jobExecutionConfiguration, rep, null);
            SlaveServerJobStatus jobStatus = remoteSlaveServer.getJobStatus(jobName, lastCarteObjectId, 0);
            if (jobStatus.getResult() != null) {
                result=jobStatus.getResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 停止远程执行的任务
     *
     * @param Transname
     * @param carteObjectid
     */

    public void stopRemoteJob(String Transname, String carteObjectid, SlaveServer remoteSlaveServer) {
        try {
            remoteSlaveServer.stopJob(Transname, carteObjectid);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取远程作业状态
     * @param remoteSlaveServer
     * @param jobMeta
     * @param lastCarteObjectId
     * @return
     */
    public String getRemoteJobStatus(SlaveServer remoteSlaveServer, JobMeta jobMeta, String lastCarteObjectId) {
        SlaveServerJobStatus jobStatus = null;

        try {
            jobStatus = remoteSlaveServer.getJobStatus(jobMeta.getName(), lastCarteObjectId, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jobStatus.toString();
    }

    /**
     * 执行任务
     * @param rep
     * @param slaveServer
     * @param jobPath
     * @param jobName
     * @param versionLabel
     * @param params
     * @param logLevel
     */
    public void run(AbstractRepository rep,SlaveServer slaveServer, String jobPath, String jobName, String
            versionLabel, Map<String, String> params, LogLevel logLevel) {
        // 根据相对目录地址获取ktr所在目录信息
        RepositoryDirectoryInterface rdi = null;
        try {
            rdi = rep.loadRepositoryDirectoryTree().findDirectory(FileUtil.getParentPath(jobPath));
            // 在指定资源库的目录下找到要执行的转换
            JobMeta jm = rep.loadJob(FileUtil.getFileName(jobName), rdi, new ProgressNullMonitorListener(), versionLabel);
            jm.setLogLevel(logLevel);
            executeJobRemote(slaveServer,jobName,jm,rep,params);
        } catch (KettleException e) {
            e.printStackTrace();
        }

    }

}
