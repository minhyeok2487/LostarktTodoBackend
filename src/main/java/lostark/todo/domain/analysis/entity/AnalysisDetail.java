package lostark.todo.domain.analysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AnalysisDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_detail_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id")
    @JsonBackReference
    private Analysis analysis;

    private String attrName;

    private Long attrValue;
}
