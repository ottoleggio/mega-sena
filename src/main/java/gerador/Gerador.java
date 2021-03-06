package gerador;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * SOLU��O:
 * 
 * (1) Este gerador gera todos os resultados poss�veis da megasena e os armazena
 *     em arquivo para consulta posterior. 
 *     
 * (2) Gerar programas para excluir dos resultados poss�veis (gerados no passo
 *     anterior) aqueles que atendem a algum crit�rio de exclus�o. A execu��o de
 *     cada programa dever� dar origem a um arquivo menor, resultante daquele de 
 *     entrada menos os resultados que passaram nos crit�rios para exclus�o.
 *     
 * (3) Lista de crit�rios de sele��o:
 *     - Muitas dezenas na mesma coluna ou linha
 *     - H� pelo menos tr�s dezenas formando s�rie aritm�tica
 *     - H� muitos pares ou �mpares
 * 
 */
public class Gerador {
	int jogo = 0; // indica jogo gerado (numerados a partir de 1)

	UsaSequencia us = null;

	int szSeq = 0;

	int szDom = 0;

	int[] v = null;

	// Protege array v atrav�s de uma c�pia
	// A c�pia � vis�vel externamente
	int[] copiaV = null;

	/**
	 * Cria gerador padrão. Embora todas as seqüências sejam geradas, nenhuma
	 * operação é estabelecida para cada seqüência.
	 * 
	 * @param szSeq
	 * @param szDom
	 */
	public Gerador(int szSeq, int szDom) {
		this(new UsaSequencia() {
			public void usaSequencia(int jogo, int[] dzs) {
			};
		}, szSeq, szDom);
	}

	public Gerador(UsaSequencia us, int szSeq, int szDom) {
		super();
		this.us = us;
		this.szSeq = szSeq;
		this.szDom = szDom;
		v = new int[szSeq + 1];
		v[0] = 0;

		copiaV = new int[szSeq];
	}

	private int[] copiaSequencia() {
		System.arraycopy(v, 1, copiaV, 0, copiaV.length);
		return copiaV;
	}

	public void gerador() {
		gera(1);
	}

	private void gera(int k) {
		for (v[k] = v[k - 1] + 1; v[k] <= szDom; v[k]++) {
			if (k == szSeq) {
				us.usaSequencia(++jogo, copiaSequencia());
			} else {
				gera(k + 1);
			}
		}
		v[k]--;
	}

	// Vers�o alternativa para gera que n�o foi testada
	// Talvez seja mais leg�vel que a anterior, embora
	// fa�a uso de mais mem�ria.
	private void repita(int k, int i, int f) {
		if (k == 16) {
			us.usaSequencia(++jogo, copiaSequencia());
		} else {
			for (int j = i; j <= f; j++) {
				v[k] = j;
				repita(k + 1, j + 1, f);
			}
		}
	}
	
	/**
	 * Gera identificador �nico para a dezena. Este identificador � 
	 * suficiente para gerar a seq��ncia de dezenas de tal forma que
	 * ou o identificador ou a seq��ncia � suficiente para identificar
	 * seis dezenas. 
	 * 
	 * @param dzns
	 * @return
	 */
	public static int dezenas56ParaNumero(int[] dzns) {
		// C�lculo abaixo gera para jogos no formato (1,2,3,4,d5,d6)
		// o n�mero correspondente do jogo para d5 e d6 v�lidos. 
		// Precisa expandir para considerar d4 e assim sucessivamente. 
		// Valor limite � 1540.
		int d5 = dzns[4];
		int d6 = dzns[5];
		
		int n = d5 - 5;
		int p = n > 0 ? ((109 - n) * n) / 2 + n : 0;
		return p + d6 - d5;
	}

	public static void main(String[] args) {

		// Exibe todos os jogos poss�veis da megasena
		UsaSequencia us = new UsaSequencia() {
			private PrintWriter pw;
			
			{
				try {
					pw = new PrintWriter("/tmp/jogos.txt");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

			public void usaSequencia(int jogo, int[] dzs) {
				String dzns = String.format("%02d %02d %02d %02d %02d %02d", dzs[0], dzs[1], dzs[2],dzs[3], dzs[4], dzs[5]);
				pw.printf("%08d %s\n", jogo, dzns);
			}
		};

		// Contador contador = new Contador();
		new Gerador(us, 6, 60).gerador();
	}
}