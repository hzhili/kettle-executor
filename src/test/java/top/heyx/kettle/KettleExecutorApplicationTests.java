package top.heyx.kettle;

import com.xxl.job.core.log.XxlJobLogger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

@SpringBootTest
class KettleExecutorApplicationTests {

    @Test
    void contextLoads() {
        System.out.print("换行" + System.getProperty("line.separator"));
        System.out.print("换行2:" + "\r\n");
    }

    @Test
    void readFile() throws Exception {
        File file = new File("D:\\usr\\local\\xxljob\\logs\\job\\周口.log");
        boolean first = true;
        boolean read = false;
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        while (!read) {

            try {
                long filePointer = randomAccessFile.getFilePointer();
                System.out.println(filePointer);
                if (!first) {
                    randomAccessFile.seek(filePointer + 1);
                }
                String line;
                while ((line = randomAccessFile.readLine()) != null) {
                    String msg = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                    System.out.println("正在读取日志文件:" + msg);
                }
                first = false;
                read = Thread.currentThread().isInterrupted();
                System.out.println(read);
            } catch (IOException e) {
                XxlJobLogger.log(e);
            }
            Thread.sleep(5000);
        }
        randomAccessFile.close();
    }

    @Test
    void read() {
        File file = new File("D:\\usr\\local\\xxljob\\logs\\job\\周口.log");
        boolean first = true;
        boolean read = false;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"))) {
                while (!read) {
                    String line;
                        while ((line=bufferedReader.readLine())!=null){
                            XxlJobLogger.log(line);
                        }
                    read = Thread.currentThread().isInterrupted();
                }
            }
        }catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
