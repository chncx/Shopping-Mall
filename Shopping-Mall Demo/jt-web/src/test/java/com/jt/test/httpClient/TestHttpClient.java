package com.jt.test.httpClient;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class TestHttpClient {
	
	/**
	 * 测试步骤
	 * 	1.需要实例化HttpClient对象
	 *  2.编辑网址
	 *  3.创建请求对象
	 *  4.发起请求获取响应结果
	 *  5.判断结果是否正确 状态码是否为200
	 *  6.获取服务器返回值数据.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	@Test
	public void testGet() throws ClientProtocolException, IOException{
		CloseableHttpClient httpClient = 
				HttpClients.createDefault();
		
		//2.定义访问url
		String url = "https://item.jd.com/28354118289.html";
		
		//3.创建请求对象
		HttpGet httpGet = new HttpGet(url);
		
		//4.发起请求获取结果
		CloseableHttpResponse httpResponse 
		= httpClient.execute(httpGet);
		
		//5.判断返回是否正确
		if(httpResponse.getStatusLine().getStatusCode() == 200){
			
			//获取返回值全部信息
			String result = 
			EntityUtils.toString(httpResponse.getEntity());
			System.out.println(result);
		}
	}








}
