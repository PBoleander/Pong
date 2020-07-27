package general;

import javax.swing.JFrame;

class Pong extends JFrame {

    Pong() {
        super();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Pong");
        setResizable(false);

        add(new Viewer(740, 1000));

        pack();

        setVisible(true);
    }

    public static void main(String[] args) {
        new Pong();
    }
}
