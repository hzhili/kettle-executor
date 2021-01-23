//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package top.heyx.kettle.handler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.annotation.XxlJob;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.heyx.kettle.config.KettleConfig;
import top.heyx.kettle.init.Repository;
import top.heyx.kettle.runner.JobRunner;
import top.heyx.kettle.runner.TransExecute;

@Component
public class ETLJobHandler {
    @Autowired
    KettleConfig kettleConfig;


    @XxlJob(value = "kettleJob",destroy = "garbageCollection")
    public ReturnT<String> jobExec(String param) throws Exception {
        Map<String, String> params = paramFormat(param);
        KettleDatabaseRepository dbRepository = getDbRepository();
        ReturnT<String> result = JobRunner.run(dbRepository, params.get("path"), params.get("name"), null, params, LogLevel.BASIC);
        dbRepository.disconnect();
        return result;
    }

    @XxlJob(value = "kettleTrans",destroy = "garbageCollection")
    public ReturnT<String> transExec(String param) throws Exception {
        Map<String, String> params = paramFormat(param);
        KettleDatabaseRepository dbRepository = getDbRepository();
        ReturnT<String> result = TransExecute.run(dbRepository, params.get("path"), params.get("name"), null, params, LogLevel.BASIC);
        dbRepository.disconnect();
        return result;
    }

    public KettleDatabaseRepository getDbRepository() throws KettleException {
        Repository repository = Repository.getInstance();
        return repository.jndiInit(kettleConfig.getDbType(), kettleConfig.getDbName(), kettleConfig.getRepName());
    }

    public Map<String, String> paramFormat(String param) throws IOException {
        param = StringUtils.replace(param, ";", "\r\n");
        Properties properties = load(param);
        return this.getJobInfo(properties);
    }

    public Map<String, String> getJobInfo(Map prop) {
        Map<String, String> params = new HashMap();
        prop.forEach((k, v) -> {
            if (StringUtils.equalsIgnoreCase(k.toString(), "path")) {
                params.put("path", v.toString());
                System.out.println("path:" + v.toString());
            } else if (StringUtils.equalsIgnoreCase(k.toString(), "name")) {
                params.put("name", v.toString());
                System.out.println("name:" + v.toString());
            } else {
                params.put(k.toString(), v.toString());
            }

        });
        return params;
    }

    public Properties load(String params) throws IOException {
        InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(params.getBytes()));
        Properties prop = new Properties();
        prop.load(reader);
        reader.close();
        return prop;
    }

    public void garbageCollection() {

        System.gc();
    }
}
