package cn.aijiamuyingfang.weapp.sender.fragment;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.aijiamuyingfang.client.rest.api.ShopOrderControllerApi;
import cn.aijiamuyingfang.commons.domain.response.ResponseBean;
import cn.aijiamuyingfang.commons.domain.shoporder.SendType;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrder;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrderStatus;
import cn.aijiamuyingfang.commons.domain.shoporder.response.GetShopOrderListResponse;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.Constant;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.OnItemClickListener;
import cn.aijiamuyingfang.weapp.sender.R;
import cn.aijiamuyingfang.weapp.sender.activity.OwnSendDetailActivity;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.OwnSendDoingAdapter;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.OwnSendUnStartAdapter;
import io.reactivex.Observable;

/**
 * Created by pc on 2018/5/7.
 */

public final class OwnSendFragment extends RefreshableTabFragment<ShopOrder, GetShopOrderListResponse> {
    private ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();
    private List<Integer> mTabTitleList = Arrays.asList(R.string.tab_ownsend_layout_unstart_title, R.string.tab_ownsend_layout_doing_title);
    private List<CommonAdapter<ShopOrder>> mAdapterList = Arrays.asList(
            new OwnSendUnStartAdapter(CommonApp.getApplication(), new ArrayList<>()),
            new OwnSendDoingAdapter(CommonApp.getApplication(), new ArrayList<>())
    );
    private int[] mTotalpageArray = new int[]{1, 1};
    private int[] mCurrentPageArray = new int[]{1, 1};
    private int mTabIndex = 0;
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
            Intent intent = new Intent(getContext(), OwnSendDetailActivity.class);
            intent.putExtra(Constant.INTENT_SHOPORDER, mAdapter.getData(position));
            startActivity(intent);
        }

        @Override
        public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
            return false;
        }
    };
    private List<ShopOrderStatus> mCurShopOrderStatus = new ArrayList<>();
    private List<SendType> mCurShopOrderSendType = new ArrayList<>();

    @NonNull
    @Override
    public List<Integer> getTabTitleList() {
        return mTabTitleList;
    }

    @NonNull
    @Override
    public String getToolBarTitle() {
        return "送货的订单";
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int tag = (int) tab.getTag();
        mTabIndex = mTabTitleList.indexOf(tag);
        mAdapter = mAdapterList.get(mTabIndex);
        mRecyclerView.setAdapter(mAdapter);
        mCurShopOrderSendType.clear();
        mCurShopOrderStatus.clear();
        switch (tag) {
            case R.string.tab_ownsend_layout_unstart_title:
                mCurShopOrderSendType.add(SendType.OWNSEND);
                mCurShopOrderStatus.add(ShopOrderStatus.UNSTART);
                break;
            case R.string.tab_ownsend_layout_doing_title:
                mCurShopOrderSendType.add(SendType.OWNSEND);
                mCurShopOrderStatus.add(ShopOrderStatus.DOING);
                break;
            default:
                break;
        }
        super.refreshData();
    }

    @Override
    public int getCurrentPage() {
        return mCurrentPageArray[mTabIndex];
    }

    @Override
    public void setCurrentPage(int currentpage) {
        mCurrentPageArray[mTabIndex] = currentpage;
    }

    @Override
    public int getTotalPage() {
        return mTotalpageArray[mTabIndex];
    }

    @Override
    public void setTotalPage(int totalpage) {
        mTotalpageArray[mTabIndex] = totalpage;
    }

    @NonNull
    @Override
    public CommonAdapter<ShopOrder> getRecyclerViewAdapter() {
        for (CommonAdapter<ShopOrder> adapter : mAdapterList) {
            adapter.setOnItemClickListener(mOnItemClickListener);
        }
        mCurShopOrderSendType.add(SendType.OWNSEND);
        mCurShopOrderStatus.add(ShopOrderStatus.UNSTART);
        return mAdapterList.get(0);
    }

    @Override
    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    @Override
    protected Observable<ResponseBean<GetShopOrderListResponse>> customGetData(int mCurrPage, int mPageSize) {
        return shopOrderControllerApi.getShopOrderList(CommonApp.getApplication().getUserToken(), mCurShopOrderStatus, mCurShopOrderSendType, mCurrPage, mPageSize);
    }

    @Override
    protected List<ShopOrder> customBeforeServerData() {
        return Collections.emptyList();
    }
}
