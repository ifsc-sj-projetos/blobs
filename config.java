import java.util.ArrayList;

interface Config{
    static final int  ALTURA           = 30,
                      LARGURA          = 60,
                      MAX_INIMIGOS     = 10,
					  MAX_ALIADOS	   = 2,
					  MAX_VIDA		   = 3,
                      LENTIDAO_INICIAL = 12;

    static final char  DIR_PARADO   = 0,
					   DIR_CIMA     = 'w',
					   DIR_BAIXO    = 's',
					   DIR_DIREITA  = 'd',
					   DIR_ESQUERDA = 'a';

    static final char IDENT_JOGADOR = '0',
                      IDENT_INIMIGO = 'x',
                      IDENT_ALIADO  = 'O',
                      IDENT_VIDA	= '@',
                      IDENT_ENTORNO = 0; //Invisível

    static Posicao inicial = new Posicao(5,5);

    String RESET  = "\u001B[0m";
    String RED	  = "\u001B[31m";
    String GREEN  = "\u001B[32m";
    String YELLOW = "\u001B[33m";
    String BLUE   = "\u001B[34m";
}

abstract class Metodos implements Config{
    static void limparTela(){System.out.printf("\033[H\033[2J");};
    static void gotoxy(int x, int y){System.out.printf("%c[%d;%df",0x1B,y,x);}

    void colocaObjeto(ArrayList<Base> listaObj){
        for (Base obj : listaObj) {
			gotoxy(obj.pos.x,obj.pos.y);
			if(obj.identificador == IDENT_VIDA)System.out.printf("%s%c%s\n", BLUE, obj.identificador, RESET);
			else System.out.printf("%c\n", obj.identificador);
			if(obj.possuiEntorno)colocaObjeto(((Entorno)obj).entorno);
        }
    }

    static void desenhaBorda(int LARGURA, int ALTURA){
		System.out.printf(YELLOW);
	    for(int i = 2; i < ALTURA; i++){ //vertical
	    	gotoxy(1,i);
	    	System.out.printf("||");
	    	gotoxy(LARGURA,i);
	    	System.out.printf("||");
	    }
	    for(int i = 1; i < LARGURA+2; i++){ //horizontal
	    	gotoxy(i,1);
	    	System.out.printf("=");
	    	gotoxy(i,ALTURA);
	    	System.out.printf("=");
	    }
	    System.out.printf(RESET);
    }

    static void barraInfo(int LARGURA, int ALTURA, Jogador j, int tempo){
        gotoxy(1,ALTURA+1);
        //System.out.println("Posicao = { " + j.pos.x + " , " + j.pos.y + " }");
        System.out.printf("Vidas: ");
        for(int i = 0; i < j.getVida(); i++){
			if(j.getVida() < 3)System.out.printf("%s▄%s ",RED,RESET);
			else if(j.getVida() < 5)System.out.printf("%s▄%s ",YELLOW,RESET);
			else System.out.printf("%s▄%s ",GREEN,RESET);
		}
        System.out.println("\nTempo: " + tempo + " horas");
        System.out.println("Distancia percorrida: "+j.getDistanciaPercorrida()+" cm");
        System.out.println("Pontos: "+j.getPontos());
    }

    static void movimentoObjeto(ArrayList<Base> listaObj){
        for (Base obj : listaObj) {
			if(obj.identificador == IDENT_JOGADOR){
				if(((Jogador)obj).tocouParede(obj.direcao) || ((Jogador)obj).blobsTocaramParede(obj.direcao))obj.direcao = DIR_PARADO;
				else if(obj.direcao != DIR_PARADO){
				int dist = ((Jogador)obj).getDistanciaPercorrida();
				((Jogador)obj).setDistanciaPercorrida(++dist);
				}
			}

			switch (obj.direcao){
			case DIR_CIMA:
				obj.pos.y--;
				break;
			case DIR_BAIXO:
				obj.pos.y++;
				break;
			case DIR_DIREITA:
				obj.pos.x++;
				break;
			case DIR_ESQUERDA:
				obj.pos.x--;
				break;
			default:
				obj.direcao = 0;
			}

			if(obj.identificador != IDENT_ENTORNO && (obj.pos.x == 2 || obj.pos.x == LARGURA ||obj.pos.y == 1 ||  obj.pos.y == ALTURA)){
			    listaObj.remove(obj);
			break;
			}
			if(obj.possuiEntorno){
				Entorno obj_entorno = (Entorno)obj;
				obj_entorno.destinarOutros(obj_entorno.entorno);
				movimentoObjeto(obj_entorno.entorno);
			}
		}
    }

    static void gerarObjeto(ArrayList<Base> listaObj, char identificador){
		Base novoObj;
		if(identificador == IDENT_ALIADO) novoObj = new Entorno(identificador,null, null);
		else novoObj = new Base(identificador);

		double x = Math.random(), y = Math.random();
		int int_x, int_y;
		int_x = (int) (x*100)%(LARGURA-3) + 3;
		int_y = (int) (y*100)%(ALTURA-2) + 2;
		if(((int)(x*100))%(4) == 0) {
			int_x = 3;
			novoObj.direcao = DIR_DIREITA;
		}else if(((int)(x*100))%(4) == 1) {
			int_x = LARGURA-3;
			novoObj.direcao = DIR_ESQUERDA;
		}else if(((int)(x*100))%(4) == 2){
			int_y = 2;
			novoObj.direcao = DIR_BAIXO;
		}else {
			int_y = ALTURA-1;
			novoObj.direcao = DIR_CIMA;
		}
		novoObj.pos = new Posicao(int_x,int_y);
		listaObj.add(novoObj);
	}

   static int qtdObjeto(ArrayList<Base> listaObj, char identificador){
    	int qtd = 0;
    	for (Base obj: listaObj){
    		if(obj.identificador == identificador)qtd++;
    	}
    	return qtd;
   }
}

class Posicao{
	int x, y;
	public Posicao(int a, int b){
        this.x = a;
        this.y = b;
    }
}

class Base extends Metodos implements Config{
	char identificador;
	Posicao pos;
	int direcao;
	boolean possuiEntorno = false;

	public Base(char identificador){
		this.identificador = identificador;
	}

	Base tocouAlgo(ArrayList<Base> listaObj, char identAlgo){
		for(Base obj: listaObj) if(obj.identificador == identAlgo){
			if((this.pos.x == obj.pos.x) && (this.pos.y == obj.pos.y)){
				return obj;
			}
		}
		return null;
	}

    boolean impacto(ArrayList<Base> listaObj){
        Base inimigo = this.tocouAlgo(listaObj, IDENT_INIMIGO);
        if(inimigo == null) return false;
        gotoxy(this.pos.x, this.pos.y);
		System.out.println("BOOM!");
        listaObj.remove(inimigo);
        listaObj.remove(this);
        return true;
	}

}

class Entorno extends Base implements Config {
    private Entorno mestre;
    Jogador ultraMestre;
    ArrayList<Base> entorno = null; //Não poderia ser um array do tipo 'Entorno' pois ficaria incompatível com colocaObjeto()

    public Entorno(char identificador, Entorno mestre, Jogador ultraMestre){
		super(identificador);
		setMestre(mestre);
		this.ultraMestre = ultraMestre;
    }

    Entorno getMestre(){
		return this.mestre;
    }
    void setMestre(Entorno meuMestre){
		this.mestre = meuMestre;
    }
    boolean tocouParede(int direcao){
		switch(direcao){
			case DIR_DIREITA:
				if(this.pos.x == LARGURA-1) return true;
				else return false;
			case DIR_ESQUERDA:
				if(this.pos.x == 3) return true;
				else return false;
			case DIR_CIMA:
				if(this.pos.y == 2) return true;
				else return false;
			case DIR_BAIXO:
				if(this.pos.y == ALTURA-1) return true;
				else return false;
			default: return false;
		}
	}
	boolean blobsTocaramParede(int direcao){
		if(this.possuiEntorno){
			for(Base obj: this.entorno){
				if(obj.possuiEntorno){
					if(((Entorno)obj).tocouParede(direcao)) return true;
					else if(((Entorno)obj).blobsTocaramParede(direcao)) return true;
				}
			}
		}
		return false;
	}
	void gerarEntorno(){
		this.possuiEntorno = true;
		this.entorno = new ArrayList<>();
		int mpos_x = this.pos.x, mpos_y = this.pos.y;
		for(int i = -1; i <= 1; i++){
			for(int j = -1; j <= 1; j++){
				if(!(i == 0 && j ==0)){
					Entorno e = new Entorno(IDENT_ENTORNO, this, this.ultraMestre);
					e.pos = new Posicao(mpos_x + j, mpos_y + i);
					this.entorno.add(e);
				}
			}
		}
	}

    void destruirEntorno(){
        for(Base e: this.entorno)e = null;
        this.entorno.clear();
    }

	Base verificarEntorno(ArrayList<Base> listaObj, char identificador){
		for (Base entornoObj: this.entorno){
			Base objetoNoEntorno = entornoObj.tocouAlgo(listaObj, identificador);
			if(objetoNoEntorno != null){
				System.out.println("TOCOU EM ALGO!");
				return objetoNoEntorno;
			}
		}
		return null;
	}

	void blobar(ArrayList<Base> listaObj, char identificador){
		if(this.possuiEntorno) for (Base entornoObj: this.entorno){
			Base blob = entornoObj.tocouAlgo(listaObj, identificador);
			if(blob != null){
				listaObj.remove(blob);
				((Entorno)blob).setMestre(this);
				((Entorno)blob).ultraMestre = this.ultraMestre;
				((Entorno)blob).gerarEntorno();
				this.entorno.add(blob);
				int pontos = ultraMestre.getPontos();
				int qtdBlobs = ultraMestre.getQtdBlobs();
				ultraMestre.setPontos(++pontos);
				ultraMestre.setQtdBlobs(++qtdBlobs);
				gotoxy(blob.pos.x+2, blob.pos.y);
				System.out.println(BLUE+"+1"+RESET);
				break;
			} else ((Entorno)entornoObj).blobar(listaObj, identificador);
		}
	}

	void destinarOutros(ArrayList<Base> listaObj){
		for(Base outro: listaObj) outro.direcao = this.direcao;
	}

	@Override
    boolean impacto(ArrayList<Base> listaObj){
        Base inimigo = this.tocouAlgo(listaObj, IDENT_INIMIGO);
        Base obj_vida = this.tocouAlgo(listaObj, IDENT_VIDA);
        if(this.possuiEntorno){
			Entorno mestre = this.getMestre();
			while(mestre.getMestre() != null){
				mestre = mestre.getMestre();
			}
			if(inimigo == null && obj_vida == null){
				for(Base e: this.entorno){
							if(((Entorno)e).impacto(listaObj))return true;
				}
				return false;
			}else if(inimigo != null){
				listaObj.remove(inimigo);
				this.destruirEntorno();
				gotoxy(this.pos.x, this.pos.y);
				this.getMestre().entorno.remove(this);
				System.out.println("BOOM!");
				int qtdBlobs = ultraMestre.getQtdBlobs();
				ultraMestre.setQtdBlobs(--qtdBlobs);
				return true;
			}else{
				listaObj.remove(obj_vida);
				int vida = ultraMestre.getVida();
				ultraMestre.setVida(++vida);
				gotoxy(mestre.pos.x, mestre.pos.y);
				System.out.println("UP");
				return true;
			}
		}
	return false;
    }

}

class Jogador extends Entorno implements Config{
	private int distancia_percorrida, pontos, vida, qtd_blobs;
	boolean pause;
	ArrayList<Base> listaJogador = new ArrayList<>();//necessário para que o movimento do jogador seja independente do tempo

	int getDistanciaPercorrida() {
		return distancia_percorrida;
	}

	void setDistanciaPercorrida(int distancia_percorrida) {
		this.distancia_percorrida = distancia_percorrida;
	}

	int getPontos() {
		return pontos;
	}

	void setPontos(int pontos) {
		this.pontos = pontos;
	}

	int getVida() {
		return vida;
	}

	void setVida(int vida) {
		this.vida = vida;
	}

	int getQtdBlobs(){
		return this.qtd_blobs;
	}

	void setQtdBlobs(int qtd){
		this.qtd_blobs = qtd;
	}

    public Jogador(){
		super(IDENT_JOGADOR, null, null); //meuMestre = null
		this.ultraMestre = this;
		this.possuiEntorno = true;
		this.pos = inicial;
		this.pause = false;
		this.setDistanciaPercorrida(0);
		this.setPontos(0);
		this.setVida(1);
		this.setQtdBlobs(0);
		this.gerarEntorno();
		this.listaJogador.add(this);
	}
	@Override
    boolean impacto(ArrayList<Base> listaObj){
        Base inimigo = this.tocouAlgo(listaObj, IDENT_INIMIGO);
        Base obj_vida = this.tocouAlgo(listaObj, IDENT_VIDA);
        if(inimigo == null && obj_vida == null){
			for(Base e: this.entorno){
					if(((Entorno)e).impacto(listaObj))return true;
			}
			return false;
		}
		int vida = this.getVida();
		if(inimigo != null){
			this.setVida(--vida);
			listaObj.remove(inimigo);
			gotoxy(this.pos.x, this.pos.y);
			System.out.println("BOOM!");
		}else{
			this.setVida(++vida);
			listaObj.remove(obj_vida);
			gotoxy(this.pos.x+2, this.pos.y);
			System.out.println("UP");
		}
		return true;
	}

	@Override
	void colocaObjeto(ArrayList<Base> listaObj){
        for (Base obj : listaObj) {
			if(obj.identificador == IDENT_JOGADOR)System.out.println(RED);
			else System.out.println(YELLOW);
			gotoxy(obj.pos.x,obj.pos.y);
			System.out.printf("%c\n", obj.identificador);
			if(obj.possuiEntorno)colocaObjeto(((Entorno)obj).entorno);
        }
        System.out.println(RESET);
    }
}

class Tela_GameOver extends Metodos{
	void encerrarJogo(int tempo, Jogador j){
		limparTela();
		this.desenhaBorda(51, ALTURA-1);
		gotoxy(3, 1);
		System.out.println(RED+"""

--  ▗▄▄▖ ▗▄▖ ▗▖  ▗▖▗▄▄▄▖     ▗▄▖ ▗▖  ▗▖▗▄▄▄▖▗▄▄▖  --
-- ▐▌   ▐▌ ▐▌▐▛▚▞▜▌▐▌       ▐▌ ▐▌▐▌  ▐▌▐▌   ▐▌ ▐▌ --
-- ▐▌▝▜▌▐▛▀▜▌▐▌  ▐▌▐▛▀▀▘    ▐▌ ▐▌▐▌  ▐▌▐▛▀▀▘▐▛▀▚▖ --
-- ▝▚▄▞▘▐▌ ▐▌▐▌  ▐▌▐▙▄▄▖    ▝▚▄▞▘ ▝▚▞▘ ▐▙▄▄▖▐▌ ▐▌ --
-- *********************    ********************* --


		""");
		gotoxy(5, 9);
		System.out.println(YELLOW+"Sua vida durou "+BLUE+ tempo +YELLOW+" horas");
		gotoxy(5, 11);
		System.out.println("Você percorreu a longa distancia de "+RESET+ j.getDistanciaPercorrida() +YELLOW+" cm");
		gotoxy(5, 13);
		System.out.println("Entre idas e vindas, você conheceu "+RED+ j.getPontos() +YELLOW+" blobs");
		gotoxy(5, 14);
		System.out.println("Mas, na sua morte, você tinha "+GREEN+ j.getQtdBlobs() +YELLOW+" amigos ao seu lado");

		gotoxy(5, 16);
		System.out.println(RESET+"PONTUAÇÃO FINAL: "+BLUE+ tempo*(tempo+j.getDistanciaPercorrida()+j.getQtdBlobs()+j.getPontos()) +RESET+" pts");

		gotoxy(1,ALTURA);

		System.exit(0);
	}
}
