package pstmproj;

public class PSTMSolution {

	public static void main(String args[]) {
		PSTM pstm = new PSTM();
		Agent[] students = pstm.students();
		Agent[] courses = pstm.courses();
		System.out.println("匹配过程:");
		while(pstm.check(students)) {
			pstm.matching();
		}
		System.out.println();
		System.out.println("匹配结果:");
		SymbolTable<Agent, Agent> matches = pstm.matchSet();
		pstm.checkMatches();
		for(Agent agent : students) {
			System.out.println(agent.name() + " vcapacity is : " + agent.vcapacity());
			
		}
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
