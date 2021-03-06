package br.edu.ifsp.IniciacaoCientifica;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class GImage {
	
	private BufferedImage image;
	private String nomeFile;
	private String nomeKerne;
	public int width, height;	
	
	public GImage(File img) throws IOException{
		nomeFile = img.getName();
		
		image = ImageIO.read(img);
		width = image.getWidth();
    	height = image.getHeight();
	}
	
	public GImage(BufferedImage im, String nomeFile){
	    	image = im;
	    	
	    	this.nomeFile = nomeFile;
	    	
	    	width = image.getWidth();
	        height = image.getHeight();	        
	    }
	
	public BufferedImage getImagem() {
		return image;
	}
	
	public void reduz(double fat){
		
		System.out.println("Reduzindo a imagem para Largura: "+(int)(width*fat)+", Altura:"+(int)(height*fat)+"\n");
		
    	int tipo = AffineTransformOp.TYPE_BILINEAR; //TYPE_NEAREST_NEIGHBOR  TYPE_BICUBIC TYPE_BILINEAR // interpola��o dos pontos
    	
    	BufferedImage rimage = new BufferedImage((int)(image.getWidth()*fat), (int)(image.getHeight()*fat), BufferedImage.TYPE_3BYTE_BGR);
    	
    	AffineTransform at = AffineTransform.getScaleInstance(fat, fat);
    	
    	AffineTransformOp afo = new AffineTransformOp(at, tipo);   
    	
    	rimage = afo.filter(image, rimage);	
    	image = rimage;    	
    	
    	width = image.getWidth();
    	height = image.getHeight();
    }

    public void roda(double alfa, char eixo){
    	roda(alfa, 0, 0, eixo);	
    }
    
    public void roda(double alfa, double x, double y, char eixo){
    	// o angulo 'alfa' � passado em radianos
    	// negativo para girar no sentido anti-horario  
    	// eixo = cateto adjacente do angulo alfa
    	
    	AffineTransform at = new AffineTransform();     
    	BufferedImage rimage = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_3BYTE_BGR); 
    	
    	if(eixo == x){
	       	at.rotate(-(alfa), x, y);
	       	System.out.println("Transladando o ponto ("+(int)x+"; "+(int)y+") para a (0, 0)");
	       	System.out.print("Rotacionando a imagem em "+Math.toDegrees(-alfa)+ " graus\n");
	       	System.out.println("Transladando o ponto (0, 0) para a ("+(int)x+"; "+(int)y+")\n");
    	}else{
    		double sh = Math.sin(-(alfa));
    		System.out.println("Aplicando a distor��o linear em "+eixo+" com valor: " +sh);
    		at.shear(sh, 0);
    	}
               	
    	AffineTransformOp afo = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);  // TYPE_NEAREST_NEIGHBOR
     	afo.filter(image, rimage);     	
     	
    	image = rimage;     
    	
    	width = image.getWidth();
    	height = image.getHeight();
    }
    
    public void roda(double alfa, double alfa2, double x, double y, char eixo){
    	// o angulo 'alfa' e 'alfa2' � passado em radianos
    	// negativo para girar no sentido anti-horario
    	// o par�metro eixo � indica o eixo de maior dimens�o
    	
    	AffineTransform at = new AffineTransform();    	
    	BufferedImage rimage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);  
    	
    	
    	if(eixo == x){
	       	at.rotate(-(alfa), x, y); 
	       	System.out.println("Transladando o ponto ("+(int)x+"; "+(int)y+") para (0, 0)");
	       	System.out.print("Rotacionando a imagem em "+Math.toDegrees(-alfa)+ " graus\n");
	       	System.out.println("Transladando o ponto (0, 0) para a ("+(int)x+"; "+(int)y+")\n");
	       	
	        double sh = Math.sin(-(alfa2));	        
	        System.out.println("Aplicando a distor��o linear em "+eixo+" com valor: " +sh);
	    	at.shear(0, sh);
    	}else{
    		double sh = Math.sin(-(alfa));
    		System.out.println("Aplicando a distor��o linear em "+eixo+" com valor: " +sh);
    		at.shear(sh, 0); //valor em y est�vel, pontos se movem em x
    		
    		at.rotate(-(alfa2), x, y); 
	       	System.out.println("Transladando o ponto ("+(int)x+"; "+(int)y+") para (0, 0)");
	       	System.out.print("Rotacionando a imagem em "+Math.toDegrees(-alfa2)+ " graus\n");
	       	System.out.println("Transladando o ponto (0, 0) para a ("+(int)x+"; "+(int)y+")\n");
    	}
       	
    	AffineTransformOp afo = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);  // TYPE_NEAREST_NEIGHBOR
    	afo.filter(image, rimage);
    	
    	image = rimage;
    	
    	width = image.getWidth();
    	height = image.getHeight();
    }
    	
    public int getIntRGB(int x, int y){
    	
    	return getIntRGB(Color.WHITE, x, y);
    }
    
    public int getIntRGB(Color c, int x, int y){
    	int r = c.getRed();
    	int g = c.getGreen();
    	int b = c.getBlue();
    	int rgb;
    	
    	Color cor = new Color(image.getRGB(x,y));
    	rgb = ((cor.getRed()*r) + (cor.getGreen()*g) + (cor.getBlue()*b)) / (r+g+b);
    	
    	return rgb;
    }
    
	public int getGray(int x, int y){
    	Color c;
    	int corMediaPonto;    	
    	c = new Color(image.getRGB(x, y));
    	
    	corMediaPonto = (c.getRed() + c.getBlue() + c.getGreen())/3;
    	
    	return corMediaPonto;
    }
	
	public String getNomeFile(){
		return this.nomeFile;
	}
		
	public String getNomeKernel(){
		return this.nomeKerne;
	}
	
    public void setGray(int x, int y, boolean chave){    	
    	if(chave)
    		image.setRGB(x, y, 0xffffff);
    	else
    		image.setRGB(x, y, 0);
    }

    public void conv(Kernel k, String nomeKernel){
    	System.out.println("Convolucionando a imagem usando o kernel " + nomeKernel + "\n");
    	BufferedImage rimage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
    	
    	ConvolveOp COp = new ConvolveOp(k);
    	
    	COp.filter(image, rimage);
    	
    	this.nomeKerne = nomeKernel;
    	
    	image = rimage;   
    	
    	width = image.getWidth();
    	height = image.getHeight();
    }
    
    public void pintaQuadrado(double x, double y, double l, double h){
    	pintaQuadrado((int) x, (int) y, (int) l, (int) h);
    }
    
    public void pintaQuadrado(int x, int y, int l, int h){
    	//System.out.println(x+","+y+","+l+","+h);
    	
    	int c = 0x0000fff0;
    	
    	int xmin, xmax, ymin, ymax;
		
		xmin = Math.max(0, x);
		xmax = Math.min(width-2, x+l);
		ymin = Math.max(0, y);
		ymax = Math.min(height-2, y+h);
    	    	
    	for(int i = xmin; i < xmax; i++){
    	  image.setRGB(i, ymin, c);
    	  image.setRGB(i, ymax, c);
    	}
    	
    	for(int j = ymin; j < ymax; j++){
      	  image.setRGB(xmin, j, c);
      	  image.setRGB(xmax, j, c); 
      	}    	
    	
    }

    public void pintaLinhaY(int y, int cor){
    	pintaLinhaY(1, y, width-2, cor);
    }
    
    public void pintaLinhaY(int x, int y, int l, int cor){    	    	
    	
    	for(int i = x; i < x+l; i++)
    		image.setRGB(i, y, cor);
    }
    
    public void pintaLinhaX(int x, int cor){
    	pintaLinhaX(x, 1, height-2, cor);
    }
    
    public void pintaLinhaX(int x, int y, int h, int cor){    
    	
    	for(int i = y; i < y+h; i++)
    		image.setRGB(x, i, cor);
    }
    
    public static double[] ajustaBiParabolica(double[] x, double[] y, double[] f){
		// f(x, y) = a0 + a1*x + a2*y
		/*
		 * 
		 Create a real matrix with two rows and three columns, using a factory
		method that selects the implementation class for us.
			double[][] matrixData = { {1d,2d,3d}, {2d,5d,3d}};
			RealMatrix m = MatrixUtils.createRealMatrix(matrixData);
			
		 One more with three rows, two columns, this time instantiating the
		RealMatrix implementation class directly.
			double[][] matrixData2 = { {1d,2d}, {2d,5d}, {1d, 7d}};
			RealMatrix n = new Array2DRowRealMatrix(matrixData2);
		 */
		
		double[] constantes = new double[5]; //a0, a1, a2, a3, a4
		double[][] matriz = new double[x.length][5]; // abscissa.length tem que ser 9
		
		for(int i = 0; i < x.length; i++){
			matriz[i][0] = 1;
			matriz[i][1] = x[i];
			matriz[i][2] = y[i];
			matriz[i][3] = x[i] * x[i];
			matriz[i][4] = y[i] * y[i];
		}
		
		RealMatrix matrizX = MatrixUtils.createRealMatrix(matriz); // conjunto de derivadas por cada constante (a0, a1, a2)
		
		RealMatrix matrizXTransposta = matrizX.transpose();
		
		RealMatrix multXTX = matrizXTransposta.multiply(matrizX);
		
		RealMatrix MXTXI = MatrixUtils.inverse(multXTX);
		
		RealMatrix vetorTreshold = MatrixUtils.createColumnRealMatrix(f);
		
		RealMatrix matrizXTVetorY = matrizXTransposta.multiply(vetorTreshold);
		
		RealMatrix conjuntoConstantes = MXTXI.multiply(matrizXTVetorY);
		
		constantes = conjuntoConstantes.getColumn(0);
		
		return constantes;
	}
    
    public static double[] ajustaPlano(double[] x, double[] y, double[] f){
		// f(x, y) = a0 + a1*x + a2*y
		/*
		 * 
		 Create a real matrix with two rows and three columns, using a factory
		method that selects the implementation class for us.
			double[][] matrixData = { {1d,2d,3d}, {2d,5d,3d}};
			RealMatrix m = MatrixUtils.createRealMatrix(matrixData);
			
		 One more with three rows, two columns, this time instantiating the
		RealMatrix implementation class directly.
			double[][] matrixData2 = { {1d,2d}, {2d,5d}, {1d, 7d}};
			RealMatrix n = new Array2DRowRealMatrix(matrixData2);
		 */
		
		double[] constantes = new double[3]; //a0, a1, a2 
		double[][] matriz = new double[x.length][3]; // abscissa.length tem que ser 9
		
		for(int i = 0; i < x.length; i++){
			matriz[i][0] = 1;
			matriz[i][1] = x[i];
			matriz[i][2] = y[i];
		}
		
		RealMatrix matrizX = MatrixUtils.createRealMatrix(matriz); // conjunto de derivadas por cada constante (a0, a1, a2)
		
		RealMatrix matrizXTransposta = matrizX.transpose();
		
		RealMatrix multXTX = matrizXTransposta.multiply(matrizX);
		
		RealMatrix MXTXI = MatrixUtils.inverse(multXTX);
		
		RealMatrix vetorTreshold = MatrixUtils.createColumnRealMatrix(f);
		
		RealMatrix matrizXTVetorY = matrizXTransposta.multiply(vetorTreshold);
		
		RealMatrix conjuntoConstantes = MXTXI.multiply(matrizXTVetorY);
		
		constantes = conjuntoConstantes.getColumn(0);
		
		return constantes;
	}
    
    /*public static double[] minQuadradoPolinomial(double[] x, double[] y, int order){
    	System.out.println("Calculando o Qui-Quadrado de ordem " +(order+1));
    	double[][] X = new double[x.length][order + 1];
		
		for(int i = 0; i < x.length; i++){			
			for(int k = 0; k < order + 1; k++){
				X[i][k] = Math.pow(x[i], k);
			}
		}
		
		RealMatrix MX = MatrixUtils.createRealMatrix(X);
		RealMatrix MY = MatrixUtils.createColumnRealMatrix(y); // VETOR[] de 1 coluna e y.length-linhas
		RealMatrix MXT = MX.transpose();
		RealMatrix MXTX = MXT.multiply(MX);
		RealMatrix MXTXI = MatrixUtils.inverse(MXTX);
		RealMatrix MXTY=MXT.multiply(MY);
		RealMatrix A = MXTXI.multiply(MXTY);

		double[] a = A.getColumn(0);
		System.out.print("Foram encontrados as constantes ");
		for(double cons : a)
			System.out.print(cons+", ");
		
		System.out.println("");

		return a;
    }*/
    
    public static double[] minQuadradoPolinomial( double[] xi, double[] yi, int order ){
		int n=order+1;
    	double[][] x = new double[xi.length][n];
		for( int i = 0; i < xi.length; i++ ){
			//System.out.println(x[i]+";"+y[i]);
			for( int k = 0; k < n; k++ ){
				x[i][k] = Math.pow(xi[i], k);
			}
		}
		RealMatrix X = MatrixUtils.createRealMatrix(x);
		//System.out.println(X.toString());
		RealMatrix Y = MatrixUtils.createColumnRealMatrix(yi);
		//System.out.println(Y.toString());
		RealMatrix Xt = X.transpose();
		RealMatrix XtX = Xt.multiply(X);
		//System.out.println(XtX.toString());
		double[][] r = new double[n][n];
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(i==j)r[i][j]=1/Math.sqrt(XtX.getData()[i][j]);else r[i][j]=0;;
			}
		}
		RealMatrix R=MatrixUtils.createRealMatrix(r);
		RealMatrix RM=R.multiply(XtX);
		RealMatrix Ml=RM.multiply(R);
		//System.out.println(Ml.toString());
		RealMatrix Mli=MatrixUtils.inverse(Ml);
		RealMatrix RMli=R.multiply(Mli);
		RealMatrix XtXi=RMli.multiply(R);
		//System.out.println(XtXi.toString());
		//RealMatrix XtXi = MatrixUtils.inverse(XtX);
		RealMatrix XtY=Xt.multiply(Y);
		RealMatrix A = XtXi.multiply(XtY);
		double[] a = A.getColumn(0);
		return a;
    }
    
    public GImage copia(){
    	return copia(0, 0, image.getWidth(), image.getHeight());
    }
    
    public GImage copia(int x, int y, int dx, int dy){
    	BufferedImage si = image.getSubimage(x, y, dx, dy);

    	BufferedImage cp = new BufferedImage (si.getWidth(),si.getHeight(),si.getType());

    	cp.setData(si.getData());
    	
    	String nomeFile = this.nomeFile;
    	
    	return new GImage(cp, nomeFile);
    } 
    
}
