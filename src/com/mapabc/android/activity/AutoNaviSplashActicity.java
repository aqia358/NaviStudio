package com.mapabc.android.activity;

import java.io.File;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mapabc.android.activity.base.BaseActivity;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.utils.SettingForLikeTools;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.general.function.http.NetworkListener;
import com.mapabc.general.function.http.NetworkTask;
import com.mapabc.general.function.overload.DownLoadManager;
import com.mapabc.general.function.overload.ObserverDownLoadFileOver;
import com.mapabc.naviapi.utils.SysParameterManager;

/**
 * @description: 首页免责申明
 * @author: zhuhao 2012-08-06
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class AutoNaviSplashActicity extends BaseActivity implements
		NetworkListener {
	private static final String TAG = "AutoNaviSplashActicity";
	private static final int GETSYSVERSION = 1;
	private AlertDialog dialog;
	private String apkurl;// 下载APK URL
	String path = SysParameterManager.getBasePath() + "/MapABC/download.apk";;// 下载程序的存储路径
	public ProgressDialog pdg = null;
	private boolean isCancel = true;
	NetworkTask net = null;
	Handler h = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 0) {
				if (pdg != null) {
					isCancel = false;
					pdg.cancel();
				}
				String tipinfo = AutoNaviSplashActicity.this
						.getString(R.string.autonavisplashacticity_getnewversion_failed);
				Utils.showTipInfo(AutoNaviSplashActicity.this, tipinfo
						+ msg.obj);
				showLogo();
			} else if (msg.what == 1) {
				if (pdg != null) {
					isCancel = false;
					pdg.cancel();
				}
				String versioninfo = msg.obj + "";
				String versionarray[] = versioninfo.split(",");
				String version = versionarray[0].split("\\_")[0];
				apkurl = versionarray[2];
				String currentversion[] = AutoNaviSplashActicity.this
						.getString(R.string.aboutautonavi_softnum).split("v");
				int newintversion = Utils.getIntVersion(version);
				int currenversion = Utils.getIntVersion(currentversion[1]
						.split("\\_")[0]);
				if (newintversion > currenversion) {
					showUpdateTip(version, Integer.parseInt(versionarray[1]));
				} else {
					Utils.showTipInfo(AutoNaviSplashActicity.this,
							R.string.autonavisplashacticity_lastversioninfo);
					File file = new File(path);
					if (file.exists()) {
						file.delete();
					}
					showLogo();
				}
			}
		}

	};
	ObserverDownLoadFileOver loadListener = new ObserverDownLoadFileOver() {

		@Override
		public void downLoadFileOver(int arg0) {
			Log.e(TAG, "返回状态：" + arg0);
			if (arg0 == ObserverDownLoadFileOver.DOWNLOAD_SUCCESS) {
				showUpdateSussesDialog();
			} else {
				if (arg0 == ObserverDownLoadFileOver.NO_PATH) {
					Utils.showTipInfo(AutoNaviSplashActicity.this,
							R.string.autonavisplashacticity_load_no_path);
				} else if (arg0 == ObserverDownLoadFileOver.DOWNLOAD_ERROR) {
					Utils.showTipInfo(AutoNaviSplashActicity.this,
							R.string.autonavisplashacticity_load_no_net);
				} else if (arg0 == ObserverDownLoadFileOver.DOWNLOAD_TIMEOUT) {
					Utils.showTipInfo(AutoNaviSplashActicity.this,
							R.string.autonavisplashacticity_load_timeout);
				} else if (arg0 == ObserverDownLoadFileOver.NO_ENOUGH_STORAGE) {
					Utils
							.showTipInfo(
									AutoNaviSplashActicity.this,
									R.string.autonavisplashacticity_load_no_enough_storage);
				}
				showLogo();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Utils.isLand(this)) {
			setContentView(R.layout.navisplash_splashscreen_land);
		} else {
			setContentView(R.layout.navisplash_splashscreen);
		}

		// 判断SDCard是否存在
		if (!Utils.pathExist(SysParameterManager.getBasePath() + "/MapABC")) {
			showExitTip(Utils.getValue(this, R.string.navisplash_nosdcard_tip));
			return;
		}
		// 判断是否显示申明
		if (SettingForLikeTools.getDisclaimerStates(this)) {
			showDisclaimer();
		} else if (SettingForLikeTools.getUpdateState(this) == 0) {
			// 联网判断当前版本
			getversion();
		} else {
			showLogo();
		}
		// ///////////////////////全国路测//////////////////////////////
		if (new File(SysParameterManager.getBasePath()).exists()) {
			String path = SysParameterManager.getBasePath() + "/MapABC/log";
			File f = new File(path);
			if (!f.exists()) {
				boolean re = f.mkdir();
				if (!re) {
					return;
				}
			}
		}
		// ///////////////////////全国路测//////////////////////////////
	}

	private void getversion() {
		String url = Utils.getUpdateSysURL();
		if (url.startsWith("http://")) {
			//

			net = new NetworkTask(this, url);
			net.setListener(this);
			net.start();
			showDialog();
		}
	}

	private void showDialog() {
		pdg = new ProgressDialog(this);
		pdg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pdg.setIndeterminate(false);
		pdg.setCancelable(false);
		pdg.setMessage(this
				.getString(R.string.atuonavisplashactivity_getversion));
		pdg.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (isCancel) {
					showLogo();
				}
			}
		});
		pdg.show();
		pdg.setCancelable(false);
	}

	private void showLogo() {
		// TODO Auto-generated method stub
		if (SettingForLikeTools.getDisclaimerStates(this)) {
			dialog.dismiss();
		}
		if (net != null) {
			net.setListener(null);
		}
		startTask();
	}

	private void startTask() {
		AsyncTask<Void, Void, Void> async = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				new Handler().postDelayed(new Runnable() {
					public void run() {
						startActivity(new Intent(
								Constants.ACTIVITY_LOADING_LOGO));
						finish();
					}
				}, 1500);
			}
		};
		async.execute();
	}

	// 显示申明
	private void showDisclaimer() {

		View view = View.inflate(this, R.layout.navisplash_disclaimer_dialog,
				null);
		CheckBox cb = (CheckBox) view.findViewById(R.id.cb_disclaimer_dialog);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					SettingForLikeTools.setDisclaimerStates(
							AutoNaviSplashActicity.this, 0);
				} else {
					SettingForLikeTools.setDisclaimerStates(
							AutoNaviSplashActicity.this, 1);
				}
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(
				view).setPositiveButton(R.string.common_confirm,
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (SettingForLikeTools
								.getUpdateState(AutoNaviSplashActicity.this) == 0) {
							// 联网判断当前版本
							getversion();
						} else {
							showLogo();
						}
					}
				}).setNegativeButton(R.string.navisplash_refuse,
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
						System.exit(0);
					}
				});
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.navisplash_splashscreen_land);
		} else {
			setContentView(R.layout.navisplash_splashscreen);
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		System.exit(0);
	}

	private void showExitTip(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.common_tip).setIcon(R.drawable.alert_dialog_icon)
				.setMessage(msg).setPositiveButton(R.string.common_confirm,
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
								System.exit(0);
							}
						});
		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		dialog.setCancelable(false);
	}

	private void showUpdateSussesDialog() {
		String msg = this.getString(R.string.autonavisplashacticity_load_ok);
		msg = msg.replaceFirst("\\?", path);
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.common_tip).setIcon(R.drawable.alert_dialog_icon)
				.setMessage(msg).setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						Log.e(TAG, "showUpdateTip,onCancel");
						showLogo();
					}

				}).setPositiveButton(R.string.common_confirm,
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent
										.setDataAndType(Uri.fromFile(new File(
												path)),
												"application/vnd.android.package-archive");
								startActivity(intent);
								AutoNaviSplashActicity.this.finish();
							}
						}).setNegativeButton(R.string.common_cancel,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								showLogo();
							}
						});

		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		dialog.setCancelable(false);

	}

	private void showUpdateTip(String version, int datalength) {
		String msg = this.getString(R.string.autonavisplashacticity_update_msg);
		datalength = datalength / 1024;
		datalength = datalength / 1024;
		msg = msg.replaceFirst("\\?", version);
		msg = msg.replaceFirst("\\?", datalength + "");
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.common_tip).setIcon(R.drawable.alert_dialog_icon)
				.setMessage(msg).setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						Log.e(TAG, "showUpdateTip,onCancel");
						showLogo();
					}

				}).setPositiveButton(R.string.common_confirm,
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								DownLoadManager download = new DownLoadManager(
										AutoNaviSplashActicity.this,
										loadListener, apkurl, path);
								download.checkUpdateInfo();
							}
						}).setNegativeButton(R.string.common_cancel,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								showLogo();
							}
						});

		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		dialog.setCancelable(false);
	}
	@Override
	public Object onRetainNonConfigurationInstance() {
		// TODO Auto-generated method stub
		return super.onRetainNonConfigurationInstance();
	}

	@Override
	public void onConnectionError(int arg0, String arg1) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		msg.what = 0;
		msg.obj = arg1;
		h.sendMessage(msg);
	}

	@Override
	public void onConnectionOver() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionRecieveData(byte[] arg0, int arg1) {
		// TODO Auto-generated method stub
		if (arg0 != null && arg0.length > 0) {
			String version = new String(arg0);
			Message msg = Message.obtain();
			msg.what = 1;
			msg.obj = version;
			h.sendMessage(msg);
		} else {
			Message msg = Message.obtain();
			msg.what = 0;
			msg.obj = this
					.getString(R.string.autonavisplashacticity_hasnoversioninfo);
			h.sendMessage(msg);
		}
	}
}
