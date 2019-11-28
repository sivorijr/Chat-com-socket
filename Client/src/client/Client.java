package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import util.Mensagem;
import util.Status;

public class Client {

    public static void main(String[] args) {
        System.out.print("Digite seu nome: ");
        Scanner sc = new Scanner(System.in);
        String nome = sc.nextLine();
        System.out.println("Estabelecendo conexão...");
        System.out.println("Conexão estabelecida.");

        while (!nome.isEmpty()) {
            try {
                Socket socket = new Socket("localhost", 5555);

                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                String msg = sc.nextLine();
                
                if (msg.equals("exit")) {
                    break;
                }
                
                Mensagem m = new Mensagem(msg);
                m.setStatus(Status.SOLICITACAO);
                m.setParam("nome", nome);

                output.writeObject(m);
                output.flush();

                System.out.println(nome + ": " + m.getOperacao());

                m = (Mensagem) input.readObject();

                if (m.getStatus() == Status.OK) {
                    System.out.println(m.getParam("nome") + ": " + m.getParam("mensagem"));
                } else {
                    System.out.println("Erro: " + m.getStatus());
                }

                input.close();
                output.close();
                socket.close();
            } catch (IOException ex) {
                System.out.println("Erro no cliente: " + ex.getMessage());
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                System.out.println("Erro no cast: " + ex.getMessage());
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
