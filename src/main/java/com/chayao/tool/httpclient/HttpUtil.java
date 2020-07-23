package com.chayao.tool.httpclient;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * get示例
     */
    public static void get() {
        // 1. 创建一个HttpClient
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            // 2. 新建一个HttpGet对象
            HttpGet httpget = new HttpGet("http://httpbin.org/get");
            System.out.println("Executing request " + httpget.getRequestLine());

            // 3. 创建一个自定义的response处理器
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };

            // 4.httpClient执行，将httpget、responseHandler传入，获得responseHanlder的处理结果
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * post示例
     */
    public static void post() {
        // 1. 创建一个HttpClient
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            // 2. 新建一个httpPost对象
            HttpPost httpPost = new HttpPost("http://httpbin.org/post");
//            httpPost.setHeader();
            httpPost.setEntity(new StringEntity("Hello, World"));

            System.out.println("Executing request " + httpPost.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String responseBody = httpclient.execute(httpPost, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  post Json格式数据
     *
     * @param url url
     * @param data 数据
     * @return response实体的字符串格式
     */
    public String postJson(String url, Map<String, Object> data) {
        JSONObject paramMap = new JSONObject();
        if (data != null) {
            for (Map.Entry<String, Object> item : data.entrySet()) {
                paramMap.put(item.getKey(), item.getValue());
            }
        }
        StringEntity entity;
        entity = new StringEntity(paramMap.toJSONString(), "UTF-8");
        entity.setContentType("application/json");
        return post(url, entity);
    }

    private String post(String url, HttpEntity entity) {
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        try {
            post.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
            post.setEntity(entity);
            // 设置请求时间，请根据实际情况重新设置
            RequestConfig config = RequestConfig.custom().setConnectTimeout(1000).setSocketTimeout(2000).build();
            post.setConfig(config);
            CookieStore cookieStore = new BasicCookieStore();
            HttpClientBuilder clientBuilder = HttpClients.custom();
            clientBuilder.setDefaultCookieStore(cookieStore);
            client = clientBuilder.build();
            response = client.execute(post);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return e.getMessage();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("关闭http错误");
                }
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    logger.error("http post client 关闭错误", e);
                }
            }
        }
    }

    public static void main(String[] args) {
        post();
    }

}
