package br.edu.ifsp.IniciacaoCientifica;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

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
			lstPathImagens = new JList<File>(listModel);
			scrollList = new JScrollPane(lstPathImagens);	
			
			btnProcessa = new JButton("Processar");
			
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
			
		}
	}

}
