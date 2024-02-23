package pro_practice.no2_leauge;

import java.io.*;
import java.util.*;


/**
 * 1. N명의 선수들이 경기를 진행하려고 한다
 * 	1-0. 선수들의 수 < 40_000
 * 	1-1. 선수들이 많기 때문에 여러 개의 리그로 나눠서 경기를 진행
 * 	1-2. 리그의 개수는 L개(0~L-1개의 id 값을 가진다) -> id 값이 작을 수록 우수한 리그
 * 2. N명의 선수들은 0부터 N-1까지의 id 값과 각각 능력 값을 가지고 있다
 * 	2-1. 능력 값이 높을 수록 좋은 선수 -> 능력 값이 같으면 id가 작을 수록 더 능력 좋은 선수
 * 3. 리그 승강제를 도입
 * 	3-1. 각각의 리그에서 능력이 가장 좋지 않은 선수는 바로 아래 리그로 내려가고, 리그에서 증력이 가장 놓은 선수는 바로 위 이그로 올라감
 * 	3-2. 위의 리그 = i - 1, 아래의 리그 = i + 1
 * 4. 트레이드 제도 도입
 * 	4-1. 각각 리그에서 능력이 가장 좋은 선수를 바로 위 리그의 중간 급 능력의 선수와 맞교환
 * 	4-2. 리그 내의 중간 급 능력 선수 = (M+1)/2 번째 선수
 *
 * 다음과 같은 과정을 통해 각 리그의 선수들을 바꾸고 순서를 유지함
 * 1. 바꿀 선수들을 각 리그에서 제거하고, 따로  SwapInfo를 통해 어떤 리그에서 어떤 선수 끼리 바꿀지 기록하고 있는다
 * 2. 모든 리그들에 대해서 이를 수행했을때,모든 SwapInfo를 탐색
 * 3. SwapInfo에 따라서 두 선수를 바꾼다
 * 	3-0. 이미 각 리그의 선수둘은 정렬되어 있음 -> 새로 추가될 선수의 위치만 정해주면 정렬된 상태가 유지된다
 * 	3-1. 이분탐색을 통해 하위 리그에서 데려온 선수를 상위 리그에서의 위치(순서)를 정해준다 -> 해당 위치에 삽입
 * 	3-2. 이분탐색을 통해 상위 리그에서 데려온 선수를 하위 리그에서의 위치(순서)를 정해준다 -> 해당 위치에 삽입
 */
class UserSolution {

    static int totalPlayers;
    static int totalLeagues;
    static int perLeague;
    static List<Player>[] league;

    /*
     * 선수의 수, 리그의 수, 선수의 능력치가 주어짐
     * 선수들은 순서대로 리그에 배치됨 -> 처음에 최초로 모든 리그들을 정렬한다
     */
    void init(int N, int L, int mAbility[]) {
        totalPlayers = N;
        totalLeagues = L;
        perLeague = N / L;
        league = new List[totalLeagues];

        for(int idx = 0; idx < totalLeagues; idx++) {
            league[idx] = new ArrayList<>();
        }
        for(int id = 0; id < N; id++) {
            int ability = mAbility[id];

            int leagueId = id / perLeague;
            int idx = id % perLeague;

            league[leagueId].add(new Player(id, ability));
        }
        sortLeagues();
    }

    /*
     * (최대 500번 호출)
     * 리그 승강제
     *
     */
    int move() {
        int result = 0;

        Deque<SwapInfo> infos = new ArrayDeque<>();
        for(int currentLeague = 1; currentLeague < totalLeagues; currentLeague++) {
            int upperLeague = currentLeague - 1;
            int upperLeagueIdx = league[upperLeague].size() - 1;
            infos.offer(new SwapInfo(league[upperLeague].get(upperLeagueIdx), upperLeague, league[currentLeague].get(0), currentLeague));

            result += league[upperLeague].get(upperLeagueIdx).id + league[currentLeague].get(0).id;
            remove(upperLeague, upperLeagueIdx, currentLeague, 0);
        }
        while(!infos.isEmpty()) {
            SwapInfo info =  infos.poll();
            sortLeague(info.playerB, info.leagueA);
            sortLeague(info.playerA, info.leagueB);
        }

        return result;
    }

    /*
     * (최대 1000번 호출)
     * 트레이드 제도
     */
    int trade() {
        int result = 0;

        Deque<SwapInfo> infos = new ArrayDeque<>();
        for(int currentLeague = 1; currentLeague < totalLeagues; currentLeague++) {
            int upperLeague = currentLeague - 1;
            int upperLeagueIdx = ((league[upperLeague].size() + 1) / 2) - 1;
            infos.offer(new SwapInfo(league[upperLeague].get(upperLeagueIdx), upperLeague, league[currentLeague].get(0), currentLeague));

            result += league[upperLeague].get(upperLeagueIdx).id + league[currentLeague].get(0).id;
            remove(upperLeague, upperLeagueIdx, currentLeague, 0);

        }
        while(!infos.isEmpty()) {
            SwapInfo info =  infos.poll();
            // playerA <-> playerB
            sortLeague(info.playerB, info.leagueA);
            sortLeague(info.playerA, info.leagueB);
        }

        return result;
    }

    public static void sortLeagues() {
        for(int leagueId = 0; leagueId < totalLeagues; leagueId++) {
            Collections.sort(league[leagueId]);
        }
    }

    // 새로 들어오는 선수(player)의 다른 리그(leagueIdx)의 위치를 정해서 삽입
    public static void sortLeague(Player player, int leagueIdx) {
        // 시간 절약을 위해 이분탐색
        int insertIdx = Collections.binarySearch(league[leagueIdx], player);

        if(insertIdx < 0) {
            insertIdx = -(insertIdx+1);
        }

        league[leagueIdx].add(insertIdx, player);
    }

    // 각 리그에서 다른 리그로 보낼 선수들을 제거
    public static void remove(int leagueA, int leagueAIdx, int leagueB, int leagueBIdx) {
        league[leagueA].remove(leagueAIdx);
        league[leagueB].remove(leagueBIdx);
    }

    public static class SwapInfo{
        Player playerA;
        int leagueA;
        Player playerB;
        int leagueB;


        public SwapInfo(Player playerA, int leagueA, Player playerB, int leagueB) {
            this.playerA = playerA;
            this.leagueA = leagueA;
            this.playerB = playerB;
            this.leagueB = leagueB;
        }
    }


    public static class Player implements Comparable<Player>{
        int id;
        int ability;

        public Player(int id, int ability) {
            this.id = id;
            this.ability = ability;
        }

        @Override
        public int compareTo(Player p) {
            if(p.ability == this.ability) {
                return this.id - p.id;
            }
            return p.ability - this.ability;
        }

        @Override
        public String toString() {
            return String.format("[id=%d, ability=%d]", id, ability);
        }
    }

}
class Solution {
    private static Scanner sc;
    private static UserSolution usersolution = new UserSolution();

    private final static int CMD_INIT = 100;
    private final static int CMD_MOVE = 200;
    private final static int CMD_TRADE = 300;

    private static boolean run() throws Exception {

        int query_num = sc.nextInt();
        int ans;
        boolean ok = false;

        for (int q = 0; q < query_num; q++) {
            int query = sc.nextInt();

            if (query == CMD_INIT) {
                int N = sc.nextInt();
                int L = sc.nextInt();
                int mAbility[] = new int[N];
                for (int i = 0; i < N; i++){
                    mAbility[i] = sc.nextInt();
                }
                usersolution.init(N, L, mAbility);
                ok = true;
            } else if (query == CMD_MOVE) {
                int ret = usersolution.move();
                ans = sc.nextInt();
                if (ans != ret) {
                    ok = false;
                }
            } else if (query == CMD_TRADE) {
                int ret = usersolution.trade();
                ans = sc.nextInt();
                if (ans != ret) {
                    ok = false;
                }
            }
        }
        return ok;
    }

    public static void main(String[] args) throws Exception {
        int T, MARK;

        System.setIn(new java.io.FileInputStream("./ssafy_11/pro_class/src/pro_practice/no2_leauge/sample_input.txt"));
        sc = new Scanner(System.in);
        T = sc.nextInt();
        MARK = sc.nextInt();

        for (int tc = 1; tc <= T; tc++) {
            int score = run() ? MARK : 0;
            System.out.println("#" + tc + " " + score);
        }
        sc.close();
    }
}