package com.example.devkorproject.diet.service;
import com.example.devkorproject.baby.entity.BabyEntity;
import com.example.devkorproject.baby.exception.BabyDoesNotExistException;
import com.example.devkorproject.baby.repository.BabyRepository;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.diet.entity.DietEntity;
import com.example.devkorproject.customer.exception.CustomerDoesNotExistException;
import com.example.devkorproject.customer.repository.CustomerRepository;
import com.example.devkorproject.diet.config.ChatGptConfig;
import com.example.devkorproject.diet.dto.*;
import com.example.devkorproject.diet.repository.DietRepository;

import com.example.devkorproject.post.exception.CustomerDoesNotMatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DietService {

    private final DietRepository dietRepository;
    private final BabyRepository babyRepository;
    private final CustomerRepository customerRepository;
    public DietService(DietRepository dietRepository, BabyRepository babyRepository, CustomerRepository customerRepository) {
        this.dietRepository = dietRepository;
        this.babyRepository = babyRepository;
        this.customerRepository =  customerRepository;
    }

    @Autowired
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${apikey.chatgpt}")
    private String apiKey;

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
    public SimpleResDto[] splitMessage(String message){
        SimpleResDto[] simples = new SimpleResDto[3];

        String[] tempArr = message.split("\n\n");
        for(int i = 0; i < 3; i++){
            String[] simple = tempArr[i].split("\n");
            for(int j = 0; j < simple.length; j++){
                String[] split = simple[j].split(": ");
                simple[j] = split[split.length - 1];
            }
            SimpleResDto simpleResDto = new SimpleResDto(simple[0],simple[1],simple[2],simple[3]);
            simples[i] = simpleResDto;
        }

        return simples;
    }
    public  SimpleResDto[] askQuestion(Long customerId, Long babyId, SimpleReqDto simpleRequestDto) {

        Optional<CustomerEntity> opCustomerEntity = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomerEntity.isEmpty())
            throw new CustomerDoesNotExistException();
        CustomerEntity customerEntity = opCustomerEntity.get();

        Optional<BabyEntity> opBabyEntity = babyRepository.findBabyEntityByBabyId(babyId);
        if(opBabyEntity.isEmpty())
            throw new BabyDoesNotExistException();
        BabyEntity babyEntity = opBabyEntity.get();

        if(customerEntity.getCustomerId() != customerId){
            throw new CustomerDoesNotMatchException();
        }

        List<ChatGptMessage> messages = new ArrayList<>();
        String fridge = simpleRequestDto.getFridge();
        String keyword = simpleRequestDto.getKeyword();
        String type = simpleRequestDto.getType();
        String fridgeMessage = "";
        String keywordMessage = "";
        String allergyMessage = "";

        if(fridge != null){
            fridgeMessage = fridge + "를 활용한";
        }
        if(keyword != null){
            keywordMessage = keyword + " ";
        }
        if(babyEntity.getAllergy() != null){
            allergyMessage = babyEntity.getAllergy() + "환자도 먹을 수 있는";
        }

        String question =  fridgeMessage +
                keywordMessage +
                allergyMessage +
                type +
                "의 (메뉴명: 숫자없이),(간단한 소개:),(소요시간(분):),(난이도:)를 한 줄씩 알려줘. " +
                "시간은 단위 떼고 숫자만 알려줘. " +
                "난이도는 간단/보통/복잡 중 하나로 선택해줘. " +
                "똑같은 방법으로 세 가지 메뉴 알려줘.";

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

        SimpleResDto[] simples = splitMessage(message);


        return simples;
    }

    public DietResDto getDetailDiet(Long customerId, Long babyId, DietReqDto dietReqDto){

        Optional<CustomerEntity> opCustomerEntity = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomerEntity.isEmpty())
            throw new CustomerDoesNotExistException();
        CustomerEntity customerEntity = opCustomerEntity.get();

        Optional<BabyEntity> opBabyEntity = babyRepository.findBabyEntityByBabyId(babyId);
        if(opBabyEntity.isEmpty())
            throw new BabyDoesNotExistException();
        BabyEntity babyEntity = opBabyEntity.get();

        if(customerEntity.getCustomerId() != customerId){
            throw new CustomerDoesNotMatchException();
        }

        List<ChatGptMessage> messages = new ArrayList<>();
        String question = dietReqDto.getDietName() + "의 재료와 레시피를 (재료:) 한 줄, (레시피:) 한 줄씩 구분해서 알려줘. " +
                "재료는 그람수 단위로 자세하게 한줄로 알려주고, " +
                "레시피는 단계별로 나눠서 자세하게 한줄로 알려줘.";

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

        String[] tempArr = message.split("\n\n");
        String ingredients = tempArr[0].split(": ")[1];
        String recipe = tempArr[1].split(":\n")[1];

//        현재 날짜 구하기
        LocalDateTime now = LocalDateTime.now();
        String imageUrl = "image";

        DietEntity dietEntity = DietEntity.builder()
                .dietName(dietReqDto.getDietName())
                .description(dietReqDto.getDescription())
                .ingredients(ingredients)
                .recipe(recipe)
                .difficulty(dietReqDto.getDifficulty())
                .time(Long.parseLong(dietReqDto.getTime()))
                .info(dietReqDto.getType()) //간식 or 식사
                .available(dietReqDto.getFridge()) //냉장고 재료
                .allergy(babyEntity.getAllergy())
                .needs(babyEntity.getNeeds())
                .keyword(dietReqDto.getKeyword())//#빨리 먹을 수 있는
                .imageUrl(imageUrl)
                .date(now)
                .customer(customerEntity)
                .baby(babyEntity)
                .build();
        dietRepository.save(dietEntity);

        DietResDto dietResDto = new DietResDto(dietReqDto.getDietName(), dietReqDto.getDescription(),
                ingredients, recipe, dietReqDto.getTime(), dietReqDto.getDifficulty());

        return dietResDto;
    }
}
