import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static spark.Spark.*;

public class Main {

    public static void main(String [] args) {
        port(8080);
        String manual = "<pre>" +
                "Manual de Utilizacao, para utilizar segue abaixo exemplos de como utilizar cada uma das funcoes " +
                "por meio dos caminhos\n\n" +
                "Multiplicacao: /mult/2*2\n" +
                "Soma:/soma/2+20\n" +
                "Subtracao: /sub/23-3\n" +
                "divisao: /div/4/2\n" +
                "potencia: /pot/4/2" +
                "</pre>";
        get("/", (request, response) -> manual);

        get("/soma/*", (request, response) -> {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            return Conectar("soma " + request.splat()[0]);
        });
        get("/sub/*", (request, response) ->  {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            return Conectar("sub " + request.splat()[0]);
        });
        get("/div/*", (request, response) -> {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            return Conectar("div " + request.splat()[0]);
        });

        get("/mult/*", (request, response) -> {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            return Conectar("mult " + request.splat()[0]);
        });

        get("/pot/:num1/:num2", (request, response) -> {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            String num1 = request.params("num1");
            String num2 = request.params("num2");

            return Conectar("pot " + num1 + " ^ " + num2 );
        });

        get("/raiz/:num", (request, response) -> {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            String num = request.params("num");

            return Conectar("raizQ " + num);
        });

        get("/porc/:num1/:num2", (request, response) -> {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            String num1 = request.params("num1");
            String num2 = request.params("num2");

            return Conectar("porc " + num1 + " % " + num2 );
        });
    }

    public static String Conectar(String expresao) {
        System.out.println("Executando ...");
        Socket s = null;
        InputStream i = null;
        OutputStream o = null;
        String str = "";

        try {
            do {
                for(int pos = expresao.length(); pos < 30000; pos++) {
                    expresao += " ";
                }

                byte[] line = expresao.getBytes(StandardCharsets.UTF_8);
                Processador processadorClient = new Processador(line);
                processadorClient.processarDados();

                s = new Socket("127.0.0.1", processadorClient.Id() >= 5 && processadorClient.Id() <= 7 ? 9998 : 9999);
                i  = s.getInputStream();
                o = s.getOutputStream();

                o.write(line);
                i.read(line);
                str = new String(line);

                if (!str.trim().equals("100"))
                    return str.trim();
                else
                    return "FINALIZANDO";

            } while (!str.trim().equals("100")) ;

        }
        catch (ConnectException e) {
            return "{\"erro\": \"Por favor, ligue os servidores\"}";
        }
        catch (Exception err) {
            err.printStackTrace();
            return err.getMessage();
        }
    }
}
