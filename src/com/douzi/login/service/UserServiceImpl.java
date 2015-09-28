package com.douzi.login.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.douzi.login.ui.LoginActivity;

import android.util.Log;

public class UserServiceImpl implements IUserService
{

	@Override
	public void userLogin(String loginName, String loginPassword)
			throws Exception
	{
		// TODO Auto-generated method stub
		// 模拟等待3秒
		/**  
		 * Thread.sleep(3000);
		 * 
		 * Log.d("douzi", loginName); Log.d("douzi", loginPassword);
		 * 
		 * if ("tom".equals(loginName) && "123".equals(loginPassword)){
		 * 
		 * } else{ throw new
		 * ServiceRulesException(LoginActivity.MSG_LOGIN_FAIL); }
		 */

		//get
//		HttpClient client = new DefaultHttpClient();
//		/**
//		 * 真机与wifi在同一个网段
//		 */
//		String uri = "http://192.168.3.2:8080/douzi/login.do?loginName="
//				+ loginName + "&loginPassword=" + loginPassword;
//		HttpGet get = new HttpGet(uri);
//		// 响应
//		HttpResponse response = client.execute(get);
		
		//post
		String uri = "http://192.168.3.2:8080/douzi/login.do";
		
		//参数设置
		HttpParams params = new BasicHttpParams();
		//通过params设置请求的字符集
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		//设置客户端与服务端连接的超时时间（还没有连接到服务器） ConnectTimeoutException
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		//设置服务器的响应时间（已经连接到服务器了，对话后的响应时间） SocketTimeoutException
		HttpConnectionParams.setSoTimeout(params, 3000);
		
		//设置https与http都可以访问
		SchemeRegistry sr = new SchemeRegistry();
		sr.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 433));
		sr.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager conn = new ThreadSafeClientConnManager(params, sr) ;
		
		HttpClient client = new DefaultHttpClient(conn, params);
		HttpPost post = new HttpPost(uri);
		NameValuePair loginNameParam  = new BasicNameValuePair("loginName", loginName);
		NameValuePair loginPasswordParam = new BasicNameValuePair("loginPassword", loginPassword);
		List<NameValuePair> paramters = new ArrayList<NameValuePair>();
		paramters.add(loginNameParam);
		paramters.add(loginPasswordParam);
		post.setEntity(new UrlEncodedFormEntity(paramters, HTTP.UTF_8));
		HttpResponse response = client.execute(post);
		
		
		int status = response.getStatusLine().getStatusCode();
		if (status != HttpStatus.SC_OK)
		{
			throw new
			  ServiceRulesException(LoginActivity.MSG__LOGIN_RESPONSE_ERROR);
		}
		
		String result =  EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		
		if ("success".equals(result))
		{
			Log.i("douzi", "登陆成功");
		}
		else{
			throw new
			  ServiceRulesException(LoginActivity.MSG_LOGIN_FAIL);
		}
	}

}
