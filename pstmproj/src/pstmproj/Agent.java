package pstmproj;


/*
 * 匹配参与人
 */
public class Agent{

	//参与人名称
	private final String name;
	//真实容量
	private final int capacity;
	/*参与人偏好列表，索引表示偏好度，最偏好0索引处的参与人
	***二维数组表示参与人偏好，其中行表示偏好程度顺序，第0行偏好程度最高
	****行中的所有列表示同一偏好程度的所有对方集合的参与人
	*****对当前对象来说，同一行中的参与人是无差异的
	******某行中的某个参与人出现在了该行的前后行表示弱偏好
	*******某行与相邻行不存在相同的参与人表示严格偏好
	*/
	private Agent[][] preference;
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
	public Agent[][] preference() {
		return this.preference;
	}
	public void addPre(Agent[][] agents) {
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
	public boolean lessCap() {
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
		//遍历偏好集合
		for(Agent[] order : preference) {
			//偏好列表为空，返回false
			if(order.length == 0) return false;
			//遍历不同偏好程度的参与人
			for(Agent pre : order) {
				if(pre.equals(agent)) return true;
			}
		}
		return false;
	}
	//判断agent是否与当前对象匹配
	private boolean matched(Agent agent) {
		if(onhold == 0) return false;
		for(Agent a : matches) {
		//	if(a==null)System.out.println("" + null + "" + onhold);
			if(agent.equals(a)) return true;
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
	//返回agent在当前对象偏好列表中的排序，即行标
	private int indexOf(Agent agent) {
		for(int i = 0; i < preference.length; i++) {
			for(Agent a : preference[i]) {
				if(a.equals(agent)) return i;
			}
		}
		//查找失败
		return -1;
	}
	//返回当前参与人的破坏对集合S(当前对象是course时通过测试)
	public Agent[] blockS() {
		//定义破坏对集合,！！！！
		Agent [] blocks = new Agent[preference.length];
		//遍历当前参与人的偏好列表，寻找一个破坏对
		//遍历行
		int i = 0;
		for(Agent[] order : preference()) {
			for(Agent agent : order) {
				//当前对象与agent已匹配，直接跳过
				if(matched(agent)) continue;
				//将破坏对添加到破坏对集合中
				if(block(agent)) {
					
					//blocks数组默认存null，只需要在索引范围内找到第一个null
					while(blocks[i++] != null && i < blocks.length) {
					}
					//agent若与当前对象构成破坏对，将agent添加到当前对象的破坏对集合
					blocks[i - 1] = agent;
				}
			}
		}
		//去除blocks中的null
		Agent[] results = new Agent[i];
		System.arraycopy(blocks, 0, results, 0, i);
		//blocks数组中需要剔除onhold中的参与人 done
		return results;
	}
	//返回破坏对集合S的最偏好个体s
	public Agent maxpreS(Agent[] blockS) {
		this.BubbleSort(blockS);
		return blockS[0];
	}
	//达成匹配
	public void asign(Agent agent) {
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
		System.out.println("参与人 " + this.name() + " 与参与人 " + agent.name() + " 达成匹配");
	}
	//取消course-student匹配，当前对象为course
	public void unassign() {
		//最偏好的个体无容量余额时，需要取消匹配
		if(!lessCap()) {
			//找到该个体已匹配参与人中最差的参与人
			Agent agent = this.matches[matches.length-1];
			//该个体中删除最差参与人
			this.matches[matches.length-1] = null;
			this.onhold--;		
			//对于有虚拟容量限制的参与人还需要回退虚拟容量		
			//同时从最差参与人个体中删除该个体
			Agent[] find = agent.matches();
			for(int i = 0; i < find.length; i++) {
				if(agent.matches[i] == this) {
					agent.matches[i] = null;
					agent.onhold--;
					agent.vcapacity--;			
				}
			}	
			System.out.println("参与人 " + this.name() + " 与参与人 " + agent.name() + " 取消匹配");
		}
	}
	//破坏对集合中的参与人排序，找出最偏好的参与人
	public  void BubbleSort(Agent[] agents) {
		for(int x = agents.length - 1; x  > 0; x-- ) {
			for(int y = 0; y < x; y++ ) {
				if(greater(agents[y], agents[y+1]))exchange(agents, y, y + 1);
			}
		}
	}
	private  boolean greater(Agent v, Agent w) {
		return indexOf(v) >= indexOf(w);
	}
	private  void exchange(Agent []agents, int x, int y) {
		Agent t = agents[x];
		agents[x] = agents[y];
		agents[y] = t;
	}
}
