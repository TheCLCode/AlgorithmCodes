package com.theclcode.swc.newsletter.aug19.imdbrecommender;

import java.util.Scanner;
import java.util.*;

public class Solution {
    public static final int MAX_MOVIE = 5000;
    public static final int MAX_WATCH = 5000;
    public static final int MAX_USER = 1000;
    private static Map<Integer, User> users = new HashMap<>();
    private static Map<Integer, Movie> movies = new HashMap<>();
    private static int addedOn;

    /**************** START OF USER SOLUTION ****************/

    static void init()
    {
        users.clear();
        movies.clear();
        addedOn=1;
    }

    static void newMovie(int mid)
    {
        if(movies.get(mid)==null && movies.size()<=MAX_MOVIE){
            Movie movie = new Movie(mid, new ArrayList<>(), addedOn++);
            movies.put(mid, movie);
        }
    }

    static void addUser(int uid)
    {
        if(users.get(uid)==null && users.size()<=MAX_USER){
            User user = new User(uid, new HashMap<>());
            users.put(uid, user);
        }
    }

    static void watchMovie(int uid, int mid)
    {
        User user = users.get(uid);
        Movie movie = movies.get(mid);

        if(user!=null && movie!=null && user.moviesWatched.size()<=MAX_WATCH){
            if(user.getMoviesWatched().containsKey(movie)){
                int movieWatchedCount = user.getMoviesWatched().get(movie);
                user.getMoviesWatched().put(movie, ++movieWatchedCount);
            } else {
                user.getMoviesWatched().put(movie, 1);
            }
            user.incrementWatchCounter();
            if(!movie.getWatchers().contains(user)){
                movie.getWatchers().add(user);
            }
        }
    }

    static int getRecommendedMovie(int uid)
    {
        int numberOfTopUsers = users.size()*0.01 > 1 ? (int)(users.size()*0.01) : 1;
        User[] similarUsers = new User[numberOfTopUsers];
        User user = users.get(uid);
        Map<User, Integer> candidateSimilarUsers = new HashMap<>();

        findSimilarTasteGroupSmart(user, similarUsers, candidateSimilarUsers);
        if(candidateSimilarUsers.isEmpty()) {
            return -1;
        }
        Map<Movie, Integer> candidateRecommendedMovies = getCandidateRecommendedMovies(user, similarUsers);
        int topMovie = getTopMovie(candidateRecommendedMovies);
        return topMovie;
    }

    private static void findSimilarTasteGroupSmart(User user, User[] similarUsers, Map<User, Integer> candidateSimilarUsers) {
        for(Map.Entry<Movie, Integer> movie : user.getMoviesWatched().entrySet()){
            for(User similarUser: movie.getKey().getWatchers()){
                if(similarUser==user){
                    continue;
                }
                if(candidateSimilarUsers.containsKey(similarUser)){
                    int numOfSameMoviesWatched = candidateSimilarUsers.get(similarUser);
                    candidateSimilarUsers.put(similarUser, numOfSameMoviesWatched+1);
                } else {
                    candidateSimilarUsers.put(similarUser, 1);
                }
            }
        }

        if(!candidateSimilarUsers.isEmpty()){
            buildTopUsersSmart(similarUsers, candidateSimilarUsers);
        }
    }
    private static void buildTopUsersSmart(User[] similarUsers, Map<User, Integer> candidateSimilarUsers) {
        User[] candidateSimilarUsersArray = sortMapSmart(candidateSimilarUsers);
        for(int i=0,j=candidateSimilarUsersArray.length-1; i<similarUsers.length; i++,j--){
            similarUsers[i] = candidateSimilarUsersArray[j];
        }
    }


    private static User[] sortMapSmart(Map<User, Integer> candidateSimilarUsers) {
        User[] candidateSimilarUsersArray = new User[candidateSimilarUsers.size()];
        int counter=0;
        for(Map.Entry<User, Integer> candidateSimilarUser : candidateSimilarUsers.entrySet()){
            candidateSimilarUsersArray[counter] = candidateSimilarUser.getKey();
            counter++;
        }
        for(int i=1; i<candidateSimilarUsers.size(); i++){
            int temp = candidateSimilarUsers.get(candidateSimilarUsersArray[i]);
            User tempUser = candidateSimilarUsersArray[i];
            int j = i-1;
            while(j >= 0 && temp < candidateSimilarUsers.get(candidateSimilarUsersArray[j])){
                candidateSimilarUsersArray[j+1] = candidateSimilarUsersArray[j];
                j--;
            }
            candidateSimilarUsersArray[j+1] = tempUser;
        }
        for(int i=1; i<candidateSimilarUsers.size(); i++){
            int temp = candidateSimilarUsers.get(candidateSimilarUsersArray[i]);
            User tempUser = candidateSimilarUsersArray[i];
            int j = i-1;
            while(j >= 0 && temp==candidateSimilarUsers.get(candidateSimilarUsersArray[j])){
                if(tempUser.getWatchCounter()<candidateSimilarUsersArray[j].getWatchCounter()){
                    candidateSimilarUsersArray[j+1] = candidateSimilarUsersArray[j];
                    candidateSimilarUsersArray[j]=tempUser;
                }
                j--;
            }
        }
        for(int i=1; i<candidateSimilarUsers.size(); i++){
            int temp = candidateSimilarUsersArray[i].getWatchCounter();
            User tempUser = candidateSimilarUsersArray[i];
            int j = i-1;
            while(j >= 0 && temp==candidateSimilarUsersArray[j].getWatchCounter()
            && candidateSimilarUsers.get(candidateSimilarUsersArray[j])==candidateSimilarUsers.get(tempUser)){
                if(tempUser.getUid()>candidateSimilarUsersArray[j].getUid()){
                    candidateSimilarUsersArray[j+1] = candidateSimilarUsersArray[j];
                }
                j--;
            }
            candidateSimilarUsersArray[j+1] = tempUser;

        }
        return candidateSimilarUsersArray;
    }

    private static int getTopMovie(Map<Movie, Integer> candidateRecommendedMovies) {
        Movie mostWatched = null;
        int watchCount = 0;

        for(Map.Entry<Movie, Integer> recommendedMovie : candidateRecommendedMovies.entrySet()){
            if(mostWatched==null){
                mostWatched=recommendedMovie.getKey();
                watchCount=recommendedMovie.getValue();
                continue;
            }
            //Most watched within the taste group
            if(recommendedMovie.getValue()>watchCount){
                mostWatched = recommendedMovie.getKey();
                watchCount = recommendedMovie.getValue();
            }
            else if(recommendedMovie.getValue()==watchCount){
                if(recommendedMovie.getKey().getAdded()>mostWatched.getAdded()){
                    mostWatched=recommendedMovie.getKey();
                    watchCount= recommendedMovie.getValue();
                }
            }

        }
        if(mostWatched!=null){
            return mostWatched.getMid();
        }
        return -1;
    }

    private static Map<Movie, Integer> getCandidateRecommendedMovies(User user, User[] similarUsers) {
        Map<Movie, Integer> movieList = new HashMap<>();
        for(User similarUser : similarUsers){
            for(Map.Entry<Movie, Integer> movieWatched: similarUser.getMoviesWatched().entrySet()){
                Movie movie = movieWatched.getKey();
                if(user.getMoviesWatched().get(movie)==null){
                    if(movieList.containsKey(movie)){
                        int totalWatchCount = movieList.get(movie);
                        movieList.put(movie, totalWatchCount+movieWatched.getValue());
                    } else {
                        movieList.put(movie, movieWatched.getValue());
                    }
                }
            }
        }
        return movieList;
    }

    /***************** END OF USER SOLUTION *****************/


    public static final int CMD_INIT = 10;
    public static final int CMD_MOVIE = 20;
    public static final int CMD_USER = 30;
    public static final int CMD_WATCH = 40;
    public static final int CMD_RECOMMEND = 50;

    static Scanner sc;

    static void run() {
        int  m, cmd, arg1, arg2, ret;

        m = sc.nextInt();

        while (m-- > 0) {
            cmd = sc.nextInt();

            switch (cmd) {
                case CMD_INIT:
                    init();

                    break;

                case CMD_MOVIE:
                    arg1 = sc.nextInt();
                    newMovie(arg1);

                    break;

                case CMD_USER:
                    arg1 = sc.nextInt();
                    addUser(arg1);

                    break;

                case CMD_WATCH:
                    arg1 = sc.nextInt();
                    arg2 = sc.nextInt();
                    watchMovie(arg1, arg2);

                    break;

                case CMD_RECOMMEND:
                    arg1 = sc.nextInt();
                    ret = getRecommendedMovie(arg1);

                    System.out.printf("%d\n", ret);

                    break;
            }
        }
    }

    public static void main(String[] args) {
        int T, ANS, tc;

        sc = new Scanner(System.in);

        T = sc.nextInt();
        ANS = sc.nextInt();

        for (tc = 1; tc <= T; ++tc) {
            System.out.printf("Case #%d:\n", tc);
            run();
        }
    }

    public static class Movie {
        private int mid;
        private List<User> watchers;
        private int added;

        public Movie(int mid, List<User> watchers, int added) {
            this.added = added;
            this.mid = mid;
            this.watchers = watchers;
        }

        public int getAdded() {
            return added;
        }

        public void setAdded(int added) {
            this.added = added;
        }

        public int getMid() {
            return mid;
        }

        public void setMid(int mid) {
            this.mid = mid;
        }

        public List<User> getWatchers() {
            return watchers;
        }

        public void setWatchers(List<User> watchers) {
            this.watchers = watchers;
        }
    }

    public static class User {
        private int uid;
        //Movie, Number of times they watched the movie.
        private Map<Movie, Integer> moviesWatched;

        private int watchCounter=0;

        public User(int uid, Map<Movie, Integer> moviesWatched) {
            this.uid = uid;
            this.moviesWatched = moviesWatched;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public Map<Movie, Integer> getMoviesWatched() {
            return moviesWatched;
        }

        public void setMoviesWatched(Map<Movie, Integer> moviesWatched) {
            this.moviesWatched = moviesWatched;
        }

        public int getWatchCounter() {
            return watchCounter;
        }

        public void incrementWatchCounter() {
            this.watchCounter = getWatchCounter()+1;
        }
    }
}
