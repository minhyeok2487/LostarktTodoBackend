//package lostark.todo.controller.apiV3;
//
//import lombok.extern.slf4j.Slf4j;
//import lostark.todo.domain.notices.Notices;
//import lostark.todo.service.NoticesService;
//import lostark.todo.service.WebHookService;
//import lostark.todo.service.discordWebHook.DiscordWebhook;
//import lostark.todo.service.lostarkApi.LostarkNewsService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//
//import javax.transaction.Transactional;
//import java.awt.*;
//import java.util.List;
//
//@SpringBootTest
//@Transactional
//@Slf4j
//public class LostarkNoticeControllerTest {
//
//    @Autowired
//    LostarkNewsService newsService;
//
//    @Autowired
//    NoticesService noticesService;
//
//    @Autowired
//    WebHookService webHookService;
//
//    @Value("${API-KEY3}")
//    public String apiKey;
//
//    /*로스트아크 새로운 공지사항 가져와서 저장 Method*/
//    public void getLostarkNotice() {
//        List<Notices> noticesList = newsService.getNoticeList(apiKey);
//        for (Notices notices : noticesList) {
//            if (noticesService.save(notices)) {
//                webHookService.sendMessage(new DiscordWebhook.EmbedObject()
//                        .setTitle("새로운 로스트아크 공지사항이 저장되었습니다.")
//                        .addField("제목", notices.getTitle(), true)
//                        .addField("링크", notices.getLink(), false)
//                        .setColor(Color.GREEN));
//            }
//        }
//    }
//
////    @Test
////    @Rollback(value = false)
////    void getLostarkNoticeTest() {
////        getLostarkNotice();
////    }
//}
