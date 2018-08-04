package cn.aijiamuyingfang.weapp.sender;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.aijiamuyingfang.weapp.sender.fragment.FinishedFragment;
import cn.aijiamuyingfang.weapp.sender.fragment.OwnSendFragment;
import cn.aijiamuyingfang.weapp.sender.fragment.PickupFragment;
import cn.aijiamuyingfang.weapp.sender.fragment.PreOrderFragment;
import cn.aijiamuyingfang.weapp.sender.fragment.ThirdSendFragment;
import cn.aijiamuyingfang.weapp.manager.commons.activity.BaseActivity;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.FragmentTabHost;
import cn.aijiamuyingfang.weapp.manager.widgets.Tab;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getName();
    @BindView(android.R.id.tabhost)
    FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.instance = this;
    }

    @Override
    public int getContentResourceId() {
        return R.layout.activity_main;
    }


    @Override
    protected void init() {
        initTab();
    }

    /**
     * 初始化选项卡
     */
    private void initTab() {
        List<Tab> mTabs = new ArrayList<>();
        mTabs.add(new Tab(R.string.tab_thirdsend_itle, R.drawable.selector_icon_thirdsend, ThirdSendFragment.class));
        mTabs.add(new Tab(R.string.tab_ownsend_title, R.drawable.selector_icon_ownsend, OwnSendFragment.class));
        mTabs.add(new Tab(R.string.tab_pickup_title, R.drawable.selector_icon_pickup, PickupFragment.class));
        mTabs.add(new Tab(R.string.tab_preorder_title, R.drawable.selector_icon_preorder, PreOrderFragment.class));
        mTabs.add(new Tab(R.string.tab_finished_title, R.drawable.selector_icon_finished, FinishedFragment.class));

        mTabHost.setup(this, this.getSupportFragmentManager(), R.id.realtabcontent);
        for (Tab tab : mTabs) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getString(tab.getTitle()));
            tabSpec.setIndicator(buildIndicator(tab));
            mTabHost.addTab(tabSpec, tab.getFragment(), null);
        }

        mTabHost.setOnTabChangedListener(tabId -> Log.i(TAG, "onTabChanged:" + tabId));
        mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabHost.setCurrentTab(0);
    }

    private View buildIndicator(Tab tab) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_indicator, null);
        ImageView img = view.findViewById(R.id.icon_tab);
        TextView tv = view.findViewById(R.id.txt_indicator);
        img.setImageResource(tab.getIcon());
        tv.setText(tab.getTitle());
        return view;
    }

    //控制物理返回键
    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtils.showSafeToast(MainActivity.this, "再点一次退出轻松购");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static MainActivity instance;

    public static MainActivity getActivity() {
        return instance;
    }
}
