from fastapi import FastAPI, UploadFile, File, Form, Request
from fastapi.responses import StreamingResponse
import fitz  # PyMuPDF, biblioteca para manipular PDFs
from typing import Dict, Any
import json
from io import BytesIO
from datetime import datetime

app = FastAPI()

# Função para determinar o dispositivo a partir do User-Agent
def get_device_from_user_agent(user_agent: str) -> str:
    if "Mobile" in user_agent:
        return "Telefone"
    else:
        return "Desktop"

# Função para assinar o PDF
def sign_pdf(pdf_data: bytes, metadata: Dict[str, Any], device: str) -> BytesIO:
    try:
        # Carregar o PDF a partir dos bytes
        pdf_document = fitz.open(stream=pdf_data, filetype="pdf")

        # Verificar se o PDF tem páginas
        page_count = pdf_document.page_count
        if page_count == 0:
            raise ValueError("O documento PDF não contém páginas.")

        print(f"PDF contém {page_count} páginas.")

        # Processar cada página do documento
        for i in range(page_count):
            try:
                page = pdf_document.load_page(i)  # Carregar a página
                user_index = i % len(metadata["users"])
                user_info = metadata["users"][user_index]

                # Obter informações de assinatura
                current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                text = (
                    f"Assinado por: {user_info['name']} ({user_info['email']})\n"
                    f"Data/Hora: {current_time}\n"
                    f"Dispositivo: {device}"
                )

                # Adicionar a assinatura como texto na página
                page.insert_text((50, 50), text, fontsize=12, color=(0, 0, 0))
            except Exception as page_error:
                print(f"Erro ao processar a página {i}: {page_error}")
                raise page_error

        # Salvar o documento PDF modificado em memória
        output_pdf = BytesIO()
        pdf_document.save(output_pdf)
        output_pdf.seek(0)  # Voltar para o início dos dados

        return output_pdf

    except Exception as e:
        print(f"Erro ao processar o PDF: {e}")
        raise e

# Endpoint para upload e assinatura de PDF
@app.post("/sign-pdf/")
async def sign_pdf_endpoint(
    request: Request,
    file: UploadFile = File(...),
    metadata: str = Form(...)
):
    try:
        # Ler o conteúdo do arquivo PDF
        pdf_data = await file.read()

        # Verificar se o arquivo não está vazio
        if len(pdf_data) == 0:
            return {"error": "O arquivo PDF está vazio ou corrompido."}

        # Converter os metadados de string JSON para dicionário Python
        metadata_obj = json.loads(metadata)

        # Obter o User-Agent da requisição
        user_agent = request.headers.get("User-Agent", "")
        device = get_device_from_user_agent(user_agent)

        # Chamar a função de assinatura
        signed_pdf = sign_pdf(pdf_data, metadata_obj, device)

        # Retornar o PDF assinado como resposta de streaming
        return StreamingResponse(signed_pdf, media_type="application/pdf", headers={
            "Content-Disposition": f"inline; filename=signed_{file.filename}"  
        })

    except Exception as e:
        return {"error": str(e)}
