package com.toucheese.favorites.entity;

import com.toucheese.member.entity.Member;
import com.toucheese.studio.entity.Studio;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "Favorites")
public class Favorites {
    /*
        @Id
        - 해당 필드가 엔티티의 PK임을 나타내는 애노테이션

        @GeneratedValue(strategy = GenerationType.IDENTITY)
        - PK의 값을 자동으로 생성하는 방법을 정의하는 애노테이션
        - GenerationType.IDENTITY
            - DB에서 PK의 값을 자동으로 증가시키도록 설정하여, 엔티티가 저장될 때마다 새로운 고유한 값을 생성함.

        @Column(name = "like_id")
        - 데이터베이스 테이블의 열 속성을 정의하는 애노테이션
        - name = "like_id"
            - 해당 필드가 DB에서 "like_id"라는 이름의 열에 매핑됨을 나타낸다.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id; // 기본 키의 값을 저장하는 데 사용

    /*
        @ManyToOne
        - 다:1 즉, 현재 엔티티가 여러 개의 인스턴스를 다른 엔티티와 연결할 수 있음
        - fetch = FetchType.LAZY는 Member 엔티티를 필요할 때만 로드하도록 설정
        - 즉,
    */

    /*
        @JoinColumn
        - 현재 엔티티와 연결된 Studio 엔티티의 외래 키 컬럼을 정의하는 애노테이션.
        - name은 member_id로 외래 키 열의 이름을 지정함.
        - nullable = false는 이 외래 키가 NULL 값을 가질 수 없음을 의미함.
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studio_id", nullable = false)
    private Studio studio;

    @Column(nullable = false) // createdAt 필드가 DB에 저장될 때 널 값을 허용하지 않도록 설정
    private LocalDateTime createdAt; // 객체가 생성된 시점을 저장

    /*
        @PrePersist
        - 엔티티가 데이터베이스에 저장되기 전에 특정 작업을 수행하기 위해 사용
     */
    @PrePersist
    public void prePersist(){ // 이 메서드는 엔티티가 저장되기 직전에 호출
        // LocalDateTime.now(): 현재 시간을 가져오는 메서드
        // createdAt 필드에 현재 시간을 할당
        this.createdAt = LocalDateTime.now();
    }
}
