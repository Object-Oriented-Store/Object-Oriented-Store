package com.ohgiraffers.store.order.service;

import com.ohgiraffers.store.common.config.DBConnection;
import com.ohgiraffers.store.order.model.OrderDTO;
import com.ohgiraffers.store.order.model.OrderItemDTO;
import com.ohgiraffers.store.order.repository.OrderDAO;
import com.ohgiraffers.store.order.repository.OrderItemDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();

    // 상품 추가 업무 흐름
    public boolean addOrderItem(
            int memberCode,
            int productCode,
            int quantity
    ) {

        if (quantity <= 0) {
            return false;
        }

        try (Connection connection =
                     DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // 회원의 기존 PENDING 주문 조회
                OrderDTO pendingOrder =
                        orderDAO.findPendingOrderByMemberCode(
                                connection,
                                memberCode
                        );

                // 기존 PENDING 주문이 없으면 새 주문 생성
                if (pendingOrder == null) {
                    pendingOrder = new OrderDTO();

                    pendingOrder.setOrderCode(
                            generateOrderCode()
                    );
                    pendingOrder.setMemberCode(memberCode);
                    pendingOrder.setOriginalAmount(0);
                    pendingOrder.setDiscountAmount(0);
                    pendingOrder.setFinalAmount(0);

                    int orderResult =
                            orderDAO.insertOrder(
                                    connection,
                                    pendingOrder
                            );

                    if (orderResult == 0) {
                        connection.rollback();
                        return false;
                    }
                }

                // 조회하거나 생성한 주문에 상품 추가
                OrderItemDTO orderItem =
                        new OrderItemDTO(
                                pendingOrder.getOrderCode(),
                                productCode,
                                quantity
                        );

                // 장바구니에 담는 순간 재고를 예약한다.
                // 재고가 부족하면 0행이 수정되므로 주문상품도 저장하지 않는다.
                int stockResult =
                        orderItemDAO.decreaseProductStock(
                                connection,
                                productCode,
                                quantity
                        );

                if (stockResult != 1) {
                    connection.rollback();
                    return false;
                }

                int itemResult =
                        orderItemDAO.insertOrderItem(
                                connection,
                                orderItem
                        );

                // 상품 추가 후 행사 할인을 포함해 주문 총액 재계산
                int amountResult =
                        recalculateOrderAmounts(
                                connection,
                                pendingOrder
                        );

                if (itemResult > 0 && amountResult > 0) {
                    connection.commit();
                    return true;
                }

                connection.rollback();
                return false;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "주문 상품 추가 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    // 주문 상품 수량 수정 업무 흐름
    public boolean updateOrderItemQuantity(
            int memberCode,
            int productCode,
            int quantity
    ) {

        if (quantity <= 0) {
            return false;
        }

        try (Connection connection =
                     DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // 회원의 PENDING 주문 조회
                OrderDTO pendingOrder =
                        orderDAO.findPendingOrderByMemberCode(
                                connection,
                                memberCode
                        );

                // 수정할 PENDING 주문이 없으면 실패
                if (pendingOrder == null) {
                    connection.rollback();
                    return false;
                }

                int currentQuantity =
                        orderItemDAO.findQuantity(
                                connection,
                                pendingOrder.getOrderCode(),
                                productCode
                        );

                // 수정할 상품이 장바구니에 없으면 실패
                if (currentQuantity <= 0) {
                    connection.rollback();
                    return false;
                }

                int quantityDifference =
                        quantity - currentQuantity;

                // 수량이 늘어난 경우 늘어난 수량만큼 추가로 재고를 예약한다.
                if (quantityDifference > 0) {
                    int stockResult =
                            orderItemDAO.decreaseProductStock(
                                    connection,
                                    productCode,
                                    quantityDifference
                            );

                    if (stockResult != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                // 수량이 줄어든 경우 줄어든 수량만큼 재고를 즉시 돌려놓는다.
                if (quantityDifference < 0) {
                    int stockResult =
                            orderItemDAO.increaseProductStock(
                                    connection,
                                    productCode,
                                    -quantityDifference
                            );

                    if (stockResult != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                OrderItemDTO orderItem =
                        new OrderItemDTO(
                                pendingOrder.getOrderCode(),
                                productCode,
                                quantity
                        );

                int itemResult =
                        orderItemDAO.updateOrderItemQuantity(
                                connection,
                                orderItem
                        );

                // 수정할 상품이 주문에 없으면 실패
                if (itemResult == 0) {
                    connection.rollback();
                    return false;
                }

                // 수량 변경 후 행사 할인을 포함해 주문 총액 재계산
                int amountResult =
                        recalculateOrderAmounts(
                                connection,
                                pendingOrder
                        );

                if (amountResult > 0) {
                    connection.commit();
                    return true;
                }

                connection.rollback();
                return false;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "주문 상품 수량 수정 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    // 선택한 주문 상품 삭제 업무 흐름
    public boolean deleteOrderItem(
            int memberCode,
            int productCode
    ) {

        try (Connection connection =
                    DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // 회원의 PENDING 주문 조회
                OrderDTO pendingOrder =
                        orderDAO.findPendingOrderByMemberCode(
                                connection,
                                memberCode
                        );

                // 삭제할 PENDING 주문이 없으면 실패
                if (pendingOrder == null) {
                    connection.rollback();
                    return false;
                }

                int deletedQuantity =
                        orderItemDAO.findQuantity(
                                connection,
                                pendingOrder.getOrderCode(),
                                productCode
                        );

                if (deletedQuantity <= 0) {
                    connection.rollback();
                    return false;
                }

                // 선택한 상품 삭제
                int itemResult =
                        orderItemDAO.deleteOrderItem(
                                connection,
                                pendingOrder.getOrderCode(),
                                productCode
                        );

                // 주문에 해당 상품이 없으면 실패
                if (itemResult == 0) {
                    connection.rollback();
                    return false;
                }

                // 장바구니에서 뺀 수량을 상품 재고로 복구한다.
                int stockResult =
                        orderItemDAO.increaseProductStock(
                                connection,
                                productCode,
                                deletedQuantity
                        );

                if (stockResult != 1) {
                    connection.rollback();
                    return false;
                }

                // 상품 삭제 후 행사 할인을 포함해 주문 총액 재계산
                int amountResult =
                        recalculateOrderAmounts(
                                connection,
                                pendingOrder
                        );

                if (amountResult > 0) {
                    connection.commit();
                    return true;
                }

                connection.rollback();
                return false;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "주문 상품 삭제 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    // 주문 상품 전체 삭제 업무 흐름
    public boolean deleteAllOrderItems(
            int memberCode
    ) {

        try (Connection connection =
                    DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // 회원의 PENDING 주문 조회
                OrderDTO pendingOrder =
                        orderDAO.findPendingOrderByMemberCode(
                                connection,
                                memberCode
                        );

                // 삭제할 PENDING 주문이 없으면 실패
                if (pendingOrder == null) {
                    connection.rollback();
                    return false;
                }

                List<OrderItemDTO> orderItems =
                        orderItemDAO.findAllByOrderCode(
                                connection,
                                pendingOrder.getOrderCode()
                        );

                if (orderItems.isEmpty()) {
                    connection.rollback();
                    return false;
                }

                // 주문에 추가된 상품 전체 삭제
                int itemResult =
                        orderItemDAO.deleteAllOrderItems(
                                connection,
                                pendingOrder.getOrderCode()
                        );

                // 삭제할 상품이 없으면 실패
                if (itemResult == 0) {
                    connection.rollback();
                    return false;
                }

                // 장바구니에서 전체 삭제한 상품의 예약 재고를 모두 복구한다.
                for (OrderItemDTO orderItem : orderItems) {
                    int stockResult =
                            orderItemDAO.increaseProductStock(
                                    connection,
                                    orderItem.getProductCode(),
                                    orderItem.getQuantity()
                            );

                    if (stockResult != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                // 전체 삭제 후 행사 할인을 포함해 주문 총액 재계산
                int amountResult =
                        recalculateOrderAmounts(
                                connection,
                                pendingOrder
                        );

                if (amountResult > 0) {
                    connection.commit();
                    return true;
                }

                connection.rollback();
                return false;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "주문 상품 전체 삭제 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    // 로그인한 회원의 결제 전 주문 상품 조회
    public List<OrderItemDTO> findPendingOrderItems(
            int memberCode
    ) {
        if (memberCode <= 0) {
            throw new IllegalArgumentException(
                    "회원번호는 1 이상이어야 합니다."
            );
        }

        try (Connection connection =
                     DBConnection.getConnection()) {

            OrderDTO pendingOrder =
                    orderDAO.findPendingOrderByMemberCode(
                            connection,
                            memberCode
                    );

            if (pendingOrder == null) {
                return List.of();
            }

            return orderItemDAO.findAllByOrderCode(
                    connection,
                    pendingOrder.getOrderCode()
            );

        } catch (SQLException e) {
            throw new RuntimeException(
                    "주문 상품 조회 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    // 로그인한 회원의 결제 전 주문 조회
    public OrderDTO findPendingOrder(
            int memberCode
    ) {

        if (memberCode <= 0) {
            throw new IllegalArgumentException(
                    "회원번호는 1 이상이어야 합니다."
            );
        }

        try (Connection connection =
                     DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                OrderDTO pendingOrder =
                        orderDAO.findPendingOrderByMemberCode(
                                connection,
                                memberCode
                        );

                if (pendingOrder == null) {
                    connection.rollback();
                    return null;
                }

                // 결제 직전에도 현재 활성 행사를 기준으로 금액을 다시 계산한다.
                int amountResult =
                        recalculateOrderAmounts(
                                connection,
                                pendingOrder
                        );

                if (amountResult != 1) {
                    connection.rollback();
                    return null;
                }

                connection.commit();
                return pendingOrder;

            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "결제 전 주문 조회 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    // 주문상품이 바뀔 때마다 할인 전 금액, 행사 할인금액, 최종금액을 함께 갱신한다.
    private int recalculateOrderAmounts(
            Connection connection,
            OrderDTO pendingOrder
    ) throws SQLException {

        int originalAmount =
                orderDAO.selectOriginalAmount(
                        connection,
                        pendingOrder.getOrderCode()
                );

        int discountAmount =
                orderDAO.selectPromotionDiscountAmount(
                        connection,
                        pendingOrder.getOrderCode()
                );

        pendingOrder.setOriginalAmount(originalAmount);
        pendingOrder.setDiscountAmount(discountAmount);
        pendingOrder.setFinalAmount(
                Math.max(originalAmount - discountAmount, 0)
        );

        return orderDAO.updateOrderAmounts(
                connection,
                pendingOrder
        );
    }

    // 새로운 주문 식별번호 생성
    private int generateOrderCode() {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMddHHmmss");

        String orderCode =
                LocalDateTime.now().format(formatter);

        return Integer.parseInt(orderCode);
    }
}
