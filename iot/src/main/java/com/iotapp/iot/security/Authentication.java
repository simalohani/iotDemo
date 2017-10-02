package com.iotapp.iot.security;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

public class Authentication {
	SharedPreferences authpref;
	Set userList;

	public Authentication(Context ctx) {
		this.authpref = ctx.getSharedPreferences("auth", 0);
		userList = this.authpref.getStringSet("user", null);

	}

	public String userAuthentication(String user) {
		int length =user.length();
		String imei =user.substring(length-15,length);
		if (userList != null) {
			Iterator iterator = userList.iterator();
			if (iterator.hasNext()) {
				String storeUser=iterator.next().toString();
				int size =storeUser.length();
				String imeiNo =storeUser.substring(size-15,size);
				if (imeiNo.equalsIgnoreCase(imei)) {
					return "Device Already Registered with user :"+storeUser.substring(0,length-15);
				}
				else if(storeUser.substring(0,length-15).equalsIgnoreCase(user.substring(0,length-15))){
					return "User Already Registered ";
				}
				
			} 
			userList.add(user);	
			
		} else {
			userList = new HashSet();
			userList.add(user);
		}
		this.authpref.edit().putStringSet("user", userList).commit();
		return "User Registered successfully";

	}
}
