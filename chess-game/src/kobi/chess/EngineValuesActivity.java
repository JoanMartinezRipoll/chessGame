package kobi.chess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EngineValuesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.engine_values);
		
		Bundle b = getIntent().getExtras();
		
		String speedRelation = b.getString("speedRelationValue"); //Your id
		String ping = b.getString("ping"); //Your id
		String connectionAvailable = b.getString("connectionAvailable");
		String serverAvailable = b.getString("serverAvailable");
		String connectionType = b.getString("connectionType");
		
		((TextView) findViewById(R.id.speedRelationValue)).setText(speedRelation);
		((TextView) findViewById(R.id.pingValue)).setText(ping);
		((TextView) findViewById(R.id.connectionAvailableValue)).setText(connectionAvailable);
		((TextView) findViewById(R.id.serverAvailableValue)).setText(serverAvailable);
		((TextView) findViewById(R.id.connectionTypeValue)).setText(connectionType);

		
		Button back = (Button) findViewById(R.id.back_button_engine_values_screen);
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}

		});

	}

	private static double round(double nD, int nDec) {
		return Math.round(nD*Math.pow(10,nDec))/Math.pow(10,nDec);
	}

}