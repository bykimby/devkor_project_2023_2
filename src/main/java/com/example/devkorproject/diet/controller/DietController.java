package com.example.devkorproject.diet.controller;
import com.example.devkorproject.diet.dto.DietResDto;
import com.example.devkorproject.diet.dto.DietReqDto;
import com.example.devkorproject.common.dto.HttpDataResponse;

import com.example.devkorproject.diet.service.DietService;
import lombok.*;

import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/diet")
@RestController
public class DietController {
    private final DietService dietService;

    @PostMapping("/question")
    public HttpDataResponse<DietResDto[]> askQuestion(@RequestBody DietReqDto dietRequestDto){
        return HttpDataResponse.of(dietService.askQuestion(dietRequestDto));
    }
}
//    public ResponseEntity sendQuestion(
//            Locale locale,
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @RequestBody QuestionReq questionRequest) {
//        String code = ResponseCode.CD_SUCCESS;
//        GptResDto GptResDto = null;
//        try {
//            GptResDto = dietService.askQuestion(questionRequest);
//        } catch (Exception e) {
//            apiResponse.printErrorMessage(e);
//            code = e.getMessage();
//        }
//        //return 부분은 자유롭게 수정하시면됩니다. ex)return GptResDto;
//        return apiResponse.getResponseEntity(locale, code,
//                GptResDto != null ? GptResDto.getChoices().get(0).getMessage().getContent() : new GptResDto());
//    }
//}


