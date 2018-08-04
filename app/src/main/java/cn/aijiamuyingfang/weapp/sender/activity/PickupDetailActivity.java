package cn.aijiamuyingfang.weapp.sender.activity;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import cn.aijiamuyingfang.client.rest.api.ShopOrderControllerApi;
import cn.aijiamuyingfang.client.rest.api.UserControllerApi;
import cn.aijiamuyingfang.commons.domain.address.RecieveAddress;
import cn.aijiamuyingfang.commons.domain.response.ResponseCode;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrder;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrderStatus;
import cn.aijiamuyingfang.commons.domain.shoporder.request.UpdateShopOrderStatusRequest;
import cn.aijiamuyingfang.commons.utils.StringUtils;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.UserControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.Constant;
import cn.aijiamuyingfang.weapp.manager.commons.activity.BaseActivity;
import cn.aijiamuyingfang.weapp.manager.commons.utils.DateUtils;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.ClearEditText;
import cn.aijiamuyingfang.weapp.manager.widgets.WeToolBar;
import cn.aijiamuyingfang.weapp.sender.R;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.DetailActivityGoodAdapter;

public class PickupDetailActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    WeToolBar toolBar;
    @BindView(R.id.operator_ll)
    LinearLayout mOperatorLinearLayout;
    @BindView(R.id.operator)
    ClearEditText mOperatorEditText;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    DetailActivityGoodAdapter mGoodAdapter;
    @BindView(R.id.total_price)
    TextView mTotalPriceTextView;
    @BindView(R.id.send_type)
    TextView mSendTypeTextView;
    @BindView(R.id.pickup_address)
    TextView mPickupAddressTextView;
    @BindView(R.id.store_contactNumber)
    TextView mStoreContactNumberTextView;
    @BindView(R.id.user_phoneNumber)
    TextView mUserPhoneNumberTextView;
    @BindView(R.id.pickup_time)
    TextView mPickupTimeTextView;
    @BindView(R.id.shoporder_no)
    TextView mShopOrderNoTextView;
    @BindView(R.id.shoporder_createtime)
    TextView mCreateTimeTextView;

    private ShopOrder mShopOrder;

    private ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();

    @Override
    protected void init() {
        initData();
        initAdapter();
    }

    private void initData() {
        Intent intent = getIntent();
        mShopOrder = intent.getParcelableExtra(Constant.INTENT_SHOPORDER);
        if (mShopOrder.getStatus().equals(ShopOrderStatus.UNSTART)) {
            toolBar.setTitle("订单详情(未开始)");
            toolBar.setRightButtonText("开始");
            toolBar.setRightButtonOnClickListener(v -> {
                final Button button = toolBar.getRightButton();
                button.setClickable(false);
                String operator = mOperatorEditText.getText().toString();
                if (StringUtils.isEmpty(operator)) {
                    ToastUtils.showSafeToast(PickupDetailActivity.this, "请输操作员姓名");
                    button.setClickable(true);
                    return;
                }
                UpdateShopOrderStatusRequest updateBean = new UpdateShopOrderStatusRequest();
                updateBean.setStatus(ShopOrderStatus.DOING);
                updateBean.setOperator(operator);
                updateBean.setThirdsendCompany("自取");
                shopOrderControllerApi.updateShopOrderStatus(CommonApp.getApplication().getUserToken(), mShopOrder.getId(), updateBean).subscribe(responseBean -> {
                    if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                        PickupDetailActivity.this.finish();
                    } else {
                        button.setClickable(true);
                        ToastUtils.showSafeToast(PickupDetailActivity.this, "服务端异常,请稍后再试");
                    }
                });
            });
        }

        if (mShopOrder.getStatus().equals(ShopOrderStatus.DOING)) {
            toolBar.setTitle("订单详情(进行中)");
            mOperatorLinearLayout.setVisibility(View.INVISIBLE);
        }

        mTotalPriceTextView.setText(Html.fromHtml("合计 ￥<span style='color:#eb4f38'>" + mShopOrder.getTotalPrice() + "</span>", Html.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.SPANNABLE);
        mSendTypeTextView.setText(mShopOrder.getSendtype().name());
        RecieveAddress recieveaddress = mShopOrder.getRecieveAddress();
        mPickupAddressTextView.setText(recieveaddress.getPhone() + "\n" + recieveaddress.getProvince().getName()
                + recieveaddress.getCity().getName()
                + recieveaddress.getCounty().getName() + recieveaddress.getDetail());
        mStoreContactNumberTextView.setText(recieveaddress.getPhone());
        userControllerApi.getUser(CommonApp.getApplication().getUserToken(), mShopOrder.getUserid()).subscribe(userResponseBean -> {
            if (ResponseCode.OK.getCode().equals(userResponseBean.getCode())) {
                mUserPhoneNumberTextView.setText(userResponseBean.getData().getPhone());
            }
        });
        mPickupTimeTextView.setText(DateUtils.date2String(mShopOrder.getPickupTime(), DateUtils.YMD_HMS_FORMAT));
        mShopOrderNoTextView.setText(mShopOrder.getOrderNo());
        mCreateTimeTextView.setText(DateUtils.date2String(mShopOrder.getCreateTime(), DateUtils.YMD_HMS_FORMAT));
    }

    private UserControllerApi userControllerApi = new UserControllerClient();

    private void initAdapter() {
        mGoodAdapter = new DetailActivityGoodAdapter(PickupDetailActivity.this, mShopOrder.getOrderItemList());
        mRecyclerView.setAdapter(mGoodAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(PickupDetailActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(PickupDetailActivity.this,
                DividerItemDecoration.HORIZONTAL));
    }

    @Override
    public int getContentResourceId() {
        return R.layout.activity_pickup_detail;
    }
}
