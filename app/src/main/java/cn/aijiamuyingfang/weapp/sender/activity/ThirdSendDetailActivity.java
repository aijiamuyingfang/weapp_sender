package cn.aijiamuyingfang.weapp.sender.activity;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.aijiamuyingfang.client.rest.api.ShopOrderControllerApi;
import cn.aijiamuyingfang.commons.domain.address.RecieveAddress;
import cn.aijiamuyingfang.commons.domain.response.ResponseBean;
import cn.aijiamuyingfang.commons.domain.response.ResponseCode;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrder;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrderStatus;
import cn.aijiamuyingfang.commons.domain.shoporder.request.UpdateShopOrderStatusRequest;
import cn.aijiamuyingfang.commons.utils.StringUtils;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.access.server.utils.RxJavaUtils;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.Constant;
import cn.aijiamuyingfang.weapp.manager.commons.activity.BaseActivity;
import cn.aijiamuyingfang.weapp.manager.commons.utils.DateUtils;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.ClearEditText;
import cn.aijiamuyingfang.weapp.manager.widgets.WeToolBar;
import cn.aijiamuyingfang.weapp.sender.R;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.DetailActivityGoodAdapter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ThirdSendDetailActivity extends BaseActivity {
    private static final String TAG = ThirdSendDetailActivity.class.getName();
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
    @BindView(R.id.thirdsend_company)
    ClearEditText mThirdSendCompanyEditText;
    @BindView(R.id.thirdsend_no)
    ClearEditText mThirdSendNoEditText;

    @BindView((R.id.operator_ll))
    LinearLayout mOperatorLinearLayout;
    @BindView(R.id.operator)
    ClearEditText mOperatorEditText;

    @BindView(R.id.btn_save)
    Button mSaveButton;

    private ShopOrder mShopOrder;
    private ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();
    private List<Disposable> disposableList = new ArrayList<>();

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
        mThirdSendCompanyEditText.setText(mShopOrder.getThirdsendCompany());
        mThirdSendNoEditText.setText(mShopOrder.getThirdsendNo());
    }

    private void initAdapter() {
        mGoodAdapter = new DetailActivityGoodAdapter(ThirdSendDetailActivity.this, mShopOrder.getOrderItemList());
        mRecyclerView.setAdapter(mGoodAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ThirdSendDetailActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(ThirdSendDetailActivity.this,
                DividerItemDecoration.HORIZONTAL));
    }

    @OnClick(R.id.btn_save)
    public void onClick(View view) {
        mSaveButton.setClickable(false);
        String thirdsendNo = mThirdSendNoEditText.getText().toString();
        if (StringUtils.isEmpty(thirdsendNo)) {
            ToastUtils.showSafeToast(ThirdSendDetailActivity.this, "请输入快递单号");
            mSaveButton.setClickable(true);
            return;
        }

        String thirdsendCompany = mThirdSendCompanyEditText.getText().toString();
        if (StringUtils.isEmpty(thirdsendCompany)) {
            ToastUtils.showSafeToast(ThirdSendDetailActivity.this, "请填写快递公司");
            mSaveButton.setClickable(true);
            return;
        }
        String operator = mOperatorEditText.getText().toString();
        if (StringUtils.isEmpty(operator)) {
            ToastUtils.showSafeToast(ThirdSendDetailActivity.this, "请输操作员姓名");
            mSaveButton.setClickable(true);
            return;
        }
        UpdateShopOrderStatusRequest updateBean = new UpdateShopOrderStatusRequest();
        updateBean.setStatus(ShopOrderStatus.DOING);
        updateBean.setOperator(operator);
        updateBean.setThirdsendCompany(thirdsendCompany);
        updateBean.setThirdsendno(thirdsendNo);
        shopOrderControllerApi.updateShopOrderStatus(CommonApp.getApplication().getUserToken(), mShopOrder.getId(), updateBean)
                .subscribe(new Observer<ResponseBean<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposableList.add(d);
                    }

                    @Override
                    public void onNext(ResponseBean<Void> responseBean) {
                        if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                            ThirdSendDetailActivity.this.finish();
                        } else {
                            Log.e(TAG, responseBean.getMsg());
                            mSaveButton.setClickable(true);
                            ToastUtils.showSafeToast(ThirdSendDetailActivity.this, "服务端异常,请稍后再试");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "update shoporder status failed", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "update shoporder status complete");
                    }
                });

    }

    @Override
    public int getContentResourceId() {
        return R.layout.activity_thirdsend_detail;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxJavaUtils.dispose(disposableList);
    }
}
