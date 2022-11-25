package pstmproj;


import util.SymbolTable;

public class PSTM {
	private Agent[] students;
	private Agent[] courses;
	//当前的匹配对集合，key存course，value存course-student
	private SymbolTable<Agent, Agent> matches;
	//检查cycle时使用，索引处的值对应同索引students数组中的参与人是否匹配
	private boolean[] marked;
	public PSTM() {
		initAgents();
		initPreference();
		matches = new SymbolTable<Agent, Agent>();
		marked = new boolean[students.length];
	}
	public void initAgents() {
		students = new Agent[3];
		courses = new Agent[3];
		students[0] = new Agent("s1", 1);
		students[1] = new Agent("s2", 2);
		students[2] = new Agent("s3", 1);
		//students[3] = new Agent("s4", 1);
		courses[0] = new Agent("c1", 1);
		courses[1] = new Agent("c2", 2);
		courses[2] = new Agent("c3", 1);
		//courses[3] = new Agent("c4", 2);
		for(Agent course : courses) {
			course.increaseMax();
		}
	}
	public void initPreference()
	{
		//Agent [][] pre1 = {{courses[0]}, {courses[3]}, {courses[2]}, {courses[1]}};
		Agent [][] pre1 = {{courses[0], courses[1]}};
		students[0].addPre(pre1);
		//Agent [][] pre2 = {{courses[0]}, {courses[2]}, {courses[3]}, {courses[1]}};
		Agent [][] pre2 = {{courses[0]}, {courses[1]}, {courses[2]}};
		students[1].addPre(pre2);
		//Agent [][] pre3 = {{courses[3]}, {courses[0]}, {courses[2],courses[0]}, {courses[1]}};
		Agent [][] pre3 = {{courses[1]}};
		students[2].addPre(pre3);
		//Agent [][] pre7 = {{courses[0]}, {courses[2]}, {courses[1]}, {courses[3]}};
		//students[3].addPre(pre7);
		//Agent [][] pre4 = {{students[3]}, {students[0]}, {students[2]}, {students[1]}};
		Agent [][] pre4 = {{students[0], students[1]}};
		courses[0].addPre(pre4);
		//Agent [][] pre5 = {{students[0]}, {students[3]}, {students[2]}, {students[1]}};
		Agent [][] pre5 = {{students[0]}, {students[1]}, {students[2]}};
		courses[1].addPre(pre5);
		//Agent [][] pre6 = {{students[0]}, {students[2]}, {students[3]}, {students[1]}};
		Agent [][] pre6 = {{students[1]}};
		courses[2].addPre(pre6);
		//Agent [][] pre8 = {{students[0]}, {students[2]}, {students[1]}, {students[3]}};
		//courses[3].addPre(pre8);
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
	public SymbolTable<Agent, Agent> matchSet(){
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
			Agent del = null;
			if((del = maxCourse.unassign()) != null) {
				//删除c2 s3 时错误的把s1也删除了
				/*
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 
				 * */
				matches.delete(maxCourse, del);
			}
			//increase-cap 6a&6c
			student.asign(maxCourse);
			matches.put(maxCourse, student);
			//increase-cap 6b
			//达成匹配会产生augmentationCycle则回退
			/*Agent[] path = this.hasAugmentationCycle();
			if(path != null) {
				//得到C\{(s, c')},不改变path中参与人数量，仅仅是断开匹配
				
				student.unassignCycle(maxCourse);
				this.eliminatePath(path);
				System.out.println("augmentation cycle occured!");
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				for(Agent agent : path) {
					if(agent == null) continue;
					sb.append(agent.name() + ",");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append("]");
				System.out.println(sb);
			} */
		}
	}
	//augmentationcycle是已匹配的参与人集合，若不存在则返回null,
	private Agent[] hasAugmentationCycle() {
		for(int i = 0; i < marked.length; i++) {
			marked[i] = false;
		}
		//获取已匹配参与人集合，偶数下标存放student，奇数下标存放course
		//得到已匹配的参与人数量，构建cycle
		int number = 0;
		for(Agent course : courses) {
			Agent[] stu = (Agent[])matches.get(course);
			if(stu != null) number = number + stu.length + 1;
		}
		if((number < 3) | (number % 2 != 0)) return null;
		//将已匹配的参与人按cycle排列
		Agent[] cycle = new Agent[number];
		/*int count2 = matches.size();
		for(Agent course : courses) {
			//course已匹配才加入cycle
			if(matches.get(course) != null) {
					int index = this.indexOf(matches.get(course), students);
					System.out.print(agent.name() + " " + index + " ,");
					System.out.println();
			}
				
		}*/
		//实际得到[s1, c1, s2, c2]，未按路径排列，需断开匹配
		int cindex = 1;
		for(Agent course : courses) {
			//course已匹配才加入cycle
			if(matches.get(course) != null) {
				cycle[cindex] = course;
				for(Agent agent : matches.get(course)) {
					int index = this.indexOf(agent, students);
					System.out.print(agent.name() + " " + index + " ,");
					//跳过value数组的索引0处的course
					if(index > -1) marked[index] = true;
					else continue;
				}
				cindex += 2;
				System.out.println();
			}
		}
		for(boolean b : marked) System.out.print(b + " ,");
		int sindex = 0;
		for(Agent student : students) {
			int index = this.indexOf(student, students);
			if(marked[index] == true && sindex < cycle.length) {
				cycle[sindex] = student;
				sindex += 2;
			}
		}
		//[s1, c1, s2, c2]调整顺序 变成 ---> [s2, c1, s1, c2]
		//索引0处和索引2，4，6，8，...处交换元素
		
		int exch = 2;
		while(exch < number) {
			exchange(cycle, 0, exch);
			exch += 2;
		}
		
		//同下标匹配且相邻下标未匹配，则继续判断偏好
	
			
		for(int i = 0; i < number - 1; i+=2) {
			if(i == 0) {
				if(cycle[i].matched(cycle[number - 1]) || !cycle[i].matched(cycle[i+1])) return null;
			}else {
					if(cycle[i].matched(cycle[i-1]) || !cycle[i].matched(cycle[i+1])) return null;
				}
		}
		
		//全部弱偏好,首位需要特殊处理避免数组越界
		for(int i = 0; i < number - 1; i+=2) {
			if(i == number - 2){
				if(!(cycle[i].weak(cycle[i+1], cycle[i-1]) && cycle[i+1].weak(cycle[i], cycle[0]))) return null;
			}
			else if(i == 0) {
				if(!(cycle[i].weak(cycle[i+1], cycle[number-1]) && cycle[i+1].weak(cycle[i], cycle[i+2]))) return null;
			}else {
				if(!(cycle[i].weak(cycle[i+1], cycle[i-1]) && cycle[i+1].weak(cycle[i], cycle[i+1]))) return null;
			}		
		}
		
		//是否存在一个严格偏好
		for(int i = 0; i < number - 1; i+=2) {
			if(i == 0) {
				if(cycle[i].strict(cycle[i+1], cycle[number-1]) || cycle[i+1].strict(cycle[i], cycle[i+2])) return cycle;
			}	
			else if(i == number - 2){
				if(cycle[i].strict(cycle[i+1], cycle[i-1]) || cycle[i+1].strict(cycle[i], cycle[0])) return cycle;
			}else {
				if(cycle[i].strict(cycle[i+1], cycle[i-1]) || cycle[i+1].strict(cycle[i], cycle[0])) return cycle;
			}
		}
		
		return null;
	}
	private int indexOf(Agent agent, Agent[] agents) {
		for(int i = 0; i < agents.length; i++) {
			if(agent == agents[i]) return i;
		}
		return -1;
	}
	private void exchange(Agent[] agents, int i, int j) {
		Agent tmp = agents[i];
		agents[i] = agents[j];
		agents[j] = tmp;
		
	}
	private boolean contains(Agent[] agents, Agent agent) {
		for(Agent agent2 : agents) {
			if(agent2.equals(agent)) return true;
		}
		return false;
	}
	private void eliminatePath(Agent[] path) {
		
	}

}
