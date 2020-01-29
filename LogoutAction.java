package com.internousdev.latte.action;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

public class LogoutAction extends ActionSupport implements SessionAware {

	public Map<String,Object> session;

	public String execute() {

		String userId = String.valueOf(session.get("userId"));
		String tempSavedUserIdFlg = String.valueOf(session.get("savedUserIdFlg"));

		boolean savedUserIdFlg = "null".equals(tempSavedUserIdFlg)? false : Boolean.valueOf(tempSavedUserIdFlg);
		session.clear();
		if (savedUserIdFlg) {
			session.put("savedUserIdFlg", savedUserIdFlg);
			session.put("userId",userId);
		}
		return SUCCESS;
	}

	public Map<String,Object> getSession() {
		return session;
	}
	@Override
	public void setSession(Map<String,Object> session) {
		this.session = session;
	}

}