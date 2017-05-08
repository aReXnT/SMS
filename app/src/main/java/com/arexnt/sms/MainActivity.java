package com.arexnt.sms;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.arexnt.sms.common.Constant;
import com.arexnt.sms.data.DataServer;
import com.arexnt.sms.ui.base.SmsFragmentPagerAdapter;
import com.arexnt.sms.ui.captcha.CaptchaListFragment;
import com.arexnt.sms.ui.conversation.ConversationFragment;
import com.arexnt.sms.ui.setting.SettingActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionYes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;

    private int mCurrentPage = 0;
    private ConversationFragment mPersonalFragment;
    private ConversationFragment mNotifFagment;
    private CaptchaListFragment mCaptchaListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getPermission();
        check();
    }

    private void setupViewPager(ViewPager viewpager){
        SmsFragmentPagerAdapter adapter = new SmsFragmentPagerAdapter(getSupportFragmentManager());
        mPersonalFragment = new ConversationFragment().newInstance(Constant.PERSONAL_LIST);
        adapter.addFragment(mPersonalFragment, getString(R.string.personal_fragment));
        mNotifFagment = new ConversationFragment().newInstance(Constant.NOTIF_LIST);
        adapter.addFragment(mNotifFagment , getString(R.string.notification_fragment));
        mCaptchaListFragment = new CaptchaListFragment();
        adapter.addFragment(mCaptchaListFragment, getString(R.string.captcha_fragment));
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(3);
        mTabs.setupWithViewPager(mViewpager);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupMenu(){
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        mNavView.setNavigationItemSelectedListener(this);
        mFab.setOnClickListener(v -> smoothScrollToTop());

        //双击标题栏
        final GestureDetector detector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        scrollToTop();
                        return super.onDoubleTap(e);
                    }
                });
        mToolbar.setOnTouchListener((v, event) ->
                detector.onTouchEvent(event));
    }

    public void smoothScrollToTop(){
        switch (mCurrentPage){
            case 0:
                mPersonalFragment.smoothScrollToTop();
                break;
            case 1:
                mNotifFagment.smoothScrollToTop();
                break;
            case 2:
                mCaptchaListFragment.smoothScrollToTop();
        }
    }

    public void scrollToTop(){
        switch (mCurrentPage){
            case 0:
                mPersonalFragment.scrollToTop();
                break;
            case 1:
                mNotifFagment.scrollToTop();
                break;
            case 2:
                mCaptchaListFragment.scrollToTop();
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_info) {
            setDefaultSmsApp();
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setDefaultSmsApp(){
        String defaultSmsApp = null;
        String currentPn = getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
        }
        if (!defaultSmsApp.equals(currentPn)){
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, currentPn);
            startActivity(intent);
        }
    }

    //获取权限
    public void getPermission(){
        AndPermission.with(this)
                .requestCode(Constant.REQUEST_CODE_PERMISSION_READ_SMS_AND_CONTACT)
                .permission(Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS)
                .rationale((requestCode, rationale) ->
                AndPermission.rationaleDialog(MainActivity.this, rationale).show()
                )
                .send();
    }
    //权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    //获取权限成功返回方法
    @PermissionYes(100)
    private void getReadPermissions(List<String> grantedPermissions){
//        Snackbar.make(mDrawerLayout,"获取权限成功",Snackbar.LENGTH_SHORT).show();
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        DataServer dataServer = new DataServer(getApplicationContext(), mPreferences, Constant.PERSONAL_LIST);
        dataServer.getAddress();
        dataServer.filterConversation();

        setupViewPager(mViewpager);
        setupMenu();

    }


    public void check(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> mNotifList = preferences.getStringSet(Constant.NOTIF_SENDERS, new HashSet<String>());
        Set<String> mPersonalList = preferences.getStringSet(Constant.PERSONAL_SENDERS, new HashSet<String>());
        Set<String> copy = new HashSet<>(mNotifList);
        Log.d("chekFilterList", String.valueOf(mNotifList));
        Log.d("chekFilterList", String.valueOf(mPersonalList));
//        Log.d("compareListHash","original: " + mNotifList.hashCode() + ",\n copy: " + copy.hashCode());
    }
}
