package com.commerce.common.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebListener
public class SessionConfig implements HttpSessionListener {

	private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

	//중복로그인 지우기
	public synchronized static String getSessionIdCheck(String type, String compareId){
		String result = "";
		for( String key : sessions.keySet() ){
			HttpSession hs = sessions.get(key);
			if(hs != null &&  hs.getAttribute(type) != null && hs.getAttribute(type).toString().equals(compareId) ){
				result =  key.toString();
			}
		}
		removeSessionForDoubleLogin(result);
		return result;
	}

	private static void removeSessionForDoubleLogin(String userId){
		log.info("remove userId : " + userId);
		if(userId != null && userId.length() > 0){
			sessions.get(userId).invalidate();
			sessions.remove(userId);
		}
	}

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		System.out.println(se);
		sessions.put(se.getSession().getId(), se.getSession());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		if(sessions.get(se.getSession().getId()) != null){
			sessions.get(se.getSession().getId()).invalidate();
			sessions.remove(se.getSession().getId());
		}
	}
}