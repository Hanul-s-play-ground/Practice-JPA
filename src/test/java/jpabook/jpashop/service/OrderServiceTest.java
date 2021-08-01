package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("상품 주문")
    public void 상품주문() {
        // given
        Member member = createMember("hanul");

        Book book = createBook("JPA", 10000, 10);

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        // 주문 상태 확인
        Assertions.assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        // 주문 상품 수량 확인
        Assertions.assertThat(getOrder.getOrderItems().size()).isEqualTo(1);
        // 주문 가격 확인
        Assertions.assertThat(getOrder.getTotalPrice()).isEqualTo(10000 * orderCount);
        // 주문 후 재고 확인
        Assertions.assertThat(book.getStockQuantity()).isEqualTo(8);

    }

    @Test
    @DisplayName("재고 수량 초과")
    public void 재고수량초과() {
        // given
        Member member = createMember("hanul");
        Item item = createBook("JPA", 10000, 10);

        int orderCount = 11;

        // when
        // then
        assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), item.getId(), orderCount);
        });
    }

    @Test
    public void 주문취소() {
        // given
        Member member = createMember("hanul");
        Book book = createBook("JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);
        // 주문이 취소 상태인지 확인
        Assertions.assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.CANCLE);
        // 주문 취소 후 상품 수량이 원복 되었는지 확인
        Assertions.assertThat(book.getStockQuantity()).isEqualTo(10);

    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);
        return book;
    }

}
