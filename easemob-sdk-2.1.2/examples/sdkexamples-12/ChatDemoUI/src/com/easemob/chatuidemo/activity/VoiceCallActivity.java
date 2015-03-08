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

package com.easemob.chatuidemo.activity;

import java.text.SimpleDateFormat;
import java.util.UUID;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.R;
import com.easemob.exceptions.EMServiceNotReadyException;

/**
 * 语音通话页面
 * 
 */
public class VoiceCallActivity extends BaseActivity implements OnClickListener {
	private LinearLayout comingBtnContainer;
	private Button hangupBtn;
	private Button refuseBtn;
	private Button answerBtn;
	private ImageView muteImage;
	private ImageView handsFreeImage;

	private boolean isMuteState;
	private boolean isHandsfreeState;
	private boolean isInComingCall;
	private TextView callStateTextView;
	private SoundPool soundPool;
	private int streamID;
	private boolean endCallTriggerByMe = false;
	private Handler handler = new Handler();
	private Ringtone ringtone;
	private int outgoing;
	private TextView nickTextView;
	private TextView durationTextView;
	private SimpleDateFormat dateFormat;
	private WindowManager windowManager;
	private AudioManager audioManager;
	private Chronometer chronometer;

	private String callDruationText;
	private String username;
	private CallingState callingState = CallingState.CANCED;
	String msgid;
	private boolean isAnswered;
	private LinearLayout voiceContronlLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_call);

		comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
		refuseBtn = (Button) findViewById(R.id.btn_refuse_call);
		answerBtn = (Button) findViewById(R.id.btn_answer_call);
		hangupBtn = (Button) findViewById(R.id.btn_hangup_call);
		muteImage = (ImageView) findViewById(R.id.iv_mute);
		handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
		callStateTextView = (TextView) findViewById(R.id.tv_call_state);
		nickTextView = (TextView) findViewById(R.id.tv_nick);
		durationTextView = (TextView) findViewById(R.id.tv_calling_duration);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);

		refuseBtn.setOnClickListener(this);
		answerBtn.setOnClickListener(this);
		hangupBtn.setOnClickListener(this);
		muteImage.setOnClickListener(this);
		handsFreeImage.setOnClickListener(this);

		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMicrophoneMute(false);

		// 注册语音电话的状态的监听
		addCallStateListener();
		msgid = UUID.randomUUID().toString();

		username = getIntent().getStringExtra("username");
		// 语音电话是否为接收的
		isInComingCall = getIntent().getBooleanExtra("isComingCall", false);

		// 设置通话人
		nickTextView.setText(username);
		if (!isInComingCall) {// 拨打电话
			soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
			outgoing = soundPool.load(this, R.raw.outgoing, 1);

			comingBtnContainer.setVisibility(View.INVISIBLE);
			hangupBtn.setVisibility(View.VISIBLE);
			callStateTextView.setText("正在呼叫...");
			handler.postDelayed(new Runnable() {
				public void run() {
					streamID = playMakeCallSounds();
				}
			}, 300);
			try {
				// 拨打语音电话
				EMChatManager.getInstance().makeVoiceCall(username);
			} catch (EMServiceNotReadyException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(VoiceCallActivity.this, "尚未连接至服务器", 0);
					}
				});
			}
		} else { // 有电话进来
			voiceContronlLayout.setVisibility(View.INVISIBLE);
			Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			audioManager.setMode(AudioManager.MODE_RINGTONE);
			audioManager.setSpeakerphoneOn(true);
			ringtone = RingtoneManager.getRingtone(this, ringUri);
			ringtone.play();
		}
	}

	/**
	 * 设置电话监听
	 */
	void addCallStateListener() {
		EMChatManager.getInstance().addVoiceCallStateChangeListener(new EMCallStateChangeListener() {

			@Override
			public void onCallStateChanged(CallState callState, CallError error) {
				// Message msg = handler.obtainMessage();
				switch (callState) {

				case CONNECTING: // 正在连接对方
					VoiceCallActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							callStateTextView.setText("正在连接对方...");
						}

					});
					break;
				case CONNECTED: // 双方已经建立连接
					VoiceCallActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							callStateTextView.setText("已经和对方建立连接，等待对方接受...");
						}

					});
					break;

				case ACCEPTED: // 电话接通成功
					VoiceCallActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								if (soundPool != null)
									soundPool.stop(streamID);
							} catch (Exception e) {
							}
							closeSpeakerOn();
							chronometer.setVisibility(View.VISIBLE);
							chronometer.setBase(SystemClock.elapsedRealtime());
							// 开始记时
							chronometer.start();
							callStateTextView.setText("通话中...");
							callingState = CallingState.NORMAL;
						}

					});
					break;
				case DISCONNNECTED: // 电话断了
					final CallError fError = error;
					VoiceCallActivity.this.runOnUiThread(new Runnable() {
						private void postDelayedCloseMsg() {
							handler.postDelayed(new Runnable() {

								@Override
								public void run() {
									saveCallRecord();
									Animation animation = new AlphaAnimation(1.0f, 0.0f);
									animation.setDuration(800);
									findViewById(R.id.root_layout).startAnimation(animation);
									finish();
								}

							}, 200);
						}

						@Override
						public void run() {
							chronometer.stop();
							callDruationText = chronometer.getText().toString();

							if (fError == CallError.REJECTED) {
								callingState = CallingState.BEREFUESD;
								callStateTextView.setText("对方拒绝接受！...");
							} else if (fError == CallError.ERROR_TRANSPORT) {
								callStateTextView.setText("连接建立失败！...");
							} else if (fError == CallError.ERROR_INAVAILABLE) {
								callingState = CallingState.OFFLINE;
								callStateTextView.setText("对方不在线，请稍后再拨...");
							} else if (fError == CallError.ERROR_BUSY) {
								callingState = CallingState.BUSY;
								callStateTextView.setText("对方正在通话中，请稍后再拨");
							} else if (fError == CallError.ERROR_NORESPONSE) {
								callingState = CallingState.NORESPONSE;
								callStateTextView.setText("对方未接听");
							} else {
								if (isAnswered) {
									callingState = CallingState.NORMAL;
									if (endCallTriggerByMe) {
										callStateTextView.setText("挂断...");
									} else {
										callStateTextView.setText("对方已经挂断...");
									}
								} else {
									if (isInComingCall) {
										callingState = CallingState.UNANSWERED;
										callStateTextView.setText("未接听");
									} else {
										callingState = CallingState.CANCED;
										callStateTextView.setText("已取消");
									}
								}
							}
							postDelayedCloseMsg();
						}

					});

					break;

				default:
					break;
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_refuse_call: // 拒绝接听
			if (ringtone != null)
				ringtone.stop();
			try {
				EMChatManager.getInstance().rejectCall();
			} catch (Exception e1) {
				e1.printStackTrace();
				saveCallRecord();
				finish();
			}
			callingState = CallingState.REFUESD;
			break;

		case R.id.btn_answer_call: // 接听电话
			comingBtnContainer.setVisibility(View.INVISIBLE);
			hangupBtn.setVisibility(View.VISIBLE);
			voiceContronlLayout.setVisibility(View.VISIBLE);
			if (ringtone != null)
				ringtone.stop();
			closeSpeakerOn();
			if (isInComingCall) {
				try {
					isAnswered = true;
					EMChatManager.getInstance().answerCall();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					saveCallRecord();
					finish();
				}
			}
			break;

		case R.id.btn_hangup_call: // 挂断电话
			if (soundPool != null)
				soundPool.stop(streamID);
			endCallTriggerByMe = true;
			try {
				EMChatManager.getInstance().endCall();
			} catch (Exception e) {
				e.printStackTrace();
				saveCallRecord();
				finish();
			}
			break;

		case R.id.iv_mute: // 静音开关
			if (isMuteState) {
				// 关闭静音
				muteImage.setImageResource(R.drawable.icon_mute_normal);
				audioManager.setMicrophoneMute(false);
				isMuteState = false;
			} else {
				// 打开静音
				muteImage.setImageResource(R.drawable.icon_mute_on);
				audioManager.setMicrophoneMute(true);
				isMuteState = true;
			}
			break;
		case R.id.iv_handsfree: // 免提开关
			if (isHandsfreeState) {
				// 关闭免提
				handsFreeImage.setImageResource(R.drawable.icon_speaker_normal);
				closeSpeakerOn();
				isHandsfreeState = false;
			} else {
				handsFreeImage.setImageResource(R.drawable.icon_speaker_on);
				openSpeakerOn();
				isHandsfreeState = true;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 播放拨号响铃
	 * 
	 * @param sound
	 * @param number
	 */
	private int playMakeCallSounds() {
		try {
			// 最大音量
			float audioMaxVolumn = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
			// 当前音量
			float audioCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_RING);
			float volumnRatio = audioCurrentVolumn / audioMaxVolumn;

			audioManager.setMode(AudioManager.MODE_RINGTONE);
			audioManager.setSpeakerphoneOn(false);

			// 播放
			int id = soundPool.play(outgoing, // 声音资源
					volumnRatio, // 左声道
					volumnRatio, // 右声道
					1, // 优先级，0最低
					-1, // 循环次数，0是不循环，-1是永远循环
					1); // 回放速度，0.5-2.0之间。1为正常速度
			return id;
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	protected void onDestroy() {
		if (soundPool != null)
			soundPool.release();
		if (ringtone != null && ringtone.isPlaying())
			ringtone.stop();
		audioManager.setMode(AudioManager.MODE_NORMAL);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		EMChatManager.getInstance().endCall();
		callDruationText = chronometer.getText().toString();
		saveCallRecord();
		finish();
	}

	// 打开扬声器
	public void openSpeakerOn() {
		try {
//			audioManager.setMode(AudioManager.MODE_IN_CALL);
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

			if (!audioManager.isSpeakerphoneOn())
				audioManager.setSpeakerphoneOn(true);
			 audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//			audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
//					AudioManager.STREAM_VOICE_CALL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 关闭扬声器
	public void closeSpeakerOn() {

		try {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager != null) {
//				int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
				if (audioManager.isSpeakerphoneOn())
					audioManager.setSpeakerphoneOn(false);
				 audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//				audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, curVolume, AudioManager.STREAM_VOICE_CALL);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存通话消息记录
	 */
	private void saveCallRecord() {
		EMMessage message = null;
		TextMessageBody txtBody = null;
		if (!isInComingCall) { // 打出去的通话
			message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			message.setReceipt(username);
		} else {
			message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
			message.setFrom(username);
		}

		switch (callingState) {
		case NORMAL:
			txtBody = new TextMessageBody("通话时长 " + callDruationText);
			break;
		case REFUESD:
			txtBody = new TextMessageBody("已拒绝");
			break;
		case BEREFUESD:
			txtBody = new TextMessageBody("对方已拒绝");
			break;
		case OFFLINE:
			txtBody = new TextMessageBody("对方不在线");
			break;
		case BUSY:
			txtBody = new TextMessageBody("对方正在通话中");
			break;
		case NORESPONSE:
			txtBody = new TextMessageBody("对方未接听");
			break;
		case UNANSWERED:
			txtBody = new TextMessageBody("未接听");
			break;
		default:
			txtBody = new TextMessageBody("已取消");
			break;
		}
		// 设置扩展属性
		message.setAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, true);

		// 设置消息body
		message.addBody(txtBody);
		message.setMsgId(msgid);

		// 保存
		EMChatManager.getInstance().saveMessage(message, false);
	}

	enum CallingState {
		CANCED, NORMAL, REFUESD, BEREFUESD, UNANSWERED, OFFLINE, NORESPONSE, BUSY
	}
}
