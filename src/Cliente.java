
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author guill
 */
public class Cliente {

    Socket s;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    PrivateKey clavepriv;
    PublicKey clavepub;
    Scanner sc = new Scanner (System.in);

    private void startCliente() {
        try {
            s = new Socket("localhost", 5050);
            System.out.println("Se ha conectado con el servidor");
            ejecutarConexion();
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void levantarFlujos() {
        try {
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
            System.out.println("Se han levantado los flujos de comunicacion con el servidor");
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enviarClave() {
        generarClaves();
        try {
            oos.writeObject(clavepub);
            oos.flush();
            System.out.println("Clave enviada con exito");
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void generarClaves() {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("DSA");
            SecureRandom numero = SecureRandom.getInstance("SHA1PRNG");
            kpg.initialize(1024, numero);

            KeyPair kp = kpg.genKeyPair();
            clavepriv = kp.getPrivate();
            clavepub = kp.getPublic();
            
            System.out.println("Claves generadas con exito");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enviarMensajes() {
        String mensaje = "";

        do {
            System.out.println("Introduzca el mensaje: ");
            mensaje = sc.nextLine();
            
            try {
                Signature sign = Signature.getInstance("SHA1withDSA");
                sign.initSign(clavepriv);
                sign.update(mensaje.getBytes());
                byte [] mensajeFirmado = sign.sign();
                
                oos.writeObject(mensajeFirmado);
                oos.writeObject(mensaje);
                oos.flush();
                System.out.println("Mensaje y firma enviados con exito");
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SignatureException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (!mensaje.equals("."));
        cerrarConexion();
    }

    private void cerrarConexion() {
        try {
            oos.close();
            ois.close();
            s.close();
            System.out.println("Se ha cerrado la conexion con exito");
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void ejecutarConexion() {
        levantarFlujos();
        enviarClave();
        enviarMensajes();
    }
    
    public static void main(String[] args) {
        Cliente c = new Cliente();
        c.startCliente();
    }
}
