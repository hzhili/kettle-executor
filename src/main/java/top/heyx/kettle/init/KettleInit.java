package top.heyx.kettle.init;

import lombok.Setter;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.logging.KettleLogStore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import top.heyx.kettle.utils.FileUtil;

import java.io.File;

/**
 * kettle初始化
 *
 * @author lyf
 */
//@Component
public class KettleInit implements InitializingBean {
    @Setter
    private String kettleHome;
    @Setter
    private String kettlePluginPackages;
    @Setter
    private String jndiFilePath;
    @Override
    public void afterPropertiesSet() throws Exception {
        // 自定义环境初始化
        environmentInit();
        // kettle环境初始化
        File file = new File(jndiFilePath);
        Const.JNDI_DIRECTORY=file.getCanonicalPath();
        KettleEnvironment.init(true);
        //Repository.init();
        KettleLogStore.init();
    }

    private void environmentInit() {

        System.getProperties().put("KETTLE_HOME",kettleHome);
        if (StringUtils.hasText(kettlePluginPackages)) {
            System.getProperties().put("KETTLE_PLUGIN_PACKAGES", FileUtil.replaceSeparator(kettlePluginPackages));
        }
    }
}
