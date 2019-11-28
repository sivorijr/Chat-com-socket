package server;

import util.Mensagem;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import util.Status;

public class Server {

    private ServerSocket serverSocket;

    private void criarServerSocket(int porta) throws IOException {
        serverSocket = new ServerSocket(porta);
    }

    private Socket esperaConexao() throws IOException {
        Socket socket = serverSocket.accept();
        return socket;
    }

    private void trataConexao(Socket socket) throws IOException, ClassNotFoundException {
        try {
            Scanner sc = new Scanner(System.in);
           
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            Mensagem m = (Mensagem) input.readObject();
            Mensagem reply = new Mensagem("REPLY");
            reply.setParam("nome", "Server");

            System.out.println("Mensagem recebida de " + m.getParam("nome") + ": " + m.getOperacao());
            if (m.getParam("nome") != null) {
                reply.setStatus(Status.OK);
                System.out.println("Escreva sua mensagem:");
                reply.setParam("mensagem", sc.nextLine());
            } else {
                reply.setStatus(Status.PARAMERROR);
            }

            output.writeObject(reply);
            output.flush();

            input.close();
            output.close();
        } catch (IOException e) {
            System.out.println("Problema no tratamento com " + socket.getInetAddress());
            System.out.println("Erro: " + e.getMessage());
        } finally {
            fechaSocket(socket);
        }
    }

    private void fechaSocket(Socket socket) throws IOException {
        socket.close();
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            System.out.println("Servidor iniciado.");
            server.criarServerSocket(5555);

            while (true) {
                Socket socket = server.esperaConexao();
                server.trataConexao(socket);
            }
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Erro no cast: " + e.getMessage());
        }
    }
}