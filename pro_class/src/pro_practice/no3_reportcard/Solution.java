package pro_practice.no3_reportcard;

import java.io.*;
import java.util.*;

class UserSolution {

	public static final String MALE = "male";
	public static final String FEMALE = "female";

	public static Map<Integer, Student> studentMap;
	public static List<Student>[][] students;

	public static final int INF = 1_000_000_001;

	public void init() {
		students = new List[2][4];
		studentMap = new HashMap<>();
		for(int gender = 0; gender < 2; gender++){
			for(int grade = 0; grade < 4; grade++){
				students[gender][grade] = new ArrayList<>();
			}
		}

		return;
	}

	public int add(int mId, int mGrade, char mGender[], int mScore) {
		String gender = charToString(mGender);
		int genderInt = gender.equals(MALE) ? 0 : 1;

		Student student;
		List<Student> curStudents = students[genderInt][mGrade];
		if(studentMap.containsKey(mId)){
			student = studentMap.get(mId);
			student.score = mScore;
		}else{
			student = new Student(mId, mGrade, genderInt, mScore);

			studentMap.put(mId, student);
			int insertIdx = Collections.binarySearch(curStudents, student);
			if(insertIdx < 0){
				insertIdx = -insertIdx - 1;
			}

			curStudents.add(insertIdx, student);
		}

		int lastIdx = curStudents.size() - 1;
		return curStudents.get(lastIdx).id;
	}

	public int remove(int mId) {
//		System.out.println("studentMap=" + studentMap);
		Student student = studentMap.get(mId);
		if(student == null){
			return 0;
		}
		int gender = student.gender;
		int grade = student.grade;

		List<Student> curStudent = students[gender][grade];
//		System.out.println("curStudent = " + curStudent);
		int removeIdx = -1;
//		System.out.println("mId=" + mId +", curStudent=" + curStudent);
		for(int idx = 0; idx < curStudent.size(); idx++){
			Student tmpStudent = curStudent.get(idx);
			if(mId == tmpStudent.id){
				removeIdx = idx;
				break;
			}
		}

		curStudent.remove(removeIdx);
		studentMap.remove(mId);

		if(curStudent.size() == 0){
			return 0;
		}

		return curStudent.get(0).id;
	}

	public int query(int mGradeCnt, int mGrade[], int mGenderCnt, char mGender[][], int mScore) {
		int id = INF;
		int minScore = INF;
		for(int genderIdx = 0; genderIdx < mGenderCnt; genderIdx++){
			int gender = charToString(mGender[genderIdx]).equals(MALE) ? 0 : 1;
			for(int gradeIdx = 0; gradeIdx < mGradeCnt; gradeIdx++){
				int grade = mGrade[gradeIdx];
//				System.out.println("students[genderInt][mGrade]=" + students[genderIdx][grade]);

				List<Student> curStudent = students[gender][grade];
				for(Student student : curStudent){
					if(student.score >= mScore){
						if(student.score < minScore){
							minScore = student.score;
							id = student.id;
						}else if(student.score == minScore){
							id = Math.min(id, student.id);
						}
						break;
					}
				}
			}
		}

		return id == INF ? 0 : id;
	}

	public static String charToString(char[]input){
		StringBuilder output = new StringBuilder();

		for(int idx = 0; idx < input.length; idx++){
			output.append(input[idx]);
		}

		return output.toString().trim();
	}

	public static class Student implements Comparable<Student>{
		int id;
		int grade;
		int gender;
		int score;

		public Student(int id, int grade, int gender, int score){
			this.id = id;
			this.grade = grade;
			this.gender = gender;
			this.score = score;
		}

		@Override
		public int compareTo(Student o){
			if(this.score == o.score){
				return this.id - o.id;
			}
			return this.score - o.score;
		}

		@Override
		public String toString(){
			return String.format("[id=%d, grade=%d, gender=%d, score=%d]", id, grade, gender, score);
		}
	}
}
class Solution {
	private final static int CMD_INIT = 100;
	private final static int CMD_ADD = 200;
	private final static int CMD_REMOVE = 300;
	private final static int CMD_QUERY = 400;

	private final static UserSolution usersolution = new UserSolution();

	private static void String2Char(char[] buf, String str) {
		for (int k = 0; k < str.length(); ++k)
			buf[k] = str.charAt(k);
		buf[str.length()] = '\0';
	}
	private static boolean run(BufferedReader br) throws Exception {
		int q = Integer.parseInt(br.readLine());

		int id, grade, score;
		int cmd, ans, ret;
		boolean okay = false;

		for (int i = 0; i < q; ++i) {
			StringTokenizer st = new StringTokenizer(br.readLine(), " ");
			cmd = Integer.parseInt(st.nextToken());
			switch (cmd) {
				case CMD_INIT:
					usersolution.init();
					okay = true;
					break;
				case CMD_ADD:
					char[] gender = new char[7];
					id = Integer.parseInt(st.nextToken());
					grade = Integer.parseInt(st.nextToken());
					String2Char(gender, st.nextToken());
					score = Integer.parseInt(st.nextToken());
					ans = Integer.parseInt(st.nextToken());
					ret = usersolution.add(id, grade, gender, score);
//					System.out.println("[ADD]result=" + ret);
					if (ret != ans)
						okay = false;
					break;
				case CMD_REMOVE:
					id = Integer.parseInt(st.nextToken());
					ans = Integer.parseInt(st.nextToken());
					ret = usersolution.remove(id);
//					System.out.println("[REMOVE]result=" + ret);
					if (ret != ans)
						okay = false;
					break;
				case CMD_QUERY:
					int gradeCnt, genderCnt;
					int[] gradeArr = new int[3];
					char[][] genderArr = new char[2][7];
					gradeCnt = Integer.parseInt(st.nextToken());
					for (int j = 0; j < gradeCnt; ++j) {
						gradeArr[j] = Integer.parseInt(st.nextToken());
					}
					genderCnt = Integer.parseInt(st.nextToken());
					for (int j = 0; j < genderCnt; ++j) {
						String2Char(genderArr[j], st.nextToken());
					}
					score = Integer.parseInt(st.nextToken());
					ans = Integer.parseInt(st.nextToken());
					ret = usersolution.query(gradeCnt, gradeArr, genderCnt, genderArr, score);
//					System.out.println("[QUERY]result=" + ret);
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

		System.setIn(new java.io.FileInputStream("C:\\Users\\pies6\\Desktop\\ssafy_git\\ssafy_11\\pro_class\\src\\pro_practice\\no3_reportcard\\sample_input.txt"));

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