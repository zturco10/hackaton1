package org.example.hackaton1.service;

import org.example.hackaton1.model.Sale;
import org.example.hackaton1.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesAggregationServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private SalesAggregationService salesAggregationService;

    private Sale createSale(String sku, int units, double price, String branch) {
        Sale s = new Sale();
        s.setSku(sku);
        s.setUnits(units);
        s.setPrice(BigDecimal.valueOf(price));
        s.setBranch(branch);
        s.setSoldAt(LocalDateTime.now());
        return s;
    }

    @Test
    void testWithValidData() {
        List<Sale> mockSales = List.of(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"),
                createSale("OREO_DOUBLE", 5, 2.49, "San Isidro"),
                createSale("OREO_CLASSIC", 15, 1.99, "Miraflores")
        );

        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        when(saleRepository.findByDateRangeAndBranch(null, from, to)).thenReturn(mockSales);

        var result = salesAggregationService.calculateAggregates(
                from.atStartOfDay(),
                to.atStartOfDay(),
                null
        );

        assertThat(result.totalUnits()).isEqualTo(30);
        assertThat(result.totalRevenue()).isEqualByComparingTo("42.43");
        assertThat(result.topSku()).isEqualTo("OREO_CLASSIC");
        assertThat(result.topBranch()).isEqualTo("Miraflores");
    }

    @Test
    void testWithEmptyList() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        when(saleRepository.findByDateRangeAndBranch(null, from, to)).thenReturn(List.of());

        var result = salesAggregationService.calculateAggregates(
                from.atStartOfDay(),
                to.atStartOfDay(),
                null
        );

        assertThat(result.totalUnits()).isEqualTo(0);
        assertThat(result.totalRevenue()).isEqualByComparingTo("0.00");
        assertThat(result.topSku()).isNull();
        assertThat(result.topBranch()).isNull();
    }

    @Test
    void testFilterByBranch() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        List<Sale> mockSales = List.of(
                createSale("OREO_CLASSIC", 20, 1.99, "Miraflores")
        );

        when(saleRepository.findByDateRangeAndBranch("Miraflores", from, to)).thenReturn(mockSales);

        var result = salesAggregationService.calculateAggregates(
                from.atStartOfDay(),
                to.atStartOfDay(),
                "Miraflores"
        );

        assertThat(result.totalUnits()).isEqualTo(20);
        assertThat(result.totalRevenue()).isEqualByComparingTo("39.80");
        assertThat(result.topSku()).isEqualTo("OREO_CLASSIC");
        assertThat(result.topBranch()).isEqualTo("Miraflores");
    }

    @Test
    void testFilterByDateRange() {
        LocalDate from = LocalDate.of(2025, 9, 1);
        LocalDate to = LocalDate.of(2025, 9, 7);

        List<Sale> mockSales = List.of(
                createSale("OREO_THINS", 32, 2.19, "San Isidro")
        );

        when(saleRepository.findByDateRangeAndBranch(null, from, to)).thenReturn(mockSales);

        var result = salesAggregationService.calculateAggregates(
                from.atStartOfDay(),
                to.atStartOfDay(),
                null
        );

        assertThat(result.totalUnits()).isEqualTo(32);
        assertThat(result.totalRevenue()).isEqualByComparingTo("70.08");
        assertThat(result.topSku()).isEqualTo("OREO_THINS");
        assertThat(result.topBranch()).isEqualTo("San Isidro");
    }

    @Test
    void testTopSkuWithTie() {
        LocalDate from = LocalDate.now().minusDays(3);
        LocalDate to = LocalDate.now();

        List<Sale> mockSales = List.of(
                createSale("OREO_MINI", 20, 1.5, "Lima"),
                createSale("OREO_MEGA", 20, 1.5, "Lima")
        );

        when(saleRepository.findByDateRangeAndBranch(null, from, to)).thenReturn(mockSales);

        var result = salesAggregationService.calculateAggregates(
                from.atStartOfDay(),
                to.atStartOfDay(),
                null
        );

        assertThat(result.totalUnits()).isEqualTo(40);
        assertThat(result.topSku()).isIn("OREO_MINI", "OREO_MEGA"); // cualquiera es válido
    }
}
