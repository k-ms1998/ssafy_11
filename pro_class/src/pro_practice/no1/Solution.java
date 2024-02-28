package pro_practice.no1;

import java.io.*;
import java.util.*;

/**
 * 1. 전체 영토는 NxN 영토로 구성
 * 	1-1. 각 영토에는 다스리는 군주가 있음. 이름이 중복되는 군주는 없음 & 군주는 단 하나의 영토만 통치함
 * 2. 초기에는 군주들은 동맹니나 적대 관계가 없다 & 각 영토에는 병사들이 있다
 * 3. 군주들은 서로 동맹을 한다
 * 	3-1. 동맹을 하면 전투시 함께 공격 & 방어를 한다
 * 	3-2. 두 군주들이 동맹을 하면 서로의 모든 동맹까지 함께 동맹을 맺는다
 * 		3-2-1. 이때, 적대관계가 있는 군주가 있으면 적대관계는 유지된다
 * 		3-2-2. 동맹을 맺는 군주와 적대관계에 있는 군주까지도 적대관계가 된다
 * 4. 전투는 동맹과 동맨 간의 전투다
 * 	4-1. 공격을 받는 군주가 공경하는 군주의 동맹에 속해 있는 경우에느 전투는 발생 X
 * 	4-2. 공격하는 군주 또는 그의 동맹 영토가 인접해 있는 영토만 전투가 가능
 * 		4-2-1. 인접 = 8방향 인접
 * 	4-3. 전투를 진행하면 두 동맹들은 적대관계가 된다
 * 	4-4. 공격하는 인접 동맹들은 자신이 가진 병사의 절반을 공격 대상 영토에 보내 함께 공격
 * 	4-5. 방어를 하는 군주의 인접 동맹에서도 병서의 절반씩을 보내 함께 방어함
 * 	4-6. 벙사들은 상대 병사와 1대1로 싸운다
 * 		4-6-1. 공격 병사가 방어 병사보다 더 많으면 -> 공격 성공
 * 			4-6-1-1. 새로운 군주의 병사의 수는 공격하고 남은 병사의 수
 * 		4-6-2. 방어 병사가 공격 벙사보다 더 많으면 -> 수비 성공
 * 			4-6-2-1. 방어하는 군주의 병사의 수는 방어하고 남은 병사의 수
 */
class UserSolution {

    static String[][] land;
    static Map<String, Node> monarchs;
    static Map<String, Set<String>> enemies;
    static int size;

    static final int[] dCol = {1, 1, 0, -1, -1, -1, 0, 1};
    static final int[] dRow = {0, 1, 1, 1, 0, -1, -1, -1};

    /*
     * N = 전체 영토의 크기 (4 ≤ N ≤ 25, 16 ≤ N x N ≤ 625)
     * mSoldier = 각 영토의 병사의 수
     * mMonarch = 각 영토의 군주의 이름(알파벳 소문자, 4~10자리 문자열)
     */
    void init(int N, int mSoldier[][], char mMonarch[][][])
    {
        land = new String[N][N];
        monarchs = new HashMap<>();
        enemies = new HashMap<>();
        size = N;

        for(int row = 0; row < N; row++) {
            for(int col = 0; col < N; col++) {
                String name = convertCharToString(mMonarch[row][col]);
                Node node = new Node(name, name, mSoldier[row][col], col, row);

                land[row][col] = name;
                monarchs.put(name, node);
                enemies.put(name, new HashSet<>());
            }
        }

//    	for(String key : monarchs.keySet()) {
//    		System.out.println(monarchs.get(key));
//    	}
    }

    /*
     * 각 테스트 케이스의 마지막에 호출됨
     */
    void destroy()
    {

    }

    /*
     * (8_000 회 호출)
     * 군주 mMonarchA 의 동맹들이 군주 mMonarchB 의 동맹들과 동맹을 맺는다
     *
     * 군주 mMonarchA 와 군주 mMonarchB 가 동일 하거나 이미 동맹관계이면 -1을 반환한다.
     * 군주 mMonarchA 의 동맹과 군주 mMonarchB 의 동맹 간에 적대관계가 있으면 -2를 반환한다.
     *
     * 위의 두 경우가 아닌 경우 동맹관계가 맺어지고, 1을 반환한다.
     *
     */
    int ally(char mMonarchA[], char mMonarchB[])
    {
        String monarchA = convertCharToString(mMonarchA);
        String monarchB = convertCharToString(mMonarchB);
        Node nodeA =  monarchs.get(monarchA);
        Node nodeB =  monarchs.get(monarchB);
//    	printLand(-100);
//		System.out.printf("nodeA=%s, nodeB=%s, monarchA=%s, monarchB=%s\n",
//    			nodeA, nodeB, monarchA, monarchB);

        Node rootA = findRoot(monarchA);
        Node rootB = findRoot(monarchB);

        if(rootA.name.equals(rootB.name)) {
            return -1;
        }

        Set<String> enemiesA = enemies.getOrDefault(rootA.name, new HashSet<>());
        Set<String> enemiesB = enemies.getOrDefault(rootB.name, new HashSet<>());
        Set<String> enemiesNodeA = enemies.getOrDefault(nodeA.name, new HashSet<>());
        Set<String> enemiesNodeB = enemies.getOrDefault(nodeB.name, new HashSet<>());
//    	System.out.println("enemiesA=" + enemiesA);
//    	System.out.println("enemiesB=" + enemiesB);

        if(enemiesA.contains(rootB.name) || enemiesB.contains(rootA.name)
                || enemiesA.contains(monarchB) || enemiesB.contains(monarchA)) {
            return -2;
        }

        rootA.children.add(rootB);
        rootB.parent = rootA.name;
        nodeB.parent = rootA.name;

        enemiesA.addAll(enemiesB);
        enemiesA.addAll(enemiesNodeB);
        enemies.put(rootA.name, enemiesA);

        return 1;
    }

    /*
     * (8_000 회 호출)
     * 군주 mMonarchA 와 동맹들이 군주 mMonarchB 의 영토를 공격한다.
     * 공격을 지휘하는 장수는 mGeneral 이다.
     *
     * 군주 mMonarchA 와 군주 mMonarchB 가 동맹관계 이면 -1을 반환하고, 전투는 일어나지 않는다
     *
     * 군주 mMonarchA 의 영토 또는 동맹 영토가 군주 mMonarchB 의 영토와 인접하지 않다면 -2을 반환하고, 전투는 일어나지 않는다.
     *
     *
     * 전투가 발생하면 군주 mMonarchA 의 동맹과 군주 mMonarchB 의 동맹은 서로 적대관계가 된다.
     *
     * 공격이 성공하면 군주 mMonarchB 는 처형되고,새로운 영토의 군주는 mGeneral 이 되고, mMonarchA의 동맹에 편입되며, 적대 관계는 mMonarchA 의 적대 관계와 동일하다.
     */
    int attack(char mMonarchA[], char mMonarchB[], char mGeneral[])
    {
        String monarchA = convertCharToString(mMonarchA);
        String monarchB = convertCharToString(mMonarchB);
        Node nodeA = monarchs.get(monarchA);
        Node nodeB = monarchs.get(monarchB);
//    	System.out.printf("nodeA=%s, nodeB=%s, monarchA=%s, monarchB=%s\n",
//    			nodeA, nodeB, monarchA, monarchB);
        String general = convertCharToString(mGeneral);

        Node rootA = findRoot(monarchA);
        Node rootB = findRoot(monarchB);

//    	System.out.printf("monarchA=%s, monarchB=%s, nodeA=%s, nodeB=%s, rootA=%s, rootB=%s\n",
//    			monarchA, monarchB, nodeA.name, nodeB.name, rootA.name, rootB.name);
        if(rootA.name.equals(rootB.name)) {
            return -1;
        }

        int adjA = 0;
        int adjB = 0;

        int col = nodeB.col;
        int row = nodeB.row;
        for(int dir = 0; dir < 8; dir++) {
            int nCol = col + dCol[dir];
            int nRow = row + dRow[dir];

            if(nCol < 0 || nRow < 0 || nCol >= size || nRow >= size) {
                continue;
            }

            String nextName = land[nRow][nCol];
            Node adjNode = monarchs.get(nextName);
            Node nextRoot = findRoot(nextName);

            if(nextRoot.name.equals(rootA.name)) { // 공격과 동맹
                adjA++;
            }
            if(nextRoot.name.equals(rootB.name)) { // 방어와 동맹
                adjB++;
            }
        }

        if(adjA == 0) {
            return -2;
        }

        int attackSoldierA = 0;
        int defendSoldierB = nodeB.soldier;
        for(int dir = 0; dir < 8; dir++) {
            int nCol = col + dCol[dir];
            int nRow = row + dRow[dir];

            if(nCol < 0 || nRow < 0 || nCol >= size || nRow >= size) {
                continue;
            }

            String nextName = land[nRow][nCol];
            Node adjNode = monarchs.get(nextName);
            Node nextRoot = findRoot(nextName);

            if((nextRoot.name.equals(rootA.name) || (nextRoot.name.equals(nodeA.name)))
                    && adjNode.soldier > - 1) { // 공격과 동맹
                if(general.equals("cxqfxtus")) {
                    System.out.printf("adjNode=%s\n", adjNode);
                }
                attackSoldierA += (adjNode.soldier/2);
                adjNode.soldier -= (adjNode.soldier/2);
            }
            if(nextRoot.name.equals(rootB.name) && adjNode.soldier > - 1) { // 방어와 동맹
                defendSoldierB += (adjNode.soldier/2);
                adjNode.soldier -= (adjNode.soldier/2);
            }
        }

        Set<String> rootAEnemies = enemies.getOrDefault(rootA.name, new HashSet<>());
        Set<String> rootBEnemies = enemies.getOrDefault(rootB.name, new HashSet<>());

        rootAEnemies.add(rootB.name);
        rootAEnemies.add(nodeB.name);
        rootBEnemies.add(rootA.name);
        rootBEnemies.add(nodeA.name);

        enemies.put(rootA.name, rootAEnemies);
        enemies.put(rootB.name, rootBEnemies);
        enemies.put(nodeA.name, rootAEnemies);
        enemies.put(nodeB.name, rootBEnemies);

        int diff = Math.abs(attackSoldierA - defendSoldierB);
        if(general.equals("cxqfxtus")) {
            System.out.printf("diff=%d, rootA=%s, rootB=%s, attack=%d, defend=%d\n",
                    diff, rootA.name, rootB.name, attackSoldierA, defendSoldierB);
            printLand(-100);
        }

        if(attackSoldierA > defendSoldierB) {
            Node freshNode = new Node(rootA.name, general, diff, col, row);
            nodeB.soldier = -1;
            monarchs.put(general, freshNode);
            enemies.put(general, new HashSet<>());
            land[row][col] = general;

            return 1;
        }else {
            nodeB.soldier = diff;

            return 0;
        }
    }

    /*
     * (13_000 회 호출)
     * 병사를 모집한다.
     *
     * mOption 이 0 일 때,
     * 	군주 mMonarch 의 영토에 mNum 명의 병사를 모집한다.
     * 	병사 모집 이후에 군주 mMonarch 영토의 병사의 수를 반환한다.
     *
     * mOption 이 1 일 때,
     * 	군주 mMonarch 를 포함한 모든 동맹의 영토에 각각 mNum 명의 병사를 모집한다.
     * 	병사 모집 이후에 군주 mMonarch 동맹의 모든 병사의 수 합산하여 반환한다.
     */
    int recruit(char mMonarch[], int mNum, int mOption)
    {
        String name = convertCharToString(mMonarch);
        if(name.equals("vjuzo")) {
            System.out.println("FLAG!!");
        }
        if(mOption == 0) {
            Node node = monarchs.get(name);
            node.soldier += mNum;
            if((name.equals("vjuzo"))) {
                System.out.println("node=" + node + ", mNum=" + mNum);
            }
            return node.soldier;
        }else {
            int totalSoldier = 0;
            Node root = findRoot(name);
            for(int row = 0; row < size; row++) {
                for(int col = 0; col < size; col++) {
                    Node curNode = monarchs.get(land[row][col]);
                    Node curRoot = findRoot(land[row][col]);
                    if((curRoot.name.equals(root.name) || (curRoot.name.equals(name)))
                            && curNode.soldier > -1) {
                        curNode.soldier += mNum;
                        totalSoldier += curNode.soldier;
                        if(land[row][col].equals("vjuzo")) {
                            System.out.printf("[recruit] root=%s, node=%s\n", root, curNode);
                        }
                    }
                }
            }

            return totalSoldier;
        }

    }

    public static String convertCharToString(char[] input) {
        StringBuilder output = new StringBuilder();
        for(int idx = 0; idx < input.length; idx++) {
            output.append(input[idx]);
        }

        return output.toString().trim();
    }

    public static Node findRoot(String name) {
        Node node = monarchs.get(name);

        if(node.parent.equals(name)) {
            return node;
        }

        String parentName = node.parent;
        Node parentNode = monarchs.get(parentName);
        if(parentNode == null) {
            return node;
        }

        Node nextParent = findRoot(node.parent);
        node.parent = nextParent.parent;

        return nextParent;
    }

    public static void printLand(int idx) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("#%d\n", idx + 1));
        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                String name = land[row][col];
                sb.append(monarchs.get(name)).append(" ");
            }
            sb.append("\n");
        }
        sb.append("----------\n");

        System.out.println(sb);
    }

    public static class Node{
        String parent;
        String name;
        int soldier;
        int col;
        int row;
        List<Node> children;

        public Node(String parent, String name, int soldier, int col, int row) {
            this.parent = parent;
            this.name = name;
            this.soldier = soldier;
            this.col = col;
            this.row = row;
            this.children = new ArrayList<>();
        }

        @Override
        public String toString() {
            return String.format("[parent=%s, name=%s,soldier=%d]", parent, name, soldier);
//    		return String.format("[name=%s, soldier=%d, col=%d, row=%d]", name, soldier, col, row);
        }
    }
}
class Solution {
    private static Scanner sc;
    private static UserSolution usersolution = new UserSolution();

    static final int MAX_N = 25;
    static final int MAX_L = 10;

    static final int CMD_INIT = 100;
    static final int CMD_DESTROY = 200;
    static final int CMD_ALLY = 300;
    static final int CMD_ATTACK = 400;
    static final int CMD_RECRUIT = 500;

    static int [][] Sol= new int [MAX_N][MAX_N];
    static char [][][] Monarch = new char [MAX_N][MAX_N][MAX_L+1];

    private static void String2Char(char[] buf, String str) {
        Arrays.fill(buf, (char)0);
        for (int i = 0; i < str.length(); ++i)
            buf[i] = str.charAt(i);
        buf[str.length()] = '\0';
    }

    private static int run() throws IOException {
        int isOK = 0;

        int mN;
        char[] mMonarchA= new char [MAX_L + 1];
        char[] mMonarchB = new char [MAX_L + 1];
        char[] mGeneral= new char [MAX_L + 1];
        int mOption;
        int num;

        int N = sc.nextInt();
        int cmd, result, check;

        for (int c = 0; c < N; ++c) {

            cmd =  sc.nextInt();
            switch (cmd) {
                case CMD_INIT:
                    mN = sc.nextInt();
                    for (int j = 0; j < mN; j++)
                        for (int i = 0; i < mN; i++)
                            Sol[j][i] =  sc.nextInt();

                    for (int j = 0; j < mN; j++)
                        for (int i = 0; i < mN; i++)
                            String2Char(Monarch[j][i],  sc.next());

                    usersolution.init(mN, Sol, Monarch);
//                usersolution.printLand(c);
                    isOK = 1;
                    break;

                case CMD_ALLY:
                    String2Char(mMonarchA,  sc.next());
                    String2Char(mMonarchB,  sc.next());
                    if(usersolution.convertCharToString(mMonarchA).equals("cxqfxtus") ||
                            usersolution.convertCharToString(mMonarchB).equals("cxqfxtus")) {
                        System.out.printf("#idx=%d, [ALLY] mMonarchA=%s, mMonarchB=%s\n",
                                c, usersolution.convertCharToString(mMonarchA), usersolution.convertCharToString(mMonarchB));
                    }
                    result = usersolution.ally(mMonarchA, mMonarchB);
//                System.out.println("ally=" + result);
                    check = sc.nextInt();
                    if (result != check)
                        isOK = 0;
                    break;

                case CMD_ATTACK:
                    String2Char(mMonarchA,  sc.next());
                    String2Char(mMonarchB,  sc.next());
                    String2Char(mGeneral,  sc.next());

                    boolean flag = false;
                    if(usersolution.convertCharToString(mMonarchA).equals("cxqfxtus") ||
                            usersolution.convertCharToString(mMonarchB).equals("cxqfxtus") ||
                            usersolution.convertCharToString(mGeneral).equals("cxqfxtus")) {
                        System.out.printf("#idx=%d, [ATTACK] mMonarchA=%s, mMonarchB=%s, mGeneral=%s\n",
                                c, usersolution.convertCharToString(mMonarchA), usersolution.convertCharToString(mMonarchB), usersolution.convertCharToString(mGeneral));
                        flag = true;
                    }
                    result = usersolution.attack(mMonarchA, mMonarchB, mGeneral);
                    if(flag) System.out.println("attack=" + result);
                    check = sc.nextInt();
                    if (result != check)
                        isOK = 0;
                    break;

                case CMD_RECRUIT:
                    String2Char(mMonarchA,  sc.next());
                    num = sc.nextInt();
                    mOption = sc.nextInt();
                    if(usersolution.convertCharToString(mMonarchA).equals("cxqfxtus")) {
                        System.out.printf("#idx=%d, [RECRUIT] mMonarchA=%s, mMonarchB=%s\n",
                                c, usersolution.convertCharToString(mMonarchA), usersolution.convertCharToString(mMonarchB));
                    }
                    result = usersolution.recruit(mMonarchA, num, mOption);
                    System.out.printf("[RECRUIT] #idx=%d, recruit=%d, node=%s, root=%s\n", c + 1, result, usersolution.convertCharToString(mMonarchA), usersolution.findRoot(usersolution.convertCharToString(mMonarchA)));
                    check = sc.nextInt();
                    if (result != check)
                        isOK = 0;
                    break;
            }
//            usersolution.printLand(c);
        }

        usersolution.destroy();
        return isOK;
    }

    public static void main(String[] args) throws Exception {
        int T, MARK;
        //System.setIn(new java.io.FileInputStream("res/sample_input.txt"));
        sc = new Scanner(new FileInputStream("C:\\Users\\pies6\\Desktop\\ssafy_git\\ssafy_11\\pro_class\\src\\pro_practice\\no1\\sample_input.txt"));

        T = sc.nextInt();
        MARK = sc.nextInt();
        for (int tc = 1; tc <= T; tc++) {

            if (run() == 1)
                System.out.println("#" + tc + " " + MARK);
            else
                System.out.println("#" + tc + " 0");
        }
        sc.close();
    }
}