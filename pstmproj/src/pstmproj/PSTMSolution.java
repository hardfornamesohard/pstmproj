package pstmproj;

public class PSTMSolution {

	public static void main(String args[]) {
		PSTM pstm = new PSTM();
		Agent[] students = pstm.students();
		Agent[] courses = pstm.courses();
		while(pstm.check(students)) {
			pstm.matching();
		}
		for(Agent agent : students) {
			for(Agent a : agent.matches()) {
				if(a != null) System.out.println(agent.name() + "<--->" + a.name());
			}
		}
		for(Agent agent : courses) {
			for(Agent a : agent.matches()) {
				if(a != null) System.out.println(agent.name() + "<--->" + a.name());
			}
		}	
	}
}
