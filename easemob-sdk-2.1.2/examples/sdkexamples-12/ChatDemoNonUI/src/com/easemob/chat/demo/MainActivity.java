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

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private EditText tvMsg;
	private TextView tvReceivedMsg;

	private NewMessageBroadcastReceiver msgReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tvMsg = (EditText) findViewById(R.id.et_msg);
		tvReceivedMsg = (TextView) findViewById(R.id.tv_receive_msg);

		// 注册message receiver 接收消息
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		registerReceiver(msgReceiver, intentFilter);
		
		//app初始化完毕
		EMChat.getInstance().setAppInited();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		// 注销接收聊天消息的message receiver
		if (msgReceiver != null) {
			try {
				unregisterReceiver(msgReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}

	/**
	 * 发送消息。本demo是发送消息给测试机器人（其账号为"bot"）。该测试机器人接收到消息后会把接收的消息原封不动的自动发送回来
	 * 
	 * @param view
	 */
	public void onSendTxtMsg(View view) {
		EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
		// 消息发送给测试机器人，bot 会把消息自动发送回来
		msg.setReceipt("bot");
		TextMessageBody body = new TextMessageBody(tvMsg.getText().toString());
		msg.addBody(body);

		// 下面的code 展示了如何添加扩展属性
		msg.setAttribute("extStringAttr", "String Test Value");
		// msg.setAttribute("extBoolTrue", true);
		// msg.setAttribute("extBoolFalse", false);
		// msg.setAttribute("extIntAttr", 100);

		// send out msg
		try {
			EMChatManager.getInstance().sendMessage(msg);
			// Log.d("chatdemo", "消息发送成功:" + msg.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 接收消息的BroadcastReceiver
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String msgId = intent.getStringExtra("msgid"); // 消息id
			// 从SDK 根据消息ID 可以获得消息对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			Log.d("main",
					"new message id:" + msgId + " from:" + message.getFrom() + " type:" + message.getType() + " body:" + message.getBody());
			switch (message.getType()) {
			case TXT:
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				tvReceivedMsg.append("text message from:" + message.getFrom() + " text:" + txtBody.getMessage() + " \n\r");
				break;
			case IMAGE:
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				tvReceivedMsg.append("img message from:" + message.getFrom() + " thumbnail:" + imgBody.getThumbnailUrl() + " remoteurl:"
						+ imgBody.getRemoteUrl() + " \n\r");
				break;
			case VOICE:
				VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
				tvReceivedMsg.append("voice message from:" + message.getFrom() + " length:" + voiceBody.getLength() + " remoteurl:"
						+ voiceBody.getRemoteUrl() + " \n\r");
				break;
			case LOCATION:
				LocationMessageBody locationBody = (LocationMessageBody) message.getBody();
				tvReceivedMsg.append("location message from:" + message.getFrom() + " address:" + locationBody.getAddress() + " \n\r");
				break;
			}
		}
	}

}
