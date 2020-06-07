package Logic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Node{
    private HashSet<Node> neighbours = new HashSet<Node>();
    protected boolean visible=false;
    protected boolean hasBomb=false;
    protected boolean flagged=false;
    protected int bombNeighbours=0;

    protected Node(){}
    protected Node(Node... neighbours){
        this(new HashSet<Node>(Arrays.asList(neighbours)));
    }
    protected Node(Set<Node> neighbours){
        this.neighbours = (HashSet<Node>) neighbours;
        this.neighbours.remove(null);
        for(Node neighbour:neighbours)neighbour.connectTo(this);
    }
    public int getBombCount(){
        if(!visible)return -1;
        else return bombNeighbours;
    }
    public State getState(){
        if(!visible)return flagged? State.FLAG: State.HIDDEN;
        if(hasBomb)return State.BOMB;
        return State.EMPTY;
    }
    public String toString(){
        State type= getState();
        switch (type){
            case BOMB: return "*";
            case FLAG: return "!";
            case HIDDEN: return "?";
            default: return bombNeighbours==0?" ":Integer.toString(bombNeighbours);
        }
    }

    private void connectTo(Node other){
        this.neighbours.add(other);
    }
    private void join(Node other){
        this.connectTo(other);
        other.connectTo(this);
    }
    protected boolean putBomb(){
        if(hasBomb)return false;
        else{
            hasBomb=true;
            for(Node neighbour:neighbours)neighbour.bombNeighbours++;
            return true;
        }
    }
    protected void flag(){
        if(!visible)flagged=!flagged;
    }
    protected Set<Node> uncover(){              //uncovers and returns uncovered nodes
        HashSet<Node> out = new HashSet<Node>();
        if(visible || flagged)return out;
        visible=true;
        out.add(this);
        if(hasBomb)return out;
        if(bombNeighbours==0){
            for(Node neighbour:neighbours)out.addAll(neighbour.uncover());
        }
        return out;
    }

    public enum State {
        HIDDEN, FLAG, BOMB,EMPTY
    }
}