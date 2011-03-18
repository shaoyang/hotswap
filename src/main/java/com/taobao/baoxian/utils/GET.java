package com.taobao.baoxian.utils;

import javax.servlet.http.HttpServletRequest;

public class GET {

	private static final Long LONG_DEFAULT = 0L;
	private static final Integer INT_DEFAULT = 0;
	private static final String STR_DEFAULT = "";

	private HttpServletRequest request;

	public GET(HttpServletRequest request) {
		this.request = request;
	}

	public String getUserName() {
		return GET.getUserName(request);
	}

	public static String getUserName(HttpServletRequest request) {
		try {
			return request.getSession().getAttribute("_nk_").toString();
		} catch (Exception e) {
			return "";
		}

	}

	public Long getUserId() {
		return GET.getUserId(request);
	}

	public static Long getUserId(HttpServletRequest request) {
		try {
			return Long.valueOf(request.getSession().getAttribute("userIDNum")
					.toString());
		} catch (Exception e) {
				return 0L;
		}
	}
	public static Long getUserIdFromLongLogin(HttpServletRequest request) {
		Long userId = getUserId(request);
		if(0L==userId){
			try{
				String longLogin=request.getSession().getAttribute("longLogin").toString();
				return Long.valueOf(longLogin.split("_")[0]);
			}catch(Exception e1){
				return 0L;
			}
		}
		else{
			return userId;
		}
	}

	public String getString(String name) {
		return GET.getString(request, name, STR_DEFAULT);
	}

	public String getString(String name, String fail) {
		return GET.getString(request, name, fail);
	}

	public Long getLong(String name) {
		return GET.getLong(request, name);
	}

	public Long getLong(String name, Long fail) {
		return GET.getLong(request, name, fail);
	}

	public Integer getInteger(String name) {
		return GET.getInteger(request, name);
	}

	public Integer getInteger(String name, Integer fail) {
		return GET.getInteger(request, name, fail);
	}

	public static String getString(HttpServletRequest request, String name) {
		return GET.getString(request, name, GET.STR_DEFAULT);
	}

	public static String getString(HttpServletRequest request, String name,
			String fail) {
		try {
			return GET.getRaw(request, name, fail).toString();
		} catch (Exception e) {
		}

		return fail;
	}

	public static Integer getInteger(HttpServletRequest request, String name) {
		return GET.getInteger(request, name, GET.INT_DEFAULT);
	}

	public static Integer getInteger(HttpServletRequest request, String name,
			Integer fail) {
		try {
			return Integer.valueOf(GET.getRaw(request, name, fail).toString());
		} catch (Exception e) {
		}

		return fail;
	}

	public static Long getLong(HttpServletRequest request, String name) {
		return GET.getLong(request, name, GET.LONG_DEFAULT);
	}

	public static Long getLong(HttpServletRequest request, String name,
			Long fail) {
		try {
			return Long.valueOf(GET.getRaw(request, name, fail).toString());
		} catch (Exception e) {
		}

		return fail;
	}

	private static Object getRaw(HttpServletRequest request, String name,
			Object fail) {
		try {
			Object o = request.getParameter(name);
			if (o != null) {
				return o;
			}
		} catch (Exception e) {
		}

		return fail;
	}
}
