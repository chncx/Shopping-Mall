package com.jt.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HttpClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientService.class);

    @Autowired(required=false)
    private CloseableHttpClient httpClient;

    @Autowired(required=false)
    private RequestConfig requestConfig;
    
    
    /**
     * 说明:
     * 	 请求方式:Get和POST请求
     * 	 参数如何添加:
     * 		http://www.jd.com/add?id=1&name=2
     *   参数进行封装:
     *   如果用户需要传递参数,则通过指定的方法进行调用即可.
     *   Map<String,String>类型
     *   
     *   /*url  = url + "?";
	    		
	    		//www.baidu.com?id=1&name=tom&
	    		for (Map.Entry<String, String> entry: params.entrySet()) {
	    			
	    			url  = url + entry.getKey() + "=" + entry.getValue() + "&";
				}
	    		url = url.substring(0, url.length()-1);
	  */
  
    public String doGet(String url,Map<String,String> params,String charset){
    	
    	String result = null; //访问服务端程序时回传的JSON数据
    	
    	//判断字符集编码是否为null,如果为null设定默认字符集
    	if(StringUtils.isEmpty(charset)){
    		
    		charset = "UTF-8";
    	}
    	
    	try {
	    	//判断参数是否为null
	    	if(params != null){
	    		URIBuilder builder = new URIBuilder(url);
	    		for (Map.Entry<String,String> entry : params.entrySet()) {
	    	
	    			builder.addParameter(entry.getKey(),entry.getValue());
				}
	    		//自动的拼接?和&符  http://www.baidu.com?id=1&name=tom
	    		url = builder.build().toString();
	    		
	    	}
	    	
	    	//System.out.println("访问的请求:" + url);
	    	//定义请求的类型
	    	HttpGet httpGet = new HttpGet(url);
	    	httpGet.setConfig(requestConfig);
	    	
	    	//通过httpClient发送请求
	    	CloseableHttpResponse httpResponse = 
	    			httpClient.execute(httpGet);
	    	
	    	if(httpResponse.getStatusLine().getStatusCode() == 200){
	    		//获取返回值数据
	    		result = 
	    		EntityUtils.toString(httpResponse.getEntity(),charset);
	    	}
	    	
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return result;
    }
    
    public String doGet(String url,Map<String,String> params){
    	
    	return doGet(url, params, null);
    }
    
    public String doGet(String url){
    	
    	return doGet(url, null, null) ;
    }
    
    //实现httpClient中的post请求
    public String doPost(String url,Map<String,String> params,String charset){
    	
    	String result = null;
    	//判断字符集编码
    	if(StringUtils.isEmpty(charset)){
    		
    		charset = "UTF-8";
    	}
    	
    	/**
    	 * 2.需要先创建请求对象
    	 * 	 2.1创建表单实体对象封装参数.
    	 *   2.2将表单对象保存到Post对象中
    	 *   2.3之后发起请求 	
    	 */
    	HttpPost httpPost = new HttpPost(url);
    	httpPost.setConfig(requestConfig);
    	
    	
    	try {
	    	//判断是否有参数
	    	if(params != null){
	    		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
	    		
	    		//获取用户传递的数据
	    		for (Map.Entry<String,String> entry: params.entrySet()) {
	    			BasicNameValuePair pair = 
	    		new BasicNameValuePair(entry.getKey(), entry.getValue());
	    			parameters.add(pair);
				}
	    		//创建表单实体对象
	    		UrlEncodedFormEntity formEntity = 
	    				new UrlEncodedFormEntity(parameters,charset);
	    		
	    		//将请求实体添加到请求对象中
	    		httpPost.setEntity(formEntity);
	    	}
	    	
	    	//实现post请求
	    	CloseableHttpResponse httpResponse = 
	    	httpClient.execute(httpPost);
	    	
	    	if(httpResponse.getStatusLine().getStatusCode() == 200){
	    		
	    		result = EntityUtils.toString(httpResponse.getEntity(),charset);
	    	}
	    	
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return result;
    }
    
    public String doPost(String url,Map<String,String> params){
    	
    	return doPost(url, params, null);
    }
    
    public String doPost(String url){
    	
    	return doPost(url, null, null);
    }
}
