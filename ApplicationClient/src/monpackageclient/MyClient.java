package monpackageclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadT1Client;
import com.sun.javacard.apduio.CadTransportException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class MyClient extends JPanel  {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final byte CLA_MONAPPLET = (byte) 0xB0;
	
	public static final byte INS_DEBITER_COMPTEUR = 0x00;
	public static final byte INS_INTERROGER_COMPTEUR = 0x01; 
	public static final byte INS_AJOUTER_COMPTEUR = 0x02;
	public static final byte INS_ECRASER_COMPTEUR = 0x03;
	
	private JButton btn_Reset;
    private JButton btn_Verif;
    private JButton btn_Cancel;
    
    private JButton btn_Debit;
    private JButton btn_Ajout;
    private JButton btn_Show;
    private JButton btn_ResetSolde;
    
    private JLabel lb_ATM;
    private JLabel lb_ERROR;
    private JPasswordField in_Pin;
    private JLabel lb_pin;
    private JTextField in_Order;

    private final String PIN = "0000";
    private  int nb_attempts = 1;
    private  boolean pinState = false;
    CadT1Client cad;
	Socket sckCarte;
	private byte solde;
	
	
    public MyClient() throws IOException, CadTransportException {
        //construct components
        btn_Reset = new JButton ("Reset");
        btn_Verif = new JButton ("Verify");
        btn_Cancel = new JButton ("Cancel");
        
        btn_Debit = new JButton ("Débiter");
        btn_Ajout = new JButton ("Ajouter");
        btn_Show = new JButton ("Afficher Solde");
        btn_ResetSolde = new JButton ("écraser");
        
        
        lb_ATM = new JLabel ("ATM Machine");
        lb_ERROR = new JLabel ("Enter your code pin please ");
        in_Pin = new JPasswordField (0);
        lb_pin = new JLabel ("Pin Code :");
        in_Order = new JTextField(0);

        //adjust size and set layout
        setPreferredSize (new Dimension (677, 288));
        setLayout (null);
        lb_ATM.setFont(new Font("Arial", Font.BOLD, 25));
        //add components
        add (btn_Reset);
        add (btn_Verif);
        add (btn_Cancel);
        add (btn_Debit);
        add (btn_Ajout);
        add (btn_Show);
        add (btn_ResetSolde);
        add (lb_ATM);
        add (lb_ERROR);
        add (in_Pin);
        add (lb_pin);
        add (in_Order);

        //set component bounds (only needed by Absolute Positioning)
        btn_Reset.setBounds (426, 220, 90, 30);
        btn_Verif.setBounds (140, 220, 90, 30);
        btn_Cancel.setBounds (280, 220, 90, 30);
        lb_ATM.setBounds (245, 50, 170, 30);
        lb_ERROR.setBounds (150, 180, 300, 30);
        in_Pin.setBounds (150, 130, 355, 45);
        lb_pin.setBounds (150, 100, 100, 25);

        
        
        
        
        btn_Verif.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	System.out.println("button clicked");
                String pin = in_Pin.getText();
                System.out.println(pin+" == "+PIN);
                if(pin.equals(PIN)){
                    lb_ERROR.setText("pin correct");
                    lb_ERROR.setForeground(Color.green);
                    pinState = true;
                    
                    btn_Verif.setVisible(false);
                    //btn_Cancel.setVisible(false);
                    btn_Reset.setVisible(false);
                    //lb_ATM.setVisible(false);
                    lb_ERROR.setVisible(false);
                    in_Pin.setVisible(false);
                    lb_pin.setVisible(false);
                    
                    lb_ATM.setBounds (245, 50, 200, 30);
                    lb_ATM.setText("choose function");
                    
                    btn_Debit.setBounds (180, 200, 90, 30);
                    btn_Ajout.setBounds (280, 200, 90, 30);
                    btn_ResetSolde.setBounds (380, 200, 90, 30);
                    
                    btn_Show.setBounds (200, 240, 130, 30);
                    btn_Cancel.setBounds (350, 240, 90, 30);
                    
                    
                    in_Order.setBounds (150, 110, 355, 45);
                    
                    
                }else{
                    if(nb_attempts<3){
                        if(3-nb_attempts==1){
                            lb_ERROR.setText("pin incorrect,  you still have " + (3-nb_attempts)+ " attempt");
                        }else{
                            lb_ERROR.setText("pin incorrect,  you still have " + (3-nb_attempts)+ " attempts");
                        }
                        lb_ERROR.setForeground(Color.RED);
                        nb_attempts++;
                    }else{
                        lb_ERROR.setText("card rejected");
                        lb_ERROR.setForeground(Color.RED);
                        btn_Verif.setEnabled(false);
                        btn_Reset.setEnabled(false);
                    }
                }
            }
        });

        btn_Cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            	/* Mise hors tension de la carte */
            	System.exit(0);
       		 try {
       			 cad.powerDown();
                 
       		 } catch (Exception exc) {
       			 System.out.println("Erreur lors de l'envoi de la commande Powerdown a la Javacard");
       			 return;
       		 }

            }
        });

        btn_Reset.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                in_Pin.setText("");
            }
        });

        
        
        /* Connexion - Javacard */
        
	   	try {
	   		sckCarte = new Socket("localhost", 9025);
	   		sckCarte.setTcpNoDelay(true);
		   	 BufferedInputStream input = new BufferedInputStream(sckCarte.getInputStream());
		   	 BufferedOutputStream output = new BufferedOutputStream(sckCarte.getOutputStream());
		   	 cad = new CadT1Client(input, output);
	   	} catch (Exception e) {
		   	 System.out.println("Erreur : impossible de se connecter a la Javacard");
		   	 return;
	   	}
	   	 /* Mise sous tension de la carte */
	   	try {
	   		cad.powerUp();
	   		System.out.println("Connected");
	   	} catch (Exception e) {
	   		 System.out.println("Erreur lors de l'envoi de la commande Powerup a la Javacard");
	   		 return;
	   	}
	   	
	   	
/*******************************************************************************************/			   	 
	   	 

	   		
		   	 /* Sélection de l'applet */
			 final Apdu apdu = new Apdu();
			 apdu.command[Apdu.CLA] = 0x00;
			 apdu.command[Apdu.INS] = (byte) 0xA4;
			 apdu.command[Apdu.P1] = 0x04;
			 apdu.command[Apdu.P2] = 0x00;
			 byte[] appletAID = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x00 };
			 apdu.setDataIn(appletAID);
			 cad.exchangeApdu(apdu);
			 if (apdu.getStatus() != 0x9000) {
				 System.out.println("Erreur lors de la sélection de l'applet");
				 System.exit(1);
			 }
			 
			 
			 
			
			 /* Menu principal */

/*////////////////////////////*********************///////////////////////*/
			 
			 
         	Apdu apdu1 = new Apdu();
	   			apdu1.command[Apdu.CLA] = MyClient.CLA_MONAPPLET;
	   			apdu1.command[Apdu.P1] = 0x00;
	   			apdu1.command[Apdu.P2] = 0x00;
	   			apdu1.setLe(0x7f);
	   			apdu1.command[Apdu.INS] =  MyClient.INS_INTERROGER_COMPTEUR;
	   		 try {
				cad.exchangeApdu(apdu1);
	   		} catch (Exception ex) {
				 System.out.println("exchange error");
				 lb_ERROR.setText("Erreur : status word different de 0x9000");
				 return;
			 }
			if (apdu1.getStatus() != 0x9000) {
			

				
				System.out.println("Erreur : status word different de 0x9000");
			} else {
				solde = apdu1.dataOut[0];

			}
			
			
			
/*///////////////////////*****************************/////////////////////////////			
			
			 
			 
			 
			 btn_Debit.addActionListener(new ActionListener() {

		            @SuppressWarnings("null")
					public void actionPerformed(ActionEvent e) {
		            	System.out.println("btn_Debit clicked");
		            	lb_ERROR.setVisible(true);
		                lb_ERROR.setBounds (150, 150, 300, 30);

		                byte[] data = {0};
		                //System.out.println(in_Order.getText());
		                
		                //System.out.println(data);
		                if(in_Order.getText().equals("")) {
		                	lb_ERROR.setForeground(Color.RED);
		            		lb_ERROR.setText("entrer un montant");
		                }else {
		                	data[0]=Byte.parseByte(in_Order.getText());
			            	if((int)solde>=Integer.parseInt(in_Order.getText())) {
			            		Apdu apdu = new Apdu();
					   			apdu.command[Apdu.CLA] = MyClient.CLA_MONAPPLET;
					   			apdu.command[Apdu.P1] = 0x00;
					   			apdu.command[Apdu.P2] = 0x00;
					   			apdu.setLe(0x7f);
					   			apdu.command[Apdu.INS] =  MyClient.INS_DEBITER_COMPTEUR;
					   			apdu.setDataIn(data);
					   			
						   		 try {
									cad.exchangeApdu(apdu);
						   		} catch (Exception ex) {
									 System.out.println("exchange error");
									 lb_ERROR.setText("Erreur : status word different de 0x9000");
									 return;
								 }
					             
								if (apdu.getStatus() != 0x9000) {
								
			
									lb_ERROR.setText("Erreur : status word different de 0x9000");
									System.out.println("Erreur : status word different de 0x9000");
								} else {
									lb_ERROR.setForeground(Color.GREEN);
									solde = apdu.dataOut[0];
									lb_ERROR.setText("Votre solde est : " + solde);
									System.out.println("Valeur du compteur : " + solde);
								}
			            	}else {
			            		lb_ERROR.setForeground(Color.RED);
			            		lb_ERROR.setText("solde insuffisant");
			            		
			            	}
		            	}			   			
			   			
		            }
		        });
			 
			 
			 btn_Ajout.addActionListener(new ActionListener() {

		            @SuppressWarnings("null")
					public void actionPerformed(ActionEvent e) {
		            	System.out.println("btn_Debit clicked");
		            	lb_ERROR.setVisible(true);
		            	lb_ERROR.setBounds (150, 150, 300, 30);

		                byte[] data = {0};
		                if(in_Order.getText().equals("")) {
		                	lb_ERROR.setForeground(Color.RED);
		            		lb_ERROR.setText("entrer un montant");
		                }else {
			                //System.out.println(in_Order.getText());
		                	data[0]=Byte.parseByte(in_Order.getText());

			            	if(!((int)solde+Integer.parseInt(in_Order.getText())>(int)127)) {
				                //System.out.println(data);
				            	Apdu apdu = new Apdu();
					   			apdu.command[Apdu.CLA] = MyClient.CLA_MONAPPLET;
					   			apdu.command[Apdu.P1] = 0x00;
					   			apdu.command[Apdu.P2] = 0x00;
					   			apdu.setLe(0x7f);
					   			apdu.command[Apdu.INS] =  MyClient.INS_AJOUTER_COMPTEUR;
					   			apdu.setDataIn(data);
					   			
						   		 try {
									cad.exchangeApdu(apdu);
						   		} catch (Exception ex) {
									 System.out.println("exchange error");
									 lb_ERROR.setText("Erreur : status word different de 0x9000");
									 return;
								 }
					             
								if (apdu.getStatus() != 0x9000) {
								
			
									lb_ERROR.setText("Erreur : status word different de 0x9000");
									System.out.println("Erreur : status word different de 0x9000");
								} else {
									solde = apdu.dataOut[0];
									lb_ERROR.setForeground(Color.GREEN);
									lb_ERROR.setText("Votre solde est : " + solde);
									System.out.println("Valeur du compteur : " + solde);
								}
			            	}else {
			            		lb_ERROR.setForeground(Color.RED);
			            		lb_ERROR.setText("le plafond est 127");
			            		
			            	}
						
		                }
			   			
			   			
		            }
		        });
			 
			 btn_ResetSolde.addActionListener(new ActionListener() {

		            @SuppressWarnings("null")
					public void actionPerformed(ActionEvent e) {
		            	System.out.println("btn_ResetSolde clicked");
		            	lb_ERROR.setVisible(true);


		                byte[] data = {0};
		                if(in_Order.getText().equals("")) {
		                	lb_ERROR.setForeground(Color.RED);
		            		lb_ERROR.setText("entrer un montant");
		                }else {
			                //System.out.println(in_Order.getText());
			                data[0]=Byte.parseByte(in_Order.getText());
			                //System.out.println(data);
			            	Apdu apdu = new Apdu();
				   			apdu.command[Apdu.CLA] = MyClient.CLA_MONAPPLET;
				   			apdu.command[Apdu.P1] = 0x00;
				   			apdu.command[Apdu.P2] = 0x00;
				   			apdu.setLe(0x7f);
				   			apdu.command[Apdu.INS] =  MyClient.INS_ECRASER_COMPTEUR;
				   			apdu.setDataIn(data);
				   			
					   		 try {
								cad.exchangeApdu(apdu);
					   		} catch (Exception ex) {
								 System.out.println("exchange error");
								 lb_ERROR.setText("Erreur : status word different de 0x9000");
								 return;
							 }
				             
							if (apdu.getStatus() != 0x9000) {
							
		
								lb_ERROR.setText("Erreur : status word different de 0x9000");
								System.out.println("Erreur : status word different de 0x9000");
							} else {
								solde = apdu.dataOut[0];
								lb_ERROR.setForeground(Color.GREEN);
								lb_ERROR.setText("Votre solde est : " + solde);
								System.out.println("Valeur du compteur : " + solde);
							}
					
		                }
			   			
			   			
		            }
		        });
			 
			 
			 btn_Show.addActionListener(new ActionListener() {

		            public void actionPerformed(ActionEvent e) {
		            	System.out.println("btn_Show clicked");
		            	lb_ERROR.setVisible(true);
		            	lb_ERROR.setForeground(Color.MAGENTA);
		                lb_ERROR.setBounds (150, 150, 300, 30);
		                System.out.println("test");
		            	Apdu apdu = new Apdu();
			   			apdu.command[Apdu.CLA] = MyClient.CLA_MONAPPLET;
			   			apdu.command[Apdu.P1] = 0x00;
			   			apdu.command[Apdu.P2] = 0x00;
			   			apdu.setLe(0x7f);
			   			apdu.command[Apdu.INS] =  MyClient.INS_INTERROGER_COMPTEUR;
			   		 try {
						cad.exchangeApdu(apdu);
			   		} catch (Exception ex) {
						 System.out.println("exchange error");
						 lb_ERROR.setText("Erreur : status word different de 0x9000");
						 return;
					 }
					if (apdu.getStatus() != 0x9000) {
					

						lb_ERROR.setText("Erreur : status word different de 0x9000");
						System.out.println("Erreur : status word different de 0x9000");
					} else {
						solde = apdu.dataOut[0];
						lb_ERROR.setText("Votre solde est : " + solde);
						System.out.println("Valeur du compteur : " + solde);
					}
			   			
			   			
		            }
		        });
			 
			 
			 
			 
			
			
			
			 
			 
			 
			 
	   	
		
			   	
/*******************************************************************************************/		  	
	   	
	   
		 
    }
    
    
    
	 
    
    
	@SuppressWarnings("resource")
	public static void main(String args[]) throws IOException, CadTransportException {
		// TODO Auto-generated method stub
	
        
		
		
		 
		 /*frame creation*/
		 JFrame frame = new JFrame ("ATM");
	        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	        frame.getContentPane().add (new MyClient());
	        frame.pack();
	        frame.setVisible (true);

		
		 
		 
	 }
}


