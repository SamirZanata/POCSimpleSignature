package org.samir;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Base64;

public class SignDocument {

    // Método para gerar a assinatura eletrônica
    public static String generateSignature(String userData, String documentData, PrivateKey privateKey) throws Exception {
        String dataToSign = userData + documentData + LocalDateTime.now(); // Inclui o timestamp na assinatura

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(dataToSign.getBytes(StandardCharsets.UTF_8));

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(hash);
        byte[] signedData = signature.sign();

        return Base64.getEncoder().encodeToString(signedData);
    }

    // Método para adicionar a assinatura ao PDF
    public static void signPDF(String pdfPath, String userData, String signature) throws IOException {
        File file = new File(pdfPath);

        if (!file.exists()) {
            throw new IOException("O arquivo PDF não existe: " + pdfPath);
        }

        try (PDDocument document = PDDocument.load(file)) {
            PDPage page = document.getPage(0);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 100);  // Local onde a assinatura será inserida
                contentStream.showText("Assinado por: " + userData);
                contentStream.newLineAtOffset(0, -15);  // Pula para a próxima linha
                contentStream.showText("Assinatura: " + signature);
                contentStream.endText();
            }

            // Define o caminho para salvar o arquivo assinado
            String signedFilePath = "output/signed_" + file.getName(); 
            document.save(new File(signedFilePath));
            System.out.println("Documento PDF assinado com sucesso em: " + signedFilePath);
        }
    }

    // Método principal que processa o PDF e assina
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: java SignDocument <caminho_do_pdf>");
            return;
        }

        String pdfPath = args[0]; // Recebe o caminho do PDF via argumento
        try {
            // Dados fictícios do usuário
            String userData = "Nome: Samir Zanata | Email: samir@example.com";
            String documentData = "Contrato de prestação de serviços - Documento XYZ";

            // Gera chave privada e assinatura
            KeyPair keyPair = generateKeyPair();
            savePrivateKey(keyPair.getPrivate(), "private_key.pem");

            String signature = generateSignature(userData, documentData, keyPair.getPrivate());
            System.out.println("Assinatura Eletrônica (Base64): " + signature);

            // Assina o PDF
            signPDF(pdfPath, userData, signature);

        } catch (IOException e) {
            System.err.println("Erro de I/O: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algoritmo não encontrado: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para gerar um par de chaves (privada e pública)
    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // Tamanho da chave
        return keyGen.generateKeyPair();
    }

    // Método para salvar a chave privada
    private static void savePrivateKey(PrivateKey privateKey, String filename) throws Exception {
        byte[] encoded = privateKey.getEncoded();
        String privateKeyPEM = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getEncoder().encodeToString(encoded) +
                "\n-----END PRIVATE KEY-----";

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(privateKeyPEM.getBytes(StandardCharsets.UTF_8));
        }

        System.out.println("Chave privada salva em: " + filename);
    }

    // Método para carregar uma chave pública a partir de um certificado X.509
    private static PublicKey loadPublicKey(String filename) throws Exception {
        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException("O arquivo do certificado não existe: " + filename);
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) factory.generateCertificate(fis);
            return certificate.getPublicKey();
        }
    }
}
