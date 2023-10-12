package pstmproj;


import javax.swing.*;
import java.util.*;

public class PSTM {
	private Agent[] students;
	private Agent[] courses;
	//��ǰ��ƥ��Լ��ϣ�key��course��value��course-student
	private HashMap<Agent, List<Agent>> matches;
	private Agent[] e = new Agent[2];


	public PSTM() {
		init();
		matches = new HashMap<>();

	}
	public void init(){
		PSTMInitializer initializer = new PSTMInitializer();
		initializer.init();
		courses = initializer.getCourses();
		students = initializer.getStudents();
	}

	public Agent[] students() {
		return this.students;
	}
	public Agent[] courses() {
		return this.courses;
	}
	//�������˼������Ƿ������������С����ʵ�����ĸ���
	public boolean check()
	{
		for(Agent agent : students) {
			if(agent == null) continue;

			if(agent.addCap()) return true;
		}
		return false;
	}
	public HashMap<Agent, List<Agent>> matchSet(){
		return matches;
	}
	//������������������ƥ��
	//increase-cap
	public void matching() {
		increaseCap();
			
		
	}
	//augmentationcycle����ƥ��Ĳ����˼��ϣ����������򷵻�null,
	private Agent[] hasAugmentationCycle() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}

		//��ȡ��ƥ������˼��ϣ�ż���±���student�������±���course
		//�õ���ƥ��Ĳ���������������cycle
		int number = 0;
		for(Agent course : courses) {
			List<Agent> courseMatchedStu = matches.get(course);
			if(courseMatchedStu != null) {
				//course ��ƥ�䣬��Ҫ���
				course.initMarkedCheckCycle();
				//��course��ƥ�䣬����
				number++;
//				System.out.println("course ���� " + number);
//				System.out.print(course);
				for(Agent stu : courseMatchedStu) {
					int stuIndex = indexOf(stu, courseMatchedStu.toArray(new Agent[0]));//
//					System.out.print("," + stu);

					if(!course.isMarkedCheckCycle(stuIndex)) {
						//��student��һ�γ�����ƥ���У�����
						number++;
//						System.out.println("student ���� " + number);
						//��ǣ�����student������courseƥ��ʱ�����ظ�����
						course.markedCheckCycle(stuIndex);
					}
				}

			}
		}

		if((number < 3) | (number % 2 != 0)) return null;
		//����ƥ��Ĳ����˰�cycle����
		Agent[] cycle = new Agent[number];
		/*int count2 = matches.size();
		for(Agent course : courses) {
			//course��ƥ��ż���cycle
			if(matches.get(course) != null) {
					int index = this.indexOf(matches.get(course), students);
					System.out.print(agent.name() + " " + index + " ,");
					System.out.println();
			}
				
		}*/
		//ʵ�ʵõ�[s1, c1, s2, c2]��δ��·�����У���Ͽ�ƥ��
		int cindex = 1, sindex = 0;
		for(Agent course : courses) {
			//course��ƥ��ż���cycle
			List<Agent> matchedToCourse = matches.get(course);
			//����ƥ����Ż����list

			if(matchedToCourse!=null && matchedToCourse.size() != 0) {
				cycle[cindex] = course;
				for(Agent student : matchedToCourse) {
					int index = this.indexOf(student, students);
					//System.out.print(agent.name() + " " + index + " ,");
					//����value���������0����course
					if(index == this.indexOf(course, courses)) {
						cycle[sindex] = student;
						sindex += 2;
					}
				}
				cindex += 2;
			}
		}

		//for(boolean b : marked) System.out.print(b + " ,");
//		int sindex = 0;
//		for(Agent student : students) {
//			int index = this.indexOf(student, students);
//			if(marked[index] && sindex < cycle.length) {
//				cycle[sindex] = student;
//				sindex += 2;
//			}
//		}
//		System.out.println(number);
		//[s1, c1, s2, c2]����˳�� ��� ---> [s2, c1, s1, c2]
		//����0��������2��4��6��8��...������Ԫ��
		
		int exch = 2;
		while(exch < number) {
			exchange(cycle, 0, exch);
			exch += 2;
		}
//		print cycle
//		for (Agent agent : cycle) {
//			System.out.println(agent);
//		}
		//ͬ�±�ƥ���������±�δƥ�䣬������ж�ƫ��	
		for(int i = 0; i < number - 1; i+=2) {
			if(i == 0) {

				if(cycle[i] == null) return null;
				if(cycle[i]!=null && !cycle[i].isMatched(cycle[number - 1]) || cycle[i].isMatched(cycle[i+1])) return null;
			}else {
					if(!cycle[i].isMatched(cycle[i-1]) || cycle[i].isMatched(cycle[i+1])) return null;
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
		StudentArrayIter<Agent> iter = new StudentArrayIter<>(students);
		while (iter.hasNext()){
			Agent student = iter.next();

			//ѡ��student��������������ֱ���ﵽ��ʵ����
			if(student == null || student.vcapacity() == student.capacity()) continue;
			student.increaseC();
			System.out.println("��ǰѧ���ǣ�" + student);
			//student��ǰ���ƻ��Լ���
			Agent[] blocks = student.blockS();
			System.out.println("��ǰ���ƻ��Լ���");
			for (Agent block : blocks) {
				System.out.print( block + " ");
			}
			System.out.println(student.vcapacity() < student.capacity());
			System.out.println();
			//		for(Agent agent : blocks) {if(agent!=null)System.out.println(agent.name());}
			//ѡ���ƻ��Լ�������ƫ�õĸ��壬���ƥ�䣨���ж��Ƿ������㻷·��
			if(blocks.length == 0) continue;
			Agent maxCourse = student.maxpreS(blocks);
			//student��ƫ�õ�courseû������ʱ�����ƥ�䵽�����student
			if(!maxCourse.lessCap()) {
				Agent del = maxCourse.unassign();
				if (del != null) {
					if (del.isSuitor) {
						del.recoverVc();
						addDelStudent(findNextNullIndex(), del);
					}
					matches.get(maxCourse).remove(del);

				}
			}
			//ƥ��s��maxCourse
			//increase-cap 6a&6c
			student.asign(maxCourse);
			if(matches.containsKey(maxCourse)) matches.get(maxCourse).add(student);
			else {
				List<Agent> stu = new ArrayList<>();
				stu.add(student);
				matches.put(maxCourse, stu);
			}
			//increase-cap 6b
			//���ƥ������augmentationCycle�����
			Agent[] path = this.hasAugmentationCycle();
			if(path != null) {
				//�õ�C\{(s, c')},���ı�path�в����������������ǶϿ�ƥ��
				student.unassign(maxCourse);
				matches.get(maxCourse).remove(student);
				//�������㻷ʱ��ӡȡ��ƥ��������·��
				this.eliminatePath(path);
			}
		}
	}

	private void addDelStudent(int nextNullIndex, Agent stu) {
		students[nextNullIndex] = stu;
	}

	private int findNextNullIndex() {
		int nextNull = 0;
		for (Agent student : students) {
			if(student!=null) nextNull++;
			else return nextNull;
		}
		//����
		if(nextNull == students.length) {
			addStudentsCapacity();
			return nextNull;
		}
		return -1;
	}

	private void addStudentsCapacity() {
		Agent[] newArr = new Agent[2*students.length];
		System.arraycopy(students, 0, newArr, 0, students.length);
		students = newArr;
	}

	public void printPath(Agent[] path) {
		if(path != null) {
			System.out.printf("ȡ��ƥ����augmentation path = ");
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

		printPath(path);
		System.out.println("������·�С�����������������");
		int eindex = 0;
		System.out.print("Path = [");
		for (Agent agent : path) {
			System.out.print(agent + ",");
		}
		System.out.println("]");
//		����Ƿ�ȡ��ƥ��
//		checkMatches();
		eIncr(eindex, path);

		while (eIsNotNull())
		{
			//ȡ·���ϵĵ�eindex�Բ�����
			if(eindex+2>path.length) break;
			eIncr(eindex, path);
			Agent first = e[0];
			Agent second = e[1];
//			System.out.println("e= [" +first+"," +second+ "]");
			//e����ƥ�䣬����ƥ��
			if(!first.isMatched(second)){
				System.out.println("����ƥ��" + "e= [" +first+"," +second+ "]");
				first.asign(second);
				checkMatches();
				//������·��ȡ��������eindex++
				if ((hasAugmentationCycle()!=null)){

				}else {

					//��������·���Ƿ񳬳�������
					//�ǣ���������
					// �ж��Ƿ��ƫ�ò���������ƥ��Ķ���
					if(!first.lessOrEqualCap() && second.lessOrEqualCap()) {
						if(first.preferCurThanLeast(second)){
							Agent leas = first.leastAgent();
							first.unassign(leas);//ȡ�����������ƥ��
							if(matches.containsKey(first)) matches.get(first).remove(leas);
							else  matches.get(leas).remove(first);
							return;
						}else {
							//��������

							System.out.printf("%s���������Ҳ���Ϊ%s��%s�ϸ�ã�����\r\n", first,second,first.leastAgent());
							first.unassign(second);
							if(matches.containsKey(first)) matches.get(first).remove(second);
							else  matches.get(second).remove(first);
						}
					}else if(!second.lessOrEqualCap() && first.lessOrEqualCap())
					{
						if(second.preferCurThanLeast(first)){
							Agent leas = second.leastAgent();
							second.unassign(leas);
							if(matches.containsKey(second)) matches.get(first).remove(leas);
							else  matches.get(leas).remove(second);
							return;
						}else {
							//��������
							System.out.printf("%s������������Ϊ%s����%s�ϸ�ã�����\r\n", second,first,second.leastAgent());
							second.unassign(first);
							if(matches.containsKey(first)) matches.get(first).remove(second);
							else  matches.get(second).remove(first);
						}
					}else if(!first.lessOrEqualCap() && !second.lessOrEqualCap()){
						System.out.println(first+"��ǰƥ����� "+first.getOnhold() + ", �������" + first.getCapacity() + first.lessOrEqualCap());
						System.out.println(second+"��ǰƥ����� "+second.getOnhold() + ", �������" + second.getCapacity() + second.lessOrEqualCap());
						System.out.println("˫��������");
					}

					else {
						//						�������
						System.out.println("������·����");
						checkMatches();
						return;
					}



					//����eindex++

					//		�ǣ���ȡ���뵱ǰ�����ƥ�䣬���������仯

				}

			}
			//e��ƥ�䣬����ȡ��ƥ��
			else {
				System.out.println("����ȡ��ƥ��" + "e= [" +first+"," +second+ "]");
				first.unassign(second);
				if(matches.containsKey(first)) matches.get(first).remove(second);
				else  matches.get(second).remove(first);
				//��������·����ȡ��һ�Բ�����
				if (hasAugmentationCycle() == null){

				}else {

				}
				//����������

				//eindex++

			}
			checkMatches();
			eindex++;
		}
		System.out.println("������==·���� ");
	}

	private void eIncr(int eindex, Agent[] path) {
		if(eindex+2>path.length) return;
		System.arraycopy(path,eindex,e,0,2);
	}

	public void checkMatches() {
		System.out.println("-------------------------��ӡƥ�����-------------------------");
//		��ӡʱȥ�ظ�
		List<Agent> printed = new ArrayList<>();
		for(Agent student : students) {

			if (printed.contains(student)) return;
			if (student == null) continue;
			Agent[] matchToStu = student.matches();
			if (matchToStu == null) return;
			for (Agent course : matchToStu) {
				if (course == null) continue;
				System.out.println(student+ " <---> " + course);
			}
			printed.add(student);
		}
	}

	private boolean eIsNotNull() {
		return e[0] != null && e[1] != null;
	}

	public void printPreference() {
		System.out.println("-------------------------��ӡ������ƫ�����-------------------------");
		Arrays.stream(students).filter(Objects::nonNull).forEach(Agent::printPreference);
		Arrays.stream(courses).filter(Objects::nonNull).forEach(Agent::printPreference);
//		for (Agent student : students) {
//
//			student.printPreference();
//		}
//		for (Agent cours : courses) {
//			cours.printPreference();
//		}
	}
}
