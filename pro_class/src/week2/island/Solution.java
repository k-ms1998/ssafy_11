package week2.island;

import java.io.*;
import java.util.*;

/**
 * 1. NxN 크기의 섬이 있다(가장 자리는 바다로 둘러 쌓여 있음)
 * 	1-1. 5 <= N <= 20
 * 	1-2. 각 좌표의 높이는 1이상 5이하
 * 2. 지구온난화로 인한 해수면 상승 -> 섬이 바다에 잠길 위험이 있다
 * 	2-1. 해수면이 mSeaLevel 만큼 상승 -> 섬에서 고도가 mSeaLevel - 1이하인 지역으로 침투함
 * 	2-2. 상하좌우 인접한 방향만 침투 가능
 * 3. 섬을 지키기 위해 1xM 크기의 구조물 설치 가능
 * 	3-1. 구조물을 설치했을때 해당 좌표들의 높이를 구조물의 각 부분의 높이만큼 추가
 * 		3-1-1. 이때 모두 높이가 같아지면 설치 가능. 높이가 모두 같지 않으면 불가능
 * 	3-2. 구조물은 회전을 시킬 수 있다
 */
class UserSolution
{
    static int[][] island;
    static int[][][] updatedIsland;
    static List<Point>[] bySeaLevel;
    static int size;

    static int maxCombinationNum = 55_556;
    static List<Point>[] candidatesH = new List[maxCombinationNum];
    static List<Point>[] candidatesV = new List[maxCombinationNum];

    static final int[] dCol = {1, 0, -1, 0};
    static final int[] dRow = {0, 1, 0, -1};

    /*
     * 처음에 1회 호출
     * N x N 크기의 섬을 만든다
     */
    public void init(int N, int mMap[][])
    {
        bySeaLevel = new List[11];
        for(int seaLevel = 0; seaLevel < 11; seaLevel++){
            bySeaLevel[seaLevel] = new ArrayList<>();
        }
        size = N;
        island = new int[size][size];
        updatedIsland = new int[size][size][4];
        for(int row = 0; row < N; row++) {
            for(int col = 0; col < N; col++) {
                int curSeaLevel = mMap[row][col];
                island[row][col] = curSeaLevel;
                Arrays.fill(updatedIsland[row][col], curSeaLevel);
                if(row == 0 || col == 0 || row == size - 1 || col == size - 1){
                    bySeaLevel[curSeaLevel].add(new Point(col, row, -1));
                }
            }
        }

        for(int idx = 0; idx < maxCombinationNum; idx++) {
            candidatesH[idx] = new ArrayList<>();
            candidatesV[idx] = new ArrayList<>();
        }

        Deque<Integer> queue = new ArrayDeque<>();
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        queue.offer(4);
        queue.offer(5);
        for(int l = 1; l < 5; l++) {
            int qSize = queue.size();
            for(int idx = 0; idx < qSize; idx++) {
                int curNum = queue.poll();
                for(int num = 1; num <= 5; num++) {
                    int nextNum = 10*curNum + num;
                    queue.offer(nextNum);
                    int tmpNum = nextNum;
                    int[] arr = new int[l + 1];

                    for(int arrIdx = 0; arrIdx < l + 1; arrIdx++) {
                        arr[l - arrIdx] = tmpNum % 10;
                        tmpNum /= 10;
                    }
//            		System.out.println("nextNum=" + nextNum);
//            		for(int arrIdx = 0; arrIdx < l + 1; arrIdx++) {
//            			System.out.print(arr[arrIdx] + " ");
//            		}
//            		System.out.println();
                    for(int row = 0; row < size; row++) {
                        for(int col = 0; col < size; col++) {
                            int cnt1 = 1;
                            int cnt2 = 1;
                            int target1 = island[row][col] + arr[0];
                            int target2 = island[row][col] + arr[0];
                            for(int d = 1; d < l + 1; d++) {
                                if(col + d < size) {
                                    int h1 = island[row][col + d] + arr[d];
                                    if(h1 == target1) {
                                        cnt1++;
                                    }
                                }
                                if(row + d < size) {
                                    int h2 = island[row + d][col] + arr[d];
                                    if(h2 == target2) {
                                        cnt2++;
                                    }
                                }
                            }

                            if(cnt1 == l + 1) {
                                candidatesH[nextNum].add(new Point(col, row, -1));
                            }
                            if(cnt2 == l + 1) {
                                candidatesV[nextNum].add(new Point(col, row, -1));
                            }
                        }
                    }
                }
            }

        }
    }

    /*
     * (150_000번 호출)
     *
     * 구조물(mStructure)를 설치했을때 나타날 수 있는 경우의 수 반환
     * (1 <= M <= 5)
     *
     * 각 테스트 케이스에서 maxArea() 함수의 구조물 mStructure를 설치할 수 있는 경우의 수의 총합은 5,000 이하이다.
     */
    public int numberOfCandidate(int M, int mStructure[])
    {
        if(M == 1) {
            return size * size;
        }

        boolean isReverseSame = true;
        for(int idx = 0; idx < M / 2; idx++) {
            if(mStructure[M - 1 - idx] != mStructure[idx]) {
                isReverseSame = false;
                break;
            }
        }

        int numIdx = 0;
        for(int idx = M - 1; idx >= 0; idx--) {
            numIdx = 10 *numIdx + mStructure[idx];
        }
        int reverseNumIdx = 0;
        for(int idx = 0; idx < M; idx++) {
            reverseNumIdx = 10 *reverseNumIdx + mStructure[idx];
        }

        int possibleCount = candidatesH[numIdx].size() + candidatesV[numIdx].size();
        int reversePossibleCount = candidatesH[reverseNumIdx].size() + candidatesV[reverseNumIdx].size();
        return isReverseSame ? possibleCount : possibleCount + reversePossibleCount;
    }

    /*
     * (50번 호출)
     *
     * 해수면이  mSeaLevel 만큼 상승해도, mStructure를 1개 설치 했을때 바다에 잠기지 않고 남아있는 지역의 수 반환
     * 구조물을 설치 할 수 없는 경우에는 -1 반환
     *
     * 1 <= mSeaLevel <= 10
     */
    public int maxArea(int M, int mStructure[], int mSeaLevel)
    {
        int answer = -1;

        int[] reverseStructure = new int[M];
        for(int idx = 0; idx < M; idx++) {
            reverseStructure[idx] = mStructure[M - 1- idx];
        }
        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                int cnt1 = 1; // 우
                int cnt2 = 1; // 좌
                int cnt3 = 1; // 상
                int cnt4 = 1; // 하
                int target1 = island[row][col] + mStructure[0];
                int target2 = island[row][col] + reverseStructure[0];
                int target3 = island[row][col] + mStructure[0];
                int target4 = island[row][col] + reverseStructure[0];
                for(int d = 1; d < M; d++) {
                    if(col + d < size) {
                        int h1 = island[row][col + d] + mStructure[d];
                        int h2 = island[row][col + d] + reverseStructure[d];
                        if(h1 == target1) {
                            cnt1++;
                        }
                        if(h2 == target2) {
                            cnt2++;
                        }
                    }
                    if(row + d < size) {
                        int h3 = island[row + d][col] + mStructure[d];
                        int h4 = island[row + d][col] + reverseStructure[d];
                        if(h3 == target3) {
                            cnt3++;
                        }
                        if(h4 == target4) {
                            cnt4++;
                        }
                    }
                }

                if(cnt1 != M && cnt2 != M && cnt3 != M && cnt4 != M){
                    continue;
                }

                boolean[][][] flooded = new boolean[size][size][4];
                int[] islandCounter = {0, 0, 0, 0};
                Deque<Point> queue = new ArrayDeque<>();

                for(int d = 0; d < M; d++) {
                    if(cnt1 == M) {
                        updatedIsland[row][col + d][0] += mStructure[d];
                    }
                    if(cnt2 == M) {
                        updatedIsland[row][col + d][1] += reverseStructure[d];
                    }
                    if(cnt3 == M) {
                        updatedIsland[row + d][col][2] += mStructure[d];
                    }
                    if(cnt4 == M) {
                        updatedIsland[row + d][col][3] += reverseStructure[d];
                    }

                }
                if(cnt1 == M) {
                    for(int seaLevel = 0; seaLevel < mSeaLevel; seaLevel++){
                        for(Point p : bySeaLevel[seaLevel]){
                            int curRow = p.row;
                            int curCol = p.col;
                            if(updatedIsland[curRow][curCol][0] < mSeaLevel){
                                flooded[curRow][curCol][0] = true;
                                queue.offer(new Point(curCol, curRow, 0));
                            }
                        }
                    }
                }
                if(cnt2 == M) {
                    for(int seaLevel = 0; seaLevel < mSeaLevel; seaLevel++){
                        for(Point p : bySeaLevel[seaLevel]){
                            int curRow = p.row;
                            int curCol = p.col;
                            if(updatedIsland[curRow][curCol][1] < mSeaLevel){
                                flooded[curRow][curCol][1] = true;
                                queue.offer(new Point(curCol, curRow, 1));
                            }
                        }
                    }
                }
                if(cnt3 == M) {
                    for(int seaLevel = 0; seaLevel < mSeaLevel; seaLevel++){
                        for(Point p : bySeaLevel[seaLevel]){
                            int curRow = p.row;
                            int curCol = p.col;
                            if(updatedIsland[curRow][curCol][2] < mSeaLevel){
                                flooded[curRow][curCol][2] = true;
                                queue.offer(new Point(curCol, curRow, 2));
                            }
                        }
                    }
                }
                if(cnt4 == M) {
                    for(int seaLevel = 0; seaLevel < mSeaLevel; seaLevel++){
                        for(Point p : bySeaLevel[seaLevel]){
                            int curRow = p.row;
                            int curCol = p.col;
                            if(updatedIsland[curRow][curCol][3] < mSeaLevel){
                                flooded[curRow][curCol][3] = true;
                                queue.offer(new Point(curCol, curRow, 3));
                            }
                        }
                    }
                }

                while(!queue.isEmpty()) {
                    Point p =  queue.poll();
                    int pRow = p.row;
                    int pCol = p.col;
                    int pType = p.type;

                    for(int dir = 0; dir < 4; dir++) {
                        int nRow = pRow + dRow[dir];
                        int nCol = pCol + dCol[dir];

                        if(nCol < 0 || nRow < 0 || nCol >= size || nRow >= size) {
                            continue;
                        }

                        if(!flooded[nRow][nCol][pType] && updatedIsland[nRow][nCol][pType] < mSeaLevel) {
                            flooded[nRow][nCol][pType] = true;
                            queue.offer(new Point(nCol, nRow, pType));
                        }
                    }
                }

                for(int iRow = 0; iRow < size; iRow++){
                    for(int iCol = 0; iCol < size; iCol++){
                        if(!flooded[iRow][iCol][0] && cnt1 == M){
                            islandCounter[0]++;
                        }
                        if(!flooded[iRow][iCol][1] && cnt2 == M){
                            islandCounter[1]++;
                        }
                        if(!flooded[iRow][iCol][2] && cnt3 == M){
                            islandCounter[2]++;
                        }
                        if(!flooded[iRow][iCol][3] && cnt4 == M){
                            islandCounter[3]++;
                        }
                    }
                }

                for(int islandCounterIdx = 0; islandCounterIdx < 4; islandCounterIdx++){
                    answer = Math.max(answer, islandCounter[islandCounterIdx]);
                }

                for(int d = 0; d < M; d++) {
                    if(cnt1 == M) {
                        updatedIsland[row][col + d][0] -= mStructure[d];
                    }
                    if(cnt2 == M) {
                        updatedIsland[row][col + d][1] -= reverseStructure[d];
                    }
                    if(cnt3 == M) {
                        updatedIsland[row + d][col][2] -= mStructure[d];
                    }
                    if(cnt4 == M) {
                        updatedIsland[row + d][col][3] -= reverseStructure[d];
                    }

                }

            }
        }

        return answer;
    }

    public static class Point{
        int col;
        int row;
        int type;

        public Point(int col, int row, int type) {
            this.col = col;
            this.row = row;
            this.type = type;
        }
    }
}
class Solution {
    private final static int CMD_INIT					= 1;
    private final static int CMD_NUMBER_OF_CANDIDATE	= 2;
    private final static int CMD_MAX_AREA				= 3;

    private final static UserSolution usersolution = new UserSolution();

    private static int[][] mMap = new int[20][20];
    private static int[] mStructure = new int[5];

    private static boolean run(BufferedReader br) throws Exception
    {
        StringTokenizer st;

        int numQuery;
        int N, M, mSeaLevel;
        int userAns, ans;

        boolean isCorrect = false;

        numQuery = Integer.parseInt(br.readLine());

        for (int q = 0; q < numQuery; ++q)
        {
            st = new StringTokenizer(br.readLine(), " ");

            int cmd;
            cmd = Integer.parseInt(st.nextToken());

            switch (cmd)
            {
                case CMD_INIT:
                    N = Integer.parseInt(st.nextToken());
                    for (int i = 0; i < N; i++)
                        for (int j = 0; j < N; j++)
                            mMap[i][j] = Integer.parseInt(st.nextToken());
                    usersolution.init(N, mMap);
                    isCorrect = true;
                    break;
                case CMD_NUMBER_OF_CANDIDATE:
                    M = Integer.parseInt(st.nextToken());
                    for (int i = 0; i < M; i++)
                        mStructure[i] = Integer.parseInt(st.nextToken());
                    userAns = usersolution.numberOfCandidate(M, mStructure);
//				System.out.println("numberOfCandidate=" + userAns);
                    ans = Integer.parseInt(st.nextToken());
                    if (userAns != ans)
                    {
                        isCorrect = false;
                    }
                    break;
                case CMD_MAX_AREA:
                    M = Integer.parseInt(st.nextToken());
                    for (int i = 0; i < M; i++)
                        mStructure[i] = Integer.parseInt(st.nextToken());
                    mSeaLevel = Integer.parseInt(st.nextToken());
                    userAns = usersolution.maxArea(M, mStructure, mSeaLevel);
//                    System.out.println("maxArea=" + userAns);
                    ans = Integer.parseInt(st.nextToken());
                    if (userAns != ans)
                    {
                        isCorrect = false;
                    }
                    break;
                default:
                    isCorrect = false;
                    break;
            }
        }
        return isCorrect;
    }

    public static void main(String[] args) throws Exception
    {
        int TC, MARK;

        //System.setIn(new java.io.FileInputStream("res/sample_input.txt"));

        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(
                                new File("src/week2/island/sample_input.txt"))));
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