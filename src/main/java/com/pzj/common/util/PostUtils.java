package com.pzj.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostUtils {
    private static int                                RESP_SUCCESS                 = 200;
    private static String                             DEFAULT_CHARSET              = "utf-8";
    private static int                                maxTotalConnections          = 1024;                                    // 最大活动连接数
    private static int                                defaultMaxConnectionsPerHost = 1024;                                    // 最大连接数1000
    private static int                                defaultConnectionTimeout     = 5000;                                    // 连接超时时间(单位毫秒)
    private static int                                defaultSoTimeout             = 60000;                                   // 读取数据超时时间(单位毫秒)
    private static MultiThreadedHttpConnectionManager manager                      = new MultiThreadedHttpConnectionManager();

    private static Logger logger = LoggerFactory.getLogger(PostUtils.class);

    /**
     * data:username=kevin&password=12345
     * @param urlPath
     * @param data
     * @return
     * @throws IOException 
     */
    public static String qunarPost(String urlPath, String data) throws IOException {
        URL url = new URL(urlPath);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        out.write(data); // 向页面传递数据。post的关键所在！
        out.flush();
        out.close();
        // 一旦发送成功，用以下方法就可以得到服务器的回应：
        String sCurrentLine;
        String sTotalString;
        sCurrentLine = "";
        sTotalString = "";
        InputStream l_urlStream;
        l_urlStream = connection.getInputStream();
        // 传说中的三层包装阿！
        BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
        while ((sCurrentLine = l_reader.readLine()) != null) {
            sTotalString += sCurrentLine + "\r\n";

        }
        return sTotalString;
    }

    /**
    * 用传统的URI类进行请求
    * @param urlPath
    * @param data
    * @param type发送文件类型
    * @return
    */
    public static String post(String urlPath, String data, String type) {
        try {
            URL url = new URL(urlPath);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            if ("xml".equals(type)) {
                //TODO发送xml格式文件
                con.setRequestProperty("Content-Type", "text/xml");
            }
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream(), "UTF-8");

            out.write(data);
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";
            StringBuffer buf = new StringBuffer();
            while ((line = br.readLine()) != null) {
                buf.append(line).append("\r\n");
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getContentByPost(String url, String charSet) {
        return getContentByPost(url, charSet, null);
    }

    public static String getContentByPost(String url, String charSet, String token) {
        String content = "";
        int statusCode = 0;
        HttpClient hc = getHttpClient();
        PostMethod postMethod = new PostMethod(url);
        try {
            if (CheckUtils.isNull(charSet)) {
                charSet = DEFAULT_CHARSET;
            }
            postMethod.addRequestHeader("token", token);
            postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));
            statusCode = hc.executeMethod(postMethod);
            if (statusCode == RESP_SUCCESS) {
                content = read(postMethod.getResponseBodyAsStream(), charSet).trim();
            } else {
                logger.debug("使用get获取第三方平台资源[" + url + "]响应失败,响应吗：" + statusCode);
                throw new RuntimeException("使用post获取第三方平台资源[" + url + "]响应失败,响应吗：" + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
        return content;
    }

    public static HttpClient getHttpClient() {
        HttpClient hc = new HttpClient(manager);
        hc.getHttpConnectionManager().getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnectionsPerHost);
        hc.getHttpConnectionManager().getParams().setMaxTotalConnections(maxTotalConnections);
        hc.getHttpConnectionManager().getParams().setConnectionTimeout(defaultConnectionTimeout);
        hc.getHttpConnectionManager().getParams().setSoTimeout(defaultSoTimeout);
        return hc;
    }

    public static String read(InputStream is, String code) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, code));

        StringBuffer sb = new StringBuffer();
        String line = null;

        while ((line = br.readLine()) != null) {
            sb.append(line).append("\r\n");
        }

        br.close();

        return sb.toString().trim();
    }
}
