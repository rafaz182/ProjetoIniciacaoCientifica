package br.edu.ifsp.IniciacaoCientifica;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class ProjetoGUI extends JFrame {
	
	private static final int WIDTH = 800;
	private static final int HEIGHT = 500;
	
	public ProjetoGUI(){
		setLayout(new BorderLayout());
		setTitle("Leitor de Imagens Escaneadas");
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/10, dim.height/4);
		
		add(new PainelPrincipal());
		
		setSize(WIDTH, HEIGHT);
		setVisible(true);
		setResizable(false);
	}
		
	private class PainelPrincipal extends JPanel implements ActionListener{
		
		private JLabel lblRoot;	
		
		private JTextField tfdRaiz;	// path das imagens
		
		private JButton btnSelec; // inicia o FileDialog
		
		private JFileChooser fcRoot;
		
		private File[] arquivos; // array de imagens selecionadas
		
		private JList<File> lstPathImagens; // JList q exibe o path de todos os arquivos
		
		private JButton btnProcessa; // inicia o processamento de somente 1 imagem (a selecionada no JList)
		
		private JButton btnProcessTodos; // inicia o processamento d todas imagens do JList
		
		private JScrollPane scrollList;
		
		private DefaultListModel<File> listModel;
		
		public PainelPrincipal(){
			
			lblRoot = new JLabel("Path: ");
			
			tfdRaiz = new JTextField("", 20);
			tfdRaiz.setEditable(false);
			
			btnSelec = new JButton("Selecionar");
			btnSelec.addActionListener(this);
			
			listModel = new DefaultListModel<File>();
			lstPathImagens = new JList<File>(listModel); // só exibe os elememtos de: listModel
			scrollList = new JScrollPane(lstPathImagens);	
			
			btnProcessa = new JButton("Processar");
			btnProcessa.addActionListener(this);
			
			btnProcessTodos = new JButton("Todos");
			
			setBounds(10, 10, ProjetoGUI.HEIGHT-10, ProjetoGUI.WIDTH-10);
			
			GroupLayout gl_Princ = new GroupLayout(this);
			gl_Princ.setAutoCreateGaps(true);
			gl_Princ.setAutoCreateContainerGaps(true);
			
			gl_Princ.setHorizontalGroup(gl_Princ.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addGroup(gl_Princ.createSequentialGroup()
							.addComponent(lblRoot)
							.addComponent(tfdRaiz)
							.addComponent(btnSelec)
							)
					.addGroup(gl_Princ.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(scrollList)
							.addGroup(gl_Princ.createSequentialGroup()
									.addComponent(btnProcessa)
									.addComponent(btnProcessTodos)
									)
							)
					);
			
			gl_Princ.setVerticalGroup(gl_Princ.createSequentialGroup()
					.addGroup(gl_Princ.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(lblRoot)
							.addComponent(tfdRaiz)
							.addComponent(btnSelec)
							)
					.addComponent(scrollList)
					.addGroup(gl_Princ.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(btnProcessa)
							.addComponent(btnProcessTodos)
							)
					);
			
			setLayout(gl_Princ);
			
			fcRoot = new JFileChooser();
			fcRoot.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fcRoot.setMultiSelectionEnabled(true);
			fcRoot.addChoosableFileFilter(new ImageFilter());
            fcRoot.setAcceptAllFileFilterUsed(false);			
			
		}
		
		

		@Override
		public void actionPerformed(ActionEvent e) {

			if(e.getSource() == btnSelec){
				int returnVal = fcRoot.showOpenDialog(this); // parametro: posicao de onde abrir o FileChooser
				
				if(returnVal == JFileChooser.APPROVE_OPTION){
					arquivos = fcRoot.getSelectedFiles();
					tfdRaiz.setText(fcRoot.getCurrentDirectory().toString());
					
					for(File a : arquivos)
						listModel.addElement(a);
					
					lstPathImagens.setSelectedIndex(0);															
				}
			}
			
			if(e.getSource() == btnProcessa){				
				ImageFrame janelaImg;
				try {
					janelaImg = new ImageFrame(listModel.getElementAt(lstPathImagens.getSelectedIndex()));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}			
			}
			
		}
		
	}
	
	private class ImageFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			// Accept all directories and all gif, jpg, tiff, or png files.
			if (f.isDirectory()) {
				return true;
			}

			String extension = Utils.getExtension(f);
			if (extension != null) {
				if (extension.equals(Utils.tiff) || extension.equals(Utils.tif) || extension.equals(Utils.gif)
						|| extension.equals(Utils.jpeg) || extension.equals(Utils.jpg) || extension.equals(Utils.png)) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return ".gif .jpeg .jpg .png .tif .tiff";
		}

	}

}
