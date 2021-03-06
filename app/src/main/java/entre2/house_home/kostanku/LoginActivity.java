package entre2.house_home.kostanku;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import entre2.house_home.kostanku.controllers.UserController;
import entre2.house_home.kostanku.models.User;
import entre2.house_home.kostanku.utilities.Session;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvSignin, dontHaveAccount, linkSignup;
    EditText txtEmail, txtPassword;
    Button btnSignin;
    TextInputLayout input_layout_email, input_layout_password;
    Session session;
    boolean touch;


    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
        this.startActivity(intent);

        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface quicksand = Typeface.createFromAsset(getAssets(), "Quicksand-Regular.ttf");

        tvSignin = (TextView) findViewById(R.id.tvSignin);
        dontHaveAccount = (TextView) findViewById(R.id.dontHaveAccount);
        linkSignup = (TextView) findViewById(R.id.linkSignup);

        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        btnSignin = (Button) findViewById(R.id.btnSignin);

        input_layout_email = (TextInputLayout) findViewById(R.id.input_layout_email);
        input_layout_password = (TextInputLayout) findViewById(R.id.input_layout_password);

        tvSignin.setTypeface(quicksand);
        dontHaveAccount.setTypeface(quicksand);
        linkSignup.setTypeface(quicksand,Typeface.BOLD);
        linkSignup.setOnClickListener(this);

        txtEmail.setTypeface(quicksand);
        txtPassword.setTypeface(quicksand);
        btnSignin.setTypeface(quicksand);
        btnSignin.setOnClickListener(this);

        input_layout_email.setTypeface(quicksand);
        input_layout_password.setTypeface(quicksand);
    }

    @Override
    public void onClick(View v) {
        if(v == linkSignup){
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            finish();
        }else if(v == btnSignin){
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();

            session = new Session(getApplicationContext());
            User user = session.getUser();
            if(user == null){
                if(UserController.userAuth(email,password)){
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    user = UserController.getUser(email);
                    session.setUser(user);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

}
