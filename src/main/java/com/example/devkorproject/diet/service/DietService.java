package com.example.devkorproject.diet.service;
import com.example.devkorproject.baby.entity.BabyEntity;
import com.example.devkorproject.baby.exception.BabyDoesNotExistException;
import com.example.devkorproject.baby.repository.BabyRepository;
import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.diet.entity.DietEntity;
import com.example.devkorproject.customer.exception.CustomerDoesNotExistException;
import com.example.devkorproject.customer.repository.CustomerRepository;
import com.example.devkorproject.diet.config.ChatGptConfig;
import com.example.devkorproject.diet.dto.*;
import com.example.devkorproject.diet.entity.SimpleDietEntity;
import com.example.devkorproject.diet.exception.DietDoesNotExistException;
import com.example.devkorproject.diet.exception.SimpleDietDoesNotExistException;
import com.example.devkorproject.diet.repository.DietRepository;
import com.example.devkorproject.diet.repository.SimpleDietRepository;
import com.example.devkorproject.fridge.entity.FridgeEntity;
import com.example.devkorproject.fridge.repository.FridgeRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DietService {

    private final DietRepository dietRepository;
    private final BabyRepository babyRepository;
    private final CustomerRepository customerRepository;
    private final FridgeRepository fridgeRepository;
    private final SimpleDietRepository simpleDietRepository;
    public DietService(DietRepository dietRepository,
                       BabyRepository babyRepository,
                       CustomerRepository customerRepository,
                       FridgeRepository fridgeRepository,
                       SimpleDietRepository simpleDietRepository) {
        this.dietRepository = dietRepository;
        this.babyRepository = babyRepository;
        this.customerRepository =  customerRepository;
        this.fridgeRepository = fridgeRepository;
        this.simpleDietRepository = simpleDietRepository;
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

        SimpleResDto[] simples = new SimpleResDto[3];

        String[] tempArr = message.split("\n\n");
        for(int i = 0; i < 3; i++){
            String[] simple = tempArr[i].split("\n");
            for(int j = 0; j < simple.length; j++){
                String[] split = simple[j].split(": ");
                simple[j] = split[split.length - 1];
            }

            SimpleDietEntity simpleDietEntity = SimpleDietEntity.builder()
                    .dietName(simple[0])
                    .description(simple[1])
                    .time(Long.parseLong(simple[2]))
                    .difficulty(simple[3])
                    .customer(customerEntity)
                    .baby(babyEntity)
                    .build();

            simpleDietRepository.save(simpleDietEntity);

            SimpleResDto simpleResDto = new SimpleResDto(
                    simpleDietEntity.getSimpleDietId(),
                    simpleDietEntity.getDietName(),
                    simpleDietEntity.getDescription(),
                    simpleDietEntity.getTime(),
                    simpleDietEntity.getDifficulty(),
                    simpleDietEntity.isHeart()
            );

            simples[i] = simpleResDto;
        }


        return simples;
    }

    public List<FridgeSimpleDietResDto> getFridgeSimpleDiet(Long customerId, Long babyId, String type){
        Optional<CustomerEntity> opCustomerEntity = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomerEntity.isEmpty())
            throw new CustomerDoesNotExistException();
        CustomerEntity customerEntity = opCustomerEntity.get();

        Optional<BabyEntity> opBabyEntity = babyRepository.findBabyEntityByBabyId(babyId);
        if(opBabyEntity.isEmpty())
            throw new BabyDoesNotExistException();
        BabyEntity babyEntity = opBabyEntity.get();

        if(customerEntity.getCustomerId() != customerId) {
            throw new CustomerDoesNotMatchException();
        }
        String allergyMessage = "";
        if(babyEntity.getAllergy() != null){
            allergyMessage = babyEntity.getAllergy() + "환자도 먹을 수 있는 ";
        }

        List<String> ingredientsList = fridgeRepository.findActiveIngredientsByCustomerId(customerId);
        if(ingredientsList.isEmpty()){
            return Collections.emptyList();
        }
        String ingredients = ingredientsList.stream().collect(Collectors.joining(","));

        List<ChatGptMessage> messages = new ArrayList<>();
        String question = ingredients + "를 활용한 " +
                allergyMessage +
                type +
                "의 (메뉴명: 숫자없이),(간단한 소개:),(소요시간(분):),(난이도:)를 한 줄씩 알려줘. " +
                "시간은 단위 떼고 숫자만 알려줘. " +
                "난이도는 간단/보통/복잡 중 하나로 선택해줘. " +
                "똑같은 방법으로 두 가지 메뉴 알려줘.";

//        String question = ingredients +
//                "가 활용되는 음식을 (메뉴명:),(소요시간(분):),(난이도:) 형태로 한 줄씩 알려줘. " +
//                "시간은 단위 떼고 숫자만 알려줘. " +
//                "난이도는 간단/보통/복잡 중 하나로 선택해줘. " +
//                "똑같은 방법으로 두 가지 메뉴 알려줘.";

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

        FridgeSimpleDietResDto[] simples = new FridgeSimpleDietResDto[2];

        String[] tempArr = message.split("\n\n");
        for(int i = 0; i < 2; i++){
            String[] simple = tempArr[i].split("\n");
            for(int j = 0; j < simple.length; j++){
                String[] split = simple[j].split(": ");
                simple[j] = split[split.length - 1];
            }

            SimpleDietEntity simpleDietEntity = SimpleDietEntity.builder()
                    .dietName(simple[0])
                    .description(simple[1])
                    .time(Long.parseLong(simple[2]))
                    .difficulty(simple[3])
                    .customer(customerEntity)
                    .baby(babyEntity)
                    .build();

            simpleDietRepository.save(simpleDietEntity);

            FridgeSimpleDietResDto fridgeSimpleDietResDto = new FridgeSimpleDietResDto(
                    simpleDietEntity.getSimpleDietId(),
                    simpleDietEntity.getDietName(),
                    simpleDietEntity.getTime(),
                    simpleDietEntity.getDifficulty(),
                    simpleDietEntity.isHeart()
            );

            simples[i] = fridgeSimpleDietResDto;
        }

        return Arrays.asList(simples);

//        String[] tempArr = message.split("\n\n");
//
//        FridgeSimpleDietResDto[] fridgeSimpleDietResDto = new FridgeSimpleDietResDto[2];
//
//        String[] fdiet = tempArr[0].split("\n");
//        SimpleDietEntity simpleDietEntity = SimpleDietEntity.builder()
//                .dietName(fdiet[0].split(":")[1])
//                .time(Long.parseLong(fdiet[1].split(":")[1]))
//                .difficulty(fdiet[2].split(":")[1])
//                .build();
//
//        simpleDietRepository.save(simpleDietEntity);
//        fridgeSimpleDietResDto[0] = new FridgeSimpleDietResDto(simpleDietEntity.getSimpleDietId(), simpleDietEntity.getDietName(), simpleDietEntity.getTime(),simpleDietEntity.getDifficulty());
//
//        fdiet = tempArr[1].split("\n");
//        simpleDietEntity = SimpleDietEntity.builder()
//                .dietName(fdiet[0].split(":")[1])
//                .time(Long.parseLong(fdiet[1].split(":")[1]))
//                .difficulty(fdiet[2].split(":")[1])
//                .build();
//
//        simpleDietRepository.save(simpleDietEntity);
//        fridgeSimpleDietResDto[1] = new FridgeSimpleDietResDto(simpleDietEntity.getSimpleDietId(), simpleDietEntity.getDietName(), simpleDietEntity.getTime(),simpleDietEntity.getDifficulty());
//
//        return fridgeSimpleDietResDto;

    }


    public DietResDto getDetailDiet(Long simpleDietId,DietReqDto dietReqDto){

        Optional<SimpleDietEntity> optionalSimpleDiet = simpleDietRepository.findBySimpleDietId(simpleDietId);
        if(optionalSimpleDiet.isEmpty())
            throw new SimpleDietDoesNotExistException();
        SimpleDietEntity simpleDiet = optionalSimpleDiet.get();

        List<ChatGptMessage> messages = new ArrayList<>();
        String question = simpleDiet.getDietName() + "의 재료와 레시피를 (재료:) 한 줄, (레시피:) 한 줄씩 구분해서 알려줘. " +
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
                .ingredients(ingredients)
                .recipe(recipe)
                .available(dietReqDto.getFridge()) //냉장고 재료
                .allergy(simpleDiet.getBaby().getAllergy())
                .needs(simpleDiet.getBaby().getNeeds())
                .keyword(dietReqDto.getKeyword())//#빨리 먹을 수 있는
                .imageUrl(imageUrl)
                .date(now)
                .build();
        dietRepository.save(dietEntity);

        DietResDto dietResDto = new DietResDto(dietReqDto.getDietName(), dietReqDto.getDescription(),
                ingredients, recipe, simpleDiet.getTime(), dietReqDto.getDifficulty());

        return dietResDto;
    }


//    public DietResDto getFridgeDiet(Long customerId, Long babyId, FridgeDietReqDto fridgeDietReqDto){
//
//        Optional<CustomerEntity> opCustomerEntity = customerRepository.findCustomerEntityByCustomerId(customerId);
//        if(opCustomerEntity.isEmpty())
//            throw new CustomerDoesNotExistException();
//        CustomerEntity customerEntity = opCustomerEntity.get();
//
//        Optional<BabyEntity> opBabyEntity = babyRepository.findBabyEntityByBabyId(babyId);
//        if(opBabyEntity.isEmpty())
//            throw new BabyDoesNotExistException();
//        BabyEntity babyEntity = opBabyEntity.get();
//
//        if(customerEntity.getCustomerId() != customerId){
//            throw new CustomerDoesNotMatchException();
//        }
//
//        List<ChatGptMessage> messages = new ArrayList<>();
//        String question = fridgeDietReqDto.getDietName() +
//                "의 재료, 소개, 레시피를 (재료:),(소개:),(레시피:) 형태로 한줄씩 알려줘. " +
//                "재료는 그람수 단위로 알려주고, " +
//                "레시피는 단계별로 나눠서 자세히 알려줘.";
//
//        messages.add(ChatGptMessage.builder()
//                .role(ChatGptConfig.ROLE)
//                .content(question)
//                .build());
//
//        String message =  this.getResponse(
//                this.buildHttpEntity(
//                        new GptReqDto(
//                                ChatGptConfig.CHAT_MODEL,
//                                ChatGptConfig.MAX_TOKEN,
//                                ChatGptConfig.TEMPERATURE,
//                                ChatGptConfig.STREAM,
//                                messages
//                        )
//                )
//        ).getChoices().get(0).getMessage().getContent();
//
//        String[] tempArr = message.split("\n\n");
//        String ingredients = tempArr[0].split(": ")[1];
//        String description = tempArr[1].split(": ")[1];
//        String recipe = tempArr[2].split(":\n")[1];
//
//        LocalDateTime now = LocalDateTime.now();
//        String imageUrl = "image";
//
//        DietEntity dietEntity = DietEntity.builder()
//                .ingredients(ingredients)
//                .recipe(recipe)
//                .info(fridgeDietReqDto.getType()) //간식 or 식사
//                .available(fridgeDietReqDto.getFridge()) //냉장고 재료
//                .allergy(babyEntity.getAllergy())
//                .needs(babyEntity.getNeeds())
//                .keyword(fridgeDietReqDto.getKeyword())//#빨리 먹을 수 있는
//                .imageUrl(imageUrl)
//                .date(now)
//                .build();
//        dietRepository.save(dietEntity);
//
//        DietResDto dietResDto = new DietResDto(fridgeDietReqDto.getDietName(), description,
//                ingredients, recipe, fridgeDietReqDto.getTime(), fridgeDietReqDto.getDifficulty());
//
//        return dietResDto;
//
//
//    }

    public void pressHeart(Long simpleDietId){

        Optional<SimpleDietEntity> opSimpleDiet = simpleDietRepository.findBySimpleDietId(simpleDietId);
        if(opSimpleDiet.isEmpty())
            throw new SimpleDietDoesNotExistException();


        SimpleDietEntity simpleDiet = opSimpleDiet.get();
        if(simpleDiet.isHeart()){
            simpleDiet.setHeart(false);
            simpleDietRepository.save(simpleDiet);
        }else{
            simpleDiet.setHeart(true);
            simpleDietRepository.save(simpleDiet);
        }
    }

    public List<HeartDietResDto> getHeartDiet(Long customerId){
        Optional<CustomerEntity> opCustomerEntity = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomerEntity.isEmpty())
            throw new CustomerDoesNotExistException();
        CustomerEntity customerEntity = opCustomerEntity.get();

        if(customerEntity.getCustomerId() != customerId){
            throw new CustomerDoesNotMatchException();
        }

        List<SimpleDietEntity> simpleDiets = simpleDietRepository.findByCustomerCustomerIdAndHeart(customerId, true);
        if(simpleDiets.isEmpty())
            throw new SimpleDietDoesNotExistException();
        for (SimpleDietEntity simpleDiet : simpleDiets) {
            if (simpleDiet.getCustomer().getCustomerId() != customerId) {
                throw new CustomerDoesNotMatchException();
            }
        }
        return simpleDiets.stream().map(diet -> {
            return new HeartDietResDto(
                    diet.getSimpleDietId(),
                    diet.getDietName(),
                    diet.getDescription(),
                    diet.getTime(),
                    diet.getDifficulty()
            );
        }).collect(Collectors.toList());
    }

    public DietResDto getHeartDietView(Long simpleDietId){
        Optional<SimpleDietEntity> optionalSimpleDiet = simpleDietRepository.findBySimpleDietId(simpleDietId);
        if(optionalSimpleDiet.isEmpty())
            throw new SimpleDietDoesNotExistException();
        SimpleDietEntity simpleDiet = optionalSimpleDiet.get();

        DietResDto dietResDto = new DietResDto(
                simpleDiet.getDietName(),
                simpleDiet.getDescription(),
                simpleDiet.getDiet().getIngredients(),
                simpleDiet.getDiet().getRecipe(),
                simpleDiet.getTime(),
                simpleDiet.getDifficulty()
        );

        return dietResDto;
    }
}
