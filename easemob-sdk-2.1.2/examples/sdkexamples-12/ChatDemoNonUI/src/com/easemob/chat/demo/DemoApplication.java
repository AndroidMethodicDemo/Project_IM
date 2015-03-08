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

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class DemoApplication extends Application {

	public static Context appContext;

	@Override
	public void onCreate() {
		super.onCreate();
		appContext = this;

		// 初始化环信聊天SDK
		Log.d("EMChat Demo", "initialize EMChat SDK");
		EMChat.getInstance().setDebugMode(true);
		EMChat.getInstance().init(appContext);
	}
}
