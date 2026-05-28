
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author guill
 */
public class Servidor {

    ServerSocket ss;
    int numCliente;

    private void startServer() {
        try {
            ss = new ServerSocket(5050);
            System.out.println("Servidor levantado en el puerto 5050");

            while (true) {
                Socket s = ss.accept();
                numCliente ++;
                handleCliente(s, numCliente);
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleCliente(Socket s, int numCliente) {
        Thread HiloCliente = new Thread(new Runnable() {
            Socket socket = s;
            ObjectInputStream ois;
            ObjectOutputStream oos;
            PublicKey claveCliente;

            private void levantarFlujos() {
                try {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());
                    System.out.println("Se han levantado los flujos de comunicacion con el cliente " + numCliente);
                } catch (IOException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            private void recibirClave (){
                try {
                    claveCliente = (PublicKey) ois.readObject();
                    System.out.println("Obtenida la clave del cliente " + numCliente);
                } catch (IOException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            private void recibirMensajes() {
                String mensaje = "";
                do {
                    try {
                        byte[] mensajeFirmado = (byte[]) ois.readObject();
                        mensaje = (String) ois.readObject();

                        Signature sign = Signature.getInstance("SHA1withDSA");
                        sign.initVerify(claveCliente);
                        sign.update(mensaje.getBytes());
                        boolean check = sign.verify(mensajeFirmado);

                        if (check) {
                            System.out.println("Mensaje verificado: " + mensaje + " ( " + numCliente + " )");
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvalidKeyException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SignatureException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } while (mensaje.equals("."));
            }

            @Override
            public void run() {
                levantarFlujos();
                recibirClave();
                recibirMensajes();
            }
        });

        HiloCliente.start();
    }
    
    public static void main(String[] args) {
        Servidor s = new Servidor();
        s.startServer();
    }
}
