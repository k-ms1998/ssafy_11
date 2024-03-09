package pro_practice.no6_giftcard;

import java.io.*;
import java.util.*;

class UserSolution {
	
	public static int size;
	public static Map<Integer, Node> nodeMap;
	public static Node[] roots;
	public static int[] totalPeople;

	/*
	 * 각 테스크 케이스의 처음에 호출된다
	 * 1. N개의 그룸, 최상위 부서 ID, 부서 인원 수
	 * ---
	 * 1. 총 N개의 그룹에 대해서 각각의 트리 생성
	 * 2. 각 트리에서 각 노드마다 부서 총 인원의 수를 저장하고 있는다(누적합)
	 * 	2-1. 이때, 상향식으로 루트에서 관리하면 add를 진행할때마다 루트까지 올라가면서 누적합 업데이트
	 */
	public void init(int N, int mId[], int mNum[]) {
		size = N;
		nodeMap = new HashMap<>();
		roots = new Node[size];
		totalPeople = new int[size];

		for(int idx = 0; idx < size; idx++){
			int id = mId[idx];
			int num = mNum[idx];

			Node node = new Node(id, id, num, num, new HashSet<>());
			nodeMap.put(id, node);
			roots[idx] = node;
			totalPeople[idx] = num;
		}

		return;
	}

	/*
	 * mId부서를 mParent부서의 하위 부서로 추가
	 * mNum만큼의 인원 수
	 * 
	 *  mParent 값은 항상 존재하는 부서의 ID만 주어진다 -> 추가되는 부서는 최상위 부서가 될 수 없다
	 *  mParent 부서는 최대 3개의 하위 부서만 존재할 수 있다
	 *  	- 만약 이미 3개의 하위 부서가 있다면 추가 실패 -> -1 반환
	 *  부서 추가에 성공하면 해당 부서 안에 있는 모든 부서의 인원 수 합을 반환
	 *  
	 */
	public int add(int mId, int mNum, int mParent) {
		Node parent = nodeMap.get(mParent);
		if(parent.children.size() >= 3){
			return -1;
		}

		parent.children.add(mId);
		Node node = new Node(mId, mParent, mNum, mNum, new HashSet<>());
		nodeMap.put(mId, node);
		updateSum(mId, mNum);

		return parent.sumPCnt;
	}

	public int remove(int mId) {
		if(!nodeMap.containsKey(mId)){
			return -1;
		}

		Node node = nodeMap.get(mId);
		Node parent = nodeMap.get(node.parent);
		parent.children.remove(mId);

		int result = node.sumPCnt;
		updateSum(mId, -node.sumPCnt);

		Deque<Integer> queue = new ArrayDeque<>();
		queue.offer(mId);
		while(!queue.isEmpty()){
			int id = queue.poll();
			Node curNode = nodeMap.get(id);

			for(int childId : curNode.children){
				queue.offer(childId);
			}

			nodeMap.remove(id);
		}

		return result;
	}

	public int distribute(int K) {
		int left = 0;
		int right = 0;
		for(int idx = 0; idx < size; idx++){
			right = Math.max(right, roots[idx].sumPCnt);
		}

		while(left <= right){
			int mid = (left + right) / 2;
			int totalNeeded = findTotalNeeded(mid);

			if(totalNeeded == K){
				return mid;
			}

			if(totalNeeded > K){
				right = mid - 1;
			}else{
				left = mid + 1;
			}
		}

		return left - 1;
	}

	public static int findTotalNeeded(int target){
		int result = 0;

		for(int idx = 0; idx < size; idx++){
			result += Math.min(target, roots[idx].sumPCnt);
		}

		return result;
	}

	public static void updateSum(int id, int num){
		Node node = nodeMap.get(id);
		int parentId = node.parent;
		if(id == parentId){
			return;
		}

		Node parent = nodeMap.get(parentId);
		parent.sumPCnt += num;

		updateSum(parentId, num);
	}

	public static class Node{
		int id;
		int parent;
		int pCnt;
		int sumPCnt;
		Set<Integer> children;

		public Node(int id, int parent, int pCnt, int sumPCnt, Set<Integer> children){
			this.id = id;
			this.parent = parent;
			this.pCnt = pCnt;
			this.sumPCnt = sumPCnt;
			this.children = children;
		}
	}
}
class Solution {
	private final static int CMD_INIT = 1;
	private final static int CMD_ADD = 2;
	private final static int CMD_REMOVE = 3;
	private final static int CMD_DISTRIBUTE = 4;

	private final static UserSolution usersolution = new UserSolution();

	private static boolean run(BufferedReader br) throws Exception {
		int q = Integer.parseInt(br.readLine());

		int[] midArr = new int[1000];
		int[] mnumArr = new int[1000];
		int mid, mnum, mparent, n, k;
		int cmd, ans, ret = 0;
		boolean okay = false;

		for (int i = 0; i < q; ++i) {
			StringTokenizer st = new StringTokenizer(br.readLine(), " ");
			cmd = Integer.parseInt(st.nextToken());
			switch (cmd) {
				case CMD_INIT:
					n = Integer.parseInt(st.nextToken());
					for (int j = 0; j < n; ++j) {
						StringTokenizer dep = new StringTokenizer(br.readLine(), " ");
						midArr[j] = Integer.parseInt(dep.nextToken());
						mnumArr[j] = Integer.parseInt(dep.nextToken());
					}
					usersolution.init(n, midArr, mnumArr);
					okay = true;
					break;
				case CMD_ADD:
					mid = Integer.parseInt(st.nextToken());
					mnum = Integer.parseInt(st.nextToken());
					mparent = Integer.parseInt(st.nextToken());
					ans = Integer.parseInt(st.nextToken());
					ret = usersolution.add(mid, mnum, mparent);
//					System.out.println("[ADD]result=" + ret);
					if (ret != ans)
						okay = false;
					break;
				case CMD_REMOVE:
					mid = Integer.parseInt(st.nextToken());
					ans = Integer.parseInt(st.nextToken());
					ret = usersolution.remove(mid);
//					System.out.println("[REMOVE]result=" + ret);
					if (ret != ans)
						okay = false;
					break;
				case CMD_DISTRIBUTE:
					k = Integer.parseInt(st.nextToken());
					ans = Integer.parseInt(st.nextToken());
					ret = usersolution.distribute(k);
//					System.out.println("[DISTRIBUTE]result=" + ret);
					if (ret != ans)
						okay = false;
					break;
				default:
					okay = false;
					break;
			}
		}
		return okay;
	}

	public static void main(String[] args) throws Exception {
		int TC, MARK;

		System.setIn(new java.io.FileInputStream("C:\\Users\\pies6\\Desktop\\ssafy_git\\ssafy_11\\pro_class\\src\\pro_practice\\no6_giftcard\\sample_input.txt"));
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine(), " ");

		TC = Integer.parseInt(st.nextToken());
		MARK = Integer.parseInt(st.nextToken());

		for (int testcase = 1; testcase <= TC; ++testcase) {
			int score = run(br) ? MARK : 0;
			System.out.println("#" + testcase + " " + score);
		}

		br.close();
	}
}