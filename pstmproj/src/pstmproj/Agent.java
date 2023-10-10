package pstmproj;


import java.lang.reflect.Array;
import java.util.Arrays;

/*
 * ƥ�������
 */
public class Agent{

	//����������
	private final String name;
	//��ʵ����
	private final int capacity;
	/*������ƫ���б�������ʾƫ�öȣ���ƫ��0�������Ĳ�����
	***��ά�����ʾ������ƫ�ã������б�ʾƫ�ó̶�˳�򣬵�0��ƫ�ó̶����
	****���е������б�ʾͬһƫ�ó̶ȵ����жԷ����ϵĲ�����
	*****�Ե�ǰ������˵��ͬһ���еĲ��������޲����
	******ĳ���е�ĳ�������˳������˸��е�ǰ���б�ʾ��ƫ��
	*******ĳ���������в�������ͬ�Ĳ����˱�ʾ�ϸ�ƫ��
	*/
	private Agent[][] preference;
	//��������
	private int vcapacity;
	//��ʹ������
	private int onhold;
	//�˲�����ƥ�䵽�Ĳ������б�����������/*�Ա����鷽���ܲ���*/��ÿ��ƥ�����Ҫ����
	private Agent[] matches;
	private final boolean isSuitor;
	//���cycleʱʹ�ã���������ֵ��Ӧͬ����students�����еĲ������Ƿ�ƥ��
	private boolean[] marked;
	public void initMarkedCheckCycle(){
		Arrays.fill(marked, false);
	}
	public void markedCheckCycle(int index){
		marked[index] = true;
	}
	public boolean isMarkedCheckCycle(int index){
		return marked[index];
	}
	public Agent(String name, int capacity, boolean isSuitor) {
		this.name = name;
		this.capacity = capacity;
		this.preference = null;
		this.vcapacity = 0;
		this.onhold = 0;
		this.matches = new Agent[capacity];
		this.isSuitor = isSuitor;

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
		this.marked = new boolean[preference.length];
	}
	public void increaseC() {
		this.vcapacity++;
	}
	public void increaseOnhold() {
		this.onhold++;
	}
	//�����������������Ĳ�����������������Ϊ�������
	public void increaseMax() {
		if(!isSuitor) this.vcapacity = this.capacity;	
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
	//�жϵ�ǰ�Ƿ����������
	public boolean lessCap() {
		//�����ǰ������student���ж��Ƿ������������Ҫ�ж��Ƿ�С����������
		if(isSuitor) return onhold < vcapacity;
		//��ǰ����Ϊcourse�ж��Ƿ�С����ʵ����
		return onhold < capacity;
	}
	//���ص�ǰ�������Ƿ���Ҫ������������
	public boolean addCap() {
		return isSuitor && vcapacity < capacity;
	}
	//�ж�agent�Ƿ��ڵ�ǰ�����ƫ���б���
	private boolean contains(Agent agent) {
		//����ƫ�ü���
		for(Agent[] order : preference) {
			//ƫ���б�Ϊ�գ�����false
			if(order.length == 0) return false;
			//������ͬƫ�ó̶ȵĲ�����
			for(Agent pre : order) {
				if(pre.equals(agent)) return true;
			}
		}
		return false;
	}
	//�ж�agent�Ƿ��뵱ǰ����ƥ��
	public boolean matched(Agent agent) {
		if(onhold == 0) return false;
		if(agent == null) return false;
		for(Agent a : matches) {
			
			if(agent.equals(a)) return true;
		}
		return false;
	}
	public boolean block(Agent agent) {
		//agent���ڵ�ǰ�����ƫ���б��У����ߵ�ǰ������agent��ƫ���б��з���false;
		if(!contains(agent) || !agent.contains(this)) return false;
		//��ǰ���������������ж�agent�Ƿ����������
		if(lessCap()) {
			//agent���������
			if(agent.lessCap())return true;
			//agent����������ǰ���������������ж�agent�Ƿ��ƫ�õ�ǰ����
			else if(agent.prefer(this))
			//agent��ƫ�õ�ǰ������ǵ�ǰƥ�伯�ϵ���������
			{
				//���agent��ƥ��					
				return true;
			}
			//agent�Ǹ�ƫ�õ�ǰ����
			else return false;
		}
		//��ǰ����������������ȵ�ǰ����ƥ����������ˣ���ǰ�����ϲ��agent
		else if(this.prefer(agent)) {
			//�ж�agent�Ƿ������
			if(agent.lessCap()) return true;
			else if(agent.prefer(this)) return true;
		}
		//��ǰ��������������Ҹ�ƫ�õ�ǰ�������������
		return false;

	}
	//��ǰ�����Ƿ��ϲ��agent���ǵ�ǰ����ƥ���ĳ��������
	private boolean prefer(Agent agent) {
		boolean result = false;
		//���ݵ�ǰ��������ͣ�ѡ��ȽϹ���
		result = isSuitor? onhold < vcapacity : onhold < capacity;
		//�����ǰ�����ƥ�伯��Ϊ����������true
		if(result) return true;
		else {
			//��ȡagent�ڵ�ǰ����ƫ���б��е�λ��
			int indexA = indexOf(agent);
			//������ǰ�����ƥ�伯�ϣ�����ȡ��ǰ������ƥ��������ڵ�ǰ����ƫ���б��е�λ��
			int indexB = -1;
			int i = 0;
			while(true) {
				//�����ҵ�һ�����������������true
				indexB = indexOf(matches[i++]);
				if(indexA < indexB) return true;
				//matches���������ϣ�����false
				if(i == matches.length) return false;
			}
		}
	}
	//����agent�ڵ�ǰ����ƫ���б��е����򣬼��б�
	private int indexOf(Agent agent) {
		for(int i = 0; i < preference.length; i++) {
			for(Agent a : preference[i]) {
				if(a.equals(agent)) return i;
			}
		}
		//����ʧ��
		return -1;
	}
	//�ƻ��Լ��ϵ���󳤶ȣ����ų���ƫ�õ��µ��ص�
	private int blockSize() {
		int blockSize = preference.length;
		for(Agent[] order : preference) blockSize += order.length;
		return blockSize;
	}
	//���ص�ǰ�����˵��ƻ��Լ���S(��ǰ������courseʱͨ������)
	public Agent[] blockS() {
		//�����ƻ��Լ���
		Agent [] blocks = new Agent[blockSize()];
		//������ǰ�����˵�ƫ���б�Ѱ��һ���ƻ���
		//i-->blocks�����в�������˵�λ��
		int i = 0;
		//����ƫ����
		for(Agent[] order : preference()) {
			for(Agent agent : order) {
				//��ǰ������agent��ƥ�䣬ֱ������
				if(matched(agent)) continue;
				//agent���뵱ǰ���󹹳��ƻ��ԣ���agent��ӵ���ǰ������ƻ��Լ���
				if(block(agent)) {
					//blocks����Ĭ�ϴ�null��ֻ��Ҫ��������Χ���ҵ���һ��null
					while(i < blocks.length && blocks[i++] != null) {
					}
					//��agent��ӵ���ǰ������ƻ��Լ�����
					blocks[i - 1] = agent;
				}
			}
		}

		//ȥ��blocks�е�null
		Agent[] results = new Agent[i];
		System.arraycopy(blocks, 0, results, 0, i);
		//blocks��������Ҫ�޳�onhold�еĲ����� done
		return results;
	}
	//�����ƻ��Լ���S����ƫ�ø���s
	public Agent maxpreS(Agent[] blockS) {
		//this.BubbleSort(blockS);
		
		return blockS[0];
	}
	//���ƥ��
	public void asign(Agent agent) {
		//����˫��������
		//����matches
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
		//����onhold
		this.increaseOnhold();
		agent.increaseOnhold();
		System.out.println("������ " + this.name() + "(��ʹ�õ�ƥ����"+ this.onhold + ")" + " ������� " + agent.name() + "(��ʹ�õ�ƥ����"+ agent.onhold + ")" + " ���ƥ��");
		
	}
	//ȡ��course-studentƥ�䣬��ǰ����Ϊcourse
	public Agent unassign() {
		//��ƫ�õĸ������������ʱ����Ҫȡ��ƥ��
		if(!lessCap()) {
			//�ҵ��ø�����ƥ������������Ĳ�����
			Agent agent = this.matches[matches.length-1];
			//�ø�����ɾ����������
			this.matches[matches.length-1] = null;
			this.onhold--;		
			//�����������������ƵĲ����˻���Ҫ������������		
			//ͬʱ���������˸�����ɾ���ø���
			agent.del(this);
			System.out.println("������ " + this.name() + "(��ʹ�õ�ƥ����"+ this.onhold + ")" + " ������� " + agent.name() + "(��ʹ�õ�ƥ����"+ agent.onhold + ")" + " ȡ��ƥ��");
			return agent;
		}
		return null;
	}/*����matches����ʱ���ܻ��׳���ָ���쳣*/
	//���ƥ��Ժ���ܻ����augmentationcycle�����ô˷������˱���ƥ��
	//ȡ����ǰ������agent�����ƥ��
	public void unassignCycle(Agent agent) {
		//�ҵ���ǰ����ƥ�伯���е�agent,���ڼ�����ɾ��
		this.del(agent);
		//�ҵ�agentƥ�伯���еĵ�ǰ����,���ڼ�����ɾ��
		agent.del(this);
		System.out.println("����augmentation cycle�� ������ " + this.name() + "(��ʹ�õ�ƥ����"+ this.onhold + ")" + " ������� " + agent.name() + "(��ʹ�õ�ƥ����"+ agent.onhold + ")" + " ȡ��ƥ��");
	}
	//�ӵ�ǰ����ƥ�伯����ɾ��ָ��������
	private void del(Agent agent) {
		for(int i = 0; i < this.matches.length; i++) {
			if(agent.equals(this.matches[i])) {
				this.matches[i] = null;
				this.onhold--;
				if(isSuitor) this.vcapacity--;
				break;
			}
		}

	}
	//��ǰ������Ϊagent1���ٺ�agent2һ����
	public boolean weak(Agent agent1, Agent agent2) {
		return indexOf(agent1) <= indexOf(agent2);
	}
	//��ǰ������Ϊagent1��agent2��
	public boolean strict(Agent agent1, Agent agent2) {
		return indexOf(agent1) < indexOf(agent2);
	}
	//��ǰ������Ϊagent1��agent2һ����
	public boolean indifferent(Agent agent1, Agent agent2) {
		return indexOf(agent1) == indexOf(agent2);
	}
	//�ƻ��Լ����еĲ����������ҳ���ƫ�õĲ�����
	public  void BubbleSort(Agent[] agents) {
		for(int x = agents.length - 1; x  > 0; x-- ) {
			for(int y = 0; y < x; y++ ) {
				if(greater(agents[y], agents[y+1]))exchange(agents, y, y + 1);
			}
		}
	}
	private  boolean greater(Agent v, Agent w) {
		return indexOf(v) > indexOf(w);
	}
	private  void exchange(Agent []agents, int x, int y) {
		Agent t = agents[x];
		agents[x] = agents[y];
		agents[y] = t;
	}
	@Override
	public String toString(){
		return name;
	}
}
