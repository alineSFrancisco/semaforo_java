import java.awt.*;
import javax.swing.*;

public class App extends JFrame {
    private enum Estado {
        VERMELHO("Vermelho", new Color(239, 68, 68), 5),
        VERDE("Verde", new Color(34, 197, 94), 5),
        AMARELO("Amarelo", new Color(250, 204, 21), 2);

        private final String nome;
        private final Color cor;
        private final int duracao;

        Estado(String nome, Color cor, int duracao) {
            this.nome = nome;
            this.cor = cor;
            this.duracao = duracao;
        }
    }

    private Estado estadoAtual = Estado.VERMELHO;
    private int segundosRestantes = estadoAtual.duracao;
    private boolean emExecucao;

    private final PainelSemaforo painelEsquerdo = new PainelSemaforo(false);
    private final PainelSemaforo painelDireito = new PainelSemaforo(true);
    private final JLabel estadoLabel = new JLabel("Esquerdo: Vermelho", SwingConstants.CENTER);
    private final JLabel estadoDireitoLabel = new JLabel("Direito: Verde", SwingConstants.CENTER);
    private final JLabel tempoLabel = new JLabel("Pronto para iniciar", SwingConstants.CENTER);
    private final JButton iniciarPausar = new JButton("Iniciar");
    private final Timer timer = new Timer(1000, e -> avancarTempo());

    public App() {
        super("Simulador de Semáforo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(760, 700);
        setMinimumSize(new Dimension(600, 580));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(15, 23, 42));
        setLayout(new BorderLayout(0, 12));

        JLabel titulo = new JLabel("SIMULADOR DE SEMÁFORO", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(24, 12, 4, 12));

        JPanel informacoes = new JPanel(new GridLayout(3, 1, 0, 6));
        informacoes.setOpaque(false);
        estadoLabel.setForeground(estadoAtual.cor);
        estadoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        estadoDireitoLabel.setForeground(Estado.VERDE.cor);
        estadoDireitoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        tempoLabel.setForeground(new Color(203, 213, 225));
        tempoLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        informacoes.add(estadoLabel);
        informacoes.add(estadoDireitoLabel);
        informacoes.add(tempoLabel);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botoes.setOpaque(false);
        estilizarBotao(iniciarPausar, new Color(37, 99, 235));
        JButton reiniciar = new JButton("Reiniciar");
        estilizarBotao(reiniciar, new Color(51, 65, 85));
        iniciarPausar.addActionListener(e -> alternarExecucao());
        reiniciar.addActionListener(e -> reiniciar());
        botoes.add(iniciarPausar);
        botoes.add(reiniciar);

        JPanel rodape = new JPanel(new BorderLayout(0, 14));
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 16, 24, 16));
        rodape.add(informacoes, BorderLayout.NORTH);
        rodape.add(botoes, BorderLayout.SOUTH);

        add(titulo, BorderLayout.NORTH);
        JPanel semaforos = new JPanel(new GridLayout(1, 2, 14, 0));
        semaforos.setOpaque(false);
        semaforos.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        semaforos.add(criarColunaSemaforo("ESQUERDO", painelEsquerdo));
        semaforos.add(criarColunaSemaforo("DIREITO", painelDireito));

        add(semaforos, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private JPanel criarColunaSemaforo(String titulo, PainelSemaforo painel) {
        JPanel coluna = new JPanel(new BorderLayout(0, 8));
        coluna.setOpaque(false);
        JLabel etiqueta = new JLabel(titulo, SwingConstants.CENTER);
        etiqueta.setForeground(new Color(148, 163, 184));
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 13));
        coluna.add(etiqueta, BorderLayout.NORTH);
        coluna.add(painel, BorderLayout.CENTER);
        return coluna;
    }

    private void alternarExecucao() {
        emExecucao = !emExecucao;
        if (emExecucao) {
            iniciarPausar.setText("Pausar");
            tempoLabel.setText("Próxima mudança em " + segundosRestantes + " s");
            timer.start();
        } else {
            iniciarPausar.setText("Continuar");
            timer.stop();
            tempoLabel.setText("Pausado · " + segundosRestantes + " s restantes");
        }
    }

    private void avancarTempo() {
        segundosRestantes--;
        if (segundosRestantes <= 0) {
            switch (estadoAtual) {
                case VERMELHO:
                    estadoAtual = Estado.VERDE;
                    break;
                case VERDE:
                    estadoAtual = Estado.AMARELO;
                    break;
                case AMARELO:
                    estadoAtual = Estado.VERMELHO;
                    break;
            }
            segundosRestantes = estadoAtual.duracao;
        }
        atualizarEstado();
    }

    private void reiniciar() {
        timer.stop();
        emExecucao = false;
        estadoAtual = Estado.VERMELHO;
        segundosRestantes = estadoAtual.duracao;
        iniciarPausar.setText("Iniciar");
        atualizarEstado();
        tempoLabel.setText("Pronto para iniciar");
    }

    private void atualizarEstado() {
        Estado estadoDireito = obterEstadoDireito();
        estadoLabel.setText("Esquerdo: " + estadoAtual.nome);
        estadoLabel.setForeground(estadoAtual.cor);
        estadoDireitoLabel.setText("Direito: " + estadoDireito.nome);
        estadoDireitoLabel.setForeground(estadoDireito.cor);
        painelEsquerdo.repaint();
        painelDireito.repaint();
        if (emExecucao) {
            tempoLabel.setText("Próxima mudança em " + segundosRestantes + " s");
        }
    }

    private Estado obterEstadoDireito() {
        if (estadoAtual == Estado.VERMELHO) {
            return Estado.VERDE;
        }
        if (estadoAtual == Estado.VERDE
                && segundosRestantes > Estado.VERDE.duracao - Estado.AMARELO.duracao) {
            return Estado.AMARELO;
        }
        return Estado.VERMELHO;
    }

    private static void estilizarBotao(JButton botao, Color cor) {
        botao.setFont(new Font("SansSerif", Font.BOLD, 14));
        botao.setForeground(Color.WHITE);
        botao.setBackground(cor);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private final class PainelSemaforo extends JPanel {
        private final boolean ladoDireito;

        PainelSemaforo(boolean ladoDireito) {
            this.ladoDireito = ladoDireito;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Estado estadoAceso = ladoDireito ? obterEstadoDireito() : estadoAtual;
            int altura = Math.min(390, getHeight() - 12);
            int larguraTotal = Math.min(250, getWidth() - 8);
            int larguraCarros = Math.min(145, (larguraTotal - 12) * 3 / 5);
            int larguraPedestres = larguraTotal - larguraCarros - 12;
            int xCarros = (getWidth() - larguraTotal) / 2;
            int xPedestres = xCarros + larguraCarros + 12;
            int y = (getHeight() - altura) / 2;
            int yPainel = y + 22;
            int alturaPainel = altura - 22;

            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            g.setColor(new Color(148, 163, 184));
            g.drawString("VEÍCULOS", xCarros + (larguraCarros - g.getFontMetrics().stringWidth("VEÍCULOS")) / 2, y + 14);
            g.drawString("PEDESTRES", xPedestres + (larguraPedestres - g.getFontMetrics().stringWidth("PEDESTRES")) / 2, y + 14);

            g.setColor(new Color(30, 41, 59));
            g.fillRoundRect(xCarros, yPainel, larguraCarros, alturaPainel, 36, 36);
            g.setColor(new Color(71, 85, 105));
            g.setStroke(new BasicStroke(3));
            g.drawRoundRect(xCarros, yPainel, larguraCarros, alturaPainel, 36, 36);

            int diametro = Math.max(32, Math.min(alturaPainel / 4, larguraCarros - 32));
            int espacamento = (alturaPainel - 3 * diametro) / 4;
            Estado[] estados = {Estado.VERMELHO, Estado.AMARELO, Estado.VERDE};
            for (int i = 0; i < estados.length; i++) {
                int luzX = xCarros + (larguraCarros - diametro) / 2;
                int luzY = yPainel + espacamento + i * (diametro + espacamento);
                boolean acesa = estados[i] == estadoAceso;
                g.setColor(acesa ? estados[i].cor : new Color(51, 65, 85));
                g.fillOval(luzX, luzY, diametro, diametro);
                if (acesa) {
                    Color cor = estados[i].cor;
                    g.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 70));
                    g.setStroke(new BasicStroke(9));
                    g.drawOval(luzX - 5, luzY - 5, diametro + 10, diametro + 10);
                }
            }

            int alturaPedestres = Math.min(250, alturaPainel - 20);
            int yPedestres = yPainel + (alturaPainel - alturaPedestres) / 2;
            g.setColor(new Color(30, 41, 59));
            g.fillRoundRect(xPedestres, yPedestres, larguraPedestres, alturaPedestres, 28, 28);
            g.setColor(new Color(71, 85, 105));
            g.setStroke(new BasicStroke(3));
            g.drawRoundRect(xPedestres, yPedestres, larguraPedestres, alturaPedestres, 28, 28);

            boolean podeAtravessar = estadoAceso == Estado.VERMELHO;
            int diametroPedestre = Math.min(larguraPedestres - 16, alturaPedestres / 3);
            int centroX = xPedestres + larguraPedestres / 2;
            int centroYVermelho = yPedestres + alturaPedestres / 3;
            int centroYVerde = yPedestres + 2 * alturaPedestres / 3;
            desenharSinalPedestre(g, centroX, centroYVermelho, diametroPedestre,
                    !podeAtravessar, Estado.VERMELHO.cor, false);
            desenharSinalPedestre(g, centroX, centroYVerde, diametroPedestre,
                    podeAtravessar, Estado.VERDE.cor, true);
            g.dispose();
        }

        private void desenharSinalPedestre(Graphics2D g, int centroX, int centroY, int diametro,
                                           boolean aceso, Color cor, boolean andando) {
            int x = centroX - diametro / 2;
            int y = centroY - diametro / 2;
            g.setColor(aceso ? cor : new Color(51, 65, 85));
            g.fillOval(x, y, diametro, diametro);
            if (aceso) {
                g.setColor(Color.WHITE);
                int escala = Math.max(18, diametro / 2);
                int cabeca = Math.max(5, escala / 5);
                int topo = centroY - escala / 2;
                g.setStroke(new BasicStroke(Math.max(2, escala / 12), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawOval(centroX - cabeca / 2, topo, cabeca, cabeca);
                int troncoY = topo + cabeca;
                int meioY = centroY + escala / 6;
                g.drawLine(centroX, troncoY, andando ? centroX + escala / 10 : centroX, meioY);
                if (andando) {
                    g.drawLine(centroX, troncoY + escala / 4, centroX - escala / 4, meioY - escala / 8);
                    g.drawLine(centroX, troncoY + escala / 4, centroX + escala / 3, meioY - escala / 3);
                    g.drawLine(centroX + escala / 10, meioY, centroX - escala / 4, centroY + escala / 2);
                    g.drawLine(centroX + escala / 10, meioY, centroX + escala / 3, centroY + escala / 3);
                } else {
                    g.drawLine(centroX, troncoY + escala / 4, centroX - escala / 4, meioY);
                    g.drawLine(centroX, troncoY + escala / 4, centroX + escala / 4, meioY);
                    g.drawLine(centroX, meioY, centroX - escala / 5, centroY + escala / 2);
                    g.drawLine(centroX, meioY, centroX + escala / 5, centroY + escala / 2);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}
