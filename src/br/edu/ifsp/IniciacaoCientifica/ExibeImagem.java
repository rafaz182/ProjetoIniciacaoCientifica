package br.edu.ifsp.IniciacaoCientifica;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ExibeImagem extends JFrame implements ActionListener{
	
	private GImage image;
	
	private JPanel barra;
	
	private JButton btnSalvar;
	
	public ExibeImagem(GImage image){
		setLayout(new BorderLayout());	
		setTitle("ExibeImagem_"+image.getNomeFile());
		
		this.image = image;
		
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
				
		setSize(image.getImagem().getWidth()+30, image.getImagem().getHeight()+30);		
		setVisible(true);
		setResizable(false);		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == btnSalvar){
			File outputfile = new File(image.getNomeKernel()+image.getNomeFile()+"_EI_.jpg");
			try {
				ImageIO.write(image.getImagem(), "jpg", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

}
