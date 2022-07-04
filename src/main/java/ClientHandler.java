import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;

public class ClientHandler extends Thread {
    final Socket s;
    final InputStream i;
    final OutputStream o;

    public ClientHandler(Socket s, InputStream i, OutputStream o) {
        this.s = s;
        this.i = i;
        this.o = o;
    }

    @Override
    public void run() {
        try {
            Processador processador;
            do {
                byte[] line = new byte[30000];
                i.read(line);

                processador = new Processador(line).processarDados();

                // Enviando para o cliente os dados processado pelo protocolo
                System.out.print(processador.statusCode() + " ");
                if (processador.statusCode() == 200) {

                    // Limpando o buffer
                    String buffer = "";
                    for (int j = 0; j < 60; j++) buffer += " ";

                    System.out.println("Dados processado com sucesso!");
                    o.write((processador.json().trim()).getBytes(StandardCharsets.UTF_8));
                } else if (processador.statusCode() == 404) {
                    System.out.println("Operação solicitada não pode ser processada!");
                    o.write(("{\"erro\":\"" + processador.getDados() + " não encontrado!\"").replace("\n", "").getBytes(StandardCharsets.UTF_8));
                } else if (processador.statusCode() == 100) {
                    o.write(String.valueOf(processador.statusCode()).getBytes(StandardCharsets.UTF_8));
                } else if (processador.statusCode() == 101) {
                    System.out.println("Máquina desligada pelo dispositivo " + s.getRemoteSocketAddress());
                    o.write((processador.getResponse()).getBytes(StandardCharsets.UTF_8));
                } else {
                    o.write("Erro de servidor! :(".getBytes(StandardCharsets.UTF_8));
                }

            } while (processador == null || processador.statusCode() != 100 && processador.statusCode() != 101);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
