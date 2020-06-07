package Logic;

import java.util.*;

public class Grid {
    private static Random random = new Random();

    private Node[][] nodes;
    protected int width;
    protected int height;
    private int nodeCount;
    protected int targetBombCount;
    private boolean generated=false;
    protected HashSet<Node> remaining = new HashSet<Node>();            //remaining empty nodes to uncover
    protected HashSet<Node> bombs = new HashSet<Node>();

    Grid(int width, int height, int targetBombCount){
        this.width=width;
        this.height=height;
        this.nodeCount=width*height;
        this.targetBombCount = targetBombCount;
        nodes = new Node[height][width];
        createBlank();
    }
    public int getWidth() { return width; }
    public int getHeight() {
        return height;
    }

    public String toString(){
        StringBuilder out = new StringBuilder();
        int rowCount=0;
        for(Node[] row:nodes){
            for(Node node:row){
                out.append(node+" ");
            }
            out.append("|"+rowCount+++"\n");
        }
        for(int i=0;i<width;i++)out.append("--");
        out.append("\n");
        for(int i=0;i<width;i++)out.append(i%10+" ");
        return out.toString();
    }

    public Node getNode(int x, int y){
        return validPosition(x,y)?nodes[y][x]:null;
    }

    private boolean validPosition(int x, int y){
        return x>=0 && x<width && y>=0 && y<height;
    }

    /*
    a b c d e f g h
    i j
    createBlank builds the field by appending new nodes, joining to 3 above and one to the left;
    if neighbour doesn't exist e.g.(-1,0), the get(int,int) function returns null, which is discarded in constructor
    a starts with no neighbours
    b connects to a
    i connects to a, b
    j connects to a, b, c, i
     */
    private void createBlank(){
        for(int h=0;h<height;h++){
            for(int w=0;w<width;w++){
                nodes[h][w] = new Node(getNode(w-1,h-1), getNode(w,h-1), getNode(w+1,h-1), getNode(w-1,h));
                remaining.add(nodes[h][w]);
            }
        }
    }

    private void generateBombs(int x, int y, int safeDist){       //x, y - initial click coordinates
        int bombCount=targetBombCount;
        List<Node> bombCandidates = new ArrayList<Node>(remaining);
        for(int h=y-safeDist;h<=y+safeDist;h++){                            //making safe parameter
            for(int w=x-safeDist;w<=x+safeDist;w++){
                bombCandidates.remove(getNode(w,h));
            }
        }
        if(bombCandidates.size()<bombCount)bombCount=bombCandidates.size(); //capping the bombCount (there might not be enough candidates)
        Collections.shuffle(bombCandidates,random);
        bombCandidates=bombCandidates.subList(0,bombCount);
        for(Node target:bombCandidates){
            target.putBomb();
            bombs.add(target);
        }
        remaining.removeAll(bombCandidates);
        generated=true;
    }

    protected boolean uncover(int x, int y){                          //returns whether the player survived
        Node target = getNode(x,y);
        if(target.flagged)return true;
        if(!generated){
            generateBombs(x,y,1);
        }
        remaining.removeAll(target.uncover());
        if(target.hasBomb)uncoverBombs();
        return !target.hasBomb;
    }

    protected void flag(int x, int y){
        getNode(x,y).flag();
    }

    private void uncoverBombs(){ for(Node bomb:bombs)bomb.uncover(); }
}