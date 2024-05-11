package com.example.userdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.wizarpos.wizarviewagentassistant.aidl.IModifyAdminPwdService;

public class MainActivity extends Activity {
    IModifyAdminPwdService adminPwdService;

    private Button confirmBtn;
    private Button resetBtn;
    private Button enableBtn;
    private Button disableBtn;
    private Switch switchBtn;
    private EditText passwordEdit;
    private Context mContext;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        confirmBtn = findViewById(R.id.confirm);
        resetBtn = findViewById(R.id.reset);
        enableBtn = findViewById(R.id.enableLogin);
        disableBtn = findViewById(R.id.disableLogin);
        passwordEdit = findViewById(R.id.password);
        confirmBtn.setOnClickListener(btnClickListener);
        resetBtn.setOnClickListener(btnClickListener);
        enableBtn.setOnClickListener(btnClickListener);
        disableBtn.setOnClickListener(btnClickListener);
        mContext = this;
        bindService();
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                adminPwdService = IModifyAdminPwdService.Stub.asInterface(service);
                Log.d("IModifyAdminPwdService", "IModifyAdminPwdService bind success.");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Remote exception:", e);
            } finally {
                unbindService(this);
            }

        }
    };

    public void bindService() {
        ComponentName comp = new ComponentName(
                "com.wizarpos.wizarviewagentassistant",
                "com.wizarpos.wizarviewagentassistant.AdminPwdMainService");
        startService(this, comp, serviceConnection);
        Log.i(TAG, "IModifyAdminPwdService bind success.");
    }

    private void startService(Context context, ComponentName comp, ServiceConnection connection) {
        try {
            Intent intent = new Intent();
            intent.setPackage(comp.getPackageName());
            intent.setComponent(comp);
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Button.OnClickListener btnClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            Button btn = (Button) v;
            String newPwd = passwordEdit.getText().toString();
            try {
                if (btn.getId() == R.id.confirm) {
                    if (newPwd.trim().isEmpty()) {
                        Toast.makeText(mContext, "please enter the password", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isUserPwd = adminPwdService.isUserPwd(newPwd);
                        if (isUserPwd) {
                            Toast.makeText(mContext, "login success!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "login failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (btn.getId() == R.id.reset) {
                    if (newPwd.trim().isEmpty()) {
                        Toast.makeText(mContext, "please enter the password", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean result = adminPwdService.forceModifyUserPwd(newPwd);
                        if (result) {
                            Toast.makeText(mContext, "update password success!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "update password failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (btn.getId() == R.id.enableLogin) {
                    boolean result = adminPwdService.enableUserLogin(true);
                    if (result) {
                        Toast.makeText(mContext, "enables success!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "enables failed!", Toast.LENGTH_SHORT).show();
                    }
                } else if (btn.getId() == R.id.disableLogin) {
                    boolean result = adminPwdService.enableUserLogin(false);
                    if (result) {
                        Toast.makeText(mContext, "disables success!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "disables failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "Remote exception:", e);
            }
        }
    };

}
