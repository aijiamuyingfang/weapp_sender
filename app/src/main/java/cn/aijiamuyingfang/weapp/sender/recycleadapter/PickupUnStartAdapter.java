package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;
import android.util.Log;

import java.util.List;

import cn.aijiamuyingfang.client.rest.api.UserControllerApi;
import cn.aijiamuyingfang.commons.domain.address.StoreAddress;
import cn.aijiamuyingfang.commons.domain.response.ResponseBean;
import cn.aijiamuyingfang.commons.domain.response.ResponseCode;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrder;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.commons.domain.user.User;
import cn.aijiamuyingfang.commons.domain.user.response.GetUserPhoneResponse;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.UserControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.utils.DateUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by pc on 2018/5/7.
 */

public class PickupUnStartAdapter extends CommonAdapter<ShopOrder> {
    private static final String TAG = PickupUnStartAdapter.class.getName();

    public PickupUnStartAdapter(Context context, List<ShopOrder> data) {
        super(context, data, R.layout.adapter_item_pickup_unstart);
    }

    private UserControllerApi userControllerApi = new UserControllerClient();

    @Override
    protected void convert(final RecyclerViewHolder viewHolder, final ShopOrder itemData, int position) {
        StringBuilder sb = new StringBuilder();
        for (ShopOrderItem orderGood : itemData.getOrderItemList()) {
            sb.append(orderGood.getGood().getName()).append("*").append(orderGood.getCount()).append("\n");
        }
        viewHolder.setText(R.id.goods, sb.toString());
        viewHolder.setText(R.id.total_price, "总价:" + itemData.getTotalPrice());

        StoreAddress storeAddress = itemData.getPickupAddress();
        if (storeAddress != null) {
            viewHolder.setText(R.id.pickup_address, "取货地址:" + storeAddress.getDetail());
            viewHolder.setText(R.id.store_contactNumber, "门店联系电话:" + storeAddress.getPhone());
        }

        userControllerApi.getUserPhone(CommonApp.getApplication().getUserToken(), itemData.getUserid()).subscribe(new Observer<ResponseBean<GetUserPhoneResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {
                //DO NOT NEED IMPLEMENT
            }

            @Override
            public void onNext(ResponseBean<GetUserPhoneResponse> responseBean) {
                if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                    viewHolder.setText(R.id.user_phoneNumber, "用户电话:" + responseBean.getData().getPhone());
                } else {
                    Log.e(TAG, responseBean.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "get user failed", e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "get user complete");
            }
        });
        viewHolder.setText(R.id.pichup_time, "取货时间:" + DateUtils.date2String(itemData.getPickupTime(), DateUtils.YMD_HMS_FORMAT));
        viewHolder.setText(R.id.unstart_days, "订单未处理天数:" + itemData.getLastModifyTime());
    }


}
