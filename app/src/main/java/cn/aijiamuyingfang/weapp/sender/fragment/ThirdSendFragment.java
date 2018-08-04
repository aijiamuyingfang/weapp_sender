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
import cn.aijiamuyingfang.weapp.sender.activity.ThirdSendDetailActivity;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.ThirdSendDoingAdapter;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.ThirdSendUnStartAdapter;
import io.reactivex.Observable;

/**
 * Created by pc on 2018/5/7.
 */

public final class ThirdSendFragment extends RefreshableTabFragment<ShopOrder, GetShopOrderListResponse> {
    private ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();
    private List<Integer> mTabTitleList = Arrays.asList(R.string.tab_thirdsend_layout_unstart_title, R.string.tab_thirdsend_layout_doing_title);
    private List<CommonAdapter<ShopOrder>> mAdapterList = Arrays.asList(
            new ThirdSendUnStartAdapter(getContext(), new ArrayList<>()),
            new ThirdSendDoingAdapter(getContext(), new ArrayList<>())
    );
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
            Intent intent = new Intent(getContext(), ThirdSendDetailActivity.class);
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
        return "快递送货的订单";
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int tag = (int) tab.getTag();
        int tabIndex = mTabTitleList.indexOf(tag);
        mAdapter = mAdapterList.get(tabIndex);
        mCurShopOrderSendType.clear();
        mCurShopOrderStatus.clear();
        switch (tag) {
            case R.string.tab_thirdsend_layout_unstart_title:
                mCurShopOrderSendType.add(SendType.THIRDSEND);
                mCurShopOrderStatus.add(ShopOrderStatus.UNSTART);
                break;
            case R.string.tab_thirdsend_layout_doing_title:
                mCurShopOrderSendType.add(SendType.THIRDSEND);
                mCurShopOrderStatus.add(ShopOrderStatus.DOING);
                break;
            default:
                break;
        }
        super.refreshData();
    }

    @NonNull
    @Override
    public CommonAdapter<ShopOrder> getRecyclerViewAdapter() {
        for (CommonAdapter<ShopOrder> adapter : mAdapterList) {
            adapter.setOnItemClickListener(mOnItemClickListener);
        }
        mCurShopOrderSendType.add(SendType.THIRDSEND);
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
