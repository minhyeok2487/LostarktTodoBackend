package lostark.todo.domain.lostark.notices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
            return true;
        } else {
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

    public Page<Notices> findAll(int page, int size) {
        return noticesRepository.findAll(PageRequest.of(page, size));
    }
}
