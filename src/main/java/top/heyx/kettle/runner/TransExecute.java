package top.heyx.kettle.runner;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.ProgressNullMonitorListener;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.repository.AbstractRepository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import top.heyx.kettle.listener.LogListenerImpl;
import top.heyx.kettle.utils.CollectionUtil;
import top.heyx.kettle.utils.FileUtil;
import top.heyx.kettle.utils.JsonUtil;
import top.heyx.kettle.utils.KettleLogUtil;

import java.util.Map;

/**
 * kettle的ktr脚本执行器
 *
 * @author lyf
 */
@Slf4j
public class TransExecute {

    /**
     * ktr执行单元，所有的ktr执行都调用该方法
     *
     * @param tm ktr元数据
     * @param params ktr需要的命名参数
     * @param args 命令行参数
     */
    private static ReturnT<String> executeTrans(TransMeta tm, Map<String, String> params, String[] args) throws KettleException {
        ReturnT<String> returnT;
        // 通过元数据获取ktr的实例
        Trans trans = new Trans(tm);
        // 传入ktr需要的变量
        if (CollectionUtil.isNotEmpty(params)) {
			log.info("传入kettle的参数：{}", JsonUtil.toJsonString(params));
            for (Map.Entry<String, String> entry : params.entrySet()) {
                //设置变量
                trans.setVariable(entry.getKey(),entry.getValue());
               // trans.setParameterValue(entry.getKey(), entry.getValue());
            }
        }

        // 开始执行ktr，该方法还可以传入命令行参数args
        trans.execute(args);
        //添加自定义日志监听
        LogListenerImpl logListener = new LogListenerImpl();
        logListener.setChannelId(trans.getLogChannelId());
        logListener.setIncludeGeneral(true);
        KettleLogStore.getAppender().addLoggingEventListener(logListener);
        // 线程等待，直到ktr执行完成
        trans.waitUntilFinished();
        // 执行完成后获取日志

		// 判断执行过程中是否有错误, 有错误就抛出错误日志
		if (trans.getErrors() > 0) {
            returnT = new ReturnT<String>(IJobHandler.FAIL.getCode(), "作业" +trans.getName() + " 运行失败\r\n");
            return returnT;
        }
        returnT=ReturnT.SUCCESS;
        // 没有错误就返回正常执行日志
        return returnT;
    }

    /**
     * 运行单个ktr
     *
     * @param fullPathName ktr全路径名
     * @param params ktr需要的命名参数
     * @param logLevel 日志级别
     */
    public static ReturnT<String> run(String fullPathName, Map<String, String> params, LogLevel logLevel) throws KettleException {
        // 通过ktr全路径名获取ktr元数据
        TransMeta tm = new TransMeta(FileUtil.replaceSeparator(fullPathName));
        // 设置日志级别
        if (logLevel != null) {
            tm.setLogLevel(logLevel);
        }
        // 开始执行ktr
        return executeTrans(tm, params, null);
    }

    /**
     * 运行资源库中的ktr
     *
     * @param rep 资源库对象
     * @param transPath ktr所在路径
     * @param transName ktr名称
     * @param versionLabel 版本号，传入null表示执行最新的ktr
     * @param params ktr需要的命名参数
     * @param logLevel 日志级别
     */
    public static ReturnT<String> run(AbstractRepository rep, String transPath, String transName, String versionLabel, Map<String, String> params, LogLevel logLevel) throws KettleException {
        // 根据相对目录地址获取ktr所在目录信息
        RepositoryDirectoryInterface rdi = rep.loadRepositoryDirectoryTree().findDirectory(FileUtil.getParentPath(transPath));
        // 在指定资源库的目录下找到要执行的转换
        TransMeta tm = rep.loadTransformation(FileUtil.getFileName(transName), rdi, new ProgressNullMonitorListener(), true, versionLabel);
        // 设置日志级别
        if (logLevel != null) {
            tm.setLogLevel(logLevel);
        }
        // 开始执行ktr
        return executeTrans(tm, params, null);
    }
}
