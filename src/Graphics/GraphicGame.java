package Graphics;

import Logic.Game;
import Logic.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GraphicGame extends Game implements MouseListener {
    JFrame display;
    JButton[][] buttons;
    BlockingQueue<PlayerAction> moves = new LinkedBlockingQueue<PlayerAction>(10);
    ImageIcon[] tilesNumeric;
    HashMap<Node.State,Icon> tilesSpecial;

    public static void main(String[] args) throws InterruptedException {
        new GraphicGame(10,10,25);
    }

    GraphicGame(int width, int height, int bombs) throws InterruptedException {
        super(width,height,bombs);
        buttons = new JButton[width][height];
        display = new JFrame();
        display.setLayout(new GridLayout(height,width));
        display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //display.setLocationRelativeTo(null);
        display.setSize(width*50+16,height*50+60);

        loadImages();
        addButtons();
        addMenuBar();
        display.setVisible(true);

        mainLoop();
        updateButtons();

        if(yesNoPopup(isWon()?"You won":"You lost"))restart();
        else System.exit(0);
    }

    public PlayerAction getAction(Game caller) throws InterruptedException {
        updateButtons();
        return moves.take();
    }

    private void addButtons(){
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                JButton button=new JButton();
                button.setActionCommand(String.format("%d %d",x,y));
                button.addMouseListener(this);
                button.setSize(50,50);
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setBorder(null);
                buttons[x][y]=button;
                display.add(button);
            }
        }
    }

    private void updateButton(int x, int y){
        Icon icon;
        Node.State state =  getNode(x,y).getState();
        int count =         getNode(x,y).getBombCount();
        if(state!= Node.State.EMPTY || count==0)icon= tilesSpecial.get(getNode(x,y).getState());
        else icon=tilesNumeric[count-1];
        buttons[x][y].setIcon(icon);
    }

    private void updateButtons(){
        for(int y=0;y<getHeight();y++) {
            for (int x = 0; x < getWidth(); x++) {
                //buttons[x][y].setText(getNode(x, y).toString());
                updateButton(x,y);
            }
        }
    }

    private void loadImages(){
        String imgPath = this.getClass().getResource("images/").getPath();
        tilesNumeric = new ImageIcon[8];
        tilesSpecial = new HashMap<Node.State,Icon>(4);
        for(int i=0;i<8;i++){
            tilesNumeric[i]=new ImageIcon(String.format("%s%d.png",imgPath,i+1));
        }
        String[] names= new String[]{"hidden","flag","bomb","empty"};
        int i=0;
        for(Node.State state:Node.State.values()){
            tilesSpecial.put(state,new ImageIcon(String.format("%s%s.png",imgPath,names[i++])));
        }
    }

    private void addMenuBar() {
        JMenuBar mb = new JMenuBar();
        display.setJMenuBar(mb);
        JMenu menu = new JMenu("new game...");
        mb.add(menu);
        JMenuItem restart = new JMenuItem("restart");
        JMenuItem custom = new JMenuItem("custom");
        menu.add(restart);
        menu.add(custom);

        restart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {restart();}});
        custom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {newCustom();}});
    }

    protected void restart(){
        display.dispose();
        new Thread(new Runnable() {
            public void run() {
                try {new GraphicGame(width,height,targetBombCount);}
                catch (Exception e){}
            }
        }).start();
    }

    protected void newCustom(){
        ArrayList<Integer> args = argsPopup();
        if(args.size()==0)return;//System.exit(0);
        display.dispose();
        for(int i=0;i<=1;i++){
            if(args.get(i)<0)args.set(i,3);
            else if(args.get(i)>20)args.set(i,20);
        }
        new Thread(() -> {
            try {new GraphicGame(args.get(0),args.get(1),args.get(2));}
            catch (Exception e){}
        }).start();
    }

    protected ArrayList<Integer> argsPopup(){
        ArrayList<Integer> out = new ArrayList<Integer>(3);
        JTextField width = new JTextField(5);
        JTextField height = new JTextField(5);
        JTextField bombs = new JTextField(5);

        JPanel myPanel = new JPanel();

        myPanel.add(new JLabel("width:"));
        myPanel.add(width);

        myPanel.add(new JLabel("height:"));
        myPanel.add(height);

        myPanel.add(new JLabel("bombs:"));
        myPanel.add(bombs);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                out.add(Integer.parseInt(width.getText()));
                out.add(Integer.parseInt(height.getText()));
                out.add(Integer.parseInt(bombs.getText()));
            }
            catch(Exception e){return argsPopup();}
        }
        return out;
    }

    protected boolean yesNoPopup(String text){
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(this.display, "play again?", text, dialogButton);
        return (dialogResult == JOptionPane.OK_OPTION);
    }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        if(!isActive())return;
        String [] split = ((JButton)e.getComponent()).getActionCommand().split(" ");
        int x=Integer.parseInt(split[0]);
        int y=Integer.parseInt(split[1]);
        moves.offer(new PlayerAction(x,y,e.getButton()==1));
        //System.out.println(this);
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}