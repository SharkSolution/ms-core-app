package com.suresell.mscoreapp.infrastructure.web.adapter;

import jakarta.validation.Valid;
import com.suresell.mscoreapp.application.usecase.ValeraService;
import com.suresell.mscoreapp.application.dto.Valera;
import com.suresell.mscoreapp.application.dto.CreateValeraRequest;
import com.suresell.mscoreapp.application.dto.UseMealRequest;
import com.suresell.mscoreapp.application.dto.CustomerValerasResponse;
import com.suresell.mscoreapp.application.dto.ValeraStatsResponse;
import com.suresell.mscoreapp.application.dto.ValeraDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/valeras")
public class ValeraController {

    private static final Logger logger = LoggerFactory.getLogger(ValeraController.class);

    private final ValeraService service;

    public ValeraController(ValeraService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createValera(@Valid @RequestBody CreateValeraRequest request) {
        logger.info("POST /api/valeras - Creando valera para: {}", request.getCustomerName());

        try {
            Valera valera = service.createValera(request);
            logger.info("Valera creada exitosamente: {}", valera.getCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(valera);
        } catch (IllegalArgumentException e) {
            logger.warn("⚠Datos inválidos para crear valera: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado creando valera", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/use-meal")
    public ResponseEntity<?> useMeal(@Valid @RequestBody UseMealRequest request) {
        logger.info("🍽️ POST /api/valeras/use-meal - Usando comida de valera: {}", request.getValeraCode());

        try {
            Valera valera = service.useMeal(request);
            logger.info("Comida registrada exitosamente. Quedan: {} comidas", valera.getRemainingMeals());
            return ResponseEntity.ok(valera);
        } catch (IllegalArgumentException e) {
            logger.warn("Error usando comida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido para usar comida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado usando comida", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getValeraByCode(@PathVariable("code") String code) {
        logger.info("🔍 GET /api/valeras/code/{} - Consultando valera", code);

        try {
            ValeraDto valera = service.getValeraByCode(code);
            logger.info("Valera encontrada: {}", code);
            return ResponseEntity.ok(valera);
        } catch (IllegalArgumentException e) {
            logger.warn("Valera no encontrada: {}", code);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error consultando valera: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/{customerDocument}")
    public ResponseEntity<?> getCustomerValeras(@PathVariable("customerDocument") String customerDocument) {
        logger.info("👤 GET /api/valeras/customer/{} - Consultando valeras del cliente", customerDocument);

        try {
            CustomerValerasResponse response = service.getCustomerValeras(customerDocument);
            logger.info("{} valeras encontradas para cliente: {}", response.getTotalValeras(), customerDocument);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Error consultando cliente: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error consultando valeras del cliente: {}", customerDocument, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveValeras() {
        logger.info("📋 GET /api/valeras/active - Consultando valeras activas");

        try {
            List<ValeraDto> valeras = service.getActiveValeras();
            logger.info("{} valeras activas encontradas", valeras.size());
            return ResponseEntity.ok(valeras);
        } catch (Exception e) {
            logger.error("Error consultando valeras activas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/expiring")
    public ResponseEntity<?> getExpiringValeras(@RequestParam(value = "days", defaultValue = "7") int days) {
        logger.info("GET /api/valeras/expiring?days={} - Consultando valeras por vencer", days);

        try {
            List<ValeraDto> valeras = service.getExpiringValeras(days);
            logger.info("{} valeras por vencer en {} días", valeras.size(), days);
            return ResponseEntity.ok(valeras);
        } catch (IllegalArgumentException e) {
            logger.warn("Parámetro de días inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error consultando valeras por vencer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/expire-old")
    public ResponseEntity<?> processExpiredValeras() {
        logger.info("POST /api/valeras/expire-old - Procesando expiración automática");

        try {
            service.processExpiredValeras();
            logger.info("Proceso de expiración completado");
            return ResponseEntity.ok(Map.of("message", "Proceso de expiración completado"));
        } catch (Exception e) {
            logger.error("Error procesando expiración automática", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<?> suspendValera(@PathVariable("id") String id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        logger.info("PUT /api/valeras/{}/suspend - Suspendiendo valera", id);

        try {
            Valera valera = service.suspendValera(id, reason);
            logger.info("Valera {} suspendida", id);
            return ResponseEntity.ok(valera);
        } catch (IllegalArgumentException e) {
            logger.warn("Error suspendiendo valera: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado suspendiendo valera: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivateValera(@PathVariable("id") String id) {
        logger.info("PUT /api/valeras/{}/reactivate - Reactivando valera", id);

        try {
            Valera valera = service.reactivateValera(id);
            logger.info("Valera {} reactivada", id);
            return ResponseEntity.ok(valera);
        } catch (IllegalArgumentException e) {
            logger.warn("Error reactivando valera: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado reactivando valera: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelValera(@PathVariable("id") String id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        logger.info("PUT /api/valeras/{}/cancel - Cancelando valera", id);

        try {
            Valera valera = service.cancelValera(id, reason);
            logger.info("Valera {} cancelada", id);
            return ResponseEntity.ok(valera);
        } catch (IllegalArgumentException e) {
            logger.warn("Error cancelando valera: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado cancelando valera: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        logger.info("GET /api/valeras/statistics - Consultando estadísticas");

        try {
            ValeraStatsResponse stats = service.getStatistics();
            logger.info("Estadísticas generadas exitosamente");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error generando estadísticas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchValeras(@RequestParam("q") String searchTerm) {
        logger.info("🔍 GET /api/valeras/search?q={} - Buscando valeras", searchTerm);

        try {
            List<ValeraDto> valeras = service.searchValeras(searchTerm);
            logger.info("{} valeras encontradas", valeras.size());
            return ResponseEntity.ok(valeras);
        } catch (IllegalArgumentException e) {
            logger.warn("Término de búsqueda inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error buscando valeras", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
