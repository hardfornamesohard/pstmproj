package pstmproj;

import util.SymbolTable;

public class PSTM {
	private Agent[] students;
	private Agent[] courses;
	//当前的匹配对集合，key存course，value存course-student
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
	//检查参与人集合中是否存在虚拟容量小于真实容量的个体
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
	//增加虚拟容量，进行匹配
	//increase-cap
	public void matching() {
		for(Agent student : students) {
			//选择student，增加虚拟容量直到达到真实容量
			if(student.vcapacity() == student.capacity()) continue;
			student.increaseC();
			//student当前的破坏对集合
			Agent[] blocks = student.blockS();
	//		for(Agent agent : blocks) {if(agent!=null)System.out.println(agent.name());}
			//选择破坏对集合中最偏好的个体，达成匹配（需判断是否有曾广环路）
			if(blocks.length == 0) continue;
			Agent maxCourse = student.maxpreS(blocks);
			//student最偏好的course没有容量时，解除匹配到的最差student
			if(maxCourse.unassign()) {
				matches.delete(maxCourse);
			}
			//increase-cap 6a&6c
			student.asign(maxCourse);
			Agent[] matchpairs = {maxCourse,student};
			matches.put(maxCourse, matchpairs);
			//increase-cap 6b
			//达成匹配会产生augmentationCycle则回退
			if(this.hasAugmentationCycle() != null) {
				student.unassignCycle(maxCourse);
				//得到C\{(s, c')}
				Agent[] path = this.hasAugmentationCycle();
				this.eliminatePath(path);
			} 
		}
	}
	//augmentationcycle是已匹配的参与人集合，若不存在则返回null,
	private Agent[] hasAugmentationCycle() {
		return null;
	}
	private void eliminatePath(Agent[] path) {
		
	}

}
