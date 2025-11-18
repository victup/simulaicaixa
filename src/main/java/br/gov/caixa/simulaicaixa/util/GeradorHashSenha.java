package br.gov.caixa.simulaicaixa.util;
import org.mindrot.jbcrypt.BCrypt;

public class GeradorHashSenha {

    public static void main(String[] args) {
        if (args.length == 0) {
            gerarHashPadroes();
        } else {
            gerarHashArgumentos(args);
        }
    }

    private static void gerarHashPadroes() {
        String[] senhas = {
                "senha123",
                "senha000"
        };

        System.out.println("Gerando hashes padrão:");
        for (String senha : senhas) {
            String hash = BCrypt.hashpw(senha, BCrypt.gensalt(10));
            System.out.println("Senha: " + senha);
            System.out.println("Hash : " + hash);
            System.out.println();
        }
    }

    private static void gerarHashArgumentos(String[] senhas) {
        System.out.println("Gerando hashes para senhas passadas como argumento:");
        for (String senha : senhas) {
            String hash = BCrypt.hashpw(senha, BCrypt.gensalt(10));
            System.out.println("Senha: " + senha);
            System.out.println("Hash : " + hash);
            System.out.println();
        }
    }
}