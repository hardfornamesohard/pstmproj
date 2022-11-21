package pstmproj;

import util.SymbolTable;

public class PSTM {
	private Agent[] students;
	private Agent[] courses;
	//��ǰ��ƥ��Լ��ϣ�key��course��value��course-student
	private SymbolTable<Agent, Agent[]> matches;
	public PSTM() {
		initAgents();
		initPreference();
		matches = new SymbolTable<Agent, Agent[]>();
	}
	public void initAgents() {
		students = new Agent[4];
		courses = new Agent[4];
		students[0] = new Agent("s1", 1);
		students[1] = new Agent("s2", 2);
		students[2] = new Agent("s3", 1);
		students[3] = new Agent("s4", 1);
		courses[0] = new Agent("c1", 1);
		courses[1] = new Agent("c2", 2);
		courses[2] = new Agent("c3", 1);
		courses[3] = new Agent("c4", 2);
		for(Agent course : courses) {
			course.increaseMax();
		}
	}
	public void initPreference()
	{
		Agent [][] pre1 = {{courses[0]}, {courses[3]}, {courses[2]}, {courses[1]}};
		students[0].addPre(pre1);
		Agent [][] pre2 = {{courses[0]}, {courses[2]}, {courses[3]}, {courses[1]}};
		students[1].addPre(pre2);
		Agent [][] pre3 = {{courses[3]}, {courses[0]}, {courses[2],courses[0]}, {courses[1]}};
		students[2].addPre(pre3);
		Agent [][] pre7 = {{courses[0]}, {courses[2]}, {courses[1]}, {courses[3]}};
		students[3].addPre(pre7);
		Agent [][] pre4 = {{students[3]}, {students[0]}, {students[2]}, {students[1]}};
		courses[0].addPre(pre4);
		Agent [][] pre5 = {{students[0]}, {students[3]}, {students[2]}, {students[1]}};
		courses[1].addPre(pre5);
		Agent [][] pre6 = {{students[0]}, {students[2]}, {students[3]}, {students[1]}};
		courses[2].addPre(pre6);
		Agent [][] pre8 = {{students[0]}, {students[2]}, {students[1]}, {students[3]}};
		courses[3].addPre(pre8);
	}
	public Agent[] students() {
		return this.students;
	}
	public Agent[] courses() {
		return this.courses;
	}
	//�������˼������Ƿ������������С����ʵ�����ĸ���
	public boolean check(Agent[] agents)
	{
		for(Agent agent : agents) {
			if(agent.addCap()) return true;
		}
		return false;
	}
	public SymbolTable<Agent, Agent[]> matchSet(){
		return matches;
	}
	//������������������ƥ��
	//increase-cap
	public void matching() {
		for(Agent student : students) {
			//ѡ��student��������������ֱ���ﵽ��ʵ����
			if(student.vcapacity() == student.capacity()) continue;
			student.increaseC();
			//student��ǰ���ƻ��Լ���
			Agent[] blocks = student.blockS();
	//		for(Agent agent : blocks) {if(agent!=null)System.out.println(agent.name());}
			//ѡ���ƻ��Լ�������ƫ�õĸ��壬���ƥ�䣨���ж��Ƿ������㻷·��
			if(blocks.length == 0) continue;
			Agent maxCourse = student.maxpreS(blocks);
			//student��ƫ�õ�courseû������ʱ�����ƥ�䵽�����student
			if(maxCourse.unassign()) {
				matches.delete(maxCourse);
			}
			//increase-cap 6a&6c
			student.asign(maxCourse);
			Agent[] matchpairs = {maxCourse,student};
			matches.put(maxCourse, matchpairs);
			//increase-cap 6b
			//���ƥ������augmentationCycle�����
			if(this.hasAugmentationCycle() != null) {
				student.unassignCycle(maxCourse);
				//�õ�C\{(s, c')}
				Agent[] path = this.hasAugmentationCycle();
				this.eliminatePath(path);
			} 
		}
	}
	//augmentationcycle����ƥ��Ĳ����˼��ϣ����������򷵻�null,
	private Agent[] hasAugmentationCycle() {
		return null;
	}
	private void eliminatePath(Agent[] path) {
		
	}

}
