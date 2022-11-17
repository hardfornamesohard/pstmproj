package pstmproj;
import util.Bubble;
/*
 * 匹配参与人
 */
public class Agent implements Comparable<Agent>{

	//参与人名称
	private final String name;
	//真实容量
	private final int capacity;
	//参与人偏好列表，索引表示偏好度，最偏好0索引处的参与人
	private Agent[] preference;
	//虚拟容量
	private int vcapacity;
	//已使用容量
	private int onhold;
	//此参与人匹配到的参与人列表，按优劣排序，每次匹配后需要更新
	private Agent[] matches;
	public Agent(String name, int capacity) {
		this.name = name;
		this.capacity = capacity;
		this.preference = null;
		this.vcapacity = 0;
		this.matches = new Agent[capacity];
	}
	public String name() {
		return this.name;
	}
	public int capacity() {
		return this.capacity;
	}
	public Agent[] preference() {
		return this.preference;
	}
	public void addPre(Agent[] agents) {
		this.preference = agents;
	}
	public void increaseC() {
		this.vcapacity++;
	}
	public void increaseOnhold() {
		this.onhold++;
	}
	//无需设置虚拟容量的参与人虚拟容量设置为最大容量
	public void increaseMax() {
		this.vcapacity = this.capacity;
	}
	public int vcapacity() {
		return vcapacity;
	}
	public Agent[] matches() {
		return this.matches;
	}
	public void updateMatch(int i, Agent agent) {
		this.matches[i] = agent;
	}
	//判断当前是否还有容量余额
	private boolean lessCap() {
		//如果当前对象是student，判断是否有容量余额需要判断是否小于虚拟容量
		if(vcapacity < capacity) return onhold < vcapacity;
		//当前对象为course判断是否小于真实容量
		return onhold < capacity;
	}
	//返回当前参与人是否需要增加虚拟容量
	public boolean addCap() {
		return vcapacity < capacity;
	}
	//判断agent是否在当前对象的偏好列表中
	private boolean contains(Agent agent) {
		for(Agent a : preference) {
			if(a == null) return false;
			if(a.equals(agent)) return true;
		}
		return false;
	}
	//判断agent是否与当前对象匹配
	private boolean matched(Agent agent) {
		for(Agent a : matches) {
			if(a == null) return false;
			if(a.equals(agent)) return true;
		}
		return false;
	}
	public boolean block(Agent agent) {
		//agent不在当前对象的偏好列表中，或者当前对象不在agent的偏好列表中返回false;
		if(!contains(agent) || !agent.contains(this)) return false;
		//当前对象有容量余额，则判断agent是否有容量余额
		if(lessCap()) {
			//agent有容量余额
			if(agent.lessCap())return true;
			//agent无容量余额但当前对象有容量余额，则判断agent是否更偏好当前对象
			else if(agent.prefer(this))
			//agent更偏好当前对象而非当前匹配集合的最差参与人
			{
				//解除agent的匹配					
				return true;
			}
			//agent非更偏好当前对象
			else return false;
		}
		//当前对象无容量余额，但相比当前对象匹配的最差参与人，当前对象更喜欢agent
		else if(this.prefer(agent)) {
			//判断agent是否有余额
			if(agent.lessCap()) return true;
			else if(agent.prefer(this)) return true;
		}
		//当前对象无容量余额且更偏好当前对象的最差参与人
		return false;

	}
	//当前对象是否更喜欢agent而非当前对象匹配的某个参与人
	private boolean prefer(Agent agent) {
		boolean result = false;
		//根据当前对象的类型，选择比较规则
		result = vcapacity < capacity? onhold < vcapacity : onhold < capacity;
		//如果当前对象的匹配集合为非满，返回true
		if(result) return true;
		else {
			//获取agent在当前对象偏好列表中的位置
			int indexA = indexOf(agent);
			//遍历当前对象的匹配集合，并获取当前对象所匹配参与人在当前对象偏好列表中的位置
			int indexB = -1;
			int i = 0;
			while(true) {
				//若能找到一个更大的索引，返回true
				indexB = indexOf(matches[i++]);
				if(indexA < indexB) return true;
				//matches数组遍历完毕，返回false
				if(i == matches.length) return false;
			}
		}
	}
	//返回agent在当前对象偏好列表中的排序
	private int indexOf(Agent agent) {
		for(int i = 0; i < preference.length; i++) {
			if(preference[i].equals(agent)) return i;
		}
		//查找失败
		return -1;
	}
	//返回当前参与人的破坏对集合S(当前对象是course时通过测试)
	public Agent[] blockS() {
		//定义破坏对集合,！！！！
		Agent [] blocks = new Agent[preference.length - onhold];
		//遍历当前参与人的偏好列表，寻找一个破坏对
		for(Agent course : preference()) {
			//course已匹配，直接跳过
			if(matched(course)) continue;
			//将破坏对添加到破坏对集合中
			if(block(course)) {
				int i = 0;
				while(blocks[i] != null) {
					i++;
				}
				blocks[i] = course;
			}
		}
		//去除blocks中的null
		int j = 0 ;
		while(blocks[j]!=null && (j < blocks.length-1)) {
			j++;
		}
		Agent[] results = new Agent[j];
		System.arraycopy(blocks, 0, results, 0, j);
		//blocks数组中需要剔除onhold中的参与人 done
		return results;
	}
	//返回破坏对集合S的最偏好个体s
	public Agent maxpreS(Agent[] blockS) {
		Bubble.sort(blockS);

		return blockS[blockS.length - 1];
	}
	//达成匹配
	public void matching(Agent agent) {
		//更新双方参与人
		//更新matches
		for(int i = 0; i < matches.length; i++) {
			if(matches[i] == null) {
				this.updateMatch(i, agent);
				break;
			}
		}
		Agent[] amatches = agent.matches();
		for(int i = 0; i < amatches.length; i++) {
			if(amatches[i] == null) {
				agent.updateMatch(i, this);;
				break;
			}
		}
		//更新onhold
		this.increaseOnhold();
		agent.increaseOnhold();
	}
	@Override
	public int compareTo(Agent agent) {
		if(matches == null) return 1;
		//当前对象匹配的最差参与人
		Agent minAgent = matches[matches.length - 1];
		//通过比较当前对象最差参与人与agent在当前对象偏好列表中的排序
		if(indexOf(agent) >= indexOf(minAgent)) return 1;
		return -1;
	}
}
