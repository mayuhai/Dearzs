package com.dearzs.app.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.adapter.GvDoctorForumListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.download.DownloadNotification;
import com.dearzs.app.download.DownlodeActivity;
import com.dearzs.app.entity.EntityAppUpdateInfo;
import com.dearzs.app.entity.EntityDirAlbum;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.EntityUserVisits;
import com.dearzs.app.entity.resp.RespAppUpdate;
import com.dearzs.app.widget.HorizontalListView;
import com.dearzs.commonlib.utils.PfUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;

import static com.tencent.qalsdk.base.a.T;
import static com.tencent.qalsdk.base.a.v;

/**
 * 常用方法工具类
 */
public class Utils {
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");
    private static final SimpleDateFormat ymd_str = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat ymd = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat md_str = new SimpleDateFormat("MM-dd");
    private static final SimpleDateFormat time_str = new SimpleDateFormat("HH:mm");

    public static String mTokenId;

    public static String getTimeStamp(long specialTime) {
        return sdf2.format(new Date(specialTime));
    }

    public static String getDateYmd(Date date) {
        return date != null ? ymd_str.format(date) : "";
    }

    public static String getDateYmd(long specialTime) {
        Date date = new Date(specialTime);
        return date != null ? ymd_str.format(date) : "";
    }

    public static Date getDate(String curDate) {
        try {
            return ymd_str.parse(curDate);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static Date getTime(String curTime) {
        try {
            return time_str.parse(curTime);
        } catch (ParseException e) {
            return new Date();
        }
    }

    //TextView 设置中划线
    public static void setTextViewMiddleLine(TextView view){
        view.setPaintFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
    }

    public static String getModle(){
        String model = android.os.Build.MODEL;
        //将中文和英文状态下的空格转换成“_”
        if(model.contains(" ") || model.contains(" ")){
            model.replace(" ", "_");
            model.replace(" ", "_");
        }
        return model;
    }

    public static String getSimIccd(Context ctx){
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }

    public static String getLocalMacAddress(Context ctx) {
        WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public static String getPlatformId(){
        return android.os.Build.VERSION.SDK;
    }

    public static String getImei(Context ctx){
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static String getImsi(Context ctx){
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE);
        return tm.getSubscriberId();
    }

    public static String getDateHm(Date date) {
        return date != null ? time_str.format(date) : "";
    }

    //判断是否都是数字
    public static Boolean isNumber(String str) {
        Boolean isNumber = false;
        String expr = "^[0-9]+$";
        if (str.matches(expr)) {
            isNumber = true;
        }
        return isNumber;
    }

    //判断是否都是数字
    public static Boolean isBankCardNum(String str) {
        Boolean isBankCardNum = false;
        if (isNumber(str) && str.length() > 15 && str.length() < 20) {
            isBankCardNum = true;
        }
        return isBankCardNum;
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "((^(13|15|18|17)[0-9]{9}$)|(^0[1,2]{1}d{1}-?d{8}$)|"
                + "(^0[3-9] {1}d{2}-?d{7,8}$)|"
                + "(^0[1,2]{1}d{1}-?d{8}-(d{1,4})$)|"
                + "(^0[3-9]{1}d{2}-? d{7,8}-(d{1,4})$))";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void setEmptyView(Context ctx, ListView mLv, String tip) {
//        setEmptyView(ctx, mLv, R.color.content_bg, R.mipmap.icon_null_car, tip);
    }

    public static void setEmptyView(Context ctx, ListView mLv, int imgRes, String tip) {
        setEmptyView(ctx, mLv, R.color.content_bg, imgRes, tip);
    }

    //当前应用是否处于前台
    public static boolean isForeground(Context context) {
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo: processes) {
                if (processInfo.processName.equals(context.getPackageName())) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void setEmptyView(Context ctx, View emptyView, int imgRes, String tip, boolean showImg) {
        if (emptyView == null) {
            return;
        }
//        TextView mTvTip = GetViewUtil.getView(emptyView, R.id.lv_empty_tv_tip);
//        ImageView mIvTip = GetViewUtil.getView(emptyView, R.id.lv_empty_iv_tip);
//
//        if (showImg) {
//            mIvTip.setVisibility(View.VISIBLE);
//            try {
//                mIvTip.setImageResource(imgRes);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            mIvTip.setVisibility(View.GONE);
//        }
//        mTvTip.setText(tip);
    }

    public static void setEmptyView(Context ctx, ListView mLv, int bgRes, int imgRes, String tip) {
        if (mLv == null) {
            return;
        }
//        View emptyView = LayoutInflater.from(ctx).inflate(R.layout.listview_empty_layout, null);
//        ViewGroup mLayoutEmpty = GetViewUtil.getView(emptyView, R.id.lv_empty_layout);
//        TextView mTvTip = GetViewUtil.getView(emptyView, R.id.lv_empty_tv_tip);
//        ImageView mIvTip = GetViewUtil.getView(emptyView, R.id.lv_empty_iv_tip);
//        ViewGroup parentView = (ViewGroup) mLv.getParent();
//        parentView.addView(emptyView, 2); // 你需要在这儿设置正确的位置，以达到你需要的效果。
//        mLv.setEmptyView(emptyView);
//
//        try {
//            mLayoutEmpty.setBackgroundColor(ctx.getResources().getColor(bgRes));
//            mIvTip.setImageResource(imgRes);
//            mTvTip.setText(tip);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    //给TextView添加底部横线
    public static void setTvBottomLine(TextView tv){
        tv .getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
    }

    //给TextView添加中间横线
    public static void setTvMiddleLine(TextView tv){
        tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG );
    }

    public static float getScreenDensity(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        return density;
    }

    //获取横ListView的高度
    public static int getHorizontallListViewHeight(HorizontalListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        View listItem = listAdapter.getView(0, null, listView);
        listItem.measure(0, 0);
        return listItem.getMeasuredHeight();
    }

    /**
     * 重新计算ListView的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
     *
     * @param listView
     */
    public static int getListViewHeight(ListView listView) {
        // 获取ListView对应的Adapter
        if (listView == null) {
            return 0;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null || listAdapter.getCount() <= 0) {
            return 0;
        }
        int listViewHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            listViewHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        int totalHeight = listViewHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        return totalHeight;
    }

    /**
     * 重新计算GridView的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
     *
     * @param gridView
     */
    public static int getGridViewHeight(GridView gridView) {
        // 获取ListView对应的Adapter
        if (gridView == null) {
            return 0;
        }
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null || listAdapter.getCount() <= 0) {
            return 0;
        }
        // 获取有多少列
        int col = 2;
        int totalHeight = 0;
        // i每次加gridView的列数（例如2列 则每次 +2 ，相当于listAdapter.getCount()小于等于2时 循环一次，
        // 计算一次item的高度 ，listAdapter.getCount()小于等于4时计算两次高度相加）
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }
        return totalHeight;
    }

    public static Bitmap strToBitmap(String base64Str) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(base64Str, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 打开拨号面板
     *
     * @param mCtx
     * @param mTel
     */
    public static void intentDial(Context mCtx, String mTel) {
        Intent intent = new Intent(Intent.ACTION_DIAL,
                Uri.parse(String.format("tel:%s", mTel)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(intent);
    }

    public static boolean isRunning(Context ctx) {
        String pkgName = null;
        try {
            pkgName = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(pkgName) && info.baseActivity.getPackageName().equals(pkgName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRunningActivity(Context ctx, String actName) {
        if (actName == null) return false;
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        String className = info.topActivity.getClassName();//完整类名
        return className == null ? false : className.contains(actName);
    }

    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
            return true;
        }
        return false;
    }

    /**
     * 转换HashMap集合中的数据
     *
     * @param mPicMap
     * @return
     */
    public static ArrayList<EntityDirAlbum> getPicList(HashMap<String, List<String>> mPicMap) {
        if (mPicMap == null) {
            return null;
        }
        ArrayList<EntityDirAlbum> picList = new ArrayList<EntityDirAlbum>();
        Iterator<Map.Entry<String, List<String>>> it = mPicMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            EntityDirAlbum album = new EntityDirAlbum();

            String key = entry.getKey();
            ArrayList<String> value = (ArrayList) entry.getValue();
            album.setDirName(key);
            album.setChildList(value);
            if (value != null) {
                album.setTopImgPath(value.get(0));//获取该组的第一张图片
            }

            picList.add(album);
        }

        return picList;
    }


    public static boolean isNull(CharSequence str) {
        if (TextUtils.isEmpty(str) || "null".equalsIgnoreCase(str.toString())) {
            return true;
        }
        return false;
    }

    //下载相关
    public static boolean mIsAPPUpdataing;
    /**
     * 下载的通知id
     */
    public static final int DOWNLOAD_NEWS_NF_ID = 123;
    /**
     * 下载的文件目录
     */
    public static final String DOWNLOAD_DIR = "/dearzs";
    public static final String DOWNLOAD_NAME = "dearzs.apk";

    public static void checkSoftwareUpdate(final Context context, RespAppUpdate response, boolean isShowToast) {
        RespAppUpdate.EntityAppUpdate entityAppUpdate = response.getResult();
        if (entityAppUpdate != null) {
            EntityAppUpdateInfo entityAppUpdateInfo = entityAppUpdate.getVersion();
            if (entityAppUpdateInfo != null) {
                final String url = entityAppUpdateInfo.getFilePath();
                int version = entityAppUpdateInfo.getVersionCode();
                String updataInfo = entityAppUpdateInfo.getInfo();
                String newVersionName = entityAppUpdateInfo.getAppVersion();
                if (Utils.isLastVersion(context, version)) {
                    if (isShowToast) {
                        ToastUtil.showShortToast(R.string.current_is_last);
                    }
                } else {
                    Log.e("dearzs", "show new version update");
                    PfUtils.setStr(context, Constant.DEARZS_SP, Constant.KEY_VERSIONNAME, newVersionName);
//                    Utils.doNewVersionUpdate(context,url, version, updataInfo);
                    StringBuffer sb = new StringBuffer();
                    sb.append("当前版本:");
                    try {
                        sb.append(Utils.getVersionCode(context) + ";\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sb.append("最新版本:" + version + ";\n");
                    //		sb.append("文件大小：" + update.getFilesize() + "&lt;br/&gt;");
                    sb.append("更新内容:\n");
                    sb.append(updataInfo);
//                    Spanned sp = Html.fromHtml(sb.toString());
//                    Spanned sp1 = Html.fromHtml(sp.toString());

                    String appName = context.getString(R.string.app_name);
                    ((BaseActivity)context).showConfirmDialog(context, appName + " 更新提示", sb.toString().replace("\\n", "\n"), "确定", "暂不更新", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadNotification downloadNotification = new DownloadNotification(context);
                            downloadNotification.downloadNews(url);
                        }
                    }, null);
                }
            }
        }
    }

//    public static void checkSoftwareUpdate(Context context, RespAppUpdate response, boolean isShowToast) {
//        RespAppUpdate.EntityAppUpdate entityAppUpdate = response.getResult();
//        if (entityAppUpdate != null) {
//            EntityAppUpdateInfo entityAppUpdateInfo = entityAppUpdate.getVersion();
//            if (entityAppUpdateInfo != null) {
//                String url = entityAppUpdateInfo.getFilePath();
//                int version = entityAppUpdateInfo.getVersionCode();
//                String updataInfo = entityAppUpdateInfo.getInfo();
//                String newVersionName = entityAppUpdateInfo.getAppVersion();
//                if (isLastVersion(context, version)) {
//                    if (isShowToast) {
//                        ToastUtil.showShortToast(R.string.current_is_last);
//                    }
//                } else {
//                    Log.e("dearzs", "show new version update");
//                    PfUtils.setStr(context, Constant.DEARZS_SP, Constant.KEY_VERSIONNAME, newVersionName);
//                    doNewVersionUpdate(context,url, version, updataInfo);
//                }
//            }
//        }
//    }

    // show Update Dialog
    public static void doNewVersionUpdate(final Context context, final String url, int version, String updataInfo) {

//		String updatecontent = "1，减小安装包大小；2，添加设备各个状态显示及控制；3，添加智能场景流程；4，添加忘记，修改密码逻辑";

        StringBuffer sb = new StringBuffer();
        sb.append("当前版本:");
        try {
            sb.append(getVersionCode(context) + "&lt;br/&gt;");
        } catch (Exception e) {
            e.printStackTrace();
        }
        sb.append("最新版本:" + version + "&lt;br/&gt;");
//		sb.append("文件大小：" + update.getFilesize() + "&lt;br/&gt;");
        sb.append("更新内容:");
        sb.append(updataInfo);
        Spanned sp = Html.fromHtml(sb.toString());
        Spanned sp1 = Html.fromHtml(sp.toString());

        String appName = context.getString(R.string.app_name);
        Dialog dialog = new AlertDialog.Builder(context).setTitle(appName + " 更新提示")
                .setMessage(sp1)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloadNotification downloadNotification = new DownloadNotification(context);
                        downloadNotification.downloadNews(url);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        dialog.show();
    }

    public static boolean isLastVersion(Context context, int version) {
        int currentVersion = Utils.getVersionCode(context);
        try {
            int versionInt = version;
            if(currentVersion >= versionInt) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    /**
     * mayuhai
     *
     * @param context
     * @param icon
     * @param title
     * @param total
     * @param progress
     */
    public static void showDownloadNotification(Context context, int icon,
                                                String title, int total, int progress) {
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification progressNotification = new Notification(icon, title,
                System.currentTimeMillis());
        RemoteViews view = null;
        PendingIntent pIntent = null;// 更新显示
        Intent intent = new Intent();
        view = new RemoteViews(context.getPackageName(),
                R.layout.notification_progress_green);
        intent.setClass(context, DownlodeActivity.class);
        pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        view.setImageViewResource(R.id.image, icon);// 起一个线程用来更新progress
        view.setTextViewText(R.id.title, title);
        view.setProgressBar(R.id.pb, total, progress, false);
        // 通知的图标必须设置(其他属性为可选设置),否则通知无法显示
//		progressNotification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        progressNotification.flags |= Notification.FLAG_AUTO_CANCEL; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，
        progressNotification.contentView = view;
        progressNotification.contentIntent = pIntent;
        nm.notify(DOWNLOAD_NEWS_NF_ID, progressNotification);
    }

    // 根据notification_id 关闭对应的通知
    public static void closeNotification(Context context, int notification_id) {
        NotificationManager nm;
        nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notification_id);
    }

    public static void installApk(Context context, File file) {
//        System.out.println("installApk");
//        System.out.println("installApk filsName = " + file.getAbsolutePath());
//		File file = new File(fileName);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(Uri.fromFile(file), type);
        context.startActivity(intent);

//        SmartToast.showShortSingletonToast(context, "正在安装...");
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    //安装APK
    public static void installAssetsAPK(Context context, String apkName) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + DOWNLOAD_DIR + File.separator + "apk";
        InputStream is=null;
        FileOutputStream fos =null;
        File fileapk = null;
        try{
            is = context.getAssets().open(apkName);
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            fileapk=new File(file, apkName);
            fileapk.createNewFile();
            fos= new FileOutputStream(fileapk);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                if(fos!=null){
                    fos.close();
                }
                if(is!=null){
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Utils.installApk(context, new File(fileapk.getPath()));
    }


    public static int indexOfArr(String[] arr, String value2) {
        if (arr == null && arr.length <= 0) {
            return -1;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value2)) {
                return i;
            }
        }
        return -1;
    }

    public static String getUserToken(Context context) {
        String token = PfUtils.getStr(context, Constant.DEARZS_SP, Constant.KEY_TOKENID, null);
        return token;
    }

    public static String getVersion(Context context)//获取版本号
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "未知";
        }
    }

    public static String getWeek(Date date) {
        String[] weeks = {"周天", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    public static int getWeekId(Date date) {
        String[] weeks = {"周天", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK);
        if (week_index < 0) {
            week_index = 0;
        }
        return week_index;
    }

    //获取已星期为键，已时间为值的map
    public static Map<String, String> getStrVisitTime(List<EntityUserVisits> visits) {
        if(visits == null || visits.size() <= 0){
            return new HashMap<String, String>();
        }
        try{
            Map<String, String> dayTimeMap = new HashMap<String, String>();
            StringBuffer visitTime = new StringBuffer();
            for (int i = 0; i < visits.size(); i++) {
                dayTimeMap.put(String.valueOf(visits.get(i).getWeek()), visits.get(i).getTime());
            }
            return dayTimeMap;
        } catch (Exception e){
            e.printStackTrace();
            return new HashMap<String, String>();
        }
    }

    public static String getStrVisitWeek(Context context, List<EntityUserVisits> visits) {
        String[] weeks = context.getResources().getStringArray(R.array.week);
        if(visits == null || visits.size() <= 0){
            return "";
        }
        try{
            StringBuffer visitTime = new StringBuffer();
            for (int i = 0; i < visits.size(); i++) {
                String time = visits.get(i).getTime();
                if(!TextUtils.isEmpty(time)){
                    visitTime.append(weeks[visits.get(i).getWeek() - 1]);
                }
            }
            return visitTime.toString();
        } catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Activity跳转
     *
     * @param b
     */
    public static void startIntent(Context ctx, Class<?> cls, Bundle b) {
        Intent intent = new Intent();
        intent.setClass(ctx, cls);
        if (null != b) {
            intent.putExtras(b);
        }
        ctx.startActivity(intent);
    }

    // 将字符串转为时间戳
    public static int getTime(String timeStr, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date;
        Date nowDate = new Date();
        int n = 0;
        try {

            date = sdf.parse(timeStr);

            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            try {
                c1.setTime(date);
                c2.setTime(nowDate);
            } catch (Exception e3) {
//                System.out.println("wrong occured");
            }


            while (!c1.after(c2)) {                     // 循环对比，直到相等，n 就是所要的结果
                n++;
                c1.add(Calendar.DATE, 1);           // 比较天数，日期+1
            }

            n = n-1;
            n = (int)n/365;


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return n;
    }


    // 将时间戳转为字符串
    public static String getStrTime(String cc_time, String format) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;

    }

    // 将字符串转为时间戳
    public static String getTimeTag(String timeStr, String format) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date d;
        try {
            d = sdf.parse(timeStr);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }

    public static boolean isDateBeforeToday(String timeStr, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date currentTime = calendar.getTime();
        Date d;
        try {
            d = sdf.parse(timeStr);
            long l = d.getTime();
            long current = currentTime.getTime();
            System.currentTimeMillis();
            return l < current - 1000;      //这样获取的时间，总是比当前00点的时间多了几百。所以减去1000
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 验证身份证格式
     * @param identityCard
     * @return
     */
    public static boolean isIdentityCard(String identityCard) {

        boolean isIdentityCard = false;

        //定义判别用户身份证号的正则表达式（要么是15位，要么是18位，最后一位可以为字母）
        Pattern identityCardPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
//        Pattern identityCardPattern = Pattern.compile("\"^[1-9][0-7]\\d{4}(([1-9]\\d{3}(0[13-9]|1[012])(0[1-9]|[12]\\d|30))|([1-9]\\d{3}(0[13578]|1[02])31)|([1-9]\\d{3}02(0[1-9]|1\\d|2[0-8]))|(19([13579][26]|[2468][048]|0[48])0229)|(20([13579][26]|[2468][048]|0[048])0229))\\d{3}(\\d|X|x)?$");
        //通过Pattern获得Matcher
        Matcher identityCardMatcher = identityCardPattern.matcher(identityCard);
        //判断用户输入是否为身份证号
        if (identityCardMatcher.matches()) {
            isIdentityCard = true;
        }
        return  isIdentityCard;
    }

    public static String getJpushRegistrationID(Context context) {
        String rid = JPushInterface.getRegistrationID(context);
        if (!rid.isEmpty()) {
        } else {
            ToastUtil.showShortToast("Get registration fail, JPush init failed!");
        }

        return rid;

    }

    /**
     * 判断是不是医生用户
     * @return
     */
    public static boolean isDoctoruser() {
        boolean isDoctoruser = false;
        EntityUserInfo userInfo = BaseApplication.getInstance().getUserInfo();
        int type = EntityUserInfo.NORMALUSER;
        if (userInfo != null) {
            type = userInfo.getType();
        }

        if (type == EntityUserInfo.NORMALUSER) {//如果是患者
        }else{
            isDoctoruser = true;
        }
        return isDoctoruser;
    }

    /**
     * 判断是不是医生用户
     * @return
     */
    public static long getUserId() {
        long userId = -1l;
        EntityUserInfo userInfo = BaseApplication.getInstance().getUserInfo();
        if (userInfo != null) {
            userId = userInfo.getId();
        }
        return userId;
    }

}
