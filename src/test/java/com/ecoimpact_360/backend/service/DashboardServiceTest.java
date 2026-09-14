package com.ecoimpact_360.backend.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import com.ecoimpact_360.backend.dto.DashboardDTO;
import com.ecoimpact_360.backend.model.Classroom;
import com.ecoimpact_360.backend.model.enums.WasteCategory;
import com.ecoimpact_360.backend.repository.AlertRepository;
import com.ecoimpact_360.backend.repository.ClassroomRepository;
import com.ecoimpact_360.backend.repository.WasteEntryRepository;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DashboardServiceTest {
    @Mock
    private WasteEntryRepository wasteEntryRepository;
    @Mock
    private AlertRepository alertRepository;
    @Mock
    private ClassroomRepository classroomRepository;
    @Mock
    private ClassRoomService classRoomService;
    @Mock
    private ImpactService impactService;
    @InjectMocks
    private DashboardService dashboardService;
    private Classroom classroom1;
    private Classroom classroom2;
    private static final Long SCHOOL_ID = 1L;
    @BeforeEach
    void setUp() {
        classroom1 = new Classroom();
        classroom1.setId(1L);
        classroom1.setName("Aula 1A");
        classroom1.setScore(100);
        classroom2 = new Classroom();
        classroom2.setId(2L);
        classroom2.setName("Aula 2B");
        classroom2.setScore(50);
        when(classRoomService.getOwnedClassroomOrThrow(1L, SCHOOL_ID)).thenReturn(classroom1);
    }
    @Test
    void getGlobalStats_ReturnsDashboardWithZeros_WhenNoData() {
        when(wasteEntryRepository.sumKgBySchool(SCHOOL_ID)).thenReturn(null);
        when(wasteEntryRepository.sumCo2BySchool(SCHOOL_ID)).thenReturn(null);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(eq(SCHOOL_ID), any(WasteCategory.class))).thenReturn(null);
        when(alertRepository.countByResolvedFalseAndClassroomSchoolId(SCHOOL_ID)).thenReturn(0L);
        when(classroomRepository.findBySchoolIdOrderByScoreDesc(SCHOOL_ID)).thenReturn(Collections.emptyList());
        when(impactService.calculateTreesEquivalent(0.0)).thenReturn(0.0);
        when(impactService.calculateKmCarEquivalent(0.0)).thenReturn(0.0);
        DashboardDTO result = dashboardService.getGlobalStats(SCHOOL_ID);
        assertNotNull(result);
        assertEquals(0.0, result.getTotalKgRecolectados());
        assertEquals(0.0, result.getTotalCo2Equivalente());
        assertEquals(0L, result.getTotalAlertasActivas());
        assertTrue(result.getRankingAulas().isEmpty());
    }
    @Test
    void getGlobalStats_ReturnsCorrectMetrics_WhenDataExists() {
        when(wasteEntryRepository.sumKgBySchool(SCHOOL_ID)).thenReturn(100.0);
        when(wasteEntryRepository.sumCo2BySchool(SCHOOL_ID)).thenReturn(150.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.PLASTIC)).thenReturn(50.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.PAPER)).thenReturn(30.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.GLASS)).thenReturn(20.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.ORGANIC)).thenReturn(0.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.GENERAL)).thenReturn(0.0);
        when(alertRepository.countByResolvedFalseAndClassroomSchoolId(SCHOOL_ID)).thenReturn(3L);
        when(classroomRepository.findBySchoolIdOrderByScoreDesc(SCHOOL_ID)).thenReturn(Arrays.asList(classroom1, classroom2));
        when(impactService.calculateTreesEquivalent(150.0)).thenReturn(7.5);
        when(impactService.calculateKmCarEquivalent(150.0)).thenReturn(1250.0);
        when(impactService.calculateWaterSaved("PLASTIC", 50.0)).thenReturn(100.0);
        when(impactService.calculateWaterSaved("PAPER", 30.0)).thenReturn(780.0);
        when(impactService.calculateWaterSaved("GLASS", 20.0)).thenReturn(24.0);
        when(impactService.calculateWaterSaved("ORGANIC", 0.0)).thenReturn(0.0);
        DashboardDTO result = dashboardService.getGlobalStats(SCHOOL_ID);
        assertNotNull(result);
        assertEquals(100.0, result.getTotalKgRecolectados());
        assertEquals(150.0, result.getTotalCo2Equivalente());
        assertEquals(7.5, result.getArbolesEquivalentes());
        assertEquals(1250.0, result.getKmCarroEquivalente());
        assertEquals(3L, result.getTotalAlertasActivas());
        assertEquals(2, result.getRankingAulas().size());
        assertEquals("Aula 1A", result.getRankingAulas().get(0).getName());
        assertEquals(100, result.getRankingAulas().get(0).getScore());
    }
    @Test
    void getGlobalStats_CalculatesWaterSavedCorrectly() {
        when(wasteEntryRepository.sumKgBySchool(SCHOOL_ID)).thenReturn(100.0);
        when(wasteEntryRepository.sumCo2BySchool(SCHOOL_ID)).thenReturn(150.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.PLASTIC)).thenReturn(50.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.PAPER)).thenReturn(30.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.GLASS)).thenReturn(20.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.ORGANIC)).thenReturn(0.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.GENERAL)).thenReturn(0.0);
        when(alertRepository.countByResolvedFalseAndClassroomSchoolId(SCHOOL_ID)).thenReturn(0L);
        when(classroomRepository.findBySchoolIdOrderByScoreDesc(SCHOOL_ID)).thenReturn(Collections.emptyList());
        when(impactService.calculateTreesEquivalent(150.0)).thenReturn(7.5);
        when(impactService.calculateKmCarEquivalent(150.0)).thenReturn(1250.0);
        when(impactService.calculateWaterSaved("PLASTIC", 50.0)).thenReturn(100.0);
        when(impactService.calculateWaterSaved("PAPER", 30.0)).thenReturn(780.0);
        when(impactService.calculateWaterSaved("GLASS", 20.0)).thenReturn(24.0);
        when(impactService.calculateWaterSaved("ORGANIC", 0.0)).thenReturn(0.0);
        DashboardDTO result = dashboardService.getGlobalStats(SCHOOL_ID);
        assertEquals(904.0, result.getTotalAguaAhorrada());
    }
    @Test
    void getClassroomStats_ReturnsDashboardWithZeros_WhenNoData() {
        when(wasteEntryRepository.sumKgByClassroom(1L)).thenReturn(null);
        when(wasteEntryRepository.sumCo2ByClassroom(1L)).thenReturn(null);
        when(classroomRepository.findBySchoolIdOrderByScoreDesc(SCHOOL_ID)).thenReturn(Arrays.asList(classroom1, classroom2));
        when(impactService.calculateTreesEquivalent(0.0)).thenReturn(0.0);
        when(impactService.calculateKmCarEquivalent(0.0)).thenReturn(0.0);
        DashboardDTO result = dashboardService.getClassroomStats(1L, SCHOOL_ID);
        assertNotNull(result);
        assertEquals(0.0, result.getTotalKgRecolectados());
        assertEquals(0.0, result.getTotalCo2Equivalente());
        assertEquals(0L, result.getTotalAlertasActivas());
    }
    @Test
    void getClassroomStats_ReturnsCorrectMetrics_WhenDataExists() {
        when(wasteEntryRepository.sumKgByClassroom(1L)).thenReturn(50.0);
        when(wasteEntryRepository.sumCo2ByClassroom(1L)).thenReturn(75.0);
        when(classroomRepository.findBySchoolIdOrderByScoreDesc(SCHOOL_ID)).thenReturn(Arrays.asList(classroom1, classroom2));
        when(impactService.calculateTreesEquivalent(75.0)).thenReturn(3.75);
        when(impactService.calculateKmCarEquivalent(75.0)).thenReturn(625.0);
        when(impactService.calculateWaterSaved("GENERAL", 50.0)).thenReturn(0.0);
        DashboardDTO result = dashboardService.getClassroomStats(1L, SCHOOL_ID);
        assertNotNull(result);
        assertEquals(50.0, result.getTotalKgRecolectados());
        assertEquals(75.0, result.getTotalCo2Equivalente());
        assertEquals(3.75, result.getArbolesEquivalentes());
        assertEquals(625.0, result.getKmCarroEquivalente());
        assertEquals(2, result.getRankingAulas().size());
    }
    @Test
    void getClassroomStats_ThrowsForbidden_WhenClassroomBelongsToAnotherSchool() {
        when(classRoomService.getOwnedClassroomOrThrow(1L, 999L))
                .thenThrow(new com.ecoimpact_360.backend.exception.ForbiddenException("Esta aula no pertenece a tu colegio"));
        assertThrows(com.ecoimpact_360.backend.exception.ForbiddenException.class,
                () -> dashboardService.getClassroomStats(1L, 999L));
    }
    @Test
    void getGlobalStats_ResiduosPorCategoria_MapsCorrectly() {
        when(wasteEntryRepository.sumKgBySchool(SCHOOL_ID)).thenReturn(100.0);
        when(wasteEntryRepository.sumCo2BySchool(SCHOOL_ID)).thenReturn(100.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.PLASTIC)).thenReturn(40.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.PAPER)).thenReturn(30.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.GLASS)).thenReturn(20.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.ORGANIC)).thenReturn(10.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(SCHOOL_ID, WasteCategory.GENERAL)).thenReturn(0.0);
        when(alertRepository.countByResolvedFalseAndClassroomSchoolId(SCHOOL_ID)).thenReturn(0L);
        when(classroomRepository.findBySchoolIdOrderByScoreDesc(SCHOOL_ID)).thenReturn(Collections.emptyList());
        when(impactService.calculateTreesEquivalent(anyDouble())).thenReturn(0.0);
        when(impactService.calculateKmCarEquivalent(anyDouble())).thenReturn(0.0);
        when(impactService.calculateWaterSaved(anyString(), anyDouble())).thenReturn(0.0);
        DashboardDTO result = dashboardService.getGlobalStats(SCHOOL_ID);
        assertNotNull(result.getResiduosPorCategoria());
        assertEquals(5, result.getResiduosPorCategoria().size());
        assertEquals(40.0, result.getResiduosPorCategoria().get("PLASTIC"));
        assertEquals(30.0, result.getResiduosPorCategoria().get("PAPER"));
        assertEquals(20.0, result.getResiduosPorCategoria().get("GLASS"));
    }
    @Test
    void getClassroomStats_ResiduosPorCategoria_MapsCorrectly() {
        when(wasteEntryRepository.sumKgByClassroom(1L)).thenReturn(50.0);
        when(wasteEntryRepository.sumCo2ByClassroom(1L)).thenReturn(75.0);
        when(classroomRepository.findBySchoolIdOrderByScoreDesc(SCHOOL_ID)).thenReturn(Arrays.asList(classroom1, classroom2));
        when(wasteEntryRepository.sumKgByClassroomAndCategory(1L, WasteCategory.PLASTIC)).thenReturn(30.0);
        when(wasteEntryRepository.sumKgByClassroomAndCategory(1L, WasteCategory.PAPER)).thenReturn(20.0);
        when(wasteEntryRepository.sumKgByClassroomAndCategory(1L, WasteCategory.GLASS)).thenReturn(0.0);
        when(wasteEntryRepository.sumKgByClassroomAndCategory(1L, WasteCategory.ORGANIC)).thenReturn(0.0);
        when(wasteEntryRepository.sumKgByClassroomAndCategory(1L, WasteCategory.GENERAL)).thenReturn(0.0);
        when(impactService.calculateTreesEquivalent(75.0)).thenReturn(3.75);
        when(impactService.calculateKmCarEquivalent(75.0)).thenReturn(625.0);
        when(impactService.calculateWaterSaved("PLASTIC", 30.0)).thenReturn(60.0);
        when(impactService.calculateWaterSaved("PAPER", 20.0)).thenReturn(520.0);
        DashboardDTO result = dashboardService.getClassroomStats(1L, SCHOOL_ID);
        assertNotNull(result.getResiduosPorCategoria());
        assertEquals(5, result.getResiduosPorCategoria().size());
        assertEquals(30.0, result.getResiduosPorCategoria().get("PLASTIC"));
        assertEquals(20.0, result.getResiduosPorCategoria().get("PAPER"));
        assertEquals(0.0, result.getResiduosPorCategoria().get("GLASS"));
        assertEquals(580.0, result.getTotalAguaAhorrada());
    }
    @Test
    void getGlobalStats_RankingAulas_OrderedByScoreDesc() {
        when(wasteEntryRepository.sumKgBySchool(SCHOOL_ID)).thenReturn(100.0);
        when(wasteEntryRepository.sumCo2BySchool(SCHOOL_ID)).thenReturn(100.0);
        when(wasteEntryRepository.sumKgBySchoolAndCategory(eq(SCHOOL_ID), any(WasteCategory.class))).thenReturn(0.0);
        when(alertRepository.countByResolvedFalseAndClassroomSchoolId(SCHOOL_ID)).thenReturn(0L);
        when(classroomRepository.findBySchoolIdOrderByScoreDesc(SCHOOL_ID)).thenReturn(Arrays.asList(classroom1, classroom2));
        when(impactService.calculateTreesEquivalent(anyDouble())).thenReturn(0.0);
        when(impactService.calculateKmCarEquivalent(anyDouble())).thenReturn(0.0);
        DashboardDTO result = dashboardService.getGlobalStats(SCHOOL_ID);
        assertEquals(2, result.getRankingAulas().size());
        assertEquals("Aula 1A", result.getRankingAulas().get(0).getName());
        assertEquals(100, result.getRankingAulas().get(0).getScore());
        assertEquals("Aula 2B", result.getRankingAulas().get(1).getName());
        assertEquals(50, result.getRankingAulas().get(1).getScore());
    }
}
