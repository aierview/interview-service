package com.aierview.backend.shared.utils;

import com.aierview.backend.interview.domain.exceptions.UnavailableIAServiceException;
import com.aierview.backend.interview.domain.model.BeginInterviewRequest;
import com.aierview.backend.interview.domain.model.GenerateFeedbackRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GeminiFunctUtils {

    private final RestTemplate restTemplate;
    @Value("${gemini.api-key}")
    private String apiKey;
    @Value("${gemini.api-url}")
    private String apiUrl;

    public String generateQuestionsPrompt(BeginInterviewRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("📋 Contexto:\n");
        prompt.append("Você está atuando como entrevistador e irá conduzir uma entrevista técnica com um desenvolvedor ");
        prompt.append(request.getRole() + " de nível " + request.getInterviewLevel() + " em " + request.getStack() + ".\n\n");
        prompt.append("🎯 Objetivo:\n");
        prompt.append("Gerar 3 perguntas técnicas para que o entrevistado possa responder com base em seus conhecimentos e experiência.\n\n");
        prompt.append("📝 Instrução:\n");
        prompt.append("Liste as perguntas no seguinte formato:\n\n");
        prompt.append("pergunta\n");
        prompt.append("Uma pergunta por linha.\n");
        prompt.append("Adicione ## ao final de cada pergunta para indicar o término da pergunta.\n");
        return prompt.toString();
    }

    public String generateFeedbackPrompt(GenerateFeedbackRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("📋 Contexto:\n");
        prompt.append("Você está atuando como entrevistador técnico e irá avaliar a resposta de um desenvolvedor na seguinte entrevista. ");
        prompt.append("O candidato possui o papel de ").append(request.role())
                .append(", com nível ").append(request.level())
                .append(", e está sendo avaliado na stack ").append(request.stack()).append(".\n\n");

        prompt.append("🎯 Objetivo:\n");
        prompt.append("Sua tarefa é avaliar a qualidade da resposta fornecida pelo candidato com base na clareza, precisão técnica, completude e comunicação. ");
        prompt.append("Além disso, forneça um feedback construtivo que possa ajudá-lo a melhorar.\n\n");

        prompt.append("📝 Instruções:\n");
        prompt.append("1. Retorne o feedback utilizando o seguinte formato:\n");
        prompt.append("Seu feedback aqui##\n\n");
        prompt.append("2. Atribua uma nota de 0 a 10 com com base na resposta, considerando critérios técnicos e comunicativos:\n");
        prompt.append("a nota aqui##\n\n");
        prompt.append("Adicione ## ao final do feedback para indicar o término o feedback.\n");
        prompt.append("Adicione ## ao final do score para indicar o término da nota, nunca esqueça de atribuir uma nota.\n");
        prompt.append("Ignore erros ortográficos ou de digitação ao atribuir o score. ");
        prompt.append("Erros de português não devem ser apontados no feedback e não devem influenciar negativamente ");
        prompt.append("no score do candidato caso o conteúdo técnico esteja correto.\n\n");

        prompt.append("🧠 Dados da entrevista:\n");
        prompt.append("Pergunta:\n").append(request.question()).append("\n\n");
        prompt.append("Resposta:\n").append(request.answerText()).append("\n");
        return prompt.toString();
    }

    public List<String> getResponse(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt.toString());
        Map<String, Object> content = new HashMap<>();
        content.put("parts", new Object[]{part});
        requestBody.put("contents", new Object[]{content});
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        URI uri = UriComponentsBuilder.fromHttpUrl(this.apiUrl).queryParam("key", this.apiKey).build().toUri();
        Map<String, Object> response = restTemplate.postForObject(uri, requestEntity, Map.class);
        if (response == null || !response.containsKey("candidates"))
            throw new UnavailableIAServiceException();

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates.isEmpty()) throw new UnavailableIAServiceException();

        content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        if (parts == null || parts.isEmpty()) throw new UnavailableIAServiceException();
        List<String> responseText = Arrays.stream(((String) parts.get(0).get("text")).split("##"))
                .map(String::trim)
                .collect(Collectors.toList());
        return responseText;
    }
}
