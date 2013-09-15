package com.example.headhunter;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

import android.widget.LinearLayout;
import 	android.view.View;

public class MainActivity extends Activity {
	private static final int requestCodeResponce=1;
	private Integer NumberOfEntry =0;	
	
	public void sendClick(View v){
		if (isFildFull()){
		Intent intent = new Intent(this,SecondaryActivity.class);
		intent.putExtra("Resume", ResumeToString());
	    startActivityForResult(intent, requestCodeResponce);	    
	    }		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);        		 
		final EditText etPerson = (EditText)findViewById(R.id.etPerson); 
		final EditText etBirthday = (EditText)findViewById(R.id.etBirthday);
		final EditText etJobTitle = (EditText)findViewById(R.id.etJobTitle);
		
		//this.Test();
				
		etJobTitle.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String JobTitle = etJobTitle.getText().toString().trim();
				String FirstChar="";
				if (JobTitle.length()>0){
					FirstChar=JobTitle.substring(0, 1).toUpperCase();
					etJobTitle.setText(FirstChar+JobTitle.substring(1, JobTitle.length()));
				}			
			}});
		
		etPerson.setOnFocusChangeListener(new OnFocusChangeListener(){

		@Override
		public void onFocusChange(View v, boolean hasFocus) {				
				String Person = etPerson.getText().toString().trim();
				Person=Person.replaceAll("  ", " ");
				Integer Position =0;
				NumberOfEntry =0;
				while (Person.indexOf(" ", Position)!=-1) {
					NumberOfEntry++;
					Position=Person.indexOf(" ", Position)+1;
					if (NumberOfEntry == 3){
						Person=Person.substring(0, Position);				    	
					}
					etPerson.setText(Person);								
				};
				
		}});
				
		etPerson.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
			
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			/**
			 * Если первый символ строки строчная буква заменить на заглавную.
			 * Если предпоследний символ строки " " заменить последний символ на заглавную.
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {	
				if (etPerson.getText().length()>0) {
					String Person = etPerson.getText().toString();
					int length = Person.length();
					if (length==1) Person = Person.substring(0,1).toUpperCase(Locale.getDefault());
					else if (Person.substring(length-2, length-1).equals(" ")) Person=Person.substring(0, length-1)+Person.substring(length-1).toUpperCase(Locale.getDefault());
						
					etPerson.removeTextChangedListener(this);
					etPerson.setText(Person);
					etPerson.setSelection(Person.length());
					etPerson.addTextChangedListener(this);				
				}
				
			}});
				
		etBirthday.addTextChangedListener(new TextWatcher() {
			 private String current = "";
			 private String ddmmyyyy = "DDMMYYYY";
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().equals(current)) {
		            String clean = s.toString().replaceAll("[^\\d.]", "");
		            String cleanC = current.replaceAll("[^\\d.]", "");

		            int cl = clean.length();
		            int sel = cl;
		            for (int i = 2; i <= cl && i < 6; i += 2) {
		                sel++;
		            }
		            //Fix for pressing delete next to a forward slash
		            if (clean.equals(cleanC)) sel--;

		            if (clean.length() < 8){
		               clean = clean + ddmmyyyy.substring(clean.length());
		            }else{
		               //This part makes sure that when we finish entering numbers
		               //the date is correct, fixing it otherways
		               int day  = Integer.parseInt(clean.substring(0,2));
		               int mon  = Integer.parseInt(clean.substring(2,4));
		               int year = Integer.parseInt(clean.substring(4,8));
                     GregorianCalendar cal = new GregorianCalendar() ;
		               if(mon > 12) mon = 12;
		               cal.set(Calendar.MONTH, mon-1);
		               day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
		               year = (year<1900)?1900:(year>2013)?2013:year;
		               clean = String.format("%02d%02d%02d",day, mon, year);
		            }

		            clean = String.format("%s/%s/%s", clean.substring(0, 2),
		                clean.substring(2, 4),
		                clean.substring(4, 8));
		            current = clean;
		            etBirthday.setText(current);
		            etBirthday.setSelection(sel < current.length() ? sel : current.length());
				
			}
				
			}
		});
	
	
	
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		  super.onActivityResult(requestCode, resultCode, data);

		  switch (requestCode) {
		  case (requestCodeResponce): {
			  if (resultCode == Activity.RESULT_OK) {			  
			      String  Responce = data.getExtras().getString("Responce");
			      StringToResume(data.getExtras().getString("Resume"));
			      AlertDialog.Builder ad = new AlertDialog.Builder(this);
			      ad.setTitle("Ответ работодателя"); 
			      ad.setMessage(Responce); 
			      ad.show();
			  }			  		   
		  }
		  break;
		  default:		  
		  }	
	  }
	
    private static boolean isEmailValid(String email) {
	    
	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);	    
	    return pattern.matcher(email).matches();
	}

	
	private static boolean isPhoneNumberValid(String PhoneNumber){
		String expression = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);		
		return pattern.matcher(PhoneNumber).matches();
	}
	
    private boolean isFildFull(){
		final EditText etPerson = (EditText)findViewById(R.id.etPerson);
		etPerson.clearFocus();
		final EditText etBirthday = (EditText)findViewById(R.id.etBirthday);
		final EditText etJobTitle = (EditText)findViewById(R.id.etJobTitle);
		final EditText etSalary = (EditText)findViewById(R.id.etSalary);
		final EditText etPhoneNumber = (EditText)findViewById(R.id.etPhoneNumber);
		final EditText etEmail = (EditText)findViewById(R.id.etEmail);
    	if ((etPerson.length()==0) || (NumberOfEntry<2)) {
    		Toast.makeText(this," Поле  \"ФИО\" не заполнено", Toast.LENGTH_LONG).show();
    		return false;};
    	if (etBirthday.length()==0) {
    		Toast.makeText(this,"Поле \"День рождения\" не заполнено", Toast.LENGTH_LONG).show();
    		return false;};
    	if (etJobTitle.length()==0) {
    		Toast.makeText(this,"Поле \"Должность\" не заполнено", Toast.LENGTH_LONG).show();
    		return false;};
    	if (etSalary.length()==0) {
    		Toast.makeText(this,"Поле \"Зарплата\" не заполнено", Toast.LENGTH_LONG).show();
    		return false;};
    	if (!isPhoneNumberValid(etPhoneNumber.getText().toString())) {   		
        	Toast.makeText(this,"Поле \"Телефонный номер\" не заполнено", Toast.LENGTH_LONG).show();
        	return false;}	
    	if (!isEmailValid(etEmail.getText().toString())) {
    		Toast.makeText(this,"Поле \"Электронная почта\" не заполнено", Toast.LENGTH_LONG).show();
    		return false;};
    	return true;
    }
    
    private String ResumeToString(){
    	final EditText etPerson = (EditText)findViewById(R.id.etPerson); 
		final EditText etBirthday = (EditText)findViewById(R.id.etBirthday);
		final EditText etJobTitle = (EditText)findViewById(R.id.etJobTitle);
		final EditText etSalary = (EditText)findViewById(R.id.etSalary);
		final Spinner  spGender= (Spinner)findViewById(R.id.spGender);
		final EditText etPhoneNumber = (EditText)findViewById(R.id.etPhoneNumber);
		final EditText etEmail = (EditText)findViewById(R.id.etEmail);        
    	String sResume;
    	sResume ="ФИО: "+etPerson.getText().toString()+ "\n"
        +"День рождения: "+etBirthday.getText().toString()+"\n"
        +"Пол: "+ spGender.getSelectedItem().toString()+"\n"
        +"Должность: "+etJobTitle.getText().toString()+"\n"
        +"Зарплата: "+etSalary.getText().toString()+"\n" 
        +"Номер телефона: "+etPhoneNumber.getText().toString()+"\n"
        +"Электронная почта: "+etEmail.getText().toString()+"\n";
    	
    	return sResume;
    }
    
    private void StringToResume(String string){
    	final EditText etPerson = (EditText)findViewById(R.id.etPerson); 
		final EditText etBirthday = (EditText)findViewById(R.id.etBirthday);
		final EditText etJobTitle = (EditText)findViewById(R.id.etJobTitle);
		final EditText etSalary = (EditText)findViewById(R.id.etSalary);
		final Spinner  spGender= (Spinner)findViewById(R.id.spGender);
		final EditText etPhoneNumber = (EditText)findViewById(R.id.etPhoneNumber);
		final EditText etEmail = (EditText)findViewById(R.id.etEmail);   
		EditText[] array = {etPerson,etBirthday,etJobTitle,etSalary,etPhoneNumber,etEmail};
		int start=0;
	      int count=0;
	      int a=0;
	      while (string.indexOf(":", start)!=-1){
	    	if (count!=2) { 
	    	array[a].setText(string.substring(string.indexOf(":", start)+2, string.indexOf("\n", start))); 
	    	start=string.indexOf("\n", start)+1;
	    	a++;}
	    	else { 
	    		if (string.substring(string.indexOf(":", start)+2, string.indexOf("\n", start)).equals("Мужской"))spGender.setSelection(0);
	    		else spGender.setSelection(1);  
	    		start=string.indexOf("\n", start)+1;
	    	}
	    	count++;
	      }
    }

	private void Test(){
		final EditText etPerson = (EditText)findViewById(R.id.etPerson); 
		final EditText etBirthday = (EditText)findViewById(R.id.etBirthday);
		final EditText etJobTitle = (EditText)findViewById(R.id.etJobTitle);
		final EditText etSalary = (EditText)findViewById(R.id.etSalary);
		final EditText etPhoneNumber = (EditText)findViewById(R.id.etPhoneNumber);
		final EditText etEmail = (EditText)findViewById(R.id.etEmail);
		etPerson.setText("Пушкин Александр Сергеевич");
		etBirthday.setText("26/04/1799");
		etJobTitle.setText("Java Team Lead");
		etSalary.setText("200000");
		etPhoneNumber.setText("+7(707)034-38-06");
		etEmail.setText("push@mail.ru");
	}
	
		
}

