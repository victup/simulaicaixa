package br.gov.caixa.simulaicaixa.util.dev;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class GeradorChavesJwt {

    public static void main(String[] args) throws Exception {
        KeyPairGenerator gerador = KeyPairGenerator.getInstance("RSA");
        gerador.initialize(2048);
        KeyPair par = gerador.generateKeyPair();

        salvarPem("privateKey.pem", "PRIVATE KEY", par.getPrivate().getEncoded());
        salvarPem("publicKey.pem", "PUBLIC KEY", par.getPublic().getEncoded());
    }

    private static void salvarPem(String nomeArquivo, String tipo, byte[] conteudo) throws Exception {
        String base64 = Base64.getEncoder().encodeToString(conteudo);
        StringBuilder sb = new StringBuilder();
        sb.append("-----Inicio ").append(tipo).append("-----\n");
        for (int i = 0; i < base64.length(); i += 64) {
            sb.append(base64, i, Math.min(i + 64, base64.length())).append("\n");
        }
        sb.append("-----Fim ").append(tipo).append("-----\n");
        Files.writeString(Path.of(nomeArquivo), sb.toString());
    }
}