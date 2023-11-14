package com.example.devkorproject.diet.service;
import com.example.devkorproject.diet.config.ChatGptConfig;
import com.example.devkorproject.diet.dto.*;
import com.example.devkorproject.diet.entity.DietEntity;
import com.example.devkorproject.diet.repository.DietRepository;

import lombok.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@Service
public class DietService {

    private final DietRepository dietRepository;

    @Autowired
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${apikey.chatgpt}")
    private String apiKey;
//
//    private final String apiKey = "123";
    public HttpEntity<GptReqDto> buildHttpEntity(GptReqDto chatGptRequest){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(ChatGptConfig.MEDIA_TYPE));
        httpHeaders.add(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + apiKey);
        return new HttpEntity<>(chatGptRequest, httpHeaders);
    }

    public GptResDto getResponse(HttpEntity<GptReqDto> chatGptRequestHttpEntity){

        ResponseEntity<GptResDto> responseEntity = restTemplate.postForEntity(
                ChatGptConfig.CHAT_URL,
                chatGptRequestHttpEntity,
                GptResDto.class);

        return responseEntity.getBody();
    }

    public DietResDto[] splitMessage(String message){
        DietResDto[] diets = new DietResDto[3];

        String[] tempArr = message.split("\n\n");
        for(int i = 0; i < 3; i++){
            String[] diet = tempArr[i].split("\n");
            for(int j = 0; j < diet.length; j++){
                String[] split = diet[j].split(": ");
                diet[j] = split[split.length - 1];
            }
            DietResDto dietResDto = new DietResDto(diet[0],diet[1],diet[2],diet[3],diet[4], diet[5]);
            diets[i] = dietResDto;
        }

        return diets;
    }
    public  DietResDto[] askQuestion(DietReqDto dietRequestDto) {

        List<ChatGptMessage> messages = new ArrayList<>();
        String fridge = dietRequestDto.getFridge();
        String keyword = dietRequestDto.getKeyword();
        String type = dietRequestDto.getType();
        String question =  fridge +
                "를 활용한 " +
                keyword +
                " " +
                type +
                "의 (메뉴명:),(설명:),(재료:),(레시피:),(소요시간(분):),(난이도:)를 한 줄씩 알려줘. " +
                "재료는 그람수 단위로 자세하게 한 줄로 알려주고, " +
                "레시피는 5단계로 자세하게 한 줄로 설명하고, " +
                "시간은 단위 떼고 숫자만 알려줘. " +
                "난이도는 간단/보통/복잡 중 하나로 선택해줘. " +
                "똑같은 방법으로 세 가지 메뉴 알려줘. " +
                "레시피 설명할 때 줄바꿈 없어야 돼.";

        messages.add(ChatGptMessage.builder()
                .role(ChatGptConfig.ROLE)
                .content(question)
                .build());

        String message =  this.getResponse(
                this.buildHttpEntity(
                        new GptReqDto(
                                ChatGptConfig.CHAT_MODEL,
                                ChatGptConfig.MAX_TOKEN,
                                ChatGptConfig.TEMPERATURE,
                                ChatGptConfig.STREAM,
                                messages
                        )
                )
        ).getChoices().get(0).getMessage().getContent();

        DietResDto[] diets = splitMessage(message);

        //현재 날짜 구하기
        LocalDate now = LocalDate.now();

        String imageUrl = "image";
        for(int i = 0; i < 3; i++) {
            DietEntity dietEntity = DietEntity.builder()
                    .dietName(diets[i].getDietName())
                    .description(diets[i].getDescription())
                    .ingredients(diets[i].getIngredients())
                    .recipe(diets[i].getRecipe())
                    .difficulty(diets[i].getDifficulty())
                    .time(Long.parseLong(diets[i].getTime()))
                    .info(type) //간식 or 식사
                    .available(fridge) //냉장고 재료
                    .imageUrl(imageUrl)
//                .allergy()
//                .needs()
                    .keyword(keyword) //#빨리 먹을 수 있는
                    .date(now)
                    .build();
            dietRepository.save(dietEntity);
        }
        return diets;
    }
}
