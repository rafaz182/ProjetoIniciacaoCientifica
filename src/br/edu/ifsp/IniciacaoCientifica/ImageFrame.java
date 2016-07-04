package br.edu.ifsp.IniciacaoCientifica;

import java.awt.BorderLayout;
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
		
		image = new GImage(ImageIO.read(img));
		
		ImageIcon icon = new ImageIcon(image.getImagem());
		
		JLabel imgLabel = new JLabel(icon);
		
		add(new JScrollPane(imgLabel), BorderLayout.CENTER);
				
		setSize(image.getImagem().getWidth()+30, image.getImagem().getHeight()+30);
		setVisible(true);
		setResizable(false);
		
	}
	
	public void processaImg(){
		long tempoInicial1 = System.currentTimeMillis();
		
		double r = 400./Math.min(image.getImagem().getHeight(), image.getImagem().getWidth());	 // define razão
		
		if(r != 1.)
			image.reduz(r);
		
		
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
