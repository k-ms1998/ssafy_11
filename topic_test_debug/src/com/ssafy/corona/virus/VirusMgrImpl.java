package com.ssafy.corona.virus;



public class VirusMgrImpl implements VirusMgr {
	private static VirusMgr instance = new VirusMgrImpl(); // 싱글톤으로 인스턴스 관리

	private Virus[] virus;
	private int index;
	
	private VirusMgrImpl() { // 싱글톤으로 인스턴스 관리 -> 생성자를 private으로 변경
		virus=new Virus[100];		
	}

	public static VirusMgr getInstance(){ 
		// 다른 위치에서 인스턴스를 가져오기 위해서 public static으로 설정
		// static으로 설정해야 VirusMgrImpl 객체를 외부에서 생성하지 않고 메서드를 접근 할 수 있음
		return instance;
	}
	
	@Override
	public void add(Virus v) throws DuplicatedException{ 
		// DuplicatedException에 대해서는 메인에서 처리하고 있음
		// -> add 메서드에서 처리하지 않고 메인으로 예외 던지기
		try {
			search(v.getName());
		} catch (NotFoundException e) {
			virus[index++]=v;
		}
	}
	@Override
	public Virus[] search() {
		return virus;
	}
	@Override
	public Virus search(String name) throws NotFoundException, DuplicatedException {
		for(int i=0; i<virus.length; i++) {
			if(virus[i] == null) break;
			if(virus[i].getName().equals(name)) { // 바이러스 이름이 같으면 중복 -> DuplicatedException 처리
				// 예외 던지기
				// 1. throw new DuplicatedException(name + ": 등록된 바이러스입니다.");
				// 2. 메서드의 시그니처에 throws  DuplicatedException 추가
				// 3. VirusMgrImpl는 VirusMgr를 상속 받고 있음 -> VirusMgr의 search 메서드에도 throws  DuplicatedException 추가
				throw new DuplicatedException(name + ": 등록된 바이러스입니다.");
			}
		}
		throw new NotFoundException(name+": 미등록 바이러스입니다.");
	}
	


}
