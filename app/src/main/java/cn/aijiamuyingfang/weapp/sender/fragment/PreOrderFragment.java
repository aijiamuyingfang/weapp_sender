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
import cn.aijiamuyingfang.commons.domain.PageResponse;
import cn.aijiamuyingfang.commons.domain.response.ResponseBean;
import cn.aijiamuyingfang.commons.domain.shoporder.PreOrderGood;
import cn.aijiamuyingfang.commons.domain.shoporder.SendType;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrderStatus;
import cn.aijiamuyingfang.commons.domain.shoporder.response.GetPreOrderGoodListResponse;
import cn.aijiamuyingfang.commons.domain.shoporder.response.GetShopOrderListResponse;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.Constant;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.OnItemClickListener;
import cn.aijiamuyingfang.weapp.sender.R;
import cn.aijiamuyingfang.weapp.sender.activity.PreOrderDetailActivity;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.PreOrderGoodAdapter;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.PreOrderOrderAdapter;
import io.reactivex.Observable;

/**
 * Created by pc on 2018/5/7.
 */
@SuppressWarnings("unchecked")
public final class PreOrderFragment extends RefreshableTabFragment<Object, PageResponse<Object>> {

    private List<Integer> mTabTitleList = Arrays.asList(R.string.tab_preorder_layout_good_title, R.string.tab_preorder_layout_order_title);
    private List<CommonAdapter> mAdapterList = Arrays.asList(
            new PreOrderGoodAdapter(getContext(), new ArrayList<>()),
            new PreOrderOrderAdapter(getContext(), new ArrayList<>())
    );
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
            Intent intent = new Intent(getContext(), PreOrderDetailActivity.class);
            intent.putExtra(Constant.INTENT_SHOPORDER, (PreOrderGood) mAdapter.getData(position));
            startActivity(intent);
        }

        @Override
        public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
            return false;
        }
    };
    private int mCurTag;
    private ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();
    private Observable<ResponseBean<PageResponse<Object>>> getPreOrderGoodList;
    private Observable<ResponseBean<PageResponse<Object>>> getShopOrderList;

    @NonNull
    @Override
    public List<Integer> getTabTitleList() {
        return mTabTitleList;
    }

    @NonNull
    @Override
    public String getToolBarTitle() {
        return "预订的订单";
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mCurTag = (int) tab.getTag();
        int tabIndex = mTabTitleList.indexOf(mCurTag);
        mAdapter = mAdapterList.get(tabIndex);
        super.refreshData();
    }

    @NonNull
    @Override
    public CommonAdapter<Object> getRecyclerViewAdapter() {
        return mAdapterList.get(0);
    }

    @Override
    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }


    @Override
    protected Observable<ResponseBean<PageResponse<Object>>> customGetData(int mCurrPage, int mPageSize) {
        synchronized (this) {
            if (null == getPreOrderGoodList) {
                Observable<ResponseBean<GetPreOrderGoodListResponse>> observable1 = shopOrderControllerApi.getPreOrderGoodList(CommonApp.getApplication().getUserToken(),
                        mCurrPage, mPageSize);
                getPreOrderGoodList = observable1.map(responseBean -> {
                    if (null == responseBean) {
                        return null;
                    }
                    ResponseBean<PageResponse<Object>> result = new ResponseBean<>();
                    result.setCode(responseBean.getCode());
                    result.setMsg(responseBean.getMsg());
                    result.setData((PageResponse) responseBean.getData());
                    return result;
                });
            }
            if (null == getShopOrderList) {
                List<SendType> sendtype = new ArrayList<>();
                List<ShopOrderStatus> status = new ArrayList<>();
                status.add(ShopOrderStatus.PREORDER);
                Observable<ResponseBean<GetShopOrderListResponse>> observable2 = shopOrderControllerApi.getShopOrderList(CommonApp.getApplication().getUserToken(),
                        status, sendtype, mCurrPage, mPageSize);
                getShopOrderList = observable2.map(responseBean -> {
                    if (null == responseBean) {
                        return null;
                    }
                    ResponseBean<PageResponse<Object>> result = new ResponseBean<>();
                    result.setCode(responseBean.getCode());
                    result.setMsg(responseBean.getMsg());
                    result.setData((PageResponse) responseBean.getData());
                    return result;
                });
            }
        }
        switch (mCurTag) {
            case R.string.tab_preorder_layout_good_title:
                return getPreOrderGoodList;
            case R.string.tab_preorder_layout_order_title:
                return getShopOrderList;
            default:
                return getPreOrderGoodList;
        }
    }

    @Override
    protected List<Object> customBeforeServerData() {
        return Collections.emptyList();
    }
}
