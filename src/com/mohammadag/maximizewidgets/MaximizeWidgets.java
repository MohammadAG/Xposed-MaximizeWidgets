package com.mohammadag.maximizewidgets;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MaximizeWidgets implements IXposedHookLoadPackage {

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("android"))
			return;
		
		String packageName;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			packageName = "com.android.keyguard";
		} else {
			packageName = "com.android.internal.policy.impl.keyguard";
		}

		Class<?> KeyguardHostView = findClass(packageName + ".KeyguardHostView", lpparam.classLoader);

		XposedHelpers.findAndHookMethod(KeyguardHostView, "onFinishInflate", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) {
				Object object = getObjectField(param.thisObject, "mSlidingChallengeLayout");
				callMethod(object, "showChallenge", false);
			}
		});

		XposedHelpers.findAndHookMethod(KeyguardHostView, "onScreenTurnedOn", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) {
				Object object = getObjectField(param.thisObject, "mSlidingChallengeLayout");
				callMethod(object, "showChallenge", false);
			}
		});

		findAndHookMethod(packageName + ".SlidingChallengeLayout", lpparam.classLoader, "onMeasure", int.class, int.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
				try {
					final View mExpandChallengeView = (View) getObjectField(param.thisObject, "mExpandChallengeView");
					ViewGroup slidingChallengeLayout = (ViewGroup) param.thisObject;
					final FrameLayout keyGuardHostView = (FrameLayout) slidingChallengeLayout.getParent();
					if (mExpandChallengeView != null && slidingChallengeLayout != null && keyGuardHostView != null) {
						mExpandChallengeView.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								mExpandChallengeView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
								callMethod(keyGuardHostView, "dismiss");
								return false;
							}
						});
					}
				} catch (NoSuchFieldError e) {
					e.printStackTrace();
				} catch (NoSuchMethodError e) {
					e.printStackTrace();
				}
			}
		});
	}
}