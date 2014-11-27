package kobi.chess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class NewGameActivity extends Activity {
	
	int maxPly;
	int searchDepth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_game_options);
	
		Bundle b = getIntent().getExtras();

		RadioGroup rg = (RadioGroup) findViewById(R.id.dificultiesOptions);
		rg.check(R.id.easy);
		
		Button back = (Button) findViewById(R.id.back_button_engine_values_screen);
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent returnIntent = new Intent();
				returnIntent.putExtra("maxPly",maxPly);
				returnIntent.putExtra("searchDepth", searchDepth);
				setResult(RESULT_OK, returnIntent);
				finish();
			}

		});

	}
	
    public void onRadioButtonClicked(View view){
       	RadioButton rbhard = ((RadioButton) findViewById(R.id.hard));
       	RadioButton rbeasy = ((RadioButton) findViewById(R.id.easy));
    	
       	if (rbhard.isChecked()) { maxPly=6;searchDepth=10;}
       	else if (rbeasy.isChecked()){ maxPly=0;searchDepth=0;}
    	else {maxPly=1;searchDepth=1;}
   		
    }
}
