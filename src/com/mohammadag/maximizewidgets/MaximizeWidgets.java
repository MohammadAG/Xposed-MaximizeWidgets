package com.mohammadag.maximizewidgets;

import static de.robv.android.xposed.XposedHelpers.findClass;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MaximizeWidgets implements IXposedHookLoadPackage {

		@Override
		public void handleLoadPackage(LoadPackageParam lpparam)
				throws Throwable {
			if (!lpparam.packageName.equals("android"))
				return;
			
			Class<?> KeyguardHostView = findClass("com.android.internal.policy.impl.keyguard.KeyguardHostView", lpparam.classLoader);
			
			XposedHelpers.findAndHookMethod(KeyguardHostView, "onFinishInflate", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) {
					Object object = XposedHelpers.getObjectField(param.thisObject, "mSlidingChallengeLayout");
					XposedHelpers.callMethod(object, "showChallenge", false);
				}
			});
			
			XposedHelpers.findAndHookMethod(KeyguardHostView, "onScreenTurnedOn", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) {
					Object object = XposedHelpers.getObjectField(param.thisObject, "mSlidingChallengeLayout");
					XposedHelpers.callMethod(object, "showChallenge", false);
				}
			});
		}
}