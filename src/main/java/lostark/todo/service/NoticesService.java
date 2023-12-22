package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.notices.NoticesRepository;
import lostark.todo.domain.notices.Notices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NoticesService {

    private final NoticesRepository noticesRepository;

    public boolean save(Notices newNotice) {
        String noticeId = getLinkLastPart(newNotice);
        boolean existsed = noticesRepository.existsByLinkContains(noticeId);
        if (!existsed) {
            noticesRepository.save(newNotice);
            log.info("로스트아크 새로운 공지사항 저장");
            return true;
        } else {
            log.info("새로운 공지사항이 아닙니다.");
            return false;
        }
    }

    private String getLinkLastPart(Notices notice) {
        if (notice == null || notice.getLink() == null) {
            return null;
        }
        String[] parts = notice.getLink().split("/");
        return parts[parts.length - 1];
    }
}
