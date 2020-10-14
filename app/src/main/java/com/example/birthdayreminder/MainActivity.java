package com.example.birthdayreminder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	//年、月、日、年齢
	private int year, month, day, age;

	//誕生日までの残り日数を表示するTextView
	private TextView dispView;

	//年齢を表示するTextView
	private TextView ageView;

	//デフォルトのメッセージ
	private static final String DEFAULT_MSG = "誕生日を設定してください";

	//プレファレンスファイル名
	private static final String PREFS_FILE ="MyPrefsFile";

	//プレファレンスのためのキー
	private static final String YEAR = "YEAR";
	private static final String MONTH = "MONTH";
	private static final String DAY = "DAY";

	//「クリア」ボタンのイベントリスナー
	public class ClearButtonClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v){
			//プレファレンスをクリア
			SharedPreferences prefs = getSharedPreferences(PREFS_FILE, Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.clear();
			editor.commit();
			dispView.setText(DEFAULT_MSG);
			ageView.setText("");
			year = month = day= 0;
		}
	}

	//「誕生日設定」ボタンのイベントリスナー
	public class SetButtonClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v){
			new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					MainActivity.this.year = year;
					month = monthOfYear;
					day = dayOfMonth;
					showResult();
					//データを保存
					savePrefs();
				}
			}, 1979, 6, 3).show();
		}
	}

	//結果を表示する
	private void showResult(){
		String dStr = String.format("%04d/%02d/%02d", year, month + 1, day);
		dispView.setText(dStr);

		//現在の日時を表すCalendarオブジェクトを生成
		Calendar now = Calendar.getInstance();

		//誕生日を管理するCalendarオブジェクトを生成
		Calendar birthday = (Calendar) now.clone();
		birthday.set(year, month, day);

		//年齢を求める
		age = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR) - 1;

		//birthdayを今年の誕生日に設定
		int thisYear = now.get(Calendar.YEAR);
		birthday.set(Calendar.YEAR, thisYear);

		if (now.after(birthday)) {
			//誕生日が過ぎていれば来年に
			birthday.add(Calendar.YEAR, 1);

			//年齢を1増やす
			age += 1;
		} else if (now.equals(birthday)) {
			//今日が誕生日であれば年齢を1増やす
			age += 1;
		}

		//今年の誕生日までの日数を調べる
		int diff = (int) ((birthday.getTimeInMillis() - now.getTimeInMillis()) / (1000 * 60 * 60 * 24));
		String dispStr = "誕生日まであと" + Integer.toString(diff) + "日";

		//日数を表示する
		dispView.setText(dispStr);

		//年齢を表示する
		ageView.setText(age + "才");
	}

	// プレファレンスに保存する
	private void savePrefs(){
		SharedPreferences prefs = getSharedPreferences(PREFS_FILE, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(YEAR, year);
		editor.putInt(MONTH, month);
		editor.putInt(DAY, day);
		editor.commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//「誕生日設定」ボタンのイベントリスナーを設定
		Button setDateBtn = (Button) findViewById(R.id.setDateBtn);
		setDateBtn.setOnClickListener(new SetButtonClickListener());

		//「クリア」ボタンのイベントリスナーを設定
		Button clearBtn = (Button) findViewById(R.id.clearBtn);
		clearBtn.setOnClickListener(new ClearButtonClickListener());

		//残り日数を表示するTextView
		dispView = (TextView) findViewById(R.id.dispView);

		//年齢を表示するTextView
		ageView = (TextView) findViewById(R.id.ageView);

		//プレファレンスからデータを読み込む
		SharedPreferences prefs = getSharedPreferences(PREFS_FILE, Activity.MODE_PRIVATE);
		year = prefs.getInt(YEAR, 0);
		month = prefs.getInt(MONTH, 0);
		day = prefs.getInt(DAY, 0);
	}

	@Override
	protected void onResume(){
		super.onResume();
		if (year != 0){
			//読み込んだ値を表示
			showResult();
		}else {
			//メッセージを表示
			dispView.setText(DEFAULT_MSG);
		}
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu){
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		//Handle action bar item clicks here.The action bar will automatically handle
		// clicks on the Home/Up button, so long as you specify a parent
		//activity in AndroidManifest.xml.
		int id = item.getItemId();

		//no inspection SimplifiableStatement
		if (id == R.id.action_settings){
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	*/

}