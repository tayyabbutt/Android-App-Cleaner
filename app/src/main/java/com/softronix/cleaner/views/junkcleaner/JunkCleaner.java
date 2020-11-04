package com.softronix.cleaner.views.junkcleaner;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.marozzi.roundbutton.RoundButton;
import com.softronix.cleaner.R;
import com.softronix.cleaner.Util.CleanUtil;
import com.softronix.cleaner.callback.IScanCallback;
import com.softronix.cleaner.model.JunkGroup;
import com.softronix.cleaner.model.JunkInfo;
import com.softronix.cleaner.task.OverallScanTask;
import com.softronix.cleaner.task.ProcessScanTask;
import com.softronix.cleaner.task.SysCacheScanTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.softronix.cleaner.views.MainScreen.mainscreenloadtime;

public class JunkCleaner extends AppCompatActivity {
    public static final int MSG_SYS_CACHE_BEGIN = 0x1001;
    public static final int MSG_SYS_CACHE_POS = 0x1002;
    public static final int MSG_SYS_CACHE_FINISH = 0x1003;

    public static final int MSG_PROCESS_BEGIN = 0x1011;
    public static final int MSG_PROCESS_POS = 0x1012;
    public static final int MSG_PROCESS_FINISH = 0x1013;

    public static final int MSG_OVERALL_BEGIN = 0x1021;
    public static final int MSG_OVERALL_POS = 0x1022;
    public static final int MSG_OVERALL_FINISH = 0x1023;

    public static final int MSG_SYS_CACHE_CLEAN_FINISH = 0x1100;
    public static final int MSG_PROCESS_CLEAN_FINISH = 0x1101;
    public static final int MSG_OVERALL_CLEAN_FINISH = 0x1102;
    public static final String HANG_FLAG = "hanged";
    private static Handler handler;
    private ListHeaderView mHeaderView;

    private boolean mIsSysCacheScanFinish = false;
    private boolean mIsSysCacheCleanFinish = false;

    private boolean mIsProcessScanFinish = false;
    private boolean mIsProcessCleanFinish = false;

    private boolean mIsOverallScanFinish = false;
    private boolean mIsOverallCleanFinish = false;

    private boolean mIsScanning = false;

    private BaseExpandableListAdapter mAdapter;
    private HashMap<Integer, JunkGroup> mJunkGroups = null;

    // private Button mCleanButton;
    private RoundButton mCleanButton;
    AdView mAdView;
    String totalSize;
    ExpandableListView listView;
    private LottieAnimationView doneAnimation, animation;
    RelativeLayout doneAnimationlayout, relativelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_junk_cleaner);
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        MobileAds.initialize(this, getString(R.string.bannerappid));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(id)
                .build();
        adRequest.isTestDevice(this);
        boolean istestdeviice = adRequest.isTestDevice(this);
        mAdView.loadAd(adRequest);
        boolean shown = mAdView.isShown();
        relativelayout = findViewById(R.id.relativelayout);
        animation = findViewById(R.id.animation);
        doneAnimation = findViewById(R.id.doneAnimation);
        doneAnimationlayout = findViewById(R.id.doneAnimationlayout);
        handler();
        mCleanButton = (RoundButton) findViewById(R.id.do_junk_clean);
        mCleanButton.setEnabled(false);
        // final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        mCleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCleanButton.startAnimation();
                mCleanButton.setEnabled(false);

                clearAll();
            }
        });
        resetState();
        listView = (ExpandableListView) findViewById(R.id.junk_list);
        mHeaderView = new ListHeaderView(this, listView);
        mHeaderView.mProgress.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        listView.addHeaderView(mHeaderView);
        listView.setGroupIndicator(null);
        listView.setChildIndicator(null);
        listView.setDividerHeight(0);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                JunkInfo info = (JunkInfo) mAdapter.getChild(groupPosition, childPosition);
                if (groupPosition == JunkGroup.GROUP_APK ||
                        info.mIsChild ||
                        (groupPosition == JunkGroup.GROUP_ADV && !info.mIsChild && info.mPath != null)) {
                    if (info.mPath != null) {
                        Toast.makeText(JunkCleaner.this, info.mPath, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int childrenInThisGroup = mAdapter.getChildrenCount(groupPosition);
                    for (int i = childPosition + 1; i < childrenInThisGroup; i++) {
                        JunkInfo child = (JunkInfo) mAdapter.getChild(groupPosition, i);
                        if (!child.mIsChild) {
                            break;
                        }
                        child.mIsVisible = !child.mIsVisible;
                    }
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        mAdapter = new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return mJunkGroups.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                if (mJunkGroups.get(groupPosition).mChildren != null) {
                    return mJunkGroups.get(groupPosition).mChildren.size();
                } else {
                    return 0;
                }
            }

            @Override
            public Object getGroup(int groupPosition) {
                return mJunkGroups.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return mJunkGroups.get(groupPosition).mChildren.get(childPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return 0;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                GroupViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(JunkCleaner.this)
                            .inflate(R.layout.group_list, null);
                    holder = new GroupViewHolder();
                    holder.mPackageNameTv = (TextView) convertView.findViewById(R.id.package_name);
                    holder.mPackageSizeTv = (TextView) convertView.findViewById(R.id.package_size);
                    convertView.setTag(holder);
                } else {
                    holder = (GroupViewHolder) convertView.getTag();
                }

                JunkGroup group = mJunkGroups.get(groupPosition);
                holder.mPackageNameTv.setText(group.mName);
                holder.mPackageSizeTv.setText(CleanUtil.formatShortFileSize(JunkCleaner.this, group.mSize));

                return convertView;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                JunkInfo info = mJunkGroups.get(groupPosition).mChildren.get(childPosition);

                if (info.mIsVisible) {
                    ChildViewHolder holder;
                    if (info.mIsChild) {
                        convertView = LayoutInflater.from(JunkCleaner.this)
                                .inflate(R.layout.level2_item_list, null);
                    } else {
                        convertView = LayoutInflater.from(JunkCleaner.this)
                                .inflate(R.layout.level1_item_list, null);
                    }
                    holder = new ChildViewHolder();
                    holder.mJunkTypeTv = (TextView) convertView.findViewById(R.id.junk_type);
                    holder.mJunkSizeTv = (TextView) convertView.findViewById(R.id.junk_size);

                    holder.mJunkTypeTv.setText(info.name);
                    holder.mJunkSizeTv.setText(CleanUtil.formatShortFileSize(JunkCleaner.this, info.mSize));
                } else {
                    convertView = LayoutInflater.from(JunkCleaner.this)
                            .inflate(R.layout.item_null, null);
                }

                return convertView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }
        };

        listView.setAdapter(mAdapter);

        if (!mIsScanning) {
            mIsScanning = true;
            startScan();
        }
    }

    public void handler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MSG_SYS_CACHE_BEGIN:
                        break;

                    case MSG_SYS_CACHE_POS:
                        mHeaderView.mProgress.setText(getString(R.string.Scanning) + ":" + ((JunkInfo) msg.obj).mPackageName);
                        mHeaderView.mSize.setText(CleanUtil.formatShortFileSize(JunkCleaner.this, getTotalSize()));
                        break;

                    case MSG_SYS_CACHE_FINISH:
                        mIsSysCacheScanFinish = true;
                        checkScanFinish();
                        break;

                    case MSG_SYS_CACHE_CLEAN_FINISH:
                        mIsSysCacheCleanFinish = true;
                        checkCleanFinish();
                        Bundle bundle = msg.getData();
                        if (bundle != null) {
                            boolean hanged = bundle.getBoolean(HANG_FLAG, false);
                            if (hanged) {
                                Toast.makeText(JunkCleaner.this, getString(R.string.exception), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                        break;

                    case MSG_PROCESS_BEGIN:
                        break;

                    case MSG_PROCESS_POS:
                        mHeaderView.mProgress.setText(getString(R.string.Scanning) + ":" + ((JunkInfo) msg.obj).mPackageName);
                        mHeaderView.mSize.setText(CleanUtil.formatShortFileSize(JunkCleaner.this, getTotalSize()));
                        break;

                    case MSG_PROCESS_FINISH:
                        mIsProcessScanFinish = true;
                        checkScanFinish();
                        break;

                    case MSG_PROCESS_CLEAN_FINISH:
                        mIsProcessCleanFinish = true;
                        checkCleanFinish();
                        break;

                    case MSG_OVERALL_BEGIN:
                        break;

                    case MSG_OVERALL_POS:
                        mHeaderView.mProgress.setText(getString(R.string.Scanning) + ":" + ((JunkInfo) msg.obj).mPath);
                        mHeaderView.mSize.setText(CleanUtil.formatShortFileSize(JunkCleaner.this, getTotalSize()));
                        break;

                    case MSG_OVERALL_FINISH:
                        mIsOverallScanFinish = true;
                        checkScanFinish();
                        break;

                    case MSG_OVERALL_CLEAN_FINISH:
                        mIsOverallCleanFinish = true;
                        checkCleanFinish();
                        break;
                }
            }
        };

    }


    private void clearAll() {
        Thread clearThread = new Thread(new Runnable() {
            @Override
            public void run() {
                JunkGroup processGroup = mJunkGroups.get(JunkGroup.GROUP_PROCESS);
                for (JunkInfo info : processGroup.mChildren) {
                    CleanUtil.killAppProcesses(info.mPackageName);
                }
                Message msg = handler.obtainMessage(JunkCleaner.MSG_PROCESS_CLEAN_FINISH);
                msg.sendToTarget();

//                CleanUtil.freeAllAppsCache(handler);

                ArrayList<JunkInfo> junks = new ArrayList<>();
                JunkGroup group = mJunkGroups.get(JunkGroup.GROUP_APK);
                junks.addAll(group.mChildren);

                group = mJunkGroups.get(JunkGroup.GROUP_LOG);
                junks.addAll(group.mChildren);

                group = mJunkGroups.get(JunkGroup.GROUP_TMP);
                junks.addAll(group.mChildren);

                CleanUtil.freeJunkInfos(junks, handler);
            }
        });

        clearThread.start();
    }

    private void resetState() {
        mIsScanning = false;

        mIsSysCacheScanFinish = false;
        mIsSysCacheCleanFinish = false;

        mIsProcessScanFinish = false;
        mIsProcessCleanFinish = false;

        mJunkGroups = new HashMap<>();

        mCleanButton.setEnabled(false);

        JunkGroup cacheGroup = new JunkGroup();
        cacheGroup.mName = getString(R.string.cache_clean);
        cacheGroup.mChildren = new ArrayList<>();
        mJunkGroups.put(JunkGroup.GROUP_CACHE, cacheGroup);

        JunkGroup processGroup = new JunkGroup();
        processGroup.mName = getString(R.string.process_clean);
        processGroup.mChildren = new ArrayList<>();
        mJunkGroups.put(JunkGroup.GROUP_PROCESS, processGroup);

        JunkGroup apkGroup = new JunkGroup();
        apkGroup.mName = getString(R.string.apk_clean);
        apkGroup.mChildren = new ArrayList<>();
        mJunkGroups.put(JunkGroup.GROUP_APK, apkGroup);

        JunkGroup tmpGroup = new JunkGroup();
        tmpGroup.mName = getString(R.string.tmp_clean);
        tmpGroup.mChildren = new ArrayList<>();
        mJunkGroups.put(JunkGroup.GROUP_TMP, tmpGroup);

        JunkGroup logGroup = new JunkGroup();
        logGroup.mName = getString(R.string.log_clean);
        logGroup.mChildren = new ArrayList<>();
        mJunkGroups.put(JunkGroup.GROUP_LOG, logGroup);
    }

    private void checkScanFinish() {

        mAdapter.notifyDataSetChanged();
//&& mIsSysCacheScanFinish
        if (mIsProcessScanFinish && mIsOverallScanFinish) {
            mIsScanning = false;

            JunkGroup cacheGroup = mJunkGroups.get(JunkGroup.GROUP_CACHE);
            ArrayList<JunkInfo> children = cacheGroup.mChildren;
            cacheGroup.mChildren = new ArrayList<>();
            for (JunkInfo info : children) {
                cacheGroup.mChildren.add(info);
                if (info.mChildren != null) {
                    cacheGroup.mChildren.addAll(info.mChildren);
                }
            }
            children = null;

            long size = getTotalSize();
            totalSize = CleanUtil.formatShortFileSize(this, size);
            mHeaderView.mSize.setText(totalSize);
            mHeaderView.mProgress.setText("\n" + getString(R.string.Foundtogether) + ":" + totalSize);
            mHeaderView.mProgress.setGravity(Gravity.CENTER);

            mCleanButton.setEnabled(true);
        }
    }

    private void checkCleanFinish() {
//        && mIsSysCacheCleanFinish
        if (mIsProcessCleanFinish && mIsOverallCleanFinish) {
            String[] size = totalSize.split("\\.");
            animateTextView(Integer.parseInt(size[0]), 0, mHeaderView.mSize);
//            mHeaderView.mProgress.setText("\n" + "Clean up");
//            mHeaderView.mSize.setText(CleanUtil.formatShortFileSize(this, 0L));

//            for (JunkGroup group : mJunkGroups.values()) {
//                group.mSize = 0L;
//                group.mChildren = null;
//            }
//
//            mAdapter.notifyDataSetChanged();
//            finish();
        }
    }

    private void startScan() {

        ProcessScanTask processScanTask = new ProcessScanTask(new IScanCallback() {
            @Override
            public void onBegin() {
                Message msg = handler.obtainMessage(MSG_PROCESS_BEGIN);
                msg.sendToTarget();
            }

            @Override
            public void onProgress(JunkInfo info) {
                Message msg = handler.obtainMessage(MSG_PROCESS_POS);
                msg.obj = info;
                msg.sendToTarget();
            }

            @Override
            public void onFinish(ArrayList<JunkInfo> children) {
                JunkGroup cacheGroup = mJunkGroups.get(JunkGroup.GROUP_PROCESS);
                cacheGroup.mChildren.addAll(children);
                for (JunkInfo info : children) {
                    cacheGroup.mSize += info.mSize;
                }
                Message msg = handler.obtainMessage(MSG_PROCESS_FINISH);
                msg.sendToTarget();
            }
        });
        processScanTask.execute();

        SysCacheScanTask sysCacheScanTask = new SysCacheScanTask(new IScanCallback() {
            @Override
            public void onBegin() {
                Message msg = handler.obtainMessage(MSG_SYS_CACHE_BEGIN);
                msg.sendToTarget();
            }

            @Override
            public void onProgress(JunkInfo info) {
                Message msg = handler.obtainMessage(MSG_SYS_CACHE_POS);
                msg.obj = info;
                msg.sendToTarget();
            }

            @Override
            public void onFinish(ArrayList<JunkInfo> children) {
                JunkGroup cacheGroup = mJunkGroups.get(JunkGroup.GROUP_CACHE);
                cacheGroup.mChildren.addAll(children);
                Collections.sort(cacheGroup.mChildren);
                Collections.reverse(cacheGroup.mChildren);
                for (JunkInfo info : children) {
                    cacheGroup.mSize += info.mSize;
                }
                Message msg = handler.obtainMessage(MSG_SYS_CACHE_FINISH);
                msg.sendToTarget();
            }
        });
        sysCacheScanTask.execute();

        OverallScanTask overallScanTask = new OverallScanTask(new IScanCallback() {
            @Override
            public void onBegin() {
                Message msg = handler.obtainMessage(MSG_OVERALL_BEGIN);
                msg.sendToTarget();
            }

            @Override
            public void onProgress(JunkInfo info) {
                Message msg = handler.obtainMessage(MSG_OVERALL_POS);
                msg.obj = info;
                msg.sendToTarget();
            }

            @Override
            public void onFinish(ArrayList<JunkInfo> children) {
                for (JunkInfo info : children) {
                    String path = info.mChildren.get(0).mPath;
                    int groupFlag = 0;
                    if (path.endsWith(".apk")) {
                        groupFlag = JunkGroup.GROUP_APK;
                    } else if (path.endsWith(".log")) {
                        groupFlag = JunkGroup.GROUP_LOG;
                    } else if (path.endsWith(".tmp") || path.endsWith(".temp")) {
                        groupFlag = JunkGroup.GROUP_TMP;
                    }

                    JunkGroup cacheGroup = mJunkGroups.get(groupFlag);
                    cacheGroup.mChildren.addAll(info.mChildren);
                    cacheGroup.mSize = info.mSize;
                }

                Message msg = handler.obtainMessage(MSG_OVERALL_FINISH);
                msg.sendToTarget();
            }
        });
        overallScanTask.execute();
    }

    private long getTotalSize() {
        long size = 0L;
        for (JunkGroup group : mJunkGroups.values()) {
            size += group.mSize;
        }
        return size;
    }

    public static class GroupViewHolder {
        public TextView mPackageNameTv;
        public TextView mPackageSizeTv;
    }

    public static class ChildViewHolder {
        public TextView mJunkTypeTv;
        public TextView mJunkSizeTv;
    }


    public void animateTextView(int initialValue, final int finalValue, final TextView textview) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(4500);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                textview.setText(valueAnimator.getAnimatedValue().toString() + " %");
                if (valueAnimator.getAnimatedValue().equals(finalValue)) {
                    mHeaderView.mProgress.setText("\n" + getString(R.string.do_junk_clean));
                    mCleanButton.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    relativelayout.setBackgroundColor(ContextCompat.getColor(JunkCleaner.this, R.color.sea));
                    doneAnimationlayout.setVisibility(View.VISIBLE);
                    doneAnimation.playAnimation();
                }
            }

        });
        valueAnimator.start();
    }

    @Override
    public void onBackPressed() {
        mainscreenloadtime++;
        super.onBackPressed();
    }
}
