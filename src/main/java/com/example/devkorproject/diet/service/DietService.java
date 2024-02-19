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
import com.example.devkorproject.diet.exception.SimpleDietHeartFalseException;
import com.example.devkorproject.diet.repository.DietRepository;
import com.example.devkorproject.diet.repository.SimpleDietRepository;
import com.example.devkorproject.fridge.entity.FridgeEntity;
import com.example.devkorproject.fridge.repository.FridgeRepository;
import com.example.devkorproject.post.exception.CustomerDoesNotMatchException;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONObject;


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

    public  SimpleResDto[] askQuestion(Long customerId, Long babyId, SimpleReqDto simpleRequestDto) throws JSONException {

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
                "를 다음의 json 형식으로 세 가지 추천해줘. " +
                "{“dietName”:””,“description”:””,“time”:”분 단위, 숫자만”,“difficulty”:”간단/보통/복잡 중 하나”}";

        System.out.println(question);
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
        System.out.println(message);

        JSONArray jsonArray = new JSONArray(message);

        // 각 메뉴 정보 저장할 리스트 생성
        List<SimpleResDto> menuList = new ArrayList<>();

        // 각 객체에서 dietName, description, time, difficulty 추출하여 Menu 객체로 저장
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject menuJson = jsonArray.getJSONObject(i);

            String dietName = menuJson.getString("dietName");
            String description = menuJson.getString("description");
            String time = menuJson.getString("time");
            String difficulty = menuJson.getString("difficulty");

            SimpleDietEntity simpleDietEntity = SimpleDietEntity.builder()
                    .dietName(dietName)
                    .description(description)
                    .time(Long.parseLong(time))
                    .difficulty(difficulty)
                    .type(type)
                    .heart(false)
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
            menuList.add(simpleResDto);
        }

        return menuList.toArray(new SimpleResDto[0]);
    }

    public List<FridgeSimpleDietResDto> getFridgeSimpleDiet(Long customerId, Long babyId) throws JSONException {
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
        System.out.println(ingredients);

        List<ChatGptMessage> messages = new ArrayList<>();
        String question = ingredients + "를 활용한 " +
                allergyMessage +
                "음식을 다음의 json 형식으로 두 가지 추천해줘. " +
                "{“dietName”:””,“description”:””,“time”:”분 단위, 숫자만”,“difficulty”:”간단/보통/복잡 중 하나”}";
        System.out.println(question);

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
        System.out.println(message);

        JSONArray jsonArray = new JSONArray(message);
        // 각 메뉴 정보 저장할 리스트 생성
        List<FridgeSimpleDietResDto> menuList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject menuJson = jsonArray.getJSONObject(i);

            String dietName = menuJson.getString("dietName");
            String description = menuJson.getString("description");
            String time = menuJson.getString("time");
            String difficulty = menuJson.getString("difficulty");

            SimpleDietEntity simpleDietEntity = SimpleDietEntity.builder()
                    .dietName(dietName)
                    .description(description)
                    .time(Long.parseLong(time))
                    .difficulty(difficulty)
                    .heart(false)
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
            menuList.add(fridgeSimpleDietResDto);
        }


        return menuList;

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


    public DietResDto getDetailDiet(Long simpleDietId,DetailReqDto detailReqDto) throws JSONException {

        Optional<SimpleDietEntity> optionalSimpleDiet = simpleDietRepository.findBySimpleDietId(simpleDietId);
        if(optionalSimpleDiet.isEmpty())
            throw new SimpleDietDoesNotExistException();
        SimpleDietEntity simpleDiet = optionalSimpleDiet.get();

        List<ChatGptMessage> messages = new ArrayList<>();
        String question = simpleDiet.getDietName()
                + "에 대해 다음의 json형식으로 답해줘. 레시피는 단계별로 줄바꿈해줘."
                + "{‘ingredients’:’’,’recipe’:’’}";

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
        System.out.println(message);

        JSONObject jsonObject = new JSONObject(message);

        // ingredients와 recipe 추출
        String ingredients = jsonObject.getString("ingredients");
        String recipe = jsonObject.getString("recipe");

//        현재 날짜 구하기
        LocalDateTime now = LocalDateTime.now();
//        String imageUrl = "image";

        DietEntity dietEntity = DietEntity.builder()
                .ingredients(ingredients)
                .recipe(recipe)
                .available(detailReqDto.getFridge()) //냉장고 재료
                .allergy(simpleDiet.getBaby().getAllergy())
                .needs(simpleDiet.getBaby().getNeeds())
                .keyword(detailReqDto.getKeyword())//#빨리 먹을 수 있는
//                .imageUrl(imageUrl)
                .date(now)
                .build();
        dietRepository.save(dietEntity);
        simpleDiet.setDiet(dietEntity);



        DietResDto dietResDto = new DietResDto(simpleDiet.getDietName(), simpleDiet.getDescription(),
                ingredients, recipe, simpleDiet.getTime(), simpleDiet.getDifficulty(), simpleDiet.isHeart());

        return dietResDto;
    }


//    public DietResDto getFridgeDetailDiet(Long simpleDietId, String type){
//
//        Optional<SimpleDietEntity> optionalSimpleDiet = simpleDietRepository.findBySimpleDietId(simpleDietId);
//        if(optionalSimpleDiet.isEmpty())
//            throw new SimpleDietDoesNotExistException();
//        SimpleDietEntity simpleDiet = optionalSimpleDiet.get();
//
//        List<ChatGptMessage> messages = new ArrayList<>();
//        String question = simpleDiet.getDietName() + "의 재료와 레시피를 (재료:) 한 줄, (레시피:) 한 줄씩 구분해서 알려줘. " +
//                "재료는 그람수 단위로 자세하게 한줄로 알려주고, " +
//                "레시피는 단계별로 나눠서 자세하게 한줄로 알려줘.";
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
//        System.out.println(message);
//
//
//        String[] tempArr = message.split("\n\n");
//        String ingredients = tempArr[0].split(": ")[1];
//        String recipe = tempArr[1].split(":\n")[1];
//        System.out.println(ingredients);
//        System.out.println(recipe);
////        현재 날짜 구하기
//        LocalDateTime now = LocalDateTime.now();
////        String imageUrl = "image";
//
//        DietEntity dietEntity = DietEntity.builder()
//                .ingredients(ingredients)
//                .recipe(recipe)
//                .allergy(simpleDiet.getBaby().getAllergy())
//                .needs(simpleDiet.getBaby().getNeeds())
////                .imageUrl(imageUrl)
//                .date(now)
//                .build();
//        dietRepository.save(dietEntity);
//
//
//        DietResDto dietResDto = new DietResDto(simpleDiet.getDietName(), simpleDiet.getDescription(),
//                ingredients, recipe, simpleDiet.getTime(), simpleDiet.getDifficulty());
//
//        return dietResDto;
//    }

    public PressDto pressHeart(Long simpleDietId){

        Optional<SimpleDietEntity> opSimpleDiet = simpleDietRepository.findBySimpleDietId(simpleDietId);
        if(opSimpleDiet.isEmpty())
            throw new SimpleDietDoesNotExistException();


        SimpleDietEntity simpleDiet = opSimpleDiet.get();
        if(simpleDiet.isHeart()){
            simpleDiet.setHeart(false);
            simpleDietRepository.save(simpleDiet);
        }else {
            simpleDiet.setHeart(true);
            simpleDietRepository.save(simpleDiet);
        }

        return new PressDto(simpleDiet.isHeart());
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

        for (SimpleDietEntity simpleDiet : simpleDiets) {
            if (simpleDiet.getCustomer().getCustomerId() != customerId) {
                throw new CustomerDoesNotMatchException();
            }
        }

        if(simpleDiets.isEmpty())
            return Collections.emptyList();

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

    public DietResDto getHeartDietView(Long simpleDietId) throws JSONException {
        Optional<SimpleDietEntity> optionalSimpleDiet = simpleDietRepository.findBySimpleDietId(simpleDietId);
        if(optionalSimpleDiet.isEmpty())
            throw new SimpleDietDoesNotExistException();
        SimpleDietEntity simpleDiet = optionalSimpleDiet.get();

        if(!simpleDiet.isHeart()){
            throw new SimpleDietHeartFalseException();
        }

        if(simpleDiet.getDiet() != null) {
            return new DietResDto(
                    simpleDiet.getDietName(),
                    simpleDiet.getDescription(),
                    simpleDiet.getDiet().getIngredients(),
                    simpleDiet.getDiet().getRecipe(),
                    simpleDiet.getTime(),
                    simpleDiet.getDifficulty(),
                    simpleDiet.isHeart()
            );
        }

        List<ChatGptMessage> messages = new ArrayList<>();
        String question = simpleDiet.getDietName()
                + "에 대해 다음의 json형식으로 답해줘. 레시피는 단계별로 줄바꿈해줘."
                + "{‘ingredients’:’’,’recipe’:’’}";

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
        System.out.println(message);


        JSONObject jsonObject = new JSONObject(message);

        // ingredients와 recipe 추출
        String ingredients = jsonObject.getString("ingredients");
        String recipe = jsonObject.getString("recipe");
//        현재 날짜 구하기
        LocalDateTime now = LocalDateTime.now();
//        String imageUrl = "image";

        DietEntity dietEntity = DietEntity.builder()
                .ingredients(ingredients)
                .recipe(recipe)
                .allergy(simpleDiet.getBaby().getAllergy())
                .needs(simpleDiet.getBaby().getNeeds())
//                .imageUrl(imageUrl)
                .date(now)
                .build();
        dietRepository.save(dietEntity);
        simpleDiet.setDiet(dietEntity);


        return new DietResDto(simpleDiet.getDietName(), simpleDiet.getDescription(),
                ingredients, recipe, simpleDiet.getTime(), simpleDiet.getDifficulty(), simpleDiet.isHeart());
    }
}
