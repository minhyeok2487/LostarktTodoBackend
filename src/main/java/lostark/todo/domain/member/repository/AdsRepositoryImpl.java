package lostark.todo.domain.member.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.admin.dto.AdminAdsSearchParams;
import lostark.todo.domain.admin.dto.AdminAdsSearchResponse;
import lostark.todo.domain.member.entity.Ads;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.member.entity.QAds.ads;


@RequiredArgsConstructor
public class AdsRepositoryImpl implements AdsCustomRepository {

    private final JPAQueryFactory factory;


    @Override
    public CursorResponse<AdminAdsSearchResponse> search(AdminAdsSearchParams params, PageRequest pageRequest) {
        List<AdminAdsSearchResponse> fetch = factory.select(
                Projections.fields(
                        AdminAdsSearchResponse.class,
                        ads.id.as("adsId"),
                        ads.createdDate.as("createdDate"),
                        ads.name.as("name"),
                        ads.proposerEmail.as("proposerEmail"),
                        ads.memberId.as("memberId"),
                        ads.checked.as("checked")
                ))
                .from(ads)
                .where(
                        ltAdsId(params.getAdsId())
                )
                .orderBy(ads.createdDate.desc())
                .limit(pageRequest.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;

        if (fetch.size() > pageRequest.getPageSize()) {
            fetch.remove(pageRequest.getPageSize());
            hasNext = true;
        }

        return new CursorResponse<>(fetch, hasNext);
    }

    @Override
    public Optional<Ads> get(String username) {
        return Optional.ofNullable(factory.selectFrom(ads).where(ads.proposerEmail.eq(username)).fetchOne());
    }

    private BooleanExpression ltAdsId(Long adsId) {
        if (adsId != null) {
            return ads.id.lt(adsId);
        }
        return null;
    }
}
