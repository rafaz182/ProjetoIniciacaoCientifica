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
		
		System.out.println("_________INICIO DO PROCESSAMENTO___________");
		System.out.println("arquivo: "+ this.getTitle());
		
		double r = 400./Math.min(image.getImagem().getHeight(), image.getImagem().getWidth());	 // define raz�o
		
		if(r != 1.)
			image.reduz(r);
		
		GImage imgConvH = image.copia(); // Convolucionar para encontrar linhas horizontais
		
		imgConvH.Conv(leKernel(new File("kernels\\k5x5-H.txt")));
				
		int ymaior = getMaxY(imgConvH, imgConvH.getImagem().getWidth()/2, imgConvH.getImagem().getHeight()/2,
				(imgConvH.getImagem().getWidth()/2) - 2, (imgConvH.getImagem().getHeight()/2) - 2);
		
		double teta1 = getTetaX(imgConvH, imgConvH.getImagem().getWidth()/2, ymaior, 70, 6);		
				
		GImage imgConvV = image.copia();
		
		if (teta1 != 0.0) 
			imgConvV.roda(Math.toRadians(teta1), imgConvV.getImagem().getHeight()/2, ymaior);
		
		imgConvV.Conv(leKernel(new File("kernels\\k5x5-V.txt")));		
			
		int xmaior = getMaxX(imgConvV, imgConvV.getImagem().getWidth()/2, imgConvV.getImagem().getHeight()/2,
				(imgConvV.getImagem().getWidth()/2) - 2, (imgConvV.getImagem().getHeight()/2) - 2);		
		
		double teta2 = getTetaY(imgConvV, xmaior, imgConvV.getImagem().getHeight()/2, 6, 70);
		
		if (teta1 != 0.0) 
			image.roda(Math.toRadians(teta1), Math.toRadians(teta2), xmaior, ymaior);
		
		image = imgConvV;
		
		System.out.println("_________FIM DO PROCESSAMENTO___________\n\n");		
	}
	
	int getMaxY(GImage img, int x, int y, int dx, int dy){
		/*
		 * x e y � o meio da janela, dx e dy � a metade da dist�ncia do meio (distancia total = dx*2 ou dy*2)
		 */
		
		int xmin, xmax, ymin, ymax; 
		
		xmin = Math.max(0, x-dx); 			// x'	
		xmax = Math.min(img.getImagem().getWidth()-1, x+dx); 		// x''
		ymin = Math.max(0, y-dy);			// y'
		ymax = Math.min(img.getImagem().getHeight()-1, y+dy);		// y''
		
		int [] histV = Histograma.getHistY(img, xmin, xmax);
		
		for(int i = 0; i < ymin; i++)
			histV[i] = 0;
		
		for(int i = ymax; i < img.getImagem().getHeight()-1; i++)
			histV[i] = 0;			
		
		int r = Histograma.getPosMax(histV);
		
		return r; // retorna a posicao do maior valor armazenado no vetor histV[]
	}
	
	int getMaxX(GImage img, int x, int y, int dx, int dy){
		int xmin, xmax, ymin, ymax;
		
		xmin = Math.max(0, x-dx);
		xmax = Math.min(img.getImagem().getWidth(), x+dx);
		ymin = Math.max(0, y-dy);
		ymax = Math.min(img.getImagem().getHeight(), y+dy);
		
		int[] histH = Histograma.getHistX(img, ymin, ymax);
		
		for(int i = 0; i < xmin; i++)
			histH[i] = 0;
		
		for(int i = xmax; i < img.getImagem().getWidth()-1; i++)
			histH[i] = 0;
		
		int r = Histograma.getPosMax(histH);
		
		return r;
	}
	
	double getTetaX(GImage img, int x, int y, int dx, int dy){
		//System.out.println(x+";"+y+";"+dx+";"+dy);
		double constantes[];
		double teta = 0;
		int N, disc;	
		
		N = (2*dx)*(2*dy); // n� total de pontos da janela
		
		double[] NX = new double[N]; // coordenada x cada ponto de intensidade maior q o discriminador
		double[] NY = new double[N]; //
		
		
		int k = 0;
		int cor;
		
		disc = 25; //acima de 25 � branco, abaixo � preto
		
		for(int i = x-dx; i < (x+dx); i++){
			for(int j = y-dy; j < (y+dy); j++){
				cor = img.getIntRGB(Color.WHITE, i, j);
				if(cor > disc){
					NX[k] = i;
					NY[k] = j;
					k++;	
				}
			}
		}
		
		double [] X = new double [k]; // NX tem espa�os vazios (nem todos elementos do vetor s�o preeenchidos)
		double [] Y = new double [k];	
		
		for(int i = 0; i < k; i++){
			X[i] = NX[i];
			Y[i] = NY[i];
		}
		
		constantes = GImage.minQuadradoPolinomial(X, Y, 1); // retorna as constantes da fun��o reta y = a + bx
				
		teta = Math.toDegrees(Math.atan(constantes[1]));
		
		return -teta;
	}
	
	double getTetaY(GImage img, int x, int y, int dx, int dy){
		
		double constantes[];
		double teta = 0;
		int N, disc;
		
		N = (2*dx)*(2*dy);
		
		double [] NX = new double [N];
		double [] NY = new double [N];
		
		int k = 0;
		int cor;
		
		disc = 25;
		
		for(int i = x-dx; i < (x+dx); i++){
			// eixo abscissa
			for(int j = y-dy; j < (y+dy); j++){
				// eixo ordenada
				cor = img.getIntRGB(Color.WHITE, i, j);
				//System.out.println(cor);
				if(cor > disc){
					NY[k] = i; // NY recebe valor da abscissa
					NX[k] = j;					
					k++;	
				}
			}
		}
		
		//System.out.println(k);
		double [] X = new double [k];
		double [] Y = new double [k];	
		
		for(int i = 0; i < k; i++){
			X[i] = NX[i];
			Y[i] = NY[i];
			//System.out.println("("+NX[i]+";"+NY[i]+")");
		}	
		

		
		constantes= GImage.minQuadradoPolinomial(X,Y,1);
		
		teta = Math.toDegrees(Math.atan(constantes[1]));
		//System.out.println("a + bx e teta " +constantes[0]+"  "+constantes[1]+ " "+(-teta)+ " "+Math.toDegrees(constantes[1]) );
		return -teta;
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
