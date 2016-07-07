package br.edu.ifsp.IniciacaoCientifica;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class GImage {
	
	private BufferedImage image;
	private int xRight, xLeft, yTop, yBottom; // coordenada dos limites do documento
	
	public GImage(BufferedImage img){
		image = img;
	}
	
	public BufferedImage getImagem() {
		return image;
	}
	
	public void reduz(double fat){    	
		double f05 = 0.5; // Aplica uma reducao de 50%
    	int tipo = AffineTransformOp.TYPE_BILINEAR; //TYPE_NEAREST_NEIGHBOR  TYPE_BICUBIC TYPE_BILINEAR // interpolação dos pontos
    	while (fat<0.5){
    		fat = fat*2;
    		BufferedImage rimage = new BufferedImage((int)(image.getWidth()*f05), (int)(image.getHeight()*f05), BufferedImage.TYPE_3BYTE_BGR);	//cria a malha bitmap
    		AffineTransform at = AffineTransform.getScaleInstance(f05, f05); // cria matriz de redução
    		AffineTransformOp afo = new AffineTransformOp(at, tipo);   // cria matriz homogenea de redução e interpolaçao
    		rimage = afo.filter(image, rimage);	
    		image = rimage;
       	}
    	BufferedImage rimage = new BufferedImage((int)(image.getWidth()*fat), (int)(image.getHeight()*fat), BufferedImage.TYPE_3BYTE_BGR);	
    	AffineTransform at = AffineTransform.getScaleInstance(fat, fat);
    	AffineTransformOp afo = new AffineTransformOp(at, tipo);   
    	rimage = afo.filter(image, rimage);	
    	image = rimage;    	
    }

    public void roda(double alfa){
    	roda(alfa,0,0);	
    }
    
    public void roda(double alfa, double x, double y){
    	// o angulo 'alfa' é passado em radianos
    	
    	AffineTransform at = new AffineTransform(); 
       	at.rotate(alfa, x, y);
       	
       	BufferedImage rimage = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_3BYTE_BGR); 
       	
    	AffineTransformOp afo = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);  // TYPE_NEAREST_NEIGHBOR
     	afo.filter(image, rimage);
     	
    	image = rimage;     
    }
    
    public void roda(double alfa, double alfa2, double x, double y){
    	// o angulo 'alfa' e 'alfa2' é passado em radianos
    	AffineTransform at = new AffineTransform();
    	
    	BufferedImage rimage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
    	
       	at.rotate(alfa, x, y);
       	
       	double sh = Math.sin(alfa2);
       	
       	at.shear(sh, 0);
       	
    	AffineTransformOp afo = new AffineTransformOp(at,AffineTransformOp.TYPE_BILINEAR);  // TYPE_NEAREST_NEIGHBOR
    	afo.filter(image, rimage);
    	
    	image = rimage;
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

    public void setGray(int x, int y, boolean chave){    	
    	if(chave)
    		image.setRGB(x, y, 0xffffff);
    	else
    		image.setRGB(x, y, 0);
    }

    public void Conv(Kernel k){
    	BufferedImage rimage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
    	
    	ConvolveOp COp = new ConvolveOp(k);
    	
    	COp.filter(image, rimage);
    	
    	image = rimage;    	
    }
    
    public static double[] minQuadradoPolinomial(double[] x, double[] y, int order){
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

		return a;
    }
    
    public GImage copia(){
    	return copia(0, 0, image.getWidth(), image.getHeight());
    }
    
    public GImage copia(int x, int y, int dx, int dy){
    	BufferedImage si = image.getSubimage(x, y, dx, dy);

    	BufferedImage cp = new BufferedImage (si.getWidth(),si.getHeight(),si.getType());

    	cp.setData(si.getData());
    	
    	return new GImage(cp);
    } 
    
}
