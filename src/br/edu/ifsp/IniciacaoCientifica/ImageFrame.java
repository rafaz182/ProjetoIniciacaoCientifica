package br.edu.ifsp.IniciacaoCientifica;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class ImageFrame extends JFrame implements ActionListener{
	
	private static int counter = 0;
	
	private GImage image;
	
	private JPanel barra;
	
	private JButton btnSalvar;
	
	//private JPanel panelImg;

	public ImageFrame(File img) throws IOException{
		setLayout(new BorderLayout());	
		setTitle(img.getName());
				
		image = new GImage(img);		
				
		processaImg();
		
		ImageIcon icon = new ImageIcon(image.getImagem());
		
		JLabel imgLabel = new JLabel(icon);
		
		barra = new JPanel();
		
		btnSalvar = new JButton("Salvar");
		btnSalvar.addActionListener(this);
		
		barra.setLayout(new FlowLayout());
		barra.setBounds(0, 0, image.getImagem().getWidth(), 75);
		
		barra.add(btnSalvar);
		
		add(barra, BorderLayout.NORTH);
		
		add(new JScrollPane(imgLabel), BorderLayout.CENTER);
				
		//setSize(image.getImagem().getWidth()+30, image.getImagem().getHeight()+30);		
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		setVisible(true);
		setResizable(true);		
	}
	
	public void processaImg(){
		long tempoInicial1 = System.currentTimeMillis();
		
		System.out.println("_________INICIO DO PROCESSAMENTO["+counter+"]___________");
		System.out.println("arquivo: "+ this.getTitle()+"\n");
		
		System.out.println("Largura:"+image.width+", Altura:"+image.height+"\n");
		
		double fatorReducao = 200./Math.min(image.height, image.width);	 // define razão
		
		GImage imgReduzida = image.copia();
		
		if(fatorReducao != 1.)
			imgReduzida.reduz(fatorReducao);
		
		//System.out.println("Largura:"+image.width+", Altura:"+image.height+"\n");	
		
		//new ExibeImagem(image);
		
		double[] tetaXeY = new double[2];
		if(imgReduzida.width > imgReduzida.height)
			alinhaPaisagem(imgReduzida);
		else
			tetaXeY = alinhaRetrato(imgReduzida);
		
		int[][] limitFolha = detectaFolha(imgReduzida);
		
		double fatorAmpliacao = Math.min(image.height, image.width) / 200.;
		
		limitFolha[0][0] *= fatorAmpliacao;
		limitFolha[0][1] *= fatorAmpliacao;
		limitFolha[1][0] *= fatorAmpliacao;
		limitFolha[1][1] *= fatorAmpliacao;
		
		image.roda(tetaXeY[1],  tetaXeY[0], image.width/2, image.height/2, 'y');
		
		//image.pintaQuadrado(limitFolha[0][0], limitFolha[0][1], limitFolha[1][0] - limitFolha[0][0], limitFolha[1][1] - limitFolha[0][1]);
		
		int divisoes = 9; // tem de ser um numero quadrado perfeito: 4, 9, 25, 36 ...
		
		int[][][] mapaFolhaDividida = divideFolha(limitFolha, divisoes);
		
		double[] discriminadorPuro = new double[divisoes];
		
		double discriGlobal;
		
		double bias = -1.2;
		
		double[] vetorX = new double[divisoes];
		double[] vetorY = new double[divisoes];
		
		discriGlobal = calcThresholdNiblackTechnique(image, limitFolha[0][0],limitFolha[0][1], limitFolha[1][0] - limitFolha[0][0], 
				limitFolha[1][1] - limitFolha[0][1], bias);
		
		for(int k = 0; k < divisoes; k++){
			discriminadorPuro[k] = calcThresholdNiblackTechnique(image, mapaFolhaDividida[k][0][0], mapaFolhaDividida[k][0][1], 
					(mapaFolhaDividida[k][1][0]-mapaFolhaDividida[k][0][0]), (mapaFolhaDividida[k][1][1]-mapaFolhaDividida[k][0][1]), bias);
			
			vetorX[k] = (mapaFolhaDividida[k][0][0] + mapaFolhaDividida[k][1][0]) / 2;
			vetorY[k] = (mapaFolhaDividida[k][0][1] + mapaFolhaDividida[k][1][1]) / 2;
		}
		
		//GImage norm = image.copia();
		
		//double[] parametros = GImage.ajustaPlano(vetorX, vetorY, discriminadorPuro);
		
		double[] parametros = GImage.ajustaBiParabolica(vetorX, vetorY, discriminadorPuro);
		
		//aplicaThresholdGlobal(norm, limitFolha, discriGlobal);
		
		//aplicaThresholdPlano(image, limitFolha, parametros);
		
		aplicaThresholdBiParabolica(image, limitFolha, parametros);
		
		/*for(int k = 0; k < divisoes; k++){
			image.pintaQuadrado(mapaFolhaDividida[k][0][0], mapaFolhaDividida[k][0][1], (mapaFolhaDividida[k][1][0]-mapaFolhaDividida[k][0][0]),
					(mapaFolhaDividida[k][1][1]-mapaFolhaDividida[k][0][1]));
		}*/
		
		//new ExibeImagem(norm);
		
		long tempoFinal1 = System.currentTimeMillis(); 
		System.out.println("tempo total = " + (tempoFinal1 - tempoInicial1));
		System.out.println("_________FIM DO PROCESSAMENTO["+counter+"]___________\n\n");
		counter++;
	}

	public void aplicaThresholdBiParabolica(GImage img, int[][] limitFolha, double[] params){
		
		int x = limitFolha[0][0];
		int y = limitFolha[0][1];
		int l = limitFolha[1][0] - limitFolha[0][0];
		int h = limitFolha[1][1] - limitFolha[0][1];
		
		double discriminador = 0;
		
		for(int i = x; i < (x+l); i++){
			for(int j = y; j < (y+h); j++){
				discriminador = params[0] + (params[1] * i) + (params[2] * j) + (params[3] * (i * i)) + (params[4] * (j * j));
				int cinza = img.getGray(i, j);
				
				if(cinza > discriminador)
					img.setGray(i, j, true);
				else
					img.setGray(i, j, false);
			}
		}
	}
	
	public void aplicaThresholdPlano(GImage img, int[][] limitFolha, double[] params){
		
		int x = limitFolha[0][0];
		int y = limitFolha[0][1];
		int l = limitFolha[1][0] - limitFolha[0][0];
		int h = limitFolha[1][1] - limitFolha[0][1];
		
		double discriminador = 0;
		
		for(int i = x; i < (x+l); i++){
			for(int j = y; j < (y+h); j++){
				discriminador = params[0] + (params[1] * i) + (params[2] * j);
				int cinza = img.getGray(i, j);
				
				if(cinza > discriminador)
					img.setGray(i, j, true);
				else
					img.setGray(i, j, false);
			}
		}
	}
	
	public void aplicaThreshold(GImage img, int[][][] gapQuadrados, double[] discriminador, int divisoes){
		for(int k = 0; k < divisoes; k++){
			int x = gapQuadrados[k][0][0];
			int y = gapQuadrados[k][0][1];
			int l = gapQuadrados[k][1][0] - gapQuadrados[k][0][0];
			int h = gapQuadrados[k][1][1] - gapQuadrados[k][0][1];
			
			for(int j = y; j < (y+h); j++){
				for(int i = x; i < (x+l); i++){
					int cinza = img.getGray(i, j);
					if(cinza > discriminador[k])
						img.setGray(i, j, true);
					else
						img.setGray(i, j, false);
				}
			}
		}
		
	}
	
	public void aplicaThresholdGlobal(GImage img, int[][] limitFolha, double discriminador){
		int x = limitFolha[0][0];
		int y = limitFolha[0][1];
		int l = limitFolha[1][0] - limitFolha[0][0];
		int h = limitFolha[1][1] - limitFolha[0][1];
		
		for(int j = y; j < (y+h); j++){
			for(int i = x; i < (x+l); i++){
				int cinza = img.getGray(i, j);
				if(cinza > discriminador)
					img.setGray(i, j, true);
				else
					img.setGray(i, j, false);
			}
		}
	}
	
	public double calcThresholdNiblackTechnique(GImage img, int x, int y, int l, int h, double bias){
		double threshold = 0;
		double media = 0;
		int N = 0;
		double desvio = 0;

		int[] histogramaCor = new int[256];
		
		for(int a = 0; a < 256; a++)
			histogramaCor[a] = 0;
		
		for(int i = x; i < (x+l); i++){
			for(int j = y; j < (y+h); j++){
				histogramaCor[img.getGray(i, j)]++;
				media += img.getGray(i, j);
				N++;
			}
		}
		
		media /= N;
	
		
		for(int i = x; i < (x+l); i++){
			for(int j = y; j < (y+h); j++){
				desvio += (img.getGray(i, j)-media) * (img.getGray(i, j)-media);
			}
		}
		
		desvio = Math.sqrt((desvio/N));
		
		threshold = media + (bias*desvio);	
				
		return threshold;
	}
	
	public int[][][] divideFolha(int[][] limitFolha, int divisoes){
		
		int[][][] mapaFolhaDividida = new int[divisoes][2][2];
		
		int dx = limitFolha[1][0] - limitFolha[0][0]; 
		int dy = limitFolha[1][1] - limitFolha[0][1];
		
		System.out.println("Dividindo a folha em "+divisoes+" partes.");
		
		double quad = Math.sqrt(divisoes);
		//System.out.println(quad);
		
		int k = 0;
		for(int j = 0; j < quad; j++){
			for(int i = 0; i < quad; i++){
				mapaFolhaDividida[k][0][0] = limitFolha[0][0] + ((int)(dx* (i/quad)));
				mapaFolhaDividida[k][1][0] = limitFolha[0][0] + ((int)(dx* ((i+1)/quad))); 
				
				mapaFolhaDividida[k][0][1] = limitFolha[0][1] + ((int)(dy* (j/quad)));
				mapaFolhaDividida[k][1][1] = limitFolha[0][1] + ((int)(dy* ((j+1)/quad)));
				
				k++;
				
				System.out.println("Quadrado "+k+": de ("
						+(limitFolha[0][0] + ((int)(dx* (i/quad))))+
						", "
						+(limitFolha[0][1] + ((int)(dy* (j/quad))))+
						") até ("
						+(limitFolha[0][0] + ((int)(dx* ((i+1)/quad))))+
						", "
						+(limitFolha[0][1] + ((int)(dy* ((j+1)/quad))))+
						")"
						);
			}
		}		
		
		System.out.println("");
		
		return mapaFolhaDividida;
	}
	
	public int[][] detectaFolha(GImage img){
		int[][] coordenada = new int[2][2];
		
		double xmin, ymin, xmax, ymax;
		
		xmin = img.width*(4./19.); // retrato
		ymin = img.height*(6./23.);
		xmax = img.width*(15./19.);
		ymax = img.height*(17./23.);
		
		int gap = 10;
		
		GImage imgConvEsquerda = img.copia();
		imgConvEsquerda.conv(leKernel(new File("kernels\\bordaEsquerda7x7.txt")), "bordaEsquerda7x7.txt");
		int[] xMaiorEsquerda;
		xMaiorEsquerda = getMaxX(imgConvEsquerda, gap, gap, xmin, imgConvEsquerda.height-gap);
		
		//new ExibeImagem(imgConvEsquerda);
		
		GImage imgConvTop = img.copia(); 		
		imgConvTop.conv(leKernel(new File("kernels\\bordaTop7x7.txt")), "bordaTop7x7.txt");					
		int[] yMaiorTop; // recebe o maior valor e a linha de maior valor entre (0, 0) até (imgConvTop.width, ymin) // 0 = valor; 1 = posição	
		yMaiorTop = getMaxY(imgConvTop, gap, gap, imgConvTop.width-gap, ymin);	
		
		//new ExibeImagem(imgConvTop);
			
		coordenada[0][0] = xMaiorEsquerda[1];
		coordenada[0][1] = yMaiorTop[1];
		
		GImage imgConvDireita = img.copia();
		imgConvDireita.conv(leKernel(new File("kernels\\bordaDireita7x7.txt")), "bordaDireita7x7.txt");
		int[] xMaiorDireita;
		xMaiorDireita = getMaxX(imgConvDireita, xmax, gap, imgConvDireita.width-gap, imgConvDireita.height-gap);	
		
		//new ExibeImagem(imgConvDireita);
		
		GImage imgConvBottom = img.copia();		
		imgConvBottom.conv(leKernel(new File("kernels\\bordaBottom7x7.txt")), "bordaBottom7x7.txt");		
		int[] yMaiorBottom; // recebe o maior valor e a linha de maior valor entre (0, ymax) até (imgConvBottom.width, imgConvBottom.height) // 0 = valor; 1 = posição		
		yMaiorBottom = getMaxY(imgConvBottom, gap, ymax, imgConvBottom.width-gap, imgConvBottom.height-gap);
		
		//new ExibeImagem(imgConvBottom);
		
		coordenada[1][0] = xMaiorDireita[1];
		coordenada[1][1] = yMaiorBottom[1];
		
		//System.out.println("A folha tem inicio nas coordenadas ("+coordenada[0][0]+", "+coordenada[0][1]+") até ("+coordenada[1][0]+", "+coordenada[1][1]+").\n");
		
		return coordenada;
	}
	
	public void alinhaPaisagem(GImage img){
		/*
		 * Esta função convoluciona o objeto img 4 vezes, cada vez para uma borda da folha (Top, Bottom, Direita, Esquerda)
		 * Obtem os angulos TetaX e TetaY
		 * */
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
		
		//______________________________________ O primeiro angulo é definido com base na maior linha (no caso, a horizontal)
		
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
	
	public double[] alinhaRetrato(GImage img){
		/*
		 * Esta função convoluciona o objeto img 4 vezes, cada vez para uma borda da folha (Top, Bottom, Direita, Esquerda)
		 * Obtem os angulos TetaX e TetaY
		 * */
		
		double[] tetaXeY = new double[2]; //tetaXeY[0] = tetaX; tetaXeY[1] = tetaY
		
		final double PORCENTAGEM_COMPRIMENTO_H = 0.30;
		final double PORCENTAGEM_ALTURA_H = 0.0040;
		
		final double PORCENTAGEM_ALTURA_V = 0.24;
		final double PORCENTAGEM_COMPRIMENTO_V = 0.0060;
		
		double xmin, ymin, xmax, ymax;
		
		char eixo = 'y';
		
		xmin = img.width*(4./19.); // retrato
		ymin = img.height*(6./23.);
		xmax = img.width*(15./19.);
		ymax = img.height*(17./23.);
		
		//______________________________________ O primeiro angulo é definido com base na maior linha (no caso, a vertical)
		
		GImage imgConvEsquerda = img.copia();
		imgConvEsquerda.conv(leKernel(new File("kernels\\bordaEsquerda7x7.txt")), "bordaEsquerda7x7.txt");
		int[] xMaiorEsquerda;
		xMaiorEsquerda = getMaxX(imgConvEsquerda, 0, 0, xmin, imgConvEsquerda.height);
		
		//new ExibeImagem(imgConvEsquerda);

		GImage imgConvDireita = img.copia();
		imgConvDireita.conv(leKernel(new File("kernels\\bordaDireita7x7.txt")), "bordaDireita7x7.txt");
		int[] xMaiorDireita;
		xMaiorDireita = getMaxX(imgConvDireita, xmax, 0, imgConvDireita.width, imgConvDireita.height);

		//new ExibeImagem(imgConvDireita);
		
		int xMaior; // recebe a coluna de maior valor entre xMaiorDireita e xMaiorEsquerda
		double tetaY;
		if(xMaiorDireita[0] > xMaiorEsquerda[0]){
			xMaior = xMaiorDireita[1];
			tetaY = getTetaY(imgConvDireita, xMaior, imgConvDireita.height / 2,
					(int) (imgConvDireita.width * PORCENTAGEM_COMPRIMENTO_V),
					(int) (imgConvDireita.height * PORCENTAGEM_ALTURA_V));
		}else{
			xMaior = xMaiorEsquerda[1];
			tetaY = getTetaY(imgConvEsquerda, xMaior, imgConvEsquerda.height / 2,
					(int) (imgConvEsquerda.width * PORCENTAGEM_COMPRIMENTO_V),
					(int) (imgConvEsquerda.height * PORCENTAGEM_ALTURA_V));
		}
		
		//______________________________________
		
		GImage imgConvTop = img.copia(); 
		if(tetaY != 0.0){
			imgConvTop.roda(tetaY, eixo);
		}	
		
		imgConvTop.conv(leKernel(new File("kernels\\bordaTop7x7.txt")), "bordaTop7x7.txt");					
		int[] yMaiorTop; // recebe o maior valor e a linha de maior valor entre (0, 0) até (imgConvTop.width, ymin) // 0 = valor; 1 = posição	
		yMaiorTop = getMaxY(imgConvTop, 20, 20, imgConvTop.width-20, ymin);	
		
		//new ExibeImagem(imgConvTop);
		
		GImage imgConvBottom = img.copia();	
		if(tetaY != 0.0){
			imgConvBottom.roda(tetaY, eixo);
		}
		imgConvBottom.conv(leKernel(new File("kernels\\bordaBottom7x7.txt")), "bordaBottom7x7.txt");		
		int[] yMaiorBottom; // recebe o maior valor e a linha de maior valor entre (0, ymax) até (imgConvBottom.width, imgConvBottom.height) // 0 = valor; 1 = posição		
		yMaiorBottom = getMaxY(imgConvBottom, 20, ymax, imgConvBottom.width-20, imgConvBottom.height-20);
		
		//new ExibeImagem(imgConvBottom);
		
		int yMaior; // recebe a linha de maior valor entre yMaiorTop e yMaiorBottom		
		double tetaX;
		if(yMaiorTop[0] > yMaiorBottom[0]){
			yMaior = yMaiorTop[1];
			tetaX = getTetaX(imgConvTop, imgConvTop.width/2, yMaior, 
					(int)(imgConvTop.width*PORCENTAGEM_COMPRIMENTO_H), (int)(imgConvTop.height*PORCENTAGEM_ALTURA_H));
		}else{
			yMaior = yMaiorBottom[1];
			tetaX = getTetaX(imgConvBottom, imgConvBottom.width/2, yMaior, 
					(int)(imgConvBottom.width*PORCENTAGEM_COMPRIMENTO_H), (int)(imgConvBottom.height*PORCENTAGEM_ALTURA_H));
		}	
		
		//______________________________________
		
		if(tetaY != 0.0) 
			img.roda(tetaY, tetaX, xMaior, yMaior, eixo);	
		
		//______________________________________
		
		tetaXeY[0] = tetaX;
		tetaXeY[1] = tetaY;
		
		return tetaXeY;
		
		/*img.pintaQuadrado((imgConvTop.width/2) - (image.width*PORCENTAGEM_COMPRIMENTO_H), yMaior -  (image.height*PORCENTAGEM_ALTURA_H), 
				(int)(image.width*PORCENTAGEM_COMPRIMENTO_H)*2, (int)(image.height*PORCENTAGEM_ALTURA_H)*2);
		
		img.pintaQuadrado(xMaior - (image.width*PORCENTAGEM_COMPRIMENTO_V), img.height/2 - (image.height*PORCENTAGEM_ALTURA_V), 
				(image.width*PORCENTAGEM_COMPRIMENTO_V)*2, (image.height*PORCENTAGEM_ALTURA_V)*2);
		
		img.pintaLinhaY(yMaiorTop[1], Color.BLUE.getRGB());
		img.pintaLinhaY(yMaiorBottom[1], Color.RED.getRGB());
		img.pintaLinhaX(xMaiorEsquerda[1], Color.GREEN.getRGB());
		img.pintaLinhaX(xMaiorDireita[1], Color.YELLOW.getRGB());
		img.pintaQuadrado(xmin, ymin, xmax-xmin, ymax-ymin);*/
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
		
		disc = 25; //acima de 25 é branco, abaixo é preto (necessita de revisão: 'pq 25 é escolhido?)
		
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
				
		teta = Math.atan(constantes[1]);
		
		System.out.println("O angulo TetaX em graus é " + Math.toDegrees(teta));
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

		
		constantes = GImage.minQuadradoPolinomial(X,Y,1);
		
		teta = Math.atan(constantes[1]);
		
		System.out.println("O angulo TetaY em graus é " + Math.toDegrees(teta));
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == btnSalvar){
			File outputfile = new File(image.getNomeKernel()+image.getNomeFile()+"_IF_.JPG");
			try {
				ImageIO.write(image.getImagem(), "jpg", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		
	}
}
