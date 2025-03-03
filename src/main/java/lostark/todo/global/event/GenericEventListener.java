package lostark.todo.global.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.global.event.entity.GenericEvent;
import lostark.todo.global.event.entity.MemberEvent;
import lostark.todo.global.service.webHook.WebHookService;
import lostark.todo.global.service.webHook.DiscordWebhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenericEventListener {

    private final WebHookService webHookService;

    @Value("${discord.noticeURL}")
    private String noticeUrl;

    @Value("${discord.memberURL}")
    private String memberURL;

    @Async
    @EventListener(classes = GenericEvent.class)
    public void handleEvent(GenericEvent genericEvent) {
        webHookService.sendMessage(new DiscordWebhook.EmbedObject()
                .setTitle(genericEvent.getTitle())
                .setDescription(genericEvent.getUsername())
                .addField("내용", genericEvent.getMessage(), true)
                .setColor(Color.BLUE), noticeUrl);
    }

    @Async
    @EventListener(classes = MemberEvent.class)
    public void handleEvent(MemberEvent memberEvent) {
        Member member = memberEvent.getMember();
        String maskingResult = member.getUsername();
        if (maskingResult.length() >= 3) {
            maskingResult = maskingResult.replaceAll("(?<=.{3}).(?=.*@)", "*");
        }

        DiscordWebhook.EmbedObject object = new DiscordWebhook.EmbedObject()
                .setTitle(memberEvent.getEventType().getMessage())
                .addField("member ID", String.valueOf(member.getId()), true)
                .addField("username", maskingResult, true)
                .setColor(Color.BLUE);
        if (!member.getCharacters().isEmpty()) {
            object.addField("대표캐릭터", member.getCharacters().get(0).getCharacterName(), true);
        }
        String message = memberEvent.getEventType().getMessage() + "/ username : " + member.getUsername();
        log.info(message);

        webHookService.sendMessage(object, memberURL);
    }
}
