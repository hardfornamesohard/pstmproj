package pstmproj;
import util.Bubble;
/*
 * ƥ�������
 */
public class Agent implements Comparable<Agent>{

	//����������
	private final String name;
	//��ʵ����
	private final int capacity;
	//������ƫ���б�������ʾƫ�öȣ���ƫ��0�������Ĳ�����
	private Agent[] preference;
	//��������
	private int vcapacity;
	//��ʹ������
	private int onhold;
	//�˲�����ƥ�䵽�Ĳ������б�����������ÿ��ƥ�����Ҫ����
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
	//�����������������Ĳ�����������������Ϊ�������
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
	//�жϵ�ǰ�Ƿ����������
	private boolean lessCap() {
		//�����ǰ������student���ж��Ƿ������������Ҫ�ж��Ƿ�С����������
		if(vcapacity < capacity) return onhold < vcapacity;
		//��ǰ����Ϊcourse�ж��Ƿ�С����ʵ����
		return onhold < capacity;
	}
	//���ص�ǰ�������Ƿ���Ҫ������������
	public boolean addCap() {
		return vcapacity < capacity;
	}
	//�ж�agent�Ƿ��ڵ�ǰ�����ƫ���б���
	private boolean contains(Agent agent) {
		for(Agent a : preference) {
			if(a == null) return false;
			if(a.equals(agent)) return true;
		}
		return false;
	}
	//�ж�agent�Ƿ��뵱ǰ����ƥ��
	private boolean matched(Agent agent) {
		for(Agent a : matches) {
			if(a == null) return false;
			if(a.equals(agent)) return true;
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
		result = vcapacity < capacity? onhold < vcapacity : onhold < capacity;
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
	//����agent�ڵ�ǰ����ƫ���б��е�����
	private int indexOf(Agent agent) {
		for(int i = 0; i < preference.length; i++) {
			if(preference[i].equals(agent)) return i;
		}
		//����ʧ��
		return -1;
	}
	//���ص�ǰ�����˵��ƻ��Լ���S(��ǰ������courseʱͨ������)
	public Agent[] blockS() {
		//�����ƻ��Լ���,��������
		Agent [] blocks = new Agent[preference.length - onhold];
		//������ǰ�����˵�ƫ���б�Ѱ��һ���ƻ���
		for(Agent course : preference()) {
			//course��ƥ�䣬ֱ������
			if(matched(course)) continue;
			//���ƻ�����ӵ��ƻ��Լ�����
			if(block(course)) {
				int i = 0;
				while(blocks[i] != null) {
					i++;
				}
				blocks[i] = course;
			}
		}
		//ȥ��blocks�е�null
		int j = 0 ;
		while(blocks[j]!=null && (j < blocks.length-1)) {
			j++;
		}
		Agent[] results = new Agent[j];
		System.arraycopy(blocks, 0, results, 0, j);
		//blocks��������Ҫ�޳�onhold�еĲ����� done
		return results;
	}
	//�����ƻ��Լ���S����ƫ�ø���s
	public Agent maxpreS(Agent[] blockS) {
		Bubble.sort(blockS);

		return blockS[blockS.length - 1];
	}
	//���ƥ��
	public void matching(Agent agent) {
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
	}
	@Override
	public int compareTo(Agent agent) {
		if(matches == null) return 1;
		//��ǰ����ƥ�����������
		Agent minAgent = matches[matches.length - 1];
		//ͨ���Ƚϵ�ǰ��������������agent�ڵ�ǰ����ƫ���б��е�����
		if(indexOf(agent) >= indexOf(minAgent)) return 1;
		return -1;
	}
}
