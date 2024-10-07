import os
import subprocess
from fastapi import FastAPI, File, UploadFile
from fastapi.responses import FileResponse
import shutil

app = FastAPI()

@app.post("/sign-pdf/")
async def sign_pdf(file: UploadFile = File(...)):
    # Salvando o arquivo temporariamente no servidor
    temp_pdf_path = f"input/{file.filename}"
    
    # Cria o diretório de entrada, se não existir
    os.makedirs("input", exist_ok=True)
    
    with open(temp_pdf_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)
    
    # Caminho para o PDF assinado
    signed_pdf_path = f"output/signed_{file.filename}"
    
    # Cria o diretório de saída, se não existir
    os.makedirs("output", exist_ok=True)
    
    try:
        # Verifique se o caminho para o executável Java está correto
        java_program = [
            "java", "-cp", "target/classes:/home/samirzanata/libs/pdfbox-app-2.0.27.jar", "org.samir.SignDocument", 
            temp_pdf_path
        ]
        
        # Executa o código Java
        result = subprocess.run(java_program, check=True, capture_output=True, text=True)
        print(result.stdout)  # Exibe a saída do processo Java
        
        # Verifique se o arquivo assinado foi gerado
        if not os.path.exists(signed_pdf_path):
            return {"error": "A assinatura falhou. O arquivo PDF assinado não foi gerado."}
    
    except subprocess.CalledProcessError as e:
        print(e.stderr)
        return {"error": "Erro ao executar o código Java."}
    
    # Devolve o PDF assinado como resposta
    return FileResponse(signed_pdf_path, filename=f"signed_{file.filename}")
