package leancloud.zry.mymuseum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.UpdatePasswordCallback;

import leancloud.zry.mymuseum.LoginActivity;
import leancloud.zry.mymuseum.R;

import static com.avos.avoscloud.AVUser.getCurrentUser;

public class SetPasswordActivity extends AppCompatActivity {
    private EditText passwordNew;

    private EditText passwordOld;

    private Button save;

    private Intent intent;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog);
        super.onCreate(savedInstanceState);

        passwordNew = (EditText)findViewById(R.id.password_new);
        passwordOld = (EditText)findViewById(R.id.password_old);
        save = (Button) findViewById(R.id.btn_save_pop);
 //       intent = getIntent();
 //       userId = intent.getStringExtra("userId");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restPassword();
            }
        });

    }

    private void restPassword() {
        String oldP = passwordOld.getText().toString();
        String newP = passwordNew.getText().toString();
        final AVUser user = getCurrentUser();

        if (user != null){
            user.updatePasswordInBackground(oldP, newP, new UpdatePasswordCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        getCurrentUser().logOut();
                        Toast.makeText(SetPasswordActivity.this, "修改成功，请重新登陆", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(SetPasswordActivity.this, LoginActivity.class);
                        SetPasswordActivity.this.finish();
                        startActivity(intent2);
                    }
                    else {
                        Toast.makeText(SetPasswordActivity.this, "输入的初始密码有误", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }
}
