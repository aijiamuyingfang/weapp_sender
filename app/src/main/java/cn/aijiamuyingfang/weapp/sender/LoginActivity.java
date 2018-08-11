package cn.aijiamuyingfang.weapp.sender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.aijiamuyingfang.client.rest.api.AuthControllerApi;
import cn.aijiamuyingfang.commons.domain.response.ResponseBean;
import cn.aijiamuyingfang.commons.domain.response.ResponseCode;
import cn.aijiamuyingfang.commons.domain.user.Gender;
import cn.aijiamuyingfang.commons.domain.user.response.TokenResponse;
import cn.aijiamuyingfang.commons.utils.StringUtils;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.AuthControllerClient;
import cn.aijiamuyingfang.weapp.manager.access.server.utils.RxJavaUtils;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.activity.BaseActivity;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getName();
    @BindView(R.id.login_progress)
    ProgressBar mProgressView;
    @BindView(R.id.login_form)
    ScrollView mLoginFormView;
    @BindView(R.id.et_account)
    EditText mAcountEditView;
    @BindView(R.id.iv_account)
    ImageView mAccountImageView;

    @Override
    protected void init() {
        mAcountEditView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });
        mAcountEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NOT NEED IMPLEMENT
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //NOT NEED IMPLEMENT
            }

            @Override
            public void afterTextChanged(Editable s) {
                //如果有输入内容长度大于0那么显示clear按钮
                if (s.length() > 0) {
                    mAccountImageView.setVisibility(View.VISIBLE);
                } else {
                    mAccountImageView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private AuthControllerApi authControllerApi = new AuthControllerClient();
    private List<Disposable> authDisposableList = new ArrayList<>();

    private void attemptLogin() {
        mAcountEditView.setError(null);
        String account = mAcountEditView.getText().toString();

        if (TextUtils.isEmpty(account)) {
            mAcountEditView.setError("账号不能为空");
            mAcountEditView.requestFocus();
        } else {
            showProgress(true);
            authControllerApi.getToken(account, null, null, Gender.MALE).subscribe(new Observer<ResponseBean<TokenResponse>>() {
                @Override
                public void onSubscribe(Disposable d) {
                    authDisposableList.add(d);
                }

                @Override
                public void onNext(ResponseBean<TokenResponse> responseBean) {
                    showProgress(false);
                    if (!ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                        Log.e(TAG, responseBean.getMsg());
                        mAcountEditView.setError("账号不存在");
                        mAcountEditView.requestFocus();
                        return;
                    }
                    TokenResponse tokenResponse = responseBean.getData();
                    if (null == tokenResponse || StringUtils.isEmpty(tokenResponse.getToken())) {
                        mAcountEditView.setError("账号不存在");
                        mAcountEditView.requestFocus();
                        return;
                    }
                    CommonApp.getApplication().setUserToken(tokenResponse.getToken());
                    CommonApp.getApplication().setUserId(tokenResponse.getUserid());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "user login failed", e);
                }

                @Override
                public void onComplete() {
                    Log.d(TAG, "user login success");
                }
            });

        }
    }

    @OnClick({R.id.btn_login, R.id.iv_account})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                attemptLogin();
                break;
            case R.id.iv_account:
                mAcountEditView.setText("");
                break;
            default:
                break;
        }
    }


    @Override
    protected int getContentResourceId() {
        return R.layout.activity_login;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxJavaUtils.dispose(authDisposableList);
    }
}