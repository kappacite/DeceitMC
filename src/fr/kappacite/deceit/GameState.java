package fr.kappacite.deceit;

public enum GameState {

    LOBBY(true), PREGAME(false), GAME(false);

    private static GameState current;

    GameState(boolean b){
    }

    public static GameState getState(){
        return current;
    }

    public static void setState(GameState state){
        current = state;
    }

    public static boolean isState(GameState state){
        return current == state;
    }

}
