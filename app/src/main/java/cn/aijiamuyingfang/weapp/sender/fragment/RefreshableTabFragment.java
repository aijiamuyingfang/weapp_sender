package cn.aijiamuyingfang.weapp.sender.fragment;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;

import java.util.List;

import cn.aijiamuyingfang.commons.domain.PageResponse;
import cn.aijiamuyingfang.weapp.manager.widgets.WeToolBar;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.RefreshableBaseFragment;
import cn.aijiamuyingfang.weapp.sender.R;

public abstract class RefreshableTabFragment<E, V extends PageResponse<E>> extends RefreshableBaseFragment<E, V> implements TabLayout.OnTabSelectedListener {

    private TabLayout mTabLayout;

    /**
     * @return 获取各Tab Title的资源ID
     */
    @NonNull
    public abstract List<Integer> getTabTitleList();

    /**
     * @return Fragment标题名
     */
    @NonNull
    public abstract String getToolBarTitle();

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //NOT NEED IMPLEMENT
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //NOT NEED IMPLEMENT
    }


    @Override
    public void customRecyclerView() {
        WeToolBar toolBar = fragmentView.findViewById(R.id.toolbar);
        toolBar.setTitle(getToolBarTitle());
        mTabLayout = fragmentView.findViewById(R.id.tab_layout);
        mTabLayout.addOnTabSelectedListener(this);
        for (int resourceId : getTabTitleList()) {
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setText(resourceId);
            tab.setTag(resourceId);
            mTabLayout.addTab(tab);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        super.refreshData();
    }

    @Override
    public int getContentResourceId() {
        return R.layout.fragment_refreshable_tab;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTabLayout.removeOnTabSelectedListener(this);
    }
}
