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
package com.hyphenate.easeui.domain;

import com.hyphenate.chat.EMContact;

public class EaseUser extends EMContact {

    /**
     * 昵称首字母
     */
	protected String initialLetter;
	/**
	 * 用户头像
	 */
	protected String avatar;

	protected String sign;

	protected String age;

	protected int charm = -1;

	protected int fortune;

	protected String voice;

	protected long onlineTime;

	protected String sex;

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSex() {
		return sex;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public void setCharm(int charm) {
		this.charm = charm;
	}

	public void setFortune(int fortune) {
		this.fortune = fortune;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public void setOnlineTime(long onlineTime) {
		this.onlineTime = onlineTime;
	}

	public String getSign() {
		return sign;
	}

	public String getAge() {
		return age;
	}

	public int getCharm() {
		return charm;
	}

	public int getFortune() {
		return fortune;
	}

	public String getVoice() {
		return voice;
	}

	public long getOnlineTime() {
		return onlineTime;
	}

	public EaseUser(String username){
	    this.username = username;
	}

	public String getInitialLetter() {
		return initialLetter;
	}

	public void setInitialLetter(String initialLetter) {
		this.initialLetter = initialLetter;
	}


	public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
	public int hashCode() {
		return 17 * getUsername().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof EaseUser)) {
			return false;
		}
		return getUsername().equals(((EaseUser) o).getUsername());
	}

	@Override
	public String toString() {
		return nick == null ? username : nick;
	}
}
