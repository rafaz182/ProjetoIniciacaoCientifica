package br.edu.ifsp.IniciacaoCientifica;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class ImageFrame extends JFrame{
	
	private static int counter = 0;
	
	private GImage image;		
	
	//private JPanel panelImg;

	public ImageFrame(File img) throws IOException{
		setLayout(new BorderLayout());	
		setTitle(img.getName());
				
		image = new GImage(ImageIO.read(img));
		
				
		processaImg();
		
		ImageIcon icon = new ImageIcon(image.getImagem());
		
		JLabel imgLabel = new JLabel(icon);
		
		add(new JScrollPane(imgLabel), BorderLayout.CENTER);
				
		setSize(image.getImagem().getWidth()+30, image.getImagem().getHeight()+30);		
		setVisible(true);
		setResizable(false);		
	}
	
	public void processaImg(){
		long tempoInicial1 = System.currentTimeMillis();
		
		System.out.println("_________INICIO DO PROCESSAMENTO["+counter+"]___________");
		System.out.println("arquivo: "+ this.getTitle()+"\n");
		
		double r = 1024./Math.min(image.height, image.width);	 // define razão
		
		if(r != 1.)
			image.reduz(r);
		
		System.out.println("Largura:"+image.width+", Altura:"+image.height+"\n");	
		
		if(image.width > image.height)
			alinhaPaisagem(image);
		else
			alinhaRetrato(image);
		
		
		long tempoFinal1 = System.currentTimeMillis(); 
		System.out.println("tempo total = " + ((tempoFinal1 - tempoInicial1)/100) + " segundos");
		System.out.println("_________FIM DO PROCESSAMENTO["+counter+"]___________\n\n");
		counter++;
	}
	
	public void alinhaPaisagem(GImage img){
		// Valores que definem a janela na linha ymaior
		final double PORCENTAGEM_COMPRIMENTO_H = 0.3;
		final double PORCENTAGEM_ALTURA_H = 0.008;
		
		// Valores que definem a janela na coluna xmaior
		final double PORCENTAGEM_ALTURA_V = 0.3;
		final double PORCENTAGEM_COMPRIMENTO_V = 0.0044;
		
		//Janela que delimita a pesquisa do ymaior e xmaior
		double xmin, ymin, xmax, ymax;
		
		xmin = img.width*(4./19.);
		ymin = img.height*(2./17.);
		xmax = img.width*(15./19.);
		ymax = img.height*(15./17.);
		
		//______________________________________
		
		GImage imgConvTop = img.copia(); 		
		imgConvTop.conv(leKernel(new File("kernels\\bordaTop.txt")), "bordaTop.txt");					
		int[] yMaiorTop; // recebe o maior valor e a linha de maior valor entre (0, 0) até (imgConvTop.width, ymin) // 0 = valor; 1 = posição	
		yMaiorTop = getMaxY(imgConvTop, 0, 0, imgConvTop.width, ymin);	
		
		GImage imgConvBottom = img.copia();		
		imgConvBottom.conv(leKernel(new File("kernels\\bordaBottom.txt")), "bordaBottom.txt");		
		int[] yMaiorBottom; // recebe o maior valor e a linha de maior valor entre (0, ymax) até (imgConvBottom.width, imgConvBottom.height) // 0 = valor; 1 = posição		
		yMaiorBottom = getMaxY(imgConvBottom, 0, ymax, imgConvBottom.width, imgConvBottom.height);
		
		int yMaior; // recebe a linha de maior valor entre yMaiorTop e yMaiorBottom		
		double teta1;
		if(yMaiorTop[0] > yMaiorBottom[0]){
			yMaior = yMaiorTop[1];
			teta1 = getTetaX(imgConvTop, imgConvTop.width/2, yMaior, (int)(imgConvTop.width*PORCENTAGEM_COMPRIMENTO_H), (int)(imgConvTop.height*PORCENTAGEM_ALTURA_H));
		}else{
			yMaior = yMaiorBottom[1];
			teta1 = getTetaX(imgConvBottom, imgConvBottom.width/2, yMaior, (int)(imgConvBottom.width*PORCENTAGEM_COMPRIMENTO_H), (int)(imgConvBottom.height*PORCENTAGEM_ALTURA_H));
		}		
		
		//______________________________________
		
		//GImage imgRoda = img.copia();
		//if (teta1 != 0.0) 
		//	img.roda(Math.toRadians(teta1), img.height/2, yMaior);
		
		//______________________________________
		
		GImage imgConvEsquerda = img.copia();
		imgConvEsquerda.conv(leKernel(new File("kernels\\bordaEsquerda.txt")), "bordaEsquerda.txt");
		int[] xMaiorEsquerda;
		xMaiorEsquerda = getMaxX(imgConvEsquerda, 0, 0, xmin, imgConvEsquerda.height);
		
		GImage imgConvDireita = img.copia();				
		imgConvDireita.conv(leKernel(new File("kernels\\bordaDireita.txt")), "bordaDireita.txt");			
		int[] xMaiorDireita;		
		xMaiorDireita = getMaxX(imgConvDireita, xmax, 0, imgConvDireita.width, imgConvDireita.height);				
		
		int xMaior; // recebe a coluna de maior valor entre xMaiorDireita e xMaiorEsquerda	
		double teta2;
		if(xMaiorDireita[0] > xMaiorEsquerda[0]){
			xMaior = xMaiorDireita[1];
			teta2 = getTetaY(imgConvDireita, xMaior, imgConvDireita.height/2, 
						(int)(imgConvDireita.width*PORCENTAGEM_COMPRIMENTO_V), (int)(imgConvDireita.height*PORCENTAGEM_ALTURA_V));
		}else{
			xMaior = xMaiorEsquerda[1];
			teta2 = getTetaY(imgConvEsquerda, xMaior, imgConvEsquerda.height/2, 
						(int)(imgConvEsquerda.width*PORCENTAGEM_COMPRIMENTO_V), (int)(imgConvEsquerda.height*PORCENTAGEM_ALTURA_V));
		}		
		
		//______________________________________
		
		if(teta1 != 0.0) 
			img.roda(Math.toRadians(teta1), Math.toRadians(teta2), xMaior, yMaior, 'x');
		
		//______________________________________
		
		img.pintaQuadrado((imgConvTop.width/2) - (image.width*PORCENTAGEM_COMPRIMENTO_H), yMaior -  (image.height*PORCENTAGEM_ALTURA_H), 
				(int)(image.width*PORCENTAGEM_COMPRIMENTO_H)*2, (int)(image.height*PORCENTAGEM_ALTURA_H)*2);
		
		img.pintaQuadrado(xMaior - (image.width*PORCENTAGEM_COMPRIMENTO_V), img.height/2 - (image.height*PORCENTAGEM_ALTURA_V), 
				(image.width*PORCENTAGEM_COMPRIMENTO_V)*2, (image.height*PORCENTAGEM_ALTURA_V)*2);
		
		img.pintaLinhaY(yMaiorTop[1], Color.BLUE.getRGB());
		img.pintaLinhaY(yMaiorBottom[1], Color.RED.getRGB());
		img.pintaLinhaX(xMaiorEsquerda[1], Color.GREEN.getRGB());
		img.pintaLinhaX(xMaiorDireita[1], Color.YELLOW.getRGB());		
		img.pintaQuadrado(xmin, ymin, xmax-xmin, ymax-ymin);
	}
	
	public void alinhaRetrato(GImage img){
		
		final double PORCENTAGEM_COMPRIMENTO_H = 0.30;
		final double PORCENTAGEM_ALTURA_H = 0.0034;
		
		final double PORCENTAGEM_ALTURA_V = 0.30;
		final double PORCENTAGEM_COMPRIMENTO_V = 0.0055;
		
		double xmin, ymin, xmax, ymax;
		
		xmin = img.width*(2./17.);
		ymin = img.height*(4./19.);
		xmax = img.width*(15./17.);
		ymax = img.height*(15./19.);
		
		//______________________________________
		
		GImage imgConvEsquerda = img.copia();
		imgConvEsquerda.conv(leKernel(new File("kernels\\bordaEsquerda.txt")), "bordaEsquerda.txt");
		int[] xMaiorEsquerda;
		xMaiorEsquerda = getMaxX(imgConvEsquerda, 0, 0, xmin, imgConvEsquerda.height);

		GImage imgConvDireita = img.copia();
		imgConvDireita.conv(leKernel(new File("kernels\\bordaDireita.txt")), "bordaDireita.txt");
		int[] xMaiorDireita;
		xMaiorDireita = getMaxX(imgConvDireita, xmax, 0, imgConvDireita.width, imgConvDireita.height);

		int xMaior; // recebe a coluna de maior valor entre xMaiorDireita e xMaiorEsquerda
		double teta1;
		if (xMaiorDireita[0] > xMaiorEsquerda[0]) {
			xMaior = xMaiorDireita[1];
			teta1 = getTetaY(imgConvDireita, xMaior, imgConvDireita.height / 2,
					(int) (imgConvDireita.width * PORCENTAGEM_COMPRIMENTO_V),
					(int) (imgConvDireita.height * PORCENTAGEM_ALTURA_V));
		} else {
			xMaior = xMaiorEsquerda[1];
			teta1 = getTetaY(imgConvEsquerda, xMaior, imgConvEsquerda.height / 2,
					(int) (imgConvEsquerda.width * PORCENTAGEM_COMPRIMENTO_V),
					(int) (imgConvEsquerda.height * PORCENTAGEM_ALTURA_V));
		}
		
		//______________________________________
		
		//GImage imgRoda = img.copia();
		//if (teta1 != 0.0) 
		//	img.roda(Math.toRadians(teta1), img.height/2, yMaior);
				
		//______________________________________
		
		GImage imgConvTop = img.copia(); 		
		imgConvTop.conv(leKernel(new File("kernels\\bordaTop.txt")), "bordaTop.txt");					
		int[] yMaiorTop; // recebe o maior valor e a linha de maior valor entre (0, 0) até (imgConvTop.width, ymin) // 0 = valor; 1 = posição	
		yMaiorTop = getMaxY(imgConvTop, 0, 0, imgConvTop.width, ymin);	
		
		GImage imgConvBottom = img.copia();		
		imgConvBottom.conv(leKernel(new File("kernels\\bordaBottom.txt")), "bordaBottom.txt");		
		int[] yMaiorBottom; // recebe o maior valor e a linha de maior valor entre (0, ymax) até (imgConvBottom.width, imgConvBottom.height) // 0 = valor; 1 = posição		
		yMaiorBottom = getMaxY(imgConvBottom, 0, ymax, imgConvBottom.width, imgConvBottom.height);
		
		int yMaior; // recebe a linha de maior valor entre yMaiorTop e yMaiorBottom		
		double teta2;
		if(yMaiorTop[0] > yMaiorBottom[0]){
			yMaior = yMaiorTop[1];
			teta2 = getTetaX(imgConvTop, imgConvTop.width/2, yMaior, 
					(int)(imgConvTop.width*PORCENTAGEM_COMPRIMENTO_H), (int)(imgConvTop.height*PORCENTAGEM_ALTURA_H));
		}else{
			yMaior = yMaiorBottom[1];
			teta2 = getTetaX(imgConvBottom, imgConvBottom.width/2, yMaior, 
					(int)(imgConvBottom.width*PORCENTAGEM_COMPRIMENTO_H), (int)(imgConvBottom.height*PORCENTAGEM_ALTURA_H));
		}	
		
		//______________________________________
		
		if(teta1 != 0.0) 
			img.roda(Math.toRadians(teta1), Math.toRadians(teta2), xMaior, yMaior,'y');	
		
		//______________________________________
		
		img.pintaQuadrado((imgConvTop.width/2) - (image.width*PORCENTAGEM_COMPRIMENTO_H), yMaior -  (image.height*PORCENTAGEM_ALTURA_H), 
				(int)(image.width*PORCENTAGEM_COMPRIMENTO_H)*2, (int)(image.height*PORCENTAGEM_ALTURA_H)*2);
		
		img.pintaQuadrado(xMaior - (image.width*PORCENTAGEM_COMPRIMENTO_V), img.height/2 - (image.height*PORCENTAGEM_ALTURA_V), 
				(image.width*PORCENTAGEM_COMPRIMENTO_V)*2, (image.height*PORCENTAGEM_ALTURA_V)*2);
		
		img.pintaLinhaY(yMaiorTop[1], Color.BLUE.getRGB());
		img.pintaLinhaY(yMaiorBottom[1], Color.RED.getRGB());
		img.pintaLinhaX(xMaiorEsquerda[1], Color.GREEN.getRGB());
		img.pintaLinhaX(xMaiorDireita[1], Color.YELLOW.getRGB());
		img.pintaQuadrado(xmin, ymin, xmax-xmin, ymax-ymin);
	}
	
	public int[] getMaxY(GImage img, double x, double y, double dx, double dy){
		
		return getMaxY(img, (int)x, (int)y, (int)dx, (int)dy);
	}
	
	public int[] getMaxY(GImage img, int x, int y, int dx, int dy){
		/*
		 * x e y é o meio da janela, dx e dy é a metade da distância do meio (distancia total = dx*2 ou dy*2)
		 */
		
		int xmin, xmax, ymin, ymax; 
		
		xmin = Math.max(0, x); 			// x'	
		xmax = Math.min(img.width-1, dx); 		// x''
		ymin = Math.max(0, y);			// y'
		ymax = Math.min(img.height-1, dy);		// y''
				
		System.out.println("Obtendo a linha de maior valor em Y, de ("+xmin+", "+ymin+") até ("+xmax+", "+ymax+")");
		
		int[] histV = Histograma.getHistY(img, xmin, ymin, xmax, ymax);		
		
		int[] r = Histograma.getPosMax(histV);
		
		System.out.println("A linha encontrada foi " + r[1] +" com valor de:  "+r[0]+"\n");
		
		return r; // retorna a posicao do maior valor armazenado no vetor histV[]
	}
	
	public int[] getMaxX(GImage img, double x, double y, double dx, double dy){
		
		return getMaxX(img, (int)x, (int)y, (int)dx, (int)dy);
	}
	
	public int[] getMaxX(GImage img, int x, int y, int dx, int dy){
		
		int xmin, xmax, ymin, ymax;
		
		xmin = Math.max(0, x);	
		xmax = Math.min(img.width-1, dx); 
		ymin = Math.max(0, y);	
		ymax = Math.min(img.height-1, dy);
		
		System.out.println("Obtendo a coluna de maior valor em X, de ("+xmin+", "+ymin+") até ("+xmax+", "+ymax+")");
		
		int[] histH = Histograma.getHistX(img, xmin, ymin, xmax, ymax);
				
		int r[] = Histograma.getPosMax(histH);
		
		System.out.println("A coluna encontrada foi " + r[1] +" com valor de:  "+r[0]+ "\n");
				
		return r;
	}
	
	double getTetaX(GImage img, int x, int y, int dx, int dy){
		//TETA EM X = CATETO ADJACENTE EM X
		
		System.out.println("Obtendo TetaX");
		double constantes[];
		double teta = 0;
		int N, disc;	
		
		int xmin, xmax, ymin, ymax;
		
		xmin = Math.max(0, x-dx);
		xmax = Math.min(img.width, x+dx);
		ymin = Math.max(0, y-dy);
		ymax = Math.min(img.height, y+dy);
		
		N = (2*dx)*(2*dy); // nº total de pontos da janela
		
		double[] NX = new double[N]; // coordenada x cada ponto de intensidade maior q o discriminador
		double[] NY = new double[N]; //
		
		
		int k = 0;
		int cor;
		
		disc = 30; //acima de 25 é branco, abaixo é preto
		
		System.out.println("Procurando valores acima de: "+disc+", na janela de ("+xmin+", "+ymin+") até ("+xmax+", "+ymax+")");
		
		for(int i = xmin; i < xmax; i++){
			for(int j = ymin; j < ymax; j++){
				cor = img.getIntRGB(i, j);
				//System.out.print(cor+";");
				if(cor > disc){
					NX[k] = i;
					NY[k] = j;
					k++;	
				}
			}
		}		
			
		if(k == 0){
			System.out.println("Nenhum valor a cima de "+disc+" foi encontrado\n");
			return 0.0;		
		}
		
		System.out.println("Foram encontrados "+k+" valores acima de "+disc);
		
		double [] X = new double [k]; // NX tem espaços vazios (nem todos elementos do vetor são preeenchidos)
		double [] Y = new double [k];	
		
		for(int i = 0; i < k; i++){
			X[i] = NX[i];
			Y[i] = NY[i];
		}
		
		constantes = GImage.minQuadradoPolinomial(X, Y, 1); // retorna as constantes da função reta y = a + bx
				
		teta = Math.toDegrees(Math.atan(constantes[1]));
		
		System.out.println("O angulo TetaX em graus é " + teta);
		System.out.println("");
		
		return teta;
	}
	
	double getTetaY(GImage img, int x, int y, int dx, int dy){
		// TETA EM Y = CATETO ADJACENTE EM Y
		
		System.out.println("Obtendo TetaY");
		double constantes[];
		double teta = 0;
		int N, disc;
		
		int xmin, xmax, ymin, ymax;
		
		xmin = Math.max(0, x-dx);
		xmax = Math.min(img.width, x+dx);
		ymin = Math.max(0, y-dy);
		ymax = Math.min(img.height, y+dy);
		
		N = (2*dx)*(2*dy);
		
		double [] NX = new double [N];
		double [] NY = new double [N];
		
		int k = 0;
		int cor;
		
		disc = 25;
		
		System.out.println("Procurando valores acima de: "+disc+", na janela de ("+xmin+", "+ymin+") até ("+xmax+", "+ymax+")");		
		
		for(int i = xmin; i < xmax; i++){
			// eixo abscissa
			for(int j = ymin; j < ymax; j++){
				// eixo ordenada
				cor = img.getIntRGB(i, j);
				//System.out.print(cor+";");
				if(cor > disc){
					NY[k] = i; // NY recebe valor da abscissa
					NX[k] = j;					
					k++;	
				}
			}
		}		
		
		if(k == 0){
			System.out.println("Nenhum valor a cima de "+disc+" foi encontrado\n");
			return 0.0;
		}
		
		System.out.println("Foram encontrados "+k+" valores acima de "+disc);
		
		double [] X = new double [k];
		double [] Y = new double [k];	
		
		for(int i = 0; i < k; i++){
			X[i] = NX[i];
			Y[i] = NY[i];
			//System.out.println("("+NX[i]+";"+NY[i]+")");
		}		

		
		constantes= GImage.minQuadradoPolinomial(X,Y,1);
		
		teta = Math.toDegrees(Math.atan(constantes[1]));
		
		System.out.println("O angulo TetaY em graus é " + teta);
		System.out.println("");
		
		return teta;
	}
	
	public Kernel leKernel(File f ){
    	File fCaminho = f;
   		String linha;
   		String [] ll;
   		float[] kk = new float[1];
   		kk[0] = 0;
   		int n = 0;   		
   		try { 	
   			FileReader fr2 = new FileReader(fCaminho);
   			LineNumberReader lnr = new LineNumberReader(fr2); 
   			linha = lnr.readLine(); 
   			linha = linha.replace("\t"," ");
   			linha = linha.replaceAll("  "," ");
   			linha = linha.replaceAll("  "," ");
   			linha = linha.replaceAll("  "," ");
   			ll = linha.split(" ");
   			n = ll.length;
   			kk = new float [n*n];
   			
   			for (int j = 0; j < n; j++){
   				kk[j] = Float.parseFloat(ll[j]);
   			}
   			
   			for(int i = 1; i < n; i++){
   				linha = lnr.readLine();
   				linha = linha.replace("\t"," ");
   				linha = linha.replaceAll("  "," ");
   				linha = linha.replaceAll("  "," ");
   				linha = linha.replaceAll("  "," ");
   				ll = linha.split(" "); 		
   				for(int j = 0; j < n; j++){
   					kk[n*i+j] = Float.parseFloat(ll[j]);
   				}  		
   			}
   			lnr.close();
   		}catch (IOException e) {
		      e.printStackTrace();
		      System.out.println(fCaminho.getName());
		      System.out.println("nao leu " +e );
   		} 
		return new Kernel(n, n, kk);
    }
}
