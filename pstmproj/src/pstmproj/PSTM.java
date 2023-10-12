package pstmproj;


import javax.swing.*;
import java.util.*;

public class PSTM {
	private Agent[] students;
	private Agent[] courses;
	//当前的匹配对集合，key存course，value存course-student
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
	//检查参与人集合中是否存在虚拟容量小于真实容量的个体
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
	//增加虚拟容量，进行匹配
	//increase-cap
	public void matching() {
		increaseCap();
			
		
	}
	//augmentationcycle是已匹配的参与人集合，若不存在则返回null,
	private Agent[] hasAugmentationCycle() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}

		//获取已匹配参与人集合，偶数下标存放student，奇数下标存放course
		//得到已匹配的参与人数量，构建cycle
		int number = 0;
		for(Agent course : courses) {
			List<Agent> courseMatchedStu = matches.get(course);
			if(courseMatchedStu != null) {
				//course 已匹配，需要标记
				course.initMarkedCheckCycle();
				//此course已匹配，计数
				number++;
//				System.out.println("course 计数 " + number);
//				System.out.print(course);
				for(Agent stu : courseMatchedStu) {
					int stuIndex = indexOf(stu, courseMatchedStu.toArray(new Agent[0]));//
//					System.out.print("," + stu);

					if(!course.isMarkedCheckCycle(stuIndex)) {
						//此student第一次出现在匹配中，计数
						number++;
//						System.out.println("student 计数 " + number);
						//标记，以免student被其他course匹配时出现重复计数
						course.markedCheckCycle(stuIndex);
					}
				}

			}
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
		int cindex = 1, sindex = 0;
		for(Agent course : courses) {
			//course已匹配才加入cycle
			List<Agent> matchedToCourse = matches.get(course);
			//曾今匹配过才会出现list

			if(matchedToCourse!=null && matchedToCourse.size() != 0) {
				cycle[cindex] = course;
				for(Agent student : matchedToCourse) {
					int index = this.indexOf(student, students);
					//System.out.print(agent.name() + " " + index + " ,");
					//跳过value数组的索引0处的course
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
		//[s1, c1, s2, c2]调整顺序 变成 ---> [s2, c1, s1, c2]
		//索引0处和索引2，4，6，8，...处交换元素
		
		int exch = 2;
		while(exch < number) {
			exchange(cycle, 0, exch);
			exch += 2;
		}
//		print cycle
//		for (Agent agent : cycle) {
//			System.out.println(agent);
//		}
		//同下标匹配且相邻下标未匹配，则继续判断偏好	
		for(int i = 0; i < number - 1; i+=2) {
			if(i == 0) {

				if(cycle[i] == null) return null;
				if(cycle[i]!=null && !cycle[i].isMatched(cycle[number - 1]) || cycle[i].isMatched(cycle[i+1])) return null;
			}else {
					if(!cycle[i].isMatched(cycle[i-1]) || cycle[i].isMatched(cycle[i+1])) return null;
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
	private void increaseCap(){
		StudentArrayIter<Agent> iter = new StudentArrayIter<>(students);
		while (iter.hasNext()){
			Agent student = iter.next();

			//选择student，增加虚拟容量直到达到真实容量
			if(student == null || student.vcapacity() == student.capacity()) continue;
			student.increaseC();
			System.out.println("当前学生是：" + student);
			//student当前的破坏对集合
			Agent[] blocks = student.blockS();
			System.out.println("当前的破坏对集合");
			for (Agent block : blocks) {
				System.out.print( block + " ");
			}
			System.out.println(student.vcapacity() < student.capacity());
			System.out.println();
			//		for(Agent agent : blocks) {if(agent!=null)System.out.println(agent.name());}
			//选择破坏对集合中最偏好的个体，达成匹配（需判断是否有曾广环路）
			if(blocks.length == 0) continue;
			Agent maxCourse = student.maxpreS(blocks);
			//student最偏好的course没有容量时，解除匹配到的最差student
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
			//匹配s的maxCourse
			//increase-cap 6a&6c
			student.asign(maxCourse);
			if(matches.containsKey(maxCourse)) matches.get(maxCourse).add(student);
			else {
				List<Agent> stu = new ArrayList<>();
				stu.add(student);
				matches.put(maxCourse, stu);
			}
			//increase-cap 6b
			//达成匹配会产生augmentationCycle则回退
			Agent[] path = this.hasAugmentationCycle();
			if(path != null) {
				//得到C\{(s, c')},不改变path中参与人数量，仅仅是断开匹配
				student.unassign(maxCourse);
				matches.get(maxCourse).remove(student);
				//出现增广环时打印取消匹配后的增广路径
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
		//扩容
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
			System.out.printf("取消匹配后的augmentation path = ");
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
		System.out.println("消除环路中。。。。。。。。。");
		int eindex = 0;
		System.out.print("Path = [");
		for (Agent agent : path) {
			System.out.print(agent + ",");
		}
		System.out.println("]");
//		检查是否取消匹配
//		checkMatches();
		eIncr(eindex, path);

		while (eIsNotNull())
		{
			//取路径上的第eindex对参与人
			if(eindex+2>path.length) break;
			eIncr(eindex, path);
			Agent first = e[0];
			Agent second = e[1];
//			System.out.println("e= [" +first+"," +second+ "]");
			//e不是匹配，尝试匹配
			if(!first.isMatched(second)){
				System.out.println("尝试匹配" + "e= [" +first+"," +second+ "]");
				first.asign(second);
				checkMatches();
				//产生环路，取消操作，eindex++
				if ((hasAugmentationCycle()!=null)){

				}else {

					//不产生环路，是否超出容量，
					//是，继续处理
					// 判断是否更偏好操作过程中匹配的对象
					if(!first.lessOrEqualCap() && second.lessOrEqualCap()) {
						if(first.preferCurThanLeast(second)){
							Agent leas = first.leastAgent();
							first.unassign(leas);//取消和最差对象的匹配
							if(matches.containsKey(first)) matches.get(first).remove(leas);
							else  matches.get(leas).remove(first);
							return;
						}else {
							//撤销操作

							System.out.printf("%s超出容量且不认为%s比%s严格好，回退\r\n", first,second,first.leastAgent());
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
							//撤销操作
							System.out.printf("%s超出容量且认为%s不比%s严格好，回退\r\n", second,first,second.leastAgent());
							second.unassign(first);
							if(matches.containsKey(first)) matches.get(first).remove(second);
							else  matches.get(second).remove(first);
						}
					}else if(!first.lessOrEqualCap() && !second.lessOrEqualCap()){
						System.out.println(first+"当前匹配个数 "+first.getOnhold() + ", 最大容量" + first.getCapacity() + first.lessOrEqualCap());
						System.out.println(second+"当前匹配个数 "+second.getOnhold() + ", 最大容量" + second.getCapacity() + second.lessOrEqualCap());
						System.out.println("双方都超出");
					}

					else {
						//						否则结束
						System.out.println("消除环路结束");
						checkMatches();
						return;
					}



					//否，则eindex++

					//		是，则取消与当前对象的匹配，虚拟容量变化

				}

			}
			//e是匹配，尝试取消匹配
			else {
				System.out.println("尝试取消匹配" + "e= [" +first+"," +second+ "]");
				first.unassign(second);
				if(matches.containsKey(first)) matches.get(first).remove(second);
				else  matches.get(second).remove(first);
				//不产生环路，则取下一对参与人
				if (hasAugmentationCycle() == null){

				}else {

				}
				//否则撤销操作

				//eindex++

			}
			checkMatches();
			eindex++;
		}
		System.out.println("消除环==路结束 ");
	}

	private void eIncr(int eindex, Agent[] path) {
		if(eindex+2>path.length) return;
		System.arraycopy(path,eindex,e,0,2);
	}

	public void checkMatches() {
		System.out.println("-------------------------打印匹配情况-------------------------");
//		打印时去重复
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
		System.out.println("-------------------------打印参与人偏好情况-------------------------");
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
