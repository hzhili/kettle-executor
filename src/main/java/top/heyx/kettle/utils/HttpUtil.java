package top.heyx.kettle.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * http相关工具类
 * @author zhangxin
 *
 */
@Slf4j
public class HttpUtil {

    @Value("${drgsGroupUrl}")
    private String drgsGroupUrl;

    /**
     * 用apachehttp发送post请求
     * @param path 请求地址，可以带参数，如带参数，则data传空
     * @param data 参数
     * @return 接受到的返回值
     * @throws IOException
     */
    public static String sendHttpForApache(String path, String data) throws IOException{
        PostMethod postMethod = getPostMethod(path, data);
        if(postMethod!=null) {
            return postMethod.getResponseBodyAsString();
        }else {
            return null;
        }
    }

//    public static List<Map<String, Object>> main(PacketReaderPo packetReaderPo) {
//        try {
//            String json = new Gson().toJson(packetReaderPo);
//            String result= sendHttpForApache("http://127.0.0.1:3001/comp" + URLEncoder.encode("_", "utf-8") + "drg", json);
//            result = "[" + result + "]" ;
//            // 使用Gson转换
//
//            Gson gson = new Gson();
//
//            List<Map<String,Object>> list = gson.fromJson(result, new TypeToken<List<Map<String, Object>>>() {}.getType());
//            return list;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    /**
     * 用apachehttp发送post请求
     * @param path 请求地址，可以带参数，如带参数，则data传空
     * @param data 参数
     * @return 接受到的返回值
     * @throws IOException
     */
    public static InputStream sendHttpForApacheAsStream(String path, String data) throws IOException{
        PostMethod postMethod = getPostMethod(path, data);
        if(postMethod!=null) {
            return postMethod.getResponseBodyAsStream();
        }else {
            return null;
        }
    }

    private static PostMethod getPostMethod(String path, String data) throws IOException {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(60000); // //设置连接超时
        client.getHttpConnectionManager().getParams().setSoTimeout(180000); // //设置读取数据超时
        client.getParams().setContentCharset("UTF-8");
        PostMethod postMethod = new PostMethod(path);
        String cookie="XXL_JOB_LOGIN_IDENTITY=7b226964223a312c22757365726e616d65223a2261646d696e222c2270617373776f7264223a223732383564316230393263316661323635663934316335346637643735393864222c22726f6c65223a312c227065726d697373696f6e223a6e756c6c7d";

        postMethod.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        postMethod.setRequestHeader("Cookie",cookie);;
        StringRequestEntity requestEntity = new StringRequestEntity(data, "text/json", "UTF-8");
        postMethod.setRequestEntity(requestEntity);
        int status = client.executeMethod(postMethod);
        if (status == HttpStatus.SC_OK) {
            return postMethod;
        }else {
            log.error("调用服务器错误，状态码："+status);
            log.error("地址："+path);
            log.error("参数信息："+data);
            return null;
        }
    }
    
    /**
     * 用javahttp发送post请求
     * @param path
     * @param data
     * @return
     * @throws IOException
     */
    public static String sendHttp(String path, String data) throws IOException{
        URL url = new URL(path);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();  
        httpURLConnection.setConnectTimeout(60000);  
        httpURLConnection.setDoInput(true); //从服务器获取数据  
        httpURLConnection.setDoOutput(true); //向服务器写入数据
        httpURLConnection.setRequestMethod("POST");
        String cookie="XXL_JOB_LOGIN_IDENTITY=7b226964223a312c22757365726e616d65223a2261646d696e222c2270617373776f7264223a223732383564316230393263316661323635663934316335346637643735393864222c22726f6c65223a312c227065726d697373696f6e223a6e756c6c7d";

        byte[] mydata = data.getBytes("UTF-8"); //获得上传信息的字节大小及长度
        httpURLConnection.setRequestProperty("Content-Type","text/json"); //设置请求体的类型
        httpURLConnection.setRequestProperty("Content-Lenth",String.valueOf(mydata.length));
        httpURLConnection.setRequestProperty("Cookie",cookie);
 
        OutputStream outputStream = (OutputStream) httpURLConnection.getOutputStream(); //获得输出流，向服务器输出数据
        outputStream.write(mydata);
        
        int responseCode = httpURLConnection.getResponseCode(); //获得服务器响应的结果和状态码
        if (responseCode == 200) {
        	String message = changeInputStream((InputStream) httpURLConnection.getInputStream(), "UTF-8");
            return message; //获得输入流，从服务器端获得数据
        }else {
            log.error("调用服务器错误，状态码："+responseCode);
            log.error("地址："+path);
            log.error("参数信息："+data);
        	return null;
        }
    }
    
    /**
     * 用javahttp发送post请求
     * @param path
     * @param data
     * @return
     * @throws IOException
     */
    public static String sendHttpXml(String path, String data) throws IOException{
        URL url = new URL(path);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();  
        httpURLConnection.setConnectTimeout(60000);  
        httpURLConnection.setDoInput(true); //从服务器获取数据  
        httpURLConnection.setDoOutput(true); //向服务器写入数据
        httpURLConnection.setRequestMethod("POST");
        
        byte[] mydata = data.getBytes("UTF-8"); //获得上传信息的字节大小及长度
        httpURLConnection.setRequestProperty("Content-Type","text/xml"); //设置请求体的类型
        httpURLConnection.setRequestProperty("Content-Lenth",String.valueOf(mydata.length));
 
        OutputStream outputStream = (OutputStream) httpURLConnection.getOutputStream(); //获得输出流，向服务器输出数据
        outputStream.write(mydata);
        
        int responseCode = httpURLConnection.getResponseCode(); //获得服务器响应的结果和状态码
        if (responseCode == 200) {
        	String message = changeInputStream((InputStream) httpURLConnection.getInputStream(), "UTF-8");
            return message; //获得输入流，从服务器端获得数据
        }else {
            log.error("调用服务器错误，状态码："+responseCode);
            log.error("地址："+path);
            log.error("参数信息："+data);
        	return null;
        }
    }
    
    /**
     * 把从输入流InputStream按指定编码格式encode变成字符串String 
     * @param inputStream
     * @param encode
     * @return
     * @throws IOException 
     */
    private static String changeInputStream(InputStream inputStream, String encode) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];  
        int len = 0;  
        String result = "";  
        if (inputStream != null) {
        	while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
            result = new String(byteArrayOutputStream.toByteArray(), encode);
        }
        return result;  
    }
	
	/**
	 * 获取IP
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
	    return ip;
	}
	
	/**
	 * 获取key
	 * @param param 必须是按照ASCII码从小到大排序好的
	 * @param request
	 * @return
	 */
	public static String getKey(List<String> param, HttpServletRequest request) {
		if(param==null||param.size()<1) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for(String str : param) {
            sb.append(str+"&");
        }
		sb.append("key="+request.getAttribute("KEY"));
		return parseStrToMd5U32(sb.toString());
	}
	
	/**
	 * 生成32位大写MD5
	 * @param str
	 * @return
	 */
	public static String parseStrToMd5U32(String str){  
        String reStr = null;  
        try {  
            MessageDigest md5 = MessageDigest.getInstance("MD5");  
            byte[] bytes = md5.digest(str.getBytes());  
            StringBuffer stringBuffer = new StringBuffer();  
            for (byte b : bytes){  
                int bt = b&0xff;  
                if (bt < 16){  
                    stringBuffer.append(0);  
                }   
                stringBuffer.append(Integer.toHexString(bt));  
            }  
            reStr = stringBuffer.toString();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        return reStr.toUpperCase();  
    }

    /**
     * 获取request中下一页的信息
     * @param request 页面request对象
     * @return 如果没有下一页信息，则返回1
     */
    public static int getNextPage(HttpServletRequest request) {
        String nextpage = request.getParameter("nextpage");
        if (StringUtils.isEmpty(nextpage)) {
            nextpage = "1";
        }
        return Integer.valueOf(nextpage);
    }

}
