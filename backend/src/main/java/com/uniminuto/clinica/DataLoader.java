package com.uniminuto.clinica;

import com.uniminuto.clinica.entity.*;
import com.uniminuto.clinica.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Carga datos de prueba en la base de datos H2 después del arranque de la aplicación.
 * Se ejecuta una sola vez al iniciar (si las tablas están vacías).
 */
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            RazaRepository razaRepository,
            ClienteRepository clienteRepository,
            MedicoRepository medicoRepository,
            MascotaRepository mascotaRepository,
            CitaRepository citaRepository,
            MedicamentoRepository medicamentoRepository,
            HistoriaMedicaRepository historiaRepository,
            AnotacionHistoriaRepository anotacionRepository,
            FormulaMedicaRepository formulaRepository) {

        return args -> {
            // Solo cargar si la base está vacía
            if (razaRepository.count() > 0) {
                return;
            }

            // 1. Razas
            Raza razaPerro = new Raza();
            razaPerro.setNombre("Labrador Retriever");
            razaPerro.setEspecie("Perro");
            razaPerro.setFechaCreacion(LocalDateTime.of(2026, 1, 1, 0, 0));
            razaRepository.save(razaPerro);

            Raza razaGato = new Raza();
            razaGato.setNombre("Siamés");
            razaGato.setEspecie("Gato");
            razaGato.setFechaCreacion(LocalDateTime.of(2026, 1, 1, 0, 0));
            razaRepository.save(razaGato);

            Raza razaCaniche = new Raza();
            razaCaniche.setNombre("Caniche");
            razaCaniche.setEspecie("Perro");
            razaCaniche.setFechaCreacion(LocalDateTime.of(2026, 1, 1, 0, 0));
            razaRepository.save(razaCaniche);

            // 2. Clientes
            Cliente cliente1 = new Cliente();
            cliente1.setTipoDocumento("CC");
            cliente1.setNumeroDocumento("100000001");
            cliente1.setNombres("Juan");
            cliente1.setApellidos("Pérez López");
            cliente1.setFechaNacimiento(LocalDate.of(1990, 5, 12));
            cliente1.setGenero("M");
            cliente1.setTelefono("3001112233");
            cliente1.setDireccion("Calle 10 #12-34");
            cliente1.setActivo(true);
            clienteRepository.save(cliente1);

            Cliente cliente2 = new Cliente();
            cliente2.setTipoDocumento("CC");
            cliente2.setNumeroDocumento("100000002");
            cliente2.setNombres("María");
            cliente2.setApellidos("Gómez Rodríguez");
            cliente2.setFechaNacimiento(LocalDate.of(1985, 8, 22));
            cliente2.setGenero("F");
            cliente2.setTelefono("3012223344");
            cliente2.setDireccion("Carrera 15 #20-10");
            cliente2.setActivo(true);
            clienteRepository.save(cliente2);

            Cliente cliente3 = new Cliente();
            cliente3.setTipoDocumento("TI");
            cliente3.setNumeroDocumento("900000003");
            cliente3.setNombres("Andrés");
            cliente3.setApellidos("Martínez Díaz");
            cliente3.setFechaNacimiento(LocalDate.of(2005, 3, 18));
            cliente3.setGenero("M");
            cliente3.setTelefono("3023334455");
            cliente3.setDireccion("Av. Boyacá #45-67");
            cliente3.setActivo(true);
            clienteRepository.save(cliente3);

            // 3. Médicos
            Medico medico1 = new Medico();
            medico1.setTipoDocumento("CC");
            medico1.setNumeroDocumento("1001001001");
            medico1.setNombres("Carlos");
            medico1.setApellidos("Mendoza Gómez");
            medico1.setTelefono("3001112233");
            medico1.setRegistroProfesional("RP-1001");
            medico1.setEspecializacionId(1);
            medicoRepository.save(medico1);

            Medico medico2 = new Medico();
            medico2.setTipoDocumento("CC");
            medico2.setNumeroDocumento("1001001002");
            medico2.setNombres("Ana María");
            medico2.setApellidos("Rodríguez López");
            medico2.setTelefono("3001112234");
            medico2.setRegistroProfesional("RP-1002");
            medico2.setEspecializacionId(2);
            medicoRepository.save(medico2);

            Medico medico3 = new Medico();
            medico3.setTipoDocumento("CC");
            medico3.setNumeroDocumento("1001001003");
            medico3.setNombres("Luis");
            medico3.setApellidos("García Torres");
            medico3.setTelefono("3001112235");
            medico3.setRegistroProfesional("RP-1003");
            medico3.setEspecializacionId(3);
            medicoRepository.save(medico3);

            // 4. Mascotas
            Mascota mascota1 = new Mascota();
            mascota1.setNombreMascota("Max");
            mascota1.setEdad(3);
            mascota1.setRaza(razaPerro);
            mascota1.setFechaRegistro(LocalDateTime.of(2026, 8, 1, 8, 15));
            mascota1.setFechaModificacion(LocalDateTime.of(2026, 8, 15, 10, 20));
            mascota1.setCliente(cliente1);
            mascotaRepository.save(mascota1);

            Mascota mascota2 = new Mascota();
            mascota2.setNombreMascota("Luna");
            mascota2.setEdad(2);
            mascota2.setRaza(razaGato);
            mascota2.setFechaRegistro(LocalDateTime.of(2026, 8, 1, 9, 30));
            mascota2.setFechaModificacion(LocalDateTime.of(2026, 8, 16, 11, 0));
            mascota2.setCliente(cliente2);
            mascotaRepository.save(mascota2);

            Mascota mascota3 = new Mascota();
            mascota3.setNombreMascota("Rocky");
            mascota3.setEdad(5);
            mascota3.setRaza(razaCaniche);
            mascota3.setFechaRegistro(LocalDateTime.of(2026, 8, 2, 10, 0));
            mascota3.setFechaModificacion(null);
            mascota3.setCliente(cliente3);
            mascotaRepository.save(mascota3);

            // 5. Citas
            Cita cita1 = new Cita();
            cita1.setCliente(cliente1);
            cita1.setMascota(mascota1);
            cita1.setMedico(medico1);
            cita1.setFechaHora(LocalDateTime.of(2026, 10, 16, 21, 30));
            cita1.setEstado("Programada");
            cita1.setMotivo("Consulta general");
            citaRepository.save(cita1);

            Cita cita2 = new Cita();
            cita2.setCliente(cliente2);
            cita2.setMascota(mascota2);
            cita2.setMedico(medico2);
            cita2.setFechaHora(LocalDateTime.of(2026, 10, 17, 14, 30));
            cita2.setEstado("Programada");
            cita2.setMotivo("Consulta odontológica");
            citaRepository.save(cita2);

            Cita cita3 = new Cita();
            cita3.setCliente(cliente3);
            cita3.setMascota(mascota3);
            cita3.setMedico(medico3);
            cita3.setFechaHora(LocalDateTime.of(2026, 10, 18, 18, 30));
            cita3.setEstado("Pendiente");
            cita3.setMotivo("Vacunación anual");
            citaRepository.save(cita3);

            // 6. Medicamentos
            Medicamento med1 = new Medicamento();
            med1.setNombre("Paracetamol");
            med1.setDescripcion("Analgésico y antipirético");
            med1.setPresentacion("Tabletas 500mg");
            med1.setFechaCompra(LocalDate.of(2024, 6, 1));
            med1.setFechaVence(LocalDate.of(2026, 6, 1));
            med1.setFechaCreacionRegistro(LocalDateTime.of(2025, 9, 11, 20, 56, 13));
            medicamentoRepository.save(med1);

            Medicamento med2 = new Medicamento();
            med2.setNombre("Amoxicilina");
            med2.setDescripcion("Antibiótico de amplio espectro");
            med2.setPresentacion("Tabletas 500mg");
            med2.setFechaCompra(LocalDate.of(2024, 6, 1));
            med2.setFechaVence(LocalDate.of(2027, 6, 1));
            med2.setFechaCreacionRegistro(LocalDateTime.of(2025, 9, 12, 10, 0, 0));
            medicamentoRepository.save(med2);

            Medicamento med3 = new Medicamento();
            med3.setNombre("Meloxicam");
            med3.setDescripcion("Antiinflamatorio no esteroideo");
            med3.setPresentacion("Tabletas 15mg");
            med3.setFechaCompra(LocalDate.of(2024, 7, 1));
            med3.setFechaVence(LocalDate.of(2027, 7, 1));
            med3.setFechaCreacionRegistro(LocalDateTime.of(2025, 9, 13, 11, 0, 0));
            medicamentoRepository.save(med3);

            // 7. Historias clínicas
            HistoriaMedica historia1 = new HistoriaMedica();
            historia1.setCliente(cliente1);
            historia1.setMascota(mascota1);
            historia1.setCita(cita1);
            historia1.setMedico(medico1);
            historia1.setFechaCreacion(LocalDateTime.of(2026, 10, 16, 21, 30));
            historia1.setMotivo("Consulta general de rutina");
            historia1.setDiagnostico("Paciente sano");
            historia1.setObservaciones("Sin síntomas relevantes");
            historia1.setEstado("Abierta");
            historiaRepository.save(historia1);

            HistoriaMedica historia2 = new HistoriaMedica();
            historia2.setCliente(cliente2);
            historia2.setMascota(mascota2);
            historia2.setCita(cita2);
            historia2.setMedico(medico2);
            historia2.setFechaCreacion(LocalDateTime.of(2026, 10, 17, 14, 30));
            historia2.setMotivo("Revisión dental");
            historia2.setDiagnostico("Gingivitis leve");
            historia2.setObservaciones("Requiere limpieza dental");
            historia2.setEstado("Abierta");
            historiaRepository.save(historia2);

            HistoriaMedica historia3 = new HistoriaMedica();
            historia3.setCliente(cliente3);
            historia3.setMascota(mascota3);
            historia3.setCita(cita3);
            historia3.setMedico(medico3);
            historia3.setFechaCreacion(LocalDateTime.of(2026, 10, 18, 18, 30));
            historia3.setMotivo("Vacunación anual");
            historia3.setDiagnostico("Al día con calendario");
            historia3.setObservaciones("Sin observaciones");
            historia3.setEstado("Abierta");
            historiaRepository.save(historia3);

            // 8. Anotaciones de historia
            AnotacionHistoria anotacion1 = new AnotacionHistoria();
            anotacion1.setHistoria(historia1);
            anotacion1.setMedico(medico1);
            anotacion1.setFecha(LocalDateTime.of(2026, 10, 16, 22, 30));
            anotacion1.setDescripcion("Revisión física completa");
            anotacion1.setObservacion("Mascota ingresa estable");
            anotacion1.setDiagnostico("Control rutinario");
            anotacionRepository.save(anotacion1);

            AnotacionHistoria anotacion2 = new AnotacionHistoria();
            anotacion2.setHistoria(historia2);
            anotacion2.setMedico(medico2);
            anotacion2.setFecha(LocalDateTime.of(2026, 10, 17, 15, 30));
            anotacion2.setDescripcion("Limpieza dental realizada");
            anotacion2.setObservacion("Procedimiento exitoso");
            anotacion2.setDiagnostico("Gingivitis tratada");
            anotacionRepository.save(anotacion2);

            AnotacionHistoria anotacion3 = new AnotacionHistoria();
            anotacion3.setHistoria(historia3);
            anotacion3.setMedico(medico3);
            anotacion3.setFecha(LocalDateTime.of(2026, 10, 18, 19, 30));
            anotacion3.setDescripcion("Vacuna aplicada");
            anotacion3.setObservacion("Sin reacción adversa");
            anotacion3.setDiagnostico("Inmunización completa");
            anotacionRepository.save(anotacion3);

            // 9. Fórmulas médicas
            FormulaMedica formula1 = new FormulaMedica();
            formula1.setCita(cita1);
            formula1.setMascota(mascota1);
            formula1.setMedico(medico1);
            formula1.setHistoria(historia1);
            formula1.setMedicamento(med1);
            formula1.setDosis("500 mg cada 12 horas");
            formula1.setFrecuencia("cada 12 horas");
            formula1.setDuracionDias(7);
            formula1.setIndicaciones("Tomar después de las comidas");
            formula1.setFechaEmision(LocalDateTime.of(2026, 10, 16, 22, 0));
            formula1.setActivo(true);
            formula1.setFechaCreacionRegistro(LocalDateTime.of(2026, 10, 16, 22, 0));
            formulaRepository.save(formula1);

            FormulaMedica formula2 = new FormulaMedica();
            formula2.setCita(cita2);
            formula2.setMascota(mascota2);
            formula2.setMedico(medico2);
            formula2.setHistoria(historia2);
            formula2.setMedicamento(med2);
            formula2.setDosis("500 mg cada 8 horas");
            formula2.setFrecuencia("cada 8 horas");
            formula2.setDuracionDias(5);
            formula2.setIndicaciones("Tomar con abundante agua");
            formula2.setFechaEmision(LocalDateTime.of(2026, 10, 17, 15, 0));
            formula2.setActivo(true);
            formula2.setFechaCreacionRegistro(LocalDateTime.of(2026, 10, 17, 15, 0));
            formulaRepository.save(formula2);

            FormulaMedica formula3 = new FormulaMedica();
            formula3.setCita(cita3);
            formula3.setMascota(mascota3);
            formula3.setMedico(medico3);
            formula3.setHistoria(historia3);
            formula3.setMedicamento(med3);
            formula3.setDosis("15 mg cada 24 horas");
            formula3.setFrecuencia("cada 24 horas");
            formula3.setDuracionDias(3);
            formula3.setIndicaciones("Tomar con alimentos");
            formula3.setFechaEmision(LocalDateTime.of(2026, 10, 18, 19, 0));
            formula3.setActivo(true);
            formula3.setFechaCreacionRegistro(LocalDateTime.of(2026, 10, 18, 19, 0));
            formulaRepository.save(formula3);

            System.out.println("Datos de prueba cargados correctamente en la base de datos H2.");
        };
    }
}
