import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Processador {
    private int id_action;
    private String dados;
    private String response;
    private int status;
    private String operatorName;
    private String result;

    public Processador(String dados) {
        this.setDados(dados);
    }

    public Processador(byte[] dados) {
        this.setDados(dados);
    }

    public String getDados() {
        return this.dados;
    }

    public Processador processarDados() {

        if(Pattern.matches("SOMA[0-9\\s\\n\\+?]+", this.getDados())) this.id_action = 1;
        else if(Pattern.matches("MULT[0-9\\s\\n\\*]+", this.getDados())) this.id_action = 2;
        else if(Pattern.matches("SUB[0-9\\s\\n\\-]+", this.getDados())) this.id_action = 3;
        else if(Pattern.matches("DIV[0-9\\s\\n\\/]+", this.getDados())) this.id_action = 4;
        else if(Pattern.matches("(PORC|porc)\\s*[0-9]+\\s*\\%\\s*[0-9]+\n?", this.getDados())) this.id_action = 5;
        else if(Pattern.matches("(POT|pot)(\\s*[0-9]+\\s*\\^\\s*[0-9]+)(\n?)", this.getDados())) this.id_action = 6;
        else if(Pattern.matches("(RAIZQ|raizQ)(\\s*[0-9]+)(\n?)", this.getDados())) this.id_action = 7;

        this.realizarProcesso();
        return this;
    }

    private void realizarProcesso() {

        this.status = 200;
        switch(this.id_action) {
            case 1:
                this.setResponse(this.somar());
                break;
            case 2:
                this.setResponse(this.multiplicar());
                break;
            case 3:
                this.setResponse(this.subtrair());
                break;
            case 4:
                this.setResponse(this.divisao());
                break;
            case 5:
                this.setResponse(this.porcentagem());
                break;
            case 6:
                this.setResponse(this.potencia());
                break;
            case 7:
                this.setResponse(this.raizQuadrada());
                break;
            case 8:
                this.setResponse(this.getData());
                break;
            default:
                this.status = 404;
        }
    }

    public String mostrarManual() {
        String manual = "";
        try {
            File file = new File(new File("").getCanonicalFile() + "\\README.md");
            Scanner sc = new Scanner(file);

            String st;
            while(sc.hasNextLine()) {
                manual += sc.nextLine() + "\n";
            }
        } catch (FileNotFoundException e) {
            manual = "<<< ERRO ao tentar carregar o arquivo. Arquivo nao encontrado!";
            System.err.println(manual);
        } catch (IOException e) {
            manual = e.getMessage();
            e.printStackTrace();
        }

        return manual;
    }

    public int statusCode() {
        return this.status;
    }

    private String somar(){
        this.result =  operacao('+');
        this.operatorName = "soma";
        return "SOMA = " + this.result;
    }

    private String multiplicar() {
        this.result =  operacao('*');
        this.operatorName = "multiplicacao";
        return "MULT = " + operacao('*');
    }

    private String subtrair() {
        this.result =  operacao('-');
        this.operatorName = "subtracao";
        return "SUB = " + operacao('-');
    }

    private String divisao() {
        this.result =  operacao('/');
        this.operatorName = "divisao";
        return "DIV = " + operacao('/');
    }

    public String porcentagem() {
        this.result =  operacao('%');
        this.operatorName = "porcentagem";
        return "PORC = " + operacao('%');
    }

    public String potencia() {
        this.result =  operacao('^');
        this.operatorName = "potencia";
        return "POTENCIA = " + operacao('^');
    }

    public String raizQuadrada() {
        this.result =  operacao('r');
        this.operatorName = "raiz quadrada";
        return "RAIZ = " + operacao('r');
    }

    private String operacao(char operacao) {
        Pattern patternNum = Pattern.compile("[0-9]+");
        Matcher m = patternNum.matcher(this.getDados().toUpperCase(Locale.ROOT));

        double result = 0;
        int num_capturado = 0;
        while(m.find()) {
            num_capturado = Integer.parseInt(m.group());
            result = operacao == '+' ? result + num_capturado :
                     operacao == '*' ? (result == 0 ? 1 : result) * num_capturado :
                     operacao == '/' ? (result == 0 ? num_capturado : result / num_capturado) :
                     operacao == '-' ? (result == 0 ? num_capturado : result - num_capturado) :
                     operacao == '%' ? (result == 0 ? num_capturado : result * num_capturado / 100) :
                     operacao == '^' ? (result == 0 ? num_capturado : Math.pow(result, num_capturado)) :
                     operacao == 'r' ?  Math.sqrt(num_capturado) : 0;
        }

        return String.valueOf(result);
    }

    public void setDados(String dados) {
        this.dados = dados.toUpperCase(Locale.ROOT);
    }

    private void setResponse(String response) {
        this.response += response;
    }

    public String getResponse() {
        return this.response;
    }

    public void setDados(byte [] dados) {

        int i = 0;
        for(i = 0; i < dados.length && dados[i] != 0; i++);

        this.setDados((new String(dados, StandardCharsets.UTF_8).trim()));
    }

    private String getData() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime agora = LocalDateTime.now();
        return dtf.format(agora);
    }

    public int Id() {
        return this.id_action;
    }

    public String json() {
        return "{\"" + this.operatorName + "\":" +
                "\"" + this.result  + "\"}";
    }
}
