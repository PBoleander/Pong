package general;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Viewer extends Canvas implements Runnable {

    public static int ALTO, ANCHO;
    public static final int TIEMPO_REFRESCO = 20;

    private BufferedImage borrador;
    private Graphics graphics;
    private final Juego juego;

    Viewer(int alto, int ancho) {
        super();

        ALTO = alto;
        ANCHO = ancho;

        this.juego = new Juego();
        addKeyListener(juego);

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(ANCHO, ALTO));

        new Thread(this).start();
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(pintarBorrador(), 0, 0, null);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void run() {
        try {
            while (!isFocusOwner()) requestFocus();

            while (!juego.isPausa()) {
                repaint();
                Thread.sleep(TIEMPO_REFRESCO);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage pintarBorrador() {
        // Inicializarlos en el constructor no funciona (todavía no debe ser displayable) pero si pones que espere a que
        // lo sea tampoco va bien
        if (this.borrador == null) {
            this.borrador = (BufferedImage) createImage(ANCHO, ALTO);
            this.graphics = borrador.createGraphics();
        }

        this.graphics.clearRect(0, 0, ANCHO, ALTO);
        this.graphics.setColor(getForeground());
        
        this.juego.pintar(graphics);

        return borrador;
    }
}
