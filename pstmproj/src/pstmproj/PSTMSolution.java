package pstmproj;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

public class PSTMSolution {

	public static void main(String args[]) {

		PSTM pstm = new PSTM();
		PrintStream out = System.out;
		PrintStream printToFile = null;
		try {
			 printToFile = new PrintStream("result.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.setOut(printToFile);

		pstm.printPreference();

		System.setOut(out);

		System.out.println("Æ¥Åä¹ý³Ì:");
		while(pstm.check()) {
			pstm.matching();
		}
		System.out.println();
		System.out.println("Æ¥Åä½áÊø¡£¡£¡£");

		System.setOut(printToFile);
		pstm.checkMatches();
//		for(Agent agent : students) {
//			if(agent == null) continue;
//			System.out.println(agent.name() + " vcapacity is : " + agent.vcapacity());
//
//		}
		/*
		for(Agent agent : students) {
			for(Agent a : agent.matches()) {
				if(a != null) System.out.println(agent.name() + " <---> " + a.name());
			}
		}
		for(Agent agent : courses) {
			for(Agent a : agent.matches()) {
				if(a != null) System.out.println(agent.name() + " <---> " + a.name());
			}
		}	*/
	}
}
