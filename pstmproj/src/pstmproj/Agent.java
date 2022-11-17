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
	public boolean lessCap() {
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
	private boolean matched(Agent agent) {
		if(onhold == 0) return false;
		for(Agent a : matches) {
		//	if(a==null)System.out.println("" + null + "" + onhold);
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
	//���ص�ǰ�����˵��ƻ��Լ���S(��ǰ������courseʱͨ������)
	public Agent[] blockS() {
		//�����ƻ��Լ���,��������
		Agent [] blocks = new Agent[preference.length];
		//������ǰ�����˵�ƫ���б�Ѱ��һ���ƻ���
		//������
		for(Agent[] order : preference()) {
			for(Agent agent : order) {
				//agent��ƥ�䣬ֱ������
				if(matched(agent)) continue;
				//���ƻ�����ӵ��ƻ��Լ�����
				if(block(agent)) {
					int i = 0;
					while(blocks[i++] != null) {
					}
					blocks[i - 1] = agent;
				}
			}
		}
		//ȥ��blocks�е�null
		int j = 0 ;
		while(blocks[j]!=null && (j < blocks.length-1)) {
			j++;
		}
		Agent[] results = new Agent[j+1];
		System.arraycopy(blocks, 0, results, 0, j+1);
		/*System.out.println(results.length);
		for(Agent agent : results) {
			System.out.print(agent.name() + ",");
		}
		System.out.println();*/
		//blocks��������Ҫ�޳�onhold�еĲ����� done
		return results;
	}
	//�����ƻ��Լ���S����ƫ�ø���s
	public Agent maxpreS(Agent[] blockS) {
		//Bubble.sort(blockS);
		/*for(Agent agent : blockS) {
			System.out.print(agent.name() + ",");
		}
		System.out.println();*/
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
	}
	//ȡ��course-studentƥ�䣬��ǰ����Ϊcourse
	public void unassign() {
		//��ƫ�õĸ������������ʱ����Ҫȡ��ƥ��
		if(!lessCap()) {
			//�ҵ��ø�����ƥ������������Ĳ�����
			Agent agent = this.matches[matches.length-1];
			//�ø�����ɾ����������
			this.matches[matches.length-1] = null;
			this.onhold--;		
			//�����������������ƵĲ����˻���Ҫ������������		
			//ͬʱ���������˸�����ɾ���ø���
			Agent[] find = agent.matches();
			for(int i = 0; i < find.length; i++) {
				if(agent.matches[i] == this) {
					agent.matches[i] = null;
					agent.onhold--;
					agent.vcapacity--;			
				}
			}		
		}
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
