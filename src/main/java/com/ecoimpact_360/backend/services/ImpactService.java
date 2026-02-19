package com.ecoimpact_360.backend.services;

import org.springframework.stereotype.Service;

import com.ecoimpact_360.backend.model.WasteType;

@Service
public class ImpactService {


    // AGUA Litros por 1kg
    private static final double F_AGUA_PLASTICO = 2.0;
    private static final double F_AGUA_PAPEL = 26.0;
    private static final double F_AGUA_VIDRIO = 1.2;
    private static final double F_AGUA_ORGANICA = 0.0;

    private static final double CO2_PER_TREE_YEAR = 20.0; // 1 árbol absorbe 20kg/año
    private static final double CO2_PER_KM_CAR = 0.12; // 1 km de coche emite 0.12kg

    public double calculateCo2(WasteType wasteType, double kg) {
        if (wasteType == null || wasteType.getCo2Factor() == null) {
            return 0.0;
        }
        
        return kg * wasteType.getCo2Factor();
    }

    public double calculateWaterSaved(WasteType wasteType, double kg) {
    if (wasteType == null || wasteType.getName() == null) return 0.0;
    
    String type = wasteType.getName().toUpperCase();
    return switch (type) {
        case "PLASTICO" -> kg * F_AGUA_PLASTICO;
        case "PAPEL", "CARTON" -> kg * F_AGUA_PAPEL;
        case "VIDRIO" -> kg * F_AGUA_VIDRIO;
        case "ORGANICA" -> kg * F_AGUA_ORGANICA;
        default -> 0.0;
    };
}

    public double calculateTreesEquivalent(double co2Kg) {
        return co2Kg / CO2_PER_TREE_YEAR;
    }

    public double calculateKmCarEquivalent(double co2Kg) {
        return co2Kg / CO2_PER_KM_CAR;
    }
}
