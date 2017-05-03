package com.arexnt.sms;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.arexnt.sms.common.SettingFragment;
import com.arexnt.sms.ui.base.SmsFragmentPagerAdapter;
import com.arexnt.sms.ui.captcha.CaptchaListFragment;
import com.arexnt.sms.ui.conversation.ConversationFragment;
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
//    @BindView(R.id.fab)
//    FloatingActionButton mFab;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getPermission();
//        check();
        setSupportActionBar(mToolbar);
        setupViewPager(mViewpager);
        mTabs.setupWithViewPager(mViewpager);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        mNavView.setNavigationItemSelectedListener(this);

    }

    private void setupViewPager(ViewPager viewpager){
        SmsFragmentPagerAdapter adapter = new SmsFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ConversationFragment().newInstance(SettingFragment.PERSONAL_LIST), getString(R.string.personal_fragment));
        adapter.addFragment(new ConversationFragment().newInstance(SettingFragment.NOTIF_LIST), getString(R.string.notification_fragment));
        adapter.addFragment(new CaptchaListFragment(), getString(R.string.captcha_fragment));
        viewpager.setAdapter(adapter);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            setDefaultSmsApp();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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


    public void getPermission(){
        AndPermission.with(this)
                .requestCode(SettingFragment.REQUEST_CODE_PERMISSION_READ_SMS_AND_CONTACT)
                .permission(Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS)
                .rationale((requestCode, rationale) ->
                AndPermission.rationaleDialog(MainActivity.this, rationale).show()
                )
                .send();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    @PermissionYes(100)
    private void getReadPermissions(List<String> grantedPermissions){
//        Snackbar.make(mDrawerLayout,"获取权限成功",Snackbar.LENGTH_SHORT).show();
    }
    public void check(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> mNotifList = preferences.getStringSet(SettingFragment.NOTIF_SENDERS, new HashSet<String>());
        Set<String> mPersonalList = preferences.getStringSet(SettingFragment.PERSONAL_SENDERS, new HashSet<String>());
        Set<String> copy = new HashSet<>(mNotifList);
        Log.d("chekFilterList", String.valueOf(mNotifList));
        Log.d("chekFilterList", String.valueOf(mPersonalList));
        Log.d("compareListHash","original: " + mNotifList.hashCode() + ",\n copy: " + copy.hashCode());
    }
}
