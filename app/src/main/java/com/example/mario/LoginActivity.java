package com.example.mario;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;
import org.json.JSONException;

public class LoginActivity extends AppCompatActivity
{
	private EditText mEmail,mPassword;
	private Button buttonLogin,buttonSignup;
	private final int REQUEST_SIGNUP=101;
	private Toast mToast;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		mToast=Toast.makeText(this,"",0);
		mEmail = (EditText)findViewById(R.id.email);
		mPassword = (EditText)findViewById(R.id.password);
		buttonLogin = (Button)findViewById(R.id.button_login);
		buttonSignup = (Button)findViewById(R.id.button_signup);
		
		buttonLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String n,p;
				n = mEmail.getText().toString();
				p = mPassword.getText().toString();
				if("".equals(n))
					d("Username required");
				else if("".equals(p))
					d("Password required");
				else
					new ValidateLogin(n,p).execute();
			}
		});
		buttonSignup.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent ri = new Intent(LoginActivity.this,SignupActivity.class);
					startActivityForResult(ri,REQUEST_SIGNUP);
				}
			});
		
		setResult(RESULT_CANCELED);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case REQUEST_SIGNUP:
				if(resultCode == RESULT_OK) {
					setResult(RESULT_OK);
					finish();
				}
		}
	}
	
	private void d(String s) {
		mToast.cancel();
		mToast = Toast.makeText(LoginActivity.this,s,Toast.LENGTH_SHORT);
		mToast.show();
	}
	
	private class ValidateLogin extends AsyncTask<Void,Void,Void>
	{
		private String data,error;
		private URL url;
		
		public ValidateLogin(String n,String p) {
			try {
				data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(n, "UTF-8");
				data += "&" + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(p, "UTF-8");
			}
			catch(UnsupportedEncodingException e) {error = e.toString();}
		}

		@Override
		protected void onPreExecute()
		{
			// TODO: Implement this method
			super.onPreExecute();
			buttonLogin.setEnabled(false);
			buttonSignup.setEnabled(false);
			try {
				//url = new URL("http://client.epizy.com/mario/login.php");
				url = new URL("http://skhastagir98.000webhostapp.com/login.php");
			}
			catch(MalformedURLException e) {error = e.toString();}
		}

		@Override
		protected void onPostExecute(Void result)
		{
			// TODO: Implement this method
			super.onPostExecute(result);
			buttonLogin.setEnabled(true);
			buttonSignup.setEnabled(true);
			if(error == null) {
				JSONObject jobj = null;
				try {
					jobj = new JSONObject(data);
					if(jobj.getInt("success") == 1) {
						setResult(RESULT_OK);
						finish();
					}
					else {
						d(jobj.getString("message"));
					}
				}
				catch(JSONException e) {d(e.toString());}
			}
			else {
				d(error);
			}
		}

		@Override
		protected Void doInBackground(Void[] p1)
		{
			// TODO: Implement this method
			try {
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setUseCaches(true);
				conn.setRequestMethod("POST");
				
				//conn.setDoOutput(true); 
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
				wr.write(data); 
				wr.flush();
				wr.close();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

				while((line = reader.readLine()) != null)
					sb.append(line + "\n");
				reader.close();

				data = sb.toString();
			}
			catch(UnknownHostException e) {error = "No internet connection";}
			catch(IOException e) {error = e.toString();}
			//catch(InterruptedException e) {error = e.toString();}
			return null;
		}

	}
	
}