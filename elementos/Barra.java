package elementos;

import general.Viewer;

import java.awt.Color;
import java.awt.Graphics;

public class Barra {

    public static final int ALTURA = 150;
    public static int GROSOR = 25;
    private final int velocidad = 15;
    private int y;

    public Barra() {
        reiniciar();
    }

    public synchronized int getY() {
        return this.y;
    }

    public synchronized void moverAbajo() {
        this.y += Math.min(Viewer.ALTO - ALTURA - y, this.velocidad);
    }

    public synchronized void moverArriba() {
        this.y -= Math.min(this.y, this.velocidad);
    }

    public void pintar(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(0, y, GROSOR, ALTURA);
    }

    public synchronized void reiniciar() {
        this.y = (Viewer.ALTO - ALTURA) / 2;
    }
}
