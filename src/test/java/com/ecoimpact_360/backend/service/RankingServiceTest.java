package com.ecoimpact_360.backend.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ecoimpact_360.backend.dto.RankingDTO;
import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.model.WasteEntry;
import com.ecoimpact_360.backend.model.WasteType;
import com.ecoimpact_360.backend.repository.WasteEntryRepository;
@ExtendWith(MockitoExtension.class)
class RankingServiceTest {
    @Mock
    private WasteEntryRepository wasteEntryRepository;
    @Mock
    private ImpactService impactService;
    @InjectMocks
    private RankingService rankingService;
    private WasteEntry entryFor(String classroomName, double co2, double quantityKg) {
        Classroom classroom = new Classroom();
        classroom.setName(classroomName);
        WasteType wasteType = new WasteType();
        wasteType.setName("Plastico");
        WasteEntry entry = new WasteEntry();
        entry.setClassroom(classroom);
        entry.setWasteType(wasteType);
        entry.setCo2Equivalent(co2);
        entry.setQuantityKg(quantityKg);
        return entry;
    }
    @Test
    void getClassroomRanking_ReturnsEmptyList_WhenNoEntries() {
        when(wasteEntryRepository.findAll()).thenReturn(Collections.emptyList());
        List<RankingDTO> result = rankingService.getClassroomRanking();
        assertTrue(result.isEmpty());
    }
    @Test
    void getClassroomRanking_SortsByTotalCo2Descending() {
        when(wasteEntryRepository.findAll()).thenReturn(Arrays.asList(
                entryFor("Aula 1A", 5.0, 2.0),
                entryFor("Aula 2B", 15.0, 6.0)
        ));
        when(impactService.calculateWaterSaved(any(WasteType.class), anyDouble())).thenReturn(0.0);
        List<RankingDTO> result = rankingService.getClassroomRanking();
        assertEquals(2, result.size());
        assertEquals("Aula 2B", result.get(0).getClassroomName());
        assertEquals(15.0, result.get(0).getTotalCo2());
        assertEquals("Aula 1A", result.get(1).getClassroomName());
    }
    @Test
    void getClassroomRanking_AggregatesMultipleEntriesPerClassroom() {
        when(wasteEntryRepository.findAll()).thenReturn(Arrays.asList(
                entryFor("Aula 1A", 5.0, 2.0),
                entryFor("Aula 1A", 3.0, 1.0)
        ));
        when(impactService.calculateWaterSaved(any(WasteType.class), eq(2.0))).thenReturn(4.0);
        when(impactService.calculateWaterSaved(any(WasteType.class), eq(1.0))).thenReturn(2.0);
        List<RankingDTO> result = rankingService.getClassroomRanking();
        assertEquals(1, result.size());
        RankingDTO dto = result.get(0);
        assertEquals(8.0, dto.getTotalCo2());
        assertEquals(6.0, dto.getTotalWaterSaved());
        assertEquals(2L, dto.getTotalEntries());
    }
}
