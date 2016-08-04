package br.edu.ifsp.IniciacaoCientifica;

import java.awt.Color;

public abstract class Histograma {

	public static int[] getHistX(GImage imagem, int ini, int fim){
		return getHistX(imagem, Color.WHITE, ini, fim);
	}
    
    public static int[] getHistX(GImage imagem, Color c, int ini, int fim){
    	// cor funciona como filtro, para selecionar uma cor
    	
    	int[] x = new int[imagem.getImagem().getWidth()];
    	Color cor;
    	
    	if (ini < 0)
    		ini = 0;
    	if (fim > imagem.height) 
    		fim = imagem.height;
    	
    	int r, g, b;
    	r = c.getRed();
    	g = c.getGreen();
    	b = c.getBlue();
    	
    	for(int i = 0; i < imagem.getImagem().getWidth(); i++){	
    		x[i] = 0;
    		for(int j = ini; j < fim; j++){
    			cor = new Color(imagem.getImagem().getRGB(i, j));		  				
    			x[i] += ((cor.getRed()*r) + (cor.getGreen()*g) + (cor.getBlue()*b));
        	}
    		x[i] /= (fim-ini); // média das cores da coluna i
        }
    	
    	System.out.println("Criando histograma em X, de ("+0+", "+ini+") até ("+imagem.width+", "+fim+")");
    	
    	return x; 
    }
	
    public static int[] getHistY(GImage imagem, int ini, int fim){
    	return getHistY(imagem, Color.WHITE, ini, fim);
    }
    
    public static int[] getHistY(GImage imagem, Color c, int ini, int fim){
    	// cor funciona como filtro, para selecionar uma cor
    	
    	int[] y = new int[imagem.height];
    	Color cor;
    	int r, g, b;
    	
    	r = c.getRed(); 	// white = 255	black = 0
    	g = c.getGreen();	// white = 255	black = 0
    	b = c.getBlue();	// white = 255	black = 0
    	
    	if (ini < 0) 
    		ini = 0;
    	
    	if (fim > imagem.width) 
    		fim = imagem.width;
    	
    	for(int j = 0; j < imagem.height; j++){
    		//j = eixo ordenada
    		y[j] = 0;
    		for(int i = ini; i < fim; i++){
    			//i = eixo abscissa
    			cor = new Color(imagem.getImagem().getRGB(i, j));
    			y[j] +=  ((cor.getRed()*r) + (cor.getGreen()*g) + (cor.getBlue()*b));	
        	}
    		y[j] /= (fim-ini); // média das cores da linha j
    	}
    	
    	System.out.println("Criando histograma em Y, de ("+ini+", "+0+") até ("+fim+", "+imagem.height+")");
    	
    	return y; 
    }

    public static int[] getPosMax(int v[]){
    	return getPosMax(v, 0, v.length);
    }
    
    public static int[] getPosMax(int v[], int ini, int fim){
    	double vd[] = new double [v.length];
    	
    	for(int i = 0; i < v.length; i++)
    		vd[i] = (double)v[i];
    	
    	return getPosMax(vd, ini, fim);
    }
   
    public static int[] getPosMax(double v[]){
    	return getPosMax(v, 0, v.length);
    }
    
    public static int[] getPosMax(double v[], int ini, int fim){
        double max = 0.0; // recebe o maior valor armazenado no vetor v[]
        int iMaior = 0; // recebe a posicao do vetor v[] com maior valor
        
        if (ini < 0)
        	ini = 0;
        
        if(fim > v.length)
        	fim = v.length;
        
        for(int i = ini; i < fim; i++)
        	if(v[i] > max){
        		max = v[i]; 
        		iMaior = i;
        	}
        
        int[] resultado = new int[2];
        
        resultado[0] = (int)max;
        resultado[1] = iMaior;
        
    	return resultado; // retorna o valor e a posicao DO maior valor armazenado no vetor v[]
    }    
    

}
