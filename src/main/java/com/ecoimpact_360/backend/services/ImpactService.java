package com.ecoimpact_360.backend.services;

import org.springframework.stereotype.Service;

@Service
public class ImpactService {
 

  //  CO2 por 1kg residuo
    private static final double F_CO2_PLASTICO = 2.5;
    private static final double F_CO2_PAPEL = 0.9;
    private static final double F_CO2_VIDRIO = 0.6;
    private static final double F_CO2_ORGANICA = 0.5;

    // AGUA Litros  por 1kg 
    private static final double F_AGUA_PLASTICO = 2.0;
    private static final double F_AGUA_PAPEL = 26.0;
    private static final double F_AGUA_VIDRIO = 1.2;
    private static final double F_AGUA_ORGANICA = 0.0; 

    
    private static final double CO2_PER_TREE_YEAR = 20.0; // 1 árbol absorbe 20kg/año
    private static final double CO2_PER_KM_CAR = 0.12;    // 1 km de coche emite 0.12kg

    public double calculateCo2(String type, double kg) {
        return switch (type.toUpperCase()) {
            case "PLASTICO" -> kg * F_CO2_PLASTICO;
            case "PAPEL", "CARTON" -> kg * F_CO2_PAPEL;
            case "VIDRIO" -> kg * F_CO2_VIDRIO;
            case "ORGANICA" -> kg * F_CO2_ORGANICA;
            default -> 0.0; 
        };
    }

    public double calculateWaterSaved(String type, double kg) {
        return switch (type.toUpperCase()) {
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

