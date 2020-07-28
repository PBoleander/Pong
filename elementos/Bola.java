package elementos;

import general.Viewer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Bola {

    public static final int DIAMETRO = 20;

    private final Random random = new Random();

    private boolean colisionHorizontal, colisionVertical;
    private int x, y;
    private int vx, vy;

    public Bola() {
        reiniciar();
    }

    public synchronized void devolver() {
        do {
            this.vx = -(10 + random.nextInt(6));
            this.vy = -10 + random.nextInt(21);
        } while (vy == 0 || Math.sqrt(vx * vx + vy * vy) >= DIAMETRO / 1.5);
    }

    public synchronized double getTangenteAngulo() {
        return (double) this.vy / this.vx;
    }

    public synchronized int getVy() {
        return this.vy;
    }

    public synchronized int getX() {
        return this.x;
    }

    public synchronized int getY() {
        return this.y;
    }

    public synchronized void mover() {
        if (colisionHorizontal) {
            acelerar();
            vy = -vy;
            colisionHorizontal = false;
        }
        if (colisionVertical) {
            acelerar();
            vx = -vx;
            colisionVertical = false;
        }

        this.x += vx;
        this.y += vy;
    }

    public void pintar(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, DIAMETRO, DIAMETRO);
    }

    public synchronized void reiniciar() {
        this.x = Viewer.ANCHO - DIAMETRO - 1;
        this.y = 1 + random.nextInt(Viewer.ALTO - DIAMETRO - 1);

        do {
            this.vx = -(5 + random.nextInt(6));
            this.vy = -10 + random.nextInt(21);
        } while (vy == 0 || Math.sqrt(vx * vx + vy * vy) >= DIAMETRO / 2D);
    }

    public void setColisionHorizontal() {
        this.colisionHorizontal = true;
    }

    public void setColisionVertical() {
        this.colisionVertical = true;
    }

    private synchronized void acelerar() {
        if (Math.sqrt(vx * vx + vy * vy) < DIAMETRO / 2D) {
            this.vx += (vx < 0) ? -1 : 1;
            this.vy += (vy < 0) ? -1 : 1;
        }
    }
}
