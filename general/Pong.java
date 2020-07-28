package general;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;

class Pong extends JFrame {

    Pong() {
        super();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Pong");
        setResizable(false);

        add(new Viewer(740, 1000));

        pack();

        centrarEnPantalla();

        setVisible(true);
    }

    private void centrarEnPantalla() {
        // Averigua el tamaño de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int anchuraPantalla = (int) screenSize.getWidth();
        int alturaPantalla = (int) screenSize.getHeight();

        // Centra la ventana sabiendo su tamaño
        int anchuraVentana = this.getWidth();
        int alturaVentana = this.getHeight();
        this.setLocation((anchuraPantalla - anchuraVentana) / 2, (alturaPantalla - alturaVentana) / 2);
    }

    public static void main(String[] args) {
        new Pong();
    }
}
