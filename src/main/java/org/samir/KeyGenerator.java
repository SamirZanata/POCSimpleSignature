package org.samir;

import java.io.FileWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;


public class KeyGenerator {


    public static void generateKeyPair(String privateKeyPath, String publicKeyPath) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // Tamanho da chave


        KeyPair pair = keyGen.generateKeyPair();


        try (FileWriter privateKeyWriter = new FileWriter(privateKeyPath)) {
            privateKeyWriter.write("-----BEGIN PRIVATE KEY-----\n");
            privateKeyWriter.write(Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()).replaceAll("(.{64})", "$1\n"));
            privateKeyWriter.write("-----END PRIVATE KEY-----\n");
        }


        try (FileWriter publicKeyWriter = new FileWriter(publicKeyPath)) {
            publicKeyWriter.write("-----BEGIN PUBLIC KEY-----\n");
            publicKeyWriter.write(Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()).replaceAll("(.{64})", "$1\n"));
            publicKeyWriter.write("-----END PUBLIC KEY-----\n");
        }

        System.out.println("Par de chaves gerado e salvo com sucesso.");
    }
}
