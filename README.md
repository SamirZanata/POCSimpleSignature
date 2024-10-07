# Sistema de Assinatura Eletrônica

Este projeto é uma implementação de um sistema básico de assinatura eletrônica de documentos PDF, utilizando a biblioteca Apache PDFBox para manipulação de PDFs e algoritmos de criptografia RSA para gerar e validar assinaturas digitais. A aplicação foi desenvolvida em Java.

## Funcionalidades

- Gerar um par de chaves (pública e privada).
- Assinar documentos digitalmente.
- Salvar PDFs assinados.

## Requisitos

- Java Development Kit (JDK) 8 ou superior.
- Apache Maven para gerenciar as dependências.
- Biblioteca Apache PDFBox.

## Instalação

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/SamirZanata/POCSimpleSignature.git
   cd POCSimpleSignature


   #Compile o projeto com o comando
   
   mvn clean install

   #Execute o codigo com o comando:
   java -cp target/SignatureApp-1.0-SNAPSHOT-jar-with-dependencies.jar org.samir.SignDocument

   Contribuição
Sinta-se à vontade para contribuir com este projeto. Abra uma issue ou um pull request para melhorias e correções.

Licença
Este projeto está licenciado sob a MIT License.

Contato
Samir Zanata
Email: samirzanata@icloud.com
