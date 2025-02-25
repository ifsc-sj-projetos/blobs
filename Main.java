import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Teclado extends ConfigFrame implements Config, KeyListener {
	Jogador j;
	public Teclado(Jogador j){
		super();
		addKeyListener(this);
		this.j = j;
	}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {
		char c = e.getKeyChar();
		j.direcao = DIR_PARADO;
	}
	@Override
	public void keyPressed(KeyEvent e) {
		char c = e.getKeyChar();
		if(j.pause == false){
			j.direcao = c;
		}
		if (c == 'p'){
                	j.pause = !j.pause;
            	}
		if (c == 'q'){
                	System.exit(0);
            	}

		j.movimentoObjeto(j.listaJogador);
	}
}

public class Main extends Metodos {
	public static void main (String[] args) {
		Jogador j = new Jogador();

		ArrayList<Base> listaJ = new ArrayList<>();
		listaJ.add(j);

		ConfigFrame teclado = new Teclado(j);

		ArrayList<Base> listaObj = new ArrayList<>();


		Timer t = new Timer(50, new ActionListener() {
			int i = 0;
			int tempo = 0;
			int lentidao = LENTIDAO_INICIAL;
			int qtdInimigos = 0;
			int qtdAliados = 0;
			int qtdVida = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				tempo = i/(22);
				limparTela();

				qtdInimigos = qtdObjeto(listaObj, IDENT_INIMIGO);
				qtdAliados = qtdObjeto(listaObj, IDENT_ALIADO);
				qtdVida = qtdObjeto(listaObj, IDENT_VIDA);


				j.colocaObjeto(j.listaJogador);//necessário para que o movimento do jogador seja independente do tempo
				(new Base('0')).colocaObjeto(listaObj);
				desenhaBorda(LARGURA, ALTURA);
				barraInfo(LARGURA, ALTURA, j, tempo);


				if(j.pause)	System.out.println("PAUSADO");
				else{
					if((i%lentidao) == 0)movimentoObjeto(listaObj);
					if((i%(lentidao)) == 0 && qtdInimigos < MAX_INIMIGOS)gerarObjeto(listaObj, IDENT_INIMIGO);
					if((i%200) == 0 && qtdAliados < MAX_ALIADOS)gerarObjeto(listaObj, IDENT_ALIADO);
					if((i%250) == 0 && qtdVida < MAX_VIDA && j.getVida() < 5)gerarObjeto(listaObj, IDENT_VIDA);

					j.blobar(listaObj, IDENT_ALIADO);
					j.impacto(listaObj);

					if((i)%100 == 0 && lentidao >= 2) lentidao--; // a cada cinco segundos, se torna menos lento
					if(j.getVida() == 0){
						((Timer) e.getSource()).stop();
						(new Tela_GameOver()).encerrarJogo(tempo,j);
					}
					i++;
				}
           	}

       	});
		t.start();
	}
}
