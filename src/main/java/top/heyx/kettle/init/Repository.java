package top.heyx.kettle.init;

import com.xxl.job.core.log.XxlJobLogger;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;

/**
 * @AUTHOR HZL
 * @MAIL HZL031612@gmail.com
 * @DATE 2020/2/2
 */
public class Repository {
    private static Repository repository;
    // private static KettleDatabaseRepository kettleDatabaseRepository = new KettleDatabaseRepository();

    private Repository() {
    }

    ;

//    private KettleDatabaseRepository init(String dbType) throws KettleException {
//        KettleDatabaseRepository kettleDatabaseRepository = new KettleDatabaseRepository();
//        if (!kettleDatabaseRepository.isConnected()) {
//
//            DatabaseMeta databaseMeta = new DatabaseMeta("kettle", "mysql", "Native(JDBC)", "172.16.89.74", "kettle", "3306"
//                    , "root", "Xyh@3613571");
//            KettleDatabaseRepositoryMeta kettleDatabaseRepositoryMeta = new KettleDatabaseRepositoryMeta();
//            kettleDatabaseRepositoryMeta.setConnection(databaseMeta);
//            kettleDatabaseRepository.init(kettleDatabaseRepositoryMeta);
//            kettleDatabaseRepository.connect("admin", "admin");
//            if (kettleDatabaseRepository.isConnected()) {
//                XxlJobLogger.log("资源库连接成功");
//            } else {
//                throw new KettleException("资源库连接失败");
//            }
//        }
//        return kettleDatabaseRepository;
//    }

    public static Repository getInstance() {
        repository = new Repository();
        return repository;
    }

    public KettleDatabaseRepository jndiInit(String dbType, String dbName, String repName) throws KettleException {
        KettleDatabaseRepository kettleDatabaseRepository = new KettleDatabaseRepository();
        DatabaseMeta databaseMeta = new DatabaseMeta();
        databaseMeta.setName(repName);
        databaseMeta.setDBName(dbName);
        databaseMeta.setDatabaseType(dbType);
        databaseMeta.setAccessType(DatabaseMeta.TYPE_ACCESS_JNDI);

        KettleDatabaseRepositoryMeta kettleDatabaseRepositoryMeta = new KettleDatabaseRepositoryMeta();
        kettleDatabaseRepositoryMeta.setConnection(databaseMeta);

        kettleDatabaseRepository.init(kettleDatabaseRepositoryMeta);

        Database database = kettleDatabaseRepository.getDatabase();
        database.setLogLevel(LogLevel.DETAILED);
        kettleDatabaseRepository.setDatabase(database);

        kettleDatabaseRepository.connect("admin", "admin");
        if (kettleDatabaseRepository.isConnected()) {
            XxlJobLogger.log("资源库连接成功");
        } else {
            throw new KettleException("资源库连接失败");
        }

        return kettleDatabaseRepository;
    }
}
