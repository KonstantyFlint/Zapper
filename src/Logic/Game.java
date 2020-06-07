package Logic;

public abstract class Game extends Grid{
    private boolean active;
    private boolean victory;

    protected Game(int width, int height, int bombCount) {
        super(width,height,bombCount);
        active=false;
        victory=false;
    }

    public boolean isActive() {
        return active;
    }
    public boolean isWon() {
        return victory;
    }

    protected void mainLoop() throws InterruptedException {
        if(active)return;
        active=true;
        while(active){
            PlayerAction move= getAction(this);
            if(move.uncover)uncover(move.x,move.y);
            else            flag(move.x,move.y);
        }
    }

    public boolean uncover(int x, int y){
        if(!active)return true;
        if(!super.uncover(x,y)){        //bomb was hit - lose condition
            victory=false;
            active=false;
            return false;
        }
        else{
            if(remaining.isEmpty()){    //no more free nodes - win condition
                victory=true;
                active=false;
            }
            return true;
        }
    }

    public abstract PlayerAction getAction(Game caller) throws InterruptedException;    //supplies actions to the game

    public class PlayerAction {
        int x;
        int y;
        boolean uncover;
        public PlayerAction(int x, int y, boolean uncover){
            this.x = x;
            this.y = y;
            this.uncover = uncover;
        }
    }
}