package pstmproj;


import java.util.TimerTask;

import util.SymbolTable;

public class PSTM {
	private Agent[] students;
	private Agent[] courses;
	//��ǰ��ƥ��Լ��ϣ�key��course��value��course-student
	private SymbolTable<Agent, Agent> matches;
	//���cycleʱʹ�ã���������ֵ��Ӧͬ����students�����еĲ������Ƿ�ƥ��
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
		students[0] = new Agent("s1", 1, true);
		students[1] = new Agent("s2", 2, true);
		students[2] = new Agent("s3", 1, true);
		//students[3] = new Agent("s4", 1);
		courses[0] = new Agent("c1", 1, false);
		courses[1] = new Agent("c2", 2, false);
		courses[2] = new Agent("c3", 1, false);
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
	//�������˼������Ƿ������������С����ʵ�����ĸ���
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
	//������������������ƥ��
	//increase-cap
	public void matching() {
		increaseCap();
			
		
	}
	//augmentationcycle����ƥ��Ĳ����˼��ϣ����������򷵻�null,
	private Agent[] hasAugmentationCycle() {
		for(int i = 0; i < marked.length; i++) {
			marked[i] = false;
		}
		//��ȡ��ƥ������˼��ϣ�ż���±���student�������±���course
		//�õ���ƥ��Ĳ���������������cycle
		int number = 0;
		for(Agent course : courses) {
			Agent[] stu = matches.get(course);
			if(stu != null) {
				//��course��ƥ�䣬����
				number++;
				for(Agent agent : stu) {
					int stuIndex = indexOf(agent, students);
					if(marked[stuIndex] == false) {
						//��student��һ�γ�����ƥ���У�����
						number++;
						//��ǣ�����student������courseƥ��ʱ�����ظ�����
						marked[stuIndex] = true;
					}
				}
			}
		}
		if((number < 3) | (number % 2 != 0)) return null;
		//����ƥ��Ĳ����˰�cycle����
		Agent[] cycle = new Agent[number];
		//ʵ�ʵõ�[s1, c1, s2, c2]��δ��·�����У���Ͽ�ƥ��
		int cindex = 1;
		for(Agent course : courses) {
			//course��ƥ��ż���cycle
			if(matches.get(course) != null) {
				cycle[cindex] = course;
				for(Agent agent : matches.get(course)) {
					int index = this.indexOf(agent, students);
					//System.out.print(agent.name() + " " + index + " ,");
					//����value���������0����course
					if(index > -1) marked[index] = true;
					else continue;
				}
				cindex += 2;
			}
		}
		//for(boolean b : marked) System.out.print(b + " ,");
		int sindex = 0;
		for(Agent student : students) {
			int index = this.indexOf(student, students);
			if(marked[index] == true && sindex < cycle.length) {
				cycle[sindex] = student;
				sindex += 2;
			}
		}
		//System.out.println(number);
		//[s1, c1, s2, c2]����˳�� ��� ---> [s2, c1, s1, c2]
		//����0��������2��4��6��8��...������Ԫ��
		
		int exch = 2;
		while(exch < number) {
			exchange(cycle, 0, exch);
			exch += 2;
		}
		
		//ͬ�±�ƥ���������±�δƥ�䣬������ж�ƫ��	
		for(int i = 0; i < number - 1; i+=2) {
			if(i == 0) {
				if(!cycle[i].matched(cycle[number - 1]) || cycle[i].matched(cycle[i+1])) return null;
			}else {
					if(!cycle[i].matched(cycle[i-1]) || cycle[i].matched(cycle[i+1])) return null;
				}
		}
		
		//ȫ����ƫ��,��λ��Ҫ���⴦���������Խ��
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
		
		//�Ƿ����һ���ϸ�ƫ��
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
	private void increaseCap(){
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
			Agent del = maxCourse.unassign();
			if(del != null) {
				matches.delete(maxCourse, del);
			}
			//increase-cap 6a&6c
			student.assign(maxCourse);
			matches.put(maxCourse, student);
			//increase-cap 6b
			//���ƥ������augmentationCycle�����
			Agent[] path = this.hasAugmentationCycle();
			if(path != null) {
				//�õ�C\{(s, c')},���ı�path�в����������������ǶϿ�ƥ��
				System.out.println("augmentation cycle occured!");
				student.unassignCycle(maxCourse);
				//�������㻷ʱ��ӡȡ��ƥ��������·��
				printPath(path);
				this.eliminatePath(path);
			}
		}
	}
	public void printPath(Agent[] path) {
		if(path != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for(Agent agent : path) {
				if(agent == null) continue;
				sb.append(agent.name() + ",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("]");
			System.out.println(sb);
		}
	}
	private void eliminatePath(Agent[] path) {
		//ѡ��·���ϵĵ�һ�Բ�����
		int ePos = 0;
		Agent[] e = new Agent[2];
		System.arraycopy(path, ePos, e, 0, 2);
		System.out.println(e[0].name() + ":" +  e[1].name());
		while(e[0] != null && e[1] != null) {
			//e is not a matching
			if(e[0].isSuitor() && !e[1].isSuitor()) {
				//c is not over-matched
				if(e[1].lessCap()) {
					e[0].assign(e[1]);
					matches.put(e[1], e[0]);
					return;
				}//c is over-matched
				else {
					//c strictly perfers s(e[0]) to a current partner
					Agent[] matchedtoC = matches.get(e[1]);
					e[1].BubbleSort(matchedtoC);
 					Agent curr = matchedtoC[matchedtoC.length - 1];
					if(e[1].strict(e[0], curr)) {
						e[1].unassign();
						matches.delete(e[1], curr);
						increaseCap();
					}
					//e be the next pair
					else {
						ePos++;
						System.arraycopy(path, ePos, e, 0, 2);
					}
				}
				Agent[] cycle = this.hasAugmentationCycle();
				if(cycle == null)continue;
				//efgh
				else {
					
				}
				
			}
			//e is a matching
			else {
				e[0].unassignCycle(e[1]);
				matches.delete(e[0], e[1]);
				ePos++;
				System.arraycopy(path, ePos, e, 0, 2);
				Agent[] cycle = this.hasAugmentationCycle();
				if(cycle == null)continue;
				//efgh
				else {
					
				}
			}
		}
	}
}
