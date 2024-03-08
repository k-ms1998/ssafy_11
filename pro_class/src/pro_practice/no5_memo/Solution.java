package pro_practice.no5_memo;

import java.io.*;
import java.util.*;

class UserSolution
{
	static List<Character> str;
	static int[][] sum;
	static int cursor;
	static int lastIdx;
	static int LAST_CURSOR;
	static int height;
	static int width;

	/*
	 * 각 테스트 케이스의 처음에 호출된다
	 * 메모장의 높이(H)와 메모장의 너비(W)가 주어진다
	 * 	- 1 <= H <= 300
	 * 	- 1 <= W <= 300
	 * 초기 메모장에는 mStr의 문자열이 입력되어 있다
	 * 	- mStr은 영문 소문자로만 구성되어 있다(\0 으로 끝난다)
	 * 	- 길이: 1 <= length <= HxW(최대 90_000)
	 * 커서는 문자열의 첫 번째 문자 왼쪽에 위치해 있다
	 *---
	 * 1. 현재의 문자열(str)을 리스트로 관리
	 * 	1-1. 리스트로 관리함으로써 원하는 위치에 쉽게 문자를 추가할 수 있음
	 * 2. 문자열이 나타난 빈도를 기록하기 위해 누적합 사용
	 * 	2-1. 이때, 1차원 배열로 관리하면 시간 초과 발생
	 * 		2-1-1. 최악의 경우: cursor가 맨 앞에 있어서 문자가 추가된 후 누적합을 업데이트 할때 전체 문자열의 크기 만큼 연산을 해야함 (최대 90_000)
	 * 	2-2. 2차원 배열로 관리
	 * 		2-2-1. 문자열을 width 크기만큼으로
	 */
	void init(int H, int W, char mStr[])
	{
		height = H;
		width = W;
		str = new ArrayList<>();
		lastIdx = 1;
		LAST_CURSOR = H*W;
		cursor = 0;

		sum = new int[H + 1][26];

		for(int idx = 0; idx < mStr.length; idx++){
			char curC = mStr[idx];
			if(curC == '\0'){
				lastIdx = idx;
				break;
			}
			int curH = idx / width;
			str.add(curC);
			int curCInt = mStr[idx] - 'a';
			sum[curH][curCInt]++;
		}

	}

	/*
	 * 최대 30_000 회 호출된다
	 *
	 * 커서의 위치에 문자 mChar 입력
	 * 	- mChar는 영어 소문자
	 * 입력 후 커서는 입력된 문자의 오른쪽에 위치한다
	 * 문자 입력 후 문자열의 길이가 HxW를 초과하는 경우는 없다
	 * ---
	 */
	void insert(char mChar)
	{
		int cInt = mChar - 'a';

		int cursorH = cursor / width;
		str.add(cursor, mChar);
		sum[cursorH][cInt]++;

		lastIdx++;
		cursor++;
		int curIdx = cursorH * width;
		int lastH = lastIdx / width;
		for(int h = cursorH + 1; h <= lastH; h++){
			curIdx += width;
			if(curIdx >= str.size()) {
				break;
			}
			char curFirstC = str.get(curIdx);
			if(curFirstC == '\0') {
				break;
			}

			int curFirstCInt = curFirstC - 'a';
			sum[h - 1][curFirstCInt]--;
			sum[h][curFirstCInt]++;
		}

	}

	/*
	 * 최대 30_000 회 호출된다
	 *
	 * 커서의 위치를 메모장의 mRow mCol 문자의 왼쪽으로 이동시킨다
	 * mRow행 mCol열이 비어 있는 경우, 커서의 위치를 문자열의 마지막 문자 오른쪽으로 이동 시킨다
	 *
	 * 커서의 다음 문자를 리턴한다
	 * 커서가 문자열 끝에 위치해서 다음 다음 문자가 없는 경우에는 '$' 리턴
	 */
	char moveCursor(int mRow, int mCol)
	{
		cursor = Math.min(width * (mRow-1) + (mCol-1), lastIdx);

		if(cursor == lastIdx){
			return '$';
		}

		return str.get(cursor);
	}

	/*
	 * 최대 40_000 회 호출된다
	 *
	 * 커서 뒤쪽(오른쪽으로 마지막 문자까지)에 있는 문자열 중에서 mChar 개수를 리턴한다
	 * mChar는 영어 소문자임이 보장된다
	 */
	int countCharacter(char mChar)
	{
		int cInt = mChar - 'a';
		int cursorH = cursor / width;
		int cursorW = cursor % width;
		int lastH = lastIdx / width;

		int result = 0;
		int cursorHLastIdx = Math.min((cursorH + 1) * width - 1, lastIdx - 1);
		for(int curIdx = cursor; curIdx <= cursorHLastIdx; curIdx++) {
			char curC = str.get(curIdx);
			if(curC == mChar) {
				result++;
			}
		}

		// 현재 커서 다음줄들
		for(int h = cursorH + 1; h <= lastH; h++) {
			result += sum[h][cInt];
		}

		return result;
	}

}

class Solution
{
	private final static int CMD_INIT       = 100;
	private final static int CMD_INSERT     = 200;
	private final static int CMD_MOVECURSOR = 300;
	private final static int CMD_COUNT      = 400;
	
	private final static UserSolution usersolution = new UserSolution();
	
	private static void String2Char(char[] buf, String str, int maxLen)
	{
		for (int k = 0; k < str.length(); k++)
			buf[k] = str.charAt(k);
			
		for (int k = str.length(); k <= maxLen; k++)
			buf[k] = '\0';
	}
	
	private static char[] mStr = new char[90001];
	
	private static boolean run(BufferedReader br) throws Exception
	{
		StringTokenizer st = new StringTokenizer(br.readLine(), " ");
		
		int queryCnt = Integer.parseInt(st.nextToken());
		boolean correct = false;
		
		for (int q = 0; q < queryCnt; q++)
		{
			st = new StringTokenizer(br.readLine(), " ");
			
			int cmd = Integer.parseInt(st.nextToken());
			
			if (cmd == CMD_INIT)
			{
				int H = Integer.parseInt(st.nextToken());
				int W = Integer.parseInt(st.nextToken());
				
				String2Char(mStr, st.nextToken(), 90000);
				
				usersolution.init(H, W, mStr);
				correct = true;
			}
			else if (cmd == CMD_INSERT)
			{
				char mChar = st.nextToken().charAt(0);
				
				usersolution.insert(mChar);
			}
			else if (cmd == CMD_MOVECURSOR)
			{
				int mRow = Integer.parseInt(st.nextToken());
				int mCol = Integer.parseInt(st.nextToken());
				
				char ret = usersolution.moveCursor(mRow, mCol);
//				System.out.println("[MOVECURSOR]result=" + ret);
				char ans = st.nextToken().charAt(0);
				if (ret != ans)
				{
					correct = false;
				}
			}
			else if (cmd == CMD_COUNT)
			{
				char mChar = st.nextToken().charAt(0);
				
				int ret = usersolution.countCharacter(mChar);
//				System.out.println("[COUNT]result=" + ret);
				int ans = Integer.parseInt(st.nextToken());
				if (ret != ans)
				{
					correct = false;
				}
			}
		}
		return correct;
	}

	public static void main(String[] args) throws Exception
	{
		int TC, MARK;
		
		System.setIn(new java.io.FileInputStream("C:\\Users\\pies6\\Desktop\\ssafy_git\\ssafy_11\\pro_class\\src\\pro_practice\\no5_memo\\sample_input.txt"));
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine(), " ");
		
		TC = Integer.parseInt(st.nextToken());
		MARK = Integer.parseInt(st.nextToken());
		
		for (int testcase = 1; testcase <= TC; ++testcase)
		{
			int score = run(br) ? MARK : 0;

			System.out.println("#" + testcase + " " + score);
		}
		
		br.close();
	}
}