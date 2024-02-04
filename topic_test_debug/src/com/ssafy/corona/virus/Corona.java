package com.ssafy.corona.virus;

public class Corona extends Virus{
	public int spreadSpeed;


	public Corona(String name, int level, int spreadSpeed) { // 생성자는 리턴타입(void, String 등)이 없어여함
		setName(name);
		setLevel(level);
		setSpreadSpeed(spreadSpeed);
	}
	
	public int getSpreadSpeed() {
		return spreadSpeed;
	}
	public void setSpreadSpeed(int spreadSpeed) {
		this.spreadSpeed = spreadSpeed;
	}

	@Override
	public String toString() {
		// 출력할때 Corona가 toString()을 제대로 오버라이딩하지 않고 있었음
		// 오버라이딩을 하기 위해서는 리턴타입, 이름, 파라미터(개수, 순서 타입 등) 모두 같아야함
		StringBuilder sb=new StringBuilder();
		sb.append(super.toString()).append("\t")
		  .append(getSpreadSpeed());
		return sb.toString();
	}
}