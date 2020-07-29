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
    private final Rectangle rBarra, rBola;

    private boolean pausa, iniciado;
    private int golesEnContra, toques, toquesMax;

    Juego() {
        this.barra = new Barra();
        this.bola = new Bola();
        this.rBarra = new Rectangle();
        this.rBola = new Rectangle();

        new Thread(this).start();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {}

    @Override
    public synchronized void keyPressed(KeyEvent keyEvent) {
        int tecla = keyEvent.getKeyCode();
        if (iniciado) {
            if (tecla == KeyEvent.VK_P) pausarReanudar();
            if (!pausa) {
                if (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S) this.barra.moverAbajo();
                else if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W) this.barra.moverArriba();
            }
        } else {
            if (tecla == KeyEvent.VK_E) iniciar();
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {}

    @Override
    public void run() {
        try {
            while (!isPausa()) {
                controlColisiones();
                Thread.sleep(Viewer.TIEMPO_REFRESCO);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected synchronized boolean isPausa() throws InterruptedException {
        while (pausa || !iniciado) wait();
        return false;
    }

    protected void pintar(Graphics g) {
        g.setFont(fuente);
        FontMetrics fm = g.getFontMetrics();

        if (!iniciado) {
            String texto = "Pulsa E para empezar";
            g.drawString(texto, (Viewer.ANCHO - fm.stringWidth(texto)) / 2, (Viewer.ALTO + fm.getHeight()) / 2);
        }

        g.drawString("Récord: " + this.toquesMax, Barra.GROSOR + 10, fm.getHeight() + 10); // Récord toques
        g.drawString("Paradas: " + this.toques, Barra.GROSOR + 10, Viewer.ALTO - 10); // Toques
        // Goles en contra
        String golesEnContra = "Goles en contra: " + this.golesEnContra;
        g.drawString(golesEnContra, Viewer.ANCHO - fm.stringWidth(golesEnContra) - Barra.GROSOR - 10, Viewer.ALTO - 10);

        // Portería
        g.setColor(colorPorteria);
        g.fillRect(0, 0, Barra.GROSOR, Viewer.ALTO);

        // Barra y bola
        this.barra.pintar(g);
        this.bola.pintar(g);
    }

    private synchronized void controlColisiones() throws InterruptedException {
        int xBola = this.bola.getX();
        int yBola = this.bola.getY();
        int yBarra = this.barra.getY();

        // Bola pegando en las paredes de arriba o abajo
        if (yBola <= 0 || yBola >= Viewer.ALTO - Bola.DIAMETRO) this.bola.setColisionHorizontal();

        // Bola pegando en la barra
        this.rBarra.setBounds(0, yBarra, Barra.GROSOR, Barra.ALTURA);
        this.rBola.setBounds(xBola, yBola, Bola.DIAMETRO, Bola.DIAMETRO);
        if (rBarra.intersects(rBola)) {
            // Coordenada y corregida teniendo en cuenta la dirección de su movimiento (para que si pega en la
            // esquina de la barra rebote y vuelva por donde había venido)
            int yBolaPrima = (int) (yBola + (Bola.DIAMETRO / 2) * (1 - this.bola.getTangenteAngulo()));

            // Bola pega en una de las esquinas de la barra
            if (this.bola.getVy() > 0 && yBolaPrima == yBarra) this.bola.setColisionHorizontal();
            else if (this.bola.getVy() < 0 && yBolaPrima == yBarra + Barra.ALTURA) this.bola.setColisionHorizontal();

            // Esta colisión siempre se da porque pega en la cara larga
            this.bola.setColisionVertical();

            this.toques++;
        } else if (xBola < Barra.GROSOR) { // La bola ha entrado en la portería (gol)
            Thread.sleep(1000);
            this.golesEnContra++;
            this.barra.reiniciar();
            this.bola.reiniciar();

            if (this.toques > this.toquesMax) this.toquesMax = this.toques;
            this.toques = 0;
        } else if (xBola >= Viewer.ANCHO - Bola.DIAMETRO) this.bola.devolver(); // La bola pega en la pared de salida

        this.bola.mover();
    }

    private synchronized void iniciar() {
        this.iniciado = true;
        notifyAll();
    }

    private synchronized void pausarReanudar() {
        this.pausa = !pausa;
        notifyAll();
    }
}
