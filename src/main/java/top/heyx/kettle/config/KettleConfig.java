package top.heyx.kettle.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import top.heyx.kettle.init.KettleInit;
import top.heyx.kettle.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 配置在yml文件中的常量数据
 * @author lyf
 */

@Configuration
@Component
//@ConfigurationProperties(prefix = "kettle")
@Data
public class KettleConfig {

    /**
     * 日志文件输出路径
     */
    @Value("${logging.file.path}")
    private  String logFilePath;

    /**
     * kettle编码设置
     */
    @Value("${kettle.encoding}")
    private  Charset encoding;
    /**
     * jdbc.properties 文件路径
     */
    @Value("${kettle.jdbc-file-path}")
    private String jndiFilePath;

    /**
     * ktr或kjb文件保存路径，单个文件执行的时候需要保存ktr、kjb文件
     */
    //public static String uploadPath;

    /**
     * kettle所在路径，初始化会自动生成.kettle文件在该目录,kettle.properties,repositories.xml,shared.xml都在里面
     */
    @Value("${kettle.home}")
    private  String home;
    @Value("${kettle.repository.database.type}")
    private String dbType;
    @Value("${kettle.repository.database.name}")
    private String dbName;
    @Value("${kettle.repository.name}")
    private String repName;

    /**
     * kettle插件包所在路径 eg: D:\Development\kettle\8.3\data-integration\plugins
     */
    @Value("${kettle.plugin-packages-path}")
    private  String pluginPackagesPath;



    @Bean
    public KettleInit kettleInit() throws IOException {
        if ("".equals(jndiFilePath)||jndiFilePath==null){
            jndiFilePath=new File(getClass().getClassLoader().getResource("jdbc.properties").getFile()).getParentFile().getCanonicalPath();
        }
        KettleInit kettleInit = new KettleInit();
        kettleInit.setKettleHome(home);
        kettleInit.setJndiFilePath(FileUtil.getParentPath(jndiFilePath));
        kettleInit.setKettlePluginPackages(pluginPackagesPath);
        return kettleInit;
    }
//    @Bean
//    public KettleMeta kettleMeta(){
//        KettleMeta kettleMeta = new KettleMeta();
//        kettleMeta.setEncoding(encoding);
//        kettleMeta.setHome(home);
//        kettleMeta.setLogFilePath(logFilePath);
//        kettleMeta.setPluginPackagesPath(pluginPackagesPath);
//        return kettleMeta;
//    }
}
