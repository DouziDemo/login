package com.douzi.login.ui;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;

import com.douzi.login.R;
import com.douzi.login.service.IUserService;
import com.douzi.login.service.ServiceRulesException;
import com.douzi.login.service.UserServiceImpl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity
{
	private static final int FLAG_LOGIN_SUCCESS = 1;
	private static final String MSG_LOGIN_SUCCESS = "登陆成功";
	private static final String MSG_LOGIN_ERROR = "登陆错误";
	public static final String MSG_LOGIN_FAIL = "登陆名或密码错误";
	public static final String MSG__LOGIN_RESPONSE_ERROR = "登陆响应错误";
	private static final String MSG_REQUEST_ERROR = "服务器请求超时";
	private static final String MSG_RESPONSE_ERROR = "服务器响应超时";
	private EditText loginNameEditText;
	private EditText loginPasswordEditText;
	private Button loginButton;
	private Button resetButton;
	private static ProgressDialog dialog;

	private IUserService userService = new UserServiceImpl();

	/**
	 * 初始化控件
	 */
	private void init()
	{
		this.loginNameEditText = (EditText) findViewById(
				R.id.edittext_login_name);
		this.loginPasswordEditText = (EditText) findViewById(
				R.id.edittext_login_password);
		this.loginButton = (Button) findViewById(R.id.button_login);
		this.resetButton = (Button) findViewById(R.id.button_reset);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);
		// 初始
		this.init();
		// 点击登陆
		this.loginButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				final String loginName = loginNameEditText.getText().toString();
				final String loginPassword = loginPasswordEditText.getText()
						.toString();
				Toast.makeText(LoginActivity.this, "登陆名： " + loginName,
						Toast.LENGTH_SHORT).show();
				Toast.makeText(LoginActivity.this, "密码： " + loginPassword,
						Toast.LENGTH_SHORT).show();
						/**
						 * 检验
						 */

				/**
				 * login...
				 */

				if (dialog == null)
				{
					dialog = new ProgressDialog(LoginActivity.this);
					dialog.setTitle("请等待");
					dialog.setMessage("登陆中...");
					dialog.setCancelable(false);
					dialog.show();
				}
				/**
				 * 副线程
				 */
				Thread thread = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						try
						{
							userService.userLogin(loginName, loginPassword);
							iHandler.sendEmptyMessage(FLAG_LOGIN_SUCCESS);
						}
						catch(ConnectTimeoutException e){
							e.printStackTrace();
							Message message = new Message();
							Bundle data = new Bundle();
							data.putString("errorMsg", MSG_REQUEST_ERROR);
							message.setData(data);
							iHandler.sendMessage(message);
						}
						catch (SocketTimeoutException e) {
							e.printStackTrace();
							Message message = new Message();
							Bundle data = new Bundle();
							data.putString("errorMsg", MSG_RESPONSE_ERROR);
							message.setData(data);
							iHandler.sendMessage(message);
						}
						catch (ServiceRulesException e)
						{
							e.printStackTrace();
							Message message = new Message();
							Bundle data = new Bundle();
							data.putString("errorMsg", e.getMessage());
							message.setData(data);
							iHandler.sendMessage(message);
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
							Message message = new Message();
							Bundle data = new Bundle();
							data.putString("errorMsg", MSG_LOGIN_ERROR);
							message.setData(data);
							iHandler.sendMessage(message);
						}
					}
				});
				thread.start();
			}
		});
		// 重置
		this.resetButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				loginNameEditText.setText("");
				loginPasswordEditText.setText("");
			}
		});
	}

	private void showTip(String msg)
	{
		Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

	private static class IHandler extends Handler
	{
		private final WeakReference<Activity> mActivity;

		public IHandler(LoginActivity loginActivity)
		{
			mActivity = new WeakReference<Activity>(loginActivity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			if (dialog != null)
			{
				dialog.dismiss();
			}
			int flag = msg.what;
			switch (flag)
			{
			case 0:
				String msgStr = msg.getData().getString("errorMsg");
				((LoginActivity) mActivity.get()).showTip(msgStr);
				break;
			case FLAG_LOGIN_SUCCESS:
				((LoginActivity) mActivity.get()).showTip(MSG_LOGIN_SUCCESS);
				break;
			default:
				break;
			}
		}

	}

	private IHandler iHandler = new IHandler(this);

}
