package com.equiperocket.projects.cinemaGUI;

import java.awt.*;
import javax.swing.*;

public class UIUtils {
    public static final Color COR_FUNDO = new Color(40, 44, 52);
    public static final Color COR_SECUNDARIA = new Color(97, 175, 239);
    public static final Color COR_TEXTO = new Color(220, 223, 228);
    public static final Color COR_CLIENTE_IDOSO = new Color(184, 134, 11);
    public static final Color COR_CLIENTE_ESTUDANTE = new Color(51, 122, 183);
    public static final Color COR_CLIENTE_NORMAL = new Color(70, 136, 71);
    public static final Color COR_BOTAO_PARAR = new Color(255, 99, 71);
    public static final Color COR_BOTAO_RETOMAR = new Color(153, 217, 234);

    public static final Font FONTE_PADRAO = new Font("Arial", Font.PLAIN, 14);
    public static final Font FONTE_NEGRITO = new Font("Arial", Font.BOLD, 14);
    public static final Font FONTE_TITULO = new Font("Arial", Font.BOLD, 16);

    public static JButton criarBotaoEstilizado(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(COR_SECUNDARIA);
        botao.setForeground(Color.BLACK);
        botao.setFocusPainted(false);
        botao.setFont(FONTE_NEGRITO);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { botao.setBackground(COR_SECUNDARIA.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e) { botao.setBackground(COR_SECUNDARIA); }
        });
        return botao;
    }
}
