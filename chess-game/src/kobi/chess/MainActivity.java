package kobi.chess;

/*
 Copyright 2011 by Kobi Krasnoff

 This file is part of Pocket Chess.

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import engine.ChessEngine;
import engine.SimpleEngine;

import de.fuberlin.offloading.Engine;
import de.fuberlin.offloading.Algorithms.AlgName;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;

public class MainActivity extends Activity {
	public static final int ACITIVITY_OPTIONS = 1;
	public static final int ACITIVITY_HELP = 0;
	private static final int ENGINE_VALUES = 2;
	private static final int START_NEW_GAME = 3;
	public static final int LAST_MOVE_EXECUTION_DATA = 0;
	private BoardView boardView;
	private Point startPoint, endPoint, movePoint;
	private TextView txtStatus;

	private ChessEngine engine;
	private Engine offloadingEngine;
	private int ply = 0;
	private boolean moveEnabled = false;   
	private int searchDepth;
	private int maxPly;





	/**
	 * Constructor class
	 */
	public MainActivity()
	{
		engine = new SimpleEngine();
		startPoint = new Point();
		endPoint = new Point();
		movePoint = new Point();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// sets textView
		txtStatus = (TextView)this.findViewById(R.id.txtStatus);



		// sets view   
		final BoardView boardView = (BoardView)findViewById(R.id.BoardView);
		boardView.setFocusableInTouchMode(true);
		boardView.setFocusable(true);
		boardView.syncParent(this);
		this.boardView = boardView;

		this.boardView.displayPieces(engine.getBoard(ply));

		boardView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				if (engine.isWhiteTurn())
				{
					switch (event.getAction())
					{
					case MotionEvent.ACTION_DOWN:
						startPoint.x = (int)(event.getX() / boardView.getWidth() * 8);
						startPoint.y = (int)(event.getY() / boardView.getHeight() * 8);
						startPoint.fx = event.getX();
						startPoint.fy = event.getY();

						char temp = getSelectedPiece(engine.getBoard(ply), startPoint.x, startPoint.y);
						if (temp == 'P' || temp == 'K' || temp == 'Q' || temp == 'R' || temp == 'N' ||  temp == 'B')
						{
							moveEnabled = true;
						}

						else
							moveEnabled = false;

						if (moveEnabled)
							boardView.getMovePoint(startPoint, movePoint);
						break;
					case MotionEvent.ACTION_MOVE:
						movePoint.fx = event.getX();
						movePoint.fy = event.getY();
						if (moveEnabled)
							boardView.getMovePoint(startPoint, movePoint);
						break;
					case MotionEvent.ACTION_UP:
						endPoint.x = (int)(event.getX() / boardView.getWidth() * 8);
						endPoint.y = (int)(event.getY() / boardView.getHeight() * 8);

						if (moveEnabled)
						{
//							if(!(startPoint.x==endPoint.x&&startPoint.y==endPoint.y)){
//								writeToFile("WhiteStart: x = "+startPoint.x+" y = " + startPoint.y );
//								writeToFile("WhiteEnd: x = "+endPoint.x+" y = " + endPoint.y );
//							}	
							boardView.getUpPoint();
							
							PlayerMove(setMove());
						}

						break;
					}
				}

				return true;
			}



		});

		searchDepth=0;
		maxPly=0;
		offloadingEngine = new Engine(this.getApplicationContext());
		engine.setOffloadingEngine(offloadingEngine);
		
		//writeToFile("GAME STARTED at ON_CREATE with sDepth " + searchDepth + "and Maxply "+maxPly);
	}

	private void restartGame(){


		//	boardView;
		// startPoint, endPoint, movePoint;
		// txtStatus;
		engine.reset();
		ply = 0;
		this.boardView.displayPieces(engine.getBoard(ply));
		// moveEnabled = false;   
	
		//writeToFile("GAME RESTARTED at RESTARTGAME with sDepth " + searchDepth + "and Maxply "+maxPly);
		
	}

	
	private void PlayerMove(String move)
	{

		char selectedPiece = getSelectedPiece(engine.getBoard(ply), move);

		if (move.charAt(move.length() - 1) == '8' && selectedPiece == 'P')
		{
			showPromotionPieceDialog(move);
			return;
		}

		char[][] myBoard = engine.getBoard(ply);


		if (move.compareTo("e1-g1") == 0 && myBoard[7][4] == 'k')
		{
			move = "O-O";
		}
		else if ((move.compareTo("e1-b1") == 0 || move.compareTo("e1-c1") == 0) && myBoard[7][4] == 'k')
		{
			move = "O-O-O";
		}


		String res = engine.makeMove(move);
		if (res.startsWith("ERROR")) {
			String res2 = engine.makeMove(move.replace('-', 'x'));
			if (res2.startsWith("ERROR")) {
				if (!(engine.isDraw() || engine.isMate()))
					showToastNotification(this.getString(R.string.illegal_move_try_again));
				else if (engine.isMate())
					showToastNotification(this.getString(R.string.activity_mate));
				else if (engine.isDraw())
					showToastNotification(this.getString(R.string.activity_draw));
			}
			else
			{
				makeComputerMove();
			}

		}
		else 
		{
			makeComputerMove();
		}

		//return res;
	}


	@Override
	public void onPause() {
		super.onPause();
		offloadingEngine.savePersistentParams();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		offloadingEngine.unregisterBroadcastReceivers();
	}
	/**
	 * gets my chosen piece name.
	 */
	private char getSelectedPiece(char[][] bitBoard, String pgnLocation)
	{
		int x = 0;
		int y = 0;

		char[] myPGN =  pgnLocation.toCharArray();

		y = 8 - (int)(myPGN[1] - 48);
		switch (myPGN[0])
		{
		case 'a': x = 0; break;
		case 'b': x = 1; break;
		case 'c': x = 2; break;
		case 'd': x = 3; break;
		case 'e': x = 4; break;
		case 'f': x = 5; break;
		case 'g': x = 6; break;
		case 'h': x = 7; break;
		}

		return getSelectedPiece(bitBoard, x, y);
	}


	/**
	 * gets my chosen piece name.
	 */
	private char getSelectedPiece(char[][] bitBoard, int x, int y)
	{
		return bitBoard[7-y][x];
	}


	private void makeComputerMove()
	{
	
		this.boardView.displayPieces(engine.getBoard(ply));
		//mMyHandler.machineGO();
		threadMove();
		txtStatus.setText(R.string.activity_thinking);
		//new ComputerMoveTask().execute();

	}

	public void threadMove() {
		new Thread(new Runnable() {
			public void run() {
				boardView.post(new Runnable() {
					public void run() {
						computerMove();
					}
				});
			}
		}).start();

		/*runOnUiThread(new Runnable() {
			public void run() {
				computerMove();
			}
		});*/

	}



	/**
	 * Actually handle the computer move.
	 */
	private synchronized void computerMove()
	{
		String move = engine.go(searchDepth, maxPly);
		this.boardView.displayPieces(engine.getBoard(ply));
		txtStatus.setText(R.string.activity_yourMove);

		if (engine.isMate())
			showToastNotification(this.getString(R.string.activity_mate));
		else if (engine.isCheck())
			showToastNotification(this.getString(R.string.activity_check));
		else if (engine.isDraw())
			showToastNotification(this.getString(R.string.activity_draw));
	}

	/**
	 * returns move in the right engine format
	 * @return
	 */
	private String setMove()
	{
		String res = "";

		res = res + String.valueOf((char)(startPoint.x + 97));
		res = res + String.valueOf(8 - startPoint.y);
		res = res + "-";
		res = res + String.valueOf((char)(endPoint.x + 97));
		res = res + String.valueOf(8 - endPoint.y);

		return res;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Bundle b;
		switch (item.getItemId())
		{
		case R.id.engineValues:
	
			b = setEngineValues();
			Intent myIntent4 = new Intent(this.getBaseContext(), EngineValuesActivity.class);
			myIntent4.putExtras(b);
			startActivityForResult(myIntent4, ENGINE_VALUES);
			break;

		case R.id.lastMoveExecData:
			
			b = setLastMoveValues();
			Intent myIntent1 = new Intent(this.getBaseContext(), LastMoveExecDataActivity.class);
			myIntent1.putExtras(b);
			startActivityForResult(myIntent1, LAST_MOVE_EXECUTION_DATA);
			break;
		
		case R.id.startGame:
			Intent myIntent5 = new Intent(this.getBaseContext(), NewGameActivity.class);
			//Intent myIntent3 = new Intent(this.getBaseContext(), GNUActivity.class);
			startActivityForResult(myIntent5, START_NEW_GAME);
			break;

			//		case R.id.Copyright:
			//			Intent myIntent3 = new Intent(this.getBaseContext(), CopyrightActivity.class);
			//			//Intent myIntent3 = new Intent(this.getBaseContext(), GNUActivity.class);
			//			startActivityForResult(myIntent3, ACITIVITY_HELP);
			//			break;
		case R.id.help:
			Log.e("pointerSquare", "MENU_HELP");
			Intent myIntent2 = new Intent(this.getBaseContext(), InstructionsActivity.class);
			startActivityForResult(myIntent2, ACITIVITY_HELP);
			break;
//		case R.id.about:
//			Log.e("pointerSquare", "MENU_ABOUT");
//			new AlertDialog.Builder (MainActivity.this)
//			.setTitle (R.string.dialog_alert)
//			.setMessage (R.string.dialog_message)
//			.setIcon(R.drawable.king_white)
//			.setPositiveButton (R.string.dialog_ok_button, new DialogInterface.OnClickListener(){
//				public void onClick (DialogInterface dialog, int whichButton){
//					setResult (RESULT_OK);
//
//				}
//			})
//			.show ();  
//			break;
//		case R.id.exit:
//			Log.e("pointerSquare", "MENU_EXIT");
//			this.finish();
//			break;
		default:
			Log.e("pointerSquare", String.valueOf(item.getItemId()));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private Bundle setLastMoveValues() {
		
		Bundle b = new Bundle();
		if (offloadingEngine.getEstAndroidRuntime() == -1) b.putString("estimatedAndroidTime","Not calculated");
		else b.putString("estimatedAndroidTime",Double.toString(round(offloadingEngine.getEstAndroidRuntime(), 7)));
		
		if (offloadingEngine.getEstOffloadingTime() == -1) b.putString("estimatedOffloadingTime","Not calculated");
		else b.putString("estimatedOffloadingTime",Double.toString(round(offloadingEngine.getEstOffloadingTime(), 7)));
		
		if (offloadingEngine.getEstServerRuntime() == -1) b.putString("estimatedServerRuntime","Not calculated");
		else b.putString("estimatedServerRuntime",Double.toString(round(offloadingEngine.getEstServerRuntime(), 7)));
		
		if (offloadingEngine.isOffloadingDone()) b.putString("offloadingDone","Yes");
		else b.putString("offloadingDone","No");
		
		b.putString("overallTime", Double.toString(round(offloadingEngine.getOverallTime(), 7)));
		
		if (offloadingEngine.getRealServerTime() == -1)	b.putString("realServerTime","None");	
		else b.putString("realServerTime", Double.toString(round(offloadingEngine.getRealServerTime(), 7)));
		
		return b;
	}

	private Bundle setEngineValues() {
		Bundle b = new Bundle();
		
		if (offloadingEngine.getCsrFromAlg(AlgName.bestMove)==-1) b.putString("speedRelationValue","Not calculated");
		b.putString("speedRelationValue", Double.toString(round(offloadingEngine.getCsrFromAlg(AlgName.bestMove),7)));
		b.putString("ping", Double.toString(round(offloadingEngine.getPing(),7)));
		if (offloadingEngine.isConnectionAvailable()) b.putString("connectionAvailable", "Yes");
		else b.putString("connectionAvailable", "No");
		if (offloadingEngine.isServerAvailable()) b.putString("serverAvailable","Yes");
		else b.putString("serverAvailable","No");
		b.putString("connectionType",offloadingEngine.getConnType());
		return b;
	}

	/**
	 * Show system alerts
	 * @param text
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		
		if (requestCode == START_NEW_GAME) {

			if(resultCode == RESULT_OK){      
				maxPly=data.getIntExtra("maxPly", -1);
				searchDepth=data.getIntExtra("searchDepth", -1);
				restartGame();

			}
			if (resultCode == RESULT_CANCELED) {    
				//Write your code if there's no result
			}
		}
	}
	public void showToastNotification(String text)
	{
		Context context = this.getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
		toast.show();
	}

	public Point getStartPoint()
	{
		return startPoint;
	}

	public Point getEndPoint()
	{
		return endPoint;
	}

	/**
	 * shows dialog of promotion tools
	 */
	public void showPromotionPieceDialog(String move)
	{

		final CharSequence[] items = {this.getString(R.string.showPromotionPieceDialog_queen), this.getString(R.string.showPromotionPieceDialog_rook), this.getString(R.string.showPromotionPieceDialog_bishop), this.getString(R.string.showPromotionPieceDialog_knight)};

		final String move2 = move;

		new AlertDialog.Builder (MainActivity.this)
		.setTitle(this.getString(R.string.showPromotionPieceDialog_title))
		.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0)
				{
					PlayerMove(move2 + "Q");
				}
				else if (item == 1)
				{
					PlayerMove(move2 + "R");
				}
				else if (item == 2)
				{
					PlayerMove(move2 + "B");
				}
				else if (item == 3)
				{
					PlayerMove(move2 + "N");
				}
			}
		})
		.show();


	}
	
	 public static void writeToFile(String data){
		 	String path = new File(Environment.getExternalStorageDirectory(), "chessData.csv").getPath();
			System.out.println(path);		
			 	try {
		        	 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)));
		        	    out.println(data);
		        	    out.close();
		        }
		        catch (IOException e) {
		           	System.out.println("Data was NOT saved");
		            Log.e(MainActivity.class.getName(), "File write failed: " + e.toString());
		        } 
		         
		    }
	
	private static double round(double nD, int nDec) {
		  return Math.round(nD*Math.pow(10,nDec))/Math.pow(10,nDec);
		}

}