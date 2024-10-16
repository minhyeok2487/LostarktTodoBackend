package lostark.todo.global.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.Role;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domain.logs.LogsDayContent;
import lostark.todo.domain.logs.LogsRepository;
import lostark.todo.domain.member.Member;
import lostark.todo.global.event.entity.character.DayContentCheckEvent;
import lostark.todo.global.event.entity.CommentEvent;
import lostark.todo.global.event.entity.MemberEvent;
import lostark.todo.service.WebHookService;
import lostark.todo.service.discordWebHook.DiscordWebhook;
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
    private final LogsRepository logsRepository;

    @Value("${discord.noticeURL}")
    private String noticeUrl;

    @Value("${discord.memberURL}")
    private String memberURL;

    @Async
    @EventListener(classes = CommentEvent.class)
    public void handleEvent(CommentEvent commentEvent) {
        if (!commentEvent.getMember().getRole().equals(Role.ADMIN)) {
            webHookService.sendMessage(new DiscordWebhook.EmbedObject()
                    .setTitle("새로운 방명록이 작성되었습니다.")
                    .setDescription("<@&1184700819308822570>")
                    .addField("내용", commentEvent.getMessage(), true)
                    .setColor(Color.BLUE), noticeUrl);
        }
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

    @Async
    @EventListener(classes = DayContentCheckEvent.class)
    public void handleEvent(DayContentCheckEvent characterEvent) {
        Character character = characterEvent.getCharacter();
        LogsDayContent logsDayContent = new LogsDayContent();
        if (characterEvent.getCategory().equals("epona")) {
            double profit = 0;
            logsDayContent = LogsDayContent.toEntity(characterEvent, character, profit);
        }
        if (characterEvent.getCategory().equals("chaos")) {
            double profit = character.getDayTodo().getChaosGold();
            if (character.getDayTodo().getChaosCheck() == 1) {
                profit /= 2;
            } else if (character.getDayTodo().getChaosCheck() == 0){
                profit = -1 * profit;
            }
            logsDayContent = LogsDayContent.toEntity(characterEvent, character, profit);
        }
        if (characterEvent.getCategory().equals("guardian")) {
            double profit = character.getDayTodo().getGuardianGold();
            if (character.getDayTodo().getGuardianCheck() == 0) {
                profit = -1 * profit;
            }
            logsDayContent = LogsDayContent.toEntity(characterEvent, character, profit);
        }

        logsRepository.save(logsDayContent);
    }
}
