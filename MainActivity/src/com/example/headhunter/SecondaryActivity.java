package com.example.headhunter;

import java.util.regex.Pattern;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import android.text.util.Linkify;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLayoutChangeListener;
public class SecondaryActivity extends Activity {
	private byte set=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secondary);
		final TextView tvResume1 = (TextView)findViewById(R.id.tvResume1);
		final TextView tvResume2 = (TextView)findViewById(R.id.tvResume2);
		final EditText etResponce = (EditText)findViewById(R.id.etResponce);
		etResponce.setBackgroundColor(getResources().getColor(R.color.cetResponce));
		String sResume = getIntent().getExtras().getString("Resume");
		tvResume1.setText(sResume);
		tvResume2.setText("ФИО: Мамаев Александр Александрович\n"
        +"День рождения: 15/10/1990\n"
		+"Пол: Мужской\n"		
        +"Должность: Java Junior\n"
        +"Зарплата: 45000\n"
        +"Номер телефона: 89030163806\n"
        +"Электронная почта: aamamaev.post@gmail.com\n");
		Linkify.addLinks(tvResume1, Linkify.EMAIL_ADDRESSES|Linkify.PHONE_NUMBERS);
		Linkify.addLinks(tvResume2, Linkify.EMAIL_ADDRESSES|Linkify.PHONE_NUMBERS);
		tvResume1.setOnFocusChangeListener(new OnFocusChangeListener(){
		
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {tvResume1.setBackgroundColor(Color.GRAY);
				tvResume2.setBackgroundColor(Color.WHITE);
				set=1;}
				
				
						
			}});
		tvResume2.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {tvResume2.setBackgroundColor(Color.GRAY);
				tvResume1.setBackgroundColor(Color.WHITE);
				set=2;
				}
				
				
						
			}});
		
		
	}
	public void endActivity(String Resume, String Responce ){
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("Resume", Resume);
		intent.putExtra("Responce", Responce);
		setResult(RESULT_OK, intent);
	    finish();

	}
	public void bResponceClick(View v){
		final TextView tvResume1 = (TextView)findViewById(R.id.tvResume1);
		final TextView tvResume2 = (TextView)findViewById(R.id.tvResume2);
		final EditText etResponce= (EditText)findViewById(R.id.etResponce);
		switch (set){
		case 1:
			endActivity(tvResume1.getText().toString(),etResponce.getText().toString());
			break;
		case 2:
			endActivity(tvResume2.getText().toString(), etResponce.getText().toString());
			break;
		default:
			Toast.makeText(this, "Выберите кандидата для ответа.", Toast.LENGTH_LONG).show();
		}
		}
		
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_secondary, menu);
		return true;
	}
}
