package com.robam.androidpad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.robam.common.skin.SkinStatusBarUtils;
import com.robam.common.utils.PermissionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CrashActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String INTENT_KEY_IN_THROWABLE = "throwable";

    /** 系统包前缀列表 */
    private static final String[] SYSTEM_PACKAGE_PREFIX_LIST = new String[]
            {"android", "com.android", "androidx", "com.google.android", "java", "javax", "dalvik", "kotlin"};

    /** 报错代码行数正则表达式 */
    private static final Pattern CODE_REGEX = Pattern.compile("\\(\\w+\\.\\w+:\\d+\\)");

    public static void start(Application application, Throwable throwable) {
        if (throwable == null) {
            return;
        }
        Intent intent = new Intent(application, CrashActivity.class);
        intent.putExtra(INTENT_KEY_IN_THROWABLE, throwable);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(intent);
    }

    private TextView mTitleView;
    private DrawerLayout mDrawerLayout;
    private TextView mInfoView;
    private TextView mMessageView;
    private String mStackTrace;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //字体
        SkinStatusBarUtils.setStatusBarLightMode(this);
        setContentView(R.layout.activity_crash);

        mTitleView = findViewById(R.id.tv_crash_title);
        mDrawerLayout = findViewById(R.id.dl_crash_drawer);
        mInfoView = findViewById(R.id.tv_crash_info);
        mMessageView = findViewById(R.id.tv_crash_message);
        findViewById(R.id.iv_crash_info).setOnClickListener(this);
        findViewById(R.id.iv_crash_share).setOnClickListener(this);
        findViewById(R.id.iv_crash_restart).setOnClickListener(this);

        initData();
    }

    protected void initData() {
        Throwable throwable = (Throwable) getIntent().getSerializableExtra(INTENT_KEY_IN_THROWABLE);
        if (throwable == null) {
            return;
        }

        mTitleView.setText(throwable.getClass().getSimpleName());

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        if (cause != null) {
            cause.printStackTrace(printWriter);
        }
        mStackTrace = stringWriter.toString();
        Matcher matcher = CODE_REGEX.matcher(mStackTrace);
        SpannableStringBuilder spannable = new SpannableStringBuilder(mStackTrace);
        if (spannable.length() > 0) {
            while (matcher.find()) {
                // 不包含左括号（
                int start = matcher.start() + "(".length();
                // 不包含右括号 ）
                int end = matcher.end() - ")".length();

                // 代码信息颜色
                int codeColor = 0xFF999999;
                int lineIndex = mStackTrace.lastIndexOf("at ", start);
                if (lineIndex != -1) {
                    String lineData = spannable.subSequence(lineIndex, start).toString();
                    if (TextUtils.isEmpty(lineData)) {
                        continue;
                    }
                    // 是否高亮代码行数
                    boolean highlight = true;
                    for (String packagePrefix : SYSTEM_PACKAGE_PREFIX_LIST) {
                        if (lineData.startsWith("at " + packagePrefix)) {
                            highlight = false;
                            break;
                        }
                    }
                    if (highlight) {
                        codeColor = 0xFF287BDE;
                    }
                }

                // 设置前景
                spannable.setSpan(new ForegroundColorSpan(codeColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // 设置下划线
                spannable.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mMessageView.setText(spannable);
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        float smallestWidth = Math.min(screenWidth, screenHeight) / displayMetrics.density;

        String targetResource;
        if (displayMetrics.densityDpi > 480) {
            targetResource = "xxxhdpi";
        } else if (displayMetrics.densityDpi > 320) {
            targetResource = "xxhdpi";
        } else if (displayMetrics.densityDpi > 240) {
            targetResource = "xhdpi";
        } else if (displayMetrics.densityDpi > 160) {
            targetResource = "hdpi";
        } else if (displayMetrics.densityDpi > 120) {
            targetResource = "mdpi";
        } else {
            targetResource = "ldpi";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("设备品牌：\t").append(Build.BRAND)
                .append("\n设备型号：\t").append(Build.MODEL)
                .append("\n设备类型：\t").append(isTablet() ? "平板" : "手机");

        builder.append("\n屏幕宽高：\t").append(screenWidth).append(" x ").append(screenHeight)
                .append("\n屏幕密度：\t").append(displayMetrics.densityDpi)
                .append("\n密度像素：\t").append(displayMetrics.density)
                .append("\n目标资源：\t").append(targetResource)
                .append("\n最小宽度：\t").append((int) smallestWidth);

        builder.append("\n安卓版本：\t").append(Build.VERSION.RELEASE)
                .append("\nAPI 版本：\t").append(Build.VERSION.SDK_INT)
                .append("\nCPU 架构：\t").append(Build.SUPPORTED_ABIS[0]);

        builder.append("\n应用版本：\t").append(BuildConfig.VERSION_NAME)
                .append("\n版本代码：\t").append(BuildConfig.VERSION_CODE);

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            builder.append("\n首次安装：\t").append(dateFormat.format(new Date(packageInfo.firstInstallTime)))
                    .append("\n最近安装：\t").append(dateFormat.format(new Date(packageInfo.lastUpdateTime)))
                    .append("\n崩溃时间：\t").append(dateFormat.format(new Date()));

            List<String> permissions = Arrays.asList(packageInfo.requestedPermissions);

            if (permissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE) || permissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (!PermissionUtils.isGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        !PermissionUtils.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    builder.append("\n存储权限：\t").append("未获得");
                else
                    builder.append("\n存储权限：\t").append("已获得");
            }

            if (permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION) || permissions.contains(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                builder.append("\n定位权限：\t");
                if (PermissionUtils.isGranted(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                        PermissionUtils.isGranted(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    builder.append("精确、粗略");
                } else {
                    if (PermissionUtils.isGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        builder.append("精确");
                    } else if (PermissionUtils.isGranted(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        builder.append("粗略");
                    } else {
                        builder.append("未获得");
                    }
                }
            }

            if (permissions.contains(Manifest.permission.CAMERA)) {
                builder.append("\n相机权限：\t").append(PermissionUtils.isGranted(this, Manifest.permission.CAMERA) ? "已获得" : "未获得");
            }

            if (permissions.contains(Manifest.permission.RECORD_AUDIO)) {
                builder.append("\n录音权限：\t").append(PermissionUtils.isGranted(this, Manifest.permission.RECORD_AUDIO) ? "已获得" : "未获得");
            }

            if (permissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                builder.append("\n悬浮窗权限：\t").append(PermissionUtils.isGranted(this, Manifest.permission.SYSTEM_ALERT_WINDOW) ? "已获得" : "未获得");
            }

            if (permissions.contains(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
                builder.append("\n安装包权限：\t").append(PermissionUtils.isGranted(this, Manifest.permission.REQUEST_INSTALL_PACKAGES) ? "已获得" : "未获得");
            }

            if (permissions.contains(Manifest.permission.INTERNET)) {
                builder.append("\n当前网络访问：\t");


            } else {
                mInfoView.setText(builder);
            }

        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.iv_crash_info) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (viewId == R.id.iv_crash_share) {
            // 分享文本
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, mStackTrace);
            startActivity(Intent.createChooser(intent, ""));
        } else if (viewId == R.id.iv_crash_restart) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        // 重启应用
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 判断当前设备是否是平板
     */
    public boolean isTablet() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}