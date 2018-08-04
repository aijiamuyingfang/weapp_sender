package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import cn.aijiamuyingfang.client.rest.api.ShopOrderControllerApi;
import cn.aijiamuyingfang.client.rest.api.UserControllerApi;
import cn.aijiamuyingfang.commons.domain.address.RecieveAddress;
import cn.aijiamuyingfang.commons.domain.response.ResponseCode;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrder;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.UserControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;

/**
 * Created by pc on 2018/5/7.
 */

public class FinishedPickupAdapter extends CommonAdapter<ShopOrder> {

    public FinishedPickupAdapter(Context context, List<ShopOrder> data) {
        super(context, data, R.layout.adapter_item_finished_pickup);
    }

    private UserControllerApi userControllerApi = new UserControllerClient();
    private ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();

    @Override
    protected void convert(final RecyclerViewHolder viewHolder, final ShopOrder itemData, final int position) {
        StringBuilder sb = new StringBuilder();
        for (ShopOrderItem orderGood : itemData.getOrderItemList()) {
            sb.append(orderGood.getGood().getName()).append("*").append(orderGood.getCount()).append("\n");
        }
        viewHolder.setText(R.id.goods, sb.toString());
        viewHolder.setText(R.id.total_price, "总价:" + itemData.getTotalPrice());

        RecieveAddress recieveAddress = itemData.getRecieveAddress();
        viewHolder.setText(R.id.pickup_address, "取货地址:" + recieveAddress.getDetail());
        viewHolder.setText(R.id.store_contactNumber, "门店联系电话:" + recieveAddress.getPhone());

        userControllerApi.getUser(CommonApp.getApplication().getUserToken(), itemData.getUserid()).subscribe(userResponseBean -> {
            if (ResponseCode.OK.getCode().equals(userResponseBean.getCode())) {
                viewHolder.setText(R.id.user_phoneNumber, "用户电话:" + userResponseBean.getData().getPhone());
            }
        });
        viewHolder.setText(R.id.pichup_time, "取货时间" + itemData.getPickupTime());
        viewHolder.setText(R.id.order_createtime, "订单创建时间:" + itemData.getCreateTime());
        viewHolder.setText(R.id.order_finishtime, "订单结束时间:" + itemData.getFinishTime());
        viewHolder.setText(R.id.order_operator, "订单处理人:" + Arrays.toString(itemData.getOperator().toArray()));
        viewHolder.setOnClickListener(R.id.btn_delete, v -> {
            int finishedDays = itemData.getLastModifyTime();
            if (finishedDays <= 100) {
                ToastUtils.showSafeToast(mContext, "只有订单已完成100天以上，才能删除");
                return;
            }
            shopOrderControllerApi.delete100DaysFinishedShopOrder(CommonApp.getApplication().getUserToken(), itemData.getId()).subscribe(responseBean -> {
                if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                    FinishedPickupAdapter.this.removeData(position);
                }
            });
        });
    }
}
