package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import cn.aijiamuyingfang.client.rest.api.ShopOrderControllerApi;
import cn.aijiamuyingfang.commons.domain.address.RecieveAddress;
import cn.aijiamuyingfang.commons.domain.response.ResponseBean;
import cn.aijiamuyingfang.commons.domain.response.ResponseCode;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrder;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.utils.DateUtils;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by pc on 2018/5/7.
 */

public class FinishedThirdSendAdapter extends CommonAdapter<ShopOrder> {
    private static final String TAG = FinishedThirdSendAdapter.class.getName();

    public FinishedThirdSendAdapter(Context context, List<ShopOrder> data) {
        super(context, data, R.layout.adapter_item_finished_thirdsend);
    }

    private ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();

    @Override
    protected void convert(RecyclerViewHolder viewHolder, final ShopOrder itemData, final int position) {
        StringBuilder sb = new StringBuilder();
        for (ShopOrderItem orderGood : itemData.getOrderItemList()) {
            sb.append(orderGood.getGood().getName()).append("*").append(orderGood.getCount()).append("\n");
        }

        viewHolder.setText(R.id.goods, sb.toString());
        viewHolder.setText(R.id.total_price, "总价:" + itemData.getTotalPrice());

        RecieveAddress recieveAddress = itemData.getRecieveAddress();
        viewHolder.setText(R.id.address_recipient, "收件人:" + recieveAddress.getReciever());
        viewHolder.setText(R.id.address_contactNumber, "联系电话:" + recieveAddress.getPhone());
        viewHolder.setText(R.id.address_detailAddress, "收件地址:" + recieveAddress.getProvince().getName() + recieveAddress.getCity().getName() + recieveAddress.getCounty().getName() + recieveAddress.getDetail());
        viewHolder.setText(R.id.thirdsend_company, "快递公司:" + itemData.getThirdsendCompany());
        viewHolder.setText(R.id.thirdsend_no, "快递单号:" + itemData.getThirdsendNo());
        viewHolder.setText(R.id.order_createtime, "订单创建时间:" + DateUtils.date2String(itemData.getCreateTime(), DateUtils.YMD_HMS_FORMAT));
        viewHolder.setText(R.id.order_finishtime, "订单结束时间:" + DateUtils.date2String(itemData.getFinishTime(), DateUtils.YMD_HMS_FORMAT));
        viewHolder.setText(R.id.order_operator, "订单处理人:" + Arrays.toString(itemData.getOperator().toArray()));
        viewHolder.setOnClickListener(R.id.btn_delete, v -> {
            int finishedDays = itemData.getLastModifyTime();
            if (finishedDays <= 100) {
                ToastUtils.showSafeToast(mContext, "只有订单已完成100天以上，才能删除");
                return;
            }
            shopOrderControllerApi.delete100DaysFinishedShopOrder(CommonApp.getApplication().getUserToken(), itemData.getId()).subscribe(new Observer<ResponseBean<Void>>() {
                @Override
                public void onSubscribe(Disposable d) {
                    //DO NOT NEED IMPLEMENT
                }

                @Override
                public void onNext(ResponseBean<Void> responseBean) {
                    if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                        FinishedThirdSendAdapter.this.removeData(position);
                    } else {
                        Log.e(TAG, responseBean.getMsg());
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "delete 100 days finished shoporder failed", e);
                }

                @Override
                public void onComplete() {
                    Log.i(TAG, "delete 100 days finished shoporder complete");
                }
            });
        });
    }
}
