package com.vtc.vtcyoutube;

import android.app.Application;

import com.facebook.SessionDefaultAudience;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

public class GlobalApplication extends Application {
	private static final String APP_ID = "691296997625912";
	private static final String APP_NAMESPACE = "VTC 565";
	public static String dataCate = "";
	private AccountModel accountModel;

	public void setAccountModel(AccountModel accountModel) {
		this.accountModel = accountModel;
	}

	public AccountModel getAccountModel() {
		return accountModel;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// set log to true
		// set log to true

		// initialize facebook configuration
		Permission[] permissions = new Permission[] {
				Permission.PUBLIC_PROFILE, Permission.USER_GROUPS,
				Permission.USER_LIKES, Permission.USER_PHOTOS,
				Permission.USER_VIDEOS, Permission.USER_FRIENDS,
				Permission.USER_EVENTS, Permission.USER_VIDEOS,
				Permission.USER_RELATIONSHIPS, Permission.READ_STREAM,
				Permission.PUBLISH_ACTION

		};

		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
				.setAppId(APP_ID).setNamespace(APP_NAMESPACE)
				.setPermissions(permissions)
				.setDefaultAudience(SessionDefaultAudience.FRIENDS)
				.setAskForAllPermissionsAtOnce(false).build();

		SimpleFacebook.setConfiguration(configuration);
	}

}
