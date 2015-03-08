/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chat.demo;

import java.util.Random;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText account;
	private EditText pwd;
	private Button login;
	private Button register;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		account = (EditText) findViewById(R.id.et_account);
		pwd = (EditText) findViewById(R.id.et_pwd);
		login = (Button) findViewById(R.id.btn_login);
		register = (Button) findViewById(R.id.btn_register);

		// 登陆
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showLoginProgressDialog();
				// 登录到聊天服务器
				EMChatManager.getInstance().login(account.getText().toString(), pwd.getText().toString(), new EMCallBack() {

					@Override
					public void onError(int arg0, final String errorMsg) {
						runOnUiThread(new Runnable() {
							public void run() {
								closeLoginProgressDialog();
								Toast.makeText(LoginActivity.this, "登录聊天服务器失败：" + errorMsg, Toast.LENGTH_SHORT).show();
							}
						});
					}

					@Override
					public void onProgress(int arg0, String arg1) {
					}

					@Override
					public void onSuccess() {
						runOnUiThread(new Runnable() {
							public void run() {
								closeLoginProgressDialog();
								startActivity(new Intent(LoginActivity.this, MainActivity.class));
								Toast.makeText(LoginActivity.this, "登录聊天服务器成功", Toast.LENGTH_SHORT).show();
								finish();
							}
						});

					}
				});
			}
		});
		// 注册
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				account.setText(getAccount());
				pwd.setText("123456");
				CreateAccountTask task = new CreateAccountTask();
				task.execute(account.getText().toString(), "123456");

			}
		});
	}

	private class CreateAccountTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... args) {
			String userid = args[0];
			String pwd = args[1];
			try {
				EMChatManager.getInstance().createAccountOnServer(userid, pwd);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return userid;
		}
	}

	/**
	 * 显示提示dialog
	 */
	private void showLoginProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在登陆...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * 关闭提示dialog
	 */
	private void closeLoginProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	public String getAccount() {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字
			if ("char".equalsIgnoreCase(charOrNum)) // 字符串
			{
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
				val += (char) (choice + random.nextInt(26));
			} else if ("num".equalsIgnoreCase(charOrNum)) // 数字
			{
				val += String.valueOf(random.nextInt(10));
			}
		}

		return val.toLowerCase();
	}
}
