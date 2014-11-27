package engine;

public class ComputerNextMoveAlgorithm{

	public static String bestMove(String FEN, String bscoreString, String searchDepthString, String maxPlyString) {
		ChessModule chessModule = new ChessModule();
		//Soc la ultima generació en poder gaudir dels jocs al carrer
		int bscore = Integer.parseInt(bscoreString);
		int searchDepth = Integer.parseInt(searchDepthString);
		int maxPly = Integer.parseInt(maxPlyString);
		return chessModule.bestMove(FEN, bscore, searchDepth,maxPly);
	}

}
