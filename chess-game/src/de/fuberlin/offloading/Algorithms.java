package de.fuberlin.offloading;

import android.content.Context;
import chesspresso.position.Position;

public class Algorithms {
//Do not remove the "doSomeLoops" and "fileAndLoops" related code, it is necessary for our offloading engine to work properly
	
	public static final int MAX_REPETITIONS = 20;
	
	private static Engine offloadingEngine = null;
	private static DataBaseHelper dbHelper = null;
	private static long currentAlgInputRep = -1;
	
	//Add the name of your algorithms to this enumeration
	public static enum AlgName {
		doSomeLoops,
		fileAndLoops,
		bestMove
	}
	
	public static void setOffloadingEngine(Engine engine) {
		Algorithms.offloadingEngine = engine;
	}
	
	public static String executeLocally(AlgName algName, String... parameters) {
		switch (algName) {
		
		case doSomeLoops:
			long nLoops = Long.parseLong(parameters[0]); //Parsing of the input parameters
			return doSomeLoops(nLoops); //No casting needed of the output result, it is already a String
			
		case fileAndLoops:
			long nLoops2 = Long.parseLong(parameters[0]); //Parsing of the input parameters
			//Parameters[1] is a file encoded as a String with Base64
			//We only want to test that it was correctly received by returning its size, so no parsing is needed
			int fileLength = fileAndLoops(nLoops2, parameters[1]);
			return Integer.toString(fileLength); //In this case, the output parameter is an Integer so casting to String is needed
		
		case bestMove:
			return engine.ComputerNextMoveAlgorithm.bestMove(parameters[0],parameters[1],parameters[2],parameters[3]);
		default:
			return "Error";
		}
	}

	//Returns the predicted number of low level instructions of the algName algorithm for a given input
	public static double getCost(AlgName algName, String... parameters) {
		switch (algName) {
		
		case doSomeLoops:
			long nLoops = Long.parseLong(parameters[0]); //Parsing of the input parameters
			return doSomeLoopsCost(nLoops);
			
		case fileAndLoops:
			long nLoops2 = Long.parseLong(parameters[0]); //Parsing of the input parameters
			//We have a File encoded as a String in parameters[1], but as fileAndLoops actually behaves like doSomeLoops, we don't need this parameter in order to estimate the cost
			return fileAndLoopsCost(nLoops2);
			
		case bestMove:
			int depth = Integer.parseInt(parameters[2]);
			int max_ply = Integer.parseInt(parameters[3]);
			Position pos =  new Position (parameters[0], true);
			currentAlgInputRep = getID(pos, max_ply, depth);//System.out.println("getting cost with maxply " +  max_ply + " and depth" + depth );
			break;
		default:
			return -1.0;
		}
		return estCostWithDB(algName);
	}
	
	//No problem is actually there is no costs DB
	public static void loadAlgCostsDB(Context appContext) {
		dbHelper = new DataBaseHelper(appContext);
		if (dbHelper.isDbInAssets()) {
			//If it has not been done before, copy the DB from the "assets" folder to the "data" folder
			dbHelper.createDataBase();
			//Open the database (we'll keep it open in OPEN_READWRITE mode until onDestroy of the main Activity)
			dbHelper.openDataBase();
		}
	}
	
	//No problem if actually there is no costs DB
	public static void closeAlgCostsDB() {
		dbHelper.close();
	}
	
	public static boolean isAlgInCostsDB(AlgName algName) {
		if (!dbHelper.isDbInAssets()) return false;
		else {
			if (dbHelper.existsAlg(algName.toString())) return true;
			else return false;
		}
	}
	
	private static double estCostWithDB(AlgName algName) {
		double estRunTimeMs = dbHelper.getRuntime(algName.toString(), currentAlgInputRep, offloadingEngine.getCsrFromAlg(algName));
		return estRunTimeMs*Engine.SERVER_INST_MS;
	}
	
	public static void updateCostsDB(AlgName algName, double runtime, boolean serverGen) {
		dbHelper.insertRow(algName.toString(), currentAlgInputRep, runtime, serverGen);
		float recentCsr = dbHelper.getCsr(algName.toString(), currentAlgInputRep, offloadingEngine.getCsrFromAlg(algName));
		if (recentCsr != -1.0) offloadingEngine.updateCsr(algName, recentCsr);
	}

	private static String doSomeLoops(long nLoops) {
		long i = 0;
		while (i < nLoops) i++;
		return "Done";
	}

	private static double doSomeLoopsCost(long nLoops) {
		return nLoops * 5.0;
	}
	
	private static int fileAndLoops(long nLoops, String fileContents) {
		long i = 0;
		while (i < nLoops) i++;
		return fileContents.length();
	}
	
	private static double fileAndLoopsCost(long nLoops) {
		return nLoops * 5.0;
	}
	
	private static long getID(Position position, int max_ply, int depth){
		Long boardID = position.getHashCode();
		String stringRep = Long.toBinaryString(boardID);
		int i = stringRep.length();
		while( i < 63) {
			stringRep = "0"+stringRep;
			i++;
		}
		String shiftedRep = stringRep.substring(0, stringRep.length()-2);
		if (max_ply==0 && depth ==0) shiftedRep = "00" + shiftedRep;
		else if (max_ply==1 && depth == 1) shiftedRep = "01" + shiftedRep;
		else shiftedRep = "10" + shiftedRep;
		return Long.parseLong(shiftedRep,2);
		
//		String finalRep = Long.toString((Long.parseLong(shiftedRep,2)));
//		i=finalRep.length();
//		while (i<19) {
//			finalRep = "0"+finalRep;
//			i++;
//		}
//		return finalRep;	
	}

}
