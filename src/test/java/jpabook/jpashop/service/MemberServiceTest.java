package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입")
    public void 회원가입() {
        // given
        Member member = new Member();
        member.setName("hanul");

        // when
        Long savedId = memberService.join(member);

        // then
        Assertions.assertThat(member).isEqualTo(memberService.findOne(savedId));
    }

    @Test
    @DisplayName("중복 회원 예외")
    public void 중복_회원_예외() {
        // given
        Member member1 = new Member();
        member1.setName("hanul1");

        Member member2 = new Member();
        member2.setName("hanul1");

        // when
        memberService.join(member1);

        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });
    }

    @Test
    @DisplayName("회원 찾기")
    public void 회원_찾기() {
        // given
        Member member = new Member();
        member.setName("hanul");

        // when
        memberService.join(member);
        Member findMember = memberService.findOne(member.getId());

        // then
        Assertions.assertThat(member).isEqualTo(findMember);
    }

}