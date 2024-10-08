public static void main(String[] args) {
    if (args.length < 2) {
        System.err.println("Uso: java SignDocument <caminho_do_pdf> <user_data>");
        return;
    }

    String pdfPath = args[0]; // Recebe o caminho do PDF via argumento
    String userDataInput = args[1]; // Recebe os dados dos usuários via argumento

    try {
        // Divide os dados do usuário
        String[] usersData = userDataInput.split("\\|"); // Divide os dados pelo delimitador '|'
        for (String userData : usersData) {
            // Divida o nome e email usando um delimitador específico (por exemplo, ",")
            String[] parts = userData.split(",");
            String name = parts[0]; // Nome do usuário
            String email = parts.length > 1 ? parts[1] : "Email não fornecido"; // Email do usuário

            // Gera chave privada e assinatura
            KeyPair keyPair = generateKeyPair();
            savePrivateKey(keyPair.getPrivate(), "private_key.pem");

            String documentData = "Contrato de prestação de serviços - Documento XYZ";
            String signature = generateSignature(name + " | " + email, documentData, keyPair.getPrivate());
            System.out.println("Assinatura Eletrônica (Base64): " + signature);

            // Assina o PDF
            signPDF(pdfPath, name + " | " + email, signature);
        }

    } catch (IOException e) {
        System.err.println("Erro de I/O: " + e.getMessage());
    } catch (NoSuchAlgorithmException e) {
        System.err.println("Algoritmo não encontrado: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
    }
}
