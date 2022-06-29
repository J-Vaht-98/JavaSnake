import javax.swing.*;

public class GameFrame extends JFrame {
    GameFrame(){
        //See aken pmst
        GamePanel panel = new GamePanel();
        this.add(panel);
        this.setTitle("SnakeGame");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
