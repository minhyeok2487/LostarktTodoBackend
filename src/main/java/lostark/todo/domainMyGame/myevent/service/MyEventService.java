package lostark.todo.domainMyGame.myevent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainMyGame.myevent.dto.MyEventRequest;
import lostark.todo.domainMyGame.myevent.dto.MyEventResponse;
import lostark.todo.domainMyGame.myevent.entity.MyEvent;
import lostark.todo.domainMyGame.myevent.entity.MyEventImage;
import lostark.todo.domainMyGame.myevent.entity.MyEventVideo;
import lostark.todo.domainMyGame.myevent.enums.MyEventType;
import lostark.todo.domainMyGame.myevent.repository.EventRepository;
import lostark.todo.domainMyGame.mygame.entity.MyGame;
import lostark.todo.domainMyGame.mygame.service.MyGameService;
import lostark.todo.domainMyGame.myevent.repository.EventImageRepository;
import lostark.todo.global.dto.ImageResponse;
import lostark.todo.global.dto.ImageResponseV2;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import lostark.todo.global.service.ImagesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyEventService {

    private final EventRepository eventRepository;
    private final EventImageRepository eventImageRepository;
    private final MyGameService myGameService;
    private final ImagesService imagesService;

    public MyEvent get(Long id) {
        MyEvent event = eventRepository.get(id);
        if (event == null) {
            throw new ConditionNotMetException("존재하지 않는 이벤트입니다.");
        }
        return event;
    }

    public MyEventResponse getEventById(Long id) {
        MyEvent event = get(id);
        return MyEventResponse.from(event);
    }

    public Page<MyEventResponse> searchEvents(List<Long> gameIds,
                                              LocalDateTime startDate,
                                              LocalDateTime endDate,
                                              MyEventType type,
                                              int page,
                                              int limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<MyEvent> eventsPage = eventRepository.searchEvents(gameIds, startDate, endDate, type, pageRequest);
        return eventsPage.map(MyEventResponse::from);
    }

    @Transactional
    public MyEventResponse createEvent(MyEventRequest request) {
        MyGame game = myGameService.get(request.getGameId());
        MyEvent event = request.toEntity(game);

        MyEvent savedEvent = eventRepository.save(event);

        // 이미지 ID 리스트로 조회 후 연결
        List<Long> imageIds = request.getImageIds();
        if (!imageIds.isEmpty()) {
            AtomicInteger counter = new AtomicInteger(0);
            eventImageRepository.findAllByIdIn(imageIds)
                    .forEach(image -> {
                        image.updateEvent(savedEvent, counter.getAndIncrement());
                        savedEvent.getImages().add(image);
                    });
        }

        // 비디오는 기존 방식 유지 (URL로 저장)
        List<String> videoUrls = request.getVideos();
        IntStream.range(0, videoUrls.size())
                .mapToObj(i -> MyEventVideo.builder()
                        .url(videoUrls.get(i))
                        .ordering(i)
                        .build())
                .forEach(savedEvent::addVideo);

        return MyEventResponse.from(savedEvent);
    }

    @Transactional
    public ImageResponseV2 uploadImage(MultipartFile image) {
        String folderName = "event-images/";
        ImageResponse imageResponse = imagesService.upload(image, folderName);
        MyEventImage savedImage = eventImageRepository.save(
                MyEventImage.builder()
                        .url(imageResponse.getImageUrl())
                        .fileName(imageResponse.getFileName())
                        .ordering(0)
                        .build());
        return new ImageResponseV2(imageResponse, savedImage.getId());
    }
}
