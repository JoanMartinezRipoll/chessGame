/*
 * Created on Apr 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package engine;

import chesspresso.position.*;
import chesspresso.*;
import chesspresso.move.*;
import java.util.*;
import de.fuberlin.offloading.Engine;
import de.fuberlin.offloading.Algorithms.AlgName;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SimpleEngine implements ChessEngine {

	private Position position;
	private int ply;
	public final static int MATE = 50000;
	public final static int INF = 100000;
	private int bscore;
	//private OpeningDB book;
	private Engine offloadingEngine;
		
	//My things


	public synchronized String lastMove() {
		return Move.getString(position.getLastShortMove());
	}
	public SimpleEngine() {
		reset();
		// load the book
		//book = ChessVisualizationTrainer.book;
	}
	
	public void setOffloadingEngine ( Engine offloadingEngine){
		this.offloadingEngine = offloadingEngine;
	}
	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#reset()
	 */
	public synchronized void reset() {
		position = Position.createInitialPosition();
		ply = 0;
	}
	
	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#go()
	 */
	
	public synchronized String go(int searchDepth, int maxPly) {
		if (isDraw()) {
			return "DRAW";
		} else if (isMate()) {
			return "CHECKMATE";
		} else {
			// check the opening book first
//			Short mv = null;
//			if (book != null) {
//				mv = book.getMove(position);
//			}
//			if (mv != null) {
//				short move = mv.shortValue();
//				try {
//					position.doMove(move);
//				} catch (Exception e) {
//					return "ERROR: " + e;
//				}
//				ply++;
//				return Move.getString(move) + " (book)";
//			} else 
			{
				// use this to reward certain positional characteristics for the
				// computer
				//System.err.println("Computer white = " + computerIsWhite);
				//System.out.println("The FEN is"+position.getFEN() + " the bscore is" + bscore);
				String result = offloadingEngine.execute(AlgName.bestMove, position.getFEN(), Integer.toString(bscore),Integer.toString(searchDepth),Integer.toString(maxPly));
				short move = Short.parseShort(result.split(",")[1]);
				bscore = Integer.parseInt(result.split(",")[0]);
				try {
					position.doMove(move);
				} catch (Exception e) {
					return "ERROR: " + e;
				}
				ply++;
				if ((position.getToPlay() == Chess.WHITE)) {
					// so it means it was blacks turn to play before
					// so negate the score
				
				}
				ply++;
				if ((position.getToPlay() == Chess.WHITE)) {
					// so it means it was blacks turn to play before
					// so negate the score
					bscore = -bscore;
				}
				String retval = Move.getString(move);
				if (isDraw()) {
					retval = retval + " DRAW";
				} else if (isMate()) {
					retval = retval + " MATE";
				}
				return retval;
//				return retval
//					+ " ("
//					+ " deletedbyMe"
//					+ " nodes, eval="
//					+ bscore
//					+ ")";
			}
		}
	}

	// Alpha Beta window
	
	public final int SEARCH_DEPTH = 10; // 4 = 1 ply
	public final int MAX_PLY = 6;

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#makeMove(java.lang.String)
	 */
	public synchronized String makeMove(String move) {
		short m = parseMove(move);
		if (m == Move.ILLEGAL_MOVE) {
			return "ERROR: illegal move";
		}
		try {

			position.doMove(m);

		} catch (Exception e) {
			return "ERROR: " + e;
		}
		ply++;
		return Move.getString(m);
	}

	public synchronized short parseMove(String move) {
		move = move.trim();
		String alt1, alt2;
		if ((move.length() < 4) || move.startsWith("O")) {
			alt1 = move;
			alt2 = move;
		} else {
			alt1 = move.substring(0, 2) + "-" + move.substring(2);
			alt2 = move.substring(0, 2) + "x" + move.substring(2);
		}
		short moves[] = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			String temp = Move.getString(moves[i]);
			if (temp.equalsIgnoreCase(move)
				|| temp.equalsIgnoreCase(alt1)
				|| temp.equalsIgnoreCase(alt2)) {
				return moves[i];
			}
		}
		return Move.ILLEGAL_MOVE;
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#isWhiteTurn()
	 */
	public synchronized boolean isWhiteTurn() {
		//return Position.isWhiteToPlay(position.getHashCode());
		//return (position.getPlyNumber()%2)==0;
		return (position.getToPlay() == Chess.WHITE);
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#isDraw()
	 */
	public synchronized boolean isDraw() {
		return position.isStaleMate();
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#isMate()
	 */
	public synchronized boolean isMate() {
		return position.isMate();
	}
	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#getBoard(int)
	 */
	public synchronized char[][] getBoard(int ply) {

		for (int k = 0; k < ply; k++) {
			position.undoMove();
		}
		char[][] retval = new char[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int piece = position.getStone((i * 8) + j);
				switch (piece) {
					case Chess.BLACK_BISHOP :
						retval[i][j] = 'b';
						break;
					case Chess.WHITE_BISHOP :
						retval[i][j] = 'B';
						break;
					case Chess.BLACK_KNIGHT :
						retval[i][j] = 'n';
						break;
					case Chess.WHITE_KNIGHT :
						retval[i][j] = 'N';
						break;
					case Chess.BLACK_ROOK :
						retval[i][j] = 'r';
						break;
					case Chess.WHITE_ROOK :
						retval[i][j] = 'R';
						break;
					case Chess.BLACK_QUEEN :
						retval[i][j] = 'q';
						break;
					case Chess.WHITE_QUEEN :
						retval[i][j] = 'Q';
						break;
					case Chess.BLACK_KING :
						retval[i][j] = 'k';
						break;
					case Chess.WHITE_KING :
						retval[i][j] = 'K';
						break;
					case Chess.BLACK_PAWN :
						retval[i][j] = 'p';
						break;
					case Chess.WHITE_PAWN :
						retval[i][j] = 'P';
						break;
					default :
						retval[i][j] = ' ';
				}
			}
		}
		for (int k = 0; k < ply; k++) {
			position.redoMove();
		}
		return retval;
	}
	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#currentPly()
	 */
	public int currentPly() {
		return ply;
	}

	public int[] getBoard(char[][] ply) {
		int[] res = new int[9];
		return res;
	}
	public boolean isCheck() {
		return position.isCheck();
	}
	/**
	 * returns all possible moves
	 * @return
	 */
	public String[] getAllMoves()
	{
		Vector<String> vector = new Vector<String>();
		short[] moves = position.getAllMoves();
		
		for (int i = 0; i < moves.length; i++)
		{
			vector.add(Move.getString(moves[i]));
		}
		
		return (String[]) vector.toArray();
	}
	
	
	
}