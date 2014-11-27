package kobi.chess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LastMoveExecDataActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.last_move_exec_data);
		
		Bundle b = getIntent().getExtras();

		((TextView) findViewById(R.id.estimatedAndroidTime)).setText(b.getString("estimatedAndroidTime"));
		((TextView) findViewById(R.id.estimatedOffloadingTime)).setText(b.getString("estimatedOffloadingTime"));
		((TextView) findViewById(R.id.estimatedServerTime)).setText(b.getString("estimatedServerRuntime"));
		String offloadingDone = b.getString("offloadingDone");
		((TextView) findViewById(R.id.offloadingDone)).setText(offloadingDone);
		if(offloadingDone.equals("Yes")) ((TextView) findViewById(R.id.offloadingOrAndroidTime)).setText(R.string.ServerEstimatedTime);
		else ((TextView) findViewById(R.id.offloadingOrAndroidTime)).setText(R.string.AndroidEstimatedTime);
		((TextView) findViewById(R.id.overallTime)).setText(b.getString("overallTime"));
		((TextView) findViewById(R.id.serverRealTime)).setText(b.getString("realServerTime"));
		
		Button back = (Button) findViewById(R.id.back_button_last_move_screen);
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent returnIntent = new Intent();
				setResult(RESULT_OK, returnIntent);
				finish();
			}

		});

	}
	
}
