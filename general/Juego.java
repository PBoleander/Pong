package general;

import elementos.Barra;
import elementos.Bola;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class Juego implements KeyListener, Runnable {

    private final Barra barra;
    private final Bola bola;
    private final Color colorPorteria = new Color(0, 200, 0, 100);
    private final Font fuente = new Font(Font.DIALOG, Font.BOLD, 30);

    private boolean pausa;
    private int golesEnContra, toques;

    Juego() {
        this.barra = new Barra();
        this.bola = new Bola();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {}

    @Override
    public synchronized void keyPressed(KeyEvent keyEvent) {
        int tecla = keyEvent.getKeyCode();

        int xBola = this.bola.getX();
        int yBola = this.bola.getY();
        int yBarra = this.barra.getY();
        Rectangle rBarra = new Rectangle(0, yBarra, Barra.GROSOR, Barra.ALTURA);
        Rectangle rBola = new Rectangle(xBola, yBola, Bola.DIAMETRO, Bola.DIAMETRO);

        if (tecla == KeyEvent.VK_P) pausarReanudar();
        else if (!rBarra.intersects(rBola)) {
            if (!pausa && (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S)) this.barra.moverAbajo();
            else if (!pausa && (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W)) this.barra.moverArriba();
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {}

    @Override
    public void run() {
        try {
            while (!pausa) {
                controlColisiones();
                Thread.sleep(Viewer.TIEMPO_REFRESCO);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void iniciar() {
        new Thread(this).start();
    }

    protected synchronized boolean isPausa() throws InterruptedException {
        while (pausa) wait();
        return false;
    }

    protected void pintar(Graphics g) {
        g.setFont(fuente);
        FontMetrics fm = g.getFontMetrics();

        g.drawString(String.valueOf(this.toques), Barra.GROSOR + 10, Viewer.ALTO - 10);
        String golesEnContra = String.valueOf(this.golesEnContra);
        g.drawString(golesEnContra, Viewer.ANCHO - fm.stringWidth(golesEnContra) - Barra.GROSOR - 10, Viewer.ALTO - 10);

        g.setColor(colorPorteria);
        g.fillRect(0, 0, Barra.GROSOR, Viewer.ALTO);

        this.barra.pintar(g);
        this.bola.pintar(g);
    }

    private synchronized void controlColisiones() throws InterruptedException {
        int xBola = this.bola.getX();
        int yBola = this.bola.getY();
        int yBarra = this.barra.getY();

        if (yBola <= 0 || yBola >= Viewer.ALTO - Bola.DIAMETRO) this.bola.setColisionHorizontal();

        Rectangle rBarra = new Rectangle(0, yBarra, Barra.GROSOR, Barra.ALTURA);
        Rectangle rBola = new Rectangle(xBola, yBola, Bola.DIAMETRO, Bola.DIAMETRO);
        if (rBarra.intersects(rBola)) {
            double m = this.bola.getTangenteAngulo();
            int yBolaPrima = (int) (yBola + (Bola.DIAMETRO / 2) * (1 - m));

            if (this.bola.getVy() > 0 && yBolaPrima == yBarra) this.bola.setColisionHorizontal();
            else if (this.bola.getVy() < 0 && yBolaPrima == yBarra + Barra.ALTURA) this.bola.setColisionHorizontal();

            this.bola.setColisionVertical();

            this.toques++;
        } else if (xBola < Barra.GROSOR) {
            golesEnContra++;
            this.barra.reiniciar();
            this.bola.reiniciar();
            Thread.sleep(1000);
            this.toques = 0;
            return;
        } else if (xBola >= Viewer.ANCHO - Bola.DIAMETRO) this.bola.devolver();

        this.bola.mover();
    }

    private synchronized void pausarReanudar() {
        this.pausa = !pausa;
        notifyAll();
        if (!this.pausa) iniciar();
    }
}
