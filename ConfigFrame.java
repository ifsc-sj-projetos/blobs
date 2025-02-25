import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


class ConfigFrame extends JFrame{
	public ConfigFrame(){ // método construtor, necessário para inicializar a classe dentro do main
		setSize(400, 200);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true); // Janela sempre em foco
		setFocusableWindowState(true); 
		setLayout(new FlowLayout());
		setUndecorated(true); // Remove bordas
		setBackground(new Color(0, 0, 0, 0)); // Deixa o fundo transparente
		
		
		addWindowFocusListener(new WindowAdapter() {
		 	@Override
		 	public void windowLostFocus(WindowEvent e) {
		 	    requestFocus();
		 	}
		 });
		
		
		setVisible(true);
		requestFocus();
	}
}

