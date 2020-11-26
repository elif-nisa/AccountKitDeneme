package com.yalova.accountkitdeneme;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huawei.hms.tasks.OnCompleteListener;
import com.huawei.hms.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.service.AccountAuthServiceImpl;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

public class MainActivity extends AppCompatActivity {
    TextView loginDesc;
    Button loginButton;
    AuthHuaweiId huaweiAccount;

    @SuppressLint("ResourceType")
    public void setHuaweiAccount(AuthHuaweiId huaweiAccount) {
        this.huaweiAccount = huaweiAccount;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login);
        loginDesc = findViewById(R.id.text);
        huaweiAccount = null;

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().createParams();
                HuaweiIdAuthService service = HuaweiIdAuthManager.getService(MainActivity.this, authParams);

                if (huaweiAccount == null)
                {
                    startActivityForResult(service.getSignInIntent(), 1123);
                }
                else {
                    Task<Void> signOutTask = service.signOut();
                    signOutTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            // Processing after the sign-out.
                            Log.i("TAG", "signOut complete");
                            loginButton.setText(R.string.login);
                            loginDesc.setText(R.string.please_login);
                            huaweiAccount = null;
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Process the authorization result to obtain an ID token from AuthHuaweiId.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1123) {

            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);

            if (authHuaweiIdTask.isSuccessful()) {

                huaweiAccount = authHuaweiIdTask.getResult();
                loginDesc.setText(huaweiAccount.getEmail());
                loginButton.setText(R.string.logout);

            } else {
                Log.e("TAG", "sign in failed : " +((ApiException)authHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }
}