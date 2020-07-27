package general;

import elementos.Barra;
import elementos.Bola;

import javax.sound.sampled.Line;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;

class Juego implements KeyListener, Runnable {

    private final Barra barra;
    private final Bola bola;
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
    public void keyPressed(KeyEvent keyEvent) {
        int tecla = keyEvent.getKeyCode();
        if (!pausa && (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S)) this.barra.moverAbajo();
        else if (!pausa && (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W)) this.barra.moverArriba();
        else if (tecla == KeyEvent.VK_P) pausarReanudar();
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

        this.barra.pintar(g);
        this.bola.pintar(g);
    }

    private void controlColisiones() throws InterruptedException {
        int xBola = this.bola.getX();
        int yBola = this.bola.getY();

        if (xBola <= 0) {
            golesEnContra++;
            this.barra.reiniciar();
            this.bola.reiniciar();
            Thread.sleep(1000);
            this.toques = 0;
            return;
        } else if (xBola >= Viewer.ANCHO - Bola.DIAMETRO) this.bola.devolver();

        if (yBola <= 0 || yBola >= Viewer.ALTO - Bola.DIAMETRO) this.bola.setColisionHorizontal();

        // Colisiones con la barra
        int tipoColision = tipoColision(xBola, yBola);
        switch (tipoColision) {
            case 1 -> this.bola.setColisionHorizontal();
            case 2 -> this.bola.setColisionVertical();
            case 3 -> {
                this.bola.setColisionHorizontal();
                this.bola.setColisionVertical();
            }
        }
        if (tipoColision != 0) this.toques++;

        this.bola.mover();
    }

    private int tipoColision(int xBola, int yBola) {
        int penetracionX = Barra.GROSOR - xBola;
        int penetracionY = penetracionY(yBola);

        if (penetracionX >= 0 && penetracionY != -1) {
            if (penetracionX > penetracionY) return 1; // Colisión horizontal
            else if (penetracionX < penetracionY) return 2; // Colisión vertical
            else return 3; // Colisión en la esquina
        }
        return 0; // Sin colisión
    }

    private synchronized void pausarReanudar() {
        this.pausa = !pausa;
        notifyAll();
        if (!this.pausa) iniciar();
    }

    private int penetracionY(int yBola) {
        int py = yBola + Bola.DIAMETRO - this.barra.getY();
        if (py < 0 || py > Barra.ALTURA + Bola.DIAMETRO) return -1; // No hay penetración y
        else {
            if (py <= Bola.DIAMETRO) return py;
            else if (py >= Barra.ALTURA) return Barra.ALTURA - py;
            else return Bola.DIAMETRO;
        }
    }
}
