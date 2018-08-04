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
import butterknife.OnClick;
import cn.aijiamuyingfang.client.rest.api.ShopOrderControllerApi;
import cn.aijiamuyingfang.commons.domain.address.RecieveAddress;
import cn.aijiamuyingfang.commons.domain.response.ResponseCode;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrder;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrderStatus;
import cn.aijiamuyingfang.commons.domain.shoporder.request.UpdateShopOrderStatusRequest;
import cn.aijiamuyingfang.commons.utils.StringUtils;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.Constant;
import cn.aijiamuyingfang.weapp.manager.commons.activity.BaseActivity;
import cn.aijiamuyingfang.weapp.manager.commons.utils.DateUtils;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.ClearEditText;
import cn.aijiamuyingfang.weapp.manager.widgets.WeToolBar;
import cn.aijiamuyingfang.weapp.sender.R;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.DetailActivityGoodAdapter;

public class OwnSendDetailActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    WeToolBar toolBar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    DetailActivityGoodAdapter mGoodAdapter;
    @BindView(R.id.send_price)
    TextView mSendPriceTextView;
    @BindView(R.id.total_price)
    TextView mTotalPriceTextView;
    @BindView(R.id.send_type)
    TextView mSendTypeTextView;
    @BindView(R.id.address_detailAddress)
    TextView mDetailAddressTextView;
    @BindView(R.id.shoporder_no)
    TextView mOrderNoTextView;
    @BindView(R.id.shoporder_createtime)
    TextView mCreateTimeTextView;
    @BindView(R.id.sender_name)
    ClearEditText mSenderNameEditText;
    @BindView(R.id.sender_phone)
    ClearEditText mSenderPhoneEditText;

    @BindView((R.id.operator_ll))
    LinearLayout mOperatorLinearLayout;
    @BindView(R.id.operator)
    ClearEditText mOperatorEditText;

    @BindView(R.id.btn_save)
    Button mSaveButton;

    private ShopOrder mShopOrder;

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
            mSaveButton.setVisibility(View.VISIBLE);
        }

        if (mShopOrder.getStatus().equals(ShopOrderStatus.DOING)) {
            toolBar.setTitle("订单详情(进行中)");
            mSaveButton.setVisibility(View.INVISIBLE);
            mOperatorLinearLayout.setVisibility(View.INVISIBLE);
        }

        mSendPriceTextView.setText("￥" + mShopOrder.getSendPrice());
        mTotalPriceTextView.setText(Html.fromHtml("合计 ￥<span style='color:#eb4f38'>" + mShopOrder.getTotalPrice() + "</span>", Html.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.SPANNABLE);
        mSendTypeTextView.setText(mShopOrder.getSendtype().name());
        RecieveAddress recieveAddress = mShopOrder.getRecieveAddress();
        mDetailAddressTextView.setText(recieveAddress.getReciever() + " " + recieveAddress.getPhone() + "\n" + recieveAddress.getProvince().getName()
                + recieveAddress.getCity().getName()
                + recieveAddress.getCounty().getName() + recieveAddress.getDetail());
        mOrderNoTextView.setText(mShopOrder.getOrderNo());
        mCreateTimeTextView.setText(DateUtils.date2String(mShopOrder.getCreateTime(), DateUtils.YMD_HMS_FORMAT));
        mSenderNameEditText.setText(mShopOrder.getThirdsendNo());
    }

    private void initAdapter() {
        mGoodAdapter = new DetailActivityGoodAdapter(OwnSendDetailActivity.this, mShopOrder.getOrderItemList());
        mRecyclerView.setAdapter(mGoodAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(OwnSendDetailActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(OwnSendDetailActivity.this,
                DividerItemDecoration.HORIZONTAL));
    }

    @OnClick(R.id.btn_save)
    public void onClick(View view) {
        mSaveButton.setClickable(false);
        String senderName = mSenderNameEditText.getText().toString();
        if (StringUtils.isEmpty(senderName)) {
            ToastUtils.showSafeToast(OwnSendDetailActivity.this, "请输入送货员姓名");
            mSaveButton.setClickable(true);
            return;
        }

        String senderPhone = mSenderPhoneEditText.getText().toString();
        if (StringUtils.isEmpty(senderPhone)) {
            ToastUtils.showSafeToast(OwnSendDetailActivity.this, "请输入送货员电话");
            mSaveButton.setClickable(true);
            return;
        }
        String operator = mOperatorEditText.getText().toString();
        if (StringUtils.isEmpty(operator)) {
            ToastUtils.showSafeToast(OwnSendDetailActivity.this, "请输操作员姓名");
            mSaveButton.setClickable(true);
            return;
        }
        UpdateShopOrderStatusRequest updateBean = new UpdateShopOrderStatusRequest();
        updateBean.setStatus(ShopOrderStatus.DOING);
        updateBean.setOperator(operator);
        updateBean.setThirdsendCompany("送货上门");
        updateBean.setThirdsendno(senderName + " " + senderPhone);
        shopOrderControllerApi.updateShopOrderStatus(CommonApp.getApplication().getUserToken(), mShopOrder.getId(), updateBean).subscribe(responseBean -> {
            if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                OwnSendDetailActivity.this.finish();
            } else {
                mSaveButton.setClickable(true);
                ToastUtils.showSafeToast(OwnSendDetailActivity.this, "服务端异常,请稍后再试");
            }
        });

    }

    private ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();

    @Override
    public int getContentResourceId() {
        return R.layout.activity_ownsend_detail;
    }
}
